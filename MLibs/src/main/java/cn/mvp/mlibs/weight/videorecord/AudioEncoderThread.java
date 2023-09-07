package cn.mvp.mlibs.weight.videorecord;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaCodec;
import android.media.MediaFormat;
import android.media.MediaRecorder;
import android.util.Log;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.nio.ByteBuffer;

/**
 * 音频编码线程
 * Created by renhui on 2017/9/25.
 */
public class AudioEncoderThread extends Thread {

    public static final String TAG = "调试信息";

    public static final int SAMPLES_PER_FRAME = 1024;
    private static final int TIMEOUT_USEC = 10000;
    private static final String MIME_TYPE = "audio/mp4a-latm";
    private static final int SAMPLE_RATE = 44100;
    private static final int BIT_RATE = 64000;


    private final Object lock = new Object();
    private MediaCodec mMediaCodec;                // API >= 16(Android4.1.2)
    private volatile boolean isExit = false;
    private WeakReference<MediaMuxerThread> mediaMuxerRunnable;
    private AudioRecord audioRecord;
    private MediaCodec.BufferInfo mBufferInfo;        // API >= 16(Android4.1.2)
    private volatile boolean isStart = false;
    private volatile boolean isMuxerReady = false;
    private long prevOutputPTSUs = 0;
    private MediaFormat audioFormat;

    public AudioEncoderThread(WeakReference<MediaMuxerThread> mediaMuxerRunnable) {
        this.mediaMuxerRunnable = mediaMuxerRunnable;
        mBufferInfo = new MediaCodec.BufferInfo();
        prepare();
    }

    private void prepare() {
        audioFormat = MediaFormat.createAudioFormat(MIME_TYPE, SAMPLE_RATE, 1);
        audioFormat.setInteger(MediaFormat.KEY_BIT_RATE, BIT_RATE); //64000
        audioFormat.setInteger(MediaFormat.KEY_CHANNEL_COUNT, 1); //单通道
        audioFormat.setInteger(MediaFormat.KEY_SAMPLE_RATE, SAMPLE_RATE); //44100
    }

    private void startMediaCodec() throws IOException {
        mMediaCodec = MediaCodec.createEncoderByType(MIME_TYPE);
        mMediaCodec.configure(audioFormat, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE);
        mMediaCodec.start();

        prepareAudioRecord();

        isStart = true;
    }

    private void stopMediaCodec() {
        if (audioRecord != null) {
            audioRecord.stop();
            audioRecord.release();
            audioRecord = null;
        }
        if (mMediaCodec != null) {
            mMediaCodec.stop();
            mMediaCodec.release();
            mMediaCodec = null;
        }
        isStart = false;
        Log.e(TAG, "stop audio 录制...");
    }

    private void prepareAudioRecord() {
        if (audioRecord != null) {
            audioRecord.stop();
            audioRecord.release();
            audioRecord = null;
        }
        android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_URGENT_AUDIO);
        final int min_buffer_size = AudioRecord.getMinBufferSize(
                SAMPLE_RATE, AudioFormat.CHANNEL_IN_MONO,
                AudioFormat.ENCODING_PCM_16BIT);
        audioRecord = null;
        audioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC, SAMPLE_RATE, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT, min_buffer_size);

        if (audioRecord != null) {
            audioRecord.startRecording();
        }
    }

    public void exit() {
        isExit = true;
    }

    public void setMuxerReady(boolean muxerReady) {
        synchronized (lock) {
            Log.e(TAG, Thread.currentThread().getId() + " audio -- setMuxerReady..." + muxerReady);
            isMuxerReady = muxerReady;
            lock.notifyAll();
        }
    }

    @Override
    public void run() {
        final ByteBuffer buf = ByteBuffer.allocateDirect(SAMPLES_PER_FRAME);
        int readBytes;
        while (!isExit) {
            /*启动或者重启*/
            if (!isStart) {
                stopMediaCodec();
                if (!isMuxerReady) {
                    synchronized (lock) {
                        try {
                            Log.e(TAG, "audio -- 等待混合器准备...");
                            lock.wait();
                        } catch (InterruptedException e) {
                        }
                    }
                }

                if (isMuxerReady) {
                    try {
                        Log.e(TAG, "audio -- startMediaCodec...");
                        startMediaCodec();
                    } catch (IOException e) {
                        e.printStackTrace();
                        isStart = false;
                        try {
                            Thread.sleep(100);
                        } catch (InterruptedException e1) {
                        }
                    }
                }
            } else if (audioRecord != null) {
                buf.clear();
                readBytes = audioRecord.read(buf, SAMPLES_PER_FRAME);
                if (readBytes > 0) {
                    // set audio data to encoder
                    buf.position(readBytes);
                    buf.flip();
                    Log.e(TAG, "解码音频数据:" + readBytes);
                    try {
                        encode(buf, readBytes, getPTSUs());
                    } catch (Exception e) {
                        Log.e(TAG, "解码音频(Audio)数据 失败");
                        e.printStackTrace();
                    }
                }
            }

        }
        Log.e(TAG, "Audio 录制线程 退出...");
    }

    private void encode(final ByteBuffer buffer, final int length, final long presentationTimeUs) {
        if (isExit) return;
        final ByteBuffer[] inputBuffers = mMediaCodec.getInputBuffers();
        final int inputBufferIndex = mMediaCodec.dequeueInputBuffer(TIMEOUT_USEC);
        /*向编码器输入数据*/
        if (inputBufferIndex >= 0) {
            final ByteBuffer inputBuffer = inputBuffers[inputBufferIndex];
            inputBuffer.clear();
            if (buffer != null) {
                inputBuffer.put(buffer);
            }
            if (length <= 0) {
                mMediaCodec.queueInputBuffer(inputBufferIndex, 0, 0, presentationTimeUs, MediaCodec.BUFFER_FLAG_END_OF_STREAM);
            } else {
                mMediaCodec.queueInputBuffer(inputBufferIndex, 0, length, presentationTimeUs, 0);
            }
        }

        /*获取解码后的数据*/
        ByteBuffer[] encoderOutputBuffers = mMediaCodec.getOutputBuffers();
        int encoderStatus = mMediaCodec.dequeueOutputBuffer(mBufferInfo, TIMEOUT_USEC);
        //FORMAT_CHANGEED < 0 所以需要一个 ||
        while (encoderStatus >= 0 || encoderStatus == MediaCodec.INFO_OUTPUT_FORMAT_CHANGED) {
            if (encoderStatus == MediaCodec.INFO_OUTPUT_FORMAT_CHANGED) {
                //添加轨道的好时机，只有一次
                final MediaFormat format = mMediaCodec.getOutputFormat();
                MediaMuxerThread mediaMuxerRunnable = this.mediaMuxerRunnable.get();
                if (mediaMuxerRunnable != null) {
                    Log.e(TAG, "添加音轨 INFO_OUTPUT_FORMAT_CHANGED " + format.toString());
                    mediaMuxerRunnable.addTrackIndex(MediaMuxerThread.TRACK_AUDIO, format);
                }
            } else {
                final ByteBuffer encodedData = encoderOutputBuffers[encoderStatus];
                if ((mBufferInfo.flags & MediaCodec.BUFFER_FLAG_CODEC_CONFIG) != 0) {
                    mBufferInfo.size = 0;
                }
                if (mBufferInfo.size != 0) {
                    MediaMuxerThread mediaMuxer = this.mediaMuxerRunnable.get();
                    if (mediaMuxer != null && mediaMuxer.isMuxerStart()) {
                        mBufferInfo.presentationTimeUs = getPTSUs();
                        mediaMuxer.addMuxerData(new MediaMuxerThread.MuxerData(MediaMuxerThread.TRACK_AUDIO, encodedData, mBufferInfo));
                        prevOutputPTSUs = mBufferInfo.presentationTimeUs;
                    }
                }
                mMediaCodec.releaseOutputBuffer(encoderStatus, false);
            }
            encoderStatus = mMediaCodec.dequeueOutputBuffer(mBufferInfo, TIMEOUT_USEC);
        }
    }

    /**
     * get next encoding presentationTimeUs
     */
    private long getPTSUs() {
        long result = System.nanoTime() / 1000L;
        if (result < prevOutputPTSUs)
            result = (prevOutputPTSUs - result) + result;
        return result;
    }
}

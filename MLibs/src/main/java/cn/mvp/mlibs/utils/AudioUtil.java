package cn.mvp.mlibs.utils;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 录音工具类
 *
 * @作者 Liushihua
 * @创建日志 2021-4-27 17:07
 * @描述
 */
public class AudioUtil {
    private AudioRecord recorder;
    //录音源
    private static final int audioSource = MediaRecorder.AudioSource.MIC;
    // android 原生提供降噪
    private static final int audioSourceDenoise = MediaRecorder.AudioSource.VOICE_COMMUNICATION;
    //录音的采样频率  16k采样率
    private static final int audioRate = 16000;
    //录音的声道，单声道
    private static final int audioChannel = AudioFormat.CHANNEL_IN_MONO;
    //量化的深度  16bps比特率
    private static final int audioFormat = AudioFormat.ENCODING_PCM_16BIT;
    //缓存的大小
    private static final int bufferSize = AudioRecord.getMinBufferSize(audioRate, audioChannel, audioFormat);
    //记录播放状态
    private boolean isRecording = false;
    //数字信号数组
    private byte[] noteArray;
    //PCM文件
    private File pcmFile;
    //WAV文件
    private File wavFile;
    //文件输出流
    private OutputStream os;
    //文件根目录
    private String basePath = Environment.getExternalStorageDirectory().getAbsolutePath();

    private String savePath;// wav文件保存路径

    private ExecutorService service;

    private RecordAudioCallBack callBack;
    private Handler handler = new Handler(Looper.getMainLooper());

    /**
     * @param savePath 文件保存地址
     */
    public AudioUtil(@NonNull String savePath, @NonNull RecordAudioCallBack callBack) throws IOException {
        this.savePath = savePath;
        this.callBack = callBack;
        createFile();//创建文件
        service = Executors.newSingleThreadExecutor();
//        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
//            // TODO: Consider calling
//            //    ActivityCompat#requestPermissions
//            // here to request the missing permissions, and then overriding
//            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
//            //                                          int[] grantResults)
//            // to handle the case where the user grants the permission. See the documentation
//            // for ActivityCompat#requestPermissions for more details.
//            throw new IOException();
//        }
        recorder = new AudioRecord(audioSourceDenoise, audioRate, audioChannel, audioFormat, bufferSize);
    }

//    private boolean hashRecordAudioPermission(Context context) {
//        //ContextCompat.checkSelfPermission(mContexts, permission) != PackageManager.PERMISSION_GRANTED;
//        return ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED;
//    }


    //调用时 公用
    public String getBasePath() {
        return basePath;
    }

    //读取录音数字数据线程
    class WriteThread implements Runnable {
        public void run() {
            writeData();
        }
    }

    //开始录音
    public void startRecord() {

        Log.d("调试信息", "startRecord");
        isRecording = true;
        recorder.startRecording();
        recordData();
    }

    //停止录音
    public void stopRecord() {
        Log.d("调试信息", "stopRecord");
        isRecording = false;
        recorder.stop();
        convertWavFile(savePath);
    }

    //将数据写入文件夹,文件的写入没有做优化
    public void writeData() {
        noteArray = new byte[bufferSize];
        //建立文件输出流
        try {
            os = new BufferedOutputStream(new FileOutputStream(pcmFile));
        } catch (IOException e) {
            handler.post(() -> {
                callBack.onRecordAudioErr("writeData IOException2");
            });
        }
        while (isRecording == true) {
            int recordSize = recorder.read(noteArray, 0, bufferSize);
            if (recordSize > 0) {
                try {
                    os.write(noteArray);
                } catch (IOException e) {
                    handler.post(() -> {
                        callBack.onRecordAudioErr("writeData IOException2");
                    });
                }
            }
        }
        if (os != null) {
            try {
                os.close();
            } catch (IOException e) {

            }
        }
    }

    // 这里得到可播放的音频文件
    private void convertWavFile(final String outFileName) {
        Log.d("调试信息", "convertWaveFile:" + outFileName);
        service.execute(() -> {
            FileInputStream in = null;
            FileOutputStream out = null;
            long totalAudioLen = 0;
            long totalDataLen = totalAudioLen + 36;
            int channels = 1;
            long byteRate = 16 * AudioUtil.audioRate * channels / 8;
            byte[] data = new byte[bufferSize];
            try {
                in = new FileInputStream(pcmFile);
                out = new FileOutputStream(outFileName);
                totalAudioLen = in.getChannel().size();
                //由于不包括RIFF和WAV
                totalDataLen = totalAudioLen + 36;
                WriteWaveFileHeader(out, totalAudioLen, totalDataLen, audioRate, channels, byteRate);
                while (in.read(data) != -1) {
                    out.write(data);
                }
                out.flush();
                handler.post(() -> {
                    callBack.onRecordComplete(outFileName);
                });
            } catch (IOException e) {
                e.printStackTrace();
                Log.e("调试信息", "convertWaveFile IOException: " + e.getMessage());
                handler.post(() -> {
                    callBack.onRecordAudioErr("convertWaveFile IOException");
                });
            } finally {
                try {
                    if (in != null) {
                        in.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    if (out != null) {
                        out.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (pcmFile.isFile() && pcmFile.exists()) {
                    pcmFile.delete();
                }
            }
        });
    }

    /* 任何一种文件在头部添加相应的头文件才能够确定的表示这种文件的格式，wave是RIFF文件结构，
    每一部分为一个chunk，其中有RIFF WAVE chunk， FMT Chunk，Fact chunk,Data chunk,其中Fact chunk是可以选择的， */
    private void WriteWaveFileHeader(FileOutputStream out, long totalAudioLen, long totalDataLen, long longSampleRate,
                                     int channels, long byteRate) throws IOException {
        byte[] header = new byte[44];
        header[0] = 'R'; // RIFF
        header[1] = 'I';
        header[2] = 'F';
        header[3] = 'F';
        header[4] = (byte) (totalDataLen & 0xff);//数据大小
        header[5] = (byte) ((totalDataLen >> 8) & 0xff);
        header[6] = (byte) ((totalDataLen >> 16) & 0xff);
        header[7] = (byte) ((totalDataLen >> 24) & 0xff);
        header[8] = 'W';//WAVE
        header[9] = 'A';
        header[10] = 'V';
        header[11] = 'E';
        //FMT Chunk
        header[12] = 'f'; // 'fmt '
        header[13] = 'm';
        header[14] = 't';
        header[15] = ' ';//过渡字节
        //数据大小
        header[16] = 16; // 4 bytes: size of 'fmt ' chunk
        header[17] = 0;
        header[18] = 0;
        header[19] = 0;
        //编码方式 10H为PCM编码格式
        header[20] = 1; // format = 1
        header[21] = 0;
        //通道数
        header[22] = (byte) channels;
        header[23] = 0;
        //采样率，每个通道的播放速度
        header[24] = (byte) (longSampleRate & 0xff);
        header[25] = (byte) ((longSampleRate >> 8) & 0xff);
        header[26] = (byte) ((longSampleRate >> 16) & 0xff);
        header[27] = (byte) ((longSampleRate >> 24) & 0xff);
        //音频数据传送速率,采样率*通道数*采样深度/8
        header[28] = (byte) (byteRate & 0xff);
        header[29] = (byte) ((byteRate >> 8) & 0xff);
        header[30] = (byte) ((byteRate >> 16) & 0xff);
        header[31] = (byte) ((byteRate >> 24) & 0xff);
        // 确定系统一次要处理多少个这样字节的数据，确定缓冲区，通道数*采样位数
        header[32] = (byte) (1 * 16 / 8);
        header[33] = 0;
        //每个样本的数据位数
        header[34] = 16;
        header[35] = 0;
        //Data chunk
        header[36] = 'd';//data
        header[37] = 'a';
        header[38] = 't';
        header[39] = 'a';
        header[40] = (byte) (totalAudioLen & 0xff);
        header[41] = (byte) ((totalAudioLen >> 8) & 0xff);
        header[42] = (byte) ((totalAudioLen >> 16) & 0xff);
        header[43] = (byte) ((totalAudioLen >> 24) & 0xff);
        out.write(header, 0, 44);
    }

    //创建文件夹,首先创建目录，然后创建对应的文件
    public void createFile() {
        File file = new File(savePath);
        if (file.exists()) {
            file.delete();
        } else {
            file.getParentFile().mkdirs();
        }
        pcmFile = new File(file.getParentFile(), "audio_record_temp.pcm");//PathUtil.getTempPath() 获取临时存放pcm文件的路径
        if (pcmFile.exists()) {
            pcmFile.delete();
        } else {
            pcmFile.getParentFile().mkdirs();
        }
        try {
            pcmFile.createNewFile();
        } catch (IOException e) {
        }
    }

    //记录数据
    private void recordData() {
        new Thread(new WriteThread()).start();
    }

    public interface RecordAudioCallBack {
        /**
         * 录制失败
         *
         * @param msg 失败消息
         */
        void onRecordAudioErr(String msg);

        /**
         * 录制完成
         *
         * @param path 存储路径
         */
        void onRecordComplete(String path);
    }

}


package cn.mvp.mlibs.utils;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;

import java.util.concurrent.atomic.AtomicInteger;

public class MediaPlayerHelper {
    private MediaPlayer mediaPlayer;

    private static MediaPlayerHelper instance;

    private MediaPlayerHelper() {

    }

    public static MediaPlayerHelper getInstance() {
        if (instance == null) {
            synchronized (MediaPlayerHelper.class) {
                if (instance == null) {
                    instance = new MediaPlayerHelper();
                }
            }
        }
        return instance;
    }


    public void start() {
        if (mediaPlayer != null) {
            mediaPlayer.start();
        }
    }

    public void pause() {
        if (mediaPlayer != null) {
            mediaPlayer.pause();
        }
    }

    public void stop() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }


    public void playRawFile(Context context, int rawResourceId, MediaPlayer.OnCompletionListener onCompletionListener) {
        checkNull();
        mediaPlayer = MediaPlayer.create(context, rawResourceId);
        if (onCompletionListener != null) {
            mediaPlayer.setOnCompletionListener(onCompletionListener);
        }
        start();
    }

    /**
     * 播放指定次数
     */
    public void playRawFile(Context context, int rawResourceId, int loopingCount, MediaPlayer.OnCompletionListener onCompletionListener) {
        checkNull();
        AtomicInteger loopCount = new AtomicInteger();
        mediaPlayer = MediaPlayer.create(context, rawResourceId);
        mediaPlayer.setLooping(true);
        mediaPlayer.setOnCompletionListener(mp -> {
            if (loopCount.get() == loopingCount) {
                mediaPlayer.setLooping(false);
                if (onCompletionListener != null) {
                    onCompletionListener.onCompletion(mp);
                }
            }
            loopCount.incrementAndGet();
        });

        start();
    }


    /**
     * @param isCycle 循环
     */
    public void playUri(Context context, Uri uri, boolean isCycle, MediaPlayer.OnCompletionListener onCompletionListener) {
        checkNull();
        mediaPlayer = MediaPlayer.create(context, uri);
        mediaPlayer.setLooping(isCycle);
        if (onCompletionListener != null) {
            mediaPlayer.setOnCompletionListener(onCompletionListener);
        }
        start();
    }

    private void checkNull() {
        if (mediaPlayer != null) {
            mediaPlayer.reset();
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    public void playFile(String filePath, MediaPlayer.OnCompletionListener onCompletionListener) {
        try {
            checkNull();

            mediaPlayer = new MediaPlayer();
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.setDataSource(filePath);
            mediaPlayer.prepare();
            mediaPlayer.start();

            if (onCompletionListener != null) {
                mediaPlayer.setOnCompletionListener(onCompletionListener);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
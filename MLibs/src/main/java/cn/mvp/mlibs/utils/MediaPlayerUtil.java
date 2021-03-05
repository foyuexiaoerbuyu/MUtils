package cn.mvp.mlibs.utils;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;


/**
 * https://www.jianshu.com/p/5a8a78f8fd0a
 * 振铃实现——MediaPlayer类
 */
public class MediaPlayerUtil {
    private static MediaPlayer mMediaPlayer;
//void setDataSource (Context context , Uri uri)//根据Uri设置音频，当然还有其他几个重载的方法来指定特定的音频。
//void setLooping (boolean looping)//设置是否循环播放
//void prepare ()//让MediaPlayer真正去装载音频文件
//void start ()//开始或恢复播放
//void pause ()//暂停播放，调用start()可以恢复播放
//void stop ()//停止播放
//boolean isPlaying ()//是否正在播放
//void release ()//释放与此MediaPlayer关联的资源

    /** 播放通知声音 */
    public static void playNotificationRing(Context context) {
        Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);//用于获取手机默认铃声的Uri
        playRing(context, notification, false);
    }

    /** 开始播放手机默认铃声 */
    public static void playRing(Context context) {
        Uri alert = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);//用于获取手机默认铃声的Uri
        playRing(context, alert, false);
    }

    /** 播放指定铃声或音频文件 */
    public static void playRing(final Context context, Uri alert, boolean isLooping) {
        try {
            initMdeiaPlay();
            mMediaPlayer.setDataSource(context, alert);
            mMediaPlayer.setAudioStreamType(AudioManager.STREAM_RING);//告诉mediaPlayer播放的是铃声流
            /* 准备播放 */
            mMediaPlayer.prepare();
            /* 开始播放 */
            mMediaPlayer.start();
            /* 是否单曲循环 */
            mMediaPlayer.setLooping(isLooping);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /** 初始化 */
    private static void initMdeiaPlay() {
        if (mMediaPlayer != null) {
            mMediaPlayer.reset();
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
        mMediaPlayer = new MediaPlayer();
    }

    //销毁音乐
    public static void destoryMusic() {
        if (mMediaPlayer != null) {
            if (mMediaPlayer.isPlaying()) {
                mMediaPlayer.stop();
                mMediaPlayer.release();
                mMediaPlayer = null;
            }
        }
    }

    // 暂停播放
    private void pauseMusic() {
        if (mMediaPlayer.isPlaying()) {// 正在播放
            mMediaPlayer.pause();// 暂停
        } else {// 没有播放
            mMediaPlayer.start();
        }
    }

    // 停止播放
    private void stopMusic() {
        if (mMediaPlayer != null && mMediaPlayer.isPlaying()) {
            mMediaPlayer.stop();
        }
    }
}

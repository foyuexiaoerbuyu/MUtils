package cn.mvp.mlibs.weight.videorecord;

import android.Manifest;
import android.app.Activity;
import android.graphics.ImageFormat;
import android.hardware.Camera;
import android.media.MediaCodecInfo;
import android.media.MediaCodecList;
import android.os.Environment;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import androidx.core.app.ActivityCompat;

import java.io.File;
import java.io.IOException;

/**
 * 音视频流文件录制编码以及合成为mp4格式文件
 * https://github.com/loveONEPEICE/TestToMp4
 * https://github.com/foyuexiaoerbuyu/TestToMp4
 * <p>
 * https://github.com/wuqingsen/ToMp4Wu
 *
 * <p>
 * //申请权限
 * RecordVideoManage.getInstance().applyPermission(this, 666);
 * RecordVideoManage.getInstance().checkAVFormat();
 * SurfaceView surfaceView = (SurfaceView) findViewById(R.id.surface_view);
 * startStopButton = (Button) findViewById(R.id.startStop);
 * RecordVideoManage.getInstance().init(surfaceView);
 * //开始录制
 * RecordVideoManage.getInstance().startRecord();
 * //停止录制
 * RecordVideoManage.getInstance().stopRecord();
 */
public class RecordVideoManage {
    private static final String TAG = "RecordVideoManage";
    private static RecordVideoManage instance;

    Camera camera;
    SurfaceHolder surfaceHolder;
    int width = 1920;
    int height = 1080;

    SurfaceView surfaceView;
    private String mRecordVideoPath;

    private RecordVideoManage() {

    }

    public static RecordVideoManage getInstance() {
        if (instance == null) {
            synchronized (RecordVideoManage.class) {
                if (instance == null) {
                    instance = new RecordVideoManage();
                }
            }
        }
        return instance;
    }

    public void init(SurfaceView surfaceView) {
        surfaceHolder = surfaceView.getHolder();
        surfaceHolder.addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder surfaceHolder1) {
                Log.i(TAG, "enter surfaceCreated method");
                surfaceHolder = surfaceHolder1;
            }

            @Override
            public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {
                Log.i(TAG, "enter surfaceChanged method");
            }

            @Override
            public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
                Log.i(TAG, "enter surfaceDestroyed method");
                MediaMuxerThread.stopMuxer();
                stopCamera();

            }
        });
    }

    public void stopRecord() {
        //停止混合任务
        MediaMuxerThread.stopMuxer();
        //关闭摄像头
        stopCamera();
    }

    /**
     * @param recordVideoPath Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + System.currentTimeMillis() + "_myav.mp4"
     * @param cameraId        0:是后摄像头 1:前摄像头</>
     *                        后: Camera.CameraInfo.CAMERA_FACING_BACK  前:Camera.CameraInfo.CAMERA_FACING_FRONT
     */
    public void startRecord(String recordVideoPath, int cameraId) {
        //打开摄像头
        startCamera(cameraId);
        //开始混合任务
        mRecordVideoPath = recordVideoPath;
        if (mRecordVideoPath == null) {
            mRecordVideoPath = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "Record_" + System.currentTimeMillis() + ".mp4";
        }
        MediaMuxerThread.startMuxer();
    }

    public String getRecordVideoPath() {
        return mRecordVideoPath;
    }

    public void setRecordVideoPath(String recordVideoPath) {
        mRecordVideoPath = recordVideoPath;
    }

    //申请权限
    public void applyPermission(Activity activity, int reqCode) {
        String[] limits = {Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO,
                Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};
        ActivityCompat.requestPermissions(activity, limits, reqCode);
    }

    //----------------------- 摄像头操作相关 --------------------------------------

    /**
     * 打开摄像头
     */
    private void startCamera(int cameraId) {
        camera = Camera.open(cameraId);
        camera.setDisplayOrientation(90);
        Camera.Parameters parameters = camera.getParameters();
        parameters.setPreviewFormat(ImageFormat.NV21);

        // 这个宽高的设置必须和后面编解码的设置一样，否则不能正常处理
        parameters.setPreviewSize(width, height);

        try {
            camera.setParameters(parameters);
            camera.setPreviewDisplay(surfaceHolder);
            camera.setPreviewCallback(new Camera.PreviewCallback() {
                @Override
                public void onPreviewFrame(byte[] bytes, Camera camera) {
                    MediaMuxerThread.addVideoFrameData(bytes);
                }
            });
            camera.startPreview();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 关闭摄像头
     */
    private void stopCamera() {
        // 停止预览并释放资源
        if (camera != null) {
            camera.setPreviewCallback(null);
            camera.stopPreview();
            camera = null;
        }
    }


    //判断手机支持的编码格式
    public void checkAVFormat() {
        int numCodecs = MediaCodecList.getCodecCount();
        for (int i = 0; i < numCodecs; i++) {
            MediaCodecInfo codecInfo = MediaCodecList.getCodecInfoAt(i);
            if (!codecInfo.isEncoder()) {
                continue;
            }
            String[] types = codecInfo.getSupportedTypes();
            for (int j = 0; j < types.length; j++) {
                if (types[j].equalsIgnoreCase("video/avc")) {
                    Log.i(TAG, "支持视频H.264(avc)编码...");
                }
                if (types[j].equalsIgnoreCase("audio/mp4a-latm")) {
                    Log.i(TAG, "支持音频aac编码...");
                }
            }
        }
    }
}

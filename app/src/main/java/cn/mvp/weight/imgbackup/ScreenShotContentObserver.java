package cn.mvp.weight.imgbackup;

import android.content.ContentResolver;
import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.provider.MediaStore;

/**
 * 拍照截图观察者,拍照截图备份
 * 使用:
 * 1.
 * screenShotContentObserver = new ScreenShotContentObserver(new Handler(), this) {
 *
 * @Override protected void onScreenShot(String path, String fileName) {
 * Log.i("path = [" + path + "], fileName = [" + fileName + "]");
 * }
 * };
 * 2.
 * getContentResolver().registerContentObserver(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, true, screenShotContentObserver);
 * 3.
 * getContentResolver().unregisterContentObserver(screenShotContentObserver);
 */
public abstract class ScreenShotContentObserver extends ContentObserver {

    private Context context;
    private boolean isFromEdit = false;
    private String previousPath;

    public ScreenShotContentObserver(Handler handler, Context context) {
        super(handler);
        this.context = context;
    }

    @Override
    public boolean deliverSelfNotifications() {
        return super.deliverSelfNotifications();
    }

    @Override
    public void onChange(boolean selfChange) {
        super.onChange(selfChange);
    }

    @Override
    public void onChange(boolean selfChange, Uri uri) {
        Cursor cursor = null;
        try {
            ContentResolver contentResolver = context.getContentResolver();
            cursor = contentResolver.query(uri, new String[]{
                    MediaStore.Images.Media.DISPLAY_NAME,
                    MediaStore.Images.Media.DATA
            }, null, null, null);
            if (cursor != null && cursor.moveToLast()) {
                int displayNameColumnIndex = cursor.getColumnIndex(MediaStore.Images.Media.DISPLAY_NAME);
                int dataColumnIndex = cursor.getColumnIndex(MediaStore.Images.Media.DATA);
                String fileName = cursor.getString(displayNameColumnIndex);
                String path = cursor.getString(dataColumnIndex);
//                if (new File(path).lastModified() >= System.currentTimeMillis() - 10000) {
//                    if (isScreenshot(path) && !isFromEdit && !(previousPath != null && previousPath.equals(path))) {
//                        onScreenShot(path, fileName);
//                    }
//                    previousPath = path;
//                    isFromEdit = false;
//                } else {
//                    cursor.close();
//                    return;
//                }
                onScreenShot(path, fileName);
//                Log.e("监听到的数据take pic", fileName + "----" + path);
            }
        } catch (Exception e) {
            e.printStackTrace();
            isFromEdit = true;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        super.onChange(selfChange, uri);
    }

    private boolean isScreenshot(String path) {
        return path != null && path.toLowerCase().contains("screenshot");
    }

    protected abstract void onScreenShot(String path, String fileName);

}
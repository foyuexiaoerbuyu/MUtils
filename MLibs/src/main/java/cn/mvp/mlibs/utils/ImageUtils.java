package cn.mvp.mlibs.utils;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.DrawableRes;


/**
 * 使用方法：在Activity里，通过某个点击事件，触发
 * ImageUtils.showImagePickDialog(this);
 * 会自动进入拍照/相册界面
 * <p>
 * 在调用的Activity里，重写方法：来接收返回的图片
 * protected void onActivityResult(int requestCode, int resultCode, Intent data) {
 * super.onActivityResult(requestCode, resultCode, data); switch (requestCode) {
 * case ImageUtils.REQUEST_CODE_FROM_ALBUM: if(resultCode == RESULT_CANCELED) { return; } Uri p_w_picpathUri = data.getData(); //这里得到图片后做相应操作 .............................. break;
 * case ImageUtils.REQUEST_CODE_FROM_CAMERA: if(resultCode == RESULT_CANCELED) { ImageUtils.deleteImageUri(this, ImageUtils.p_w_picpathUriFromCamera); } else { Uri p_w_picpathUriCamera = ImageUtils.p_w_picpathUriFromCamera; //这里得到图片后做相应操作 .............................. } break; default: break;
 * }
 * }
 */
public class ImageUtils {
    public static final int REQUEST_CODE_FROM_CAMERA = 5001;
    public static final int REQUEST_CODE_FROM_ALBUM = 5002;
    /**
     * 存放拍照图片的uri地址
     */
    public static Uri p_w_picpathUriFromCamera;

    /**
     * 显示获取照片不同方式对话框
     */
    public static void showImagePickDialog(final Activity activity) {
        String title = "选择获取图片方式";
        String[] items = new String[]{"拍照", "相册"};
        new AlertDialog.Builder(activity).setTitle(title).setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                switch (which) {
                    case 0:
                        pickImageFromCamera(activity);
                        break;
                    case 1:
                        pickImageFromAlbum(activity);
                        break;
                    default:
                        break;
                }
            }
        }).show();
    }

    /**
     * 打开相机拍照获取图片
     */
    public static void pickImageFromCamera(final Activity activity) {
        p_w_picpathUriFromCamera = createImageUri(activity);
        Intent intent = new Intent();
        intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, p_w_picpathUriFromCamera);
        activity.startActivityForResult(intent, REQUEST_CODE_FROM_CAMERA);
    }

    /**
     * 打开本地相册选取图片
     */
    public static void pickImageFromAlbum(final Activity activity) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.setType("p_w_picpath/*");
        activity.startActivityForResult(intent, REQUEST_CODE_FROM_ALBUM);
    }

    /**
     * 打开本地相册选取图片2
     */
    public static void pickImageFromAlbum2(final Activity activity) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_PICK);
        intent.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        activity.startActivityForResult(intent, REQUEST_CODE_FROM_ALBUM);
    }

    /**
     * 创建一条图片uri,用于保存拍照后的照片
     */
    private static Uri createImageUri(Context context) {
        String name = "boreWbImg" + System.currentTimeMillis();
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, name);
        values.put(MediaStore.Images.Media.DISPLAY_NAME, name + ".jpeg");
        values.put(MediaStore.Images.Media.MIME_TYPE, "p_w_picpath/jpeg");
        Uri uri = context.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        return uri;
    }

    /**
     * 删除一条图片
     */
    public static void deleteImageUri(Context context, Uri uri) {
        context.getContentResolver().delete(p_w_picpathUriFromCamera, null, null);
    }

    /**
     * 获取图片文件路径
     */
    public static String getImageAbsolutePath(Context context, Uri uri) {
        Cursor cursor = MediaStore.Images.Media.query(context.getContentResolver(), uri, new String[]{MediaStore.Images.Media.DATA});
        if (cursor.moveToFirst()) {
            return cursor.getString(0);
        }
        return null;
    } /////////////////////Android4.4以上版本特殊处理如下//////////////////////////////////////

    /**
     * 根据Uri获取图片绝对路径，解决Android4.4以上版本Uri转换
     *
     * @param context
     * @param p_w_picpathUri
     */
    @TargetApi(Build.VERSION_CODES.KITKAT)
    public static String getImageAbsolutePath19(Activity context, Uri p_w_picpathUri) {
        if (context == null || p_w_picpathUri == null) return null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT && DocumentsContract.isDocumentUri(context, p_w_picpathUri)) {
            if (isExternalStorageDocument(p_w_picpathUri)) {
                String docId = DocumentsContract.getDocumentId(p_w_picpathUri);
                String[] split = docId.split(":");
                String type = split[0];
                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }
            } else if (isDownloadsDocument(p_w_picpathUri)) {
                String id = DocumentsContract.getDocumentId(p_w_picpathUri);
                Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));
                return getDataColumn(context, contentUri, null, null);
            } else if (isMediaDocument(p_w_picpathUri)) {
                String docId = DocumentsContract.getDocumentId(p_w_picpathUri);
                String[] split = docId.split(":");
                String type = split[0];
                Uri contentUri = null;
                if ("p_w_picpath".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }
                String selection = MediaStore.Images.Media._ID + "=?";
                String[] selectionArgs = new String[]{split[1]};
                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        }  // MediaStore (and general)
        if ("content".equalsIgnoreCase(p_w_picpathUri.getScheme())) { // Return the remote address if (isGooglePhotosUri(p_w_picpathUri)) return p_w_picpathUri.getLastPathSegment(); return getDataColumn(context, p_w_picpathUri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(p_w_picpathUri.getScheme())) {
            return p_w_picpathUri.getPath();
        }
        return null;
    }

    private static String getDataColumn(Context context, Uri uri, String selection, String[] selectionArgs) {
        Cursor cursor = null;
        String column = MediaStore.Images.Media.DATA;
        String[] projection = {column};
        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, null);
            if (cursor != null && cursor.moveToFirst()) {
                int index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(index);
            }
        } finally {
            if (cursor != null) cursor.close();
        }
        return null;
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    private static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    private static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    private static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is Google Photos.
     */
    private static boolean isGooglePhotosUri(Uri uri) {
        return "com.google.android.apps.photos.content".equals(uri.getAuthority());
    }

    private void bigImageLoader(Context context, Bitmap bitmap) {
        final Dialog dialog = new Dialog(context);
        ImageView image = new ImageView(context);
        image.setImageBitmap(bitmap);
        dialog.setContentView(image);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.show();
        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
            }
        });
    }

    public static void checkImgSrc(ImageView imageView, @DrawableRes int selImg, @DrawableRes int unSelImg, boolean isSel) {
        imageView.setImageResource(isSel ? selImg : unSelImg);
    }

    public static void checkImgBg(ImageView imageView, @DrawableRes int selImg, @DrawableRes int unSelImg, boolean isSel) {
        imageView.setBackground(isSel ? UIUtils.getDrawable(selImg) : UIUtils.getDrawable(unSelImg));
    }
}

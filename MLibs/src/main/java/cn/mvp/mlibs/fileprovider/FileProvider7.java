package cn.mvp.mlibs.fileprovider;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ProviderInfo;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Build;
import android.support.v4.content.FileProvider;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;


/**
 * Android 7.0 行为变更 通过FileProvider在应用间共享文件
 * https://blog.csdn.net/lmj623565791/article/details/72859156
 * 安卓7.0 url文件路径适配,setFlags要放在addFlags前面,否则会取消共享权限
 * Created by zhanghongyang01 on 17/5/31.
 */

public class FileProvider7 extends FileProvider {


    /**
     * 适配7.0及以下版本获取文件uri 该方法可以使用,旨在不进行别的修改,只做适配的情况下使用;
     * 推荐使用  setIntentDataAndType(Context context, Intent intent, String type, File file, boolean writeAble);方法
     * 安卓7.0 url文件路径适配,setFlags要放在addFlags前面,否则会取消共享权限
     * 只有读取权限.没有写入权限 (共享文件夹)
     */
    public static Uri getUriForFile(Context context, Intent intent, File file) {
        if (Build.VERSION.SDK_INT >= 24) {
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);//临时读取权限
        }
        return getUriForFile(context, file);
    }

    /**
     * (不推荐使用,该方法没有添加共享文件夹的读取权限,兼容7.0及以下)
     * 记得添加临时权限:intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);//临时读取权限
     *
     * @return 适配7.0及以下版本获取文件uri
     */
    private static Uri getUriForFile(Context context, File file) {
        Uri fileUri = null;
        if (Build.VERSION.SDK_INT >= 24) {
            fileUri = getUriForFile24(context, file);
        } else {
            fileUri = Uri.fromFile(file);
        }
        return fileUri;
    }


    /**
     * (不推荐使用),该方法只针对7.0获取文件uri)
     *
     * @return uri
     */
    public static Uri getUriForFile24(Context context, File file) {
        Uri fileUri = FileProvider.getUriForFile(context,
                context.getPackageName() + ".android7.fileprovider",
                file);
        return fileUri;
    }

    /**
     * 设置共享文件夹类型及权限(读取 / 写入)
     *
     * @param writeAble 是否授予写入权限
     */
    public static void setIntentDataAndType(Context context,
                                            Intent intent,
                                            String type,
                                            File file,
                                            boolean writeAble) {
        if (Build.VERSION.SDK_INT >= 24) {
            intent.setDataAndType(getUriForFile(context, file), type);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);//临时读取权限
            if (writeAble) {
                intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            }
        } else {
            intent.setDataAndType(Uri.fromFile(file), type);
        }
    }

    /**
     * (推荐使用,uri兼容7.0及以下,且设置了读取权限)设置共享文件夹权限(读取 / 写入)
     *
     * @param writeAble 是否授予写入权限
     */
    public static void setIntentData(Context context,
                                     Intent intent,
                                     File file,
                                     boolean writeAble) {
        if (Build.VERSION.SDK_INT >= 24) {
            intent.setData(getUriForFile(context, file));
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);//临时读取权限
            if (writeAble) {
                intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            }
        } else {
            intent.setData(Uri.fromFile(file));
        }
    }


    /**
     * 授权给某个app共享文件夹(文件)权限
     *
     * @param uri       共享文件或文件夹的路径(
     *                  Uri fileUri = FileProvider.getUriForFile(this, "com.zhy.android7.fileprovider", file);
     *                  或:
     *                  Uri fileUri = FileProvider7.getUriForFile(context, intent, new File(path));)
     * @param writeAble 是否共享给用户写入权限(默认其他app只可以读取共享文件夹的权限)
     */
    public static void grantPermissions(Context context, Intent intent, Uri uri, boolean writeAble) {

        int flag = Intent.FLAG_GRANT_READ_URI_PERMISSION;
        if (writeAble) {
            flag |= Intent.FLAG_GRANT_WRITE_URI_PERMISSION;
        }
        intent.addFlags(flag);
        List<ResolveInfo> resInfoList = context.getPackageManager()
                .queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
        for (ResolveInfo resolveInfo : resInfoList) {
            String packageName = resolveInfo.activityInfo.packageName;
            context.grantUriPermission(packageName, uri, flag);
        }
    }


    /**
     *  根据uri读取文件
     *  Uri.parse(uri)
     */
    public static InputStream readFile(Context context, Uri uri) {
//        File file = null;
        //获取文件名和大小
        InputStream inputStream = null;
        try {
            inputStream = context.getContentResolver().openInputStream(uri);
            return inputStream;
//            String path = SDCardUtils.getExternalFilesDir(context).getPath() + "/tmp/" + uri.substring(uri.lastIndexOf("/") + 1);
//            FileUtils.writeFile(path, inputStream);
//            file = new File(path);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return inputStream;
    }

    /**
     * 反射 获取FileProvider path
     */
    public static String getFPUriToPath(Context context, String uriStr) {
        Uri uri = Uri.parse(uriStr);
        try {
            List<PackageInfo> packs = context.getPackageManager().getInstalledPackages(PackageManager.GET_PROVIDERS);
            if (packs != null) {
                String fileProviderClassName = FileProvider.class.getName();
                for (PackageInfo pack : packs) {
                    ProviderInfo[] providers = pack.providers;
                    if (providers != null) {
                        for (ProviderInfo provider : providers) {
                            if (uri.getAuthority().equals(provider.authority)) {
                                if (provider.name.equalsIgnoreCase(fileProviderClassName)) {
                                    Class<FileProvider> fileProviderClass = FileProvider.class;
                                    try {
                                        Method getPathStrategy = fileProviderClass.getDeclaredMethod("getPathStrategy", Context.class, String.class);
                                        getPathStrategy.setAccessible(true);
                                        Object invoke = getPathStrategy.invoke(null, context, uri.getAuthority());
                                        if (invoke != null) {
                                            String PathStrategyStringClass = FileProvider.class.getName() + "$PathStrategy";
                                            Class<?> PathStrategy = Class.forName(PathStrategyStringClass);
                                            Method getFileForUri = PathStrategy.getDeclaredMethod("getFileForUri", Uri.class);
                                            getFileForUri.setAccessible(true);
                                            Object invoke1 = getFileForUri.invoke(invoke, uri);
                                            if (invoke1 instanceof File) {
                                                String filePath = ((File) invoke1).getAbsolutePath();
                                                return filePath;
                                            }
                                        }
                                    } catch (NoSuchMethodException e) {
                                        e.printStackTrace();
                                    } catch (InvocationTargetException e) {
                                        e.printStackTrace();
                                    } catch (IllegalAccessException e) {
                                        e.printStackTrace();
                                    } catch (ClassNotFoundException e) {
                                        e.printStackTrace();
                                    }
                                    break;
                                }
                                break;
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


}

package cn.mvp.chat2;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PushbackInputStream;
import java.io.RandomAccessFile;
import java.net.Socket;

import cn.mvp.R;

/**
 * https://www.twle.cn/l/yufei/android/android-basic-socket-tcp-uploadfile.html
 */
public class Chat2Activity extends AppCompatActivity implements View.OnClickListener {

    private EditText edit_fname;
    private Button btn_upload;
    private Button btn_stop;
    private ProgressBar pgbar;
    private TextView txt_result;

    private UploadHelper upHelper;
    private boolean flag = true;

    public static void open(Context context) {
        Intent starter = new Intent(context, Chat2Activity.class);
//    starter.putExtra();
        context.startActivity(starter);
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            pgbar.setProgress(msg.getData().getInt("length"));
            float num = (float) pgbar.getProgress() / (float) pgbar.getMax();
            int result = (int) (num * 100);
            txt_result.setText(result + "%");
            if (pgbar.getProgress() == pgbar.getMax()) {
                Toast.makeText(Chat2Activity.this, "上传成功", Toast.LENGTH_SHORT).show();
            }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat2);
        bindViews();
        upHelper = new UploadHelper(this);

        // 把 assets 下的 python.chm 拷贝到 sd
        File root = Environment.getExternalStorageDirectory();
        File dir = new File(root.getPath() + "/assets");

        if (!dir.exists()) {
            Boolean ok = dir.mkdirs();
        }

        String modelFilePath = "python.chm";
        Assets2Sd(this, modelFilePath, dir.getPath());
    }

    private void bindViews() {
        edit_fname = (EditText) findViewById(R.id.edit_fname);
        btn_upload = (Button) findViewById(R.id.btn_upload);
        btn_stop = (Button) findViewById(R.id.btn_stop);
        pgbar = (ProgressBar) findViewById(R.id.pgbar);
        txt_result = (TextView) findViewById(R.id.txt_result);

        btn_upload.setOnClickListener(this);
        btn_stop.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_upload:
                String filename = edit_fname.getText().toString();

                flag = true;
                Log.i("调试信息", "onClick:  " + Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED));
                if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                    /*File file = new File(Environment.getExternalStorageDirectory().getPath() + "/assets/", filename);
                    Log.i("调试信息", "onClick:  " + file.getAbsolutePath()+file.exists());
                    if (file.exists()) {
                        pgbar.setMax((int) file.length());
                        uploadFile(file);
                    } else {
                    }*/
                } else {
                }
                break;
            case R.id.btn_stop:
                flag = false;
                break;
        }
    }

    private void uploadFile(final File file) {
        new Thread(new Runnable() {
            public void run() {
                try {
                    String sourceid = upHelper.getBindId(file);
                    Socket socket = new Socket("172.31.254.234", 12345);
                    OutputStream outStream = socket.getOutputStream();
                    String head = "Content-Length=" + file.length() + ";filename=" + file.getName()
                            + ";sourceid=" + (sourceid != null ? sourceid : "") + "\r\n";
                    outStream.write(head.getBytes());

                    PushbackInputStream inStream = new PushbackInputStream(socket.getInputStream());
                    String response = StreamHelper.readLine(inStream);
                    String[] items = response.split(";");
                    String responseSourceid = items[0].substring(items[0].indexOf("=") + 1);
                    String position = items[1].substring(items[1].indexOf("=") + 1);
                    if (sourceid == null) {//如果是第一次上传文件，在数据库中不存在该文件所绑定的资源id
                        upHelper.save(responseSourceid, file);
                    }
                    RandomAccessFile fileOutStream = new RandomAccessFile(file, "r");
                    fileOutStream.seek(Integer.valueOf(position));
                    byte[] buffer = new byte[1024];
                    int len = -1;
                    int length = Integer.valueOf(position);
                    while (flag && (len = fileOutStream.read(buffer)) != -1) {
                        outStream.write(buffer, 0, len);
                        length += len;//累加已经上传的数据长度
                        Message msg = new Message();
                        msg.getData().putInt("length", length);
                        handler.sendMessage(msg);
                    }
                    if (length == file.length()) upHelper.delete(file);
                    fileOutStream.close();
                    outStream.close();
                    inStream.close();
                    socket.close();
                } catch (Exception e) {
                }
            }
        }).start();
    }

    /***
     * 调用方式
     *
     * String path = Environment.getExternalStorageDirectory().toString() + "/" + "Tianchaoxiong/useso";
     String modelFilePath = "Model/seeta_fa_v1.1.bin";
     Assets2Sd(this, modelFilePath, path + "/" + modelFilePath);
     *
     * @param context
     * @param fileAssetPath assets中的目录
     * @param SdPath 要复制到sd卡中的目录
     */
    public static void Assets2Sd(Context context, String fileAssetPath, String SdPath) {
        //测试把文件直接复制到sd卡中 fileSdPath完整路径

        String dstFile = SdPath + "/" + fileAssetPath;
        File file = new File(dstFile);
        if (!file.exists()) {
            try {

                InputStream myInput;
                OutputStream myOutput = new FileOutputStream(dstFile);
                myInput = context.getAssets().open(fileAssetPath);
                byte[] buffer = new byte[1024];
                int length = myInput.read(buffer);
                while (length > 0) {
                    myOutput.write(buffer, 0, length);
                    length = myInput.read(buffer);
                }

                myOutput.flush();
                myInput.close();
                myOutput.close();

            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
        }

    }

}
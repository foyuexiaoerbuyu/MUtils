package cn.mvp;

import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.text.format.Formatter;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.hjq.toast.ToastUtils;
import com.king.zxing.CaptureActivity;
import com.king.zxing.Intents;
import com.king.zxing.util.CodeUtils;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import cn.mvp.chat.ChatActivity;
import cn.mvp.chat1.Chat1Activity;
import cn.mvp.global.Constant;
import cn.mvp.mlibs.MLibs;
import cn.mvp.mlibs.utils.ClipboardUtils;
import cn.mvp.mlibs.utils.StringUtil;
import cn.mvp.mlibs.utils.VerifyUtils;
import cn.mvp.utils.PermissionsUtils;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private boolean isRefuse;
    private ImageView mImgView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        PermissionsUtils.requestDefPermissions(this);
        findViewById(R.id.main_btn_qr_code).setOnClickListener(this);
        findViewById(R.id.main_btn_creat_qr_code).setOnClickListener(this);
        mImgView = findViewById(R.id.main_img);
//        Chat1Activity.open(MainActivity.this);
        findViewById(R.id.btn1).setOnClickListener(v -> ChatActivity.open(MainActivity.this));

        findViewById(R.id.btn2).setOnClickListener(v -> {
            Chat1Activity.open(MainActivity.this);
//            new Thread(new Runnable() {
//                @Override
//                public void run() {
//                    test1.main();
//                }
//            }).start();
        });
    }

    static void sendBroadCastToCenter() {
        WifiManager wifiMgr = (WifiManager) MLibs.getContext().getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo = wifiMgr.getConnectionInfo();
        /*这里获取了IP地址，获取到的IP地址还是int类型的。*/
        int ip = wifiInfo.getIpAddress();
		/*这里就是将int类型的IP地址通过工具转化成String类型的，便于阅读
        String ips = Formatter.formatIpAddress(ip);
		*/
        /*这一步就是将本机的IP地址转换成xxx.xxx.xxx.255*/
        int broadCastIP = ip | 0xFF000000;
        DatagramSocket theSocket = null;
        try {
            InetAddress server = InetAddress.getByName(Formatter.formatIpAddress(broadCastIP));
            theSocket = new DatagramSocket();
            String data = "Hello";
            DatagramPacket theOutput = new DatagramPacket(data.getBytes(), data.length(), server, 1122);
            /*这一句就是发送广播了，其实255就代表所有的该网段的IP地址，是由路由器完成的工作*/
            theSocket.send(theOutput);
            //----------------------------------------------------------------
//            DatagramSocket  theSocket1 = new DatagramSocket(1122);
//            byte[] buffer = new byte[1024];
//            DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
//            while (true) {
//                theSocket1.receive(packet);
//                String s = new String(packet.getData(), 0, packet.getLength(), "UTF-8");
//                Log.i("调试信息", "app接收到的数据:  " +  "address : " + packet.getAddress() + ", port : " + packet.getPort() + ", content : " + s);
//            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (theSocket != null)
                theSocket.close();
        }
    }


    private void sendUdpMsg(int mPort) {
        WifiManager wifiMgr = (WifiManager) this.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo = wifiMgr.getConnectionInfo();
        /*这里获取了IP地址，获取到的IP地址还是int类型的。*/
        int ip = wifiInfo.getIpAddress();
        /*这一步就是将本机的IP地址转换成xxx.xxx.xxx.255*/
        int broadCastIP = ip | 0xFF000000;
        DatagramSocket sendSocket = null;
        try {
            InetAddress server = InetAddress.getByName(Formatter.formatIpAddress(broadCastIP));
            sendSocket = new DatagramSocket();
            String data = "Hello";
            DatagramPacket theOutput = new DatagramPacket(data.getBytes(), data.length(), server, mPort);
            sendSocket.send(theOutput);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (sendSocket != null)
                sendSocket.close();
        }
    }


    // 带回授权结果
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1024 && Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            // 检查是否有权限
            if (Environment.isExternalStorageManager()) {
                isRefuse = false;
                // 授权成功
            } else {
                isRefuse = true;
                // 授权失败
            }
        }
        if (requestCode == 1122) {
            String result = data.getStringExtra(Intents.Scan.RESULT);
            Toast.makeText(this, result, Toast.LENGTH_SHORT).show();
        }
        if (requestCode == Constant.REQUEST_CODE_SCAN_QRCODE) {
            try {
                VerifyUtils.checkTrue(StringUtil.isBlank(data), "二维码数据为空");
                String result = data.getStringExtra(Intents.Scan.RESULT);
                ClipboardUtils.copyToClipboard(MainActivity.this, result);
                VerifyUtils.checkTrue(result != null, "已复制二维码信息到剪切板: " + result);
            } catch (IOException e) {
                ToastUtils.show(e.getLocalizedMessage());
            }
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.main_btn_qr_code) {
            startActivityForResult(new Intent(this, CaptureActivity.class), Constant.REQUEST_CODE_SCAN_QRCODE);
        } else if (v.getId() == R.id.main_btn_creat_qr_code) {
            mImgView.setImageBitmap(CodeUtils.createQRCode(String.valueOf(ClipboardUtils.getText(this)), 600));
            mImgView.setVisibility(View.VISIBLE);
            mImgView.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mImgView.setVisibility(View.GONE);
                }
            }, 5000);
        }

    }
}
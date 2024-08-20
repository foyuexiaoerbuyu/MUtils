package cn.mvp.mlibs.utils;

import android.app.NotificationManager;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;

import java.util.ArrayList;

import cn.mvp.mlibs.log.Log;
import cn.mvp.mlibs.utils.ents.SmsEnt;


/** 除非是默认短信应用否则无效 */
public class SmsUtils {

    /**
     * @return 所有短信
     */
    public static ArrayList<SmsEnt> getAllSms(Context context) {
        ArrayList<SmsEnt> smsEnts = new ArrayList<>();
        ContentResolver cr = context.getContentResolver();
        String[] projection = new String[]{"_id", "address", "person", "body", "date", "type"};
        Cursor cur = cr.query(Uri.parse("content://sms/"), projection, null, null, "date desc");
        if (null == cur) {
            Log.i("ooc", "************cur == null");
            return smsEnts;
        }
        while (cur.moveToNext()) {
            SmsEnt sms = new SmsEnt();
            String number = cur.getString(cur.getColumnIndex("address"));//手机号
            String name = cur.getString(cur.getColumnIndex("person"));//联系人姓名列表
            String body = cur.getString(cur.getColumnIndex("body"));//短信内容
            smsEnts.add(sms);
            Log.i(number + name + body);
        }
        cur.close();
        return smsEnts;
    }


    /**
     * @param address  短信发送方("95559")
     * @param sendTime 短信发送时间(System.currentTimeMillis())
     * @param content  短信发送内容("尊敬的x先生您好！您尾号xxxx的卡于 " + new SimpleDateFormat("MM月dd日kk时mm分", Locale.getDefault()).format(new Date()) + "在xx自助设备存现800.00元，交易后余额为1000.000.000.00元【交通银行】")
     * @param smsType  短信类型(接收还是发送:接收短信(1) 发送短信(2) 默认(0))
     */
    public static void sendSms(Context context, String address, String sendTime, String content, int smsType) {

        // 创建一个内容提供者，使用系统短信应用提供的ContentResolver向收件箱中插入一条短信
        ContentResolver contentResolver = context.getContentResolver();
        ContentValues values = new ContentValues();

        // 添加需要插入的内容
        values.put("address", address);//短信发送方
//        values.put("name", "贾正亮");//号码在通讯录中的姓名：无为null
        values.put("date", sendTime);//短信时间
        values.put("body", content);//短信内容
//        values.put("thread_id", boday);//同一个手机号互发的短信，其序号是相同的
        values.put("type", smsType);//1 接收短信 2 发送短信 默认0
        // 向指定的uri（短信收件箱数据库）插入values
        Uri uri = Uri.parse("content://sms/inbox");// 收件箱路径
        contentResolver.insert(uri, values);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationUtil.getInstance().createNotificationChannel("1221312", "短信通知", NotificationManager.IMPORTANCE_HIGH);
        }

        Intent extraIntent = new Intent();
        extraIntent.setAction(Intent.ACTION_SEND);
        NotificationUtil.getInstance().sendNotification("1221312", "短信通知", content, extraIntent);

    }


//短信数据库
//    _id：          短信序号，如100
//　　thread_id：对话的序号，如100，与同一个手机号互发的短信，其序号是相同的
//　　address：  发件人地址，即手机号，如+86138138000
//　　person：   发件人，如果发件人在通讯录中则为具体姓名，陌生人为null
//　　date：       日期，long型，如1346988516，可以对日期显示格式进行设置
//　　protocol： 协议0SMS_RPOTO短信，1MMS_PROTO彩信
//　　read：      是否阅读0未读，1已读
//　　status：    短信状态-1接收，0complete,64pending,128failed
//　　type：       短信类型1是接收到的，2是已发出
//　　body：      短信具体内容
//　　service_center：短信服务中心号码编号，如+8613800755500
//————————————————

    /**
     * 发送彩信
     */
    public void test01() {
//        Intent intent = new Intent(Intent.ACTION_SEND);
//        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//
//        String url = TestData.TEST_FILE;
//        intent.putExtra(Intent.EXTRA_STREAM, FileProvider7.getUriForFile(MainActivity.this, intent, new File(url)));
////            intent.putExtra(Intent.EXTRA_STREAM, Uri.parse(url));// uri为你的附件的uri
//        intent.putExtra("subject", "it's subject"); //彩信的主题
//        intent.putExtra("address", "10086"); //彩信发送目的号码
//        intent.putExtra("sms_body", "it's content"); //彩信中文字内容
//        intent.putExtra(Intent.EXTRA_TEXT, "it's EXTRA_TEXT");
//        intent.setType("image/*");// 彩信附件类型
//        intent.setClassName("com.android.mms","com.android.mms.ui.ComposeMessageActivity");
//        startActivity(intent);
    }
}

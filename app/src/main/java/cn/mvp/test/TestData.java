package cn.mvp.test;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;

import com.alibaba.fastjson.JSONObject;

import java.io.File;
import java.util.ArrayList;

import cn.mvp.MainActivity;
import cn.mvp.R;
import cn.mvp.db.Todos;
import cn.mvp.global.Constant;
import cn.mvp.mlibs.log.Log;
import cn.mvp.mlibs.utils.SDCardUtils;

public class TestData {
    public static String TEST_FILE = SDCardUtils.getExternalPublicStorageDirectory() + File.separator + "01测试文件" + File.separator + "测试文件用图.png";

    public static  ArrayList<Todos>  getData() {
        ArrayList<Todos> todos = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            Todos todo = new Todos();
            todo.setId(DataUtiles.getRandomNum(1000, 2000));
            todo.setMatterTitle("test" + i);
            todo.setCreatDate(DataUtiles.getRandomDate());
            todo.setImportantLevel(DataUtiles.getRandomNum(0, 4));
            todo.setRemindTime(DataUtiles.getRandomDate());
            todo.setRemindRepeat(DataUtiles.getRandomNum(0, 4) + "");
            todo.setNoteContent("无");
            todos.add(todo);
        }
        JSONObject.toJSONString(todos);

        return todos;
    }

}

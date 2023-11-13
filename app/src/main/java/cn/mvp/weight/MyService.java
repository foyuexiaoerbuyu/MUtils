package cn.mvp.weight;

import android.os.Build;
import android.service.quicksettings.Tile;
import android.service.quicksettings.TileService;
import androidx.annotation.RequiresApi;
import android.util.Log;

@RequiresApi(api = Build.VERSION_CODES.N)
public class MyService extends TileService {

    final String LOG_TAG = "MyTileService";

    @Override
    public void onTileAdded() {
        //当用户从Edit栏添加到快速设置中调用
        Log.d(LOG_TAG, "onTileAdded");
    }

    @Override
    public void onTileRemoved() {
        //当用户从快速设置栏中移除的时候调用
        Log.d(LOG_TAG, "onTileRemoved");
    }

    @Override
    public void onClick() {
        // 点击的时候
        Log.d(LOG_TAG, "onClick");

        int state = getQsTile().getState();
        if (state == Tile.STATE_INACTIVE) {
            // 更改成非活跃状态     (还有一个参数：STATE_UNAVAILABLE 非可点击状态)
            getQsTile().setState(Tile.STATE_ACTIVE);
        } else {
            //更改成活跃状态
            getQsTile().setState(Tile.STATE_INACTIVE);
        }

        //可以点击设置图标，设置方式如下：
        //Icon icon = Icon.createWithResource(getApplicationContext(), R.drawable.xxxx);
        //getQsTile().setIcon(icon);
        //设置label：
        //getQsTile.setLabel("");
        //更新Tile
        getQsTile().updateTile();
    }

    @Override
    public void onStartListening () {
        // 打开下拉通知栏的时候调用,当快速设置按钮并没有在编辑栏拖到设置栏中不会调用
        //在TleAdded之后会调用一次
        Log.d(LOG_TAG, "onStartListening");
    }

    @Override
    public void onStopListening () {
        // 关闭下拉通知栏的时候调用,当快速设置按钮并没有在编辑栏拖到设置栏中不会调用
        // 在onTileRemoved移除之前也会调用移除
        Log.d(LOG_TAG, "onStopListening");
    }
}
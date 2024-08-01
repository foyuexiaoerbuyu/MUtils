package cn.mvp.other;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.Context;
import android.os.Handler;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

/**
 * 蓝牙工具类
 * https://juejin.cn/post/7195792730572259384
 */
public class BluetoothUtil {
    private BluetoothManager mBluetoothManager;
    private BluetoothGatt mBluetoothGatt;
    private MyBluetoothGattCallback mBluetoothGattCallback;
    private final static String TAG = "BluetoothUtil";
    private final static UUID mServiceUUID = UUID.fromString("xxx");
    //0000fdc8-0000-1000-8000-00805f9b34fb类似这类id
    private final static UUID mNotifyCharacteristicUUID = UUID.fromString("xxx");
    private final static UUID mDescriptorUUID = UUID.fromString("xxx");
    private final static UUID mWriteCharacteristicUUID = UUID.fromString("xxx");
    private BluetoothGattCharacteristic mWriteCharacteristic;
    private BluetoothInterface mBluetoothInterface;
    private Handler mHandler;
    private BluetoothAdapter mBluetoothAdapter;
    private boolean mScanning;
    private BluetoothLeScanner mBLEScanner;
    private ScanSettings mScanSettings;
    private ScanCallback mScanCallback;
    private Context mContext;
    private String mCurrentMac;

    //step1-初始化工具类
    public void init(Activity context) {
        mContext = context;
        mHandler = new Handler();
        mBluetoothManager = (BluetoothManager) mContext.getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothGattCallback = new MyBluetoothGattCallback();
        mBluetoothAdapter = mBluetoothManager.getAdapter();

        //创建ScanSettings的build对象用于设置参数
        ScanSettings.Builder builder = new ScanSettings.Builder()
                //设置高功耗模式
                .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY);
        //android 6.0添加设置回调类型、匹配模式等
        if (android.os.Build.VERSION.SDK_INT >= 23) {
            //定义回调类型
            builder.setCallbackType(ScanSettings.CALLBACK_TYPE_ALL_MATCHES);
            //设置蓝牙LE扫描滤波器硬件匹配的匹配模式
            builder.setMatchMode(ScanSettings.MATCH_MODE_STICKY);
        }
        mScanSettings = builder.build();
        mScanCallback = new ScanCallback() {
            //当一个蓝牙ble广播被发现时回调
            @Override
            public void onScanResult(int callbackType, ScanResult result) {
                super.onScanResult(callbackType, result);

                BluetoothDevice device = result.getDevice();
               
                Log.e(TAG, "onScanResult: " + device.getName());
                if (device.getName() != null && device.getName().contains("设备蓝牙名字")) {
                    String address = result.getDevice().getAddress();
                    if (address.equals(mCurrentMac)) {
                        Log.e(TAG, "找到: " + result.getDevice().getName());
                        // 停止扫描
                        stop();
                        BluetoothDevice mBluetoothDevice = mBluetoothManager.getAdapter().getRemoteDevice(address);
                        //连接
                        mBluetoothGatt = mBluetoothDevice.connectGatt(mContext, false, mBluetoothGattCallback);
                    }
                }
            }

            //批量返回扫描结果
            //@param results 以前扫描到的扫描结果列表。
            @Override
            public void onBatchScanResults(List<ScanResult> results) {
                super.onBatchScanResults(results);

            }

            //当扫描不能开启时回调
            @Override
            public void onScanFailed(int errorCode) {
                super.onScanFailed(errorCode);
                //扫描太频繁会返回ScanCallback.SCAN_FAILED_APPLICATION_REGISTRATION_FAILED，表示app无法注册，无法开始扫描。

            }
        };
    }

    //step2-判断蓝牙是否可用
    public boolean bluetoothIsEnable() {
        return mBluetoothAdapter != null && mBluetoothAdapter.isEnabled();
    }

    public void stop() {
        if (mBLEScanner != null) {
            mScanning = false;
            mBLEScanner.stopScan(mScanCallback);
        }
    }

    
    private void scanLeDevice() {
        if (!bluetoothIsEnable()) {
            return;
        }
        //处于未扫描的状态
        if (!mScanning) {
            //android 5.0后
            //标记当前的为扫描状态
            mScanning = true;
            //获取5.0新添的扫描类
            if (mBLEScanner == null) {
                //mBLEScanner是5.0新添加的扫描类，通过BluetoothAdapter实例获取。
                mBLEScanner = mBluetoothAdapter.getBluetoothLeScanner();
            }
            //开始扫描
            mBLEScanner.startScan(null, mScanSettings, mScanCallback);
        }
    }

    //step3-扫描周围可用蓝牙设备后连接目标设备
    public void connect(String bleMac, BluetoothInterface listener) {
        if (mBluetoothManager != null) {
            mBluetoothInterface = listener;
            // 根据mac地址得到蓝牙设备对象
            mCurrentMac = strToMac(bleMac);
            scanLeDevice();
        }
    }

    public boolean discoverServices() {
        return mBluetoothGatt.discoverServices();
    }

    private String strToMac(String mac) {
        List<String> list = new ArrayList<>();
        int p = 0;
        while (p < (mac.length() - 1)) {
            list.add(mac.substring(p, p + 2));
            list.add(":");
            p = p + 2;
        }
        StringBuilder str = new StringBuilder();
        for (int i = 0; i < list.size() - 1; i++) {
            str.append(list.get(i));
        }
        return str.toString();
    }

    //step-8 向设备发送数据（例如开锁指令）
    public void openDoor(List<String> password) {
        //开锁指令
        String lockOpen1 = password.get(0);
        mWriteCharacteristic.setValue(getHexBytes(lockOpen1));
        boolean result1 = mBluetoothGatt.writeCharacteristic(mWriteCharacteristic);
        Log.e(TAG, "发出一号指令: " + result1);

        mHandler.postDelayed(() -> {
            String lockOpen2 = password.get(1);
            mWriteCharacteristic.setValue(getHexBytes(lockOpen2));
            boolean result2 = mBluetoothGatt.writeCharacteristic(mWriteCharacteristic);
            Log.e(TAG, "发出二号指令: " + result2);
        }, 100);
    }

    //定义蓝牙Gatt回调类
    public class MyBluetoothGattCallback extends BluetoothGattCallback {
        //连接状态回调
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            super.onConnectionStateChange(gatt, status, newState);
            // status 用于返回操作是否成功,会返回异常码。
            // newState 返回连接状态，如BluetoothProfile#STATE_DISCONNECTED、BluetoothProfile#STATE_CONNECTED

            Log.e(TAG, "onConnectionStateChange: " + status + "-" + newState);
            //操作成功的情况下
            if (status == BluetoothGatt.GATT_SUCCESS) {
                //判断是否连接码
                if (newState == BluetoothProfile.STATE_CONNECTED) {
                    Log.e(TAG, "蓝牙连接成功 ");
                    // step4-发现服务
                    boolean success = discoverServices();
                    Log.e(TAG, "discoverServices: " + success);
                } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                    //判断是否断开连接码
                    Log.e(TAG, "断开连接 ");
                }
            } else {
                //异常码
                Log.e(TAG, "onConnectionStateChange: " + "连接异常:" + status);
                mBluetoothGatt.close();
            }
        }

        //服务发现回调
        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            super.onServicesDiscovered(gatt, status);
            Log.e(TAG, "onServicesDiscovered: " + status);
            if (status == BluetoothGatt.GATT_SUCCESS) {
                //step5-获取指定uuid的service
                BluetoothGattService gattService = mBluetoothGatt.getService(mServiceUUID);
                //获取到特定的服务不为空
                if (gattService != null) {
                    Log.e(TAG, "获取服务成功！ ");
                    //获取指定uuid的Characteristic
                    BluetoothGattCharacteristic gattCharacteristic = gattService.getCharacteristic(mNotifyCharacteristicUUID);
                    if (mBluetoothGatt.setCharacteristicNotification(gattCharacteristic, true)) {
                        BluetoothGattDescriptor descriptor = gattCharacteristic.getDescriptor(mDescriptorUUID);
                        if (descriptor != null) {
                            //step6-设置通知值(相当于监听设备的某个值)
                            descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                            boolean descriptorResult = mBluetoothGatt.writeDescriptor(descriptor);
                            Log.e(TAG, "writeDescriptorResult: " + descriptorResult);
                        }
                    }
                    mWriteCharacteristic = gattService.getCharacteristic(mWriteCharacteristicUUID);
                } else {
                    //获取特定服务失败
                    Log.e(TAG, "获取服务失败！ ");
                }
            }
        }

        //特征写入回调
        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicWrite(gatt, characteristic, status);
            if (status == BluetoothGatt.GATT_SUCCESS) {
                Log.e(TAG, "特征写入回调: " + bytes2HexString(characteristic.getValue()));
            }
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicRead(gatt, characteristic, status);
            if (status == BluetoothGatt.GATT_SUCCESS) {
                //获取读取到的特征值
                Log.e(TAG, "读取结果: " + bytes2HexString(characteristic.getValue()));
            }

        }

        //外设特征值改变回调
        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            super.onCharacteristicChanged(gatt, characteristic);
            //step-9 获取外设修改的特征值（指令发送后返回的回调，判断是否成功）
            byte[] value = characteristic.getValue();
            String result = bytes2HexString(value);
            Log.e(TAG, "外设特征值改变回调: " + result);
            mBluetoothInterface.onControlResult(result);
        }

        //描述写入回调
        @Override
        public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
            super.onDescriptorWrite(gatt, descriptor, status);
            if (status == BluetoothGatt.GATT_SUCCESS) {
                //step-7 开启监听成功，可以像设备写入命令了
                Log.e(TAG, "开启监听成功");
                mBluetoothInterface.onReadyToControl();
            }
        }
    }

    public interface BluetoothInterface {
        void onReadyToControl();

        void onControlResult(String result);
    }

    // byte转十六进制字符串
    public String bytes2HexString(byte[] bytes) {
        String ret = "";
        for (byte item : bytes) {
            String hex = Integer.toHexString(item & 0xFF);
            if (hex.length() == 1) {
                hex = '0' + hex;
            }
            ret += hex.toUpperCase(Locale.CHINA);
        }
        return ret;
    }

    // 将16进制的字符串转换为字节数组
    public byte[] getHexBytes(String message) {
        int len = message.length() / 2;
        char[] chars = message.toCharArray();
        String[] hexStr = new String[len];
        byte[] bytes = new byte[len];
        for (int i = 0, j = 0; j < len; i += 2, j++) {
            hexStr[j] = "" + chars[i] + chars[i + 1];
            bytes[j] = (byte) Integer.parseInt(hexStr[j], 16);
        }
        return bytes;
    }

}

package com.example.aqi.iotapp;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import java.util.List;
import java.util.UUID;

public class BluetoothLeService extends Service {

    private final static String TAG = BluetoothLeService.class.getSimpleName();

    private BluetoothManager mBluetoothManager;

    private BluetoothAdapter mBluetoothAdapter;

    private String mBluetoothDeviceAddress;

    private BluetoothGatt mBluetoothGatt;

    private int mConnectionState = STATE_DISCONNECTED;

    private static final int STATE_DISCONNECTED = 0;
    private static final int STATE_CONNECTING = 1;
    private static final int STATE_CONNECTED = 2;

    public final static String ACTION_GATT_CONNECTED =
            "com.example.bluetooth.le.ACTION_GATT_CONNECTED";
    public final static String ACTION_GATT_DISCONNECTED =
            "com.example.bluetooth.le.ACTION_GATT_DISCONNECTED";
    public final static String ACTION_GATT_SERVICES_DISCOVERED =
            "com.example.bluetooth.le.ACTION_GATT_SERVICES_DISCOVERED";
    public final static String ACTION_DATA_AVAILABLE =
            "com.example.bluetooth.le.ACTION_DATA_AVAILABLE";
    public final static String EXTRA_DATA =
            "com.example.bluetooth.le.EXTRA_DATA";
    public final static UUID UUID_HM_RX_TX =
            UUID.fromString(SampleGattAttributes.HM_RX_TX);

    // Implements callback methods for GATT events that the app cares about. For
    // example, connection change and services discovered.
    private final BluetoothGattCallback mGattCallback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            String intentAction;
            if(newState == BluetoothProfile.STATE_CONNECTED)
            {
                intentAction = ACTION_GATT_CONNECTED;
                mConnectionState = STATE_CONNECTED;
                broadcastUpdate(intentAction);
                Log.i(TAG, "Connected to GATT server.");
                // attempts to discover services after successful connection.
                Log.i(TAG, "Attempting to start service discovery:" +
                    mBluetoothGatt.discoverServices());
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED)
            {
                intentAction = ACTION_GATT_DISCONNECTED;
                mConnectionState = STATE_DISCONNECTED;
                Log.i(TAG, "Disconnected from GATT server.");
                broadcastUpdate(intentAction);
            }
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status)
        {
            if( status == BluetoothGatt.GATT_SUCCESS)
            {
                broadcastUpdate(ACTION_GATT_SERVICES_DISCOVERED);
            } else{
                Log.w(TAG, "onServicesDiscovered received: " + status);
            }
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt,
                                         BluetoothGattCharacteristic characteristic,
                                         int status){
            if(status == BluetoothGatt.GATT_SUCCESS){
                broadcastUpdate(ACTION_DATA_AVAILABLE, characteristic);
            }
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt,
                                            BluetoothGattCharacteristic characteristic)
        {
            broadcastUpdate(ACTION_DATA_AVAILABLE, characteristic);
        }
    };

    private void broadcastUpdate(final String action)
    {
        final Intent intent = new Intent(action);
        sendBroadcast(intent);
    }

    private void broadcastUpdate(final String action, final BluetoothGattCharacteristic characteristic)
    {
        final Intent intent = new Intent(action);

        final byte[] data = characteristic.getValue();
        Log.i(TAG, "data" + characteristic.getValue());

        if(data !=null && data.length>0){
            final StringBuilder stringBuilder = new StringBuilder(data.length);
            for(byte byteChar: data)
            {
                stringBuilder.append(String.format("%02X", byteChar));
                Log.d(TAG, String.format("%s", new String(data)));

                intent.putExtra(EXTRA_DATA, String.format("%s", new String(data)));
            }
        }

        sendBroadcast(intent);
    }

    public class LocalBinder extends Binder {
        BluetoothLeService getService(){
            return BluetoothLeService.this;
        }
    }

    private final IBinder mBinder = new LocalBinder();

    public BluetoothLeService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public boolean onUnbind(Intent intent)
    {
        // After using a given device, call BluetoothGatt.close()
        // to make sure resources are cleaned up properly
        close();
        return super.onUnbind(intent);
    }

    /**
     * Initializes a reference to the local Bluetooth adapter
     * @return Return true if the initialization is successful
     */
    public boolean initialize(){
        //For API level 18 and above, get a reference to BluetooothAdapter
        // through BluetoothManager
        if(mBluetoothManager == null)
        {
            mBluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
            if( mBluetoothManager == null)
            {
                Log.e(TAG, "Unable to initialize BluetoothManager.");
                return false;
            }
        }

        mBluetoothAdapter = mBluetoothManager.getAdapter();
        if(mBluetoothAdapter == null)
        {
            Log.e(TAG, "Unable to obtain a BluetoothAdapter.");
            return false;
        }

        return true;
    }

    /**
     * Connects to the GATT server hosted on the Bluetooth LE device
     *
     * @param address The device address of the dstination device
     * @return Return true if the connection is initiated successfully.
     *         The connection result is reported asynchronously through
     *         the {@code BluetoothGattCallback#onConnectStateChange(android.bluetooth.BluetoothGatt, int, int)}
     *         callback
     */
    public boolean connect(final String address)
    {
        if(mBluetoothAdapter == null || address == null)
        {
            Log.w(TAG, "BluetoothAdapter not initialized or unspecified address.");
            return false;
        }

        //Previously connected device. Try to reconnect.
        if(mBluetoothDeviceAddress != null && address.equals(mBluetoothDeviceAddress)
                && mBluetoothGatt != null)
        {
            Log.d(TAG, "Trying to use an existing mBluetoothGatt for connection.");
            if(mBluetoothGatt.connect()){
                mConnectionState = STATE_CONNECTING;
                return true;
            } else{
                final BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
                mBluetoothGatt = device.connectGatt(this, false, mGattCallback);
                mBluetoothDeviceAddress = address;
                return false;
            }
        }

        final BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
        if(device == null)
        {
            Log.w(TAG, "DEVICE not found. Unable to connect.");
            return false;
        }

        //We want to directly connect to the device, so we are setting the autoConnect
        // parameter to false
        mBluetoothGatt = device.connectGatt(this, false, mGattCallback);
        Log.d(TAG, "Trying to create a new connection.");
        mBluetoothDeviceAddress = address;
        mConnectionState = STATE_CONNECTING;
        return true;
    }

    /**
     * Disconnects an existing connection or cancel a pending connection.
     * The disconenction result is reported asynchronously through the
     * {@code BluetoothGattCallback#onConnectionStateChange(android.bluetooth.BluetoothGatt, int, int)}
     * callback
     */
    public void disconnect(){
        if(mBluetoothAdapter == null || mBluetoothGatt == null)
        {
            Log.w(TAG, "BluetoothAdapter not initialized");
            return;
        }
        mBluetoothGatt.disconnect();
    }

    /**
     * After using a given BLE device, the app must call this method
     * to ensure resources are released properly
     */
    public void close(){
        if(mBluetoothGatt == null)
        {
            return;
        }
        mBluetoothGatt.close();
        mBluetoothGatt = null;
    }

    /**
     * Write to a given char
     *
     * @param characteristic The characteristic to write to
     */
    public void writeCharacteristic(BluetoothGattCharacteristic characteristic){
        if(mBluetoothAdapter == null || mBluetoothGatt == null){
            Log.w(TAG, "BluetoothAdapter not intialized");
            return;
        }
        mBluetoothGatt.writeCharacteristic(characteristic);
    }

    /**
     * Enables or disables notification on a given characteristic.
     *
     * @param characteristic Characteristic to act on
     * @param enabled If true, enable notification. False otherwise.
     */
    public void setCharacteristicNotification(BluetoothGattCharacteristic characteristic,
                                              boolean enabled){
        if(mBluetoothAdapter == null || mBluetoothGatt == null)
        {
            Log.w(TAG, "BluetoothAdapter not initialized");
            return;
        }
        mBluetoothGatt.setCharacteristicNotification(characteristic, enabled);

    }

    /**
     * Retrieves a list of supported GATT services on the connected device.
     * This should be invoked only after {@code BluetoothGattDiscoverServices()}
     * completes succesfully
     *
     * @return A {@code List} of supported services.
     */
    public List<BluetoothGattService> getSupportedGattServices(){
        if(mBluetoothGatt == null) return null;
        return mBluetoothGatt.getServices();
    }

}

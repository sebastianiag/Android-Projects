package bleu.hyms.com.bleu;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

import static bleu.hyms.com.bleu.Contants.DEVICE_NAME;
import static bleu.hyms.com.bleu.Contants.MESSAGE_DEVICE_NAME;
import static bleu.hyms.com.bleu.Contants.MESSAGE_READ;
import static bleu.hyms.com.bleu.Contants.MESSAGE_TOAST;
import static bleu.hyms.com.bleu.Contants.MESSAGE_WRITE;
import static bleu.hyms.com.bleu.Contants.TOAST;
/**
 * Created by nephilim on 2/12/15.
 */
public class HealthFetchService {
    public static final int STATE_NONE = 0;
    public static final int STATE_CONNECTING = 1;
    public static final int STATE_CONNECTED = 2;
    public static final int STATE_LISTEN = 3;


    private static final UUID MY_UUID_SECURE =
            UUID.fromString("fa87c0d0-afac-11de-8a39-0800200c9a66");
    private final BluetoothAdapter mAdapter;
    private final Handler mHandler;
    private ConnectThread mConnectThread;
    private ConnectedThread mConnectedThread;
    public int mState;

    public HealthFetchService(Context context, Handler handler) {
        mAdapter = BluetoothAdapter.getDefaultAdapter();
        mHandler = handler;
        mState = STATE_NONE;
    }

    private synchronized void setState(int state) {

        mState = state;
        // Give the new state to the Handler so the UI Activity can update
        mHandler.obtainMessage(connectActivity.STATE_CHANGE, state, -1).sendToTarget();
    }

    public synchronized void connect(BluetoothDevice device) {

        // Cancel any thread attempting to make a connection
        if (mState == STATE_CONNECTING) {
            if (mConnectThread != null) {mConnectThread.cancel(); mConnectThread = null;}
        }
        // Cancel any thread currently running a connection
        if (mConnectedThread != null) {mConnectedThread.cancel(); mConnectedThread = null;}
        // Start the thread to connect with the given device
        mConnectThread = new ConnectThread(device);
        mConnectThread.start();
        setState(STATE_CONNECTING);
    }

    public synchronized  void connected(BluetoothSocket socket, BluetoothDevice device){
        if (mConnectThread != null) {
            mConnectThread.cancel();
            mConnectThread = null;
        }

        // Cancel any thread currently running a connection
        if (mConnectedThread != null) {
            mConnectedThread.cancel();
            mConnectedThread = null;
        }

        mConnectedThread = new ConnectedThread(socket);
        mConnectThread.start();


        // Send the name of the connected device back to the UI Activity
        Message msg = mHandler.obtainMessage(MESSAGE_DEVICE_NAME);
        Bundle bundle = new Bundle();
        bundle.putString(DEVICE_NAME, device.getName());
        msg.setData(bundle);
        mHandler.sendMessage(msg);
    }

    private class ConnectThread extends Thread{
        private final BluetoothSocket mSocket;
        private final BluetoothDevice mDevice;

        public ConnectThread(BluetoothDevice device){
            mDevice = device;
            BluetoothSocket tmp = null;

            try {
                tmp = device.createRfcommSocketToServiceRecord(MY_UUID_SECURE);
            } catch (IOException e) {
                Log.e("error", "creating sockets failed");
            }

            mSocket = tmp;
        }

        public void run(){
            mAdapter.cancelDiscovery();
            try {
                mSocket.connect();
            } catch (IOException e) {
                try {
                    mSocket.close();
                } catch (IOException e1) {
                    Log.e("error", "could not close socket");
                }
                Log.e("error", "could not open socket");
                connectionFailed();
                return;
            }
            synchronized (HealthFetchService.this){
                mConnectThread = null;
            }
            connected(mSocket, mDevice);
        }

        public void cancel(){
            try {
                mSocket.close();
            } catch (IOException e) {
                Log.e("error", "could not close socket");
            }
        }
    }

    private class ConnectedThread extends Thread{
        private final BluetoothSocket mmSocket;
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;

        public ConnectedThread(BluetoothSocket socket){
            mmSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            try {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            }catch(IOException e){
                Log.e("error", "unable to get input/output stream");
            }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }

        public void run(){

            byte[] buffer = new byte[1024];
            int bytes;

            // Keep listening to the InputStream while connected
            while (true) {
                try {
                    // Read from the InputStream
                    bytes = mmInStream.read(buffer);

                    // Send the obtained bytes to the UI Activity
                    mHandler.obtainMessage(MESSAGE_READ, bytes, -1, buffer)
                            .sendToTarget();
                } catch (IOException e) {
                    break;
                }
            }
        }

        public void cancel(){
            try {
                mmSocket.close();
            } catch (IOException e) {
                Log.e("error", "close() of connect socket failed", e);
            }
        }

        public void write(byte[] buffer) {
            try {
                mmOutStream.write(buffer);

                // Share the sent message back to the UI Activity
                mHandler.obtainMessage(MESSAGE_WRITE, -1, -1, buffer)
                        .sendToTarget();
            } catch (IOException e) {
                Log.e("error", "Exception during write", e);
            }
        }
    }

    private void connectionFailed() {
        // Send a failure message back to the Activity
        Message msg = mHandler.obtainMessage(MESSAGE_TOAST);
        Bundle bundle = new Bundle();
        bundle.putString(TOAST, "Device connection was lost");
        msg.setData(bundle);
        mHandler.sendMessage(msg);

        // Say connection was lost or something
    }
}

package bleu.hyms.com.bleu;

import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;


public class MainActivity extends Activity {

    private int REQUEST_ENABLE_BT = 1;
    private int PAIRED_DEVICES_SIZE;
    BluetoothAdapter mBluetoothAdapter = null;
    String NAME = "HELLO";
    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");

    ArrayAdapter<String> discoveryAdapter, pairedAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Scan button settings
        Button scanButton = (Button) findViewById(R.id.scanButton);
        scanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                discoverDevices();
            }
        });

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        // This alert dialog is for displaying errors or if device does not support bluetooth
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        if (mBluetoothAdapter == null) {
            builder.setMessage(R.string.BluetoothError).setTitle(R.string.BluetoothErrorTitle);
            AlertDialog dialog = builder.create();

            try {
                Thread.sleep(5);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }

            Intent close = new Intent(Intent.ACTION_MAIN);
            close.addCategory(Intent.CATEGORY_HOME);
            close.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(close);
        }

        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableBluetooth = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBluetooth, REQUEST_ENABLE_BT);
        } else {
            getPairs();
            if (!(PAIRED_DEVICES_SIZE > 0)) {
                discoverDevices();
            }

            Button sendButton =  (Button) findViewById(R.id.scanButton);
            sendButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    discoverDevices();
                }
            });
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mReceiver);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_CANCELED) {
            finish();
        }
        Log.d("Result", Integer.toString(resultCode));
        Log.d("RESULT_OK", Integer.toString(RESULT_OK));
        if (resultCode == RESULT_OK) {
            getPairs();
            if (!(PAIRED_DEVICES_SIZE > 0)) {
                discoverDevices();
            }

            Button scanButton = (Button) findViewById(R.id.scanButton);
            scanButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    discoverDevices();
                }
            });
        }
    }

    private void getPairs(){
        ArrayList<String> list2 = new ArrayList<String>();
        pairedAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, list2);  // show discovered devices
        ListView pairedList = (ListView) findViewById(R.id.PairedDevices);
        pairedList.setAdapter(pairedAdapter);
        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();


        if (pairedDevices.size() > 0) {
            for (BluetoothDevice device : pairedDevices) {
                pairedAdapter.add(device.getName() + '\n' + device.getAddress());
                pairedAdapter.notifyDataSetChanged();
            }
        }
        pairedList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
               Log.d("Action", "Getting data");
               String deviceName = parent.getAdapter().getItem(position).toString().split("\n")[0];
               String deviceAddress = parent.getAdapter().getItem(position).toString().split("\n")[1];
               // request connection using these two parameters
               Intent startConnection = new Intent(getBaseContext(), connectActivity.class);
               startConnection.putExtra("DEVICE_NAME", deviceName);
               startConnection.putExtra("DEVICE_ADDRESS", deviceAddress);
               startActivity(startConnection);
            }
        });
        PAIRED_DEVICES_SIZE = pairedDevices.size();
    }

    private void discoverDevices(){
        Log.d("Action", "Scanning...");
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(mReceiver, filter);  //Initialize broadcast receiver for device discovery
        ArrayList<String> list1 = new ArrayList<String>();


        discoveryAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, list1);  // show paired devices




        ListView discoveryList = (ListView) findViewById(R.id.DiscoveredDevices);
        discoveryList.setAdapter(discoveryAdapter);

        discoveryList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Log.d("Action", "Getting data");
                String deviceName = parent.getAdapter().getItem(position).toString().split("\n")[0];
                String deviceAddress = parent.getAdapter().getItem(position).toString().split("\n")[1];
                // request connection using these two parameters
                Intent startConnection = new Intent(getBaseContext(), connectActivity.class);
                startConnection.putExtra("DEVICE_NAME", deviceName);
                startConnection.putExtra("DEVICE_ADDRESS", deviceAddress);
            }
        });

        mBluetoothAdapter.startDiscovery();

    }

    private void connect(String deviceName, String deviceAddress) {


    }


    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if(BluetoothDevice.ACTION_FOUND.equals(action)){
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if(device.getBondState() != BluetoothDevice.BOND_BONDED) {
                    discoveryAdapter.add(device.getName() + '\n' + device.getAddress());
                    discoveryAdapter.notifyDataSetChanged();
                }
            }
        }
    };

/***
    //Bluetooth Server connection
    private class AcceptThread extends Thread {
        private final BluetoothServerSocket mBServerSocket;

        public AcceptThread(){
            BluetoothServerSocket tmp = null;
            try{
                tmp = mBluetoothAdapter.listenUsingRfcommWithServiceRecord(NAME, MY_UUID);
            }catch (IOException e){ }

            mBServerSocket = tmp;
        }

        public void run(){
            BluetoothSocket mBSocket = null;
            while(true){
                try{
                    mBSocket = mBServerSocket.accept();
                }catch (IOException e) { break;}

                if(mBSocket != null){
                    manageConnectedSocket(mBSocket);
                    try {
                        mBServerSocket.close();
                    } catch (IOException e){break;}
                    break;
                }
            }
        }

        public void cancel(){
            try{
                mBServerSocket.close();
            }catch(IOException e){}
        }
    }

    //Bluetooth Client connections
    private class ConnectThread extends Thread{
        private final BluetoothSocket mSocket;
        private final InputStream inStream;
        private final OutputStream outputStream;

        public ConnectThread(BluetoothSocket socket ) {
            mSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            try {
                tmpIn = mSocket.getInputStream();
                tmpOut = mSocket.getOutputStream();
            } catch (IOException e) {}

            inStream = tmpIn;
            outputStream = tmpOut;
        }

        public void run() {
            byte[] buffer = new byte[1024];
            int bytes;

            while(true){
                try{
                    bytes = inStream.read(buffer);
                    mHandler.obtainMessage(MESSAGE_READ, bytes, -1, buffer).sendToTarget();
                }catch (IOException e){
                    break;
                }
            }
        }

        public void write(byte[] bytes){
            try{
                outputStream.write(bytes);
            }catch(IOException e) {}
        }

        public void cancel(){
            try{
                mSocket.close();
            }catch (IOException e){}
        }
    }
 **/
}



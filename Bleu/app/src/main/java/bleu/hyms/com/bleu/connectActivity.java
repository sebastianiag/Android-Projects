package bleu.hyms.com.bleu;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.widget.Toast;


public class connectActivity extends Activity {
    public static final int READ_DATA = 1;
    public static final int STATE_CHANGE = 2;
    public static final int DEVICE_NAME = 3;
    private String mConnectedDeviceName = null;
    String device_name, device_address;

    // Intent request codes
    private static final int REQUEST_CONNECT_DEVICE = 1;
    //Might need to get the adapter from the previous activity
    private BluetoothAdapter mBluetoothAdapter;
    HealthFetchService mHealthFetchService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connect);
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        Intent data = getIntent();
        device_name = data.getStringExtra("DEVICE_NAME");
        device_address = data.getStringExtra("DEVICE_ADDRESS");
        mHealthFetchService = new HealthFetchService(this, mHandler);

    }

    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg){
            switch(msg.what){
                case STATE_CHANGE:
                    switch(msg.arg1){
                        case HealthFetchService.STATE_NONE:
                            //do something about not being connected right now
                            break;
                        case HealthFetchService.STATE_CONNECTED:
                            break;
                        case HealthFetchService.STATE_CONNECTING:
                            //say you are connecting, are you really that stupid plz kill urself
                            break;
                        case HealthFetchService.STATE_LISTEN:
                            //Do I even need to say? ugh
                            break;
                    }

                case DEVICE_NAME:
                    // save the connected device's name
                    mConnectedDeviceName = msg.getData().getString("device_name");
                    Toast.makeText(getApplicationContext(), "Connected to "
                            + mConnectedDeviceName, Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CONNECT_DEVICE:
                // When DeviceListActivity returns with a device to connect
                if (resultCode == Activity.RESULT_OK) {
                    // Get the device MAC address

                    // Get the BLuetoothDevice object
                    BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(device_address);
                    // Attempt to connect to the device
                    mHealthFetchService.connect(device);
                }
                break;
        }
    }
}

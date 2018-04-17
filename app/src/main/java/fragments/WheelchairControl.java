package fragments;


import android.annotation.SuppressLint;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.media.Image;
import android.media.MediaMetadata;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.kesar.swayam.MainActivity;
import com.example.kesar.swayam.R;
import com.example.kesar.swayam.StartActivity;

import java.io.IOException;
import java.util.UUID;

/**
 * A simple {@link Fragment} subclass.
 */
public class WheelchairControl extends Fragment {

    LinearLayout linearLayout;
    private ImageButton up, down, forward, backward, stop, power_on, power_off;
    private Button power_on_again, power_off_again;

    private ProgressDialog progress;

    private View mMainView;

    private String address = null;
    BluetoothAdapter myBluetooth = null;
    BluetoothSocket btSocket = null;
    private boolean isBtConnected = false;

    static final UUID myUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    public WheelchairControl() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        //address = getArguments().getString("address");
        mMainView = inflater.inflate(R.layout.fragment_wheelchair_control, container, false);

        address = ((MainActivity)getActivity()).getResult();
        Log.d("Address", address);

        up = (ImageButton) mMainView.findViewById(R.id.main_up);
        down = (ImageButton) mMainView.findViewById(R.id.main_downward);
        forward = (ImageButton) mMainView.findViewById(R.id.main_forward);
        backward = (ImageButton) mMainView.findViewById(R.id.main_backward);
        stop = (ImageButton) mMainView.findViewById(R.id.main_stop);
        power_on = (ImageButton) mMainView.findViewById(R.id.main_power_on);
        power_off = (ImageButton) mMainView.findViewById(R.id.main_power_off);

        power_on_again = (Button) mMainView.findViewById(R.id.main_power_on_again);
        power_off_again = (Button) mMainView.findViewById(R.id.main_power_off_again);

        new ConnectBT().execute();

        linearLayout = (LinearLayout) mMainView.findViewById(R.id.main_layout);

        up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                moveUp();
                //Snackbar.make(linearLayout, "Wheelchair will move in Upward Direction", Snackbar.LENGTH_LONG).show();

            }
        });

        down.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                moveDown();
                //Snackbar.make(linearLayout, "Wheelchair will move in Downward Direction", Snackbar.LENGTH_LONG).show();

            }
        });

        forward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                moveForward();
                //Snackbar.make(linearLayout, "Wheelchair will move in Forward Direction", Snackbar.LENGTH_LONG).show();

            }
        });

        backward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                moveBackward();
                //Snackbar.make(linearLayout, "Wheelchair will move in Backward Direction", Snackbar.LENGTH_LONG).show();

            }
        });

        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                moveStop();
                //Snackbar.make(linearLayout, "Wheelchair will Stop moving", Snackbar.LENGTH_LONG).show();

            }
        });

        power_on.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Snackbar.make(linearLayout, "Power is ON", Snackbar.LENGTH_LONG).show();

            }
        });

        power_off.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Snackbar.make(linearLayout, "Power is OFF", Snackbar.LENGTH_LONG).show();

            }
        });

        power_on_again.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Snackbar.make(linearLayout, "Power is ON", Snackbar.LENGTH_LONG).show();

            }
        });

        power_off_again.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Snackbar.make(linearLayout, "Power is OFF", Snackbar.LENGTH_LONG).show();

            }
        });

        return mMainView;
    }

    private void moveUp() {
        if (btSocket!=null) {
            try {
                btSocket.getOutputStream().write("0".toString().getBytes());
            }
            catch (IOException e) {
                msg("Error");
            }
        }
    }

    private void moveBackward() {
        if (btSocket!=null) {
            try {
                btSocket.getOutputStream().write("1".toString().getBytes());
                System.out.println("Left");
            }
            catch (IOException e) {
                msg("Error");
            }
        }
    }

    private void moveForward() {
        if (btSocket!=null) {
            try {
                btSocket.getOutputStream().write("2".toString().getBytes());
                System.out.println("Right");
            }
            catch (IOException e) {
                msg("Error");
            }
        }
    }

    private void moveDown() {
        if (btSocket!=null) {
            try {
                btSocket.getOutputStream().write("3".toString().getBytes());
                System.out.println("Back");
            }
            catch (IOException e) {
                msg("Error");
            }
        }
    }

    private void msg(String s)
    {
        Toast.makeText(mMainView.getContext(),s,Toast.LENGTH_LONG).show();
    }

    private void Disconnect() {
        if (btSocket!=null) {
            try {
                btSocket.close(); //close connection
            }
            catch (IOException e) {
                msg("Error");
            }
        }
        getActivity().finish();
    }

    public void moveStop() {
        if (btSocket!=null) {
            try {
                btSocket.getOutputStream().write("5".toString().getBytes());
            }
            catch (IOException e) {
                msg("Error");
            }
        }
    }

    @SuppressLint("StaticFieldLeak")
    private class ConnectBT extends AsyncTask<Void, Void, Void>  // UI thread
    {
        private boolean ConnectSuccess = true; //if it's here, it's almost connected

        @Override
        protected void onPreExecute()
        {
            progress = ProgressDialog.show(getContext(), getContext().getString(R.string.connecting), getContext().getString(R.string.please_wait));  //show a progress dialog
        }

        @Override
        protected Void doInBackground(Void... devices) //while the progress dialog is shown, the connection is done in background
        {
            try
            {
                if (btSocket == null || !isBtConnected)
                {
                    myBluetooth = BluetoothAdapter.getDefaultAdapter();//get the mobile bluetooth device
                    BluetoothDevice dispositivo = myBluetooth.getRemoteDevice(address);//connects to the device's address and checks if it's available
                    btSocket = dispositivo.createInsecureRfcommSocketToServiceRecord(myUUID);//create a RFCOMM (SPP) connection
                    BluetoothAdapter.getDefaultAdapter().cancelDiscovery();
                    btSocket.connect();//start connection
                    System.out.println(dispositivo+"Ankit ------------"+ address);
                }
            }
            catch (IOException e)
            {
                ConnectSuccess = false;//if the try failed, you can check the exception here
            }
            return null;
        }
        @Override
        protected void onPostExecute(Void result) //after the doInBackground, it checks if everything went fine
        {
            super.onPostExecute(result);

            if (!ConnectSuccess)
            {
                msg(getContext().getString(R.string.connection_failed_is_it_a_spp_bluetooth_try_again));
                ((MainActivity)getActivity()).setTAG(0);
                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.container, new UserActivity());
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
            else
            {
                msg(getContext().getString(R.string.connected));
                isBtConnected = true;
            }
            progress.dismiss();
        }
    }

}

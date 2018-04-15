package com.example.kesar.swayam;

import android.*;
import android.Manifest;
import android.app.AlarmManager;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.PendingIntent;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.Image;
import android.net.Uri;
import android.nfc.Tag;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.util.ArrayList;
import java.util.Set;

import dmax.dialog.SpotsDialog;
import fragments.UserActivity;
import fragments.UserProfile;
import fragments.WheelchairControl;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;
import utils.SharedPrefManager;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    DrawerLayout drawer;
    private FirebaseAuth mAuth;
    private DatabaseReference mUserDatabase;

    private FirebaseUser mCurrentUser;
    private Toolbar mToolbar;

    BottomNavigationView bottomNavigationView;

    //Bluetooth
    private BluetoothAdapter myBluetooth = null;
    private Set<BluetoothDevice> pairedDevices;

    ListView pairedDevicesList;

    AlertDialog.Builder dialogPaired;

    String address;

    private int TAG = 0;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/Arkhip_font.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build());

        android.app.FragmentTransaction transaction = getFragmentManager().beginTransaction();
        UserActivity fragment = new UserActivity();
        transaction.replace(R.id.container, fragment);
        transaction.commit();

        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle(R.string.swayam);

        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();

        String current_uid = mCurrentUser.getUid();

        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(current_uid);
        mUserDatabase.keepSynced(true);

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        //if the device has bluetooth
        myBluetooth = BluetoothAdapter.getDefaultAdapter();

        if(myBluetooth == null) {
            //Show a mensag. that the device has no bluetooth adapter
            Toast.makeText(getApplicationContext(), R.string.bluetooth_device_not_available, Toast.LENGTH_LONG).show();
            //finish apk
            finish();
        } else if(!myBluetooth.isEnabled()) {
            //Ask to the user turn the bluetooth on
            Intent turnBTon = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(turnBTon,1);
        }

        ActivityCompat.requestPermissions(MainActivity.this,
                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CALL_PHONE,
                Manifest.permission.READ_PHONE_STATE, Manifest.permission.RECEIVE_SMS, Manifest.permission.CHANGE_CONFIGURATION},
                1);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, mToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottomNavigationMenu);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                switch (id) {
                    case R.id.control:

                        if(!myBluetooth.isEnabled()) {
                            //Ask to the user turn the bluetooth on
                            Intent turnBTon = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                            startActivityForResult(turnBTon,1);
                        } else {
                            if (TAG == 0) {
                                showPairedDevicesDialog();
                            } else {
                                fragmentTransaction.replace(R.id.container, new WheelchairControl());
                                fragmentTransaction.addToBackStack(null);
                                fragmentTransaction.commit();
                                break;
                            }
                        }
                    case R.id.activity:
                        fragmentTransaction.replace(R.id.container, new UserActivity());
                        fragmentTransaction.addToBackStack(null);
                        fragmentTransaction.commit();
                        break;
                    case R.id.profile:
                        fragmentTransaction.replace(R.id.container, new UserProfile());
                        fragmentTransaction.addToBackStack(null);
                        fragmentTransaction.commit();
                        break;
                }
                return true;
            }
        });

        if (TAG == 0) {
            showPairedDevicesDialog();
        }

    }

    @Override
    public void onBackPressed() {

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else if (getFragmentManager().getBackStackEntryCount() > 0) {
            getFragmentManager().popBackStackImmediate();
        } else {
            super.onBackPressed();
        }

    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser == null) {

            sendToStart();

        }

    }

    private void sendToStart() {

        Intent mainIntent = new Intent(MainActivity.this, StartActivity.class);
        startActivity(mainIntent);
        finish();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.main_menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);

        if (item.getItemId() == R.id.main_logout_btn) {

            FirebaseAuth.getInstance().signOut();
            sendToStart();

        }

        if (item.getItemId() == R.id.main_help) {

            showHelpDialog();

        }

        if (item.getItemId() == R.id.main_change_language) {

            showLanguageDialog();

        }

        return true;
    }

    private void showPairedDevicesDialog() {

        dialogPaired = new AlertDialog.Builder(this);
        dialogPaired.setTitle(R.string.paired_devices);

        LayoutInflater inflater = LayoutInflater.from(this);
        View paired_devices_layout = inflater.inflate(R.layout.layout_paired_devices, null);

        dialogPaired.setView(paired_devices_layout);

        pairedDevicesList = (ListView) paired_devices_layout.findViewById(R.id.list_view);
        final Button pairedDeviceButton = (Button) paired_devices_layout.findViewById(R.id.paired_devices);

        pairedDeviceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                pairedDeviceList();

            }
        });

        dialogPaired.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });

        dialogPaired.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });

        dialogPaired.show();

    }

    private void pairedDeviceList() {

        pairedDevices = myBluetooth.getBondedDevices();
        ArrayList list = new ArrayList();

        if (pairedDevices.size() > 0) {
            for(BluetoothDevice bt : pairedDevices) {
                list.add(bt.getName() + "\n" + bt.getAddress()); //Get the device's name and the address
            }
        } else {
            Toast.makeText(getApplicationContext(), R.string.no_paired_bluetooth_devices_found, Toast.LENGTH_LONG).show();
        }

        final ArrayAdapter adapter = new ArrayAdapter(this,android.R.layout.simple_list_item_1, list);
        pairedDevicesList.setAdapter(adapter);
        pairedDevicesList.setOnItemClickListener(myListClickListener); //Method called when the device from the list is clicked

    }

    private AdapterView.OnItemClickListener myListClickListener = new AdapterView.OnItemClickListener()
    {
        public void onItemClick (AdapterView<?> av, View v, int arg2, long arg3)
        {
            // Get the device MAC address, the last 17 chars in the View
            String info = ((TextView) v).getText().toString();
            address = info.substring(info.length() - 17);

            if (!address.equals("")) {
                Log.d("Address is ", address);
                Bundle bundle = new Bundle();
                bundle.putString("address", address);
                TAG = 1;
            }

        }
    };

    public String getResult() {
        return address;
    }

    private void showLanguageDialog() {

        final AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle(R.string.change_language);
        dialog.setMessage(R.string.please_select_one_from_below);

        LayoutInflater inflater = LayoutInflater.from(this);
        View login_layout = inflater.inflate(R.layout.layout_language, null);

        dialog.setView(login_layout);

        final RadioButton radioButtonEnglish = (RadioButton) login_layout.findViewById(R.id.english);
        final RadioButton radioButtonHindi = (RadioButton) login_layout.findViewById(R.id.hindi);

        dialog.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                if (radioButtonEnglish.isChecked()) {

                    new SharedPrefManager(MainActivity.this).setLanguage("en");
                    AlarmManager mgr = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                    mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 100, PendingIntent.getActivity(MainActivity.this, 0, new Intent(MainActivity.this, MainActivity.class), PendingIntent.FLAG_CANCEL_CURRENT));
                    android.os.Process.killProcess(android.os.Process.myPid());
                    return;

                } else if (radioButtonHindi.isChecked()) {

                    new SharedPrefManager(MainActivity.this).setLanguage("hi");
                    AlarmManager mgr = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                    mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 100, PendingIntent.getActivity(MainActivity.this, 0, new Intent(MainActivity.this, MainActivity.class), PendingIntent.FLAG_CANCEL_CURRENT));
                    android.os.Process.killProcess(android.os.Process.myPid());
                    return;

                }
                dialogInterface.dismiss();
            }
        });

        dialog.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });

        dialog.show();

    }

    private void showHelpDialog() {

        final AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle(R.string.emergency);
        dialog.setMessage(R.string.please_select_one_from_below);

        LayoutInflater inflater = LayoutInflater.from(this);
        View login_layout = inflater.inflate(R.layout.layout_help, null);

        final TextView emergencyNumberOne = (TextView) login_layout.findViewById(R.id.emergency_contact_one);
        final TextView emergencyNumberTwo = (TextView) login_layout.findViewById(R.id.emergency_contact_two);


        mUserDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                String emergencyOne = dataSnapshot.child("emergency_contact_one").getValue().toString();
                String emergencyTwo = dataSnapshot.child("emergency_contact_two").getValue().toString();

                emergencyNumberOne.setText(emergencyOne);
                emergencyNumberTwo.setText(emergencyTwo);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        dialog.setView(login_layout);

        final String number_one = emergencyNumberOne.getText().toString();
        final String number_two = emergencyNumberTwo.getText().toString();

        emergencyNumberOne.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(Intent.ACTION_CALL);
                intent.setData(Uri.parse("tel:" + number_one));
                if (ActivityCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    Snackbar.make(drawer, R.string.please_give_permission_to_call, Snackbar.LENGTH_LONG).show();
                    return;
                }
                startActivity(intent);

            }
        });

        emergencyNumberTwo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(Intent.ACTION_CALL);
                intent.setData(Uri.parse("tel:" + number_two));
                if (ActivityCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    Snackbar.make(drawer, R.string.please_give_permission_to_call, Snackbar.LENGTH_LONG).show();
                    return;
                }
                startActivity(intent);

            }
        });

        dialog.setPositiveButton(R.string.call, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                Snackbar.make(drawer, R.string.please_select_a_number_to_call, Snackbar.LENGTH_LONG).show();
                dialogInterface.dismiss();
            }
        });

        dialog.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });

        dialog.show();

    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        switch (id) {

            case R.id.nav_help:
                showHelpDialog();
                break;
            case R.id.nav_change_language:
                showLanguageDialog();
                break;
            case R.id.nav_logout:
                FirebaseAuth.getInstance().signOut();
                sendToStart();
                break;
            case R.id.nav_about_project:
                Intent aboutProjectIntent = new Intent(MainActivity.this, AboutProject.class);
                startActivity(aboutProjectIntent);
                break;
            case R.id.nav_contact_us:
                Intent contactUs = new Intent(MainActivity.this, ContactUs.class);
                startActivity(contactUs);
                break;
            case R.id.nav_gallery:
                Intent gallery = new Intent(MainActivity.this, GalleryView.class);
                startActivity(gallery);
                break;
            case R.id.nav_team:
                Intent team = new Intent(MainActivity.this, Team.class);
                startActivity(team);
                break;

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        switch (requestCode) {
            case 1: {

                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(MainActivity.this, "Permission denied.", Toast.LENGTH_SHORT).show();
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}

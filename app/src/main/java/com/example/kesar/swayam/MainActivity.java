package com.example.kesar.swayam;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.Image;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.rengwuxian.materialedittext.MaterialEditText;

import dmax.dialog.SpotsDialog;
import fragments.UserActivity;
import fragments.UserProfile;
import fragments.WheelchairControl;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    DrawerLayout drawer;
    private FirebaseAuth mAuth;
    private Toolbar mToolbar;

    private ImageButton move_upward;
    private ImageButton move_downward;
    private ImageButton move_backward;
    private ImageButton move_forward;
    private ImageButton stop;

    BottomNavigationView bottomNavigationView;

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
        UserProfile fragment = new UserProfile();
        transaction.replace(R.id.container, fragment);
        transaction.commit();

        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Swayam");


        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, mToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottomNavigationMenu);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id=item.getItemId();
                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction fragmentTransaction=fragmentManager.beginTransaction();
                switch (id){
                    case R.id.control:
                        fragmentTransaction.replace(R.id.container, new WheelchairControl());
                        fragmentTransaction.addToBackStack(null);
                        fragmentTransaction.commit();
                        break;
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

    }

    @Override
    public void onBackPressed() {

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else if(getFragmentManager().getBackStackEntryCount()>0){
            getFragmentManager().popBackStackImmediate();
        } else {
            super.onBackPressed();
        }

    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser currentUser = mAuth.getCurrentUser();

        if(currentUser == null) {

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

        if(item.getItemId() == R.id.main_logout_btn) {

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

    private void showLanguageDialog() {

        final AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("Change Language");
        dialog.setMessage("Please select one from below");

        LayoutInflater inflater = LayoutInflater.from(this);
        View login_layout = inflater.inflate(R.layout.layout_language, null);

        dialog.setView(login_layout);

        dialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                dialogInterface.dismiss();
            }
        });

        dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });

        dialog.show();

    }

    private void showHelpDialog() {

        final AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("Emergency");
        dialog.setMessage("Please select anyone");

        LayoutInflater inflater = LayoutInflater.from(this);
        View login_layout = inflater.inflate(R.layout.layout_help, null);

        dialog.setView(login_layout);

        dialog.setPositiveButton("Call", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                dialogInterface.dismiss();
            }
        });

        dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
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
                Snackbar.make(drawer, "Galley will update soon...", Snackbar.LENGTH_LONG).show();

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}

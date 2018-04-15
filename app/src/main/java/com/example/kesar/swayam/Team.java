package com.example.kesar.swayam;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RadioButton;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;
import utils.SharedPrefManager;

public class Team extends AppCompatActivity {

    private Toolbar mToolbar;

    private CircleImageView ankitImage;
    private CircleImageView kishoreImage;
    private CircleImageView brijeshImage;
    private CircleImageView panditImage;
    private CircleImageView jayvardhanImage;

    private DatabaseReference mTeamDatabase;

    private CardView ankitCardView;
    private CardView brijeshCardView;
    private CardView kishoreCardView;
    private CardView panditCardView;
    private CardView jayvardhanCardView;

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

        setContentView(R.layout.activity_team);

        mToolbar = (Toolbar) findViewById(R.id.team_page_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle(R.string.Team);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ankitImage = (CircleImageView) findViewById(R.id.team_ankit);
        kishoreImage = (CircleImageView) findViewById(R.id.team_kishore);
        brijeshImage = (CircleImageView) findViewById(R.id.team_brijesh);
        panditImage = (CircleImageView) findViewById(R.id.team_pandit);
        jayvardhanImage = (CircleImageView) findViewById(R.id.team_jayvardhan);

        ankitCardView = (CardView) findViewById(R.id.team_ankit_cardview);
        brijeshCardView = (CardView) findViewById(R.id.team_brijesh_cardview);
        kishoreCardView = (CardView) findViewById(R.id.team_kishore_cardview);
        panditCardView = (CardView) findViewById(R.id.team_pandit_cardview);
        jayvardhanCardView = (CardView) findViewById(R.id.team_jayvardhan_cardview);

        mTeamDatabase = FirebaseDatabase.getInstance().getReference("Team");
        mTeamDatabase.keepSynced(true);

        mTeamDatabase.child("Ankit").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                final String image = dataSnapshot.child("image").getValue().toString();
                Picasso.with(Team.this).load(image).networkPolicy(NetworkPolicy.OFFLINE)
                        .placeholder(R.drawable.default_user).into(ankitImage, new Callback() {
                    @Override
                    public void onSuccess() {

                    }

                    @Override
                    public void onError() {
                        Picasso.with(Team.this).load(image).placeholder(R.drawable.default_user).into(ankitImage);
                    }
                });

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mTeamDatabase.child("Brijesh").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                final String image = dataSnapshot.child("image").getValue().toString();
                Picasso.with(Team.this).load(image).networkPolicy(NetworkPolicy.OFFLINE)
                        .placeholder(R.drawable.default_user).into(brijeshImage, new Callback() {
                    @Override
                    public void onSuccess() {

                    }

                    @Override
                    public void onError() {
                        Picasso.with(Team.this).load(image).placeholder(R.drawable.default_user).into(brijeshImage);
                    }
                });

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mTeamDatabase.child("Jayvardhan").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                final String image = dataSnapshot.child("image").getValue().toString();
                Picasso.with(Team.this).load(image).networkPolicy(NetworkPolicy.OFFLINE)
                        .placeholder(R.drawable.default_user).into(jayvardhanImage, new Callback() {
                    @Override
                    public void onSuccess() {

                    }

                    @Override
                    public void onError() {
                        Picasso.with(Team.this).load(image).placeholder(R.drawable.default_user).into(jayvardhanImage);
                    }
                });

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mTeamDatabase.child("Kishore").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                final String image = dataSnapshot.child("image").getValue().toString();
                Picasso.with(Team.this).load(image).networkPolicy(NetworkPolicy.OFFLINE)
                        .placeholder(R.drawable.default_user).into(kishoreImage, new Callback() {
                    @Override
                    public void onSuccess() {

                    }

                    @Override
                    public void onError() {
                        Picasso.with(Team.this).load(image).placeholder(R.drawable.default_user).into(kishoreImage);
                    }
                });

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mTeamDatabase.child("Shivam").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                final String image = dataSnapshot.child("image").getValue().toString();
                Picasso.with(Team.this).load(image).networkPolicy(NetworkPolicy.OFFLINE)
                        .placeholder(R.drawable.default_user).into(panditImage, new Callback() {
                    @Override
                    public void onSuccess() {

                    }

                    @Override
                    public void onError() {
                        Picasso.with(Team.this).load(image).placeholder(R.drawable.default_user).into(panditImage);
                    }
                });

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        ankitCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                showLanguageDialog(getApplicationContext().getString(R.string.ankit_kesarwani), getApplicationContext().getString(R.string.about_ankit));

            }
        });

        brijeshCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                showLanguageDialog(getApplicationContext().getString(R.string.brijesh_sharma), getApplicationContext().getString(R.string.about_ankit));

            }
        });

        kishoreCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                showLanguageDialog(getApplicationContext().getString(R.string.shubham_kishore), getApplicationContext().getString(R.string.about_ankit));

            }
        });

        panditCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                showLanguageDialog(getApplicationContext().getString(R.string.shivam_mishra), getApplicationContext().getString(R.string.about_ankit));

            }
        });

        jayvardhanCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                showLanguageDialog(getApplicationContext().getString(R.string.jayavardhan), getApplicationContext().getString(R.string.about_ankit));

            }
        });

    }

    private void showLanguageDialog(String name, String aboutText) {

        final AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("About "+name);

        LayoutInflater inflater = LayoutInflater.from(this);
        View login_layout = inflater.inflate(R.layout.layout_about_team_member, null);

        dialog.setView(login_layout);
        final TextView about = (TextView) login_layout.findViewById(R.id.about_text);
        about.setText(aboutText);

        dialog.show();
    }
}

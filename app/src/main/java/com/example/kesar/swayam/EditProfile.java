package com.example.kesar.swayam;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import id.zelory.compressor.Compressor;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class EditProfile extends AppCompatActivity {

    private static final int GALLERY_PICK = 1;

    RelativeLayout editProfileLayout;

    private Toolbar mToolbar;

    private CircleImageView mProfileImage;
    private TextInputLayout mEmergencyNumberOne;
    private TextInputLayout mEmergencyNumberTwo;
    private Button mSaveChanges;
    private Button mChangeAvatar;

    private FirebaseAuth mAuth;
    private DatabaseReference mUserDatabase;

    private FirebaseUser mCurrentUser;
    private StorageReference mImageStorage;
    private ProgressDialog mProgressDialog;
    private ProgressDialog mProgressDialogForSaveChanges;
    private ProgressDialog mProgressDialogForLoading;

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

        setContentView(R.layout.activity_edit_profile);

        mToolbar = (Toolbar) findViewById(R.id.edit_profile_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle(R.string.edit_profile);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        editProfileLayout = (RelativeLayout) findViewById(R.id.edit_profile_layout);

        mProfileImage = (CircleImageView) findViewById(R.id.edit_profile_image);
        mEmergencyNumberOne = (TextInputLayout) findViewById(R.id.edit_profile_emergency_contact_one);
        mEmergencyNumberTwo = (TextInputLayout) findViewById(R.id.edit_profile_emergency_contact_two);
        mSaveChanges = (Button) findViewById(R.id.edit_profile_save_changes);
        mChangeAvatar = (Button) findViewById(R.id.edit_profile_change_avatar);

        mImageStorage = FirebaseStorage.getInstance().getReference();
        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();

        String current_uid = mCurrentUser.getUid();

        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(current_uid);
        mUserDatabase.keepSynced(true);

        mProgressDialogForLoading = new ProgressDialog(this);
        mProgressDialogForLoading.setTitle(R.string.uploading_image);
        mProgressDialogForLoading.setCanceledOnTouchOutside(false);
        mProgressDialogForLoading.show();

        mUserDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                final String image = dataSnapshot.child("image").getValue().toString();
                String emergency_contact_one = dataSnapshot.child("emergency_contact_one").getValue().toString();
                String emergency_contact_two = dataSnapshot.child("emergency_contact_two").getValue().toString();

                mEmergencyNumberOne.getEditText().setText(emergency_contact_one);
                mEmergencyNumberTwo.getEditText().setText(emergency_contact_two);

                if (!image.equals("default")) {

                    Picasso.with(EditProfile.this).load(image).networkPolicy(NetworkPolicy.OFFLINE)
                            .placeholder(R.drawable.default_user).into(mProfileImage, new Callback() {
                        @Override
                        public void onSuccess() {

                        }

                        @Override
                        public void onError() {
                            Picasso.with(EditProfile.this).load(image).placeholder(R.drawable.default_user).into(mProfileImage);
                        }
                    });

                }
                mProgressDialogForLoading.dismiss();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

                mProgressDialogForLoading.dismiss();

            }

        });


        mChangeAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent galleryIntent = new Intent();
                galleryIntent.setType("image/*");
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);

                startActivityForResult(Intent.createChooser(galleryIntent, getApplicationContext().getString(R.string.select_image)), GALLERY_PICK);

            }
        });

        mSaveChanges.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mProgressDialogForSaveChanges = new ProgressDialog(EditProfile.this);
                mProgressDialogForSaveChanges.setTitle(R.string.saving_changes);
                mProgressDialogForSaveChanges.setMessage(getApplicationContext().getString(R.string.please_wait_while_we_are_updating_your_avatar));
                mProgressDialogForSaveChanges.show();

                String emergency_one = mEmergencyNumberOne.getEditText().getText().toString();
                final String emergency_two = mEmergencyNumberTwo.getEditText().getText().toString();

                mUserDatabase.child("emergency_contact_one").setValue(emergency_one).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if(task.isSuccessful()) {

                            mUserDatabase.child("emergency_contact_two").setValue(emergency_two).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {

                                    if(task.isSuccessful()) {

                                        mProgressDialogForSaveChanges.dismiss();

                                    } else {

                                        Toast.makeText(getApplicationContext(), R.string.error_while_updating_second_emergency_contact, Toast.LENGTH_LONG).show();

                                    }
                                }
                            });

                        } else {

                            Toast.makeText(getApplicationContext(), R.string.error_while_updating_first_emergency_contact, Toast.LENGTH_LONG).show();

                        }
                    }
                });

            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == GALLERY_PICK && resultCode == RESULT_OK) {

            Uri imageUri = data.getData();

            CropImage.activity(imageUri)
                    .setAspectRatio(1, 1)
                    .start(this);
        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {

            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            if (resultCode == RESULT_OK) {

                mProgressDialog = new ProgressDialog(EditProfile.this);
                mProgressDialog.setTitle(R.string.uploading_image);
                mProgressDialog.setMessage(getApplicationContext().getString(R.string.please_wait_while_we_upload_and_process_the_image));
                mProgressDialog.setCanceledOnTouchOutside(false);
                mProgressDialog.show();

                Uri resultUri = result.getUri();

                File thumb_filepath = new File(resultUri.getPath());

                String current_user_id = mCurrentUser.getUid();

                final Bitmap thumb_bitmap = new Compressor(this)
                        .setMaxWidth(200)
                        .setMaxHeight(200)
                        .setQuality(75)
                        .compressToBitmap(thumb_filepath);

                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                thumb_bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                final byte[] thumb_byte = baos.toByteArray();

                StorageReference filePath = mImageStorage.child("profile_images").child(current_user_id + ".jpg");

                final StorageReference thumb_filePath = mImageStorage.child("profile_images").child("thumbs").child(current_user_id + ".jpg");
                filePath.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                        if(task.isSuccessful()) {

                            final String download_url = task.getResult().getDownloadUrl().toString();

                            UploadTask uploadTask = thumb_filePath.putBytes(thumb_byte);
                            uploadTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> thumb_task) {

                                    String thumb_downloadUrl = thumb_task.getResult().getDownloadUrl().toString();

                                    if(thumb_task.isSuccessful()) {

                                        Map updateHashmap = new HashMap<>();
                                        updateHashmap.put("image", download_url);
                                        updateHashmap.put("thumb_image", thumb_downloadUrl);

                                        mUserDatabase.updateChildren(updateHashmap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {

                                                if(task.isSuccessful()) {

                                                    mProgressDialog.dismiss();
                                                    Snackbar.make(editProfileLayout, R.string.successfully_updated, Snackbar.LENGTH_LONG).show();

                                                }

                                            }
                                        });

                                    } else {

                                        Snackbar.make(editProfileLayout, R.string.error_in_uploading_thumbnail, Snackbar.LENGTH_LONG).show();
                                        mProgressDialog.dismiss();

                                    }

                                }
                            });
                        } else {

                            Snackbar.make(editProfileLayout, R.string.error_in_uploading, Snackbar.LENGTH_LONG).show();
                            mProgressDialog.dismiss();

                        }

                    }
                });

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {

                Exception error = result.getError();
                Snackbar.make(editProfileLayout, "Error: " + error, Snackbar.LENGTH_LONG).show();

            } else {

                Snackbar.make(editProfileLayout, "Error", Snackbar.LENGTH_LONG).show();

            }

        }

    }
}

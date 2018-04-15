package fragments;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kesar.swayam.EditProfile;
import com.example.kesar.swayam.MainActivity;
import com.example.kesar.swayam.R;
import com.example.kesar.swayam.User;
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
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import id.zelory.compressor.Compressor;

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 */
public class UserProfile extends Fragment {

    RelativeLayout user_profile_layout;

    private static final int GALLERY_PICK = 1;

    private CircleImageView userImage;
    private TextView userName;
    private TextView userEmail;
    private TextView userNumber;

    private ImageButton edit_profile;

    private View mMainView;

    private FirebaseAuth mAuth;
    private DatabaseReference mUserDatabase;

    private FirebaseUser mCurrentUser;
    private StorageReference mImageStorage;
    private ProgressDialog mProgressDialog;

    Activity activity;

    public UserProfile() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        mMainView =  inflater.inflate(R.layout.fragment_user_profile, container, false);

        mAuth = FirebaseAuth.getInstance();

        mImageStorage = FirebaseStorage.getInstance().getReference();

        mCurrentUser = mAuth.getCurrentUser();
        activity = (Activity) getActivity();

        String current_uid = mCurrentUser.getUid();

        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(current_uid);
        mUserDatabase.keepSynced(true);

        userImage = (CircleImageView) mMainView.findViewById(R.id.user_profile_image);
        userName = (TextView) mMainView.findViewById(R.id.user_profile_name);
        userEmail = (TextView) mMainView.findViewById(R.id.user_profile_email);
        userNumber = (TextView) mMainView.findViewById(R.id.user_profile_number);

        edit_profile = (ImageButton) mMainView.findViewById(R.id.edit_profile);

        user_profile_layout = (RelativeLayout) mMainView.findViewById(R.id.user_profile_layout);

        mUserDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                String name = dataSnapshot.child("name").getValue().toString();
                String email = dataSnapshot.child("email").getValue().toString();
                String phone = dataSnapshot.child("phone").getValue().toString();
                final String image = dataSnapshot.child("image").getValue().toString();

                userName.setText(name);
                userEmail.setText(email);
                userNumber.setText(phone);

                if (!image.equals("default")) {

                    Picasso.with(mMainView.getContext()).load(image).networkPolicy(NetworkPolicy.OFFLINE)
                            .placeholder(R.drawable.default_user).into(userImage, new Callback() {
                        @Override
                        public void onSuccess() {

                        }

                        @Override
                        public void onError() {

                            Picasso.with(mMainView.getContext()).load(image).placeholder(R.drawable.default_user).into(userImage);

                        }
                    });

                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        edit_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent editProfileIntent = new Intent(getActivity(), EditProfile.class);
                startActivity(editProfileIntent);
                /*Intent galleryIntent = new Intent();
                galleryIntent.setType("image/*");
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);

                startActivityForResult(Intent.createChooser(galleryIntent, String.valueOf(R.string.select_image)), GALLERY_PICK);*/

                //CropImage.activity().setGuidelines(CropImageView.Guidelines.ON).start(getActivity());

            }
        });

        return mMainView;

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == GALLERY_PICK && resultCode == RESULT_OK) {

            Uri imageUri = data.getData();

            CropImage.activity(imageUri)
                    .setAspectRatio(1, 1)
                    .start(getActivity());
        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {

            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            if (resultCode == RESULT_OK) {

                mProgressDialog = new ProgressDialog(mMainView.getContext());
                mProgressDialog.setTitle(R.string.uploading_image);
                mProgressDialog.setMessage(String.valueOf(R.string.please_wait_while_we_upload_and_process_the_image));
                mProgressDialog.setCanceledOnTouchOutside(false);
                mProgressDialog.show();

                Uri resultUri = result.getUri();

                File thumb_filepath = new File(resultUri.getPath());

                String current_user_id = mCurrentUser.getUid();

                Bitmap thumb_bitmap = new Compressor(mMainView.getContext())
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
                                                    Snackbar.make(mMainView, R.string.successfully_updated, Snackbar.LENGTH_LONG).show();

                                                }

                                            }
                                        });

                                    } else {

                                        Snackbar.make(mMainView, R.string.error_in_uploading_thumbnail, Snackbar.LENGTH_LONG).show();
                                        mProgressDialog.dismiss();

                                    }

                                }
                            });
                        } else {

                            Snackbar.make(mMainView, R.string.error_in_uploading, Snackbar.LENGTH_LONG).show();
                            mProgressDialog.dismiss();

                        }

                    }
                });

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {

                Exception error = result.getError();
                Snackbar.make(mMainView, "Error: " + error, Snackbar.LENGTH_LONG).show();

            } else {

                Snackbar.make(mMainView, "Error", Snackbar.LENGTH_LONG).show();

            }

        }


    }
}

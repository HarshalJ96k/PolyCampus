package com.polycampus.android.MyProfile;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.MODE_PRIVATE;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import com.polycampus.android.LoginActivity;
import com.polycampus.android.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class MyProfileFragment extends Fragment {
    TextView tvUsername, tvname, tvEmail, tvMobileno, tvparentno, tvAddress, tvBranch, tvSem, tvSubject, tvGender;
    TextView tvHeaderName, tvHeaderUsername;
    ImageView ivProfilePhoto;
    Button btnProfileUpdate, btnProfileLogout;
    FloatingActionButton btnChangeProfilePhoto;
    ProgressDialog progressDialog;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    Uri uri;
    Bitmap bitmap;
    String strusername;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_my_profile, container, false);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        editor = sharedPreferences.edit();

        tvUsername = view.findViewById(R.id.tvMyProfileUsername);
        tvname = view.findViewById(R.id.tvMyProfileName);
        tvEmail = view.findViewById(R.id.tvMyProfileEmail);
        tvparentno = view.findViewById(R.id.tvMyProfileParentsNo);
        tvMobileno = view.findViewById(R.id.tvMyProfileMobileNo);
        tvBranch = view.findViewById(R.id.tvMyProfileBranch);
        tvAddress = view.findViewById(R.id.tvMyProfileAddress);
        tvGender = view.findViewById(R.id.tvMyProfileGender);
        tvSem = view.findViewById(R.id.tvMyProfileSem);
        tvSubject = view.findViewById(R.id.tvMyProfileSubject);
        tvHeaderName = view.findViewById(R.id.tvMyProfileNameLabel);
        tvHeaderUsername = view.findViewById(R.id.tvMyProfileUsernameLabel);
        ivProfilePhoto = view.findViewById(R.id.ivMyProfileImage);
        btnChangeProfilePhoto = view.findViewById(R.id.btnMyProfileChangePhoto);
        btnProfileUpdate = view.findViewById(R.id.btnMyProfileUpdate);
        btnProfileLogout = view.findViewById(R.id.btnMyProfileLogOut);

        strusername = sharedPreferences.getString("username", "");

        tvUsername.setText(sharedPreferences.getString("username", ""));
        tvname.setText(sharedPreferences.getString("name", ""));
        tvHeaderName.setText(sharedPreferences.getString("name", "Student Name"));
        tvHeaderUsername.setText("@" + sharedPreferences.getString("username", "student"));
        tvMobileno.setText(sharedPreferences.getString("mobile_no",""));
        tvparentno.setText(sharedPreferences.getString("parent_mobile_no",""));
        tvEmail.setText(sharedPreferences.getString("email_id",""));
        tvGender.setText(sharedPreferences.getString("gender",""));
        tvAddress.setText(sharedPreferences.getString("address",""));
        tvBranch.setText(sharedPreferences.getString("branch",""));
        tvSem.setText(sharedPreferences.getString("sem",""));
        tvSubject.setText(sharedPreferences.getString("subject",""));

        loadProfilePhoto();

        btnChangeProfilePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showImageChooser();
            }
        });

        btnProfileLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logout();
            }
        });

        btnProfileUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            Intent intent = new Intent(getActivity(),UpdateProfileActivity.class);
            intent.putExtra("name",tvname.getText().toString());
            intent.putExtra("mobile_no",tvMobileno.getText().toString());
            intent.putExtra("parent_mobile_no",tvparentno.getText().toString());
            intent.putExtra("email",tvEmail.getText().toString());
            intent.putExtra("gender",tvGender.getText().toString());
            intent.putExtra("address",tvAddress.getText().toString());
            intent.putExtra("branch",tvBranch.getText().toString());
            intent.putExtra("sem",tvSem.getText().toString());
            intent.putExtra("subject",tvSubject.getText().toString());
            startActivity(intent);
            }
        });

        return view;
    }

    private void showImageChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), 1);
    }

    private void loadProfilePhoto() {
        String profilePhotoPath = sharedPreferences.getString("profilephoto", "");
        if (!profilePhotoPath.isEmpty()) {
            File imgFile = new File(profilePhotoPath);
            if (imgFile.exists()) {
                Bitmap bitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                ivProfilePhoto.setImageBitmap(bitmap);
            } else {
                ivProfilePhoto.setImageResource(R.drawable.imagenotfound);
            }
        }
    }

    private void logout() {
        new com.google.android.material.dialog.MaterialAlertDialogBuilder(getActivity())
                .setTitle("Log Out")
                .setMessage("Are you sure you want to log out from your profile?")
                .setIcon(R.drawable.baseline_logout_24)
                .setPositiveButton("Log out", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        editor.putBoolean("isLogin", false).apply();
                        Intent i = new Intent(getActivity(), LoginActivity.class);
                        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(i);
                    }
                })
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                .show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK && data != null && data.getData() != null) {
            uri = data.getData();
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), uri);
                ivProfilePhoto.setImageBitmap(bitmap);

                File imageFile = new File(getActivity().getFilesDir(), "profile.jpg");
                FileOutputStream outputStream = getActivity().openFileOutput(imageFile.getName(), MODE_PRIVATE);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
                outputStream.close();

                // Save file path in SharedPreferences
                editor.putString("profilephoto", imageFile.getAbsolutePath());
                editor.apply();

            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(getActivity(), "Error saving image", Toast.LENGTH_SHORT).show();
            }
        }
    }
}

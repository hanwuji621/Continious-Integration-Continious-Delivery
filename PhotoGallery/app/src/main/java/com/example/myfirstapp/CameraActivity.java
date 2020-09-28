package com.example.myfirstapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;


// CameraActivity - Launches Android's built-in camera to take a photo when pressing take a photo

public class CameraActivity extends AppCompatActivity {

    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int PERMISSION_CODE = 1000;

    String currentPhotoPath = null;
    private File photoFile;
    static final int REQUEST_TAKE_PHOTO = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
    }

    /**
     *  button onClick function for taking Photo
     */
    public void takePhoto(View v) throws IOException{

        /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.CAMERA) ==
                    PackageManager.PERMISSION_DENIED ||
                    checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) ==
                            PackageManager.PERMISSION_DENIED) {
                String[] permission = {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
                requestPermissions(permission, PERMISSION_CODE);
            }
        }*/

        dispatchTakePictureIntent();
    }
    /** Creates files, then creates jpg file for new photo */
    private File createImageFile() throws IOException{

        //Create file path to Download folder to check if it exists and later generate a URI
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        //Create the storageDir folder if it does not exist
        if(!storageDir.exists()) {
            storageDir.mkdir();
        }

        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";

        // Prepares the final path for the output photo file
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }


    private void dispatchTakePictureIntent() throws IOException {

        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        // Ensure that there is a camera acitivty to handle the intent
        // however comment out it
        /*if (takePictureIntent.resolveActivity(getPackageManager()) != null) */{

            // the file where the photo should go
            photoFile = createImageFile();

            // Check if creation of a new photo file was successful created
            if (photoFile != null) {

                // Gets a URI for the file based on authority of app's own fileprovider
                // take care using right authority
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.example.myfirstapp.fileprovider",
                        photoFile);

                // MediaStore.EXTRA_OUTPUT indicates the the following photoURI will store the photo

                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);

                // Starts the Android Camera, REQUEST_TAKE_PHOTO = 1
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }
    }

    /*@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_CANCELED){
            photoFile.delete();
        }
    }*/

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            ImageView mImageView = (ImageView) findViewById(R.id.imageView);
            mImageView.setImageBitmap(BitmapFactory.decodeFile(currentPhotoPath));
        }
    }
}

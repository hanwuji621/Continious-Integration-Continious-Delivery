package com.example.myfirstapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import static android.provider.AlarmClock.EXTRA_MESSAGE;
import java.util.Date;
import java.util.Locale;

import android.provider.MediaStore;
import android.net.Uri;


public class MainActivity extends AppCompatActivity {
    public static final String EXTRA_MESSAGE = "com.example.myfirstapp.MESSAGE";
    private ArrayList<String> photos = null;
    static final int REQUEST_IMAGE_CAPTURE =1;
    String myCurrentPhotoPath;
    private int index = 0;
    static final int REQUEST_TAKE_PHOTO = 1;
    private File photoFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        photos = findPhotos();
        if (photos.size() == 0) {
            displayPhoto(null);
        } else {
            displayPhoto(photos.get(index));
        }
    }

    public void takePhoto(View v) throws IOException {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        photoFile = createImageFile();

        // Check if creation of a new photo file was successful created
        if (photoFile != null) {
            // Gets a URI for the file based on authority of app's own fileprovider
            // take care using right authority
            Uri photoURI = FileProvider.getUriForFile(this,"com.example.myfirstapp.fileprovider", photoFile);

            // MediaStore.EXTRA_OUTPUT indicates the the following photoURI will store the photo
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);

            // Starts the Android Camera, REQUEST_TAKE_PHOTO = 1
            startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
        }
    }

    private File createImageFile() throws IOException {
        //Create an image file name
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
        myCurrentPhotoPath= image.getAbsolutePath();
        return image;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            ImageView mImageView = (ImageView) findViewById(R.id.ivGallery);
            mImageView.setImageBitmap(BitmapFactory.decodeFile(myCurrentPhotoPath));
        }
    }

    /** function to start activity for camera portion of app */

//    public void openCameraScreen(View v){
//        Intent intent = new Intent(this, CameraActivity.class);
//        startActivity(intent);
//    }

    public void startSearch(View v){
        Intent intent = new Intent(this, SearchActivity.class);
        startActivity(intent);
    }

    public void checkPermission(){
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            // temporary fix to prevent crash if current Android version lower than required SDK
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
            }
        }
    }


    private ArrayList<String> findPhotos() {
        File file = new File(Environment.getExternalStorageDirectory()
                .getAbsolutePath(), "/Android/data/com.example.myfirstapp/files/Pictures");
        ArrayList<String> photos = new ArrayList<String>();
        File[] fList = file.listFiles();
        if (fList != null) {
            for (File f : fList) { photos.add(f.getPath());
            } }
        return photos;
    }

//    private ArrayList<String> findPhotos(Date startTimestamp, Date endTimestamp, String keywords) {
//        File file = new File(Environment.getExternalStorageDirectory()
//                .getAbsolutePath(), "/Android/data/com.example.myfirstapp/files/Pictures");
//        ArrayList<String> photos = new ArrayList<String>();
//        File[] fList = file.listFiles();
//        if (fList != null) {
//            for (File f : fList) {
//                if (((startTimestamp == null && endTimestamp == null) || (f.lastModified() >= startTimestamp.getTime()
//                        && f.lastModified() <= endTimestamp.getTime())) && (keywords == "" || f.getPath().contains(keywords)))
//                    photos.add(f.getPath());
//            }
//        }
//        return photos;
//    }
//    private ArrayList<String> findPhotos(Date startTimestamp, Date endTimestamp, String keywords) {
//        File file = new File(Environment.getExternalStorageDirectory()
//                .getAbsolutePath(), "/Android/data/com.example.myfirstapp/files/Pictures");
//        ArrayList<String> photos = new ArrayList<String>();
//        File[] fList = file.listFiles();
//        if (fList != null) {
//            for (File f : fList) {
//                if (((startTimestamp == null && endTimestamp == null) || (f.lastModified() >= startTimestamp.getTime()
//                        && f.lastModified() <= endTimestamp.getTime())
//                ) && (keywords == "" || f.getPath().contains(keywords)))
//                    photos.add(f.getPath());
//            }
//        }
//        return photos;
//    }


    public void scrollPhotos(View v) { switch (v.getId()) {
        case R.id.btnPrev:
            if (index > 0) {
                index--;
            }
            break;
        case R.id.btnNext:
            if (index < (photos.size() -1)){
            index++;
        }
        break;
        default:
            break;
        }
        displayPhoto(photos.get(index));
    }


    private void displayPhoto(String path) {
        ImageView iv = (ImageView) findViewById(R.id.ivGallery);
        TextView tv = (TextView) findViewById(R.id.tvTimestamp);
        EditText et = (EditText) findViewById(R.id.etCaption);
        if (path == null || path == "") {
            iv.setImageResource(R.mipmap.ic_launcher);
            et.setText("");
            tv.setText("");
        } else {
            iv.setImageBitmap(BitmapFactory.decodeFile(path));
            String[] attr = path.split("_");
            et.setText(attr[1]);

            SimpleDateFormat preformattedDate = new SimpleDateFormat("yyyyMMdd");
            SimpleDateFormat preformattedTime = new SimpleDateFormat("HHmmss");
            SimpleDateFormat formattedDate = new SimpleDateFormat("MMM-dd-yyyy");
            SimpleDateFormat formattedTime = new SimpleDateFormat("HH:mm:ss");
            try {
                String reformattedDate = formattedDate.format(preformattedDate.parse(attr[1]));
                String reformattedTime = formattedTime.format(preformattedTime.parse(attr[2]));
                tv.setText(reformattedDate + " " + reformattedTime);
            } catch (ParseException e) {
                e.printStackTrace();
            }

//            try {
//                formattedDate = new SimpleDateFormat("yyyyMMdd", Locale.ENGLISH).parse(preformattedDate);
//                String printableDate = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).format(formattedDate);
//                tv.setText(printableDate);
//            } catch (ParseException e) {
//                e.printStackTrace();
//                String printableDate = "Could not parse date.";
//                tv.setText(printableDate);
//            }
        }
    }

}
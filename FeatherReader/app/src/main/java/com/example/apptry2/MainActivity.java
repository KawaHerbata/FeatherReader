package com.example.apptry2;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    static final int REQUEST_IMAGE_CAPTURE = 1;
    private String currentPhotoPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        deleteTempFiles(this.getCacheDir());
        setContentView(R.layout.activity_main);
    }

    public void switchSpecies(View view)
    {
        Intent i = new Intent(this, SpeciesActivity.class);
        startActivity(i);
    }

    public void endApp(View view)
    {
        this.finish();
        System.exit(0);
    }

    public void takePhoto(View view)
    {
        dispatchTakePictureIntent();
    }

    public void choosePhoto(View view){readImageFromAlbum();}

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if(takePictureIntent.resolveActivity(getPackageManager()) != null)
        {
            File photoFile = null;
            try {
                photoFile = createImageFile();
            }catch (IOException ex){

            }
            if(photoFile != null)
            {
                Uri photoURI = FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID + ".provider",photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }

    }

    private File createImageFile() throws IOException{
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(imageFileName, ".jpg", storageDir);
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    private boolean deleteTempFiles(File file){
        if(file.isDirectory())
        {
            File[] files =file.listFiles();
            if (files != null)
            {
                for(File f : files)
                {
                    if(f.isDirectory())
                    {
                        deleteTempFiles(f);
                    }else
                    {
                        f.delete();

                    }
                }
            }
        }
        return file.delete();
    }

    private void readImageFromAlbum()
    {
        try{
            Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(i, 3);
        }catch(Exception e){
            Log.i("TAG", "readImageFromAlbum: error");
        }
    }

    private void saveUriBitmap(Uri uriFile)
    {
        try {
            InputStream imgStream = getContentResolver().openInputStream(uriFile);
            Bitmap imgBitmap = BitmapFactory.decodeStream(imgStream);

            File saveFile = createImageFile();
            OutputStream fOutput = new FileOutputStream(saveFile);
            imgBitmap.compress(Bitmap.CompressFormat.JPEG,100,fOutput);
            fOutput.flush();
            fOutput.close();
            MediaStore.Images.Media.insertImage(getContentResolver(), saveFile.getAbsolutePath(), saveFile.getName(), saveFile.getName());
        }catch(FileNotFoundException e){

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK)
        {
            Intent i = new Intent(this, LoadingActivity.class);
            i.putExtra("path", currentPhotoPath);
            startActivity(i);
        }
        else
        {
            if(resultCode == RESULT_OK && data != null)
            {
                Uri selectedImage = data.getData();
                currentPhotoPath = selectedImage.getPath();
                saveUriBitmap(selectedImage);

                Intent i = new Intent(this, LoadingActivity.class);
                i.putExtra("path", currentPhotoPath);
                startActivity(i);
            }
        }
    }
}
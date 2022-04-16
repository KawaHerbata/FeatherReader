package com.example.apptry2;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.os.AsyncTask;
import android.widget.Toast;
import java.util.concurrent.TimeUnit;

import androidx.appcompat.app.AppCompatActivity;
import java.net.*;
import java.io.*;

public class LoadingActivity extends AppCompatActivity {

    private String filePath;
    private String speciesName;
    private String address = "http://192.168.0.157:5000/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle extras = getIntent().getExtras();
        if(extras != null)
        {
            filePath = extras.getString("path");
            Log.d("NEW ACTIVITY", "onCreate: "+filePath);
        }
        setContentView(R.layout.activity_loading);
    }

    @Override
    public void onBackPressed(){

    }

    @Override
    protected void onStart(){
        super.onStart();
        serverRequest();
    }

    private void serverRequest(){
        try{

            Bitmap bm;
            BitmapFactory.Options bmOptions = new BitmapFactory.Options();
            bmOptions.inSampleSize = 2;
            bm = BitmapFactory.decodeFile(filePath, bmOptions);


            UploadImageToServer uploadImageToServer = new UploadImageToServer(address,bm);
            uploadImageToServer.execute();


        }catch (Exception e){
            Log.d("HTTP SEND", "serverRequest: NOT SEND, ERROR");
        }


    }

    private void startResult(boolean result)
    {
        Intent i;
        if(result == true)
        {
            i = new Intent(this, ResultActivity.class);
        }
        else
        {
            i = new Intent(this, MainActivity.class);
        }

        i.putExtra("path", filePath);
        i.putExtra("species", speciesName);
        this.finish();
        startActivity(i);
    }

    private class UploadImageToServer extends AsyncTask<String, String, String> {
        String requesturl = "";
        Bitmap bitmap = null;

        UploadImageToServer(String requestUrl, Bitmap bitmap) {
            this.requesturl = requestUrl;
            this.bitmap = bitmap;
        }
        @Override
        protected String doInBackground(String... strings) {
            if(!this.requesturl.equals("")){
                HttpRequestHelper httpRequestHelper = new HttpRequestHelper(requesturl);
                httpRequestHelper.addFilePart(bitmap);
                String response = httpRequestHelper.finish();
                speciesName = response;
                return response;
            }
            return "";
        }


        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            Log.e("Result" , result);
            if(!result.equals("0") && !speciesName.equals("error")){
                startResult(true);
            }else{
                Toast.makeText(LoadingActivity.this, "Image is not uploaded, error ocurred.", Toast.LENGTH_LONG).show();
                File f = new File(filePath);
                f.delete();
                startResult(false);
            }
        }
    }


}




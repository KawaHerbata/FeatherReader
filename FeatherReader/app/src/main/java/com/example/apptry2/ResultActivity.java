package com.example.apptry2;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;

public class ResultActivity extends AppCompatActivity {

    private String filePath;
    private String speciesName;

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

    @Override
    public void onBackPressed(){

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);
        Bundle extras = getIntent().getExtras();
        if(extras != null)
        {
            filePath = extras.getString("path");
            speciesName = extras.getString("species");
        }
    }

    @Override
    protected void onStart()
    {
        super.onStart();
        TextView textView = (TextView) findViewById(R.id.species_name_text);
        textView.setText(speciesName);
        Bitmap bitmap = BitmapFactory.decodeFile(filePath);
        ImageView imageFeather = (ImageView) findViewById(R.id.feather_view);
        imageFeather.setImageBitmap(bitmap);

        try{
            int check = bitmap.getHeight();
            Log.d("NOT EMPTY", "onStart: NOT EMPTY BITMAP");
        }catch (Exception e)
        {
            Log.d("EMPTY BITMAP", "onStart: EMPTY BITMAP");
        }

    }

    @Override
    protected void onDestroy()
    {
        File file = new File(filePath);
        boolean deleted = file.delete();
        Log.d("DELETED ACTIVITY RESULT", String.valueOf(deleted));
        super.onDestroy();

    }


}
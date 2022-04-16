package com.example.apptry2;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.net.Uri;

import java.util.ArrayList;

public class SpeciesActivity extends AppCompatActivity {

    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_species);

        listView=(ListView) findViewById(R.id.listViewSpecies);

        ArrayList<String> listaGatunkow = new ArrayList<String>();
        String[] gat = getResources().getStringArray(R.array.species_list);

        for(int i = 0; i < gat.length; i++)
            listaGatunkow.add("   "+gat[i]);

        String[] url = getResources().getStringArray(R.array.url_list);


        ArrayAdapter arrayAdapter = new ArrayAdapter(this, R.layout.list_white_text,listaGatunkow);

        listView.setAdapter(arrayAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, android.view.View view, int position, long id) {

                try{
                    Uri uri = Uri.parse(url[position]);
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    startActivity(intent);
                }catch (Exception e){Toast.makeText(getApplicationContext(), "No Internet connection.", Toast.LENGTH_LONG).show();}

            }
        });

    }
}
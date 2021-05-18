package com.example.projetonassau.storage;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.projetonassau.R;

public class StorageUploadActivity extends AppCompatActivity implements View.OnClickListener {


    private ImageView imageView;
    private Button button_Enviar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.storage_upload_activity);

        imageView = (ImageView) findViewById(R.id.imageView_StorageUpload);
        button_Enviar = (Button) findViewById(R.id.button_StorageUpload_Enviar);

        button_Enviar.setOnClickListener(this);

    }

    //-----------------------------------------------TRATAMENTO
    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.button_StorageUpload_Enviar:

                Toast.makeText(getBaseContext(), "button_StorageUpload_Enviar", Toast.LENGTH_LONG).show();

                break;

        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_storage_upload, menu);
        return super.onCreateOptionsMenu(menu);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.item_galeria:

                Toast.makeText(getBaseContext(), "item_galeria", Toast.LENGTH_LONG).show();
                break;

            case R.id.item_camera:

                Toast.makeText(getBaseContext(), "item_camera", Toast.LENGTH_LONG).show();
                break;

        }

        return super.onOptionsItemSelected(item);
    }
}

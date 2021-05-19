package com.example.projetonassau.storage;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.projetonassau.R;
import com.example.projetonassau.util.Permissao;

import java.io.File;

public class StorageUploadActivity extends AppCompatActivity implements View.OnClickListener {


    private ImageView imageView;
    private Button button_Enviar;
    private Uri uri_Imagem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.storage_upload_activity);

        imageView = (ImageView) findViewById(R.id.imageView_StorageUpload);
        button_Enviar = (Button) findViewById(R.id.button_StorageUpload_Enviar);

        button_Enviar.setOnClickListener(this);

        permissao();

    }

    //______________________________Pemissão do Usuário______________________________________________

    private void permissao() {

        String permissoes[] = new String[]{

                Manifest.permission.CAMERA,
        };

        Permissao.permissao(this, 0, permissoes);

    }

    //----------------------------------------------- Tratamento de Click -----------------------------------
    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.button_StorageUpload_Enviar:

                Toast.makeText(getBaseContext(), "button_StorageUpload_Enviar", Toast.LENGTH_LONG).show();

                break;

        }

    }


    //-----------------------------------------------------Menu-----------------------------------------------
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_storage_upload, menu);
        return super.onCreateOptionsMenu(menu);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.item_galeria:

                obterImagem_Galeria();

                break;

            case R.id.item_camera:

                obterImagem_Camera();

                break;

        }

        return super.onOptionsItemSelected(item);
    }


    //----------------------------------------------Obter Imagens-----------------------------------------------

    private void obterImagem_Galeria() {

        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);

        startActivityForResult(Intent.createChooser(intent, "Escolha uma imagem"), 0);

    }

    private void obterImagem_Camera() {

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File diretorio = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);

        String nomeImagem = diretorio.getPath() + "/" + "CursoImagem" + System.currentTimeMillis() + ".jpg";

        File file = new File(nomeImagem);

        String autorizacao = "com.example.projetonassau";

        uri_Imagem = FileProvider.getUriForFile(getBaseContext(), autorizacao, file);

        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri_Imagem);

        startActivityForResult(intent, 1);

    }

    //-----------------------------------Respostas de comunicação-----------------------------------------------


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {

            if (requestCode == 0) {

                if (data != null) {

                    uri_Imagem = data.getData();
                    Glide.with(getBaseContext()).asBitmap().load(uri_Imagem).listener(new RequestListener<Bitmap>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Bitmap> target, boolean isFirstResource) {
                            Toast.makeText(getBaseContext(), "Falha ao selecionar a imagem", Toast.LENGTH_LONG).show();
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Bitmap resource, Object model, Target<Bitmap> target, DataSource dataSource, boolean isFirstResource) {
                            return false;
                        }
                    }).into(imageView);

                }else{

                    Toast.makeText(getBaseContext(), "Falha ao selecionar a imagem", Toast.LENGTH_LONG).show();

                }
            }

            else if (requestCode == 1) {

                if (uri_Imagem != null) {

                    Glide.with(getBaseContext()).asBitmap().load(uri_Imagem).listener(new RequestListener<Bitmap>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Bitmap> target, boolean isFirstResource) {
                            Toast.makeText(getBaseContext(), "Falha ao selecionar a imagem", Toast.LENGTH_LONG).show();
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Bitmap resource, Object model, Target<Bitmap> target, DataSource dataSource, boolean isFirstResource) {
                            return false;
                        }
                    }).into(imageView);
                } else {
                    Toast.makeText(getBaseContext(), "Falha ao Tirar Foto", Toast.LENGTH_LONG).show();
                }

            }


        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        for (int result : grantResults) {

            if (result == PackageManager.PERMISSION_DENIED) {

                Toast.makeText(this, "Aceite as permissões para o aplicativo acessar sua câmera", Toast.LENGTH_SHORT).show();
                finish();
                break;
            }
        }

    }
}

package com.example.projetonassau.database_lista_funcionario;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;


import com.example.projetonassau.R;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.example.projetonassau.R;
import com.example.projetonassau.util.DialogAlerta;
import com.example.projetonassau.util.DialogProgress;
import com.example.projetonassau.util.Util;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DatabaseListaFuncionarioDadosActivity extends AppCompatActivity implements View.OnClickListener {


    private ImageView imageView;
    private ProgressBar progressBar;



    private EditText editText_Nome;
    private EditText editText_Idade;

    private Button button_Alterar;
    private Button button_Remover;


    private Funcionario funcionarioSelecionado;

    private Uri uri_imagem = null;
    private boolean imagem_Alterada = false;


    private FirebaseDatabase database;
    private FirebaseStorage storage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.database_lista_funcionario_dados_activity);


        imageView = (ImageView)findViewById(R.id.imageView_Database_Dados_Funcionario);
        progressBar = (ProgressBar)findViewById(R.id.progressBar_Database_Dados_Funcionario);

        editText_Nome = (EditText)findViewById(R.id.editText_Database_Dados_Funcionario_Nome);
        editText_Idade = (EditText)findViewById(R.id.editText_Database_Dados_Funcionario_Idade);


        button_Alterar = (Button)findViewById(R.id.button_Database_Dados_Funconario_Alterar);
        button_Remover = (Button)findViewById(R.id.button_Database_Dados_Funconario_Remover);


        imageView.setOnClickListener(this);
        button_Alterar.setOnClickListener(this);
        button_Remover.setOnClickListener(this);


        database = FirebaseDatabase.getInstance();
        storage = FirebaseStorage.getInstance();

        funcionarioSelecionado = getIntent().getParcelableExtra("funcionario");


        carregarDadosFuncionario();

    }



    //--------------------------------------CARREGAR DADOS FUNCIONARIO ---------------------------------------


    private void carregarDadosFuncionario(){


        progressBar.setVisibility(View.VISIBLE);


        editText_Nome.setText(funcionarioSelecionado.getNome());
        editText_Idade.setText(funcionarioSelecionado.getIdade()+"");

        Picasso.with(getBaseContext()).load(funcionarioSelecionado.getUrlimagem()).into(imageView, new com.squareup.picasso.Callback(){


            @Override
            public void onSuccess() {
                progressBar.setVisibility(View.GONE);

            }

            @Override
            public void onError() {

                progressBar.setVisibility(View.GONE);

            }
        });

    }


    //--------------------------------------TRATAMENTO DE CLICKS ---------------------------------------
    @Override
    public void onClick(View v) {


        switch (v.getId()){

            case R.id.imageView_Database_Dados_Funcionario:


                obterImagem_Galeria();

                break;


            case R.id.button_Database_Dados_Funconario_Alterar:


                buttonAlterar();

                break;


            case R.id.button_Database_Dados_Funconario_Remover:

                buttonRemover();

                break;

        }
    }



    private void buttonAlterar(){



        String nome = editText_Nome.getText().toString();
        String idade_String = editText_Idade.getText().toString();


        if(Util.verificarCampos(getBaseContext(),nome,idade_String)){


            int idade = Integer.parseInt(idade_String);



            if(!nome.equals(funcionarioSelecionado.getNome()) || idade != funcionarioSelecionado.getIdade() || imagem_Alterada ){



                if( imagem_Alterada){

                    removerImagem(nome,idade);


                }else{

                    alterarDados(nome,idade,funcionarioSelecionado.getUrlimagem());
                }


            }else{

                DialogAlerta alerta = new DialogAlerta("Erro","Nenhuma informação foi alterada para poder salvar no Banco de Dados");
                alerta.show(getSupportFragmentManager(),"2");

            }

        }
    }



    private void buttonRemover(){


        if(Util.statusInternet(getBaseContext())){

            removerFuncionarioImagem();

        }else{

            Toast.makeText(getBaseContext(),"Sem conexão com a Internet",Toast.LENGTH_LONG).show();
        }

    }


    //---------------------------------------- OBTER IMAGENS ----------------------------------------------------------------



    private void obterImagem_Galeria(){


        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);


        startActivityForResult(Intent.createChooser(intent,"Escolha uma Imagem"),0);

    }

    //---------------------------------------- REPOSTAS DE COMUNICACAO ----------------------------------------------------------------




    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if(resultCode == RESULT_OK){


            if(requestCode == 0){ // RESPOSTA DA GALERIA

                if (data != null){ // CONTEUDO DA ESCOLHA DA IMAGEM DA GALERIA

                    uri_imagem = data.getData();

                    Glide.with(getBaseContext()).asBitmap().load(uri_imagem).listener(new RequestListener<Bitmap>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Bitmap> target, boolean isFirstResource) {

                            Toast.makeText(getBaseContext(),"Erro ao carregar imagem",Toast.LENGTH_LONG).show();
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Bitmap resource, Object model, Target<Bitmap> target, DataSource dataSource, boolean isFirstResource) {

                            imagem_Alterada = true;

                            return false;
                        }
                    }).into(imageView);

                }else{

                    Toast.makeText(getBaseContext(),"Falha ao selecionar imagem",Toast.LENGTH_LONG).show();

                }
            }

        }
    }





    //---------------------------------------- TRATAMENTO DE ALTERAÇÃO DE DADOS ----------------------------------------------------------------


    private void removerImagem(final String nome, final int idade){


        final DialogProgress progress = new DialogProgress();
        progress.show(getSupportFragmentManager(),"1");

        String url = funcionarioSelecionado.getUrlimagem();

        StorageReference reference = FirebaseStorage.getInstance().getReferenceFromUrl(url);



        reference.delete().addOnCompleteListener(new OnCompleteListener<Void>() {


            @Override
            public void onComplete(@NonNull Task<Void> task) {


                if(task.isSuccessful()){


                    progress.dismiss();
                    salvarDadoStorage(nome, idade);

                }else{


                    progress.dismiss();
                    Toast.makeText(getBaseContext(),"Erro ao Remover Imagem",Toast.LENGTH_LONG).show();

                }

            }
        });



    }



    private void salvarDadoStorage(final String nome, final int idade){

        final DialogProgress progress = new DialogProgress();
        progress.show(getSupportFragmentManager(),"1");


        StorageReference reference = storage.getReference().child("BD").child("empresas").child(funcionarioSelecionado.getId_empresa());

        final StorageReference nome_imagem = reference.child("CursoFirebase"+System.currentTimeMillis()+".jpg");



        Glide.with(getBaseContext()).asBitmap().load(uri_imagem).apply(new RequestOptions().override(1024,768))
                .listener(new RequestListener<Bitmap>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Bitmap> target, boolean isFirstResource) {

                        Toast.makeText(getBaseContext(),"Erro ao transformar imagem",Toast.LENGTH_LONG).show();

                        progress.dismiss();
                        return false;

                    }

                    @Override
                    public boolean onResourceReady(Bitmap resource, Object model, Target<Bitmap> target, DataSource dataSource, boolean isFirstResource) {



                        ByteArrayOutputStream bytes = new ByteArrayOutputStream();

                        resource.compress(Bitmap.CompressFormat.JPEG,70, bytes);

                        ByteArrayInputStream inputStream = new ByteArrayInputStream(bytes.toByteArray());


                        try {
                            bytes.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }


                        UploadTask uploadTask = nome_imagem.putStream(inputStream);



                        uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>(){

                            @Override
                            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {


                                return nome_imagem.getDownloadUrl();
                            }


                        }).addOnCompleteListener(new OnCompleteListener<Uri>() {


                            @Override
                            public void onComplete(@NonNull Task<Uri> task) {


                                if(task.isSuccessful()){


                                    progress.dismiss();
                                    Uri uri = task.getResult();

                                    String url_imagem = uri.toString();

                                    alterarDados(nome, idade, url_imagem);

                                }else{

                                    progress.dismiss();
                                    Toast.makeText(getBaseContext(),"Erro ao realizar Upload - Storage",Toast.LENGTH_LONG).show();
                                }

                            }
                        });



                        return false;
                    }
                }).submit();

    }







    private void alterarDados(String nome, int idade, String url_imagem){


        final DialogProgress progress = new DialogProgress();
        progress.show(getSupportFragmentManager(),"1");


        DatabaseReference reference = database.getReference().child("BD").child("Empresas").
                child(funcionarioSelecionado.getId_empresa()).child("Funcionarios");



        Funcionario funcionario = new Funcionario(nome,idade,url_imagem);


        Map<String, Object> atualizacao = new HashMap<>();


        atualizacao.put(funcionarioSelecionado.getId(),funcionario);


        reference.updateChildren(atualizacao).addOnCompleteListener(new OnCompleteListener<Void>() {


            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if(task.isSuccessful()){


                    progress.dismiss();
                    Toast.makeText(getBaseContext(),"Sucesso ao Alterar Dados",Toast.LENGTH_LONG).show();
                    finish();

                }else{

                    progress.dismiss();
                    Toast.makeText(getBaseContext(),"Erro ao Alterar Dados",Toast.LENGTH_LONG).show();

                }


            }
        });


    }




    //---------------------------------------- TRATAMENTO DE REMOCAO DE DADOS ----------------------------------------------------------------


    private void removerFuncionarioImagem(){


        String url = funcionarioSelecionado.getUrlimagem();

        StorageReference reference = FirebaseStorage.getInstance().getReferenceFromUrl(url);



        reference.delete().addOnCompleteListener(new OnCompleteListener<Void>() {


            @Override
            public void onComplete(@NonNull Task<Void> task) {


                if(task.isSuccessful()){


                    removerFuncionario();

                }else{


                    Toast.makeText(getBaseContext(),"Erro ao Remover Imagem",Toast.LENGTH_LONG).show();

                }

            }
        });


    }


    private void removerFuncionario(){


        DatabaseReference reference = database.getReference().child("BD").child("Empresas").
                child(funcionarioSelecionado.getId_empresa()).child("Funcionarios");


        reference.child(funcionarioSelecionado.getId()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {


            @Override
            public void onComplete(@NonNull Task<Void> task) {


                if(task.isSuccessful()){


                    Toast.makeText(getBaseContext(),"Sucesso ao Remover Funcionario",Toast.LENGTH_LONG).show();
                    finish();

                }else{

                    Toast.makeText(getBaseContext(),"Erro ao Remover Funcionario",Toast.LENGTH_LONG).show();

                }


            }
        });


    }





}

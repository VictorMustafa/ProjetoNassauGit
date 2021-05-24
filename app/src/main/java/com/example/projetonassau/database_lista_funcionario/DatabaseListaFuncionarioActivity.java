package com.example.projetonassau.database_lista_funcionario;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.example.projetonassau.R;
import com.example.projetonassau.database_lista_empresa.Empresa;
import com.example.projetonassau.util.DialogAlerta;
import com.example.projetonassau.util.DialogProgress;
//import com.example.projetonassau.util.PdfCreator;
import com.example.projetonassau.util.Util;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
/*import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;*/

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

public class DatabaseListaFuncionarioActivity extends AppCompatActivity implements View.OnClickListener, RecyclerView_ListaFuncionario.ClickFuncionario {


    private LinearLayout linearLayout;
    private ImageView imageView_LimparCampos;

    private EditText editText_Nome;
    private EditText editText_Idade;

    private Button button_Salvar;
    private ImageView imageView_Galeria;


    private RecyclerView recyclerView;
    private RecyclerView_ListaFuncionario recyclerView_listaFuncionario;
    private List<Funcionario> funcionarios = new ArrayList<Funcionario>();

    private FirebaseDatabase database;
    private FirebaseStorage storage;

    private Uri uri_imagem = null;

    private ChildEventListener childEventListener;
    private DatabaseReference reference_database;
    private List<String> keys = new ArrayList<String>();

    private Empresa empresa;
    private DialogProgress progress;
    private boolean imagem_Selecionada = false;


    private boolean firebaseOffline = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.database_lista_funcionario_activity);


        linearLayout = (LinearLayout) findViewById(R.id.linearLayout_Database_Funcionario);
        imageView_LimparCampos = (ImageView) findViewById(R.id.imageView_Database_Funcionario_LimparCampos);

        editText_Nome = (EditText) findViewById(R.id.editText_Database_Funcionario_Nome);
        editText_Idade = (EditText) findViewById(R.id.editText_Database_Funcionario_Idade);

        button_Salvar = (Button) findViewById(R.id.button_Database_Funcionario_Salvar);
        imageView_Galeria = (ImageView) findViewById(R.id.imageView_Database_Funcionario_Imagem);

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView_Database_Funcionario_Lista);


        imageView_LimparCampos.setOnClickListener(this);
        button_Salvar.setOnClickListener(this);
        imageView_Galeria.setOnClickListener(this);


        database = FirebaseDatabase.getInstance();
        storage = FirebaseStorage.getInstance();


        empresa = getIntent().getParcelableExtra("empresa");


        ativarFirebaseOffline();
        iniciarRecyclerView();


    }


    private void ativarFirebaseOffline() {


        try {
            if (!firebaseOffline) {

                FirebaseDatabase.getInstance().setPersistenceEnabled(true);

                firebaseOffline = true;

            } else {

                //firebase ja estiver funcionando offline
            }


        } catch (Exception e) {
            //erro
        }
    }


    //------------------------------------------INICIAR RECYCLER VIEW-----------------------------------------------


    private void iniciarRecyclerView() {


        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView_listaFuncionario = new RecyclerView_ListaFuncionario(getBaseContext(), funcionarios, this);
        recyclerView.setAdapter(recyclerView_listaFuncionario);


    }


    //------------------------------------------TRATAMENTO DE CLICKS-----------------------------------------------
    @Override
    public void onClick(View v) {


        switch (v.getId()) {

            case R.id.imageView_Database_Funcionario_LimparCampos:

                limparCampos();

                break;

            case R.id.button_Database_Funcionario_Salvar:


                buttonSalvar();

                break;

            case R.id.imageView_Database_Funcionario_Imagem:


                obterImagem_Galeria();

                break;

        }

    }


    private void buttonSalvar() {


        String nome = editText_Nome.getText().toString();

        String idade_String = editText_Idade.getText().toString();


        if (Util.verificarCampos(getBaseContext(), nome, idade_String)) {


            int idade = Integer.parseInt(idade_String);

            if (imagem_Selecionada) {

                salvarDadosStorage(nome, idade);

            } else {

                DialogAlerta alerta = new DialogAlerta("Imagem - Erro", "É obrigatorio escolher uma imagem para Salvar os dados do Funcionário");
                alerta.show(getSupportFragmentManager(), "1");
            }

        }


    }


    //-----------------------------------------------MENU-----------------------------------------------


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {


        getMenuInflater().inflate(R.menu.menu_database_lista_funcionario, menu);

        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {


        switch (item.getItemId()) {


            case R.id.item_esconder_layout:


                linearLayout.setVisibility(View.GONE);

                return true;

            case R.id.item_mostrar_layout:

                linearLayout.setVisibility(View.VISIBLE);

                return true;


           /* case R.id.item_criar_pdf_funcionarios:



                itemCriarPdf();


                return true;*/

        }


        return super.onOptionsItemSelected(item);
    }


    /*private void itemCriarPdf(){

        if(funcionarios.size() > 0){

            new GerarPDF().execute();

        }else{

            DialogAlerta alerta = new DialogAlerta("Erro ao gerar PDF","Não existem Funcionários para gerar o Relatório PDF");
            alerta.show(getSupportFragmentManager(),"1");
        }


    }*/


    @Override
    public void click_Funcionario(Funcionario funcionario) {


        funcionario.setId_empresa(empresa.getId());

        Intent intent = new Intent(getBaseContext(), DatabaseListaFuncionarioDadosActivity.class);

        intent.putExtra("funcionario", funcionario);

        startActivity(intent);


    }


    //---------------------------------------- OBTER IMAGENS ----------------------------------------------------------------


    private void obterImagem_Galeria() {


        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);


        startActivityForResult(Intent.createChooser(intent, "Escolha uma Imagem"), 0);

    }

    //---------------------------------------- REPOSTAS DE COMUNICACAO ----------------------------------------------------------------


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if (resultCode == RESULT_OK) {


            if (requestCode == 0) { // RESPOSTA DA GALERIA

                if (data != null) { // CONTEUDO DA ESCOLHA DA IMAGEM DA GALERIA

                    uri_imagem = data.getData();

                    Glide.with(getBaseContext()).asBitmap().load(uri_imagem).listener(new RequestListener<Bitmap>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Bitmap> target, boolean isFirstResource) {

                            Toast.makeText(getBaseContext(), "Erro ao carregar imagem", Toast.LENGTH_LONG).show();
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Bitmap resource, Object model, Target<Bitmap> target, DataSource dataSource, boolean isFirstResource) {

                            imagem_Selecionada = true;

                            return false;
                        }
                    }).into(imageView_Galeria);

                } else {

                    Toast.makeText(getBaseContext(), "Falha ao selecionar imagem", Toast.LENGTH_LONG).show();

                }
            }

        }
    }


    //----------------------------------------LIMPAR CAMPOS----------------------------------------------------------------


    private void limparCampos() {


        editText_Nome.setText("");
        editText_Idade.setText("");
        uri_imagem = null;
        imagem_Selecionada = false;

        imageView_Galeria.setImageResource(R.drawable.ic_galeria_24dp);


    }


    //---------------------------------------- SALVAR DADOS----------------------------------------------------------------


    private void salvarDadosStorage(final String nome, final int idade) {


        progress = new DialogProgress();
        progress.show(getSupportFragmentManager(), "2");


        StorageReference reference = storage.getReference().child("BD").child("empresas").child(empresa.getNome());

        final StorageReference nome_imagem = reference.child("CursoFirebase" + System.currentTimeMillis() + ".jpg");


        Glide.with(getBaseContext()).asBitmap().load(uri_imagem).apply(new RequestOptions().override(1024, 768))
                .listener(new RequestListener<Bitmap>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Bitmap> target, boolean isFirstResource) {

                        Toast.makeText(getBaseContext(), "Erro ao transformar imagem", Toast.LENGTH_LONG).show();

                        progress.dismiss();
                        return false;

                    }

                    @Override
                    public boolean onResourceReady(Bitmap resource, Object model, Target<Bitmap> target, DataSource dataSource, boolean isFirstResource) {


                        ByteArrayOutputStream bytes = new ByteArrayOutputStream();

                        resource.compress(Bitmap.CompressFormat.JPEG, 70, bytes);

                        ByteArrayInputStream inputStream = new ByteArrayInputStream(bytes.toByteArray());


                        try {
                            bytes.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }


                        UploadTask uploadTask = nome_imagem.putStream(inputStream);


                        uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {

                            @Override
                            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {


                                return nome_imagem.getDownloadUrl();
                            }


                        }).addOnCompleteListener(new OnCompleteListener<Uri>() {


                            @Override
                            public void onComplete(@NonNull Task<Uri> task) {


                                if (task.isSuccessful()) {


                                    progress.dismiss();

                                    Uri uri = task.getResult();

                                    String url_imagem = uri.toString();

                                    salvarDadosDatabase(nome, idade, url_imagem);

                                } else {

                                    progress.dismiss();

                                    Toast.makeText(getBaseContext(), "Erro ao realizar Upload - Storage", Toast.LENGTH_LONG).show();
                                }

                            }
                        });


                        return false;
                    }
                }).submit();

    }


    private void salvarDadosDatabase(String nome, int idade, String urlimagem) {


        progress = new DialogProgress();
        progress.show(getSupportFragmentManager(), "2");


        Funcionario funcionario = new Funcionario(nome, idade, urlimagem);


        DatabaseReference databaseReference = database.getReference().child("BD").child("Empresas").child(empresa.getId()).child("Funcionarios");


        databaseReference.push().setValue(funcionario).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {


                if (task.isSuccessful()) {

                    Toast.makeText(getBaseContext(), "Sucesso ao realizar Upload - Database", Toast.LENGTH_LONG).show();
                    progress.dismiss();

                } else {

                    Toast.makeText(getBaseContext(), "Erro ao realizar Upload - Database", Toast.LENGTH_LONG).show();
                    progress.dismiss();


                }

            }
        });
    }


    //------------------------------------------GERAR PDF----------------------------------------------------------------


    private void gerarPDF() throws DocumentException, IOException {


        ParcelFileDescriptor descriptor = null;
        OutputStream outputStream;
        File pdf = null;
        Uri uri = null;


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {

            ContentValues contentValues = new ContentValues();

            contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "application/pdf");
            contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, "RelatorioFuncionarios" + System.currentTimeMillis());
            contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS);

            ContentResolver resolver = getContentResolver();
            uri = resolver.insert(MediaStore.Downloads.getContentUri("external"), contentValues);
            descriptor = resolver.openFileDescriptor(uri, "rw");
            outputStream = new FileOutputStream(descriptor.getFileDescriptor());


        } else {

            File diretorio = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);

            String nomeArquivo = diretorio.getPath() + "/RelatorioFuncionarios" + System.currentTimeMillis() + ".pdf";
            pdf = new File(nomeArquivo);
            outputStream = new FileOutputStream(pdf);
        }


        Document document = new Document();


        PdfCreator event = new PdfCreator();

        PdfWriter writer = PdfWriter.getInstance(document, outputStream);

        writer.setBoxSize("box_a", new Rectangle(36, 54, 559, 788));
        writer.setPageEvent(event);


        document.open();

        Font font = new Font(Font.FontFamily.HELVETICA, 20, Font.BOLD);
        Font font_dados = new Font(Font.FontFamily.HELVETICA, 20, Font.NORMAL);


        Paragraph paragraph = new Paragraph("Relatório de Funcionarios", font);

        paragraph.setAlignment(Element.ALIGN_CENTER);
        document.add(paragraph);


        PdfPTable table = new PdfPTable(2);
        table.setWidthPercentage(100);
        table.setSpacingBefore(30f);
        table.setSpacingAfter(30f);


        for (Funcionario funcionario : funcionarios) {


            String dados = "Nome: " + funcionario.getNome() + "\n\nIdade: " + funcionario.getIdade();

            PdfPCell cell = new PdfPCell(new Paragraph(dados, font_dados));
            cell.setPadding(10);
            cell.setBorder(Rectangle.NO_BORDER);
            cell.setBorder(PdfPCell.NO_BORDER);

            table.addCell(cell);

        }

        document.add(table);


        document.close();
        outputStream.close();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {

            descriptor.close();
            visualizarPDF(pdf, uri);

        } else {
            visualizarPDF(pdf, uri);
        }


    }


    private void visualizarPDF(File pdf, Uri uri) {

        PackageManager packageManager = getPackageManager();

        Intent itent = new Intent(Intent.ACTION_VIEW);
        itent.setType("application/pdf");
        List<ResolveInfo> lista = packageManager.queryIntentActivities(itent, PackageManager.MATCH_DEFAULT_ONLY);

        if (lista.size() > 0) {

            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

            if (pdf == null) { // versao mais nova android

                intent.setDataAndType(uri, "application/pdf");

            } else { // versao mais antiga android

                Uri uri1 = FileProvider.getUriForFile(getBaseContext(), "com.example.mac.firebasecursods", pdf);
                intent.setDataAndType(uri1, "application/pdf");
            }

            startActivity(intent);
        } else {

            DialogAlerta dialogAlerta = new DialogAlerta("Erro ao Abrir PDF", "Não foi detectado nenhum leitor de PDF no seu Dispositivo.");
            dialogAlerta.show(getSupportFragmentManager(), "3");
        }
    }


    //---------------------------------------Ouvinte ---------------------------------------------------


    private void ouvinte() {


        // Query busca1 = database.getReference().child("BD").child("Empresas").child(empresa.getId()).
        //       child("Funcionarios").orderByChild("nome").equalTo("Flavio Augusto");

        //   Query busca1 = database.getReference().child("BD").child("Empresas").child(empresa.getId()).
        //      child("Funcionarios").orderByChild("nome").startAt("M");

        //  Query busca1 = database.getReference().child("BD").child("Empresas").child(empresa.getId()).
        //     child("Funcionarios").orderByChild("idade").startAt(50);


        // Query busca1 = database.getReference().child("BD").child("Empresas").child(empresa.getId()).
        //child("Funcionarios").orderByChild("idade").startAt(34).endAt(58);

        // Query busca1 = database.getReference().child("BD").child("Empresas").child(empresa.getId()).
        // child("Funcionarios").orderByChild("idade").endAt(77);


        reference_database = database.getReference().child("BD").child("Empresas").child(empresa.getId()).child("Funcionarios");


        if (childEventListener == null) {

            childEventListener = new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {


                    String key = dataSnapshot.getKey();

                    keys.add(key);

                    Funcionario funcionario = dataSnapshot.getValue(Funcionario.class);

                    funcionario.setId(key);

                    funcionarios.add(funcionario);


                    recyclerView_listaFuncionario.notifyDataSetChanged();

                }

                @Override
                public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {


                    String key = dataSnapshot.getKey();


                    int index = keys.indexOf(key);

                    Funcionario funcionario = dataSnapshot.getValue(Funcionario.class);

                    funcionario.setId(key);


                    funcionarios.set(index, funcionario);


                    recyclerView_listaFuncionario.notifyDataSetChanged();


                }

                @Override
                public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {


                    String key = dataSnapshot.getKey();

                    int index = keys.indexOf(key);

                    funcionarios.remove(index);

                    keys.remove(index);


                    recyclerView_listaFuncionario.notifyItemRemoved(index);
                    recyclerView_listaFuncionario.notifyItemChanged(index, funcionarios.size());


                }

                @Override
                public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            };


            //busca1.addChildEventListener(childEventListener);
            reference_database.addChildEventListener(childEventListener);
        }

    }


    //--------------------------------------------Ciclos de Vida------------------------------------

    @Override
    protected void onStart() {
        super.onStart();


        ouvinte();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();


        if (childEventListener != null) {

            reference_database.removeEventListener(childEventListener);
        }
    }


    private class GerarPDF extends AsyncTask<Void, Void, Void> {


        private DialogProgress dialogProgress;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            dialogProgress = new DialogProgress();
            dialogProgress.show(getSupportFragmentManager(), "2");
        }

        @Override
        protected Void doInBackground(Void... voids) {

            try {
                gerarPDF();
            } catch (DocumentException e) {
                e.printStackTrace();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }


        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            dialogProgress.dismiss();
        }


    }


}

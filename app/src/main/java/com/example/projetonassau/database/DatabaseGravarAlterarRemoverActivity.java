package com.example.projetonassau.database;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.projetonassau.R;
import com.example.projetonassau.util.DialogAlerta;
import com.example.projetonassau.util.DialogProgress;
import com.example.projetonassau.util.Util;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;


public class DatabaseGravarAlterarRemoverActivity extends AppCompatActivity implements View.OnClickListener {


    private EditText editText_NomePasta;
    private EditText editText_Nome;
    private EditText editText_Idade;

    private Button button_Salvar;
    private Button button_Alterar;
    private Button button_Remover;

    private FirebaseDatabase database;
    private boolean firebaseOffline = false;

    private DialogProgress progress;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.database_gravar_alterar_remover_activity);



        editText_NomePasta = (EditText)findViewById(R.id.editText_Database_GravarAlterarRemover_NomePasta);
        editText_Nome = (EditText)findViewById(R.id.editText_Database_GravarAlterarRemover_Nome);
        editText_Idade = (EditText)findViewById(R.id.editText_Database_GravarAlterarRemover_Idade);


        button_Salvar = (Button)findViewById(R.id.button_Database_GravarAlterarRemover_Salvar);
        button_Alterar = (Button)findViewById(R.id.button_Database_GravarAlterarRemover_Alterar);
        button_Remover = (Button)findViewById(R.id.button_Database_GravarAlterarRemover_Remover);


        button_Salvar.setOnClickListener(this);
        button_Alterar.setOnClickListener(this);
        button_Remover.setOnClickListener(this);




        database = FirebaseDatabase.getInstance();


        // ativarFirebaseOffline();


    }


    private void ativarFirebaseOffline(){


        try{
            if(!firebaseOffline){

                FirebaseDatabase.getInstance().setPersistenceEnabled(true);

                firebaseOffline = true;

            }else{

                //firebase ja estiver funcionando offline
            }


        }catch(Exception e){
            //erro
        }
    }





    //-----------------------------------------TRATAMENTO DE CLICKS------------------------------------------------

    @Override
    public void onClick(View v) {


        switch (v.getId()){

            case R.id.button_Database_GravarAlterarRemover_Salvar:


                buttonSalvar();

                break;


            case R.id.button_Database_GravarAlterarRemover_Alterar:

                buttonAlterar();


                break;


            case R.id.button_Database_GravarAlterarRemover_Remover:


                buttonRemover();

                break;

        }

    }

    //-----------------------------------------BUTTONS ONCLIK Salvar Alterar Remover------------------------------------------------


    private void buttonSalvar(){



        String nome = editText_Nome.getText().toString();

        String idade_String = editText_Idade.getText().toString();


        if(Util.verificarCampos(getBaseContext(),nome,idade_String)){


            int idade = Integer.parseInt(idade_String);


            salvarDados(nome,idade);

        }
    }




    private void buttonAlterar(){


        String nome = editText_Nome.getText().toString();

        String idade_String = editText_Idade.getText().toString();


        if(Util.verificarCampos(getBaseContext(),nome,idade_String)){


            int idade = Integer.parseInt(idade_String);


            alterarDados(nome,idade);

        }

    }




    private void buttonRemover(){


        String nome_Pasta = editText_NomePasta.getText().toString();


        if(!nome_Pasta.isEmpty()){


            removerDados(nome_Pasta);

        }else{


            DialogAlerta alerta = new DialogAlerta("Erro","Preencha o campo com o nome da Pasta que você quer remover");
            alerta.show(getSupportFragmentManager(),"1");
            //  Toast.makeText(getBaseContext(),"Preencha o campo com o nome da Pasta que você quer remover",Toast.LENGTH_LONG).show();

        }



    }






    //-----------------------------------------Salvar Alterar Remover------------------------------------------------


    private void salvarDados(String nome, int idade){

        progress = new DialogProgress();
        progress.show(getSupportFragmentManager(),"1");

        DatabaseReference reference = database.getReference().child("BD").child("Gerentes");


        Gerente gerente = new Gerente(nome,idade,false);


        reference.push().setValue(gerente).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if(task.isSuccessful()){

                    Toast.makeText(getBaseContext(),"Sucesso ao Gravar Dados",Toast.LENGTH_LONG).show();
                    progress.dismiss();

                }else{
                    Toast.makeText(getBaseContext(),"Erro ao Gravar Dados",Toast.LENGTH_LONG).show();

                    progress.dismiss();

                }

            }
        });

    }






    private void alterarDados(String nome, int idade){

        progress = new DialogProgress();
        progress.show(getSupportFragmentManager(),"1");


        String nome_Pasta = editText_NomePasta.getText().toString();


        if(!nome_Pasta.isEmpty()){


            DatabaseReference reference = database.getReference().child("BD").child("Gerentes");


            Gerente gerente = new Gerente(nome,idade,false);


            Map<String, Object> atualizacao = new HashMap<>();


            atualizacao.put(nome_Pasta,gerente);


            reference.updateChildren(atualizacao).addOnCompleteListener(new OnCompleteListener<Void>() {


                @Override
                public void onComplete(@NonNull Task<Void> task) {

                    if(task.isSuccessful()){

                        progress.dismiss();
                        Toast.makeText(getBaseContext(),"Sucesso ao Alterar Dados",Toast.LENGTH_LONG).show();

                    }else{
                        progress.dismiss();
                        Toast.makeText(getBaseContext(),"Erro ao Alterar Dados",Toast.LENGTH_LONG).show();

                    }


                }
            });
        }else{

            progress.dismiss();

            DialogAlerta alerta = new DialogAlerta("Erro","Preencha o campo com o nome da Pasta que você quer alterar");
            alerta.show(getSupportFragmentManager(),"1");
            // Toast.makeText(getBaseContext(),"Preencha o campo com o nome da Pasta que você quer alterar",Toast.LENGTH_LONG).show();

        }

    }




    private void removerDados(String nome_Pasta){

        progress = new DialogProgress();
        progress.show(getSupportFragmentManager(),"1");


        DatabaseReference reference = database.getReference().child("BD").child("Gerentes");


        reference.child(nome_Pasta).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {


            @Override
            public void onComplete(@NonNull Task<Void> task) {


                if(task.isSuccessful()){

                    progress.dismiss();
                    Toast.makeText(getBaseContext(),"Sucesso ao Remover Dados",Toast.LENGTH_LONG).show();

                }else{
                    progress.dismiss();
                    Toast.makeText(getBaseContext(),"Erro ao Remover Dados",Toast.LENGTH_LONG).show();

                }


            }
        });


    }

}

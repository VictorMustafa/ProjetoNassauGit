package com.example.projetonassau.database;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

import com.example.projetonassau.R;
import com.example.projetonassau.util.DialogAlerta;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class DatabaseLerDadosActivity extends AppCompatActivity {


    private TextView textView_Nome;
    private TextView textView_Idade;
    private TextView textView_Aluno;

    private TextView textView_Nome_2;
    private TextView textView_Idade_2;
    private TextView textView_Aluno_2;

    private FirebaseDatabase database;
    private ValueEventListener valueEventListener;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.database_ler_dados_activity);

        textView_Nome = (TextView) findViewById(R.id.textView_Database_LerDados_Nome);
        textView_Idade = (TextView) findViewById(R.id.textView_Database_LerDados_Idade);
        textView_Aluno = (TextView) findViewById(R.id.textView_Database_LerDados_Aluno);

        textView_Nome_2 = (TextView) findViewById(R.id.textView_Database_LerDados_Nome_2);
        textView_Idade_2 = (TextView) findViewById(R.id.textView_Database_LerDados_Idade_2);
        textView_Aluno_2 = (TextView) findViewById(R.id.textView_Database_LerDados_Aluno_2);

        database = FirebaseDatabase.getInstance();

        ouvinte_1();

    }


    //-------------------------------------Primeiro Ouvinte-----------------------------------

    private void ouvinte_1() {

        DatabaseReference reference = database.getReference().child("BD").child("Gerente").child("1");

        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                String nome = snapshot.child("Nome").getValue(String.class);
                int idade = snapshot.child("Idade").getValue(int.class);
                boolean aluno = snapshot.child("Aluno").getValue(boolean.class);


                DialogAlerta dialogAlerta = new DialogAlerta("Valor", nome + "\n" + idade + "\n" + aluno);
                dialogAlerta.show(getSupportFragmentManager(), "1");

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

}
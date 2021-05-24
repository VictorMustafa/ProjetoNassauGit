package com.example.projetonassau.database_lista_empresa;

import android.content.Intent;
import android.os.Handler;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.widget.Toast;

import com.example.projetonassau.R;
import com.example.projetonassau.database_lista_funcionario.DatabaseListaFuncionarioActivity;
import com.example.projetonassau.util.Util;
import com.example.projetonassau.util.Util;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class DatabaseListaEmpresaActivity extends AppCompatActivity implements RecyclerView_ListaEmpresa.ClickEmpresa,
        Runnable {


    private RecyclerView recyclerView;
    private FirebaseDatabase database;

    private RecyclerView_ListaEmpresa recyclerView_listaEmpresa;
    private List<Empresa> empresas = new ArrayList<Empresa>();

    private ChildEventListener childEventListener;
    private DatabaseReference reference_database;
    private List<String> keys = new ArrayList<String>();


    private boolean firebaseOffline = false;
    private Handler handler;
    private Thread thread;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.database_lista_empresa_activity);


        recyclerView = (RecyclerView) findViewById(R.id.recyclerView_Database_Empresa_Lista);


        database = FirebaseDatabase.getInstance();


        // conexaoFirebaseBD();


        handler = new Handler();
        thread = new Thread(this);
        thread.start();

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


    @Override
    public void run() {

        try {

            Thread.sleep(10000);

            handler.post(new Runnable() {

                @Override
                public void run() {

                    conexaoFirebaseBD();

                }
            });


        } catch (InterruptedException e) {

        }

    }


    private void conexaoFirebaseBD() {


        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference(".info/connected");


        databaseReference.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                boolean conexao = dataSnapshot.getValue(Boolean.class);


                if (conexao) {

                    Toast.makeText(getBaseContext(), "Temos conexao com o BD", Toast.LENGTH_LONG).show();


                } else {


                    if (Util.statusInternet(getBaseContext())) {

                        Toast.makeText(getBaseContext(), "BLOQUEIO AO NOSSO BD - ", Toast.LENGTH_LONG).show();

                    } else {


                        // Toast.makeText(getBaseContext(),"SEM CONEXAO COM A INTERNET",Toast.LENGTH_LONG).show();

                    }


                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }


    //---------------------------------------INICIAR RECYCLERVIEW------------------------------------------------
    private void iniciarRecyclerView() {


        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        recyclerView_listaEmpresa = new RecyclerView_ListaEmpresa(getBaseContext(), empresas, this);

        recyclerView.setAdapter(recyclerView_listaEmpresa);

    }


    //---------------------------------------CLICK NO ITEM------------------------------------------------

    @Override
    public void click_Empresa(Empresa empresa) {

        Intent intent = new Intent(getBaseContext(), DatabaseListaFuncionarioActivity.class);

        intent.putExtra("empresa", empresa);

        startActivity(intent);

        Toast.makeText(getBaseContext(),"Nome: "+empresa.getNome()+"\n\nPasta: "+empresa.getId(),Toast.LENGTH_LONG).show();

    }


    //---------------------------------------Ouvinte ---------------------------------------------------


    private void ouvinte() {


        reference_database = database.getReference().child("BD").child("Empresas");


        if (childEventListener == null) {

            childEventListener = new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {


                    String key = dataSnapshot.getKey();

                    keys.add(key);

                    Empresa empresa = dataSnapshot.getValue(Empresa.class);

                    empresa.setId(key);

                    empresas.add(empresa);

                    recyclerView_listaEmpresa.notifyDataSetChanged();


                    // keys 0 = 0

                    //empresas 0 = coca cola


                    // keys 1 = 1

                    //empresas 1 = pepsi


                }

                @Override
                public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {


                    String key = dataSnapshot.getKey();


                    int index = keys.indexOf(key);

                    Empresa empresa = dataSnapshot.getValue(Empresa.class);

                    empresa.setId(key);


                    empresas.set(index, empresa);


                    recyclerView_listaEmpresa.notifyDataSetChanged();


                }

                @Override
                public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {


                    String key = dataSnapshot.getKey();

                    int index = keys.indexOf(key);

                    empresas.remove(index);

                    keys.remove(index);


                    recyclerView_listaEmpresa.notifyItemRemoved(index);
                    recyclerView_listaEmpresa.notifyItemChanged(index, empresas.size());


                }

                @Override
                public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            };


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


}
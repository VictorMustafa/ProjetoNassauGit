package com.example.projetonassau;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.projetonassau.database.DatabaseGravarAlterarRemoverActivity;
import com.example.projetonassau.database.DatabaseLerDadosActivity;
import com.example.projetonassau.storage.StorageUploadActivity;
import com.example.projetonassau.util.Permissao;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.auth.FirebaseAuth;

import android.Manifest;
import android.widget.Toast;

public class PrincipalActivity extends AppCompatActivity implements View.OnClickListener {

    private CardView cardView_Storage_Upload;
    private CardView cardView_Database_LerDados;
    private CardView cardView_Database_GravarAlterarExcluir;
    private CardView cardView_Empresas;

    private Button button_Deslogar;
    private GoogleSignInClient googleSignInClient;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_principal);

        button_Deslogar = (Button) findViewById(R.id.button_Deslogar);
        button_Deslogar.setOnClickListener(this);

        cardView_Storage_Upload = (CardView) findViewById(R.id.cardView_Storage_Upload);
        cardView_Database_LerDados = (CardView) findViewById(R.id.cardView_Database_LerDados);
        cardView_Database_GravarAlterarExcluir = (CardView) findViewById(R.id.cardView_Database_GravarAlterarExcluir);
        cardView_Empresas = (CardView) findViewById(R.id.cardView_Empresas);


        cardView_Storage_Upload.setOnClickListener(this);
        cardView_Database_LerDados.setOnClickListener(this);
        cardView_Database_GravarAlterarExcluir.setOnClickListener(this);
        cardView_Empresas.setOnClickListener(this);

        permissao();

    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {


            //Botão de Upload
            case R.id.cardView_Storage_Upload:

                startActivity(new Intent(getBaseContext(), StorageUploadActivity.class));


                break;

            //Botão de LerDados
            case R.id.cardView_Database_LerDados:

                startActivity(new Intent(getBaseContext(), DatabaseLerDadosActivity.class));

                break;

            //Botão de GravarAlterarExcluir
            case R.id.cardView_Database_GravarAlterarExcluir:

                startActivity(new Intent(getBaseContext(), DatabaseGravarAlterarRemoverActivity.class));



                break;

            //Botão de Empresa
            case R.id.cardView_Empresas:

                Toast.makeText(this,"cardView_Empresas",Toast.LENGTH_LONG).show();


                break;

            //Botão de Deslogar
            case R.id.button_Deslogar:
                FirebaseAuth.getInstance().signOut();
                GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestIdToken(getString(R.string.default_web_client_id))
                        .requestEmail()
                        .build();
                googleSignInClient = GoogleSignIn.getClient(this, gso);

                googleSignInClient.signOut();
                finish();
                break;

        }

    }

    //______________________________Pemissão do Usuário______________________________________________

    private void permissao(){

        String permissoes [] = new String[] {

                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE,

        };

        Permissao.permissao(this, 0, permissoes);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        for(int result:grantResults){

            if (result == PackageManager.PERMISSION_DENIED){

                Toast.makeText(this, "Aceite as permissões para o aplicativo  funcionar corretamente", Toast.LENGTH_SHORT).show();
                finish();
                break;
            }
        }

    }
}
package com.example.projetonassau;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthActionCodeException;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Button button_Login, button_Cadastro;
    private CardView cardView_LoginGoogle;
    private FirebaseAuth auth;
    private FirebaseUser user;
    private GoogleSignInClient googleSignInClient;

    private FirebaseAuth.AuthStateListener authStateListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        button_Login = (Button) findViewById(R.id.button_Login);
        button_Cadastro = (Button) findViewById(R.id.button_Cadastro);

        cardView_LoginGoogle = (CardView) findViewById(R.id.cardView_LoginGoogle);


        button_Login.setOnClickListener(this);
        button_Cadastro.setOnClickListener(this);
        cardView_LoginGoogle.setOnClickListener(this);

        auth = FirebaseAuth.getInstance();

        servicosGoogle();
    }

    private void servicosGoogle() {

        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        googleSignInClient = GoogleSignIn.getClient(this, gso);

    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {

            case R.id.cardView_LoginGoogle:
                signInGoogle();


                break;

            case R.id.button_Login:
                startActivity(new Intent(this, LoginEmailActivity.class));
                break;

            case R.id.button_Cadastro:

                startActivity(new Intent(this, CadastrarActivity.class));
                break;
        }
    }

    private void signInGoogle() {

        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);

        if (account == null) {

            Intent intent = googleSignInClient.getSignInIntent();
            startActivityForResult(intent, 555);
        } else {

            //já existe alguém conectado com google
            Toast.makeText(getBaseContext(), "Conectado com sucesso!", Toast.LENGTH_LONG).show();
            startActivity(new Intent(getBaseContext(), PrincipalActivity.class));

        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 555) {

            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);

            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                startActivity(new Intent(getBaseContext(), PrincipalActivity.class));

            } catch (ApiException e) {

                Toast.makeText(getBaseContext(), "Erro ao logar com GOOGLE!", Toast.LENGTH_LONG).show();

            }
        }
    }
}
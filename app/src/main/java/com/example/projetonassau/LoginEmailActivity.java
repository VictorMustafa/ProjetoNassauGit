package com.example.projetonassau;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginEmailActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText editText_Email, editText_Senha;
    private Button button_Login, button_RecuperarSenha;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loginemail);

        editText_Email = (EditText) findViewById(R.id.editText_EmailLogin);
        editText_Senha = (EditText) findViewById(R.id.editText_SenhaLogin);
        button_Login = (Button) findViewById(R.id.button_OkLogin);
        button_RecuperarSenha = (Button) findViewById(R.id.button_Recuperar);

        button_Login.setOnClickListener(this);
        button_RecuperarSenha.setOnClickListener(this);

        auth = FirebaseAuth.getInstance();


    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {

            case R.id.button_OkLogin:

                loginEmail();
                break;

            case R.id.button_Recuperar:

                recuperarSenha();

                break;
        }

    }

    private void recuperarSenha() {

        String email = editText_Email.getText().toString().trim();

        if (email.isEmpty()) {

            Toast.makeText(getBaseContext(), "Favor informar um e-mail!", Toast.LENGTH_LONG).show();

        } else {

            enviarEmail(email);

        }

    }

    private void loginEmail() {

        String email = editText_Email.getText().toString().trim();
        String senha = editText_Senha.getText().toString().trim();


        if (email.isEmpty() || senha.isEmpty()) {

            Toast.makeText(getBaseContext(), "Favor preencher todos os campos!", Toast.LENGTH_LONG).show();

        } else {

            confirmarLoginEmail(email, senha);

        }


    }

    private void enviarEmail(String email) {

        auth.sendPasswordResetEmail(email).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {

                Toast.makeText(getBaseContext(), "E-mail de recuperação enviado!", Toast.LENGTH_LONG).show();

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                Toast.makeText(getBaseContext(), "Esse e-mail não está registrado em nossa plataforma!", Toast.LENGTH_LONG).show();

            }
        });

    }

    private void confirmarLoginEmail(String email, String senha) {

        auth.signInWithEmailAndPassword(email, senha).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {

            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if (task.isSuccessful()) {
                    startActivity(new Intent(getBaseContext(), PrincipalActivity.class));
                    Toast.makeText(getBaseContext(), "Usuário logado com sucesso!", Toast.LENGTH_LONG).show();
                    finish();

                } else {
                    Toast.makeText(getBaseContext(), "Usuário não cadastrado!", Toast.LENGTH_LONG).show();
                }

            }
        });

    }
}

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
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import org.w3c.dom.Text;

public class CadastrarActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText editText_Email, editText_Senha, editText_senhaRepetir;
    private Button button_Cadastrar, button_Cancelar;
    private FirebaseAuth auth;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastrar);

        editText_Email = (EditText) findViewById(R.id.editText_EmailCadastro);
        editText_Senha = (EditText) findViewById(R.id.editText_SenhaCadastro);
        editText_senhaRepetir = (EditText) findViewById(R.id.editText_ConfirmaSenhaCadastro);

        button_Cadastrar = (Button) findViewById(R.id.button_CadastrarUsuario);
        button_Cancelar = (Button) findViewById(R.id.button_Cancelar);

        button_Cadastrar.setOnClickListener(this);
        button_Cancelar.setOnClickListener(this);

        auth = FirebaseAuth.getInstance();
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {

            case R.id.button_CadastrarUsuario:

                cadastrar();

                break;

            case R.id.button_Cancelar:

                startActivity(new Intent(this, MainActivity.class));
                break;

        }

    }

    private void cadastrar() {

        String email = editText_Email.getText().toString().trim();
        String senha = editText_Senha.getText().toString().trim();
        String confirmaSenha = editText_senhaRepetir.getText().toString().trim();

        if (email.isEmpty() || senha.isEmpty() || confirmaSenha.isEmpty()) {

            Toast.makeText(getBaseContext(), "Favor preencher todos os campos!", Toast.LENGTH_LONG).show();
        } else {

            if (senha.contentEquals(confirmaSenha)) {

                criarUsuario(email, senha);

            } else {

                Toast.makeText(getBaseContext(), "Senhas não conferem!", Toast.LENGTH_LONG).show();

            }

        }

    }

    private void criarUsuario(String email, String senha) {

        auth.createUserWithEmailAndPassword(email, senha).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                boolean resultado = task.isSuccessful();

                if (resultado == true) {
                    Toast.makeText(getBaseContext(), "Cadastro efetuado com sucesso!", Toast.LENGTH_LONG).show();
                    Intent i = new Intent(CadastrarActivity.this, MainActivity.class);
                    startActivity(i);
                } else {
                    Toast.makeText(getBaseContext(), "Erro ao cadastrar", Toast.LENGTH_LONG).show();
                }
            }

        });
    }
}

package com.mathias.ifood.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.mathias.ifood.R;
import com.mathias.ifood.helper.ConfiguracaoFirebase;

public class AutenticacaoActivity extends AppCompatActivity {

    private Button botaoAcessar;
    private EditText campoEmail, campoSenha;
    private SwitchCompat tipoAcesso;

    private FirebaseAuth autenticacao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_autenticacao);
        getSupportActionBar().hide();

        inicializaComponentes();

        autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();
        //autenticacao.signOut();

        //verificar usuario logado
        verificaUsuarioLogado();


        botaoAcessar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = campoEmail.getText().toString();
                String senha = campoSenha.getText().toString();

                if(!email.isEmpty()) {
                    if(!senha.isEmpty()) {

                        //Verifica estado do Swich
                        if(tipoAcesso.isChecked()) {// cadastro
                            autenticacao.createUserWithEmailAndPassword(
                                    email,senha
                            ).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if(task.isSuccessful()) {
                                        Toast.makeText(AutenticacaoActivity.this, "Cadastro Realizado com sucesso", Toast.LENGTH_SHORT).show();
                                        abrirTelaPrincipal();

                                    }else {

                                        String erroExcecao = "";
                                        try {
                                            throw task.getException();

                                        } catch (FirebaseAuthWeakPasswordException e) {
                                            erroExcecao = "Digite uma senha mais forte!";
                                        } catch (FirebaseAuthInvalidCredentialsException e) {
                                            erroExcecao = "Por favor digite um e-mail valido";
                                        } catch (FirebaseAuthUserCollisionException e) {
                                            erroExcecao = "ESta conta já foi cadastrada";
                                        } catch (Exception e) {
                                            erroExcecao = "ao cadastrar usuário: " + e.getMessage();
                                            e.printStackTrace();
                                        }

                                        Toast.makeText(AutenticacaoActivity.this, "Erro: " + erroExcecao, Toast.LENGTH_SHORT).show();

                                    }
                                }
                            });

                        }else {//login
                            autenticacao.signInWithEmailAndPassword(
                                    email,senha
                            ).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if(task.isSuccessful()) {
                                        Toast.makeText(AutenticacaoActivity.this, "Logado com sucesso", Toast.LENGTH_SHORT).show();
                                        abrirTelaPrincipal();
                                    }else {
                                        Toast.makeText(AutenticacaoActivity.this, "Erro ao fazer login" + task.getException(), Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }

                    }else{
                        Toast.makeText(AutenticacaoActivity.this,
                                "Preencha  a senha",
                                Toast.LENGTH_SHORT).show();

                    }

                }else{
                    Toast.makeText(AutenticacaoActivity.this,
                            "Preencha o E-mail",
                            Toast.LENGTH_SHORT).show();

                }
            }
        });






    }

    private void verificaUsuarioLogado() {
        FirebaseUser usuarioAtual = autenticacao.getCurrentUser();
         if(usuarioAtual != null) {
           abrirTelaPrincipal();
         }
    }

    private void abrirTelaPrincipal() {
        startActivity(new Intent(getApplicationContext(),HomeActivity.class));
    }

    private void inicializaComponentes () {
        campoEmail = findViewById(R.id.editCadastroEmail);
        campoSenha = findViewById(R.id.editCadastroSenha);
        botaoAcessar = findViewById(R.id.buttonAcesso);
        tipoAcesso = findViewById(R.id.switchAcesso);
    }
}

package com.utmetropolitanda.kutanmayatraductor;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.*;
import android.os.Bundle;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    //defining view objects
    private EditText TextEmail;
    private EditText TextPassword;
    private Button btnIniciarsesion;
    private TextView TextForgot, TextRegistrar;
    private ProgressDialog progressDialog;

    //Declaramos un objeto firebaseAuth
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //inicializamos el objeto firebaseAuth
        firebaseAuth = FirebaseAuth.getInstance();

        //Referenciamos los views
        TextEmail = (EditText) findViewById(R.id.txtemail);
        TextPassword = (EditText) findViewById(R.id.txtcontrasenia);

        btnIniciarsesion = (Button) findViewById(R.id.botoniniciarsesion);
        TextForgot =(TextView) findViewById(R.id.txtolvidastepassword);
        TextRegistrar=(TextView) findViewById(R.id.txtregistrate);

        progressDialog = new ProgressDialog(this);

        //attaching listener to button
        btnIniciarsesion.setOnClickListener(this);

        TextRegistrar.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                startActivity(new Intent(MainActivity.this,RegistroUsuario.class));
            }
        });
    }
    private void iniciarSesion() {

        //Obtenemos el email y la contraseña desde las cajas de texto
        final String email = TextEmail.getText().toString().trim();
        String password = TextPassword.getText().toString().trim();

        //Verificamos que las cajas de texto no esten vacías
        if (TextUtils.isEmpty(email)) {
            Toast.makeText(this, "Ingrese su correo", Toast.LENGTH_LONG).show();
            return;
        }

        if (TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Ingrese una contraseña", Toast.LENGTH_LONG).show();
            return;
        }

        progressDialog.setMessage("Espere un momento...");
        progressDialog.show();

        //creating a new user
        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        //checking if success
                        if(task.isSuccessful()){


                            Toast.makeText(MainActivity.this,"Bienvenido"+ TextEmail.getText(),Toast.LENGTH_LONG).show();
                            Intent intent=new Intent(getApplication(),PrincipalActivity.class);
                            startActivity(intent);
                            finish();
                        }else{

                            Toast.makeText(MainActivity.this,"Email o contraseña no validos ",Toast.LENGTH_LONG).show();
                        }
                        progressDialog.dismiss();
                    }
                });
    }


    public void onClick(View view) {
        //Invocamos al método:
        switch (view.getId()){
            case R.id.botoniniciarsesion:
                iniciarSesion();
                break;
        }

    }

    @Override
    protected   void onStart(){
        super.onStart();
        if(firebaseAuth.getCurrentUser() !=null){
            startActivity(new Intent(MainActivity.this,PrincipalActivity.class));
            finish();
        }
    }

}

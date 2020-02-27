package com.utmetropolitanda.kutanmayatraductor;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.*;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;

public class RegistroUsuario extends AppCompatActivity {
    //Esto es para la foto de perfil
    Uri pickedImageURL;
    static int PReqCode=1;
    static int REQUESTCODE=1;
    private ImageView ImgUserPhoto;

    private EditText ETEmail;
    private EditText ETPassword;
    private EditText ETNombres;
    private EditText ETApellidos;
    private EditText ETOcupacion;
    private TextView txtNoregistrar;
    private Button BTNRegistrar;

    private ProgressBar loadingProcess;

    //Variables para almacenar los datos de los campos que necesitamos alv no mames ya me pase de lanza con este comentario bueno ya lo hago
    private String email="";
    private String password="";
    private String nombres="";
    private String apellidos="";
    private String ocupacion="";


    //Inicializamos las instacias de Firebase para que conecte esa onda
    FirebaseAuth mAuth; /*Este instancia como referencia la parte solo de autenticacion, ojo no la base de datos en tiempo real*/
    DatabaseReference mDatabase; /*Este es para que conecte a la bd en tiempo real porque el de arriba solo instancea el objeto de autenticacion simon*/


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro_usuario);

        //Inicializamos el la autenticacion de firebase
        mAuth= FirebaseAuth.getInstance();
        mDatabase= FirebaseDatabase.getInstance().getReference();

        //Referenciamos los campos del layout
        ETEmail=(EditText) findViewById(R.id.txtEmail);
        ETPassword=(EditText) findViewById(R.id.txtContraseña);
        ETNombres=(EditText) findViewById(R.id.txtNombres);
        ETApellidos=(EditText) findViewById(R.id.txtApellidos);
        ETOcupacion=(EditText) findViewById(R.id.txtOcupacion);
        BTNRegistrar=(Button) findViewById(R.id.btnRegistrar);
        ImgUserPhoto=(ImageView)findViewById(R.id.imgvUserPhoto);

        txtNoregistrar=(TextView) findViewById(R.id.txtvCancelar);
        loadingProcess=findViewById(R.id.regProgressbar);

        loadingProcess.setVisibility(View.INVISIBLE);


        //evento para regresar a la activity anterior
        txtNoregistrar.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                finish();
            }
        });

        //Clic seleccionar foto
        ImgUserPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Build.VERSION.SDK_INT >=22){
                    checkAndRequestForPermission();
                }
                else{
                    openGallery();
                }
            }
        });

        //evento para registrar al usuario nuevo
        BTNRegistrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                email=ETEmail.getText().toString();
                password=ETPassword.getText().toString();
                nombres=ETNombres.getText().toString();
                apellidos=ETApellidos.getText().toString();
                ocupacion=ETOcupacion.getText().toString();


                if(!nombres.isEmpty() && !apellidos.isEmpty() && !ocupacion.isEmpty() && !email.isEmpty() && !password.isEmpty()){
                    if(password.length() >=6){
                        RegistrarUs();
                    }
                    else {
                        Toast.makeText(RegistroUsuario.this, "La contraseña debe tener 6 o mas caracteres", Toast.LENGTH_SHORT).show();
                    }
                }
                else {
                    Toast.makeText(RegistroUsuario.this, "Llene todos los campos para registrarse", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    private void openGallery() {

        Intent gallery=new Intent(Intent.ACTION_GET_CONTENT);
        gallery.setType("image/*");
        startActivityForResult(gallery,REQUESTCODE);

    }
    private void checkAndRequestForPermission() {
        if(ContextCompat.checkSelfPermission(RegistroUsuario.this, Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
            if(ActivityCompat.shouldShowRequestPermissionRationale(RegistroUsuario.this, Manifest.permission.READ_EXTERNAL_STORAGE)){
                Toast.makeText(this, "Porfavor, conceder permisos de almacenamiento para subir foto", Toast.LENGTH_SHORT).show();
            }
            else {
                ActivityCompat.requestPermissions(RegistroUsuario.this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},PReqCode);
            }
        }
        else {
            openGallery();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode,Intent data){
        super.onActivityResult(requestCode,resultCode,data);

        if(resultCode==RESULT_OK && requestCode==requestCode && data !=null){
            pickedImageURL=data.getData();
            ImgUserPhoto.setImageURI(pickedImageURL);
        }
    }
    private void RegistrarUs(){

        mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful())
                {
                    final Map<String,Object>map =new HashMap<>();
                    map.put("email",email);
                    map.put("password",password);
                    map.put("nombres",nombres);
                    map.put("apellidos",apellidos);
                    map.put("ocupacion",ocupacion);

                    String id=mAuth.getCurrentUser().getUid();

                    mDatabase.child("Usuarios").child(id).setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task2) {
                            if(task2.isSuccessful()){


                                mAuth.signOut();
                                finish();
                                Toast.makeText(RegistroUsuario.this, "Usuario registrado con exito", Toast.LENGTH_SHORT).show();
                            }
                            else {
                                Toast.makeText(RegistroUsuario.this, "Ha ocurrido un error, intentelo mas tarde", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                }
                else {
                    Toast.makeText(RegistroUsuario.this, "Ocurrio un error durante el registro", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void SubirFoto(final FirebaseUser currentUser){
        StorageReference mStorage=FirebaseStorage.getInstance().getReference().child("fotos_perfil");
        final StorageReference imageFilePath =mStorage.child(pickedImageURL.getLastPathSegment());

        imageFilePath.putFile(pickedImageURL).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                //Recuperando url de la foto
                imageFilePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        UserProfileChangeRequest profileUpdate=new UserProfileChangeRequest.Builder()
                                .setPhotoUri(uri)
                                .build();
                        currentUser.updateProfile(profileUpdate)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        Toast.makeText(RegistroUsuario.this, "Foto Subida", Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }
                });
            }
        });

    }
}

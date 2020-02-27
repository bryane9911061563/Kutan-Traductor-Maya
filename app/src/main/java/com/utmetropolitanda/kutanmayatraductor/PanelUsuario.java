package com.utmetropolitanda.kutanmayatraductor;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.Image;
import android.net.Uri;
import android.nfc.TagLostException;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.*;

import android.os.Bundle;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.core.Tag;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;

import de.hdodenhof.circleimageview.CircleImageView;


public class PanelUsuario extends AppCompatActivity {
    private TextView TextVNombre;
    private TextView TextVApellidos;
    private TextView TextVOcupacion;
    private TextView TextVCorreo,CancelarAtras;
    private CircleImageView imgvFotoPerfil;
    private Button ActualizarInfo, BTN_CerrarSesion;
    private ProgressDialog mprogressDialog;

    //SubirFOTO
    String DISPLAY_NAME=null;
    String PROFILE_IMAGE_URL=null;
    int TAKE_IMAGE_CODE=10001;

    //Permiso Referencia a la bd
    private DatabaseReference mDatabase;
    private FirebaseDatabase FireDB;
    private FirebaseAuth mAuth;
    private StorageReference mStorage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_panel_usuario);

        TextVNombre=(TextView) findViewById(R.id.txtv_nombres);
        TextVApellidos=(TextView) findViewById(R.id.txtv_apellidos);
        TextVOcupacion=(TextView) findViewById(R.id.txtvOcupacion);
        TextVCorreo=(TextView) findViewById(R.id.txtv_correo);
        imgvFotoPerfil=(CircleImageView) findViewById(R.id.img_foto);
        ActualizarInfo=(Button) findViewById(R.id.btn_actualizarinfo);
        BTN_CerrarSesion=(Button) findViewById(R.id.btnCerrarsesion);
        CancelarAtras=(TextView)findViewById(R.id.txtvCancelar);

        mprogressDialog=new ProgressDialog(this);
        String id=mAuth.getInstance().getCurrentUser().getUid();


        mStorage= FirebaseStorage.getInstance().getReference();
        mAuth= FirebaseAuth.getInstance();

        FirebaseUser user =FirebaseAuth.getInstance().getCurrentUser();
        if(user !=null){
            if(user.getPhotoUrl()!=null){
                Glide.with(this)
                        .load(user.getPhotoUrl())
                        .into(imgvFotoPerfil);
            }
        }
        mDatabase= FirebaseDatabase.getInstance().getReference();

        //Abre la otra activity
        ActualizarInfo.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                startActivity(new Intent(PanelUsuario.this,ActualizarInfoUser.class));
            }
        });
        CancelarAtras.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                finish();
            }
        });
        imgvFotoPerfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleImageClick(v);
            }
        });
        BTN_CerrarSesion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.signOut();
                startActivity(new Intent(PanelUsuario.this,MainActivity.class));
                finish();
            }
        });





        //Obtiene los datos en el panel
        mDatabase.child("Usuarios").child(id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    String nombre=dataSnapshot.child("nombres").getValue().toString();
                    TextVNombre.setText(nombre);

                    String apellidos=dataSnapshot.child("apellidos").getValue().toString();
                    TextVApellidos.setText(apellidos);

                    String ocupacion=dataSnapshot.child("ocupacion").getValue().toString();
                    TextVOcupacion.setText(ocupacion);

                    String email=dataSnapshot.child("email").getValue().toString();
                    TextVCorreo.setText(email);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void handleImageClick(View view){
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (intent.resolveActivity(getPackageManager())!=null){
            startActivityForResult(intent,TAKE_IMAGE_CODE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data){
        super.onActivityResult(requestCode,resultCode,data);
        if(requestCode==TAKE_IMAGE_CODE){
            switch (resultCode){
                case RESULT_OK:
                    Bitmap bitmap= (Bitmap) data.getExtras().get("data");
                    imgvFotoPerfil.setImageBitmap(bitmap);
                    handleUpload(bitmap);
            }
        }
    }

    private void handleUpload(Bitmap bitmap) {
        ByteArrayOutputStream baos=new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,100, baos);

        String uid=FirebaseAuth.getInstance().getCurrentUser().getUid();
        final StorageReference reference=FirebaseStorage.getInstance().getReference().child("fotos_perfil").child(uid+".jpeg");
        reference.putBytes(baos.toByteArray()).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                getDownloadURL(reference);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(PanelUsuario.this, "Error al subir foto", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void getDownloadURL(StorageReference reference){
        reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Toast.makeText(PanelUsuario.this, "Success"+uri, Toast.LENGTH_SHORT).show();
                setUserProfileURL(uri);
            }
        });
    }

    private void setUserProfileURL(Uri uri){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        UserProfileChangeRequest request =new UserProfileChangeRequest.Builder().setPhotoUri(uri).build();
        user.updateProfile(request)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(PanelUsuario.this, "Actualizacion lista", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(PanelUsuario.this, "Fallo la carga de la imagen", Toast.LENGTH_SHORT).show();
            }
        });
    }
}

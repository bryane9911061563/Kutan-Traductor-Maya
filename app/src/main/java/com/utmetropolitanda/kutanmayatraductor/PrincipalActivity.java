package com.utmetropolitanda.kutanmayatraductor;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.annotation.NonNull;
import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaRecorder;
import android.speech.RecognizerIntent;
import android.util.Log;
import android.view.View;
import android.widget.*;
import android.database.sqlite.SQLiteDatabase;
import android.content.Context;


import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import android.os.Bundle;

import de.hdodenhof.circleimageview.CircleImageView;

public class PrincipalActivity extends AppCompatActivity {

    private static final  int REC_SPEECH__INPUT=100;

    //Instancias de los elementos
    private TextView txtUser, IDIOMA1,IDIOMA2;
    private ImageView btnCerrarSession, INVERTIR_IDIOMA;
    private ImageButton btnGrabar,btnUsuarioPanel,btnPictogramas;
    private CircleImageView FotoUsuario;
    private EditText TXT_Loquedices,TXT_Loquetraduce;
    //Permisos  de audio
    private String StoragePermission=Manifest.permission.READ_EXTERNAL_STORAGE;
    private String RecordPersmission= Manifest.permission.RECORD_AUDIO;
    private int PERMISSION_CODE=21;
    private boolean estagrabando=false,estainvertido=false;
    private MediaRecorder mediaRecorder;
    private  String recordFile;
    //Permiso Referencia a la bd
    private DatabaseReference mDatabase;
    private FirebaseDatabase FireDB;
    private FirebaseAuth mAuth;
    private StorageReference mStorage;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_principal);

        //bd local


        //-----

        mAuth=FirebaseAuth.getInstance();
        String id=mAuth.getInstance().getCurrentUser().getUid();

        mDatabase=FirebaseDatabase.getInstance().getReference();
        mDatabase.child("Usuarios").child(id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    String nombre=dataSnapshot.child("nombres").getValue().toString();
                    txtUser.setText(" Hola "+nombre+"!");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        //btnCerrarSession=(ImageView) findViewById(R.id.botoncerrarsesion);
        btnGrabar=(ImageButton) findViewById(R.id.imageview_botongrabar);
        txtUser=(TextView) findViewById(R.id.textNombreUs);
        btnUsuarioPanel=(ImageButton) findViewById(R.id.btnMipanel);
        FotoUsuario=(CircleImageView) findViewById(R.id.profile_image);
        TXT_Loquedices=(EditText) findViewById(R.id.editextLoquedices);
        TXT_Loquetraduce=(EditText) findViewById(R.id.editextLoquetraduce);
        INVERTIR_IDIOMA=(ImageView) findViewById(R.id.imgInvertirIdioma);
        IDIOMA1=(TextView) findViewById(R.id.txtIdioma1);
        IDIOMA2=(TextView) findViewById(R.id.txtIdioma2);
        btnPictogramas=(ImageButton) findViewById(R.id.boton_pictogramas);




        mStorage= FirebaseStorage.getInstance().getReference();
        mAuth= FirebaseAuth.getInstance();

        FirebaseUser user =FirebaseAuth.getInstance().getCurrentUser();
        if(user !=null){
            if(user.getPhotoUrl()!=null){
                Glide.with(this)
                        .load(user.getPhotoUrl())
                        .into(FotoUsuario);
            }
        }
        mDatabase= FirebaseDatabase.getInstance().getReference();

        INVERTIR_IDIOMA.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(estainvertido==false){
                    IDIOMA1.setText("Maya");
                    IDIOMA2.setText("Español");
                    estainvertido=true;
                }
                else {
                    IDIOMA1.setText("Español");
                    IDIOMA2.setText("Maya");
                    estainvertido=false;
                }
            }
        });
        FotoUsuario.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                startActivity(new Intent(PrincipalActivity.this,PanelUsuario.class));
            }
        });

        btnPictogramas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(PrincipalActivity.this,ActividadesyPictogramas.class));
            }
        });

        //Evento para grabar ya validado con los metodos anteriores
        btnGrabar.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                IniciarTranscripcion();

                /*if (estagrabando)
                {
                    stopRecording();
                    btnGrabar.setImageDrawable(getResources().getDrawable(R.drawable.fondoconmicrofono));
                    estagrabando=false;
                }else {
                    if(checkPermisions())
                    {
                        startRecording();
                        btnGrabar.setImageDrawable(getResources().getDrawable(R.drawable.fondoconmicrofonorecording));
                        estagrabando=true;
                    }

                }*/
            }
        });
        btnUsuarioPanel.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                startActivity(new Intent(PrincipalActivity.this,TraduccionesRecientes.class));
            }
        });

    }

    private void startRecording() {


        try {
            String recordPath=this.getExternalFilesDir("/").getAbsolutePath();
            SimpleDateFormat formatter=new SimpleDateFormat("yyyy_MM_dd_hh_mm_ss", Locale.CANADA);
            Date now=new Date();
            recordFile="traduccion_"+formatter.format(now)+".3gp";

            mediaRecorder =new MediaRecorder();
            mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            mediaRecorder.setOutputFile(recordPath+"/"+recordFile);
            mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            mediaRecorder.prepare();
        }
        catch (IOException e)
        {
            Log.d("tag","----------------------------------------------------------------------");
            e.printStackTrace();
            Log.d("tag",e.toString());
        }
        mediaRecorder.start();

    }
    private void stopRecording() {
        mediaRecorder.stop();
        mediaRecorder.release();
        mediaRecorder=null;

    }

    private  boolean checkPermisionsStorage()
    {
        if(ActivityCompat.checkSelfPermission(this, StoragePermission )== PackageManager.PERMISSION_GRANTED)
        {
            return true;
        }
        else {
            ActivityCompat.requestPermissions(this, new String[]{StoragePermission},PERMISSION_CODE);
            return false;
        }
    }
    private boolean checkPermisions() {
        if(ActivityCompat.checkSelfPermission(this, RecordPersmission )== PackageManager.PERMISSION_GRANTED)
        {
            return true;
        }
        else {
            ActivityCompat.requestPermissions(this, new String[]{RecordPersmission},PERMISSION_CODE);
            return false;
        }

    }

    private void IniciarTranscripcion(){
        Intent intent=new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,"Habla para empezar a traducir");
        try {
            startActivityForResult(intent,REC_SPEECH__INPUT);
        }
        catch (ActivityNotFoundException e){

        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
         super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case REC_SPEECH__INPUT:
                if(resultCode==RESULT_OK && null!=data){
                    ArrayList<String> result=data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    TXT_Loquedices.setText(result.get(0));
                    TraducirFirebase(TXT_Loquedices.getText().toString());


                    /*dbkutan.agregarTraduccionreciente(TXT_Loquedices.getText().toString(),TXT_Loquetraduce.getText().toString());
                    Toast.makeText(this, "Agregado a recientes", Toast.LENGTH_SHORT).show();*/
                }
                break;
        }
    }

    private void TraducirFirebase(String TextoEspan) {
        mDatabase.getDatabase().getInstance().getReference();
        //Object query =mDatabase.child("Frases").orderByChild("Texto").equalTo("me siento mejor");
       /* mDatabase.child("Frases").orderByChild("Texto").equalTo(TextoEspan).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.d("+++++++++++++",dataSnapshot.getValue().toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });*/
        mDatabase.child("Traduccion").orderByChild("EspañolMaya").equalTo("-M0c7JNXnrkP5mj3pV91").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.d("+++++++++++++",dataSnapshot.toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });/*
        mDatabase.child("Frases").child("el id obtenido de lo anterior").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.d("+++++++++++++",dataSnapshot.toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });*/
    }

   /* private void RegistrarTraduccion(){
        String id=mAuth.getInstance().getCurrentUser().getUid();
        dbLocalKutan traduc=new dbLocalKutan(this,"Traduccionesrecientes",null, 1);
        SQLiteDatabase Basededatos=traduc.getReadableDatabase();
        String textEspaniol=TXT_Loquedices.getText().toString();
        String textMaya=TXT_Loquetraduce.getText().toString();

        if(!textEspaniol.isEmpty() || !textMaya.isEmpty()){
            ContentValues registro=new ContentValues();
            registro.put("traduccionespan",textEspaniol);
            registro.put("traduccionmaya",textMaya);
            registro.put("_iduser",id);

            Basededatos.insert("Traducciones_recientes",null,registro);
            Basededatos.close();

        }
        else {
            Toast.makeText(this, "Ooy", Toast.LENGTH_SHORT).show();
        }
    }*/

    /*private void Buscar(View view){
        dbLocalKutan traducciones=new dbLocalKutan(this,"TraduccionesRecientes",null,1);
        SQLiteDatabase basededatos=traducciones.getWritableDatabase();


    }*/
}

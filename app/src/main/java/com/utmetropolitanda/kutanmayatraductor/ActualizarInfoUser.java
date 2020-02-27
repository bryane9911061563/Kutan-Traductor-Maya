package com.utmetropolitanda.kutanmayatraductor;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.*;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class ActualizarInfoUser extends AppCompatActivity {

    private EditText TXTV_Nombres,TXTV_Apellidos,TXTV_Ocupacion;
    private TextView TXTV_Cancelar;
    private Button BTN_Actualizar;

    //Permiso Referencia a la bd
    private DatabaseReference mDatabase;
    private FirebaseDatabase FireDB;
    private FirebaseAuth mAuth;

    //Strings
    private String nombres,apellidos,ocupacion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_actualizar_info_user);


        TXTV_Nombres=(EditText) findViewById(R.id.txtNombres);
        TXTV_Apellidos=(EditText) findViewById(R.id.txtApellidos);
        TXTV_Ocupacion=(EditText) findViewById(R.id.txtOcupacion);
        BTN_Actualizar=(Button) findViewById(R.id.btnRegistrar);
        TXTV_Cancelar=(TextView) findViewById(R.id.txtvCancelar);

        String id=mAuth.getInstance().getCurrentUser().getUid();
        mAuth= FirebaseAuth.getInstance();

        FirebaseUser user =FirebaseAuth.getInstance().getCurrentUser();
        mDatabase= FirebaseDatabase.getInstance().getReference();

        //Boton Atras
        TXTV_Cancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        BTN_Actualizar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Actualizar();
            }
        });



        //Obtiene los datos en el panel
        mDatabase.child("Usuarios").child(id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    String nombre=dataSnapshot.child("nombres").getValue().toString();
                    TXTV_Nombres.setText(nombre);

                    String apellidos=dataSnapshot.child("apellidos").getValue().toString();
                    TXTV_Apellidos.setText(apellidos);

                    String ocupacion=dataSnapshot.child("ocupacion").getValue().toString();
                    TXTV_Ocupacion.setText(ocupacion);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    //Metodo de actualizar datos
    public void Actualizar(){
        ObtenerDatosCampos();
        Map<String,Object> usuarioDatosMap=new HashMap<>();
        usuarioDatosMap.put("nombres",nombres);
        usuarioDatosMap.put("apellidos",apellidos);
        usuarioDatosMap.put("ocupacion",ocupacion);
        String id = mAuth.getInstance().getCurrentUser().getUid();

        mDatabase.child("Usuarios").child(id).updateChildren(usuarioDatosMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Toast.makeText(ActualizarInfoUser.this, "Datos actualizados", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    private void ObtenerDatosCampos() {
        nombres=TXTV_Nombres.getText().toString();
        apellidos=TXTV_Apellidos.getText().toString();
        ocupacion=TXTV_Ocupacion.getText().toString();
    }

}

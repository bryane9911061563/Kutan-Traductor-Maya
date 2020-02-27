package com.utmetropolitanda.kutanmayatraductor;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.*;

public class ActividadesyPictogramas extends AppCompatActivity {
    private RelativeLayout Tarjeta_salud,Tarjeta_legal,Tarjeta_trabajo,Tarjeta_otros;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_actividadesy_pictogramas);

        Tarjeta_salud=(RelativeLayout) findViewById(R.id.rlv_tarjeta1);
        Tarjeta_legal=(RelativeLayout) findViewById(R.id.rlv_tarjeta2);
        Tarjeta_trabajo=(RelativeLayout) findViewById(R.id.rlv_tarjeta3);
        Tarjeta_otros=(RelativeLayout) findViewById(R.id.rlv_tarjeta4);


        Tarjeta_salud.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ActividadesyPictogramas.this,Menu_Salud.class));
            }
        });
        Tarjeta_legal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        Tarjeta_trabajo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        Tarjeta_otros.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }
}

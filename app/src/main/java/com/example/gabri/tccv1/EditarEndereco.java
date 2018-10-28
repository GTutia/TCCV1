package com.example.gabri.tccv1;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class EditarEndereco extends AppCompatActivity {

    EditText et_endereco;
    Geocoder geocoder;
    List<Address> lista_endereco;
    Address endereco;
    double lat,lng;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar_endereco);

        et_endereco = findViewById(R.id.cadastro_endereco_casa);

        Button b_cadastro_endereco = (Button) findViewById(R.id.b_cadastro_endereco); // Informar estado
        b_cadastro_endereco.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                //Salvando no shared preferences
                SharedPreferences sharedPreferences = getSharedPreferences("endereco_casa", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("endereco_casa",et_endereco.getText().toString());
                editor.apply();

                //avisando ao usuário que foi cadastrado com sucesso
                Toast.makeText(v.getContext(), "Endereço atualizado!", Toast.LENGTH_LONG).show();

                //enviando endereço atualizado ao ESP32
                geocoder = new Geocoder(getBaseContext());
                try {
                    lista_endereco = geocoder.getFromLocationName(et_endereco.getText().toString(),1);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                endereco = lista_endereco.get(0);
                lat = endereco.getLatitude();
                lng = endereco.getLongitude();

                sharedPreferences = getSharedPreferences("endereco_casa_lat", Context.MODE_PRIVATE);
                editor = sharedPreferences.edit();
                editor.putString("endereco_casa_lat", String.valueOf(lat));
                editor.apply();

                sharedPreferences = getSharedPreferences("endereco_casa_long", Context.MODE_PRIVATE);
                editor = sharedPreferences.edit();
                editor.putString("endereco_casa_long", String.valueOf(lng));
                editor.apply();

                Intent intent = new Intent(getBaseContext(), MainActivity.class);// New activity
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();


            }
        });
    }
}

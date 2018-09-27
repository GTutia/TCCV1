package com.example.gabri.tccv1;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class EstadoPaciente extends AppCompatActivity {

    String str_telefone_emerg,str_ESP32, longitude, latitude; //localizacao_atual;
    double d_latitude,d_longitude;
    private LocationManager locationManager = null;
    private LocationListener locationListener = null;
    private FusedLocationProviderClient mFusedLocationClient;


    //Geocoder geocoder;
    //List<Address> addresses;

    @SuppressLint("MissingPermission")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_estado_paciente);

        SharedPreferences sharedPref = getSharedPreferences("telefone_contato", Context.MODE_PRIVATE);
        str_telefone_emerg = sharedPref.getString("telefone_contato", "sem_cadastro");

        sharedPref = getSharedPreferences("ESP32",Context.MODE_PRIVATE);
        str_ESP32 = sharedPref.getString("ESP32","ESP32_nao_conectado");


/*
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                longitude = " Longitude: " + String.valueOf(location.getLongitude());
                latitude = " Latitude: " + String.valueOf(location.getLatitude());
                d_latitude = location.getLatitude();
                d_longitude = location.getLongitude();

            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {


            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };



        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
        */

        /*



        geocoder = new Geocoder(this, Locale.getDefault());
        try {
            addresses = geocoder.getFromLocation(d_latitude,d_longitude,1);
            if(addresses!= null && addresses.size() > 0){

                localizacao_atual = addresses.get(0).getAddressLine(0);
                Toast.makeText(this, localizacao_atual, Toast.LENGTH_LONG).show();

            }
            else{
                Toast.makeText(this, "lista nula", Toast.LENGTH_LONG).show();

            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        */



        Button b_sim = (Button) findViewById(R.id.b_sim); // Botão de sim
        b_sim.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(str_ESP32.equals("ESP32_nao_conectado")){
                    Toast.makeText(v.getContext(), "Conecte ao seu dispositivo!", Toast.LENGTH_LONG).show();
                    Intent i = new Intent(v.getContext(), ConectarDispositivo.class);
                    startActivity(i);// Vai para Activity Editar Contato

                }
                sendSMS(str_ESP32,"Estou bem!");
                Toast.makeText(getBaseContext(),"Estado enviado!",Toast.LENGTH_LONG).show();
                Intent intent = new Intent(getBaseContext(), MainActivity.class);// New activity
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            }
        });

        Button b_nao = (Button) findViewById(R.id.b_nao); // Botão de não
        b_nao.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                if(str_telefone_emerg.equals("sem_cadatro")){

                    Toast.makeText(v.getContext(), "Cadastre um contato de emergência", Toast.LENGTH_LONG).show();
                    Intent i = new Intent(v.getContext(), EditarContato.class);
                    startActivity(i);// Vai para Activity Editar Contato

                }else{
                    mFusedLocationClient = LocationServices.getFusedLocationProviderClient(getBaseContext());
                    mFusedLocationClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            if(location!=null){
                                longitude = " Longitude: " + String.valueOf(location.getLongitude());
                                latitude = " Latitude: " + String.valueOf(location.getLatitude());
                                d_latitude = location.getLatitude();
                                d_longitude = location.getLongitude();
                                Log.d("LOCATION", String.valueOf(d_latitude));
                                Log.d("LOCATION", String.valueOf(d_longitude));
                                sendSMS(str_telefone_emerg, "Socorro! Estou em: http://maps.google.com/maps?q=" + d_latitude+ "," + d_longitude);

                            }
                        }
                    });
                    Toast.makeText(getBaseContext(),"Pedido de socorro enviado!",Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(getBaseContext(), MainActivity.class);// New activity
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    finish();
                }
            }
        });


    }

    private void sendSMS(String phoneNumber, String message) {

        SmsManager sms = SmsManager.getDefault();
        ArrayList<String> msgArray = sms.divideMessage(message);
        sms.sendMultipartTextMessage(phoneNumber,null,msgArray,null,null);

    }

}
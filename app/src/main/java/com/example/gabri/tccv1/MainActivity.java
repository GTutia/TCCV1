package com.example.gabri.tccv1;

import android.Manifest;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.Button;



public class MainActivity extends AppCompatActivity {


/*
    String[] perms = {"android.permission.FINE_LOCATION", "android.permission.RECEIVE_SMS"};
    int permsRequestCode = 200;

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 200: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }
*/

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //if(ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(this,Manifest.permission.RECEIVE_SMS) != PackageManager.PERMISSION_GRANTED){
          //  requestPermissions(perms,permsRequestCode);
        //}




      /*
        builder.setNegativeButton("cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });*/


        Button b_estado = (Button) findViewById(R.id.b_estado); // Informar estado
        b_estado.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent i = new Intent(v.getContext(), EstadoPaciente.class);
                startActivity(i);// Vai para Activity EstadoPaciente
            }
        });

        Button b_parametros = (Button) findViewById(R.id.b_parametros); // Parâmetros de Segurança
        b_parametros.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent i = new Intent(v.getContext(), ParametrosSeguranca.class);
                startActivity(i);// Vai para Activity ParâmetrosSeguranca
            }
        });

        Button b_conectar = (Button) findViewById(R.id.b_conectar);
        b_conectar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(v.getContext(), ConectarDispositivo.class);
                startActivity(i);// Vai para Activity ConectarDispositivo

            }
        });


        SharedPreferences sharedPref = getSharedPreferences("endereco_casa", Context.MODE_PRIVATE);
        String str_endereco = sharedPref.getString("endereco_casa",null);

        if(str_endereco == null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setMessage("Por favor adicione o endereço de sua casa").setTitle("Endereço de casa não cadastrado");
            builder.setPositiveButton("ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent i = new Intent(getBaseContext(), EditarEndereco.class);
                    startActivity(i);// Vai para Activity EstadoPaciente
                }
            });
            AlertDialog dialog = builder.create();
            dialog.show();
        }


        sharedPref = getSharedPreferences("telefone_contato", Context.MODE_PRIVATE);
        String str_telefone = sharedPref.getString("telefone_contato",null);
        if(str_telefone == null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setMessage("Por favor adicione o telefone do contato de emregência").setTitle("Telefone de emergência não cadastrado");
            builder.setPositiveButton("ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent i = new Intent(getBaseContext(), EditarContato.class);
                    startActivity(i);// Vai para Activity EstadoPaciente
                }
            });
            AlertDialog dialog = builder.create();
            dialog.show();
        }
    }
}

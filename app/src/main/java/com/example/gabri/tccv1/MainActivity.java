package com.example.gabri.tccv1;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.mobile.client.AWSMobileClient;
import com.amazonaws.mobile.client.AWSStartupHandler;
import com.amazonaws.mobile.client.AWSStartupResult;
import com.amazonaws.mobile.config.AWSConfiguration;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;


public class MainActivity extends AppCompatActivity {

    int PERMISSION_ALL = 1;
    String[] PERMISSIONS = {
            Manifest.permission.BLUETOOTH,
            Manifest.permission.BLUETOOTH_ADMIN,
            Manifest.permission.READ_SMS,
            Manifest.permission.SEND_SMS,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION
    };
    DynamoDBMapper dynamoDBMapper;
    int id;
    int intervalo = 1000*60*2;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL);

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
        String str_endereco = sharedPref.getString("endereco_casa", null);

        if (str_endereco == null) {
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
        String str_telefone = sharedPref.getString("telefone_contato", null);
        if (str_telefone == null) {
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

        //Monitoramento - DESCOMENTAR A PARTIR DAQUI
        /*

        AWSMobileClient.getInstance().initialize(this, new AWSStartupHandler() {
            @Override
            public void onComplete(AWSStartupResult awsStartupResult) {
                Log.d("YourMainActivity", "AWSMobileClient is instantiated and you are connected to AWS!");
            }
        }).execute();


        AWSCredentialsProvider credentialsProvider = AWSMobileClient.getInstance().getCredentialsProvider();
        AWSConfiguration configuration = AWSMobileClient.getInstance().getConfiguration();

        AmazonDynamoDBClient dynamoDBClient = new AmazonDynamoDBClient(credentialsProvider);

        this.dynamoDBMapper = DynamoDBMapper.builder()
                .dynamoDBClient(dynamoDBClient)
                .awsConfiguration(configuration)
                .build();

        final LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        final LocationListener[] locationListener = new LocationListener[1];
        id = recupera_ultimo_id();

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, intervalo, 10, locationListener[0] = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                registrarLocal(location.getLatitude(), location.getLongitude(), id);
                id++;
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
        });*/

    }

    @Override
    protected void onStop() {
        super.onStop();
        SharedPreferences sharedPreferences = getSharedPreferences("ultimo_id",Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("ultimo_id",id);
        editor.commit();
    }

    public void registrarLocal(Double lat, Double lng, double id) {

        final PacientesDO newLoc = new PacientesDO();
        newLoc.setUserId("0");
        newLoc.setLatitude(lat);
        newLoc.setLongitude(lng);
        newLoc.setLocation(id);

        Log.d("Location", "Location updated!");

        new Thread(new Runnable() {
            @Override
            public void run() {
                dynamoDBMapper.save(newLoc);
                // Item saved
            }
        }).start();
    }
    public int recupera_ultimo_id(){
        int id;
        SharedPreferences sharedPreferences = getSharedPreferences("ultimo_id",Context.MODE_PRIVATE);
        id = sharedPreferences.getInt("ultimo_id",0);
        return  id;
    }

}

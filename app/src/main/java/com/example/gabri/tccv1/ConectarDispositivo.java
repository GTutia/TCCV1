package com.example.gabri.tccv1;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class ConectarDispositivo extends AppCompatActivity {

    BluetoothDevice mDevice;
    BluetoothManager btManager;
    BluetoothAdapter btAdapter;
    BluetoothGatt bluetoothGatt;
    String chave, endereco_lat, endereco_long,numero_emerg;
    TextView tv_chave;
    String numero_ESP32;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conectar_dispositivo);

        SharedPreferences sharedPref = getSharedPreferences("telefone_contato", Context.MODE_PRIVATE);
        numero_emerg = sharedPref.getString("telefone_contato",null);

        sharedPref = getSharedPreferences("endereco_casa_lat", Context.MODE_PRIVATE);
        endereco_lat = sharedPref.getString("endereco_casa_lat",null);

        sharedPref = getSharedPreferences("endereco_casa_long",Context.MODE_PRIVATE);
        endereco_long = sharedPref.getString("endereco_casa_long",null);

        tv_chave = findViewById(R.id.et_chave);
        Button b_bluetooth = findViewById(R.id.b_bluetooth);

        b_bluetooth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chave = tv_chave.getText().toString();
                mDevice = encontrar_ESP();
                bluetoothGatt = mDevice.connectGatt(getBaseContext(),false,btleGattCallback);



            }
        });
    }

    BluetoothGattCallback btleGattCallback = new BluetoothGattCallback() {
        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            super.onServicesDiscovered(gatt, status);
            encontrar_service(gatt.getServices(), gatt);
            Log.d("OnServicesDiscovered","Entrei aqui");
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicRead(gatt, characteristic, status);
            Log.d("OnCharacteristicRead","Entrei aqui também");

            byte[] value_bt = null;
            String value = null;
            value_bt = characteristic.getValue();
            if(value_bt == null){
                Log.d("VALOR","valor nulo");
            }else{
                try {
                    value = new String(value_bt,"UTF-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                Log.d("VALOR",value);
            }
            numero_ESP32 = value;

            SharedPreferences sharedPreferences = getSharedPreferences("ESP32", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("ESP32",numero_ESP32);
            editor.commit();

            SmsManager sms = SmsManager.getDefault();
            ArrayList<String> msgArray = sms.divideMessage(chave + "," + numero_emerg + "," + endereco_lat + "," + endereco_long);
            sms.sendMultipartTextMessage(numero_ESP32,null,msgArray,null,null);

            runOnUiThread(new Runnable() {
                public void run() {
                    Toast.makeText(getBaseContext(), "Informações enviadas ao dispositivo", Toast.LENGTH_SHORT).show();
                }
            });

            gatt.disconnect();
        }

        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            super.onConnectionStateChange(gatt, status, newState);
            switch (newState){
                case 0:
                    Log.d("CONEXAO","Desconectado");

                    Intent intent = new Intent(getBaseContext(), MainActivity.class);// New activity
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    finish();
                    break;
                case 2:
                    Log.d("CONEXAO","Conectado");
                    gatt.discoverServices();
                    break;
                default:
                    Log.d("CONEXAO","Estranho");
                    break;
            }
        }
    };

    private BluetoothDevice encontrar_ESP() {
        BluetoothDevice device = null;
        btAdapter = BluetoothAdapter.getDefaultAdapter();
        Set<BluetoothDevice> pairedDevices = btAdapter.getBondedDevices();
        for(BluetoothDevice btd:pairedDevices){
            if(btd.getName().equals("MyESP32")){
                device = btd;
                Log.d("ENCONTRAR ESP","Encontrei");
                return device;
            }
        }
            return device;
    }

    private void encontrar_service(List<BluetoothGattService> services_list, BluetoothGatt gatt) {
        if(services_list == null) return ;

        for(BluetoothGattService service:services_list){
            List<BluetoothGattCharacteristic> characteristic_list = service.getCharacteristics();
            for(BluetoothGattCharacteristic characteristic:characteristic_list){
                if(characteristic.getUuid().toString().equals(chave)){
                    gatt.readCharacteristic(characteristic);
                    Log.d("ENCONTRAR SERVICE","Encontrei Service");
                    return;
                }
            }
        }
        return;
    }
}


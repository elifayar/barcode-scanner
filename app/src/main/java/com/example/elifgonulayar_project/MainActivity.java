package com.example.elifgonulayar_project;
import java.io.InputStreamReader;
import com.google.zxing.integration.android.IntentResult;
import com.google.zxing.integration.android.IntentIntegrator;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import android.os.Bundle;
import android.Manifest;
import android.content.Context;
import android.content.Intent;

public class MainActivity extends AppCompatActivity{

    Button scan;
    Button offline_Search;
    String barcode_Result;

    @Override
    protected void onCreate(Bundle savedInstanceState)  {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        barcode_Result = null;
        scan = findViewById(R.id.scan_Button);
        offline_Search = findViewById(R.id.offlineSearch_Button);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_DENIED)
        {
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.CAMERA}, 666);
        }


        scan.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View viewHolder)
            {
                startScanner();
            }
        });

        offline_Search.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View viewHolder)
            {
                if(barcode_Result != null)//!
                {
                    BufferedReader reader;

                    try{
                        final InputStream file = getAssets().open("marketdata.txt");
                        reader = new BufferedReader(new InputStreamReader(file));
                        String temp ="";
                        String line = reader.readLine();
                        while(line != null){

                            line = reader.readLine();
                            temp = line;
                            if(temp.contains(barcode_Result))
                            {
                                Toast.makeText(getApplicationContext(), "Found the product in local database!!!", Toast.LENGTH_LONG).show();
                                Toast.makeText(getApplicationContext(), line, Toast.LENGTH_LONG).show();
                                break;
                            }
                        }
                    } catch(IOException ioe){
                        ioe.printStackTrace();
                    }
                }
                else
                {
                    Toast.makeText(getApplicationContext(), "Please Scan Again!!!", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        String holder = null;
        if(result != null) {
            if(result.getContents() == null) {

                Toast.makeText(this, "Cancelled the scan!!!", Toast.LENGTH_LONG).show();
            } else
            {

                Toast.makeText(getApplicationContext(), "Scanned: " + result.getContents(), Toast.LENGTH_LONG).show();
                barcode_Result = result.getContents();

            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void startScanner()
    {
        IntentIntegrator integrator = new IntentIntegrator(this);
        integrator.setPrompt("Move your camera to the barcode!!!");
        integrator.setBeepEnabled(true);
        integrator.initiateScan();
    }

    public boolean checkConnection()
    {
        ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        if(connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
            return true;
        }
        else
            return false;
    }
}
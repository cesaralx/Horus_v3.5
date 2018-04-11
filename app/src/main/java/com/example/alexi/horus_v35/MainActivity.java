package com.example.alexi.horus_v35;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

public class MainActivity extends AppCompatActivity {

    ConnectionClass connectionClass;
    EditText edtuserid,edtpass;
    Button btnlogin, bntna, bntolvidar;
    ProgressBar pbbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Actions ac = new Actions();


        connectionClass = new ConnectionClass();
        edtuserid = (EditText) findViewById(R.id.edtuserid);
        edtpass = (EditText) findViewById(R.id.edtpass);
        btnlogin = (Button) findViewById(R.id.btnlogin);
        bntna = (Button) findViewById(R.id.bntNaccount);
        bntolvidar = (Button) findViewById(R.id.bntOlvidar);
        pbbar = (ProgressBar) findViewById(R.id.pbbar);
        pbbar.setVisibility(View.GONE);

        if (ac.readFromFile(MainActivity.this) != null){
            Intent i = new Intent(MainActivity.this, MainMenu.class); //aqui cambia de ventana
            startActivity(i);
            finish();
        }

        btnlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DoLogin doLogin = new DoLogin();
                doLogin.execute("");
            }
        });

        bntna.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, NewAccount.class); //aqui cambia de ventana
                startActivity(i);
            }
        });

        bntolvidar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, ForgetPassword.class); //aqui cambia de ventana
                startActivity(i);
            }
        });

    }

    public class DoLogin extends AsyncTask<String,String,String>
    {
        String z = "";
        Boolean isSuccess = false;


        String userid = edtuserid.getText().toString();
        String password = edtpass.getText().toString();


        @Override
        protected void onPreExecute() {
            pbbar.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(String r) {
            pbbar.setVisibility(View.GONE);
            Toast.makeText(MainActivity.this,r,Toast.LENGTH_SHORT).show();

            if(isSuccess) {
                Intent i = new Intent(MainActivity.this, MainMenu.class); //aqui cambia de ventana
                startActivity(i);
                finish();
            }

        }

        private void writeToFile(String data,Context context) {
            try {
                OutputStreamWriter outputStreamWriter = new OutputStreamWriter(context.openFileOutput("usr.txt", Context.MODE_PRIVATE));
                outputStreamWriter.write(data);
                outputStreamWriter.close();
            }
            catch (IOException e) {
                Log.e("Exception", "File write failed: " + e.toString());
            }
        }

         private String readFromFile(Context context) {

            String ret = "";

            try {
                InputStream inputStream = context.openFileInput("usr.txt");

                if (inputStream != null) {
                    InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                    BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                    String receiveString;
                    StringBuilder stringBuilder = new StringBuilder();

                    while ((receiveString = bufferedReader.readLine()) != null) {
                        stringBuilder.append(receiveString);
                    }

                    inputStream.close();
                    ret = stringBuilder.toString();
                }
            } catch (FileNotFoundException e) {
                Log.e("login activity", "File not found: " + e.toString());
            } catch (IOException e) {
                Log.e("login activity", "Can not read file: " + e.toString());
            }

            return ret;
        }

        @Override
        protected String doInBackground(String... params) {
            if(userid.trim().equals("")|| password.trim().equals(""))
                z = "Por favor ingrese usuario y contrasena";
            else
            {
                try {
                    Connection con = connectionClass.CONN();
                    if (con == null) {
                        z = "Error en conectar con el servidor\n por favor contacte al soporte tecnico";
                    } else {
                        String query = "select * from cliente where Usuario='" + userid + "' and Password='" + password + "'";
                        Statement stmt = con.createStatement();
                        ResultSet rs = stmt.executeQuery(query);

                        if(rs.next())
                        {

                            z = "Inicio correcto";
                            isSuccess=true;
                            writeToFile(userid,MainActivity.this);
                            readFromFile(MainActivity.this);
                        }
                        else
                        {
                            z = "Credenciales no validas";
                            isSuccess = false;
                        }

                    }
                }
                catch (Exception ex)
                {
                    isSuccess = false;
                    z = "Exceptions " + ex.getMessage();
                }
            }
            return z;
        }
    }
}
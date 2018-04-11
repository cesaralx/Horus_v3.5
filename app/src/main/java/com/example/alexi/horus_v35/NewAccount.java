package com.example.alexi.horus_v35;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.*;

import java.io.FileInputStream;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.*;
import java.time.*;
import java.util.Calendar;


public class NewAccount extends AppCompatActivity {

    ConnectionClass connectionClass;
    EditText email, password, nombre, usuario, telefono, direccion ;
    Button bntcreate, bntcancel;
    ProgressBar pbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_account);

        connectionClass = new ConnectionClass();
        email = (EditText) findViewById(R.id.txtEmail);
        password = (EditText) findViewById(R.id.txtPassword);
        nombre = (EditText) findViewById(R.id.txtNombre);
        usuario = (EditText) findViewById(R.id.txtUser);
        telefono = (EditText) findViewById(R.id.txtPhone);
        direccion = (EditText) findViewById(R.id.txtAdress);

        bntcreate = (Button) findViewById(R.id.bntCreate);
        bntcancel = findViewById(R.id.bntCancel);
        pbar = (ProgressBar) findViewById(R.id.pbar);
        pbar.setVisibility(View.GONE);

        bntcreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DoLogin doLogin = new DoLogin();
                doLogin.execute("");
            }
        });

        bntcancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

        public class DoLogin extends AsyncTask<String,String,String>
        {
            String z = "";
            Boolean isSuccess = false;

            String userid = usuario.getText().toString();
            String contra = password.getText().toString();
            String correo = email.getText().toString();
            String nom = nombre.getText().toString();
            String tel = telefono.getText().toString();
            String dir = direccion.getText().toString();


            @Override
            protected void onPreExecute() {
                pbar.setVisibility(View.VISIBLE);
            }

            @Override
            protected void onPostExecute(String r) {
                pbar.setVisibility(View.GONE);
                Toast.makeText(NewAccount.this,r,Toast.LENGTH_SHORT).show();

                if(isSuccess) {
                    Intent i = new Intent(NewAccount.this, MainActivity.class); //aqui cambia de ventana
                    startActivity(i);
                    finish();
                }

            }

            @Override
            protected String doInBackground(String... params) {
                if(userid.trim().equals("")|| contra.trim().equals(""))
                    z = "Por favor ingrese usuario y contrasena";
                else
                {
                    try {
                        Connection con = connectionClass.CONN();
                        if (con == null) {
                            z = "Error en conectar con el servidor\npor favor contacte al soporte tecnico";
                        } else {
                            PreparedStatement pst = con.prepareStatement("insert into cliente(ClienteNombre\n" +
                                    "      ,ClienteContNo\n" +
                                    "      ,ClienteDireccion\n" +
                                    "      ,Fecha\n" +
                                    "      ,Usuario\n" +
                                    "      ,Email, Password) values(?,?,?,?,?,?,?)");
                            pst.setString(1, nom);
                            pst.setString(2, tel);
                            pst.setString(3, dir);
                            java.sql.Timestamp date = new java.sql.Timestamp(new java.util.Date().getTime());
                            pst.setString(4, date.toString());
                            pst.setString(6, correo);
                            pst.setString(5, userid);
                            pst.setString(7, contra);

                           int rs =  pst.executeUpdate();

                            if(rs == 1)
                            {

                                z = "Cuenta creada!";
                                isSuccess=true;
                            }
                            else
                            {
                                z = "Ocurrio un problema";
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


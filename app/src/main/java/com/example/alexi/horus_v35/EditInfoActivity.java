package com.example.alexi.horus_v35;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class EditInfoActivity extends AppCompatActivity {

    ConnectionClass connectionClass;
    Actions ac = new Actions();

    TextView myName;
    TextView myEmail;
    TextView myTel;
    TextView myDir;
    Button bntcreate, bntcancel;

    //My values
    String Nombre, Email, Telefono, Direccion, userId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_info);

        connectionClass = new ConnectionClass();

        myName = (EditText)findViewById(R.id.txtNombre);
        myEmail = (EditText)findViewById(R.id.txtEmail);
        myTel = (EditText)findViewById(R.id.txtTel);
        myDir = (EditText)findViewById(R.id.txtDir);

        userId = ac.readFromFile(EditInfoActivity.this);

        bntcreate = (Button) findViewById(R.id.bntCreate);
        bntcancel = (Button)findViewById(R.id.bntCancel);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            Nombre = extras.getString("nombre");
            Email = extras.getString("email");
            Telefono = extras.getString("telefono");
            Direccion = extras.getString("direccion");
        }

        try {
                myName.setText(Nombre.toUpperCase());
                myTel.setText(Telefono.toUpperCase());
                myEmail.setText(Email.toUpperCase());
                myDir.setText(Direccion.toUpperCase());
        }
        catch (Exception ex) {
            Toast.makeText(EditInfoActivity.this, "Se genero un error en la conexion \n" + ex.getMessage(),
                    Toast.LENGTH_LONG).show();
        }


        bntcreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               if( updateInfo(userId).equals("Correcto")){

                   finish();
               }else{
                   Toast.makeText(EditInfoActivity.this, "Se genero un error en actualizar ",
                           Toast.LENGTH_LONG).show();
               }
            }
        });

        bntcancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private String updateInfo(String user){
        String z = null;
        try {
            Connection con = connectionClass.CONN();
            if (con == null) {
                z = "Error en conectar con el servidor\n por favor contacte al soporte tecnico";
            } else {

                PreparedStatement statement = con.prepareStatement("update cliente set ClienteNombre = ?," +
                        "ClienteContNo = ?," +
                        "Email = ?," +
                        "ClienteDireccion = ? where Usuario = ?");
                statement.setString(1, myName.getText().toString().toLowerCase());
                statement.setString(2, myTel.getText().toString().toLowerCase());
                statement.setString(3, myEmail.getText().toString().toLowerCase());
                statement.setString(4, myDir.getText().toString().toLowerCase());
                statement.setString(5, user.toLowerCase());


                int rs = statement.executeUpdate();

                if(rs != 0 )
                {
                    z = "Correcto";

                }
                else
                {
                    z = "Usuario no valido";
                }
            }
        }
        catch (Exception ex)
        {
            z = "Exceptions " + ex.getMessage();
            Log.e("SendMail", ex.getMessage(), ex);

        }

        return z;
    }

//    private String showInfo(String user){
//
//        String z = null;
//        try {
//            Connection con = connectionClass.CONN();
//            if (con == null) {
//                z = "Error en conectar con el servidor\n por favor contacte al soporte tecnico";
//            } else {
//
//                PreparedStatement statement = con.prepareStatement("select * from cliente where Usuario = ?");
//                statement.setString(1, user);
//                ResultSet rs = statement.executeQuery();
//
//                if(rs.next())
//                {
//                    Nombre = rs.getString("ClienteNombre");
//                    Telefono = rs.getString("ClienteContNo");
//                    Email = rs.getString("Email");
//                    Direccion = rs.getString("ClienteDireccion");
//
//                }
//                else
//                {
//                    z = "Usuario no valido";
//                }
//            }
//        }
//        catch (Exception ex)
//        {
//            z = "Exceptions " + ex.getMessage();
//        }
//        return z;
//    }


}

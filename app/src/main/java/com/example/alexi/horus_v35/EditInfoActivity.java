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

        userId = readFromFile(EditInfoActivity.this);

        bntcreate = (Button) findViewById(R.id.bntCreate);
        bntcancel = (Button)findViewById(R.id.bntCancel);



        try
        {
            String su = showInfo(userId);
            if (su != null){
                Toast.makeText(EditInfoActivity.this, "Error: " + su,
                        Toast.LENGTH_LONG).show();
            }else{
                myName.setText(Nombre.toUpperCase());
                myTel.setText(Telefono.toUpperCase());
                myEmail.setText(Email.toUpperCase());
                myDir.setText(Direccion.toUpperCase());
            }
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

//                Toast.makeText(EditInfoActivity.this, updateInfo(userId),
//                        Toast.LENGTH_LONG).show();
            }
        });

        bntcancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });



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
                        "ClienteDireccion = ?");
                statement.setString(1, myName.getText().toString().toLowerCase());
                statement.setString(2, myTel.getText().toString().toLowerCase());
                statement.setString(3, myEmail.getText().toString().toLowerCase());
                statement.setString(4, myDir.getText().toString().toLowerCase());

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
        }

        return z;
    }

    private String showInfo(String user){

        String z = null;
        try {
            Connection con = connectionClass.CONN();
            if (con == null) {
                z = "Error en conectar con el servidor\n por favor contacte al soporte tecnico";
            } else {

                PreparedStatement statement = con.prepareStatement("select * from cliente where Usuario = ?");
                statement.setString(1, user);
                ResultSet rs = statement.executeQuery();

                if(rs.next())
                {
                    Nombre = rs.getString("ClienteNombre");
                    Telefono = rs.getString("ClienteContNo");
                    Email = rs.getString("Email");
                    Direccion = rs.getString("ClienteDireccion");

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
        }
        return z;
    }
}

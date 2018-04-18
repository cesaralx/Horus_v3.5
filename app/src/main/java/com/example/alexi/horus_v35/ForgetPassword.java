package com.example.alexi.horus_v35;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.*;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Properties;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;

public class ForgetPassword extends AppCompatActivity implements View.OnClickListener{

    //Send button
    private Button buttonSend;
    private EditText editTextEmail;
    ConnectionClass connectionClass;
    String user=null, pass=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);

        //Initializing the views
        connectionClass = new ConnectionClass();

        editTextEmail = (EditText) findViewById(R.id.editTextEmail);
        buttonSend = (Button) findViewById(R.id.buttonSend);

        //Adding click listener
        buttonSend.setOnClickListener(this);
    }



    private String getusr(String email, Context context){
        String z = null;
        Boolean isSuccess = false;

        try {
            Connection con = connectionClass.CONN();
            if (con == null) {
                z = "Error en conectar con el servidor\n por favor contacte al soporte tecnico";
            } else {
                PreparedStatement statement = con.prepareStatement("select Usuario, Password from cliente where Email = ?");
                statement.setString(1, email);
                ResultSet rs = statement.executeQuery();

                if(rs.next())
                {
                    user = rs.getString("Usuario");
                    pass = rs.getString("Password");
                    isSuccess=true;
                }
                else
                {
                    z = "Email no valido";
                    isSuccess = false;
                }
            }
        }
        catch (Exception ex)
        {
            isSuccess = false;
            z = "Exceptions " + ex.getMessage();
        }
        return z;
    }


    private void sendEmail() {

        //Getting content for email
        String email = editTextEmail.getText().toString().trim();

        String subject = "Horus development support";
        String message =   "Hola no pudimos validar tu usuario ";

        String z = getusr(email, ForgetPassword.this);
        if( z != null){
            Toast.makeText(ForgetPassword.this, "Error "+z,
                    Toast.LENGTH_LONG).show();
        }else{
             message =   "Hola " + user + " tu password es " + pass;
        }



        try {
        //Creating SendMail object
        SendMail sm = new SendMail(this, email, subject, message);

        //Executing sendmail to send email
        sm.execute();
            Toast.makeText(ForgetPassword.this, "Se envio email con instrucciones",
                    Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            Log.e("SendMail", e.getMessage(), e);
        }
    }


    @Override
    public void onClick(View v) {
        try {
            sendEmail();
        } catch (Exception e) {
            e.printStackTrace();
        }
        Intent i = new Intent(ForgetPassword.this, MainActivity.class); //aqui cambia de ventana
        startActivity(i);
        finish();
    }

}

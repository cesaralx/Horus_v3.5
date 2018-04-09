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

    private String getpwd(String email, Context context){
        String z;
        Boolean isSuccess = false;

        try {
            Connection con = connectionClass.CONN();
            if (con == null) {
                z = "Error en conectar con el servidor\n por favor contacte al soporte tecnico";
            } else {
                String query = "select Password from usuario where EmailDireccion='" + email + "' ";
                Statement stmt = con.createStatement();
                ResultSet rs = stmt.executeQuery(query);

                if(rs.next())
                {
                    z = rs.getString("Password");
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

    private String getusr(String email, Context context){
        String z;
        Boolean isSuccess = false;

        try {
            Connection con = connectionClass.CONN();
            if (con == null) {
                z = "Error en conectar con el servidor\n por favor contacte al soporte tecnico";
            } else {
                String query = "select UsrName from usuario where EmailDireccion='" + email + "' ";
                Statement stmt = con.createStatement();
                ResultSet rs = stmt.executeQuery(query);

                if(rs.next())
                {
                    z = rs.getString("UsrName");
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
        String pwd = getpwd(email, ForgetPassword.this);
        String usr = getusr(email, ForgetPassword.this);
        String subject = "Horus development support";
        String message =   "Hola " + usr + " tu password es " + pwd;


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
        sendEmail();
        Intent i = new Intent(ForgetPassword.this, MainActivity.class); //aqui cambia de ventana
        startActivity(i);
    }

}

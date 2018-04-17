package com.example.alexi.horus_v35;

import android.content.Context;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class Actions {

    private ConnectionClass connectionClass = new ConnectionClass();

    private String user, email, nombre, telefono, direccion;

    private String date, accion;

    public Actions() {
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getAccion() {
        return accion;
    }

    public void setAccion(String accion) {
        this.accion = accion;
    }

    public String readFromFile(Context context) {

        String ret = null;

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
                user = stringBuilder.toString();
            }
        } catch (FileNotFoundException e) {
            Log.e("login activity", "File not found: " + e.toString());
        } catch (IOException e) {
            Log.e("login activity", "Can not read file: " + e.toString());
        }

        return ret;
    }


    public void writeToFileAny(String data,Context context, String filename) {
        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(context.openFileOutput(filename, Context.MODE_PRIVATE));
            outputStreamWriter.write(data);
            outputStreamWriter.close();
        }
        catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }
    }

    public String readFromFileAny(Context context, String filename) {

        String ret = null;

        try {
            InputStream inputStream = context.openFileInput(filename);

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


    public void writeToFileTemperature(String data,Context context) {
        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(context.openFileOutput("temp.txt", Context.MODE_PRIVATE));
            outputStreamWriter.write(data);
            outputStreamWriter.close();
        }
        catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }
    }

    public String readFromFileTemperature(Context context) {

        String ret = null;

        try {
            InputStream inputStream = context.openFileInput("temp.txt");

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


    //Elimina el archivo usr.txt
    public void deleteFile(Context context){
        File file = new File(context.getFilesDir(),"usr.txt");
        boolean deleted = file.delete();
    }

    public Boolean setHist(Context context, String action){
        Boolean isSuceess= null;
        readFromFile(context);

        try {
            Connection con = connectionClass.CONN();
            if (con == null) {
                isSuceess = false;
            } else {

                PreparedStatement statement = con.prepareStatement("insert into hist_cambios(clienteUser\n" +
                        "      ,action) values(?,?)");
                statement.setString(1, user);
                statement.setString(2, action);
                int rs =  statement.executeUpdate();

                if(rs == 1)
                {
                    isSuceess=true;
                }
                else
                {
                    isSuceess = false;
                }
            }
        }
        catch (Exception ex)
        { isSuceess = false; }
        return isSuceess;
    }



    public ItemData[] getHist(Context context){

        readFromFile(context);
        ItemData[] itemsData = null;
        Boolean isSuceess= null;

        try {
            Connection con = connectionClass.CONN();
            if (con == null) {
                isSuceess = false;
            } else {

                PreparedStatement statement = con.prepareStatement("select * from hist_cambios where clienteUser = ?",ResultSet.TYPE_SCROLL_INSENSITIVE,
                        ResultSet.CONCUR_READ_ONLY);
                statement.setString(1, user);
                ResultSet rs = statement.executeQuery();

                try {
                    rs.last();
                    itemsData = new ItemData[rs.getRow()];
                    rs.beforeFirst();
                }
                catch(Exception ex) {
                    ex.printStackTrace();
                }

                while(rs.next())
                {
//                    Log.e("Exception", "Numero de Row: " + rs.getRow());
                    itemsData[rs.getRow()] = new ItemData(rs.getString("action"),rs.getString("fecha"));
                    isSuceess=true;
                }
            }
        }
        catch (Exception ex)
        { isSuceess = false; }

//         itemsData[] = { new ItemData("Indigo","we"),
//                new ItemData("Red","we"),
//                new ItemData("Blue","we"),
//                new ItemData("Green","we"),
//                new ItemData("Amber","we"),
//                new ItemData("Deep Orange","we")};

        return itemsData;
    }


    public String setInfo(Context context){

        readFromFile(context);
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
                    setNombre( rs.getString("ClienteNombre"));
                    setTelefono( rs.getString("ClienteContNo"));
                    setDireccion( rs.getString("ClienteDireccion"));
                    setEmail( rs.getString("Email"));

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

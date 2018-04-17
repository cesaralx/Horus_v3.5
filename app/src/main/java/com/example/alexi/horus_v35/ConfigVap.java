package com.example.alexi.horus_v35;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

public class ConfigVap extends AppCompatActivity {

    Button buttonNew, buttonSalir, buttonLiquido, buttonSave;
    ConnectionClass connectionClass;
    String user=null;
    Actions ac;
    Spinner spinner1, spinner2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("Configurar Vap");
        setContentView(R.layout.activity_config_vap);
        ac = new Actions();

        //Initializing the views
        connectionClass = new ConnectionClass();

        buttonNew = (Button) findViewById(R.id.bntNewResis);
        buttonSalir = (Button) findViewById(R.id.bntSalirVap);
        buttonLiquido = (Button) findViewById(R.id.bntNewLiquido);
        spinner1 = (Spinner) findViewById(R.id.spinner);
        spinner2 = (Spinner) findViewById(R.id.spinner2);
        buttonSave = (Button) findViewById(R.id.bntGuardarC);

        setValues();


        //Adding click listener
        buttonNew.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                try
                {
                    Boolean newHist =  ac.setHist(ConfigVap.this,"Nueva Recistencia");
                    if(newHist){
                        Toast.makeText(ConfigVap.this, "Registro \nCorrecto!",
                                Toast.LENGTH_LONG).show();
                    }else{
                        Toast.makeText(ConfigVap.this, "Error!: No se puedo \nCapturar la nueva resistencia",
                                Toast.LENGTH_LONG).show();
                    }
                }
                catch (Exception ex) {
                    Toast.makeText(ConfigVap.this, "Error!: No se puedo contectar al servidor",
                            Toast.LENGTH_LONG).show();
                }
            }
        });

        buttonSalir.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                try
                {

                    finish();
                }
                catch (Exception ex) { }
            }
        });

        buttonSave.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                try
                {
                    int vaps = spinner1.getSelectedItemPosition();
                    int resi = spinner2.getSelectedItemPosition();
                    ac.writeToFileAny(String.valueOf(vaps), ConfigVap.this, "vap.txt");
                    ac.writeToFileAny(String.valueOf(resi), ConfigVap.this, "resi.txt");
                    Toast.makeText(ConfigVap.this, "Datos guardados!",
                            Toast.LENGTH_LONG).show();
                }
                catch (Exception ex) {
                    Toast.makeText(ConfigVap.this, "Error!: No se puedo contectar al servidor",
                            Toast.LENGTH_LONG).show();
                }
            }
        });

        buttonLiquido.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                try
                {
                    Boolean newHist =  ac.setHist(ConfigVap.this,"Nuevo Liquido");
                    if(newHist){
                        Toast.makeText(ConfigVap.this, "Registro \nCorrecto!",
                                Toast.LENGTH_LONG).show();
                    }else{
                        Toast.makeText(ConfigVap.this, "Error!: No se puedo \nCapturar el registro",
                                Toast.LENGTH_LONG).show();
                    }
                }
                catch (Exception ex) {
                    Toast.makeText(ConfigVap.this, "Error!: No se puedo contectar al servidor",
                            Toast.LENGTH_LONG).show();
                }
            }
        });


    }

    void setValues(){
        try {
            spinner1.setSelection(Integer.parseInt(ac.readFromFileAny(ConfigVap.this,"vap.txt")));
            spinner2.setSelection(Integer.parseInt(ac.readFromFileAny(ConfigVap.this,"resi.txt")));
        } catch (NumberFormatException e) {
            Toast.makeText(ConfigVap.this, "No existen archivos de configuracion guardados.",
                    Toast.LENGTH_LONG).show();
        }
    }

}

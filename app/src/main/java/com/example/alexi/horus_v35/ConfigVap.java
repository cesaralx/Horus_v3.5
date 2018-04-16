package com.example.alexi.horus_v35;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class ConfigVap extends AppCompatActivity {

    Button buttonNew, buttonSalir, buttonLiquido;
    ConnectionClass connectionClass;
    String user=null;
    Actions ac;

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
                catch (Exception ex) { }
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
                catch (Exception ex) { }
            }
        });


    }
}

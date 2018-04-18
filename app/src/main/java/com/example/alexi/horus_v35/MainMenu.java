package com.example.alexi.horus_v35;

import android.content.ClipData;
import android.content.DialogInterface;
import android.support.v4.app.Fragment;
import android.content.Intent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import static com.example.alexi.horus_v35.R.id.lblEmailNav;
import static com.example.alexi.horus_v35.R.id.lblUserNav;


public class MainMenu extends AppCompatActivity
        implements
        NavigationView.OnNavigationItemSelectedListener, Bluethoot.OnFragmentInteractionListener,
        Myinfo.OnFragmentInteractionListener, Home.OnFragmentInteractionListener,
        Products.OnFragmentInteractionListener{


    public FloatingActionButton fab;
    Actions ac = new Actions();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("Menu");
        setContentView(R.layout.activity_main_menu);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


         fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View innerview =  navigationView.getHeaderView(0);

        TextView user = (TextView) innerview.findViewById(R.id.lblUserNav);
        TextView email = (TextView) innerview.findViewById(R.id.lblEmailNav);


        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            email.setText( extras.getString("email"));
            user.setText( extras.getString("nombre"));
        }

        Fragment fragment = new Home();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.content_frame, fragment)
                .commit();
    }



    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
//        if (drawer.isDrawerOpen(GravityCompat.START)) {
//            drawer.closeDrawer(GravityCompat.START);
//        } else {
//            super.onBackPressed();
//        }

        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("Estas seguro que quieres salir");
        // alert.setMessage("Message");

        alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                //Your action here
                finish();
            }
        });

        alert.setNegativeButton("Cancelar",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                    }
                });

        alert.show();
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent i = new Intent(MainMenu.this, ConfigVap.class); //aqui cambia de ventana
            startActivity(i);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        //NOTE: creating fragment object
        boolean fragmentTransaction = false;
        Fragment fragment = null;

        if (id == R.id.nav_camera) {
            fragment = new Myinfo();
            fragmentTransaction = true;

        } else if (id == R.id.nav_gallery) {
            fragment = new Bluethoot();
            fragmentTransaction = true;

        } else if (id == R.id.nav_slideshow) {
            fragment = new Products();
            fragmentTransaction = true;

        } else if (id == R.id.nav_share) {
            ac.deleteFile(MainMenu.this);
            Intent i = new Intent(MainMenu.this, MainActivity.class); //aqui cambia de ventana
            startActivity(i);

            this.finish();
        } else if (id == R.id.nav_send) {
            this.finish();
        }
        else if (id == R.id.nav_main) {
            fragment = new Home();
            fragmentTransaction = true;
        }

        //NOTE: Fragment changing code
        if(fragmentTransaction) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.content_frame, fragment)
                    .commit();

            item.setChecked(true);
            getSupportActionBar().setTitle(item.getTitle());
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

}

 package com.example.alexi.horus_v35;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import java.sql.Statement;


 /**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link Myinfo.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link Myinfo#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Myinfo extends Fragment {

     Actions ac = new Actions();

    //My variables
    ConnectionClass connectionClass;
     TextView myName;
     TextView myEmail;
     TextView myTel;
     TextView myDir;

     //My values
     String Nombre, Email, Telefono, Direccion;


    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public Myinfo() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Myinfo.
     */
    // TODO: Rename and change types and number of parameters
    public static Myinfo newInstance(String param1, String param2) {
        Myinfo fragment = new Myinfo();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

    }

     @Override
     public void setUserVisibleHint(boolean visible)
     {
         super.setUserVisibleHint(visible);
         if (visible && isResumed())
         {
             onResume();
         }
     }

     @Override
     public void onResume()
     {
         super.onResume();
         if (!getUserVisibleHint())
         {
             return;
         }

         MainMenu mainActivity = (MainMenu)getActivity();
         int id = getResources().getIdentifier("com.example.alexi.horus_v35:drawable/ic_edit", null, null);

         mainActivity.fab.setImageResource(id);
         mainActivity.fab.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 //Do what you want
                 Toast.makeText(getActivity(), "Editar informacion usuario",
                         Toast.LENGTH_LONG).show();
//                 Snackbar.make(v, "Editar informacion usuario", Snackbar.LENGTH_LONG)
//                         .setAction("Action", null).show();

                 Intent i = new Intent(getActivity(), EditInfoActivity.class); //aqui cambia de ventana
                 i.putExtra("email", Email);
                 i.putExtra("nombre", Nombre);
                 i.putExtra("telefono", Telefono);
                 i.putExtra("direccion", Direccion);
                 startActivity(i);



             }
         });
     }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_myinfo, container, false);
        connectionClass = new ConnectionClass();

        myName = (TextView)view.findViewById(R.id.lblName);
        myEmail = (TextView)view.findViewById(R.id.lblEmail);
        myTel = (TextView)view.findViewById(R.id.lblPhone);
        myDir = (TextView)view.findViewById(R.id.lblDir);




        try
        {
            String su = showInfo(ac.readFromFile(getActivity()));
            if (su != null){
                Toast.makeText(getActivity(), "Error: " + su,
                        Toast.LENGTH_LONG).show();
            }else{
                myName.setText(Nombre.toUpperCase());
                myTel.setText(Telefono.toUpperCase());
                myEmail.setText(Email.toUpperCase());
                myDir.setText(Direccion.toUpperCase());
            }
        }
        catch (Exception ex) {
            Toast.makeText(getActivity(), "Se genero un error en la conexion \n" + ex.getMessage(),
                    Toast.LENGTH_LONG).show();
        }

        // Inflate the layout for this fragment
        return view;
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

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }




      void onBackPressed() {
         AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
         alert.setTitle("Do you want to logout?");
         // alert.setMessage("Message");

         alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
             public void onClick(DialogInterface dialog, int whichButton) {
                 //Your action here
             }
         });

         alert.setNegativeButton("Cancel",
                 new DialogInterface.OnClickListener() {
                     public void onClick(DialogInterface dialog, int whichButton) {
                     }
                 });

         alert.show();

     }

}

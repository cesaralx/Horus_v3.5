package com.example.alexi.horus_v35;

import android.content.Context;
import android.graphics.Rect;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Display;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Button;
import android.widget.Toast;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.DataPointInterface;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.jjoe64.graphview.series.OnDataPointTapListener;
import com.jjoe64.graphview.series.Series;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;
import android.widget.LinearLayout.LayoutParams;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link Bluethoot.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link Bluethoot#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Bluethoot extends Fragment {

    TextView myLabel, temp, resistencia, resStatus, tempLiquido, tempVap;
    BluetoothAdapter mBluetoothAdapter;
    BluetoothSocket mmSocket;
    BluetoothDevice mmDevice;
    OutputStream mmOutputStream;
    InputStream mmInputStream;
    Thread workerThread;
    byte[] readBuffer;
    int readBufferPosition;
    int counter;
    volatile boolean stopWorker;
    GraphView graph;
    Actions ac;
    Switch ocultar;
    ProgressBar pbarra;



    LinearLayout asthmaActionPlan, controlledMedication, asNeededMedication,
            rescueMedication, yourSymtoms, yourTriggers, wheezeRate, peakFlow, btmenu;
    LayoutParams params;
    LinearLayout next, prev;
    int viewWidth;
    GestureDetector gestureDetector = null;
    HorizontalScrollView horizontalScrollView;
    ArrayList<LinearLayout> layouts;
    int parentLeft, parentRight;
    int mWidth;
    int currPosition, prevPosition;


    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;


    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public Bluethoot() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Bluethoot.
     */
    // TODO: Rename and change types and number of parameters
    public static Bluethoot newInstance(String param1, String param2) {
        Bluethoot fragment = new Bluethoot();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
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
        assert mainActivity != null;
        int id = getResources().getIdentifier("horus.alexi.horus_v35:drawable/ic_bt", null, null);

        mainActivity.fab.setImageResource(id);
        mainActivity.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Toast.makeText(getActivity(), "Editar Vaporizador",
                        Toast.LENGTH_LONG).show();

                Intent i = new Intent(getActivity(), ConfigVap.class); //aqui cambia de ventana
//                i.putExtra("email", Email);
//                i.putExtra("nombre", Nombre);
//                i.putExtra("telefono", Telefono);
//                i.putExtra("direccion", Direccion);
                startActivity(i);

            }
        });
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    private final static int INTERVAL = 60000; //20000 para 30 segundos
    Handler mHandler = new Handler();
    Runnable mHandlerTask = new Runnable()
    {
        @Override
        public void run() {
            ac.writeToFileTemperature(temp.getText().toString(), getActivity());
//            String g = ac.readFromFileTemperature(getActivity());
//            Toast.makeText(getActivity() , "Se genero "+ g,
//                    Toast.LENGTH_LONG).show();
            mHandler.postDelayed(mHandlerTask, INTERVAL);
        }
    };

    void startRepeatingTask()
    {
        mHandlerTask.run();
    }

    void stopRepeatingTask()
    {
        mHandler.removeCallbacks(mHandlerTask);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_bluethoot, container, false);

        init(view);

        graph = (GraphView) view.findViewById(R.id.graph);
        graph.setVisibility(View.GONE);
        ac = new Actions();

        pbarra = (ProgressBar)view.findViewById(R.id.pbar);

        Button openButton = (Button)view.findViewById(R.id.open);
        Button closeButton = (Button)view.findViewById(R.id.close);
        Button graficaButton = (Button)view.findViewById(R.id.bntGrafica);
        myLabel = (TextView)view.findViewById(R.id.label);
        temp = (TextView)view.findViewById(R.id.txtTemp);
        resistencia = (TextView)view.findViewById(R.id.txtTemp2);
        resStatus = (TextView)view.findViewById(R.id.txtTemp3);
        tempLiquido = (TextView)view.findViewById(R.id.txtTemp4);
        tempVap = (TextView)view.findViewById(R.id.txtTemp5);
        ocultar = (Switch)view.findViewById(R.id.sw_ocultar);
        ocultar.setVisibility(View.GONE);

        // Replace 'android.R.id.list' with the 'id' of your RecyclerView
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recydates);
        mRecyclerView.setVisibility(View.GONE);





        ocultar.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                try
                {
                    checkOcultar();

                }
                catch (Exception ex) { }
            }
        });


        //Listeners
        //Open Button
        openButton.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                try
                {
                    findBT();
                    openBT();
                    startRepeatingTask();
                    horizontalScrollView.setVisibility(View.VISIBLE);
                    btmenu.setVisibility(View.VISIBLE);

                }
                catch (IOException ex) { }
            }
        });

        //Send Button
        graficaButton.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                try
                {
                    ItemData itemsData[] = ac.getHist(getActivity());

                    mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                    MyAdapter mAdapter = new MyAdapter(itemsData);
                    mRecyclerView.setAdapter(mAdapter);
                    mRecyclerView.setItemAnimator(new DefaultItemAnimator());

                    generateChart doLogin = new generateChart();
                    doLogin.execute("");
                }
                catch (Exception ex) { }
            }
        });

        //Close button
        closeButton.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                try
                {
                    stopRepeatingTask();
                    closeBT();
                }
                catch (Exception ex) { }
            }
        });

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Handler().postDelayed(new Runnable() {
                    public void run() {
                        horizontalScrollView.smoothScrollTo(
                                (int) horizontalScrollView.getScrollX()
                                        + viewWidth,
                                (int) horizontalScrollView.getScrollY());
                    }
                }, 100L);
            }
        });

        prev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Handler().postDelayed(new Runnable() {
                    public void run() {
                        horizontalScrollView.smoothScrollTo(
                                (int) horizontalScrollView.getScrollX()
                                        - viewWidth,
                                (int) horizontalScrollView.getScrollY());
                    }
                }, 100L);
            }
        });

        horizontalScrollView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (gestureDetector.onTouchEvent(event)) {
                    return true;
                }
                return false;
            }
        });

        // Inflate the layout for this fragment
        return view;
    }

    class MyGestureDetector extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
                               float velocityY) {
            if (e1.getX() < e2.getX()) {
                currPosition = getVisibleViews("left");
            } else {
                currPosition = getVisibleViews("right");
            }

            horizontalScrollView.smoothScrollTo(layouts.get(currPosition)
                    .getLeft(), 0);
            return true;
        }
    }

    public int getVisibleViews(String direction) {
        Rect hitRect = new Rect();
        int position = 0;
        int rightCounter = 0;
        for (int i = 0; i < layouts.size(); i++) {
            if (layouts.get(i).getLocalVisibleRect(hitRect)) {
                if (direction.equals("left")) {
                    position = i;
                    break;
                } else if (direction.equals("right")) {
                    rightCounter++;
                    position = i;
                    if (rightCounter == 2)
                        break;
                }
            }
        }
        return position;
    }

    void init(View view){
        prev = (LinearLayout) view.findViewById(R.id.prev);
        next = (LinearLayout) view.findViewById(R.id.next);
        horizontalScrollView = (HorizontalScrollView) view.findViewById(R.id.hsv);
        horizontalScrollView.setVisibility(View.GONE);
//        gestureDetector = new GestureDetector(new MyGestureDetector());
        gestureDetector = new GestureDetector(getActivity(), new MyGestureDetector());
        asthmaActionPlan = (LinearLayout) view.findViewById(R.id.asthma_action_plan);
        controlledMedication = (LinearLayout) view.findViewById(R.id.controlled_medication);
        asNeededMedication = (LinearLayout) view.findViewById(R.id.as_needed_medication);
        rescueMedication = (LinearLayout) view.findViewById(R.id.rescue_medication);
        yourSymtoms = (LinearLayout) view.findViewById(R.id.your_symptoms);
        yourTriggers = (LinearLayout) view.findViewById(R.id.your_triggers);
        wheezeRate = (LinearLayout) view.findViewById(R.id.wheeze_rate);
        peakFlow = (LinearLayout) view.findViewById(R.id.peak_flow);
        btmenu = (LinearLayout) view.findViewById(R.id.banex);
        btmenu.setVisibility(View.GONE);

        Display display = getActivity().getWindowManager().getDefaultDisplay();
        mWidth = display.getWidth(); // deprecated
        viewWidth = mWidth / 3;
        layouts = new ArrayList<LinearLayout>();
        params = new LayoutParams(viewWidth, LayoutParams.WRAP_CONTENT);

        asthmaActionPlan.setLayoutParams(params);
        controlledMedication.setLayoutParams(params);
        asNeededMedication.setLayoutParams(params);
        rescueMedication.setLayoutParams(params);
        yourSymtoms.setLayoutParams(params);
        yourTriggers.setLayoutParams(params);
        wheezeRate.setLayoutParams(params);
        peakFlow.setLayoutParams(params);

        layouts.add(asthmaActionPlan);
        layouts.add(controlledMedication);
        layouts.add(asNeededMedication);
        layouts.add(rescueMedication);
        layouts.add(yourSymtoms);
        layouts.add(yourTriggers);
        layouts.add(wheezeRate);
        layouts.add(peakFlow);
    }

    void checkOcultar(){
        if(ocultar.isChecked()){
            graph.setVisibility(View.GONE);
            mRecyclerView.setVisibility(View.GONE);
        }else{
            graph.setVisibility(View.VISIBLE);
            mRecyclerView.setVisibility(View.VISIBLE);
        }

    }

    // my functions

    void findBT()
    {
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if(mBluetoothAdapter == null)
        {
            myLabel.setText("No bluetooth adapter available");
            // Show proper message here
//            finish();
        }

        if(!mBluetoothAdapter.isEnabled())
        {
            Intent enableBluetooth = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBluetooth, 0);
        }

        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
        if(pairedDevices.size() > 0)
        {
            for(BluetoothDevice device : pairedDevices)
            {
                if(device.getName().equals("ElChido"))
                {
                    mmDevice = device;
                    break;
                }
            }
        }
        myLabel.setText("Bluetooth Device Found");
    }

    void openBT() throws IOException
    {
        try {
            UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"); //Standard SerialPortService ID
            mmSocket = mmDevice.createRfcommSocketToServiceRecord(uuid);
            mmSocket.connect();
            mmOutputStream = mmSocket.getOutputStream();
            mmInputStream = mmSocket.getInputStream();
        } catch (Exception e) {
            e.printStackTrace();
        }

        beginListenForData();



        myLabel.setText("Bluetooth Opened");
    }

    void beginListenForData()
    {
        final Handler handler = new Handler();
        final byte delimiter = 10; //This is the ASCII code for a newline character

        stopWorker = false;
        readBufferPosition = 0;
        readBuffer = new byte[1024];
        workerThread = new Thread(new Runnable()
        {
            public void run()
            {
                while(!Thread.currentThread().isInterrupted() && !stopWorker)
                {
                    try
                    {
                        int bytesAvailable = mmInputStream.available();
                        if(bytesAvailable > 0)
                        {
                            byte[] packetBytes = new byte[bytesAvailable];
                            mmInputStream.read(packetBytes);
                            for(int i=0;i<bytesAvailable;i++)
                            {
                                byte b = packetBytes[i];
                                if(b == delimiter)
                                {
                                    byte[] encodedBytes = new byte[readBufferPosition];
                                    System.arraycopy(readBuffer, 0, encodedBytes, 0, encodedBytes.length);
                                    final String data = new String(encodedBytes, "US-ASCII");
                                    readBufferPosition = 0;

                                    handler.post(new Runnable()
                                    {
                                        public void run()
                                        {
                                            double value = Double.parseDouble(data);
                                            double val;
                                            myLabel.setText(data);
                                            temp.setText(data); //Temperatura
                                            val = (4*470)-value;
                                            resistencia.setText(String.valueOf(val)); //Resistencia

                                            resStatus.setText(checkStatus(val)); //Status resistencia
                                            tempLiquido.setText(data); //Temperatura liquido
                                            tempVap.setText(data); //Temperatura vapor
                                        }
                                    });
                                }
                                else
                                {
                                    readBuffer[readBufferPosition++] = b;
                                }
                            }
                        }
                    }
                    catch (Exception ex)
                    {
                        stopWorker = true;
                    }
                }
            }
        });

        workerThread.start();
    }

    String checkStatus(Double temp){
        String res=null;

        if ( temp >= 1840 && temp <= 1960 ) {
            res ="Nuevo";
        }
        else if ( temp >= 1810 && temp <= 1840 ){
            res = "Bueno";
        }
        else if ( temp >= 1800 && temp <= 1810 ){
            res = "Cerca de cambio";
        }
        else {
            res = "Fuera de rango";
        }

        return res;
    }

//    void sendData() throws IOException
//    {
//        String msg = myTextbox.getText().toString();
//        msg += "\n";
//        mmOutputStream.write(msg.getBytes());
//        myLabel.setText("Data Sent");
//    }

    void closeBT() throws IOException
    {
        stopWorker = true;
        mmOutputStream.close();
        mmInputStream.close();
        mmSocket.close();
        myLabel.setText("Bluetooth Closed");
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

    public class generateChart extends AsyncTask<String,String,String>
    {
        String z = "";
        Boolean isSuccess = false;


//        String userid = edtuserid.getText().toString();
//        String password = edtpass.getText().toString();


        @Override
        protected void onPreExecute() {

            graph.setVisibility(View.VISIBLE);
            mRecyclerView.setVisibility(View.VISIBLE);
            ocultar.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(String r) {
//            graph.setVisibility(View.GONE);
            Toast.makeText(getActivity(),r,Toast.LENGTH_SHORT).show();

            if(isSuccess) {
              //setlabels();
                //aqui hacer algo si pasa
               gen();
            }
        }

        void gen(){
            try {

                LineGraphSeries<DataPoint> series = new LineGraphSeries<>(new DataPoint[] {
                        new DataPoint(0, 1),
                        new DataPoint(1, 2),
                        new DataPoint(2, 3),
                        new DataPoint(3, 2),
                        new DataPoint(4, 8),
                        new DataPoint(6,5)
                });
                graph.addSeries(series);
                series.setOnDataPointTapListener(new OnDataPointTapListener() {
                    @Override
                    public void onTap(Series series, DataPointInterface dataPoint) {
                        Toast.makeText(getActivity(), "Info. en punto seleccionado: "+dataPoint, Toast.LENGTH_SHORT).show();
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }

        }


        void setlabels(){
            if (ac.setInfo(getActivity()) != null){
                Toast.makeText(getActivity() , "Se genero un error en la conexion ",
                        Toast.LENGTH_LONG).show();
            }else {

//                email = ac.getEmail();
//                nombre = ac.getNombre();
            }
        }


        @Override
        protected String doInBackground(String... params) {
//            if(userid.trim().equals("")|| password.trim().equals(""))
//                z = "Por favor ingrese usuario y contrasena";
//            else
//            {
//                try {
//                    Connection con = connectionClass.CONN();
//                    if (con == null) {
//                        z = "Error en conectar con el servidor\n por favor contacte al soporte tecnico";
//                    } else {
//                        String query = "select * from cliente where Usuario='" + userid + "' and Password='" + password + "'";
//                        Statement stmt = con.createStatement();
//                        ResultSet rs = stmt.executeQuery(query);
//
//                        if(rs.next())
//                        {
//
//                            z = "Inicio correcto";
//                            isSuccess=true;
//                            writeToFile(userid,MainActivity.this);
//                            readFromFile(MainActivity.this);
//                        }
//                        else
//                        {
//                            z = "Credenciales no validas";
//                            isSuccess = false;
//                        }
//
//                    }
//                }
//                catch (Exception ex)
//                {
//                    isSuccess = false;
//                    z = "Exceptions " + ex.getMessage();
//                }
//            }
//            gen();
            z = "Grafica generada";
            isSuccess = true;
            return z;
        }

    }
}

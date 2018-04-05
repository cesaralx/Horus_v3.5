package com.example.alexi.horus_v35;

import android.annotation.SuppressLint;
import android.os.StrictMode;
import android.util.Log;
import java.sql.SQLException;
import java.sql.Connection;
import java.sql.DriverManager;

/**
 * Created by alexi on 1-4-18.
 */
public class ConnectionClass {

//    String ip = "192.168.0.19";
//    String port = "49679";
//    String classs = "net.sourceforge.jtds.jdbc.Driver";
//    String db = "horus_v2";
//    String un = "cesar";
//    String password = "alexaltair360Q";

    String ip = "den1.mssql6.gear.host";
    String port = "1433";
    String classs = "net.sourceforge.jtds.jdbc.Driver";
    String db = "horusv2";
    String un = "horusv2";
    String password = "Aj6TK0XR_d9_";

    @SuppressLint("NewApi")
    public Connection CONN() {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                .permitAll().build();
        StrictMode.setThreadPolicy(policy);
        Connection conn = null;
        String ConnURL = null;
        try {

            Class.forName(classs);
            ConnURL = "jdbc:jtds:sqlserver://" + ip + ":" + port +
                    ";databaseName=" + db + ";user=" + un + ";password=" + password + ";";
            conn = DriverManager.getConnection(ConnURL);
        } catch (SQLException se) {
            Log.e("ERRO", se.getMessage());
        } catch (ClassNotFoundException e) {
            Log.e("ERRO", e.getMessage());
        } catch (Exception e) {
            Log.e("ERRO", e.getMessage());
        }
        return conn;
    }
}



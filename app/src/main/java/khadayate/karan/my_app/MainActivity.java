package khadayate.karan.my_app;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.telephony.CellInfo;
import android.telephony.CellInfoCdma;
import android.telephony.CellInfoGsm;
import android.telephony.CellInfoLte;
import android.telephony.PhoneStateListener;
import android.telephony.SignalStrength;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.time.Clock;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


public class MainActivity extends AppCompatActivity {
    int SIGNAL_STRENGTH,net_type;
    TelephonyManager tm;
    LocationManager lm;
    MyPhoneStateListener plistener;
    MyLocationListener llistener;
    String LATITUDE, LONGITUDE, TSP_NAME,RESULT,NETWORK_TYPE;
    TextView resultText;
    CellInfoCdma cdma_info;
    CellInfoGsm gsm_info;
    CellInfoLte lte_info;
    List<CellInfo> myCell_info;
    File myfile;
    FileOutputStream fos;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        resultText = (TextView) findViewById(R.id.detailstv);
        if(ContextCompat.checkSelfPermission(getApplicationContext(),Manifest.permission.ACCESS_FINE_LOCATION)!=PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},1);
        }
        lm = (LocationManager) getApplication().getSystemService(Context.LOCATION_SERVICE);
        tm = (TelephonyManager) getApplication().getSystemService(Context.TELEPHONY_SERVICE);
        llistener = new MyLocationListener();
        plistener = new MyPhoneStateListener();
        myfile=new File(getExternalFilesDir(null).toString(),"data_file.txt");
        lm.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,5,llistener);
    }
    class MyLocationListener implements LocationListener {

        @Override
        public void onLocationChanged(Location location) {
            LATITUDE = Double.toString(location.getLatitude());
            LONGITUDE = Double.toString(location.getLongitude());
            getStrength();
        }

        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {
        }

        @Override
        public void onProviderEnabled(String s) {
        }

        @Override
        public void onProviderDisabled(String s) {
        }
    }
    class MyPhoneStateListener extends PhoneStateListener{
        @Override
        public void onSignalStrengthsChanged(SignalStrength signalStrength) {
            super.onSignalStrengthsChanged(signalStrength);
            SIGNAL_STRENGTH = 2 * signalStrength.getGsmSignalStrength() - 113;
            displayResult();
            tm.listen(plistener,PhoneStateListener.LISTEN_NONE);
        }
    }
    protected void displayResult()
    {
        if(TSP_NAME.equals("") || SIGNAL_STRENGTH>=0) {
            RESULT = "Network Provider: " + "Not Available" +
                    "\nStrength: " + "--" + "dBm" +
                    "\nLocation: ( " + LATITUDE + " , " + LONGITUDE + " )"+
                    "\nNetwork Type: "+"Not Available";
        }
        else
        {
            RESULT = "Network Provider: " + TSP_NAME +
                    "\nStrength: " + SIGNAL_STRENGTH + "dBm" +
                    "\nLocation: ( " + LATITUDE + " , " + LONGITUDE + " )"+
                    "\nNetwork Type: "+NETWORK_TYPE;
        }
        RESULT=RESULT+"\nDate and Time: "+Calendar.getInstance().getTime().toString();
        resultText.setText(RESULT);
    }
    public void readText(View v){
        startActivity(new Intent(MainActivity.this,History.class));
        resultText.setText("");
    }
    public void doThis(View v)
    {
        getStrength();
        //startActivity(new Intent(MainActivity.this,Activity2.class));
    }
    public void getStrength(){
        if(ContextCompat.checkSelfPermission(getApplicationContext(),Manifest.permission.ACCESS_FINE_LOCATION)!=PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},1);
        }
        TSP_NAME=tm.getNetworkOperatorName();
        net_type=tm.getNetworkType();
        if(net_type==TelephonyManager.NETWORK_TYPE_CDMA){
            NETWORK_TYPE="CDMA";
            myCell_info=tm.getAllCellInfo();
            cdma_info=(CellInfoCdma)myCell_info.get(0);
            SIGNAL_STRENGTH=cdma_info.getCellSignalStrength().getDbm();
            displayResult();
        }
        else if(net_type==TelephonyManager.NETWORK_TYPE_LTE){
            NETWORK_TYPE="LTE";
            myCell_info=tm.getAllCellInfo();
            lte_info=(CellInfoLte)myCell_info.get(0);
            SIGNAL_STRENGTH=lte_info.getCellSignalStrength().getDbm();
            displayResult();
        }
        else if(net_type==TelephonyManager.NETWORK_TYPE_GSM){
            NETWORK_TYPE="GSM";
            myCell_info=tm.getAllCellInfo();
            gsm_info=(CellInfoGsm) myCell_info.get(0);
            SIGNAL_STRENGTH=gsm_info.getCellSignalStrength().getDbm();
            displayResult();
        }
        else{
            if(net_type==TelephonyManager.NETWORK_TYPE_EDGE)NETWORK_TYPE="EDGE";
            else if(net_type==TelephonyManager.NETWORK_TYPE_HSDPA)NETWORK_TYPE="HSDPA";
            else if(net_type==TelephonyManager.NETWORK_TYPE_HSPA)NETWORK_TYPE="HSPA";
            else if(net_type==TelephonyManager.NETWORK_TYPE_HSPAP)NETWORK_TYPE="HSPA+";
            else if(net_type==TelephonyManager.NETWORK_TYPE_GPRS)NETWORK_TYPE="GPRS";
            else NETWORK_TYPE="Unknown";
            tm.listen(plistener,PhoneStateListener.LISTEN_SIGNAL_STRENGTHS);
        }
        //write into a file
        if(RESULT!=null) {
            try {
                myfile.createNewFile();
                fos = new FileOutputStream(myfile,true);
                fos.write(RESULT.getBytes());
                fos.write("\n\n".getBytes());
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
package khadayate.karan.my_app;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;

public class History extends AppCompatActivity {
    File readfile;
    FileReader fr;
    BufferedReader br;
    TextView tv;
    String data="",st;
    int i=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        tv = (TextView) findViewById(R.id.historytv);
        tv.setMovementMethod(new ScrollingMovementMethod());
        readfile = new File(getExternalFilesDir(null), "data_file.txt");
        try {
            readfile.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            fr = new FileReader(readfile);
            br=new BufferedReader(fr);
            st=br.readLine();
            while(st!=null){
                data=data+st+"\n";
                st=br.readLine();
            }
            br.close();
            fr.close();
            tv.setText(data);
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }
    public void clearFile(View v) {
        readfile.delete();
        tv.setText(" ");
        Toast.makeText(this,"Deleted",Toast.LENGTH_SHORT).show();
    }
}

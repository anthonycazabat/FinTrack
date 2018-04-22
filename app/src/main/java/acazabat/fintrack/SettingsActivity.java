package acazabat.fintrack;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.Arrays;

public class SettingsActivity extends AppCompatActivity {
    String settingsFile="settings";
    int range=10;
    String[] Fields=new String[5];
    double[] FieldVals=new double[5];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        getValues();

    }

    public void getValues(){

        try{
            FileInputStream fis=this.openFileInput(settingsFile);
            InputStreamReader isr=new InputStreamReader(fis);
            BufferedReader bufferedReader= new BufferedReader(isr);
            String line;
            int c=0;
            int i;
            //get data from file
            while ((line=bufferedReader.readLine()) != null) {
                if (c==0){
                    range=Integer.parseInt(line);
                    System.out.println(range);
                }else {
                    i=-1;
                    Fields[c-1]=(line.substring(0,line.indexOf(",",i+1)));
                    FieldVals[c-1]=Double.parseDouble(line.substring(line.indexOf(",",i+1)+1));
                    System.out.println(Fields[c-1]+","+FieldVals[c-1]);
                }
                c++;
            }
        }catch(IOException ioe){
            ioe.printStackTrace();
        }


    }
/*
    public void saveValues(){
        int[] dayArray=new int[300];
        double[] priceArray=new double[300];
        String[] fieldArray=new String[300];
        int[] parseVar = new int[2];

        try {
            FileOutputStream fos=this.openFileOutput(settingsFile, Context.MODE_APPEND);
            BufferedWriter osw=new BufferedWriter(new OutputStreamWriter(fos));
            for(int i=1;i<=dayOfTheMonth;i++){
                for(int j=0;j<dayArray.length-1;j++){
                    if(dayArray[j]==i){
                        osw.write(dayArray[j]+","+priceArray[j]+","+fieldArray[j]);
                        osw.newLine();
                        System.out.println(dayArray[j]+","+priceArray[j]+","+fieldArray[j]);
                    }
                }
            }
            osw.close();
            fos.close();
        }catch(IOException ioe){
            ioe.printStackTrace();
        }
    }
*/
    public void reset(){
        //below deletes old file, and creates a new empty one.
        System.out.println(new File(settingsFile).getAbsoluteFile().delete());
        System.out.println("filelist ="+ Arrays.toString(fileList()));
        try {
            FileOutputStream fos=this.openFileOutput(settingsFile, Context.MODE_PRIVATE);
            BufferedWriter osw=new BufferedWriter(new OutputStreamWriter(fos));
            osw.write("");
            osw.close();
            fos.close();
        }catch(IOException ioe){
            ioe.printStackTrace();
        }
    }

    public void exit(View view){
        //go back to the main screen
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}

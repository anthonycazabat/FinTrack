package acazabat.fintrack;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.SpannableStringBuilder;
import android.view.View;
import android.widget.EditText;
import android.text.TextWatcher;
import android.widget.TextView;
import android.widget.Toast;

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
    double other;
    String[] Fields=new String[5];
    double[] FieldVals=new double[5];


    TextWatcher tw=new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            displayMax();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        getValues();

        displayMax();

        EditText editText4 = (EditText) findViewById(R.id.editText4);
        EditText editText11 = (EditText) findViewById(R.id.editText11);
        EditText editText12 = (EditText) findViewById(R.id.editText12);
        EditText editText13 = (EditText) findViewById(R.id.editText13);
        EditText editText14 = (EditText) findViewById(R.id.editText14);
        EditText editText15 = (EditText) findViewById(R.id.editText15);
        editText11.addTextChangedListener(tw);
        editText12.addTextChangedListener(tw);
        editText13.addTextChangedListener(tw);
        editText14.addTextChangedListener(tw);
        editText15.addTextChangedListener(tw);
        editText4.addTextChangedListener(tw);
    }

    public void displayMax(){
        //display maximum of all values
        float max;
        TextView textView14= (TextView) findViewById(R.id.textView14);

        EditText editText11 = (EditText) findViewById(R.id.editText11);
        EditText editText12 = (EditText) findViewById(R.id.editText12);
        EditText editText13 = (EditText) findViewById(R.id.editText13);
        EditText editText14 = (EditText) findViewById(R.id.editText14);
        EditText editText15 = (EditText) findViewById(R.id.editText15);
        EditText editText4 = (EditText) findViewById(R.id.editText4);

        max= Float.valueOf(editText11.getText().toString())+ Float.valueOf(editText12.getText().toString())+ Float.valueOf(editText13.getText().toString())+ Float.valueOf(editText14.getText().toString())+ Float.valueOf(editText15.getText().toString())+ Float.valueOf(editText4.getText().toString());

        textView14.setText(Float.toString(max));
    }

    public void getValues(){
        //retrieve values from settings file.
        EditText editText4 = (EditText) findViewById(R.id.editText4);
        EditText editText5 = (EditText) findViewById(R.id.editText5);
        EditText editText6 = (EditText) findViewById(R.id.editText6);
        EditText editText7 = (EditText) findViewById(R.id.editText7);
        EditText editText8 = (EditText) findViewById(R.id.editText8);
        EditText editText10 = (EditText) findViewById(R.id.editText10);
        EditText editText11 = (EditText) findViewById(R.id.editText11);
        EditText editText12 = (EditText) findViewById(R.id.editText12);
        EditText editText13 = (EditText) findViewById(R.id.editText13);
        EditText editText14 = (EditText) findViewById(R.id.editText14);
        EditText editText15 = (EditText) findViewById(R.id.editText15);

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
                    other=Double.parseDouble(line);
                }else {
                    i=-1;
                    Fields[c-1]=(line.substring(0,line.indexOf(",",i+1)));
                    FieldVals[c-1]=Double.parseDouble(line.substring(line.indexOf(",",i+1)+1));
                }
                c++;
            }
        }catch(IOException ioe){
            ioe.printStackTrace();
        }

        editText5.setText(Fields[0]);
        editText6.setText(Fields[1]);
        editText7.setText(Fields[2]);
        editText8.setText(Fields[3]);
        editText10.setText(Fields[4]);

        editText4.setText(String.valueOf(other));
        editText11.setText(String.valueOf(FieldVals[0]));
        editText12.setText(String.valueOf(FieldVals[1]));
        editText13.setText(String.valueOf(FieldVals[2]));
        editText14.setText(String.valueOf(FieldVals[3]));
        editText15.setText(String.valueOf(FieldVals[4]));
    }

    public void saveValues(){
        //puts values from settings screen into settings file
        EditText editText4 = (EditText) findViewById(R.id.editText4);
        EditText editText5 = (EditText) findViewById(R.id.editText5);
        EditText editText6 = (EditText) findViewById(R.id.editText6);
        EditText editText7 = (EditText) findViewById(R.id.editText7);
        EditText editText8 = (EditText) findViewById(R.id.editText8);

        EditText editText10 = (EditText) findViewById(R.id.editText10);
        EditText editText11 = (EditText) findViewById(R.id.editText11);
        EditText editText12 = (EditText) findViewById(R.id.editText12);
        EditText editText13 = (EditText) findViewById(R.id.editText13);
        EditText editText14 = (EditText) findViewById(R.id.editText14);
        EditText editText15 = (EditText) findViewById(R.id.editText15);

        try {
            FileOutputStream fos=this.openFileOutput(settingsFile, Context.MODE_PRIVATE);
            BufferedWriter osw=new BufferedWriter(new OutputStreamWriter(fos));
            osw.write(editText4.getText().toString());
            for(int i=0;i<=4;i++){
                osw.newLine();
                if (i==0){
                    osw.write(editText5.getText().toString()+","+editText11.getText().toString());
                }else if(i==1){
                    osw.write(editText6.getText().toString()+","+editText12.getText().toString());
                }else if(i==2){
                    osw.write(editText7.getText().toString()+","+editText13.getText().toString());
                }else if(i==3){
                    osw.write(editText8.getText().toString()+","+editText14.getText().toString());
                }else if(i==4){
                    osw.write(editText10.getText().toString()+","+editText15.getText().toString());
                }
            }
            osw.close();
            fos.close();
        }catch(IOException ioe){
            ioe.printStackTrace();
        }
    }

    public void exit(View view){
        //go back to the main screen
        saveValues();
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
    /*
    statement is unused
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
*/
}

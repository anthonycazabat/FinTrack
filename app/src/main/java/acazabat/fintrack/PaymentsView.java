package acazabat.fintrack;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.jjoe64.graphview.series.PointsGraphSeries;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class PaymentsView extends AppCompatActivity {
    int daysInMonth= Calendar.getInstance(Locale.US).getActualMaximum(Calendar.DAY_OF_MONTH);
    SimpleDateFormat day=new SimpleDateFormat("dd");
    SimpleDateFormat month=new SimpleDateFormat("MM");
    SimpleDateFormat year=new SimpleDateFormat("yy");
    Date d = new Date();
    int dayOfTheMonth = Integer.parseInt(day.format(d));
    String monthOfTheYear=month.format(d);
    String Currentyear=year.format(d);
    String filename=monthOfTheYear+Currentyear;
    int maximumLines=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payments_view);
        displayPurchases();
    }
    public void displayPurchases(){
        //display all the purchases in a list with the row number
        TextView textView = (TextView) findViewById(R.id.textView2);
        String scrollText="";
        //open file and get each line of text and append to textView
        try{
            FileInputStream fis=this.openFileInput(filename);
            InputStreamReader isr=new InputStreamReader(fis);
            BufferedReader bufferedReader= new BufferedReader(isr);
            String line;
            int c=0;
            //get data from file
            while (((line=bufferedReader.readLine()) != null)) {
                scrollText=scrollText+c+")       "+line+"\n";
                maximumLines=c;
                c++;
            }
        }catch(IOException ioe){
            ioe.printStackTrace();
        }
        textView.setText(scrollText);
    }
    public void deleteLine(View view) {
        //put code for deleting a line
        EditText editText = (EditText) findViewById(R.id.editText3);
        int lineToDelete = Integer.parseInt(editText.getText().toString());

        int[] dayArray = new int[300];
        double[] priceArray = new double[300];
        String[] fieldArray = new String[300];
        int[] parseVar = new int[2];
if ((maximumLines>=lineToDelete)&&(lineToDelete>=0)){
        try {
            FileInputStream fis = this.openFileInput(filename);
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader bufferedReader = new BufferedReader(isr);
            String line;
            int c = 0;
            //get data from file
            while ((line = bufferedReader.readLine()) != null) {
                int nextOfIndex = 0;
                for (int i = -1; (i = line.indexOf(",", i + 1)) != -1; i++) {
                    parseVar[nextOfIndex] = i;
                    ++nextOfIndex;
                }
                dayArray[c] = Integer.parseInt(line.substring(0, parseVar[0]));
                priceArray[c] = Double.parseDouble(line.substring(parseVar[0] + 1, parseVar[1]));
                fieldArray[c] = (line.substring(parseVar[1] + 1));
                System.out.println(dayArray[c] + "," + priceArray[c] + "," + fieldArray[c]);
                c++;
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
        //delete data from file
        System.out.println(new File(filename).getAbsoluteFile().delete());
        try {
            FileOutputStream fos = this.openFileOutput(filename, Context.MODE_PRIVATE);
            BufferedWriter osw = new BufferedWriter(new OutputStreamWriter(fos));
            osw.write("");
            osw.close();
            fos.close();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
        //add the data back into file
        try {
            FileOutputStream fos = this.openFileOutput(filename, Context.MODE_APPEND);
            BufferedWriter osw = new BufferedWriter(new OutputStreamWriter(fos));
            for (int i = 1; i <= dayOfTheMonth; i++) {
                for (int j = 0; j < dayArray.length - 1; j++) {
                    if ((dayArray[j] == i) && (j != lineToDelete)) {
                        osw.write(dayArray[j] + "," + priceArray[j] + "," + fieldArray[j]);
                        osw.newLine();
                        System.out.println(dayArray[j] + "," + priceArray[j] + "," + fieldArray[j]);
                    }
                }
            }
            osw.close();
            fos.close();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }else{
    Toast.makeText(PaymentsView.this,"line doesn't exist",
            Toast.LENGTH_SHORT).show();
    }
        displayPurchases();

    }
    public void exit(View view){
        //go back to the main screen
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}

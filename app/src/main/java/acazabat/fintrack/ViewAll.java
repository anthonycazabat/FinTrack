package acazabat.fintrack;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.jjoe64.graphview.series.PointsGraphSeries;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class ViewAll extends AppCompatActivity {
    int daysInMonth= Calendar.getInstance(Locale.US).getActualMaximum(Calendar.DAY_OF_MONTH);
    SimpleDateFormat day=new SimpleDateFormat("dd");
    SimpleDateFormat month=new SimpleDateFormat("MM");
    SimpleDateFormat year=new SimpleDateFormat("yy");
    Date d = new Date();
    int dayOfTheMonth = Integer.parseInt(day.format(d));
    int currentmonth=Integer.parseInt(month.format(d));
    int currentyear=Integer.parseInt(year.format(d));
    String monthOfTheYear=month.format(d);
    String Currentyear=year.format(d);
    String filename=monthOfTheYear+Currentyear;
    int maximumLines=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_all);
        displayPurchases();
    }

    public void displayPurchases(){
        //use the current month and year first
        //on each line check for the field name if it doesn't exist create it, and add the value to it.
        //after loop display filename "Total" total value.
        //then display each of the field names followed by their total values.
        //leave a space and move to the next file if exist

        double finish;
        int[] parseVar = new int[2];
        int x;
        double y=0;


        TextView textView = (TextView) findViewById(R.id.textView17);
        String scrollText="";
        //open file and get each line of text for past year.

        for (int l=11; l>=0;l--) {
            try {
                FileInputStream fis = this.openFileInput(filename);
                InputStreamReader isr = new InputStreamReader(fis);
                BufferedReader bufferedReader = new BufferedReader(isr);
                String line;
                double monthtotal=0;
                String[] fieldarray=new String[16];
                double[] valuearray=new double[16];

                    //get data from file
                    while ((line=bufferedReader.readLine()) != null) {
                        int nextOfIndex = 0;
                        for (int i = -1; (i = line.indexOf(",", i + 1)) != -1; i++) {
                            parseVar[nextOfIndex] = i;
                            ++nextOfIndex;
                        }
                        //get line data
                            double value = Double.parseDouble(line.substring(parseVar[0] + 1, parseVar[1]));
                            String Field = (line.substring(parseVar[1] + 1));
                        int j=0;
                        boolean saved=false;
                        while (!saved) {
                            if (fieldarray[j]==null){
                                fieldarray[j]=Field;
                                valuearray[j]=value;
                                saved=true;
                            }else if (fieldarray[j].equals(Field)){
                                //add value to j of value array
                                valuearray[j]=valuearray[j]+value;
                                saved=true;
                            }
                            j++;
                        }
                        monthtotal=monthtotal+value;
                    }

                scrollText=scrollText+ filename+" TOTAL= "+ Double.toString(monthtotal) +"\n";
                int c=0;
                while (fieldarray[c]!=null){
                    //check for fields, and display
                    scrollText=scrollText+ fieldarray[c]+valuearray[c]+"\n";
                    c++;
                }
                scrollText=scrollText+"\n";

            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
            //set the month and year for next file
            if(currentmonth==1){
                currentyear--;
                currentmonth=12;
            }else{
                currentmonth--;
            }
            //update the filename to next file
        if (currentmonth>=10) {
            filename = Integer.toString(currentmonth) + Integer.toString(currentyear);
        }else{
            filename = "0"+Integer.toString(currentmonth) + Integer.toString(currentyear);
        }

            //Check if file exists


        }

        textView.setText(scrollText);
    }

    public void exit(View view){
        //go back to the main screen
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}

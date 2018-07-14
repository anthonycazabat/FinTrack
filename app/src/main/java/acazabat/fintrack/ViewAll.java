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

        TextView textView = (TextView) findViewById(R.id.textView17);
        String scrollText="";
        //open file and get each line of text for past year.

        for (int i=11; i>=0;i--) {

            try {
                FileInputStream fis = this.openFileInput(filename);
                InputStreamReader isr = new InputStreamReader(fis);
                BufferedReader bufferedReader = new BufferedReader(isr);
                String line;
                int c = 0;
                float monthtotal=0;
                String[] fieldarray=new String[10];
                float[] valuearray=new float[10];






/*
                double finish;
                int[] parseVar = new int[2];
                int x;
                double y=0;
                double rangeMinX=0;
                double rangeMaxX=dayOfTheMonth + 1;
                double rangeMinY;
                double rangeMaxY;
                int arrayLen=0;
                double optimalSpending;
                purchases = new PointsGraphSeries<DataPoint>();
                Optimal = new LineGraphSeries<DataPoint>();
                double[] yArray =new double[300];
                int [] dayArray=new int[300];

                //open file and get each of the data points
                try{
                    FileInputStream fis=this.openFileInput(filename);
                    InputStreamReader isr=new InputStreamReader(fis);
                    BufferedReader bufferedReader= new BufferedReader(isr);
                    String line;
                    int c=0;
                    //get data from file
                    while ((line=bufferedReader.readLine()) != null) {
                        int nextOfIndex = 0;
                        for (int i = -1; (i = line.indexOf(",", i + 1)) != -1; i++) {
                            parseVar[nextOfIndex] = i;
                            ++nextOfIndex;
                        }
                        //display the relevant data
                        if(viewField=="ALL") {
                            dayArray[c] = Integer.parseInt(line.substring(0, parseVar[0]));
                            yArray[c] = Double.parseDouble(line.substring(parseVar[0] + 1, parseVar[1]));
                            String Field = (line.substring(parseVar[1] + 1));
                            System.out.println(dayArray[c] + "," + yArray[c] + "," + Field);
                            c++;
                        }else if((line.substring(parseVar[1]+1)).equals(viewField)) {
                            dayArray[c] = Integer.parseInt(line.substring(0, parseVar[0]));
                            yArray[c] = Double.parseDouble(line.substring(parseVar[0] + 1, parseVar[1]));
                            String Field = (line.substring(parseVar[1] + 1));
                            System.out.println(dayArray[c] + "," + yArray[c] + "," + Field);
                            c++;
                        }
                    }
                }catch(IOException ioe){
                    ioe.printStackTrace();
                }
                //find the size of the data array
                for (int i =0;yArray[i]!=0;i++){
                    arrayLen++;
                }

                //create data trend for graph
                if(viewint==0){
                    finish=FieldVals[0]+FieldVals[1]+FieldVals[2]+FieldVals[3]+FieldVals[4];
                }else{
                    finish=FieldVals[viewint-1];
                }
                optimalSpending=(finish*dayOfTheMonth)/daysInMonth;

                */


                

                //get data from file
                while (((line = bufferedReader.readLine()) != null)) {
                    //parse out field and value from line, and save to own group.

                    scrollText = scrollText + c + ")       " + line + "\n";
                    maximumLines = c;
                    c++;
                }

                scrollText=scrollText+ filename+" TOTAL= "+ monthtotal +"\n";
                c=0;
                while (fieldarray[c]!=null){
                    //check for fields, and display
                    scrollText=scrollText+ fieldarray[c]+valuearray[c]+"\n";
                    c++;
                }
                scrollText=scrollText+"\n";

            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
            if(currentmonth==1){
                currentyear--;
                currentmonth=12;
            }else{
                currentmonth--;
            }
            filename=Integer.toString(currentmonth)+Integer.toString(currentyear);

            //maybe check if file exists

        }

        textView.setText(scrollText);
    }

    public void exit(View view){
        //go back to the main screen
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}

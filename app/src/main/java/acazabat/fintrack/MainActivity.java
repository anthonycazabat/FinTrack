package acazabat.fintrack;

import android.content.Context;
import android.content.Intent;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.helper.StaticLabelsFormatter;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.jjoe64.graphview.series.PointsGraphSeries;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Locale;

import static android.R.id.list;

//to do:
// add multi series to display function displaying multi or single series based on viewbool(add colors from string colors)
//finish inputing range bool range adjustment (change min values)
//add in a setting activity that is triggered from settings on click(has be be able to adjust text file)
//on saving a purchase verify the price and date are in bounds (price greater than 0, and between 1 and day of the month)
public class MainActivity extends AppCompatActivity {

//initialize the main activity global variables
    int daysInMonth= Calendar.getInstance(Locale.US).getActualMaximum(Calendar.DAY_OF_MONTH);
    SimpleDateFormat day=new SimpleDateFormat("dd");
    SimpleDateFormat month=new SimpleDateFormat("MM");
    SimpleDateFormat year=new SimpleDateFormat("yy");
    Date d = new Date();
    int dayOfTheMonth = Integer.parseInt(day.format(d));
    String monthOfTheYear=month.format(d);
    String Currentyear=year.format(d);
    String filename=monthOfTheYear+Currentyear;
    String settingsFile="settings";
    boolean rangebool=false;
    boolean viewbool=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //on create, create the layout
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        EditText editText2 = (EditText) findViewById(R.id.editText2);
        editText2.setText(( String.valueOf(dayOfTheMonth)));

        Toolbar mToolbar=(Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        System.out.println(getFilesDir().toString());
        System.out.println("filelist ="+ Arrays.toString(fileList()));

        //if settings file doesn't exist, create one with basic settings

        if(!Arrays.toString(fileList()).contains(settingsFile)||(true)){//remove the true after troubleshooting***********************
            try {
                FileOutputStream fos=this.openFileOutput(settingsFile, Context.MODE_PRIVATE);
                BufferedWriter osw=new BufferedWriter(new OutputStreamWriter(fos));
                osw.write("10");  // add in range
                osw.newLine();
                osw.write("Food,380");   //Field 1
                osw.newLine();
                osw.write("Car,270");    //Field 2
                osw.newLine();
                osw.write("Gifts,250");  //Field 3
                osw.newLine();
                osw.write("Misc,200");    //Field 4
                osw.newLine();
                osw.write("Travel,150");  //Field 5
                osw.close();
                fos.close();
            }catch(IOException ioe){
                ioe.printStackTrace();
            }
        }

        //check if file exists, and if not create a blank file
        //System.out.println(getFilesDir().toString());
        //System.out.println("filelist ="+ Arrays.toString(fileList()));
        if(!Arrays.toString(fileList()).contains(filename)){
            try {
                FileOutputStream fos=this.openFileOutput(filename, Context.MODE_PRIVATE);
                BufferedWriter osw=new BufferedWriter(new OutputStreamWriter(fos));
                osw.write("");
                osw.close();
                fos.close();
            }catch(IOException ioe){
                ioe.printStackTrace();
            }
        }

        System.out.println(getFilesDir().toString());
        System.out.println("filelist ="+ Arrays.toString(fileList()));

        //put all the current months data on the graph manually
        //reset();  //hard reset of all data use when need to reset whole month
        //savePaymentToFile(1,5,"food");
        //savePaymentToFile(4,5,"food");
        //savePaymentToFile(8,5,"food");
        //savePaymentToFile(12,5,"food");
        //savePaymentToFile(11,5,"food");
        //savePaymentToFile(25,780,"food");
        sort();
        putDataInGraph();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater mMenuInflater = getMenuInflater();
        mMenuInflater.inflate(R.menu.fin_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==R.id.toggle_view){
            //input code to toggle from classic view to standard view

                //in multiseries make a series for each grouping (pulling color from text, and adding for series)
            if (viewbool==true){
                viewbool=false;
            }else{
                viewbool=true;
            }
            putDataInGraph();
            Toast.makeText(MainActivity.this,"You have clicked on see individual or multiple",
                    Toast.LENGTH_SHORT).show();
        }
        if(item.getItemId()==R.id.delete_payments){
            //goes to delete payment screen.
            sort();
            Intent intent = new Intent(this, PaymentsView.class);
            startActivity(intent);
        }
        if(item.getItemId()==R.id.toggle_range){
            //if range boolean is false make current day of the month-range (as long as not less than 0) the low bound.
            //make the high bound of x the day of the month
            //make the low ybound minimum x of optimal or total price up to that day
            if (rangebool==true){
                rangebool=false;
            }else{
                rangebool=true;
            }
            putDataInGraph();
            Toast.makeText(MainActivity.this,"You have clicked on toggle range",
                    Toast.LENGTH_SHORT).show();
        }
        if(item.getItemId()==R.id.settings){
            //create a new activity that starts up on this selection.
            //in settings activity have a view boolean, a range int, max int, strings for radio buttons
            //create 2 input fields for range and max  and 5 string boxes that populate with current strings
            //create save $ exit button to move back to the main activity
            //in the main activity make the max value and radio button strings get
            // retrieved from the settings file, and load on create.

            Toast.makeText(MainActivity.this,"You have clicked on settings",
                    Toast.LENGTH_SHORT).show();
            sort();
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);

        }
        return super.onOptionsItemSelected(item);
    }

    public void addPayment(View view){
        String Field;
        EditText editText = (EditText) findViewById(R.id.editText);
        EditText editText2 = (EditText) findViewById(R.id.editText2);
        double price=Double.parseDouble(editText.getText().toString());
        int dayToInsert= Integer.parseInt(editText2.getText().toString());
        //grab radio button text to put in field.
        RadioGroup radioGroup = (RadioGroup)findViewById(R.id.radioGroup);
        RadioButton checked=(RadioButton) findViewById(radioGroup.getCheckedRadioButtonId());
        Field=checked.getText().toString();
        savePaymentToFile(dayToInsert,price,Field); //calls the save payment function
        sort();//will correct the order
        putDataInGraph();  //puts all the saved data for the current month on the graph
    }
    /*  Delete when easy enough to display purchases from menu
    public void displayDataList(View view){
        //add code here for viewing a list of purchases populate with the last 50 purchases(if they exist)
        sort();
        Intent intent = new Intent(this, PaymentsView.class);
        startActivity(intent);
    }
    */
    public void putDataInGraph(){
        //add data from settings file as well
        //initialize all vars
        PointsGraphSeries<DataPoint> purchases;
        LineGraphSeries<DataPoint> Optimal;
        GraphView graph=(GraphView) findViewById(R.id.graph);
        graph.removeAllSeries();
        double max=1250;   //delete =1000 when pulling from settings file
        int rangeint=10;
        int[] parseVar = new int[2];
        int x;
        double y=0;
        double rangeMin=0;
        int arrayLen=0;
        double optimalSpending;
        purchases = new PointsGraphSeries<DataPoint>();
        Optimal = new LineGraphSeries<DataPoint>();
        double[] yArray =new double[300];
        int [] dayArray=new int[300];
        double[] maxVals=new double[5];
        String[] fields=new String[5];

        /*
        try{
            FileInputStream fis=this.openFileInput(settingsFile);
            InputStreamReader isr=new InputStreamReader(fis);
            BufferedReader bufferedReader= new BufferedReader(isr);
            String line;
            int c=0;
            //get data from settings file
            while ((line=bufferedReader.readLine()) != null) {
                if (c==0){
                    rangeint=Integer.parseInt(line);
                    System.out.println(rangeint);
                }else if(c==1){
                    int d=0;
                    for (int i = -1; (i = line.indexOf(",", i + 1)) != -1; i++) {
                        maxVals[d]=Double.parseDouble(line.substring(i+1,line.indexOf(",", i + 1)));
                        System.out.println(maxVals[d]);
                    }
                }else if (c==2){
                    int d=0;
                    for (int i = -1; (i = line.indexOf(",", i + 1)) != -1; i++) {
                        fields[d]=(line.substring(i+1,line.indexOf(",", i + 1)));
                        System.out.println(fields[d]);
                    }
                }
                c++;
            }
        }catch(IOException ioe){
            ioe.printStackTrace();
        }

        */
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
                dayArray[c]=Integer.parseInt(line.substring(0, parseVar[0]));
                yArray[c]=Double.parseDouble(line.substring(parseVar[0]+1,parseVar[1]));
                String Field=(line.substring(parseVar[1]+1));
                System.out.println(dayArray[c]+","+yArray[c]+","+Field);
                c++;
            }
        }catch(IOException ioe){
            ioe.printStackTrace();
        }
        //find the size of the data array
        for (int i =0;yArray[i]!=0;i++){
            arrayLen++;
        }
        //put the data into data series for graph
        for(int i = 0; i<arrayLen;i++) {
            y = y + yArray[i];
            x = dayArray[i];
            purchases.appendData(new DataPoint(x, y), true,arrayLen );
            if (viewbool) {
                if (i == (dayOfTheMonth - rangeint)) {
                    rangeMin=y;
                }
            }
        }
        //add series to graph
        graph.addSeries(purchases);
        //create data series for optimal spending and add to graph
        Optimal.appendData(new DataPoint(0,0),true,2);
        //max=maxVals[0]+maxVals[1]+maxVals[2]+maxVals[3]+maxVals[4];
        optimalSpending=(max*dayOfTheMonth)/daysInMonth;
        Optimal.appendData(new DataPoint(dayOfTheMonth,optimalSpending),true,2);
        graph.addSeries(Optimal);

        if(viewbool&&false){
            if(dayOfTheMonth-rangeint>0) {
                graph.getViewport().setMinX(dayOfTheMonth-rangeint);
                if((max*dayOfTheMonth)/(dayOfTheMonth-rangeint)<rangeMin) {   //make y equal to least between optimal and measured
                    graph.getViewport().setMinY((max*dayOfTheMonth)/(dayOfTheMonth-rangeint));
                } else{
                    graph.getViewport().setMinY(rangeMin);
                }
            }else{
                graph.getViewport().setMinX(0);
                graph.getViewport().setMinY(0);
            }
            graph.getViewport().setMaxX(dayOfTheMonth + 1);
            graph.getViewport().setXAxisBoundsManual(true);

        }else {
            graph.getViewport().setMinX(0);
            graph.getViewport().setMaxX(dayOfTheMonth + 1);
            graph.getViewport().setXAxisBoundsManual(true);
            graph.getViewport().setMinY(0);
        }

        if (optimalSpending>= y){
            graph.getViewport().setMaxY(optimalSpending);
        }else{
            graph.getViewport().setMaxY(y);
        }
        graph.getViewport().setYAxisBoundsManual(true);

        graph.getViewport().setScalable(true);
        graph.getViewport().setScalableY(true);
        //StaticLabelsFormatter slf=new StaticLabelsFormatter(graph);
        //slf.setHorizontalLabels(new String[]{"old","middle","new"});
        //slf.setVerticalLabels(new String[]{"low","middle","high"});
        //graph.getGridLabelRenderer().setLabelFormatter(slf);
    }


    public void savePaymentToFile(int day,double cost,String field){
        // update the file labeled MonthYear (example0318) with a new purchase.
        try {
            FileOutputStream fos=this.openFileOutput(filename, Context.MODE_APPEND);
            BufferedWriter osw=new BufferedWriter(new OutputStreamWriter(fos));
            osw.write(day+","+cost+","+field);
            osw.newLine();
            System.out.println(day+","+cost+","+field);
            osw.close();
            fos.close();
        }catch(IOException ioe){
            ioe.printStackTrace();
        }
    }


    public void reset(){
        //below deletes old file, and creates a new empty one.
        System.out.println(new File(filename).getAbsoluteFile().delete());
        System.out.println("filelist ="+ Arrays.toString(fileList()));
        try {
            FileOutputStream fos=this.openFileOutput(filename, Context.MODE_PRIVATE);
            BufferedWriter osw=new BufferedWriter(new OutputStreamWriter(fos));
            osw.write("");
            osw.close();
            fos.close();
        }catch(IOException ioe){
            ioe.printStackTrace();
        }
    }

    public void sort(){
        int[] dayArray=new int[300];
        double[] priceArray=new double[300];
        String[] fieldArray=new String[300];
        int[] parseVar = new int[2];
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
                dayArray[c]=Integer.parseInt(line.substring(0, parseVar[0]));
                priceArray[c]=Double.parseDouble(line.substring(parseVar[0]+1,parseVar[1]));
                fieldArray[c]=(line.substring(parseVar[1]+1));
                System.out.println(dayArray[c]+","+priceArray[c]+","+fieldArray[c]);
                c++;
            }
        }catch(IOException ioe){
            ioe.printStackTrace();
        }
        reset();  //delete data from file
        //add the data back into file
        try {
            FileOutputStream fos=this.openFileOutput(filename, Context.MODE_APPEND);
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

}

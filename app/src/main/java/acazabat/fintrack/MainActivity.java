package acazabat.fintrack;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.provider.ContactsContract;
import android.support.v4.content.ContextCompat;
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
import android.widget.TextView;
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
//add in last months purchases in delete purchases view, and allow for deletions of those.
    //on load loop over twice for this and last month
    //save line value for demarcation between this and last month.
    //on delete, pick filename based on demarcation value, and run delete based on that.
    //verify that the purchases run from most recent to oldest.

//Clean up code (done on main activity, do on settingsactivity, and viewall)
    //delete all unnecessary lines of code
    //comment all java script
    //minimize code as much as possible

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
    int viewint=0;
    double other;
    String[] Fields=new String[5];
    double[] FieldVals=new double[5];
    String viewField="ALL";
    int CurrentMonthView=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //on create, create the layout
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        EditText editText2 = (EditText) findViewById(R.id.editText2);
        editText2.setText(( String.valueOf(dayOfTheMonth)));
        Toolbar mToolbar=(Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        //displays list of files in the app directory
        System.out.println(getFilesDir().toString());
        System.out.println("filelist ="+ Arrays.toString(fileList()));

        //if settings file doesn't exist, create one with basic settings
        if(!Arrays.toString(fileList()).contains(settingsFile)){
            try {
                FileOutputStream fos=this.openFileOutput(settingsFile, Context.MODE_PRIVATE);
                BufferedWriter osw=new BufferedWriter(new OutputStreamWriter(fos));
                osw.write("500");  // add in other miscelaneous values/autopay
                osw.newLine();
                osw.write("Food,400");   //Field 1
                osw.newLine();
                osw.write("Car,250");    //Field 2
                osw.newLine();
                osw.write("Gifts,250");  //Field 3
                osw.newLine();
                osw.write("Misc,200");    //Field 4
                osw.newLine();
                osw.write("Project,200");  //Field 5
                osw.close();
                fos.close();
            }catch(IOException ioe){
                ioe.printStackTrace();
            }
        }

        //check if file exists, and if not create a blank file
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


        //put an example set of data on the graph manually
        //reset();  //hard reset of all data use when need to reset whole month
        //savePaymentToFile(1,5,"food");
        //savePaymentToFile(4,5,"food");
        //savePaymentToFile(8,5,"food");
        //savePaymentToFile(12,5,"food");
        //savePaymentToFile(11,5,"food");
        //savePaymentToFile(25,780,"food");
        getSettings();
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
            //in multiseries make a series for each grouping (pulling color from text, and adding for series)
            if (viewint==5){
                viewint=0;
                viewField="ALL";
            }else{
                viewint=viewint+1;
                viewField=Fields[viewint-1];
            }
            putDataInGraph();
            Toast.makeText(MainActivity.this,"You are viewing "+viewField.toString()+" data",
                    Toast.LENGTH_SHORT).show();
        }
        if(item.getItemId()==R.id.delete_payments){
            //goes to delete payment screen.
            sort();
            Intent intent = new Intent(this, PaymentsView.class);
            startActivity(intent);
        }
        if(item.getItemId()==R.id.settings){
            //initializes the settings menu
            sort();
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);

        }
        if(item.getItemId()==R.id.ViewAll){
            //initializes the settings menu
            sort();
            Intent intent = new Intent(this, ViewAll.class);
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
        if(dayToInsert<=dayOfTheMonth&&dayToInsert>=0&&price!=0){
            savePaymentToFile(dayToInsert,price,Field); //calls the save payment function
            editText.getText().clear();
        }else{
            Toast.makeText(MainActivity.this,"Day of the month or price is out of bounds",
                    Toast.LENGTH_SHORT).show();
        }

        sort();//will correct the order
        putDataInGraph();  //puts all the saved data for the current month on the graph
    }

    public void getSettings(){
        //add code here for grabbing settings and saving to variables, and to radio text.
        RadioButton radioButton1=(RadioButton)findViewById(R.id.radioButton1);
        RadioButton radioButton2=(RadioButton)findViewById(R.id.radioButton2);
        RadioButton radioButton3=(RadioButton)findViewById(R.id.radioButton3);
        RadioButton radioButton4=(RadioButton)findViewById(R.id.radioButton4);
        RadioButton radioButton5=(RadioButton)findViewById(R.id.radioButton5);

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
            radioButton1.setText(Fields[0]);
            radioButton2.setText(Fields[1]);
            radioButton3.setText(Fields[2]);
            radioButton4.setText(Fields[3]);
            radioButton5.setText(Fields[4]);

        }catch(IOException ioe){
            ioe.printStackTrace();
        }
    }

    public void putDataInGraph(){
        //initialize all vars
        PointsGraphSeries<DataPoint> purchases;
        LineGraphSeries<DataPoint> Optimal;
        GraphView graph=(GraphView) findViewById(R.id.graph);
        graph.removeAllSeries();
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
        TextView textViewcurrent= (TextView) findViewById(R.id.textView15);
        TextView textViewoptimal= (TextView) findViewById(R.id.textView19);

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
        //add the max spending value(finish) into the textview
        textViewoptimal.setText(Double.toString(finish));

        //give initial values for max and min y
            rangeMinY = 0;
        rangeMaxY=yArray[0];

        //put the data into data series for graph
        for(int i = 0; i<arrayLen;i++) {
            y = y + yArray[i];
            x = dayArray[i];
            purchases.appendData(new DataPoint(x, y), true,arrayLen );
                if (y < rangeMinY) rangeMinY = y;
                if (y > rangeMaxY) rangeMaxY = y;
        }

        //put the current spending amount into a text view.
        textViewcurrent.setText(Double.toString(y));
        //find absolute max and min
        if (optimalSpending>= y){
            rangeMaxY=optimalSpending;
        }else{
            rangeMaxY=y;
        }

        //add series to graph
        graph.addSeries(purchases);

        //set the series color
        if (viewint==0){
            purchases.setColor(Color.BLACK);
        }else if (viewint==1){
            purchases.setColor(Color.parseColor("#5DADE2"));
        }else if (viewint==2){
            purchases.setColor(Color.parseColor("#CB4335"));
        }else if (viewint==3){
            purchases.setColor(Color.parseColor("#229954"));
        }else if (viewint==4){
            purchases.setColor(Color.parseColor("#F39C12"));
        }else if (viewint==5){
            purchases.setColor(Color.parseColor("#A569BD"));
        }

        //create and add optimal spending to graph
        Optimal.appendData(new DataPoint(0,0),true,2);
        Optimal.appendData(new DataPoint(dayOfTheMonth,optimalSpending),true,2);
        graph.addSeries(Optimal);
        Optimal.setColor(Color.BLACK);

        //sets the range of the view port
            graph.getViewport().setMinX(rangeMinX);
            graph.getViewport().setMaxX(rangeMaxX);
            graph.getViewport().setMinY(rangeMinY);
            graph.getViewport().setMaxY(rangeMaxY);

        graph.getViewport().setXAxisBoundsManual(true);
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
        //sets the new order for the data.
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
        //add the data back into file in sorted form
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

package com.ethereum.teddy.ethprice;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.JsonReader;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HttpsURLConnection;

public class MainActivity extends AppCompatActivity {


    TextView mClock;
    private Handler mHandler;
    private ScheduledExecutorService scheduleTaskExecutor;
    StringBuilder sb = new StringBuilder();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mClock = (TextView) findViewById(R.id.myTextView);

        mHandler = new Handler();
        //mHandler.post(timerTask);

       FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        scheduleTaskExecutor= Executors.newScheduledThreadPool(5);

        // This schedule a task to run every 10 minutes:
        scheduleTaskExecutor.scheduleAtFixedRate(new Runnable() {
            public void run() {
                // Parsing RSS feed:
                getEthPrice();

                // If you need update UI, simply do this:
                runOnUiThread(new Runnable() {
                    public void run() {
                        // update your UI component here.
                        mClock.clearAnimation();
                        mClock.setText("");
                        mClock.setText(sb);
                        sb.delete(0,sb.length());
                    }
                });
            }
        }, 0, 10, TimeUnit.SECONDS);
    } // end of onCreate()





    private void getEthPrice() {
        AsyncTask.execute(new Runnable(){
            @Override
            public void run() {
                //all your networking
                System.out.println("making connection");
                HttpsURLConnection myConnection = null;
                try {
                    URL coinOne = new URL("https://api.coinone.co.kr/ticker/?currency=eth");


                    myConnection = (HttpsURLConnection) coinOne.openConnection();
                    myConnection.setRequestProperty("currency", "eth");





                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                JsonReader jsonReader = null;
                try {
                    if (myConnection.getResponseCode() == 200) {
                        // Success
                        // Further processing here
                        InputStream responseBody = myConnection.getInputStream();
                        InputStreamReader responseBodyReader =
                                new InputStreamReader(responseBody, "UTF-8");

                        jsonReader = new JsonReader(responseBodyReader);
                    } else {
                        // Error handling code goes here
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

                try {
                    jsonReader.beginObject(); // Start processing the JSON object
                } catch (IOException e) {
                    e.printStackTrace();
                }

                try {
                    while (jsonReader.hasNext()) { // Loop through all keys
                        String key = jsonReader.nextName(); // Fetch the next key
                        //if (key.equals("last")) { // Check if desired key
                            // Fetch the value as a String
                            String value = jsonReader.nextString();
                            System.out.println( key + " " + value);
                        sb.append(key);
                        sb.append(" ");
                        sb.append(value);
                        sb.append("\n");

                            // Do something with the value
                            // ...

                            //break; // Break out of the loop
                        //} else {
                            //jsonReader.skipValue(); // Skip values of other keys
                        //}
                    }
                    //mClock.setText(sb.toString());
                } catch (IOException e) {
                    e.printStackTrace();
                }


                try {
                    jsonReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                myConnection.disconnect();


            }
        });
    }


    //timer task

    private Runnable timerTask = new Runnable(){
        @Override
        public void run(){
            System.out.println("periodic test");
            getEthPrice();
            mHandler.postDelayed(timerTask,5000);

        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


}

package com.example.kiramurphy.stopwatch;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    // Define variables for our views
    private TextView tv_minutes = null;
    private TextView tv_seconds = null;
    private TextView tv_milliseconds = null;
    private Button bt_left = null;
    private Button bt_right = null;
    private Timer t = new Timer();
    private Counter ctr = new Counter(); // TimerTask
    private int count = 0;
    private int totalCount = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize views
        this.tv_minutes = findViewById(R.id.tv_minutes);
        this.tv_seconds = findViewById(R.id.tv_seconds);
        this.tv_milliseconds = findViewById(R.id.tv_milliseconds);

        // Initialize buttons
        this.bt_left = findViewById(R.id.bt_left);
        this.bt_right = findViewById(R.id.bt_right);

        // Set count and total count to 0 to begin with
        this.count = 0;
        this.totalCount = 0;

        // Set text views to 0 to begin with.
        tv_minutes.setText(R.string.doubleZero);
        tv_seconds.setText(R.string.doubleZero);
        tv_milliseconds.setText("0");

        this.bt_left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Check value of left button to determine what to do
                if(bt_left.getText().equals("Start")){
                    // Start a new timer and counter and set the count to count
                    // Count will be dependent on whether the app has saved preferences.
                    t = new Timer();
                    ctr = new Counter();
                    ctr.count = count;
                    t.scheduleAtFixedRate(ctr,0,10);

                    // Set the button to stop and change the color to red
                    bt_left.setText(R.string.stop);
                    bt_left.setBackgroundColor(getColor(R.color.red));

                }else if(bt_left.getText().equals("Stop")) {

                    // Set totalCount to what the counter is currently at to preserve count for resume.
                    totalCount = ctr.count;

                    // Cancel the timer and counter/
                    t.cancel();
                    ctr.cancel();

                    // Set the button to resume and change the color to grey
                    bt_left.setText(R.string.resume);
                    bt_left.setBackgroundColor(getColor(R.color.grey));

                }else if(bt_left.getText().equals("Resume")){
                    // Start a new timer and counter
                    t = new Timer();
                    ctr = new Counter();

                    // Set the counter to the total count that was preserved in totalCount
                    ctr.count = totalCount;

                    // Start the timer
                    t.scheduleAtFixedRate(ctr,0,10);

                    // Set the button to stop and change the color to red
                    bt_left.setText(R.string.stop);
                    bt_left.setBackgroundColor(getColor(R.color.red));

                }
            }

        });

        this.bt_right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Check the state of the left button to determine if a timer needs to be stopped
                if(bt_left.getText().equals("Start") || bt_left.getText().equals("Resume")){

                    // Reset all settings and set button to start
                    bt_left.setText(R.string.start);
                    bt_left.setBackgroundColor(getColor(R.color.green));
                    MainActivity.this.count = 0;
                    MainActivity.this.totalCount = 0;
                    MainActivity.this.tv_minutes.setText(R.string.doubleZero);
                    MainActivity.this.tv_seconds.setText(R.string.doubleZero);
                    MainActivity.this.tv_milliseconds.setText("0");
                }else{
                    bt_left.setText(R.string.start);
                    bt_left.setBackgroundColor(getColor(R.color.green));
                    // Cancel the timer and the counter and create a new one
                    t.cancel();
                    ctr.cancel();
                    ctr.count = 0;
                    t = new Timer();
                    ctr = new Counter();
                    MainActivity.this.count = 0;
                    MainActivity.this.totalCount = 0;
                    MainActivity.this.tv_minutes.setText(R.string.doubleZero);
                    MainActivity.this.tv_seconds.setText(R.string.doubleZero);
                    MainActivity.this.tv_milliseconds.setText("0");
                }

            }
        });
    }

    class Counter extends TimerTask {
        // Set the count to the count from the main activity
        private int count = MainActivity.this.count;

        @Override
        public void run() {
            MainActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    // Since timer is on a period of 10, change the numbers to what
                    // should be displayed
                    int milliseconds = (count % 100) / 10;
                    int seconds = (count / 100) % 60;
                    int minutes = ((count / (100 * 60)) % 60);

                    // Change them to a string because .setText() method takes a string
                    String textMilliseconds = Integer.toString(milliseconds);
                    String textSeconds = Integer.toString(seconds);
                    String textMinutes = Integer.toString(minutes);

                    // If less than 10, we need a zero in front
                    if (seconds < 10) {
                        textSeconds = "0" + textSeconds;
                    }
                    if (minutes < 10) {
                        textMinutes = "0" + textMinutes;
                    }

                    // Set the text of the button
                    MainActivity.this.tv_milliseconds.setText(textMilliseconds);
                    MainActivity.this.tv_seconds.setText(textSeconds);
                    MainActivity.this.tv_minutes.setText(textMinutes);

                    // Increase the count
                    count++;
                }
            });
        }
    }


    @Override
    protected void onStart(){
        super.onStart();
        // Factory method - design pattern
        Toast.makeText(this, "Stopwatch is started", Toast.LENGTH_LONG).show();

        // Get the count from preferences
        count = getPreferences(MODE_PRIVATE).getInt("COUNT",0);

        // If the count is greater than 0, set the screen value to the proper count
        if(count!=0){
            int milliseconds = (count%100)/10;
            int seconds = (count/100)%60;
            int minutes = ((count/(100*60))%60);
            String textMilliseconds = Integer.toString(milliseconds);
            String textSeconds = Integer.toString(seconds);
            String textMinutes = Integer.toString(minutes);

            if (seconds < 10) {
                textSeconds = "0" + textSeconds;
            }
            if (minutes < 10) {
                textMinutes = "0" + textMinutes;
            }
            MainActivity.this.tv_milliseconds.setText(textMilliseconds);
            MainActivity.this.tv_seconds.setText(textSeconds);
            MainActivity.this.tv_minutes.setText(textMinutes);
        }

    }

    @Override
    protected void onPause() {
        super.onPause();

        // Save the count to the preferences
        getPreferences(MODE_PRIVATE).edit().putInt("COUNT",ctr.count).apply();
    }

    @Override
    protected void onStop() {
        super.onStop();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

    }

}

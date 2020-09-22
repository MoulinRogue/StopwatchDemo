package com.example.stopwatchdemo;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {
    TextView tvTimer, tvCountdown;
    long startTime, timeInMilliseconds = 0;
    long revTimeInMilliseconds = 30000; //time in
    long stopTimeRemainingMilliseconds = 0; //time stop button is pressed
    Handler customHandler = new Handler();
    Button start, stop;
    CountDownTimer cTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tvTimer = (TextView) findViewById(R.id.tvTimer);
        tvCountdown = (TextView) findViewById(R.id.tvCountdown);
        start = (Button) findViewById(R.id.btnStart);
        stop = (Button) findViewById(R.id.btnStop);
        stop.setVisibility(View.GONE);

        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                start(view);
                start.setVisibility(View.GONE);
                stop.setVisibility(View.VISIBLE);
            }
        });

        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stop(view);
                cTimer.cancel(); // stop countdown
                //tidy up milliseconds (basically code is not accurate to the millisecond, so this just ensures its tidy.
                tvCountdown.setText(getDateFromMillis(stopTimeRemainingMilliseconds));
                tvTimer.setText(getDateFromMillis(revTimeInMilliseconds - stopTimeRemainingMilliseconds));
            }
        });
    }

    public static String getDateFromMillis(long d) {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat df = new SimpleDateFormat("mm:ss:SSS");
        df.setTimeZone(TimeZone.getTimeZone("GMT"));
        return df.format(d);
    }

    public void start(View v) {
        startTime = SystemClock.uptimeMillis();
        customHandler.postDelayed(updateTimerThread, 0); //start timer
        countDown(v); //start countdown
    }

    public void stop(View v) {
        customHandler.removeCallbacks(updateTimerThread); //stop timer
    }

    public void countDown(final View v) {
        cTimer = new CountDownTimer(revTimeInMilliseconds, 10) { // adjust the milli seconds here
            public void onTick(long millisUntilFinished) {
                long m = TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished);
                long s = TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished - m * 60 * 1000);
                long ms = millisUntilFinished - m * 60 * 1000 - s * 1000;
                stopTimeRemainingMilliseconds = millisUntilFinished; //gets remaining time for the stop tidy, could also be used for pause
                tvCountdown.setText("" + String.format("%02d:%02d:%02d", m, s, ms));
            }

            public void onFinish() {
                tvCountdown.setText("00:00:00");
                stop(v); //stop the timer when countdown complete
                tvTimer.setText(getDateFromMillis(revTimeInMilliseconds)); //this just tidies up the milliseconds
            }
        }.start();
    }

    private Runnable updateTimerThread = new Runnable() {
        public void run() {
            timeInMilliseconds = SystemClock.uptimeMillis() - startTime;
            tvTimer.setText(getDateFromMillis(timeInMilliseconds));
            customHandler.postDelayed(this, 10);
        }
    };
}
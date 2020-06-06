package com.example.app01;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Vibrator;
import android.text.Layout;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

//novo
import android.content.SharedPreferences;
import android.os.CountDownTimer;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import java.util.Locale;


public class MainActivity extends AppCompatActivity {


    @BindView(R.id.btnStart) Button btnStart;
    @BindView(R.id.btnStop) Button btnStop;
    @BindView(R.id.btnPause) Button btnPause;
    @BindView(R.id.btnContinue) Button btnContinue;
    @BindView(R.id.mil) TextView mil;
    @BindView(R.id.sec) TextView sec;
    @BindView(R.id.min) TextView min;
    @BindView(R.id.hr) TextView hr;

    long initialTime;

    int setSec = 0;
    int setMin = 0;
    int setHr = 0;
    int tempPause = 0;
    boolean check = false;
    int i;

    private TextView tv;
    //novo
    private EditText mEditTextInput;
    private TextView mTextViewCountDown;
    private Button mButtonSet;
    private Button mButtonStartPause;
    private Button mButtonReset;
    private CountDownTimer mCountDownTimer;
    private boolean mTimerRunning;
    private long mStartTimeInMillis;
    private long mTimeLeftInMillis;
    private long mEndTime;


    //vibrate
    Vibrator vibrator;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        //layout
        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.layout);

        tv = findViewById(R.id.tv);

        //vibrate
        vibrator = (Vibrator)getSystemService(VIBRATOR_SERVICE);

        //novo
        mEditTextInput = findViewById(R.id.edit_text_input);
        mTextViewCountDown = findViewById(R.id.text_view_countdown);
        mButtonSet = findViewById(R.id.button_set);
        mButtonStartPause = findViewById(R.id.button_start_pause);
        mButtonReset = findViewById(R.id.button_reset);

        mButtonSet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String input = mEditTextInput.getText().toString();
                if (input.length() == 0) {
                    Toast.makeText(MainActivity.this, "Polje ne može biti prazno", Toast.LENGTH_SHORT).show();
                    return;
                }

                long millisInput = Long.parseLong(input) * 60000;
                if (millisInput == 0) {
                    Toast.makeText(MainActivity.this, "Unesite pozitivan broj", Toast.LENGTH_SHORT).show();
                    return;
                }
                setTime(millisInput);
                mEditTextInput.setText("");

            }
        });
        mButtonStartPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mTimerRunning) {
                    pauseTimer();
                } else {
                    startTimer();
                }
            }
        });
        mButtonReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetTimer();
            }
        });


        //do tu

        btnContinue.setVisibility(View.INVISIBLE);
        btnPause.setVisibility(View.INVISIBLE);
        btnStop.setVisibility(View.INVISIBLE);
    }

    //novo
    private void setTime(long milliseconds) {
        mStartTimeInMillis = milliseconds;
        resetTimer();
        closeKeyboard();
    }
    private void startTimer() {
        mEndTime = System.currentTimeMillis() + mTimeLeftInMillis;
        mCountDownTimer = new CountDownTimer(mTimeLeftInMillis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                mTimeLeftInMillis = millisUntilFinished;
                updateCountDownText();
            }
            @Override
            public void onFinish() {
                mTimerRunning = false;
                //vibrate
                vibrator.vibrate(1000);
                //toast
                Toast.makeText(MainActivity.this, "Gotovo", Toast.LENGTH_SHORT).show();
                updateWatchInterface();
            }
        }.start();
        mTimerRunning = true;
        updateWatchInterface();

    }
    private void pauseTimer() {
        mCountDownTimer.cancel();
        mTimerRunning = false;
        updateWatchInterface();
    }
    private void resetTimer() {
        mTimeLeftInMillis = mStartTimeInMillis;
        updateCountDownText();
        updateWatchInterface();
    }
    private void updateCountDownText() {
        int hours = (int) (mTimeLeftInMillis / 1000) / 3600;
        int minutes = (int) ((mTimeLeftInMillis / 1000) % 3600) / 60;
        int seconds = (int) (mTimeLeftInMillis / 1000) % 60;
        String timeLeftFormatted;
        if (hours > 0) {
            timeLeftFormatted = String.format(Locale.getDefault(),
                    "%d:%02d:%02d", hours, minutes, seconds);
        } else {
            timeLeftFormatted = String.format(Locale.getDefault(),
                    "%02d:%02d", minutes, seconds);
        }
        mTextViewCountDown.setText(timeLeftFormatted);
    }
    private void updateWatchInterface() {
        if (mTimerRunning) {
            mTextViewCountDown.setTextColor(Color.WHITE);
            mTextViewCountDown.setPadding(0, -50, 0, 0);
            tv.setVisibility(View.INVISIBLE);
            LinearLayout linearLayout = (LinearLayout) findViewById(R.id.layout);
            linearLayout.setVisibility(View.INVISIBLE);
            linearLayout.setPadding(0,-150,0,0);
            mEditTextInput.setVisibility(View.INVISIBLE);
            mButtonSet.setVisibility(View.INVISIBLE);
            mButtonReset.setVisibility(View.INVISIBLE);
            mButtonStartPause.setText(" ");
            mButtonStartPause.setBackgroundResource(R.drawable.pausebtn);
        } else {
            mTextViewCountDown.setTextColor(Color.rgb(153, 153, 153));
            mTextViewCountDown.setPadding(0, 0, 0, 0);
            tv.setVisibility(View.VISIBLE);
            LinearLayout linearLayout = (LinearLayout) findViewById(R.id.layout);
            linearLayout.setPadding(0,0,0,0);
            linearLayout.setVisibility(View.VISIBLE);
            mEditTextInput.setVisibility(View.VISIBLE);
            mButtonSet.setVisibility(View.VISIBLE);
            mButtonStartPause.setText(" ");
            mButtonStartPause.setBackgroundResource(R.drawable.playbtn);
            if (mTimeLeftInMillis < 1000) {
                mButtonStartPause.setVisibility(View.INVISIBLE);
            } else {
                mButtonStartPause.setVisibility(View.VISIBLE);
            }
            if (mTimeLeftInMillis < mStartTimeInMillis) {
                mButtonReset.setVisibility(View.VISIBLE);
            } else {
                mButtonReset.setVisibility(View.INVISIBLE);
            }
        }
    }
    private void closeKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
    @Override
    protected void onStop() {
        super.onStop();
        SharedPreferences prefs = getSharedPreferences("prefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putLong("startTimeInMillis", mStartTimeInMillis);
        editor.putLong("millisLeft", mTimeLeftInMillis);
        editor.putBoolean("timerRunning", mTimerRunning);
        editor.putLong("endTime", mEndTime);
        editor.apply();
        if (mCountDownTimer != null) {
            mCountDownTimer.cancel();
        }
    }
    @Override
    protected void onStart() {
        super.onStart();
        SharedPreferences prefs = getSharedPreferences("prefs", MODE_PRIVATE);
        mStartTimeInMillis = prefs.getLong("startTimeInMillis", 600000);
        mTimeLeftInMillis = prefs.getLong("millisLeft", mStartTimeInMillis);
        mTimerRunning = prefs.getBoolean("timerRunning", false);
        updateCountDownText();
        updateWatchInterface();
        if (mTimerRunning) {
            mEndTime = prefs.getLong("endTime", 0);
            mTimeLeftInMillis = mEndTime - System.currentTimeMillis();
            if (mTimeLeftInMillis < 0) {
                mTimeLeftInMillis = 0;
                mTimerRunning = false;
                updateCountDownText();
                updateWatchInterface();
            } else {
                startTimer();
            }
        }

    }

    //do tu
    @OnClick(R.id.btnStart)
    public void startButton(){
        btnPause.setVisibility(View.VISIBLE);
        btnStart.setVisibility(View.INVISIBLE);
        check = false;
        MyClass myClass = new MyClass();
        myClass.execute(1000, 12, 14, 16);
        hr.setTextColor(Color.WHITE);
        min.setTextColor(Color.WHITE);
        sec.setTextColor(Color.WHITE);
        mil.setTextColor(Color.WHITE);

    }
    @OnClick(R.id.btnContinue)
    public void continueButton() {
        btnPause.setVisibility(View.VISIBLE);
        btnContinue.setVisibility(View.INVISIBLE);
        btnStop.setVisibility(View.INVISIBLE);
        check = false;
        MyClass myClass = new MyClass();
        myClass.execute(1000);
    }
    @OnClick(R.id.btnPause)
    public void pauseButton() {
        btnPause.setVisibility(View.INVISIBLE);
        btnContinue.setVisibility(View.VISIBLE);
        btnStop.setVisibility(View.VISIBLE);
        check = true;
    }
    @OnClick(R.id.btnStop)
    public void stopButton() {
        btnStop.setVisibility(View.INVISIBLE);
        btnStart.setVisibility(View.VISIBLE);
        btnContinue.setVisibility(View.INVISIBLE);
        check = true;
        mil.setText("00");
        sec.setText("00");
        min.setText("00");
        hr.setText("00");
        tempPause = 0;
        setSec = 0;
        setMin = 0;
        setHr = 0;
    }


    public class MyClass extends AsyncTask<Integer, Integer, Long>{

        @Override
        protected Long doInBackground(Integer... params){
            do{
                for( i= tempPause; i<= params[0]; i++){
                    if(check){
                        Log.d("TAG", "doInBackground:Initial Time= "+initialTime);
                        tempPause = i;
                        return 999L;
                    }
                    tempPause = 0;
                    oneLoop();
                    if( i== 999){
                        setSec++;
                    }
                    if(setSec ==60){
                        setMin++;
                        setSec = 0;
                    }
                    if (setMin == 60){
                        setHr++;
                        setMin=0;
                    }
                    publishProgress( i , setSec, setMin, setHr);
                }
            }
            while (!check);
            return 999999999L;
        }
        @Override
        protected void onProgressUpdate (Integer ... values){
            super.onProgressUpdate(values);
            mil.setText(Integer.toString(values[0]));
            if(values[1]<10){
                sec.setText("0" + values[1]);
            }
            else{
                sec.setText(Integer.toString(values[1]));
            }
            if(values[1]<10){
                min.setText("0" + values[2]);
            }
            else{
                min.setText(Integer.toString(values[2]));
            }
            if(values[1]<10){
                hr.setText("0" + values[3]);
            }
            else{
                hr.setText(Integer.toString(values[3]));
            }

        }
        @Override
        protected void onPostExecute(Long s){
            super.onPostExecute(s);
        }
    }
    public static void oneLoop(){
        long startTime = System.currentTimeMillis();
        while (System.currentTimeMillis() - startTime < 1);
    }

    }



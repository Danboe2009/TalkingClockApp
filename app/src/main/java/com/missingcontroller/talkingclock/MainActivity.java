package com.missingcontroller.talkingclock;

import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;
import java.util.Locale;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    TextToSpeech tts;
    Calendar cal;
    int result;
    TextView time;
    Button talk;

    int current;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        tts = new TextToSpeech(MainActivity.this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {
                    result = tts.setLanguage(Locale.UK);
                } else {
                    Toast.makeText(getApplicationContext(), "This feature is not supported", Toast.LENGTH_SHORT).show();
                }
            }
        });
        time = (TextView) findViewById(R.id.timeText);
        talk = (Button) findViewById(R.id.talkB);

        talk.setOnClickListener(this);

        tts.speak(time.getText().toString(), TextToSpeech.QUEUE_FLUSH, null);

        Thread myThread;

        Runnable runnable = new CountDownRunner();
        myThread = new Thread(runnable);
        myThread.start();

        setTime();
    }

    public void doWork() {
        runOnUiThread(new Runnable() {
            public void run() {
                try {
                    setTime();

                    if (current != cal.get(Calendar.MINUTE)) {
                        talk();
                        current = cal.get(Calendar.MINUTE);
                    }
                } catch (Exception e) {
                }
            }
        });
    }


    class CountDownRunner implements Runnable {
        // @Override
        public void run() {
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    doWork();
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } catch (Exception e) {
                }
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.talkB:
                talk();
                break;
            default:
                break;
        }
    }

    public void talk() {
        tts.speak(time.getText().toString(), TextToSpeech.QUEUE_FLUSH, null);
    }

    public void setTime() {
        cal = Calendar.getInstance();
        String now = "" + cal.get(Calendar.HOUR) + ":" + String.format("%02d", cal.get(Calendar.MINUTE)) + " ";
        if(cal.get(Calendar.AM_PM) == 0)
        {
            now += "AM";
        }
        else
        {
            now += "PM";
        }
        time.setText(now);
    }

    protected void onDestroy() {
        super.onDestroy();
        if (tts != null) {
            tts.stop();
            tts.shutdown();
        }
    }
}

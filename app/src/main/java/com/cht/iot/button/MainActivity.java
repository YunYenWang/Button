package com.cht.iot.button;

import android.content.Intent;
import android.speech.RecognizerIntent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.cht.iot.service.api.OpenRESTfulClient;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {
    static final Logger LOG = LoggerFactory.getLogger(MainActivity.class);

    static final int RQS_VOICE_RECOGNITION = 100;

    String apiKey = "DKCEM4BCG1GMYPFPA2";
    OpenRESTfulClient restful = new OpenRESTfulClient("iot.cht.com.tw", 80, apiKey);
    String deviceId = "854322727";
    String sensorId = "Switch";

    ExecutorService executor = Executors.newSingleThreadExecutor();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void onLogoClicked(View view) {
        Intent i = new Intent(this, LightingActivity.class);
        startActivity(i);
    }

    public void onClick(View view) {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "請說出指令"); // TODO - NG
        startActivityForResult(intent, RQS_VOICE_RECOGNITION);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RQS_VOICE_RECOGNITION) {
            if (resultCode == RESULT_OK) {
                List<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                String first = result.get(0);

                Toast.makeText(this, first, Toast.LENGTH_LONG).show();

                if (first.contains("開")) { // TODO - NG
                    executor.execute(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                restful.saveRawdata(deviceId, sensorId, "1");

                            } catch (Exception e) {
                                LOG.error(e.getMessage(), e);
                            }
                        }
                    });

                } else if (first.contains("關")) { // TODO - NG
                    executor.execute(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                restful.saveRawdata(deviceId, sensorId, "0");

                            } catch (Exception e) {
                                LOG.error(e.getMessage(), e);
                            }
                        }
                    });
                }
            }
        }
    }
}

package com.cht.iot.button;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.RelativeLayout;

import com.cht.iot.persistence.entity.data.Rawdata;
import com.cht.iot.service.api.OpenMqttClient;

public class LightingActivity extends AppCompatActivity {

    String apiKey = "DKCEM4BCG1GMYPFPA2";
    String deviceId = "854322727";
    String sensorId = "Switch";

    OpenMqttClient mqtt;
    RelativeLayout layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lighting);

        layout = (RelativeLayout) findViewById(R.id.activity_lighting);

        mqtt = new OpenMqttClient("iot.cht.com.tw", 1883, apiKey);
        mqtt.subscribe(deviceId, sensorId);

        mqtt.setListener(new OpenMqttClient.ListenerAdapter() {
            @Override
            public void onRawdata(String topic, Rawdata rawdata) {
                String[] value = rawdata.getValue();
                if ((value != null) && (value.length > 0)) {
                    String v = value[0];
                    if ("1".equalsIgnoreCase(v)) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                layout.setBackgroundColor(Color.YELLOW);
                            }
                        });

                        return;
                    }

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            layout.setBackgroundColor(Color.BLACK);
                        }
                    });
                }
            }
        });

        mqtt.start();
    }

    @Override
    protected void onDestroy() {
        mqtt.stop();

        super.onDestroy();
    }
}

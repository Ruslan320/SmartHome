package com.example.smarthome.MQTT;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.example.smarthome.activity.MainActivity;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.DisconnectedBufferOptions;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import static android.widget.Toast.LENGTH_LONG;


public class MqttHelper {
    public MqttAndroidClient mqttAndroidClient;

    private final String serverUri = "tcp://192.168.0.185:1884";

    private final String clientId = "AndroidClient";
    private final String subscriptionTopic = "sensors/+";

    private final String username = "admin";
    private final String password = "admin";

    public MqttHelper(Context context){

        mqttAndroidClient = new MqttAndroidClient(context, serverUri, clientId);
        mqttAndroidClient.setCallback(new MqttCallbackExtended() {
            @Override
            public void connectComplete(boolean b, String s) {

            }

            @Override
            public void connectionLost(Throwable throwable) {

            }

            @Override
            public void messageArrived(String topic, MqttMessage mqttMessage) throws Exception {

            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {

            }
        });
        connect();
    }

    public void setCallback(MqttCallbackExtended callback) {
        mqttAndroidClient.setCallback(callback);
    }

    private void connect(){
        MqttConnectOptions mqttConnectOptions = new MqttConnectOptions();
        mqttConnectOptions.setAutomaticReconnect(true);
        mqttConnectOptions.setCleanSession(false);
        mqttConnectOptions.setUserName(username);
        mqttConnectOptions.setPassword(password.toCharArray());

        try {

            mqttAndroidClient.connect(mqttConnectOptions, null, new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {

                    DisconnectedBufferOptions disconnectedBufferOptions = new DisconnectedBufferOptions();
                    disconnectedBufferOptions.setBufferEnabled(true);
                    disconnectedBufferOptions.setBufferSize(100);
                    disconnectedBufferOptions.setPersistBuffer(false);
                    disconnectedBufferOptions.setDeleteOldestMessages(false);
                    mqttAndroidClient.setBufferOpts(disconnectedBufferOptions);
                    subscribeToTopic();
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {

                }
            });


        } catch (MqttException ex){
            ex.printStackTrace();
        }
    }


    private void subscribeToTopic() {
        try {
            mqttAndroidClient.subscribe(subscriptionTopic, 2, null, new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {

                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {

                }
            });

        } catch (MqttException ex) {
            System.err.println("Exception whilst subscribing");
            ex.printStackTrace();
        }
    }

    public void publish(String numOfRoom, String typeOfSensor, String m) {

        String topic;
        topic="sensor/" + numOfRoom + "/" + typeOfSensor + "/get";

        if(mqttAndroidClient.isConnected()) try {
            MqttMessage message = new MqttMessage(m.getBytes());
            message.setQos(2);
            mqttAndroidClient.publish(topic, message);
        } catch (MqttException e) {
            Log.e("Error", "Ошибка при отправке сообщения на MQTT сервер");
        }
    }

    public boolean isConnected() {
        return mqttAndroidClient.isConnected();
    }
    public void disconnect() {
        if (mqttAndroidClient.isConnected()) {
            try {
                mqttAndroidClient.disconnect();
            } catch (MqttException e) {
                e.getMessage();
            }
        }
    }


}
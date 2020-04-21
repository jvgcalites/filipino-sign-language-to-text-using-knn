package com.example.wingoodharry;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    Button btnOn, btnOff;
    TextView txtArduino, txtString, txtStringLength, sensorView0, sensorView1, sensorView2, sensorView3,
    sensorView4, sensorView5, sensorView6, sensorView7, sensorView8, sensorView9, sensorView10, label, accuracy;
    Handler bluetoothIn;

    final int handlerState = 0;                        //used to identify handler message
    private BluetoothAdapter btAdapter = null;
    private BluetoothSocket btSocket = null;
    private StringBuilder recDataString = new StringBuilder();

    private ConnectedThread mConnectedThread;

    // SPP UUID service - this should work for most devices
    private static final UUID BTMODULEUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    // String for MAC address
    private static String address;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main2);

        //Link the buttons and textViews to respective views
        btnOn = (Button) findViewById(R.id.buttonOn);
        btnOff = (Button) findViewById(R.id.buttonOff);
        txtString = (TextView) findViewById(R.id.txtString);
        txtStringLength = (TextView) findViewById(R.id.testView1);
        sensorView0 = (TextView) findViewById(R.id.sensorView0);
        sensorView1 = (TextView) findViewById(R.id.sensorView1);
        sensorView2 = (TextView) findViewById(R.id.sensorView2);
        sensorView3 = (TextView) findViewById(R.id.sensorView3);
        sensorView4 = (TextView) findViewById(R.id.sensorView4);
        sensorView5 = (TextView) findViewById(R.id.sensorView5);
        sensorView6 = (TextView) findViewById(R.id.sensorView6);
        sensorView7 = (TextView) findViewById(R.id.sensorView7);
        sensorView8 = (TextView) findViewById(R.id.sensorView8);
        sensorView9 = (TextView) findViewById(R.id.sensorView9);
        sensorView10 = (TextView) findViewById(R.id.sensorView10);
        label = (TextView) findViewById((R.id.label));
        accuracy = (TextView) findViewById(R.id.accuracy);


        bluetoothIn = new Handler() {
            @SuppressLint("HandlerLeak")
            public void handleMessage(android.os.Message msg) {
                if (msg.what == handlerState) {                                     //if message is what we want
                    String readMessage = (String) msg.obj;                                                                // msg.arg1 = bytes from connect thread
                    recDataString.append(readMessage);                                      //keep appending to string until ~
                    int endOfLineIndex = recDataString.indexOf("~");                    // determine the end-of-line
                    if (endOfLineIndex > 0) {                                           // make sure there data before ~
                        String dataInPrint = recDataString.substring(0, endOfLineIndex);    // extract string
                        txtString.setText("Data Received = " + dataInPrint);
                        int dataLength = dataInPrint.length();                          //get length of data received
                        txtStringLength.setText("String Length = " + String.valueOf(dataLength));

                        if (recDataString.charAt(0) == '#')                             //if it starts with # we know it is what we are looking for
                        {
                            String packet = recDataString.substring(1, endOfLineIndex);
                            String[] values = packet.split(",");
                            String f1 = values[0];
                            String f2 = values[1];
                            String f3 = values[2];
                            String f4 = values[3];
                            String f5 = values[4];
                            String gx = values[5];
                            String gy = values[6];
                            String gz = values[7];
                            String ax = values[8];
                            String ay = values[9];
                            String az = values[10];
                            /*
                            String f1 = recDataString.substring(1, 7);
                            String f2 = recDataString.substring(8, 14);
                            String f3 = recDataString.substring(15, 21);
                            String f4 = recDataString.substring(22, 28);
                            String f5 = recDataString.substring(29, 35);
                            String gx = recDataString.substring(36, 41);
                            String gy = recDataString.substring(42, 47);
                            String gz = recDataString.substring(48, 53);
                            String ax = recDataString.substring(54, 59);
                            String ay = recDataString.substring(60, 65);
                            String az = recDataString.substring(66, 71);
                            */
                            sensorView0.setText(" F1 = " + f1);
                            sensorView1.setText(" F2 = " + f2);
                            sensorView2.setText(" F3 = " + f3);
                            sensorView3.setText(" F4 = " + f4);
                            sensorView4.setText(" F5 = " + f5);
                            sensorView5.setText(" Gx = " + gx);
                            sensorView6.setText(" Gy = " + gy);
                            sensorView7.setText(" Gz = " + gz);
                            sensorView8.setText(" Ax = " + ax);
                            sensorView9.setText(" Ay = " + ay);
                            sensorView10.setText(" Az = " + az);

                            double flex1, flex2, flex3, flex4, flex5, gyrox, gyroy, gyroz, accx, accy, accz;
                            flex1 = Double.parseDouble(f1);
                            flex2 = Double.parseDouble(f2);
                            flex3 = Double.parseDouble(f3);
                            flex4 = Double.parseDouble(f4);
                            flex5 = Double.parseDouble(f5);
                            gyrox = Double.parseDouble(gx);
                            gyroy = Double.parseDouble(gy);
                            gyroz = Double.parseDouble(gz);
                            accx = Double.parseDouble(ax);
                            accy = Double.parseDouble(ay);
                            accz = Double.parseDouble(az);
                            double[] testfeatures = {flex1, flex2, flex3, flex4, flex5, gyrox, gyroy, gyroz, accx, accy, accz};
                            List<double[]> trainfeatures = new ArrayList<>();
                            List<String> trainlabel = new ArrayList<>();
                            BufferedReader reader = null;
                            try {
                                reader = new BufferedReader(
                                        new InputStreamReader(getAssets().open("dataset.txt"), "UTF-8"));

                                // do reading, usually loop until end of file reading
                                String line;
                                while ((line = reader.readLine()) != null) {
                                    //process line
                                    String[] split = line.split(","); // this contains all the values and labels
                                    //System.out.print(split);
                                    double[] feature = new double[split.length - 1]; // this contains the values only, no labels
                                    //System.out.print(feature);
                                    for (int i = 0; i < split.length - 1; i++)
                                        feature[i] = Double.parseDouble(split[i]);
                                    trainfeatures.add(feature);
                                    trainlabel.add(split[feature.length]);
                                }
                            } catch (IOException e) {
                                //log the exception
                            } finally {
                                if (reader != null) {
                                    try {
                                        reader.close();
                                    } catch (IOException e) {
                                        //log the exception
                                    }
                                }
                            }
                            //get the distance between test values and each trained values.
                            double[][] distanceList = new double[trainfeatures.size()][2];
                            for(int index = 0; index < trainfeatures.size(); index++) {
                                //compute the distance
                                double[] features1 = trainfeatures.get(index);
                                double sum = 0;
                                for (int i = 0; i < features1.length; i++)
                                {  //System.out.println(features1[i]+" "+features2[i]);
                                    //applied Euclidean distance formula
                                    sum += Math.pow(features1[i] - testfeatures[i], 2);
                                }
                                double distance = Math.sqrt(sum);
                                //add the computed distance in the list
                                distanceList[index][0] = distance;
                                distanceList[index][1] = index;
                            }
                            //sort that list from least to greatest
                            for(int i = 0; i < distanceList.length; i++)
                            {
                                for(int j = 0; j < distanceList.length -1; j++)
                                {
                                    if(distanceList[j][0] > distanceList[j+1][0])
                                    {
                                        //swap distance
                                        double temp = distanceList[j][0];
                                        distanceList[j][0] = distanceList[j+1][0];
                                        distanceList[j+1][0] = temp;
                                        //as well as label
                                        double temp2 = distanceList[j][1];
                                        distanceList[j][1] = distanceList[j+1][1];
                                        distanceList[j+1][1] = temp2;
                                    }
                                }
                            }
                            //get the "k" index and their label
                            int k = 11;
                            List<String> targets = new ArrayList<>();
                            for(int i = 0; i < k; i++)
                            {
                                //get the trained label of the current index from the list of distance
                                targets.add(trainlabel.get((int)distanceList[i][1]));
                            }
                            //get the most number of label in the list.
                            // Insert all unique strings and update count if a string is not unique.
                            Map<String,Integer> hshmap = new HashMap<String, Integer>();
                            for (String str : targets)
                            {
                                if (hshmap.keySet().contains(str)) // if already exists then update count.
                                    hshmap.put(str, hshmap.get(str) + 1);
                                else
                                    hshmap.put(str, 1); // else insert it in the map.
                            }
                            // Traverse the map for the maximum value.
                            String max_str = "";
                            int maxVal = 0;
                            for (Map.Entry<String,Integer> entry : hshmap.entrySet())
                            {
                                String key = entry.getKey();
                                Integer count = entry.getValue();
                                if (count > maxVal)
                                {
                                    maxVal = count;
                                    max_str = key;
                                }
                                // Condition for the tie.
                                else if (count == maxVal && max_str.compareTo(key) > 0)
                                    max_str = key;
                            }
                            double acc = (maxVal/(double)k)*100;
                            label.setText("Label = " + max_str);
                            accuracy.setText("Accuracy = " + acc + "%");
                        }
                        recDataString.delete(0, recDataString.length());                    //clear all string data
                        // strIncom =" ";
                        dataInPrint = " ";
                    }
                }
            }
        };

        btAdapter = BluetoothAdapter.getDefaultAdapter();       // get Bluetooth adapter
        checkBTState();

        // Set up onClick listeners for buttons to send 1 or 0 to turn on/off LED
        btnOff.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                mConnectedThread.write("0");    // Send "0" via Bluetooth
                Toast.makeText(getBaseContext(), "Turn off LED", Toast.LENGTH_SHORT).show();
            }
        });

        btnOn.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                mConnectedThread.write("1");    // Send "1" via Bluetooth
                Toast.makeText(getBaseContext(), "Turn on LED", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private BluetoothSocket createBluetoothSocket(BluetoothDevice device) throws IOException {

        return  device.createRfcommSocketToServiceRecord(BTMODULEUUID);
        //creates secure outgoing connecetion with BT device using UUID
    }

    @Override
    public void onResume() {
        super.onResume();

        //Get MAC address from DeviceListActivity via intent
        Intent intent = getIntent();

        //Get the MAC address from the DeviceListActivty via EXTRA
        address = intent.getStringExtra(DeviceListActivity.EXTRA_DEVICE_ADDRESS);

        //create device and set the MAC address
        BluetoothDevice device = btAdapter.getRemoteDevice(address);

        try {
            btSocket = createBluetoothSocket(device);
        } catch (IOException e) {
            Toast.makeText(getBaseContext(), "Socket creation failed", Toast.LENGTH_LONG).show();
        }
        // Establish the Bluetooth socket connection.
        try
        {
            btSocket.connect();
        } catch (IOException e) {
            try
            {
                btSocket.close();
            } catch (IOException e2)
            {
                //insert code to deal with this
            }
        }
        mConnectedThread = new ConnectedThread(btSocket);
        mConnectedThread.start();

        //I send a character when resuming.beginning transmission to check device is connected
        //If it is not an exception will be thrown in the write method and finish() will be called
        mConnectedThread.write("x");
    }

    @Override
    public void onPause()
    {
        super.onPause();
        try
        {
            //Don't leave Bluetooth sockets open when leaving activity
            btSocket.close();
        } catch (IOException e2) {
            //insert code to deal with this
        }
    }

    //Checks that the Android device Bluetooth is available and prompts to be turned on if off
    private void checkBTState() {

        if(btAdapter==null) {
            Toast.makeText(getBaseContext(), "Device does not support bluetooth", Toast.LENGTH_LONG).show();
        } else {
            if (btAdapter.isEnabled()) {
            } else {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, 1);
            }
        }
    }

    //create new class for connect thread
    private class ConnectedThread extends Thread {
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;

        //creation of the connect thread
        public ConnectedThread(BluetoothSocket socket) {
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            try {
                //Create I/O streams for connection
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) { }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }

        public void run() {
            byte[] buffer = new byte[256];
            int bytes;

            // Keep looping to listen for received messages
            while (true) {
                try {
                    bytes = mmInStream.read(buffer);            //read bytes from input buffer
                    String readMessage = new String(buffer, 0, bytes);
                    // Send the obtained bytes to the UI Activity via handler
                    bluetoothIn.obtainMessage(handlerState, bytes, -1, readMessage).sendToTarget();
                } catch (IOException e) {
                    break;
                }
            }
        }
        //write method
        public void write(String input) {
            byte[] msgBuffer = input.getBytes();           //converts entered String into bytes
            try {
                mmOutStream.write(msgBuffer);                //write bytes over BT connection via outstream
            } catch (IOException e) {
                //if you cannot write, close the application
                Toast.makeText(getBaseContext(), "Connection Failure", Toast.LENGTH_LONG).show();
                finish();

            }
        }
    }
}


package android.textapp.textappandroid;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;


public class Startup extends Activity {
    public Socket socket;
    BufferedReader in;
    public static PrintWriter out;

    BroadcastReceiver reciever = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_startup);
        new android.textapp.textappandroid.SmsManager();
        //IntentFilter filter = new IntentFilter("android.provider.Telephony.SMS_RECEIVED");

        startThread();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_startup, menu);
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

    /**
     * Sends text meesage to supplied phone number
     *
     * @param recieversNumber Phone number of text reciever
     * @param message         Message to send
     */
    public void sendSMS(String recieversNumber, String message) {
        SmsManager.getDefault().sendTextMessage(recieversNumber, null, message, null, null);
    }

    public void startThread()
    {
        try {
            Thread cThread = new Thread(new TextThread());
            cThread.start();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public class TextThread implements Runnable
    {

        public TextThread()
        {  }

        @Override
        public void run()
        {
            Log.d("Started", "Thread Started");
            try
            {
                socket = new Socket("192.168.1.123", 1000);
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);

                out.println("3347504677");

                Log.i("Started", "Connection Made");
            } catch (IOException e)
            {
                e.printStackTrace();
                return;
            }

            while(true)
            {
                try {
                    String text = in.readLine();
                    String[] newText = text.split(": ");
                    Log.d("Message Sent", "Addr: " + newText[0] + ", Message: " + newText[1]);
                    sendSMS(newText[0], newText[1]);
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }

        }
    }
}

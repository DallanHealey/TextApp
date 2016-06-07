package android.textapp.textappandroid;

import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;

public class SmsManager extends BroadcastReceiver
{
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("Received", "Got message");
        // Get the SMS map from Intent
        Bundle extras = intent.getExtras();

        final String SMS_EXTRA_NAME = "pdus";
        String messages = "";

        if ( extras != null )
        {
            // Get received SMS array
            Object[] smsExtra = (Object[]) extras.get( SMS_EXTRA_NAME );

            ContentResolver contentResolver = context.getContentResolver();
            SmsMessage sms = null;
            for ( int i = 0; i < smsExtra.length; ++i )
            {
                sms = SmsMessage.createFromPdu((byte[])smsExtra[i]);

                String body = sms.getMessageBody().toString();
                String address = sms.getOriginatingAddress();

                messages += address + ": ";
                messages += body;
            }
            //Cursor cursor = contentResolver.query(Uri.parse("content://sms/inbox"), null, null, null, null);

            Startup.out.println(messages);
        }
    }
}


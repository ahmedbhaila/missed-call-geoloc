package mycompany.com.enhancedcallnotifier;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.widget.Toast;

public class IncomingCallReceiver extends BroadcastReceiver {

    static boolean ring=false;
    static boolean callReceived=false;
    String callerPhoneNumber = null;
    String googleAccount = "demo;";


    public IncomingCallReceiver() {

    }

    private void getGoogleAccount(Context context) {
        AccountManager manager = (AccountManager) context.getSystemService(context.ACCOUNT_SERVICE);
        Account[] list = manager.getAccounts();
        if (list.length > 0) {
            googleAccount = list[0].name;
        }
    }
    @Override
    public void onReceive(Context context, Intent intent) {

        getGoogleAccount(context);

        // Get the current Phone State
        String state = intent.getStringExtra(TelephonyManager.EXTRA_STATE);



        // If phone state "Ringing"
        if(state.equals(TelephonyManager.EXTRA_STATE_RINGING))
        {
            ring =true;
            // Get the Caller's Phone Number
            Bundle bundle = intent.getExtras();
            callerPhoneNumber= bundle.getString("incoming_number");
        }

        // If incoming call is received
        if(state.equals(TelephonyManager.EXTRA_STATE_OFFHOOK))
        {
            callReceived=true;
        }

        // phone is idle
        if (state.equals(TelephonyManager.EXTRA_STATE_IDLE)) {
// detect missed call
            if (ring == true && callReceived == false) {

                Toast.makeText(context,
                        "Got a missed call from:"+ callerPhoneNumber,
                        Toast.LENGTH_LONG).show();


                new HttpRequestTask(context).execute(googleAccount, callerPhoneNumber);


                Toast.makeText(context, "Missed", Toast.LENGTH_LONG).show();
            }

            ring = false;
            callReceived = false;
        }


    }
}

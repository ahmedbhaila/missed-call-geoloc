package mycompany.com.enhancedcallnotifier;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;

/**
 * Created by root on 7/13/15.
 */
public class HttpRequestTask extends AsyncTask<String, String, String> {

    protected Context context;
    protected String missedCallNumber;
    protected String clientId;

    public HttpRequestTask(Context context) {
        this.context = context;
    }
    @Override
    protected String doInBackground(String... params) {
        try {
            final String url = "https://enigmatic-taiga-3619.herokuapp.com/locate/{client_id}/{number}/lookup";
            RestTemplate restTemplate = new RestTemplate();
            restTemplate.getMessageConverters().add(new StringHttpMessageConverter());
            HashMap<String, String> param = new HashMap<>();
            param.put("client_id", params[0]);
            param.put("number", params[1]);
            String status = restTemplate.getForObject(url, String.class, param);
            missedCallNumber = params[1];
            clientId = params[0];
            return status;
        } catch (Exception e) {
            Log.e("MainActivity", e.getMessage(), e);
        }

        return null;
    }

    @Override
    protected void onPostExecute(String status) {
        if(status.equals("0")) {
            NotificationCompat.Builder mBuilder =
                    new NotificationCompat.Builder(context)
                            .setSmallIcon(R.drawable.abc_ab_share_pack_mtrl_alpha)
                            .setContentTitle("Enhanced Call Information Available")
                            .setContentText("Enhanced Call Information available for this missed called: " + missedCallNumber);

            String url = "https://enigmatic-taiga-3619.herokuapp.com/map/" + clientId;
            Intent resultIntent = new Intent(Intent.ACTION_VIEW);
            resultIntent.setData(Uri.parse(url));

            PendingIntent resultPendingIntent =
                    PendingIntent.getActivity(
                            context,
                            0,
                            resultIntent,
                            PendingIntent.FLAG_UPDATE_CURRENT
                    );
            mBuilder.setContentIntent(resultPendingIntent);

            // Sets an ID for the notification
            int mNotificationId = 001;
            // Gets an instance of the NotificationManager service
            NotificationManager mNotifyMgr =
                    (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);
            // Builds the notification and issues it.
            mNotifyMgr.notify(mNotificationId, mBuilder.build());

        }


    }

}

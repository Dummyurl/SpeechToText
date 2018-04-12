package info.pratham.speechtotext.syncoperations;

/**
 * Created by Pravin on 03/02/2018.
 */

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import info.pratham.speechtotext.FormActivity;

public class NetworkChangeReceiver extends BroadcastReceiver{

    @Override
    public void onReceive(final Context context, final Intent intent) {

        String status = NetworkUtil.getConnectivityStatusString(context);
        //Toast.makeText(context, "Status : "+status, Toast.LENGTH_LONG).show();
        if(NetworkUtil.getConnectivityStatus(context) != 0) {
            FormActivity.SyncProcess syncProcess = new FormActivity.SyncProcess(context);
            syncProcess.createJsonforTransfer();
        }
    }
}

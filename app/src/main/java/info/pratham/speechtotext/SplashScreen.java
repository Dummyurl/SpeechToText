package info.pratham.speechtotext;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.widget.Button;

import static info.pratham.speechtotext.syncoperations.NetworkUtil.getConnectivityStatus;

public class SplashScreen extends ActivityManagePermission implements PermissionResult {

    Handler handler;
    Button btn_start;
    Dialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        getSupportActionBar().hide();
        handler = new Handler();

        btn_start = (Button) findViewById(R.id.btn_start);

        String[] permissionArray = new String[]{PermissionUtils.Manifest_READ_EXTERNAL_STORAGE,
                PermissionUtils.Manifest_WRITE_EXTERNAL_STORAGE,
                PermissionUtils.Manifest_RECORD_AUDIO};

        if ( Build.VERSION.SDK_INT >= Build.VERSION_CODES.M ) {

            if ( !isPermissionsGranted(SplashScreen.this, permissionArray) ) {
                askCompactPermissions(permissionArray, this);
            }
        }

        btn_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkInternet();
            }
        });
    }

    public void checkInternet() {
        int res = getConnectivityStatus(SplashScreen.this);
        if (res == 0)
            showDialog();
        else
            gotoNext();
    }

    public void gotoNext() {
        Intent intent = new Intent(SplashScreen.this, FormActivity.class);
        startActivity(intent);
    }

    public void showDialog() {

        dialog = new Dialog(SplashScreen.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setContentView(R.layout.custom_dialog_net);
        dialog.setCanceledOnTouchOutside(false);
        Button button = dialog.findViewById(R.id.dialog_btn_ok);
        dialog.show();

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

    }

    @Override
    public void permissionGranted() {
    }

    @Override
    public void permissionDenied() {
    }

    @Override
    public void permissionForeverDenied() {
    }

}

package info.pratham.speechtotext;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

public class SplashScreen extends ActivityManagePermission implements PermissionResult {
    Handler handler;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        handler = new Handler();

        String[] permissionArray = new String[]{PermissionUtils.Manifest_READ_EXTERNAL_STORAGE,
                PermissionUtils.Manifest_WRITE_EXTERNAL_STORAGE,
                PermissionUtils.Manifest_RECORD_AUDIO};

        if (!isPermissionsGranted(SplashScreen.this, permissionArray)) {
            askCompactPermissions(permissionArray, this);
        }else{
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent intent = new Intent(SplashScreen.this, FormActivity.class);
                    startActivity(intent);
                }
            }, 2000);
        }
    }

    @Override
    public void permissionGranted() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(SplashScreen.this, FormActivity.class);
                startActivity(intent);
            }
        }, 2000);

    }

    @Override
    public void permissionDenied() {

    }

    @Override
    public void permissionForeverDenied() {

    }
}

package info.pratham.speechtotext;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;

public class SplashScreen extends ActivityManagePermission implements PermissionResult {

    Handler handler;
    Button btn_start;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        handler = new Handler();

        btn_start = (Button) findViewById(R.id.btn_start);

        String[] permissionArray = new String[]{PermissionUtils.Manifest_READ_EXTERNAL_STORAGE,
                PermissionUtils.Manifest_WRITE_EXTERNAL_STORAGE,
                PermissionUtils.Manifest_RECORD_AUDIO};

        if (!isPermissionsGranted(SplashScreen.this, permissionArray)) {
            askCompactPermissions(permissionArray, this);
        }

        btn_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoNext();
            }
        });
    }

    public void gotoNext() {
        Intent intent = new Intent(SplashScreen.this, FormActivity.class);
        startActivity(intent);
    }

    @Override
    public void permissionGranted() { }

    @Override
    public void permissionDenied() { }

    @Override
    public void permissionForeverDenied() { }

}

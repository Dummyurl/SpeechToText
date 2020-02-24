package info.pratham.speechtotext;

import android.app.Application;
import android.content.Context;
import android.media.AudioManager;
import android.os.StrictMode;
import android.util.Log;
import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;

public class ApplicationClass extends Application {

    //    public static String uploadDataUrl = "http://prodigi.openiscool.org/api/cosv2/pushdata";
    public static String uploadDataUrl = "http://devprodigi.openiscool.org/api/Foundation/PushData";
    public static final boolean isTablet = true;
    public static boolean contentExistOnSD = false, LocationFlg = false;
    public static String contentSDPath = "";
    public static String foundationPath = "";
    public static String App_Thumbs_Path = "/.FCA/App_Thumbs/";
    OkHttpClient okHttpClient;
    public static ApplicationClass applicationClass;
    private static final DateFormat dateTimeFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.ENGLISH);
    private static final DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH);
    public static String path;
    public static AudioManager audioManager;

    @Override
    public void onCreate() {
        super.onCreate();
        //this way the VM ignores the file URI exposure. if commented, the camera crashes on open
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        if (applicationClass == null) {
            applicationClass = this;
        }

        okHttpClient = new OkHttpClient().newBuilder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS)
                .build();
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
    }

    public static UUID getUniqueID() {
        return UUID.randomUUID();
    }

    public static ApplicationClass getInstance() {
        return applicationClass;
    }

    public static int getRandomNumber(int min, int max) {
        return min + (new Random().nextInt(max));
    }

}

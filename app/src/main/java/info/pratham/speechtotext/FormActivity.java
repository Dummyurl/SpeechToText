package info.pratham.speechtotext;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.UUID;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import info.pratham.speechtotext.syncoperations.NetworkChangeReceiver;


public class FormActivity extends AppCompatActivity{

    public Button btnSubmit, btnServer;
    public EditText et_name, et_age, et_location, et_education, et_phno;
    DBHelper dbHelper;
    String uName, uAge, uLocation, uEducation, uPhno;
    String internalStorgePath, strData;
    ArrayList<String> path = new ArrayList<String>();
    public static final int RequestPermissionCode = 1;
    static JSONArray readingData;
    NetworkChangeReceiver networkChangeReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form);
        readingData = getJsonData();
        dbHelper = new DBHelper(this);
        btnSubmit = (Button) findViewById(R.id.btnSubmit);
        btnServer = (Button) findViewById(R.id.btnServer);
        et_name = (EditText) findViewById(R.id.etName);
        et_age = (EditText) findViewById(R.id.etAge);
        et_location = (EditText) findViewById(R.id.etPlace);
        et_education = (EditText) findViewById(R.id.etEdu);
        et_phno = (EditText) findViewById(R.id.etPhoneNo);

        networkChangeReceiver = new NetworkChangeReceiver();


        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                File file = new File(Environment.getExternalStorageDirectory().toString() + "/.PrathamSTT");
                if (!file.exists())
                    file.mkdir();

                file = new File(Environment.getExternalStorageDirectory().toString() + "/.PrathamSTT/STTContent");
                if (!file.exists())
                    file.mkdir();

                file = new File(Environment.getExternalStorageDirectory().toString() + "/.PrathamSTT/ZipFiles");
                if (!file.exists())
                    file.mkdir();

                file = new File(Environment.getExternalStorageDirectory().toString() + "/.PrathamSTT/STTContent/Recordings");
                if (!file.exists())
                    file.mkdir();

                /*uName = "Goku";*/

                uName = String.valueOf(et_name.getText());
                uAge = String.valueOf(et_age.getText());
                uLocation = String.valueOf(et_location.getText());
                uEducation = String.valueOf(et_education.getText());
                uPhno = String.valueOf(et_phno.getText());

                if (!uName.equalsIgnoreCase("") || !uAge.equalsIgnoreCase("") || !uLocation.equalsIgnoreCase("") || !uEducation.equalsIgnoreCase("") || !uPhno.equalsIgnoreCase("")) {

                    myUser user = new myUser();
                    MyDBHelper myDBHelper = new MyDBHelper(FormActivity.this);

                    user.Id = String.valueOf(UUID.randomUUID());
                    user.Name = uName;
                    user.Age = uAge;
                    user.Location = uLocation;
                    user.Education = uEducation;
                    user.Phone = uPhno;

                    myDBHelper.AddUser(user);
                    BackupDatabase.backup(FormActivity.this);

                    et_name.setText("");
                    et_age.setText("");
                    et_location.setText("");
                    et_education.setText("");
                    et_phno.setText("");

                    Intent intent;
                    intent = new Intent(FormActivity.this, MainActivity.class);
                    intent.putExtra("uName", uName);
                    startActivity(intent);
                } else {
                    Toast.makeText(FormActivity.this, "Please feel all details", Toast.LENGTH_SHORT).show();
                }

/*                Intent intent;
                intent = new Intent(FormActivity.this, MainActivity.class);
                intent.putExtra("uName", uName);
                startActivity(intent);*/
            }
        });

        btnServer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String paths = Environment.getExternalStorageDirectory().toString() + "/.PrathamSTT/STTContent/";
                File file = new File(paths + "Recordings");
                int length = file.listFiles().length;
//                if (length > 0) {
                zipFileAtPath(paths, Environment.getExternalStorageDirectory() + "/.PrathamSTT/ZipFiles/STT-" + String.valueOf(UUID.randomUUID()) + "Zip.zip");
                deleteRecursive(file);
                deleteRecursive(file);
                deleteDBEntries();
                BackupDatabase.backup(FormActivity.this);
                Toast.makeText(FormActivity.this, "Zip Created at InternalStorage/.PrathamSTT/ZipFiles", Toast.LENGTH_LONG).show();
/*                } else {
                    Toast.makeText(FormActivity.this, "No File(s) Found", Toast.LENGTH_LONG).show();
                }*/


            }


        });

    }

    public void deleteDBEntries() {
        MyDBHelper myDBHelper = new MyDBHelper(this);
        boolean success = myDBHelper.deleteAllEntries();
        Log.d("Success", String.valueOf(success));
    }


    void deleteRecursive(File fileOrDirectory) {
        if (fileOrDirectory.isDirectory())
            for (File child : fileOrDirectory.listFiles())
                deleteRecursive(child);
        fileOrDirectory.delete();
        File file = new File(Environment.getExternalStorageDirectory().toString() + "/.PrathamSTT/STTContent/Recordings");
        if (!file.exists())
            file.mkdir();

    }

    /***********************************************************************************************/
    public boolean zipFileAtPath(String sourcePath, String toLocation) {
        final int BUFFER = 2048;

        File sourceFile = new File(sourcePath);
        try {
            BufferedInputStream origin = null;
            FileOutputStream dest = new FileOutputStream(toLocation);
            ZipOutputStream out = new ZipOutputStream(new BufferedOutputStream(
                    dest));
            if (sourceFile.isDirectory()) {
                zipSubFolder(out, sourceFile, sourceFile.getParent().length());
            } else {
                byte data[] = new byte[BUFFER];
                FileInputStream fi = new FileInputStream(sourcePath);
                origin = new BufferedInputStream(fi, BUFFER);
                ZipEntry entry = new ZipEntry(getLastPathComponent(sourcePath));
                out.putNextEntry(entry);
                int count;
                while ((count = origin.read(data, 0, BUFFER)) != -1) {
                    out.write(data, 0, count);
                }
            }
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    private void zipSubFolder(ZipOutputStream out, File folder,
                              int basePathLength) throws IOException {

        final int BUFFER = 2048;

        File[] fileList = folder.listFiles();
        BufferedInputStream origin = null;
        for (File file : fileList) {
            if (file.isDirectory()) {
                zipSubFolder(out, file, basePathLength);
            } else {
                byte data[] = new byte[BUFFER];
                String unmodifiedFilePath = file.getPath();
                String relativePath = unmodifiedFilePath
                        .substring(basePathLength);
                FileInputStream fi = new FileInputStream(unmodifiedFilePath);
                origin = new BufferedInputStream(fi, BUFFER);
                ZipEntry entry = new ZipEntry(relativePath);
                out.putNextEntry(entry);
                int count;
                while ((count = origin.read(data, 0, BUFFER)) != -1) {
                    out.write(data, 0, count);
                }
                origin.close();
            }
        }
    }

    public String getLastPathComponent(String filePath) {
        String[] segments = filePath.split("/");
        if (segments.length == 0)
            return "";
        String lastPathComponent = segments[segments.length - 1];
        return lastPathComponent;
    }

    /***********************************************************************************************/

    public JSONArray getJsonData() {
        JSONArray returnStoryNavigate = null;
        try {
            InputStream is = getAssets().open("LanguageData.json");/*new FileInputStream("file:///android_assets/LanguageData.json");*/
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            JSONObject jsonObj = new JSONObject(new String(buffer));
            returnStoryNavigate = jsonObj.getJSONArray("data");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return returnStoryNavigate;
    }

    @Override
    protected void onResume() {
        super.onResume();
        networkChangeReceiver.onReceive(FormActivity.this, new Intent());
//        MyBroadcastReceiver myBroadcastReceiver = new MyBroadcastReceiver();
    }

    public class MyBroadcastReceiver extends BroadcastReceiver {
        private static final String TAG = "MyBroadcastReceiver";
        @Override
        public void onReceive(Context context, Intent intent) {
            StringBuilder sb = new StringBuilder();
            sb.append("Action: " + intent.getAction() + "\n");
            sb.append("URI: " + intent.toUri(Intent.URI_INTENT_SCHEME).toString() + "\n");
            String log = sb.toString();
            Log.d(TAG, log);
            Toast.makeText(context, "MyBroadcastReceiver "+log, Toast.LENGTH_LONG).show();
        }
    }
}

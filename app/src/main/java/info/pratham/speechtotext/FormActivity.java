package info.pratham.speechtotext;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import info.pratham.speechtotext.syncoperations.NetworkChangeReceiver;
import info.pratham.speechtotext.syncoperations.SyncUtility;


public class FormActivity extends AppCompatActivity {

    public Button btnSubmit, btnSync;
    public EditText et_name, et_age, et_location, et_education, et_phno;
    DBHelper dbHelper;
    String uName, uAge, uLocation, uEducation, uPhno, transferFileName,pushAPI;
    String internalStorgePath, strData, pushFileName;
    File[] filesForBackup;
    ArrayList<String> path = new ArrayList<String>();
    public static final int RequestPermissionCode = 1;
    static JSONArray readingData;
    int cnt = 0,allFiles=0;
    int [] fileCount;
    Boolean sentFlag = false;
    NetworkChangeReceiver networkChangeReceiver;
    public static ProgressDialog progress;

    private static final DateFormat dateTimeFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.ENGLISH);

    public static String getCurrentDateTime() {
        Calendar cal = Calendar.getInstance();
        return dateTimeFormat.format(cal.getTime());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form);
        readingData = getJsonData();
        pushFileName = "SttAppData-";
        pushAPI="http://prodigi.openiscool.org/api/pushdata/pushdata";
        transferFileName = "SttAppData-";
        dbHelper = new DBHelper(this);
        btnSubmit = (Button) findViewById(R.id.btnSubmit);
        btnSync = (Button) findViewById(R.id.btnSync);
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

                file = new File(Environment.getExternalStorageDirectory().toString() + "/.PrathamSTT/JsonsToPush");
                if (!file.exists())
                    file.mkdir();

                file = new File(Environment.getExternalStorageDirectory().toString() + "/.PrathamSTT/jsonsBackUp");
                if (!file.exists())
                    file.mkdir();

                /*uName = "Goku";*/

                uName = String.valueOf(et_name.getText());
                uAge = String.valueOf(et_age.getText());
                uLocation = String.valueOf(et_location.getText());
                uEducation = String.valueOf(et_education.getText());
                uPhno = String.valueOf(et_phno.getText());

                if (!uName.equalsIgnoreCase("") || !uAge.equalsIgnoreCase("") || !uLocation.equalsIgnoreCase("") || !uEducation.equalsIgnoreCase("") || !uPhno.equalsIgnoreCase("")) {

                    MyUser user = new MyUser();
                    MyDBHelper myDBHelper = new MyDBHelper(FormActivity.this);

                    String studentId = String.valueOf(UUID.randomUUID());

                    user.Id = studentId;
                    user.Name = uName;
                    user.Age = uAge;
                    user.Location = uLocation;
                    user.Education = uEducation;
                    user.Phone = uPhno;
                    user.Date = ""+getCurrentDateTime();

                    myDBHelper.AddUser(user);
                    BackupDatabase.backup(FormActivity.this);

                    et_name.setText("");
                    et_age.setText("");
                    et_location.setText("");
                    et_education.setText("");
                    et_phno.setText("");

                    Intent intent;
                    intent = new Intent(FormActivity.this, MainActivity.class);
                    intent.putExtra("uId", studentId);
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

        btnSync.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createJsonforTransfer();
                networkChangeReceiver.onReceive(FormActivity.this, new Intent());
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
        btnSync.performClick();
//        MyBroadcastReceiver myBroadcastReceiver = new MyBroadcastReceiver();
    }

    public void createJsonforTransfer() {

        MyDBHelper myDBHelper = new MyDBHelper(this);
        MyUser user = new MyUser();

        List<MyUser> myUserList = myDBHelper.getAllUserData();

        try {

            JSONArray usetData = new JSONArray(),
                    sttData = new JSONArray();

            for (int i = 0; i < myUserList.size(); i++) {
                JSONObject _obj = new JSONObject();
                MyUser myUsersss = myUserList.get(i);

                try {
                    _obj.put("Id", myUsersss.getId());
                    _obj.put("Name", myUsersss.getName());
                    _obj.put("Age", myUsersss.getAge());
                    _obj.put("Location", myUsersss.getLocation());
                    _obj.put("Education", myUsersss.getEducation());
                    _obj.put("Phone", myUsersss.getPhone());
                    _obj.put("Date", myUsersss.getDate());
                    usetData.put(_obj);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            {


                if (myUserList!=null) {

                    List<SttData> mySttList = myDBHelper.getAllSttData();
                    JSONObject sttObj;

                    if (mySttList != null) {
                        for (int i = 0; i < mySttList.size(); i++) {
                            sttObj = new JSONObject();
                            sttObj.put("ReordId", mySttList.get(i).getReordId());
                            sttObj.put("UserID", mySttList.get(i).getUserID());
                            sttObj.put("OriginalText", mySttList.get(i).getOriginalText());
                            sttObj.put("VoiceText", mySttList.get(i).getVoiceText());
                            sttObj.put("Date", mySttList.get(i).getDateTime());

                            sttData.put(sttObj);
                        }
                    }

                    JSONObject metaDataObj = new JSONObject();
                    metaDataObj.put("UserData", usetData.length());
                    metaDataObj.put("STTData", sttData.length());
                    metaDataObj.put("TransId", UUID.randomUUID().toString());
                    metaDataObj.put("TransDate", getCurrentDateTime());

                    String requestString = "{ \"metadata\": " + metaDataObj +
                            ", \"UserData\": " + usetData +
                            ", \"STTData\": " + sttData + "}";
                    transferFileName = "SttAppData-" + String.valueOf(UUID.randomUUID());
                    WriteSettings(this, requestString, transferFileName);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        //      }
    }

    public void WriteSettings(Context context, String data, String fName) {

        FileOutputStream fOut = null;
        OutputStreamWriter osw = null;

        try {
            String MainPath;
            MainPath = Environment.getExternalStorageDirectory() + "/.PrathamSTT/JsonsToPush/" + fName + ".json";
            File file = new File(MainPath);
            try {
                path.add(MainPath);
                fOut = new FileOutputStream(file);
                osw = new OutputStreamWriter(fOut);
                osw.write(data);
                osw.flush();
                osw.close();
                fOut.close();

            } catch (Exception e) {
                e.printStackTrace();
            }
            pushToServer();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(context, "Settings not saved", Toast.LENGTH_SHORT).show();
        }
    }

    public void pushToServer() throws IOException, ExecutionException, InterruptedException {

        cnt=0;
        allFiles=0;

        // Checking Internet Connection
        SyncUtility syncUtility = new SyncUtility(this);

        if (SyncUtility.isDataConnectionAvailable(this)) {

/*            Toast.makeText(AdminConsole.this, "Connected to the Internet !!!", Toast.LENGTH_SHORT).show();*/

            //Moving to Receive usage
            String path;
                path = Environment.getExternalStorageDirectory().toString() + "/.PrathamSTT/JsonsToPush";

            String destFolder = Environment.getExternalStorageDirectory() + "/.PrathamSTT/jsonsBackUp";

            Log.d("path", "pushToServer: " + path);

            File sttDir = new File(path);
            if (sttDir.exists()) {
                progress = new ProgressDialog(FormActivity.this);
                progress.setMessage("Please Wait...");
                progress.setCanceledOnTouchOutside(false);
                progress.show();

                File[] files = sttDir.listFiles();
                filesForBackup = sttDir.listFiles();

                for(int i=0; i<files.length;i++) {
                    if (files[i].getName().contains(pushFileName))
                        allFiles++;
                }
                fileCount = new int[files.length];
//                Toast.makeText(this, "Pushing data to server Please wait...", Toast.LENGTH_SHORT).show();
                for (int i = 0; i < files.length; i++) {
                    /*  cnt++;*/
                    if (files[i].getName().contains(pushFileName)) {
                        try {
                            startPushing(convertToString(files[i]), syncUtility, i, destFolder);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }

                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            if (cnt == 0) {
                                progress.dismiss();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }, 1000);
            }
        }/* else {
            Toast.makeText(AdminConsole.this, "Please Connect to the Internet !!!", Toast.LENGTH_SHORT).show();
        }*/

    }

    public void startPushing(String jasonDataToPush, SyncUtility syncUtility, int fileNo, String destinationFolder) throws Exception {

        ArrayList<String> arrayListToTransfer = new ArrayList<String>();
        arrayListToTransfer.add(jasonDataToPush);
        Log.d("pushedJson :::", jasonDataToPush);
        new AsyncTaskRunner(syncUtility, jasonDataToPush, fileNo, destinationFolder).execute();
    }

    public String convertToString(File file) throws IOException {
        int length = (int) file.length();
        FileInputStream in = null;
        byte[] bytes = new byte[length];
        try {
            in = new FileInputStream(file);
            in.read(bytes);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            in.close();
        }
        String contents = new String(bytes);
        return contents;
    }

    public static void fileCutPaste(File toMove, String destFolder) {
        try {
            File destinationFolder = new File(destFolder);
            File destinationFile = new File(destFolder + "/" + toMove.getName());
            if (!destinationFolder.exists()) {
                destinationFolder.mkdir();
            }
            FileInputStream fileInputStream = new FileInputStream(toMove);
            FileOutputStream fileOutputStream = new FileOutputStream(destinationFile);

            int bufferSize;
            byte[] bufffer = new byte[512];
            while ((bufferSize = fileInputStream.read(bufffer)) > 0) {
                fileOutputStream.write(bufffer, 0, bufferSize);
            }
            toMove.delete();
            fileInputStream.close();
            fileOutputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private class AsyncTaskRunner extends AsyncTask<String, String, String> {

        private String resp;
        //        ProgressDialog progressDialog;
        SyncUtility syncUtility;
        String jasonDataToPush, folderForBackup;
        int currentFileNo;


        public AsyncTaskRunner(SyncUtility syncUtility, String jasonDataToPush, int currentFileNo, String folderForBackup) {
            this.syncUtility = syncUtility;
            this.jasonDataToPush = jasonDataToPush;
            this.currentFileNo = currentFileNo;
            this.folderForBackup = folderForBackup;
        }

        @Override
        protected String doInBackground(String... params) {
            publishProgress("Sleeping..."); // Calls onProgressUpdate()
            try {
                String pushResult = syncUtility.sendData(pushAPI, jasonDataToPush);
                Log.d("pushResult", pushResult);
                if (pushResult.equalsIgnoreCase("success")) {
                    ////// incriment count
                    cnt++;
                    fileCutPaste(filesForBackup[currentFileNo], folderForBackup);


                    if(cnt == allFiles)
                        sentFlag = true;
                }

            } catch (Exception e) {
                e.printStackTrace();
                resp = e.getMessage();
            }
            return resp;
        }


        @Override
        protected void onPostExecute(String result) {
            if (cnt == allFiles) {
                progress.dismiss();
                deleteDBEntries();
            } else if (!sentFlag && (cnt == allFiles) ) {
                progress.dismiss();
                deleteDBEntries();
            }
        }


        @Override
        protected void onPreExecute() { }


        @Override
        protected void onProgressUpdate(String... text) { }
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
            Toast.makeText(context, "MyBroadcastReceiver " + log, Toast.LENGTH_LONG).show();
        }
    }
}

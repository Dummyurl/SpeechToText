package info.pratham.speechtotext;

import android.content.Context;

/**
 * Created by Ameya on 24-Oct-17.
 */

public class MyDBHelper extends DBHelper {

    Context context;
    String USERTABLE = "SttUser";
    String TEXTTABLE = "SttText";

    public MyDBHelper(Context context) {
        super(context);
        this.context = context;
        database = this.getWritableDatabase();
    }


    public boolean AddUser(myUser myuser) {
        try {
            database = this.getWritableDatabase();
            PopulateContentValues(myuser);
            long resultCount = database.insert(USERTABLE, null, contentValues);
            database.close();
            if (resultCount == -1)
                return false;
            else
                return true;
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
    }

    public boolean deleteAllEntries() {
        try {
            database = this.getWritableDatabase();
            database.execSQL("delete from "+ USERTABLE);
            database.execSQL("delete from "+ TEXTTABLE);
            return true;
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
    }

    private void PopulateContentValues(myUser myuser) {
        contentValues.put("UserId", myuser.Id.toString());
        contentValues.put("UserName", myuser.Name);
        contentValues.put("Place", myuser.Location);
        contentValues.put("Age", myuser.Age);
        contentValues.put("HighestEducation", myuser.Education);
        contentValues.put("PhoneNo", myuser.Phone);
    }


    public boolean AddSttText(myUser myuser) {
        try {
            database = this.getWritableDatabase();
            AddTextContentValues(myuser);
            long resultCount = database.insert(TEXTTABLE, null, contentValues);
            database.close();
            if (resultCount == -1)
                return false;
            else
                return true;
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
    }

    private void AddTextContentValues(myUser myuser) {
        contentValues.put("ReordId", myuser.ReordId);
        contentValues.put("UserName", myuser.UserName);
        contentValues.put("OriginalText", myuser.OriginalText);
        contentValues.put("VoiceText", myuser.VoiceText);
    }

}

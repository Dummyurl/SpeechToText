package info.pratham.speechtotext;

import android.content.Context;
import android.database.Cursor;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ketan on 24-Oct-17.
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

    public List<myUser> getAllUserData() {
        try {
            Cursor cursor = database.rawQuery("select * from " + USERTABLE + "", null);
            return _PopulateListFromCursor(cursor);
        } catch (Exception ex) {
            return null;
        }
    }

    public List<myUser> getAllSttData() {
        try {
            Cursor cursor = database.rawQuery("select * from " + TEXTTABLE+ "", null);
            return _PopulateListFromCursor2(cursor);
        } catch (Exception ex) {
            return null;
        }
    }

    private List<myUser> _PopulateListFromCursor2(Cursor cursor) {
        try {
            List<myUser> scoreList = new ArrayList<myUser>();
            myUser myUser;
            cursor.moveToFirst();

            while (cursor.isAfterLast() == false) {

                myUser = new myUser();

                myUser.setReordId(cursor.getString(cursor.getColumnIndex("ReordId")));
                myUser.setUserID(cursor.getString(cursor.getColumnIndex("UserID")));
                myUser.setOriginalText(cursor.getString(cursor.getColumnIndex("OriginalText")));
                myUser.setVoiceText(cursor.getString(cursor.getColumnIndex("VoiceText")));
                scoreList.add(myUser);
                cursor.moveToNext();
            }
            cursor.close();
            database.close();
            return scoreList;
        } catch (Exception ex) {
            return null;
        }
    }

    private List<myUser> _PopulateListFromCursor(Cursor cursor) {
        try {
            List<myUser> scoreList = new ArrayList<myUser>();
            myUser myUser;
            cursor.moveToFirst();

            while (cursor.isAfterLast() == false) {
                myUser = new myUser();
                myUser.setId(cursor.getString(cursor.getColumnIndex("UserId")));
                myUser.setName(cursor.getString(cursor.getColumnIndex("Name")));
                myUser.setAge(cursor.getString(cursor.getColumnIndex("Age")));
                myUser.setLocation(cursor.getString(cursor.getColumnIndex("Location")));
                myUser.setEducation(cursor.getString(cursor.getColumnIndex("Education")));
                myUser.setPhone(cursor.getString(cursor.getColumnIndex("Phone")));

                scoreList.add(myUser);
                cursor.moveToNext();
            }
            cursor.close();
            database.close();
            return scoreList;
        } catch (Exception ex) {
            return null;
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
        contentValues.put("UserId", myuser.UserID);
        contentValues.put("OriginalText", myuser.OriginalText);
        contentValues.put("VoiceText", myuser.VoiceText);
    }

}

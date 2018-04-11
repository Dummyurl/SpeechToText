package info.pratham.speechtotext;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ketan on 24-Oct-17.
 */

public class MyDBHelper extends DBHelper {

    Context context;
    String USERTABLE = "SttUser";
    String TEXTTABLE = "SttText";
    public SQLiteDatabase db;
    public ContentValues contentValues;

    public MyDBHelper(Context context) {
        super(context);
        this.context = context;
    }


    public boolean AddUser(MyUser myuser) {
        try {
            db = this.getWritableDatabase();
            PopulateContentValues(myuser);
            long resultCount = db.insert(USERTABLE, null, contentValues);
            db.close();
            if (resultCount == -1)
                return false;
            else
                return true;
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
    }

    public List<MyUser> getAllUserData() {
        try {
            db = this.getReadableDatabase();
            Cursor cursor = db.rawQuery("select * from " + USERTABLE+";", null);
            return _PopulateListFromCursor(cursor);
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public List<SttData> getAllSttData() {
        try {
            db = this.getWritableDatabase();
            Cursor cursor = db.rawQuery("select * from " + TEXTTABLE, null);
            return _PopulateListFromCursor2(cursor);
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    private List<SttData> _PopulateListFromCursor2(Cursor cursor) {
        try {
            List<SttData> sttList = new ArrayList<>();
            SttData sttData;
            cursor.moveToFirst();

            while (!cursor.isAfterLast()) {

                sttData = new SttData();

                sttData.setReordId(cursor.getString(cursor.getColumnIndex("ReordId")));
                sttData.setUserID(cursor.getString(cursor.getColumnIndex("UserId")));
                sttData.setOriginalText(cursor.getString(cursor.getColumnIndex("OriginalText")));
                sttData.setVoiceText(cursor.getString(cursor.getColumnIndex("VoiceText")));
                sttData.setDateTime(cursor.getString(cursor.getColumnIndex("DateTime")));
                sttList.add(sttData);
                cursor.moveToNext();
            }
            cursor.close();
            db.close();
            return sttList;
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    private List<MyUser> _PopulateListFromCursor(Cursor cursor) {
        try {
            List<MyUser> userList = new ArrayList<>();
            MyUser myUser;
            cursor.moveToFirst();

            while (cursor.isAfterLast() == false) {
                myUser = new MyUser();
                myUser.setId(cursor.getString(cursor.getColumnIndex("UserId")));
                myUser.setName(cursor.getString(cursor.getColumnIndex("UserName")));
                myUser.setAge(cursor.getString(cursor.getColumnIndex("Age")));
                myUser.setLocation(cursor.getString(cursor.getColumnIndex("Place")));
                myUser.setEducation(cursor.getString(cursor.getColumnIndex("HighestEducation")));
                myUser.setPhone(cursor.getString(cursor.getColumnIndex("PhoneNo")));
                myUser.setDate(cursor.getString(cursor.getColumnIndex("DateTime")));

                userList.add(myUser);
                cursor.moveToNext();
            }
            cursor.close();
            db.close();
            return userList;
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public boolean deleteAllEntries() {
        try {
            db = this.getWritableDatabase();
            db.execSQL("delete from "+ USERTABLE);
            db.execSQL("delete from "+ TEXTTABLE);
            return true;
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
    }

    private void PopulateContentValues(MyUser myuser) {
        contentValues = new ContentValues();
        contentValues.put("UserId", myuser.Id.toString());
        contentValues.put("UserName", myuser.Name);
        contentValues.put("Place", myuser.Location);
        contentValues.put("Age", myuser.Age);
        contentValues.put("HighestEducation", myuser.Education);
        contentValues.put("PhoneNo", myuser.Phone);
        contentValues.put("DateTime", myuser.Date);
    }


    public boolean AddSttText(SttData sttData) {
        try {
            db = this.getWritableDatabase();
            AddTextContentValues(sttData);
            long resultCount = db.insert(TEXTTABLE, null, contentValues);
            db.close();
            if (resultCount == -1)
                return false;
            else
                return true;
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
    }

    private void AddTextContentValues(SttData sttData) {
        contentValues = new ContentValues();

        contentValues.put("ReordId", sttData.ReordId);
        contentValues.put("UserId", sttData.UserID);
        contentValues.put("OriginalText", sttData.OriginalText);
        contentValues.put("VoiceText", sttData.VoiceText);
        contentValues.put("DateTime", sttData.DateTime);
    }

}

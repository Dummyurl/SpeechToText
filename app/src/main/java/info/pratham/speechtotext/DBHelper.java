package info.pratham.speechtotext;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "PrathamSTTDB.db";
    public SQLiteDatabase database;
    public Context c;

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, 20);
        try {
            c = context;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        try {
            db.execSQL("CREATE TABLE SttUser(UserId TEXT NOT NULL, UserName text, Place text, Age text, HighestEducation text, PhoneNo text, DateTime text);");
            db.execSQL("CREATE TABLE SttText(ReordId TEXT NOT NULL, UserId text, OriginalText text, VoiceText text, DateTime text );");
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        try {

            db.execSQL("Drop table if exists SttUser");
            db.execSQL("Drop table if exists SttText");

            db.execSQL("CREATE TABLE SttUser(UserId TEXT NOT NULL, UserName text, Place text, Age text, HighestEducation text, PhoneNo text, DateTime text);");
            db.execSQL("CREATE TABLE SttText(ReordId TEXT NOT NULL, UserId text, OriginalText text, VoiceText text, DateTime text );");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}

package com.nextlynxtech.gdspushnotification.classes;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.google.gson.Gson;

import org.json.JSONArray;

import java.util.ArrayList;

public class SQLFunctions {
    public static final String TAG = "GDSPush[SQLi]";
    public static final String GLOBAL_ROWID = "_id";

    private static final String DATABASE_NAME = "gdsmessages";
    private static final String TABLE_MESSAGES = "messages";
    private static final String TABLE_MESSAGES_READ = "messagesRead";
    private static final String TABLE_MESSAGES_COLOR = "messageColor";
    private static final String TABLE_MESSAGES_EVENT_DATE = "messageEventDate";
    private static final String TABLE_MESSAGES_EVENT_ID = "messageEventID";
    private static final String TABLE_MESSAGES_EVENT_NAME = "messageEventName";
    private static final String TABLE_MESSAGES_EVENT_STATUS = "messageEventStatus";
    private static final String TABLE_MESSAGES_MESSAGE = "messageContent";
    private static final String TABLE_MESSAGES_MESSAGE_DATE = "messageDate";
    private static final String TABLE_MESSAGES_MESSAGE_HEADER = "messageHeader";
    private static final String TABLE_MESSAGES_MESSAGE_ID = "messageId";
    private static final String TABLE_MESSAGES_RECALL_FLAG = "messageRecallFlag";
    private static final String TABLE_MESSAGES_MINE = "messageMine";
    private static final String TABLE_MESSAGES_DEFINED_REPLIES = "messageReplies";
    private static final int DATABASE_VERSION = 1;

    private DbHelper ourHelper;
    private final Context ourContext;
    private SQLiteDatabase ourDatabase;

    private static class DbHelper extends SQLiteOpenHelper {

        public DbHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL("CREATE TABLE " + TABLE_MESSAGES + " (" + GLOBAL_ROWID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + TABLE_MESSAGES_COLOR + " TEXT NOT NULL, "
                    + TABLE_MESSAGES_EVENT_DATE + " TEXT NOT NULL, " + TABLE_MESSAGES_EVENT_ID + " TEXT NOT NULL, " + TABLE_MESSAGES_EVENT_NAME + " TEXT NOT NULL, "
                    + TABLE_MESSAGES_EVENT_STATUS + " TEXT NOT NULL, " + TABLE_MESSAGES_MESSAGE + " TEXT NOT NULL, " + TABLE_MESSAGES_MESSAGE_DATE + " TEXT NOT NULL, "
                    + TABLE_MESSAGES_MESSAGE_HEADER + " TEXT NOT NULL, " + TABLE_MESSAGES_MESSAGE_ID + " TEXT NOT NULL, " + TABLE_MESSAGES_RECALL_FLAG + " TEXT NOT NULL, " + TABLE_MESSAGES_READ + " TEXT NOT NULL, " + TABLE_MESSAGES_MINE + " TEXT NOT NULL, " + TABLE_MESSAGES_DEFINED_REPLIES + " TEXT NOT NULL);");
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_MESSAGES);
            onCreate(db);
        }

    }

    public SQLFunctions(Context c) {
        ourContext = c;
    }

    public SQLFunctions open() throws SQLException {
        ourHelper = new DbHelper(ourContext);
        ourDatabase = ourHelper.getWritableDatabase();
        return null;
    }

    public void close() {
        if (ourHelper != null) {
            ourHelper.close();
        } else {
            Log.e(TAG, "You did not open your database. Null error");
        }
    }

    public long unixTime() {
        return System.currentTimeMillis() / 1000L;
    }

    public boolean deleteEvent(String eventId) {
        return ourDatabase.delete(TABLE_MESSAGES, TABLE_MESSAGES_EVENT_ID + "=" + eventId, null) > 0;
    }

    public boolean longerThanTwoHours(String pTime) {
        int prevTime = Integer.parseInt(pTime);
        int currentTime = (int) (System.currentTimeMillis() / 1000L);
        int seconds = currentTime - prevTime;
        int how_many;
        if (seconds > 3600 && seconds < 86400) {
            how_many = (int) seconds / 3600;
            if (how_many >= 2) { // 2 hours
                return true;
            } else {
                return false;
            }
        }
        return false;
    }

    public String getLastRowId() {
        String sql = "SELECT * FROM " + TABLE_MESSAGES + " ORDER BY " + GLOBAL_ROWID + " DESC LIMIT 1";
        Cursor cursor = ourDatabase.rawQuery(sql, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                String id = cursor.getString(cursor.getColumnIndex(GLOBAL_ROWID));
                cursor.close();
                Log.e("LATEST SQL ROW", id);
                return id;
            }
        }
        cursor.close();
        return "";
    }

    public boolean setMessageAckDone(String messageId) {
        String strFilter = TABLE_MESSAGES_MESSAGE_ID + "='" + messageId + "'";
        ContentValues args = new ContentValues();
        args.put(TABLE_MESSAGES_MESSAGE_ID, "1");
        if (ourDatabase.update(TABLE_MESSAGES, args, strFilter, null) > 0) {
            return true;
        } else {
            return false;
        }
    }

    public void deleteAllMessages() {
        ourDatabase.delete(TABLE_MESSAGES, null, null);
    }

    public void markAllAsRead() { //only for those no need to reply
        Cursor cursor = ourDatabase.rawQuery("SELECT * FROM " + TABLE_MESSAGES + " WHERE " + TABLE_MESSAGES_READ + " = '0' AND " + TABLE_MESSAGES_RECALL_FLAG + " = '0'", null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                while (cursor.isAfterLast() == false) {
                    try {
                        String id = cursor.getString(cursor.getColumnIndex(TABLE_MESSAGES_MESSAGE_ID));
                        setMessageRead(id);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    cursor.moveToNext();
                }
            }
        }
        cursor.close();
    }

    public boolean setMessageRead(String messageId) {
        String strFilter = TABLE_MESSAGES_MESSAGE_ID + "='" + messageId + "'";
        ContentValues args = new ContentValues();
        args.put(TABLE_MESSAGES_READ, "1");
        if (ourDatabase.update(TABLE_MESSAGES, args, strFilter, null) > 0) {
            return true;
        } else {
            return false;
        }
    }

    public ArrayList<Message> loadMessages(String eventId) {
        ArrayList<Message> map = new ArrayList<Message>();
        Cursor cursor = ourDatabase.rawQuery("SELECT * FROM " + TABLE_MESSAGES + " WHERE " + TABLE_MESSAGES_EVENT_ID + "= '" + eventId + "' ORDER BY " + GLOBAL_ROWID + " DESC", null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                while (cursor.isAfterLast() == false) {
                    try {
                        Message m = new Message();
                        m.setColor(cursor.getInt(cursor.getColumnIndex(TABLE_MESSAGES_COLOR)));
                        m.setEventDate(cursor.getString(cursor.getColumnIndex(TABLE_MESSAGES_EVENT_DATE)));
                        m.setEventName(cursor.getString(cursor.getColumnIndex(TABLE_MESSAGES_EVENT_NAME)));
                        m.setEventStatus(cursor.getInt(cursor.getColumnIndex(TABLE_MESSAGES_EVENT_STATUS)));
                        m.setEventId(cursor.getInt(cursor.getColumnIndex(TABLE_MESSAGES_EVENT_ID)));
                        m.setMessage(cursor.getString(cursor.getColumnIndex(TABLE_MESSAGES_MESSAGE)));
                        m.setMessageDate(cursor.getString(cursor.getColumnIndex(TABLE_MESSAGES_MESSAGE_DATE)));
                        m.setMessageHeader(cursor.getString(cursor.getColumnIndex(TABLE_MESSAGES_MESSAGE_HEADER)));
                        m.setMessageId(cursor.getInt(cursor.getColumnIndex(TABLE_MESSAGES_MESSAGE_ID)));
                        m.setRecallFlag(cursor.getInt(cursor.getColumnIndex(TABLE_MESSAGES_RECALL_FLAG)));
                        m.setRead(cursor.getInt(cursor.getColumnIndex(TABLE_MESSAGES_READ)));
                        m.setMine(cursor.getInt(cursor.getColumnIndex(TABLE_MESSAGES_MINE)));
                        String r = cursor.getString(cursor.getColumnIndex(TABLE_MESSAGES_DEFINED_REPLIES));
                        if (!r.equals("")) {
                            ArrayList<String> replies = new ArrayList<>();
                            JSONArray a = new JSONArray(r);
                            for (int i = 0; i < a.length(); i++) {
                                replies.add(a.get(i).toString());
                            }
                            m.setReplies(replies);
                        } else {
                            m.setReplies(null);
                        }
                        map.add(m);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    cursor.moveToNext();
                }
            }
        }
        cursor.close();
        return map;
    }

    public ArrayList<Message> loadEventMessages() {
        ArrayList<Message> map = new ArrayList<Message>();
        Cursor cursor = ourDatabase.rawQuery("SELECT * FROM " + TABLE_MESSAGES + " GROUP BY " + TABLE_MESSAGES_EVENT_ID + " ORDER BY " + GLOBAL_ROWID + " DESC", null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                while (cursor.isAfterLast() == false) {
                    try {
                        Message m = new Message();
                        m.setColor(cursor.getInt(cursor.getColumnIndex(TABLE_MESSAGES_COLOR)));
                        m.setEventDate(cursor.getString(cursor.getColumnIndex(TABLE_MESSAGES_EVENT_DATE)));
                        m.setEventName(cursor.getString(cursor.getColumnIndex(TABLE_MESSAGES_EVENT_NAME)));
                        m.setEventStatus(cursor.getInt(cursor.getColumnIndex(TABLE_MESSAGES_EVENT_STATUS)));
                        m.setEventId(cursor.getInt(cursor.getColumnIndex(TABLE_MESSAGES_EVENT_ID)));
                        m.setMessage(cursor.getString(cursor.getColumnIndex(TABLE_MESSAGES_MESSAGE)));
                        m.setMessageDate(cursor.getString(cursor.getColumnIndex(TABLE_MESSAGES_MESSAGE_DATE)));
                        m.setMessageHeader(cursor.getString(cursor.getColumnIndex(TABLE_MESSAGES_MESSAGE_HEADER)));
                        m.setMessageId(cursor.getInt(cursor.getColumnIndex(TABLE_MESSAGES_MESSAGE_ID)));
                        m.setRecallFlag(cursor.getInt(cursor.getColumnIndex(TABLE_MESSAGES_RECALL_FLAG)));
                        m.setRead(cursor.getInt(cursor.getColumnIndex(TABLE_MESSAGES_READ)));
                        m.setMine(cursor.getInt(cursor.getColumnIndex(TABLE_MESSAGES_MINE)));
                        map.add(m);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    cursor.moveToNext();
                }
            }
        }
        cursor.close();
        return map;
    }

    public int getUnreadMessage(String eventId) {
        int count = 0;
        try {
            Cursor mCount = ourDatabase.rawQuery("SELECT COUNT(*) FROM " + TABLE_MESSAGES + " WHERE " + TABLE_MESSAGES_EVENT_ID + " = '" + eventId + "' AND " + TABLE_MESSAGES_READ + " = '0'",
                    null);
            mCount.moveToFirst();
            count = mCount.getInt(0);
            mCount.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return count;
    }


    public void insertMessage(Message m, int mine) {
        ContentValues cv = new ContentValues();
        String sql = "SELECT * FROM " + TABLE_MESSAGES + " WHERE " + TABLE_MESSAGES_MESSAGE_ID + " = ?";
        Cursor cursor = ourDatabase.rawQuery(sql, new String[]{String.valueOf(m.getMessageId())});
        if (cursor.moveToFirst()) {
            Log.e(TAG, "Message ID exist");
        } else {
            Log.e(TAG, "New Message");
            cv.put(TABLE_MESSAGES_COLOR, m.getColor());
            cv.put(TABLE_MESSAGES_EVENT_DATE, m.getEventDate());
            cv.put(TABLE_MESSAGES_EVENT_ID, m.getEventId());
            cv.put(TABLE_MESSAGES_EVENT_NAME, m.getEventName());
            cv.put(TABLE_MESSAGES_EVENT_STATUS, m.getEventStatus());
            cv.put(TABLE_MESSAGES_MESSAGE, m.getMessage());
            cv.put(TABLE_MESSAGES_MESSAGE_DATE, m.getMessageDate());
            cv.put(TABLE_MESSAGES_MESSAGE_HEADER, m.getMessageHeader());
            cv.put(TABLE_MESSAGES_MESSAGE_ID, m.getMessageId());
            cv.put(TABLE_MESSAGES_RECALL_FLAG, m.getRecallFlag());
            cv.put(TABLE_MESSAGES_MINE, mine);
            cv.put(TABLE_MESSAGES_READ, "0");
            if (m.getReplies() != null && m.getReplies().size() > 0) {
                Gson gson = new Gson();
                Log.e("Replies", gson.toJson(m.getReplies()));
                cv.put(TABLE_MESSAGES_DEFINED_REPLIES, gson.toJson(m.getReplies()));
            } else {
                cv.put(TABLE_MESSAGES_DEFINED_REPLIES, "");
            }

            try {
                ourDatabase.insert(TABLE_MESSAGES, null, cv);
            } catch (Exception e) {
                Log.e(TAG, "Error creating message entry", e);
            }
        }
        cursor.close();
    }
}
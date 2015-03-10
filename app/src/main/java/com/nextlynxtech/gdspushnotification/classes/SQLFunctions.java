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
import java.util.HashMap;

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

    private static final String TABLE_REPLIES = "replies";
    private static final String TABLE_REPLIES_MESSAGE = "repliesMessage";
    private static final String TABLE_REPLIES_TIME = "repliesTime";
    private static final String TABLE_REPLIES_TO_MESSAGE_ID = "repliesMessageId";
    private static final String TABLE_REPLIES_SUCCESS = "repliesSuccess";


    private static final String TABLE_TIMELINE = "timeline";
    private static final String TABLE_TIMELINE_LOCATION = "timelineLocation";
    private static final String TABLE_TIMELINE_LOCATION_LAT = "timelineLocationLat";
    private static final String TABLE_TIMELINE_LOCATION_LONG = "timelineLocationLong";
    private static final String TABLE_TIMELINE_UNIX = "timelineTime";
    private static final String TABLE_TIMELINE_IMAGE = "timelineImage";
    private static final String TABLE_TIMELINE_VIDEO = "timelineVideo";
    private static final String TABLE_TIMELINE_MESSAGE = "timelineMessage";
    private static final String TABLE_TIMELINE_SUCCESS = "timelineSuccess";

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
            db.execSQL("CREATE TABLE " + TABLE_REPLIES + " (" + GLOBAL_ROWID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + TABLE_REPLIES_TO_MESSAGE_ID + " TEXT NOT NULL, "
                    + TABLE_REPLIES_MESSAGE + " TEXT NOT NULL, " + TABLE_REPLIES_TIME + " TEXT NOT NULL, " + TABLE_REPLIES_SUCCESS + " TEXT NOT NULL);");
            db.execSQL("CREATE TABLE " + TABLE_TIMELINE + " (" + GLOBAL_ROWID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + TABLE_TIMELINE_UNIX + " TEXT NOT NULL, "
                    + TABLE_TIMELINE_MESSAGE + " TEXT NOT NULL, " + TABLE_TIMELINE_IMAGE + " TEXT NOT NULL, " + TABLE_TIMELINE_VIDEO + " TEXT NOT NULL, "
                    + TABLE_TIMELINE_LOCATION + " TEXT NOT NULL, " + TABLE_TIMELINE_LOCATION_LAT + " TEXT NOT NULL, " + TABLE_TIMELINE_LOCATION_LONG + " TEXT NOT NULL, " + TABLE_TIMELINE_SUCCESS + " TEXT NOT NULL);");
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

    public boolean setUploadStatus(String id, String status) {
        String strFilter = GLOBAL_ROWID + "='" + id + "'";
        ContentValues args = new ContentValues();
        args.put(TABLE_TIMELINE_SUCCESS, status);
        if (ourDatabase.update(TABLE_TIMELINE, args, strFilter, null) > 0) {
            return true;
        } else {
            return false;
        }
    }

    public void deleteAllTimelineItem() {
        ourDatabase.delete(TABLE_TIMELINE, null, null);
    }

    public boolean deleteTimelineItem(String id) {
        return ourDatabase.delete(TABLE_TIMELINE, GLOBAL_ROWID + "=" + id, null) > 0;
    }

    /*public boolean setMessageRead(String messageId) {
        String strFilter = Consts.MESSAGES_MESSAGE_ID + "='" + messageId + "'";
        ContentValues args = new ContentValues();
        args.put(TABLE_MESSAGES_READ, "1");
        if (ourDatabase.update(TABLE_MESSAGES, args, strFilter, null) > 0) {
            return true;
        } else {
            return false;
        }
    }*/

    public ArrayList<Timeline> loadTimelineItems() {
        ArrayList<Timeline> map = new ArrayList<Timeline>();
        Cursor cursor = ourDatabase.rawQuery("SELECT * FROM " + TABLE_TIMELINE + " ORDER BY " + GLOBAL_ROWID + " DESC", null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                while (cursor.isAfterLast() == false) {
                    try {
                        Timeline t = new Timeline();
                        t.setId(cursor.getString(cursor.getColumnIndex(GLOBAL_ROWID)));
                        t.setUnixTime(cursor.getString(cursor.getColumnIndex(TABLE_TIMELINE_UNIX)));
                        t.setMessage(cursor.getString(cursor.getColumnIndex(TABLE_TIMELINE_MESSAGE)));
                        t.setImage(cursor.getString(cursor.getColumnIndex(TABLE_TIMELINE_IMAGE)));
                        t.setVideo(cursor.getString(cursor.getColumnIndex(TABLE_TIMELINE_VIDEO)));
                        t.setLocation(cursor.getString(cursor.getColumnIndex(TABLE_TIMELINE_LOCATION)));
                        t.setLocationLat(cursor.getString(cursor.getColumnIndex(TABLE_TIMELINE_LOCATION_LAT)));
                        t.setLocationLong(cursor.getString(cursor.getColumnIndex(TABLE_TIMELINE_LOCATION_LONG)));
                        t.setSuccess(cursor.getString(cursor.getColumnIndex(TABLE_TIMELINE_SUCCESS)));
                        map.add(t);
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

    public long insertTimelineItem(Timeline t) {
        ContentValues cv = new ContentValues();
        String sql = "SELECT * FROM " + TABLE_TIMELINE;
        Cursor cursor = ourDatabase.rawQuery(sql, null);

        Log.e(TAG, "New Timeline Item");
        cv.put(TABLE_TIMELINE_UNIX, t.getUnixTime());
        cv.put(TABLE_TIMELINE_MESSAGE, t.getMessage());
        cv.put(TABLE_TIMELINE_IMAGE, t.getImage());
        cv.put(TABLE_TIMELINE_VIDEO, t.getVideo());
        cv.put(TABLE_TIMELINE_LOCATION, t.getLocation());
        cv.put(TABLE_TIMELINE_LOCATION_LAT, t.getLocationLat());
        cv.put(TABLE_TIMELINE_LOCATION_LONG, t.getLocationLong());
        cv.put(TABLE_TIMELINE_SUCCESS, t.getSuccess());
        try {
            return ourDatabase.insert(TABLE_TIMELINE, null, cv);
        } catch (Exception e) {
            Log.e(TAG, "Error creating timeline item entry", e);
        }
        cursor.close();
        return 0;
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

    public boolean repliesExist(String messageId) {
        Cursor cursor = ourDatabase.rawQuery("SELECT * FROM " + TABLE_REPLIES + " WHERE " + TABLE_REPLIES_TO_MESSAGE_ID + "= '" + messageId + "' ORDER BY " + GLOBAL_ROWID + " DESC", null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                cursor.close();
                return true;
            }
        }
        cursor.close();
        return false;
    }

    public ArrayList<Message> loadReplies(String messageId) {
        ArrayList<Message> map = new ArrayList<>();
        String header = getMessageHeader(messageId);
        Cursor cursor = ourDatabase.rawQuery("SELECT * FROM " + TABLE_REPLIES + " WHERE " + TABLE_REPLIES_TO_MESSAGE_ID + "= '" + messageId + "' ORDER BY " + GLOBAL_ROWID + " ASC", null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                while (cursor.isAfterLast() == false) {
                    try {
                        Message m = new Message();
                        m.setMessage(cursor.getString(cursor.getColumnIndex(TABLE_REPLIES_MESSAGE)));
                        m.setMessageDate(cursor.getString(cursor.getColumnIndex(TABLE_REPLIES_TIME)));
                        m.setMessageHeader(header);
                        m.setReplyId(cursor.getString(cursor.getColumnIndex(GLOBAL_ROWID)));
                        m.setMine(1);
                        m.setReplyToMessageId(messageId);
                        m.setReplySuccess(cursor.getInt(cursor.getColumnIndex(TABLE_REPLIES_SUCCESS)));
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

    public String getMessageHeader(String messageId) {
        String data = "";
        Cursor cursor = ourDatabase.rawQuery("SELECT * FROM " + TABLE_MESSAGES + " WHERE " + TABLE_MESSAGES_MESSAGE_ID + "= '" + messageId + "'", null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                data = cursor.getString(cursor.getColumnIndex(TABLE_MESSAGES_MESSAGE_HEADER));
            }
        }
        cursor.close();
        return data;
    }

    public ArrayList<Message> loadMessages(String eventId) {
        ArrayList<Message> map = new ArrayList<>();
        Cursor cursor = ourDatabase.rawQuery("SELECT * FROM " + TABLE_MESSAGES + " WHERE " + TABLE_MESSAGES_EVENT_ID + "= '" + eventId + "' ORDER BY " + GLOBAL_ROWID + " ASC", null);
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
                        int messageId = cursor.getInt(cursor.getColumnIndex(TABLE_MESSAGES_MESSAGE_ID));
                        m.setMessageId(messageId);
                        int recallFlag = cursor.getInt(cursor.getColumnIndex(TABLE_MESSAGES_RECALL_FLAG));
                        m.setRecallFlag(recallFlag);

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
                        if (recallFlag == 1) {
                            Log.e("SQL", "Needs reply. Recall flag 1");
                            if (repliesExist(String.valueOf(messageId))) {
                                Log.e("SQL", "User has replies for this message.");
                                map.addAll(loadReplies(String.valueOf(messageId)));
                            }
                        }
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

    public ArrayList<Message> loadEventMessagesUnused() {
        ArrayList<Message> map = new ArrayList<>();
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

    public ArrayList<HashMap<String, String>> loadEventMessages() {
        ArrayList<HashMap<String, String>> map = new ArrayList<>();
        Cursor cursor = ourDatabase.rawQuery("SELECT * FROM " + TABLE_MESSAGES + " GROUP BY " + TABLE_MESSAGES_EVENT_ID + " ORDER BY " + GLOBAL_ROWID + " DESC", null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                while (cursor.isAfterLast() == false) {
                    try {
                        HashMap<String, String> h = new HashMap<>();
                        h.put("title", cursor.getString(cursor.getColumnIndex(TABLE_MESSAGES_EVENT_NAME)));
                        h.put("message", cursor.getString(cursor.getColumnIndex(TABLE_MESSAGES_MESSAGE)));
                        h.put("count", String.valueOf(getUnreadMessage(cursor.getString(cursor.getColumnIndex(TABLE_MESSAGES_EVENT_ID))))); //unreadcount
                        h.put("eventId", cursor.getString(cursor.getColumnIndex(TABLE_MESSAGES_EVENT_ID)));
                        h.put("read", cursor.getString(cursor.getColumnIndex(TABLE_MESSAGES_READ)));
                        map.add(h);
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

    public int insertReply(String message, String messageId, String success) {
        long id = 0;
        ContentValues cv = new ContentValues();
        Log.e(TAG, "New reply");
        cv.put(TABLE_REPLIES_MESSAGE, message);
        cv.put(TABLE_REPLIES_TIME, String.valueOf(System.currentTimeMillis() / 1000));
        cv.put(TABLE_REPLIES_TO_MESSAGE_ID, messageId);
        cv.put(TABLE_REPLIES_SUCCESS, success);
        try {
            id = ourDatabase.insert(TABLE_REPLIES, null, cv);
            if (id < 0) {
                id = 0;
            }
        } catch (Exception e) {
            Log.e(TAG, "Error creating message entry", e);
        }
        return (int) id;
    }

    public void updateReply(Message m, String success) {
        ContentValues args = new ContentValues();
        args.put(TABLE_REPLIES_SUCCESS, success);
        ourDatabase.update(TABLE_REPLIES, args, GLOBAL_ROWID + "='" + m.getReplyId() + "' AND " + TABLE_REPLIES_TO_MESSAGE_ID + " = '" + m.getReplyToMessageId() + "'", null);
    }

    public void updateReply(int id, String success) {
        ContentValues args = new ContentValues();
        args.put(TABLE_REPLIES_SUCCESS, success);
        ourDatabase.update(TABLE_REPLIES, args, GLOBAL_ROWID + "='" + id + "'", null);
    }

}
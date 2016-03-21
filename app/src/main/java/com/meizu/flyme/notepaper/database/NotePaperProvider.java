package com.meizu.flyme.notepaper.database;

import android.content.ContentProvider;
import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.OperationApplicationException;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.support.v7.appcompat.BuildConfig;
import android.text.TextUtils;
import android.util.Log;

import com.meizu.Constants;
import com.meizu.flyme.notepaper.database.NotePaper.AccountConstract;
import com.meizu.flyme.notepaper.database.NotePaper.NoteCategory;
import com.meizu.flyme.notepaper.database.NotePaper.NoteFiles;
import com.meizu.flyme.notepaper.database.NotePaper.Notes;
import com.meizu.flyme.notepaper.notepaper.R;
import com.meizu.flyme.notepaper.utils.NoteUtil;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.UUID;

//  import com.meizu.flyme.notepaper.NoteApplication;
//  import com.meizu.flyme.notepaper.TagData;
//  import com.meizu.flyme.notepaper.upgrade.NoteUpgradeUtils;
//  import com.meizu.pim.HanziToPinyin.Token;
//  import com.meizu.update.Constants;

public class NotePaperProvider extends ContentProvider {
    private static final int ACCOUNTS = 15;
    private static final int ACCOUNT_ID = 16;
    public static final String ACCOUNT_TABLE_NAME = "accounts";
    public static final String DATABASE_NAME = "note_paper.db";
    public static final int DATABASE_VERSION = 12;
    private static final int FILES = 11;
    public static final String FILES_TABLE_NAME = "notefiles";
    private static final int FILE_COLUMN_HASH = 3;
    private static final int FILE_COLUMN_ID = 0;
    private static final int FILE_COLUMN_LAST_HASH = 4;
    private static final int FILE_COLUMN_NOTE_ID = 1;
    private static final int FILE_COLUMN_TYPE = 2;
    private static final int FILE_ID = 12;
    private static final int LIVE_FOLDER_NOTES = 3;
    private static final int NOTES = 1;
    public static final String NOTES_TABLE_NAME = "notes";
    private static final int NOTE_ID = 2;
    private static final int OLD_NOTES = 21;
    private static final int SYNCSTATE = 4;
    private static final int SYNCSTATE_ID = 5;
    private static final String TAG = "NotePaperProvider";
    private static final int TAGS = 13;
    private static final int TAG_ID = 14;
    public static final String TAG_TABLE_NAME = "tags";
    private static HashMap<String, String> sAccountProjectionMap = new HashMap();
    private static HashMap<String, String> sFilesProjectionMap = new HashMap();
    private static HashMap<String, String> sLiveFolderProjectionMap;
    private static HashMap<String, String> sNotesProjectionMap = new HashMap();
    private static HashMap<String, String> sTagProjectionMap = new HashMap();
    private static final UriMatcher sUriMatcher = new UriMatcher(-1);
    private DatabaseHelper mOpenHelper;

    public static class DatabaseHelper extends SQLiteOpenHelper {
        private final Context mContext;
        public DatabaseHelper(Context context) {
            super(context, NotePaperProvider.DATABASE_NAME, null, NotePaperProvider.FILE_ID);
            this.mContext = context;
        }

        private void createFilesTable(SQLiteDatabase db) {
            db.execSQL("CREATE TABLE notefiles (_id INTEGER PRIMARY KEY AUTOINCREMENT,note_uuid TEXT,name TEXT,md5 TEXT,type INTEGER,mtime INTEGER," + NoteFiles.DATA1 + " TEXT," + NoteFiles.DATA2 + " TEXT," + NoteFiles.DIRTY + " INTEGER," + NoteFiles.SYNC_DATA1 + " TEXT," + NoteFiles.SYNC_DATA2 + " TEXT," + NoteFiles.SYNC_DATA3 + " TEXT," + NoteFiles.SYNC_DATA4 + " TEXT" + ");");
        }

        private void createTagTable(SQLiteDatabase db) {
            db.execSQL("CREATE TABLE tags (_id INTEGER PRIMARY KEY AUTOINCREMENT,uuid TEXT,name TEXT,sort INTEGER DEFAULT 0," + NoteCategory.DIRTY + " INTEGER DEFAULT 0," + NoteCategory.DELETE + " INTEGER DEFAULT 0," + NoteCategory.DATA1 + " TEXT," + NoteCategory.DATA2 + " TEXT," + NoteCategory.SYNC_DATA1 + " TEXT," + NoteCategory.SYNC_DATA2 + " TEXT," + NoteCategory.SYNC_DATA3 + " TEXT," + NoteCategory.SYNC_DATA4 + " TEXT" + ");");
            db.execSQL("insert into tags (uuid,name,sort," + NoteCategory.DIRTY + ") " + "values (" + "'" + "default_group_1" + "'" + "," + "'" + this.mContext.getResources().getString(R.string.default_tag_work) + "'" + "," + NotePaperProvider.FILE_COLUMN_ID + "," + "1" + ")");
            db.execSQL("insert into tags (uuid,name,sort," + NoteCategory.DIRTY + ") " + "values (" + "'" + "default_group_2" + "'" + "," + "'" + this.mContext.getResources().getString(R.string.default_tag_life) + "'" + "," + NotePaperProvider.NOTES + "," + "1" + ")");
            db.execSQL("insert into tags (uuid,name,sort," + NoteCategory.DIRTY + ") " + "values (" + "'" + "default_group_3" + "'" + "," + "'" + this.mContext.getResources().getString(R.string.default_tag_temp) + "'" + "," + NotePaperProvider.NOTE_ID + "," + "1" + ")");
        }

        private void createAccountTable(SQLiteDatabase db) {
            db.execSQL("create table if not exists accounts (_id integer primary key autoincrement,account_name text)");
        }

        public void onCreate(SQLiteDatabase db) {
            Log.d(NotePaperProvider.TAG, "onCreate:notes");
            db.execSQL("CREATE TABLE notes (_id INTEGER PRIMARY KEY AUTOINCREMENT,uuid TEXT,note TEXT,create_time INTEGER,modified INTEGER,paper INTEGER,title TEXT,note_with_style TEXT DEFAULT NULL," + Notes.FONT_COLOR + " INTEGER," + Notes.FONT_SIZE + " INTEGER," + Notes.FIRST_IMAGE + " TEXT," + Notes.FIRST_RECORD + " TEXT," + Notes.FILE_LIST + " TEXT," + Notes.DIRTY + " INTEGER," + Notes.ENCRYPT + " INTEGER DEFAULT 0," + Notes.TOP + " INTEGER DEFAULT 0," + Notes.TAG + " INTEGER DEFAULT 0," + Notes.DESKTOP + " INTEGER DEFAULT 0," + Notes.ACCOUNT_ID + " INTEGER DEFAULT 0," + Notes.SYNC_ID + " TEXT," + Notes.RESERVED1 + " TEXT," + Notes.RESERVED2 + " TEXT," + Notes.SYNC_DATA1 + " TEXT," + Notes.SYNC_DATA2 + " TEXT," + Notes.SYNC_DATA3 + " TEXT," + Notes.SYNC_DATA4 + " TEXT" + ");");
            createFilesTable(db);
            createTagTable(db);
            createAccountTable(db);
            insertBuiltInNoteData(db, "0");
        }

        void insertBuiltSyncNote(SQLiteDatabase db) {
            String uuid = "inbuilt_note_0";
            int count = NotePaperProvider.FILE_COLUMN_ID;
            String str = NotePaperProvider.NOTES_TABLE_NAME;
            String[] strArr = new String[NotePaperProvider.NOTES];
            strArr[NotePaperProvider.FILE_COLUMN_ID] = NoteFiles.DEFAULT_SORT_ORDER;
            String[] strArr2 = new String[NotePaperProvider.NOTES];
            strArr2[NotePaperProvider.FILE_COLUMN_ID] = uuid;
            Cursor c = db.query(str, strArr, "uuid=?", strArr2, null, null, null);
            if (c != null) {
                count = c.getCount();
                c.close();
            }
            if (count == 0) {
                String note = "\u5982\u679c\u4f60\u540c\u65f6\u662fFlyme2.0\u53ca\u4e4b\u524d\u56fa\u4ef6\u7528\u6237\uff0c\u4ee5\u4e0b\u4fe1\u606f\u5bf9\u4f60\u975e\u5e38\u91cd\u8981\u3002\n\n\u5982\u4f60\u6240\u89c1\uff0cFlyme3.0\u4fbf\u7b7e\uff0c\u662f\u4e00\u6b21\u91cd\u5927\u5347\u7ea7\uff0c\u6d82\u9e26\u72ec\u7acb\u4e3a\u753b\u677f\uff0c\u5e76\u91cd\u65b0\u8bbe\u8ba1\u4e86\u51e0\u4e4e\u6240\u6709\u90e8\u5206\uff0c\u56e0\u6b64\u6570\u636e\u540c\u6b65\u662f\u4e00\u4e2a\u91cd\u8981\u6311\u6218\u3002\n\n\u9996\u6b21\u4f7f\u7528Flyme3.0\u540c\u6b65\u65f6\uff0c\u6211\u4eec\u4f1a\u5c06\u4e4b\u524d\u7684\u4fbf\u7b7e\uff0c\u5168\u90e8\u5347\u7ea7\u4e3aFlyme3.0\u683c\u5f0f\uff0c\u540c\u65f6\u6d82\u9e26\u4f1a\u8f6c\u6362\u4e3a\u6807\u51c6\u56fe\u7247\u3002\n\n\u5347\u7ea7\u5b8c\u6210\u540e\uff0cFlyme3.0\u4fbf\u7b7e\u5c06\u4e0d\u518d\u4e0e\u4e4b\u524d\u56fa\u4ef6\u540c\u6b65\u3002\u76f8\u540c\u56fa\u4ef6\u4e4b\u95f4\u7684\u540c\u6b65\uff0c\u53ca\u624b\u673a\u4e0e\u4e91\u670d\u52a1\u7684\u540c\u6b65\u4e0d\u53d7\u5f71\u54cd\u3002";
                try {
                    JSONArray ja = new JSONArray();
                    JSONObject jo = new JSONObject();
                    jo.put(NoteUtil.JSON_STATE, NotePaperProvider.FILE_COLUMN_ID);
                    jo.put(NoteUtil.JSON_TEXT, note);
                    ja.put(jo);
                    note = ja.toString();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                Long now = Long.valueOf(System.currentTimeMillis());
                db.execSQL("insert into notes (uuid,note,create_time,modified,paper,title," + Notes.DIRTY + ") " + "values (" + "'" + uuid + "'" + "," + "'" + note + "'" + "," + now + "," + now + "," + "0" + "," + "'" + "\u4fbf\u7b7e\u540c\u6b65\u8bf4\u660e" + "'" + "," + "0" + ")");
            }
        }

        void insertBuiltInNoteData(SQLiteDatabase db, String dirty) {
            String country = Locale.getDefault().getCountry();
            Log.d(NotePaperProvider.TAG, "get current country: " + country);
            if ("CN".equalsIgnoreCase(country)) {
                String note = null;
                String uuid = "inbuilt_note_1";
                String name1 = "ai_1.jpg";
                String name2 = "ai_2.jpg";
                String name3 = "ai_3.jpg";
                String name4 = "ai_4.jpg";
                String name5 = "ai_5.jpg";
                if (copydb(R.raw.ai_1, uuid, name1) && copydb(R.raw.ai_2, uuid, name2) && copydb(R.raw.ai_3, uuid, name3) && copydb(R.raw.ai_4, uuid, name4) && copydb(R.raw.ai_5, uuid, name5)) {
                    String first_img = BuildConfig.VERSION_NAME;
                    ArrayList<String> list = new ArrayList();
                    list.add(name1);
                    list.add(name2);
                    list.add(name3);
                    list.add(name4);
                    list.add(name5);
                    String fileList = NoteUtil.getFileListString(list);
                    try {
                        JSONArray ja = new JSONArray();
                        JSONObject jo = new JSONObject();
                        jo.put(NoteUtil.JSON_STATE, NotePaperProvider.LIVE_FOLDER_NOTES);
                        jo.put(NoteUtil.JSON_FILE_NAME, name1);
                        jo.put(NoteUtil.JSON_IMAGE_HEIGHT, 500);
                        jo.put(NoteUtil.JSON_IMAGE_WIDTH, 920);
                        ja.put(jo);
                        first_img = jo.toString();
                        JSONObject jo1 = new JSONObject();
                        jo1.put(NoteUtil.JSON_STATE, NotePaperProvider.FILE_COLUMN_ID);
                        jo1.put(NoteUtil.JSON_TEXT, "\u652f\u6301\u6587\u672c\u3001\u6e05\u5355\u3001\u5f55\u97f3\u4e0e\u56fe\u7247\u63d2\u5165\u529f\u80fd\uff0c\u4e00\u4efd\u8bb0\u5f55\uff0c\u591a\u79cd\u65b9\u6cd5\u3002");
                        ja.put(jo1);
                        JSONObject jo2 = new JSONObject();
                        jo2.put(NoteUtil.JSON_STATE, NotePaperProvider.LIVE_FOLDER_NOTES);
                        jo2.put(NoteUtil.JSON_FILE_NAME, name2);
                        jo2.put(NoteUtil.JSON_IMAGE_HEIGHT, 500);
                        jo2.put(NoteUtil.JSON_IMAGE_WIDTH, 920);
                        ja.put(jo2);
                        JSONObject jo21 = new JSONObject();
                        jo21.put(NoteUtil.JSON_STATE, NotePaperProvider.FILE_COLUMN_ID);
                        jo21.put(NoteUtil.JSON_TEXT, "\u6587\u5b57\u5927\u5c0f\u3001\u80cc\u666f\u989c\u8272\u968f\u5fc3\u8c03\u6574\uff0c\u7b80\u5355\u8bb0\u5f55\u4e5f\u53ef\u5c42\u6b21\u5206\u660e\uff0c\u91cd\u70b9\u7a81\u51fa\u3002");
                        ja.put(jo21);
                        JSONObject jo3 = new JSONObject();
                        jo3.put(NoteUtil.JSON_STATE, NotePaperProvider.LIVE_FOLDER_NOTES);
                        jo3.put(NoteUtil.JSON_FILE_NAME, name3);
                        jo3.put(NoteUtil.JSON_IMAGE_HEIGHT, 500);
                        jo3.put(NoteUtil.JSON_IMAGE_WIDTH, 920);
                        ja.put(jo3);
                        JSONObject jo31 = new JSONObject();
                        jo31.put(NoteUtil.JSON_STATE, NotePaperProvider.FILE_COLUMN_ID);
                        jo31.put(NoteUtil.JSON_TEXT, "\u6307\u5b9a\u4fbf\u7b7e\u7f6e\u9876\uff0c\u91cd\u8981\u7684\u4fbf\u7b7e\uff0c\u5c31\u662f\u5728\u91cd\u8981\u7684\u4f4d\u7f6e\u3002");
                        ja.put(jo31);
                        JSONObject jo4 = new JSONObject();
                        jo4.put(NoteUtil.JSON_STATE, NotePaperProvider.LIVE_FOLDER_NOTES);
                        jo4.put(NoteUtil.JSON_FILE_NAME, name4);
                        jo4.put(NoteUtil.JSON_IMAGE_HEIGHT, 500);
                        jo4.put(NoteUtil.JSON_IMAGE_WIDTH, 920);
                        ja.put(jo4);
                        JSONObject jo41 = new JSONObject();
                        jo41.put(NoteUtil.JSON_STATE, NotePaperProvider.FILE_COLUMN_ID);
                        jo41.put(NoteUtil.JSON_TEXT, "\u4fbf\u7b7e\u5206\u7ec4\uff0c\u4ece\u6b64\u4fbf\u7b7e\u601d\u8def\u4e0e\u4f60\u5927\u8111\u540c\u6b65\u3002");
                        ja.put(jo41);
                        JSONObject jo5 = new JSONObject();
                        jo5.put(NoteUtil.JSON_STATE, NotePaperProvider.LIVE_FOLDER_NOTES);
                        jo5.put(NoteUtil.JSON_FILE_NAME, name5);
                        jo5.put(NoteUtil.JSON_IMAGE_HEIGHT, 500);
                        jo5.put(NoteUtil.JSON_IMAGE_WIDTH, 920);
                        ja.put(jo5);
                        JSONObject jo51 = new JSONObject();
                        jo51.put(NoteUtil.JSON_STATE, NotePaperProvider.FILE_COLUMN_ID);
                        jo51.put(NoteUtil.JSON_TEXT, "\u4fbf\u7b7e\u52a0\u5bc6\uff0c\u9690\u79c1\u4fe1\u606f\u4e0d\u6015\u5fd8\u6389\uff0c\u4e5f\u4e0d\u62c5\u5fc3\u88ab\u522b\u4eba\u770b\u5230\u3002");
                        ja.put(jo51);
                        note = ja.toString();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    Long now = Long.valueOf(System.currentTimeMillis());
                    db.execSQL("insert into notes (uuid,note,title,create_time,modified,paper," + Notes.FIRST_IMAGE + "," + Notes.FILE_LIST + "," + Notes.DIRTY + ") " + "values (" + "'" + uuid + "'" + "," + "'" + note + "'" + "," + "'" + "\u5168\u65b0\u8bbe\u8ba1\u7684Flyme\u4fbf\u7b7e" + "'" + "," + now + "," + now + "," + "0" + "," + "'" + first_img + "'" + "," + "'" + fileList + "'" + "," + dirty + ")");
                    File imgFile1 = NoteUtil.getFile(uuid, name1);
                    File imgFile2 = NoteUtil.getFile(uuid, name2);
                    File imgFile3 = NoteUtil.getFile(uuid, name3);
                    File imgFile4 = NoteUtil.getFile(uuid, name4);
                    File imgFile5 = NoteUtil.getFile(uuid, name5);
                    Log.d(NotePaperProvider.TAG, "name5: " + name5 + " md5: " + NoteUtil.md5sum(imgFile5.getPath()));
                    db.execSQL("insert into notefiles (note_uuid,name,md5,type,mtime," + NoteFiles.DIRTY + ") " + "values (" + "'" + uuid + "'" + "," + "'" + name1 + "'" + "," + "'" + "0f981deb84ea215335173d28223df37b" + "'" + "," + NotePaperProvider.FILE_COLUMN_ID + "," + imgFile1.lastModified() + "," + dirty + ")");
                    db.execSQL("insert into notefiles (note_uuid,name,md5,type,mtime," + NoteFiles.DIRTY + ") " + "values (" + "'" + uuid + "'" + "," + "'" + name2 + "'" + "," + "'" + "60331a4dcf37e878f63dd7365576e525" + "'" + "," + NotePaperProvider.FILE_COLUMN_ID + "," + imgFile2.lastModified() + "," + dirty + ")");
                    db.execSQL("insert into notefiles (note_uuid,name,md5,type,mtime," + NoteFiles.DIRTY + ") " + "values (" + "'" + uuid + "'" + "," + "'" + name3 + "'" + "," + "'" + "cac68e8542e397ac0eace66bb3ab85c3" + "'" + "," + NotePaperProvider.FILE_COLUMN_ID + "," + imgFile3.lastModified() + "," + dirty + ")");
                    db.execSQL("insert into notefiles (note_uuid,name,md5,type,mtime," + NoteFiles.DIRTY + ") " + "values (" + "'" + uuid + "'" + "," + "'" + name4 + "'" + "," + "'" + "c03146e871ffb3d9d9276ee5ed34a1e8" + "'" + "," + NotePaperProvider.FILE_COLUMN_ID + "," + imgFile4.lastModified() + "," + dirty + ")");
                    db.execSQL("insert into notefiles (note_uuid,name,md5,type,mtime," + NoteFiles.DIRTY + ") " + "values (" + "'" + uuid + "'" + "," + "'" + name5 + "'" + "," + "'" + "055e8882040aa2ee0278a1cf95d01557" + "'" + "," + NotePaperProvider.FILE_COLUMN_ID + "," + imgFile5.lastModified() + "," + dirty + ")");
                    this.mContext.sendBroadcast(new Intent("android.intent.action.MEDIA_SCANNER_SCAN_FILE", Uri.fromFile(new File(NoteUtil.FILES_DIR, uuid))));
                }
            }
        }

        //删除了里面的代码
        private boolean copydb(int rawFile, String uuid, String name) {
            return false;
        }

        public void onOpen(SQLiteDatabase db) {
            NotePaperProvider.createTriggers(db);
        }

        //删除暂时不需要的代码
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        }
        public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        }

    }

    //创建DatabaseHelper是为了创建数据库和表
    public boolean onCreate() {
        this.mOpenHelper = new DatabaseHelper(getContext());
        return true;
    }

    //所有的查询都在这里执行
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        String orderBy;
        //SQLiteQueryBuilder 是一个构造SQL查询语句的辅助类
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        qb.setTables(NOTES_TABLE_NAME);//指定查询的表

        int match = sUriMatcher.match(uri);//匹配uri，确定查询的方式
        //判断排序方式
        if (TextUtils.isEmpty(sortOrder)) {
            orderBy = Notes.DEFAULT_SORT_ORDER;//设置排序方式
        } else {
            orderBy = sortOrder;
        }
        //确定该执行怎样的查询
        switch (match) {
            //查询所有的Notes
            case NOTES /*1*/:
                qb.setProjectionMap(sNotesProjectionMap);
/*                long account = ((NoteApplication) getContext().getApplicationContext()).getMeizuAccount();
                if (account > 0) {
                    qb.appendWhere(Notes.ENCRYPT + " IS NULL" + " OR " + Notes.ENCRYPT + "<>" + NOTES + " OR " + "(" + Notes.ENCRYPT + "=" + NOTES + " AND " + Notes.ACCOUNT_ID + "=" + account + ")");
                    break;
                }
                break;*/
                //查询指定ID的note
            case NOTE_ID /*2*/:
                qb.setProjectionMap(sNotesProjectionMap);
/*                long accountID = ((NoteApplication) getContext().getApplicationContext()).getMeizuAccount();
                if (accountID <= 0) {
                    qb.appendWhere("_id=" + ContentUris.parseId(uri));
                    break;
                }
                qb.appendWhere("_id=" + ContentUris.parseId(uri) + " AND (" + Notes.ENCRYPT + " IS NULL" + " OR " + Notes.ENCRYPT + "<>" + NOTES + " OR " + "(" + Notes.ENCRYPT + "=" + NOTES + " AND " + Notes.ACCOUNT_ID + "=" + accountID + ")" + ")");
                break;*/
            case LIVE_FOLDER_NOTES /*3*/:
                qb.setProjectionMap(sLiveFolderProjectionMap);
                break;
            case SYNCSTATE /*4*/:
            //    return this.mOpenHelper.getSyncState().query(this.mOpenHelper.getReadableDatabase(), projection, selection, selectionArgs, sortOrder);
            case SYNCSTATE_ID /*5*/:
            //    return this.mOpenHelper.getSyncState().query(this.mOpenHelper.getReadableDatabase(), projection, "_id=" + ContentUris.parseId(uri) + Token.SEPARATOR + (TextUtils.isEmpty(selection) ? BuildConfig.VERSION_NAME : " AND (" + selection + ")"), selectionArgs, sortOrder);

            //查询所有的文件
            case FILES /*11*/:
                qb.setTables(FILES_TABLE_NAME);
                qb.setProjectionMap(sFilesProjectionMap);
                orderBy = null;
                break;
            //查询指定id的文件
            case FILE_ID /*12*/:
                qb.setTables(FILES_TABLE_NAME);
                qb.setProjectionMap(sFilesProjectionMap);
                qb.appendWhere("_id=" + ContentUris.parseId(uri));
                orderBy = null;
                break;
            //查询所有的额标签
            case TAGS /*13*/:
                qb.setTables(TAG_TABLE_NAME);
                qb.setProjectionMap(sTagProjectionMap);
                orderBy = NoteCategory.DEFAULT_SORT_ORDER;
                break;
            //查询指定id的标签
            case TAG_ID /*14*/:
                qb.setTables(TAG_TABLE_NAME);
                qb.setProjectionMap(sTagProjectionMap);
                qb.appendWhere("_id=" + ContentUris.parseId(uri));
                orderBy = NoteCategory.DEFAULT_SORT_ORDER;
                break;
            //查询所有的账户
            case ACCOUNTS /*15*/:
                qb.setTables(ACCOUNT_TABLE_NAME);
                qb.setProjectionMap(sAccountProjectionMap);
                orderBy = null;
                break;
            //查询指定id的账户
            case ACCOUNT_ID /*16*/:
                qb.setTables(ACCOUNT_TABLE_NAME);
                qb.setProjectionMap(sAccountProjectionMap);
                qb.appendWhere("_id=" + ContentUris.parseId(uri));
                orderBy = null;
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
        Cursor c = qb.query(this.mOpenHelper.getReadableDatabase(), projection, selection, selectionArgs, null, null, orderBy);
        c.setNotificationUri(getContext().getContentResolver(), uri);
        return c;
    }

    //通过sUriMatcher.match(uri)匹配uri那种类型的查询
    public String getType(Uri uri) {
        switch (sUriMatcher.match(uri)) {
            case NOTES /*1*/:
            case LIVE_FOLDER_NOTES /*3*/:
                return Notes.CONTENT_TYPE;
            case NOTE_ID /*2*/:
                return Notes.CONTENT_ITEM_TYPE;
            case SYNCSTATE /*4*/:
            case SYNCSTATE_ID /*5*/:
            case ACCOUNTS /*15*/:
            case ACCOUNT_ID /*16*/:
                return null;
            case FILES /*11*/:
                return NoteFiles.CONTENT_TYPE;
            case FILE_ID /*12*/:
                return NoteFiles.CONTENT_ITEM_TYPE;
            case TAGS /*13*/:
                return NoteCategory.CONTENT_TYPE;
            case TAG_ID /*14*/:
                return NoteCategory.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
    }

    //插入，所有数据库的插入insert都在这里
    public Uri insert(Uri uri, ContentValues initialValues) {
        SQLiteDatabase db = this.mOpenHelper.getWritableDatabase();
        ContentValues values;
        long rowId;
        Uri noteUri;
        switch (sUriMatcher.match(uri)) {
            case NOTES /*1*/:
                if (initialValues != null) {
                    values = new ContentValues(initialValues);
                } else {
                    values = new ContentValues();
                }
                Long now = Long.valueOf(System.currentTimeMillis());
                if (!values.containsKey(Notes.DIRTY)) {
                    values.put(Notes.DIRTY, Boolean.valueOf(true));
                }
                if (!values.containsKey(Notes.MODIFIED_DATE)) {
                    values.put(Notes.MODIFIED_DATE, now);
                }
                if (!values.containsKey(Notes.NOTE)) {
                    values.put(Notes.NOTE, BuildConfig.VERSION_NAME);
                }
                if (!values.containsKey(Notes.PAPER)) {
                    values.put(Notes.PAPER, Integer.valueOf(FILE_COLUMN_ID));
                }
                if (!values.containsKey(Notes.UUID)) {
                    Log.d(TAG, "insert no uuid ");
                    values.put(Notes.UUID, UUID.randomUUID().toString());
                }
                if (!values.containsKey(Notes.TOP)) {
                    values.put(Notes.TOP, Integer.valueOf(FILE_COLUMN_ID));
                }
                if (!values.containsKey(Notes.DESKTOP)) {
                    values.put(Notes.DESKTOP, Integer.valueOf(FILE_COLUMN_ID));
                }
/*                if (TagData.FUN_ENCRYPT) {
                    if (!values.containsKey(Notes.ENCRYPT)) {
                        values.put(Notes.ENCRYPT, Integer.valueOf(FILE_COLUMN_ID));
                    } else if (values.getAsInteger(Notes.ENCRYPT).intValue() == NOTES) {
                        long accountID = ((NoteApplication) getContext().getApplicationContext()).getMeizuAccount();
                        if (accountID > 0) {
                            values.put(Notes.ACCOUNT_ID, Long.valueOf(accountID));
                        }
                    }
                }*/
                rowId = db.insert(NOTES_TABLE_NAME, Notes.NOTE, values);
                if (rowId >= 0) {
                    noteUri = ContentUris.withAppendedId(uri, rowId);
                    getContext().getContentResolver().notifyChange(noteUri, null);
                    notifyAppWidgetUpdated(getContext());
                    return noteUri;
                }
                throw new SQLException("Failed to insert row into " + uri);
            case SYNCSTATE /*4*/:
/*                rowId = this.mOpenHelper.getSyncState().insert(db, initialValues);
                if (rowId >= 0) {
                    return ContentUris.withAppendedId(uri, rowId);
                }*/
                return null;
            case FILES /*11*/:
                if (!initialValues.containsKey(NoteFiles.DIRTY)) {
                    initialValues.put(NoteFiles.DIRTY, Boolean.valueOf(true));
                }
                rowId = db.insert(FILES_TABLE_NAME, null, initialValues);
                if (rowId >= 0) {
                    if (initialValues.getAsInteger(Constants.JSON_KEY_TYPE).intValue() == 0) {
                        notifyAppWidgetUpdated(getContext());
                    }
                    return ContentUris.withAppendedId(uri, rowId);
                }
                throw new SQLException("Failed to insert file into " + uri);
            case TAGS /*13*/:
                if (!initialValues.containsKey(NoteFiles.DIRTY)) {
                    initialValues.put(NoteFiles.DIRTY, Boolean.valueOf(true));
                }
                rowId = db.insert(TAG_TABLE_NAME, null, initialValues);
                if (rowId >= 0) {
                    Uri tag_uri = ContentUris.withAppendedId(uri, rowId);
                    getContext().getContentResolver().notifyChange(tag_uri, null);
                    return tag_uri;
                }
                throw new SQLException("Failed to insert file into " + uri);
            case ACCOUNTS /*15*/:
                rowId = db.insert(ACCOUNT_TABLE_NAME, null, initialValues);
                if (rowId >= 0) {
                    return ContentUris.withAppendedId(uri, rowId);
                }
                throw new SQLException("Failed to insert row into " + uri);
            case OLD_NOTES /*21*/:
                if (initialValues != null) {
                    values = new ContentValues(initialValues);
                } else {
                    values = new ContentValues();
                }
/*                rowId = new NoteUpgradeUtils().add(values, db);
                if (rowId >= 0) {
                    noteUri = ContentUris.withAppendedId(uri, rowId);
                    getContext().getContentResolver().notifyChange(noteUri, null);
                    notifyAppWidgetUpdated(getContext());
                    return noteUri;
                }*/
                throw new SQLException("Failed to insert row into " + uri);
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
    }


    //所有数据库的删除delete都在这里
    public int delete(Uri uri, String where, String[] whereArgs) {
        int count;
        SQLiteDatabase db = this.mOpenHelper.getWritableDatabase();
        int type = sUriMatcher.match(uri);
        String str;
        String[] strArr;
        Cursor c;
        String uuid;
        switch (type) {
            case NOTES /*1*/:
                str = NOTES_TABLE_NAME;
                strArr = new String[NOTES];
                strArr[FILE_COLUMN_ID] = Notes.UUID;
                c = db.query(str, strArr, where, whereArgs, null, null, null);
                if (c != null) {
                    while (c.moveToNext()) {
                        uuid = c.getString(FILE_COLUMN_ID);
                        count = db.delete(FILES_TABLE_NAME, "note_uuid=\"" + uuid + "\"", null);
                        deleteSdcardFiles(uuid);
                    }
                    c.close();
                    count = db.delete(NOTES_TABLE_NAME, where, whereArgs);
                    break;
                }
                count = FILE_COLUMN_ID;
                break;
            case NOTE_ID /*2*/:
                long noteId = ContentUris.parseId(uri);
                str = NOTES_TABLE_NAME;
                strArr = new String[NOTES];
                strArr[FILE_COLUMN_ID] = Notes.UUID;
                c = db.query(str, strArr, "_id=" + noteId + (!TextUtils.isEmpty(where) ? " AND (" + where + ')' : BuildConfig.VERSION_NAME), whereArgs, null, null, null);
                if (c != null) {
                    while (c.moveToNext()) {
                        uuid = c.getString(FILE_COLUMN_ID);
                        db.delete(FILES_TABLE_NAME, "note_uuid=\"" + uuid + "\"", null);
                        deleteSdcardFiles(uuid);
                    }
                    c.close();
                    count = db.delete(NOTES_TABLE_NAME, "_id=" + noteId + (!TextUtils.isEmpty(where) ? " AND (" + where + ')' : BuildConfig.VERSION_NAME), whereArgs);
                    break;
                }
                count = FILE_COLUMN_ID;
                break;
            case SYNCSTATE /*4*/:
             //   return this.mOpenHelper.getSyncState().delete(db, where, whereArgs);
            case SYNCSTATE_ID /*5*/:
            //    return this.mOpenHelper.getSyncState().delete(db, "_id=" + ContentUris.parseId(uri) + Token.SEPARATOR + (TextUtils.isEmpty(where) ? BuildConfig.VERSION_NAME : " AND (" + where + ")"), whereArgs);
            case FILES /*11*/:
                count = db.delete(FILES_TABLE_NAME, where, whereArgs);
                break;
            case FILE_ID /*12*/:
                count = db.delete(FILES_TABLE_NAME, "_id=" + ContentUris.parseId(uri) + (!TextUtils.isEmpty(where) ? " AND (" + where + ')' : BuildConfig.VERSION_NAME), whereArgs);
                break;
            case TAGS /*13*/:
                count = db.delete(TAG_TABLE_NAME, where, whereArgs);
                break;
            case TAG_ID /*14*/:
                count = db.delete(TAG_TABLE_NAME, "_id=" + ContentUris.parseId(uri) + (!TextUtils.isEmpty(where) ? " AND (" + where + ')' : BuildConfig.VERSION_NAME), whereArgs);
                break;
            case ACCOUNTS /*15*/:
                count = db.delete(ACCOUNT_TABLE_NAME, where, whereArgs);
                break;
            case ACCOUNT_ID /*16*/:
                count = db.delete(ACCOUNT_TABLE_NAME, "_id=" + ContentUris.parseId(uri) + (!TextUtils.isEmpty(where) ? " AND (" + where + ')' : BuildConfig.VERSION_NAME), whereArgs);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        if (count > 0 && (type == NOTES || type == NOTE_ID || type == FILES || type == FILE_ID)) {
            notifyAppWidgetUpdated(getContext());
        }
        return count;
    }

    //所有数据库的update都在这里，只能更新某些数据库
    public int update(Uri uri, ContentValues values, String where, String[] whereArgs) {
        int count;
        SQLiteDatabase db = this.mOpenHelper.getWritableDatabase();
        int type = sUriMatcher.match(uri);
        long accountID;
        switch (type) {
            case NOTES /*1*/:
/*                if (TagData.FUN_ENCRYPT && values.containsKey(Notes.ENCRYPT) && values.getAsInteger(Notes.ENCRYPT).intValue() == NOTES) {
                    accountID = ((NoteApplication) getContext().getApplicationContext()).getMeizuAccount();
                    if (accountID > 0) {
                        values.put(Notes.ACCOUNT_ID, Long.valueOf(accountID));
                    }
                }*/
                count = db.update(NOTES_TABLE_NAME, values, where, whereArgs);
                break;
            case NOTE_ID /*2*/:
                if (!values.containsKey(Notes.DIRTY)) {
                    values.put(Notes.DIRTY, Boolean.valueOf(true));
                }
/*                if (TagData.FUN_ENCRYPT && values.containsKey(Notes.ENCRYPT) && values.getAsInteger(Notes.ENCRYPT).intValue() == NOTES) {
                    accountID = ((NoteApplication) getContext().getApplicationContext()).getMeizuAccount();
                    if (accountID > 0) {
                        values.put(Notes.ACCOUNT_ID, Long.valueOf(accountID));
                    }
                }*/
                count = db.update(NOTES_TABLE_NAME, values, "_id=" + ContentUris.parseId(uri) + (!TextUtils.isEmpty(where) ? " AND (" + where + ')' : BuildConfig.VERSION_NAME), whereArgs);
                break;
            case SYNCSTATE /*4*/:
            //    return this.mOpenHelper.getSyncState().update(db, values, where, whereArgs);
            case SYNCSTATE_ID /*5*/:
             //   return this.mOpenHelper.getSyncState().update(db, values, "_id=" + ContentUris.parseId(uri) + Token.SEPARATOR + (TextUtils.isEmpty(where) ? BuildConfig.VERSION_NAME : " AND (" + where + ")"), whereArgs);
            case FILES /*11*/:
                count = db.update(FILES_TABLE_NAME, values, where, whereArgs);
                break;
            case FILE_ID /*12*/:
                if (!values.containsKey(NoteFiles.DIRTY)) {
                    values.put(NoteFiles.DIRTY, Boolean.valueOf(true));
                }
                count = db.update(FILES_TABLE_NAME, values, "_id=" + ContentUris.parseId(uri) + (!TextUtils.isEmpty(where) ? " AND (" + where + ')' : BuildConfig.VERSION_NAME), whereArgs);
                break;
            case TAGS /*13*/:
                count = db.update(TAG_TABLE_NAME, values, where, whereArgs);
                break;
            case TAG_ID /*14*/:
                if (!values.containsKey(NoteCategory.DIRTY)) {
                    values.put(NoteCategory.DIRTY, Boolean.valueOf(true));
                }
                count = db.update(TAG_TABLE_NAME, values, "_id=" + ContentUris.parseId(uri) + (!TextUtils.isEmpty(where) ? " AND (" + where + ')' : BuildConfig.VERSION_NAME), whereArgs);
                break;
            case ACCOUNTS /*15*/:
                count = db.update(ACCOUNT_TABLE_NAME, values, where, whereArgs);
                break;
            case ACCOUNT_ID /*16*/:
                count = db.update(ACCOUNT_TABLE_NAME, values, "_id=" + ContentUris.parseId(uri) + (!TextUtils.isEmpty(where) ? " AND (" + where + ')' : BuildConfig.VERSION_NAME), whereArgs);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        if (count <= 0) {
            return count;
        }
        if (type != NOTES && type != NOTE_ID && type != FILES && type != FILE_ID) {
            return count;
        }
        notifyAppWidgetUpdated(getContext());
        return count;
    }

    //
    @Override
    public ContentProviderResult[] applyBatch(ArrayList<ContentProviderOperation> operations) throws OperationApplicationException {
        SQLiteDatabase db = this.mOpenHelper.getWritableDatabase();
        if (db == null) {
            return null;
        }
        db.beginTransaction();
        ContentProviderResult[] results = super.applyBatch(operations);
        db.setTransactionSuccessful();
        db.endTransaction();
        getContext().getContentResolver().notifyChange(Notes.CONTENT_URI, null);
        notifyAppWidgetUpdated(getContext());
        File parent = new File(NoteUtil.FILES_DIR);
        if (!parent.exists()) {
            return results;
        }
        getContext().sendBroadcast(new Intent("android.intent.action.MEDIA_SCANNER_SCAN_FILE", Uri.fromFile(parent)));
        return results;
    }

    ContentValues generatInbuiltSyncNoteData(SQLiteDatabase db) {
        String uuid = "inbuilt_note_0";
        int count = FILE_COLUMN_ID;
        String str = NOTES_TABLE_NAME;
        String[] strArr = new String[NOTES];
        strArr[FILE_COLUMN_ID] = NoteFiles.DEFAULT_SORT_ORDER;
        String[] strArr2 = new String[NOTES];
        strArr2[FILE_COLUMN_ID] = uuid;
        Cursor c = db.query(str, strArr, "uuid=?", strArr2, null, null, null);
        if (c != null) {
            count = c.getCount();
            c.close();
        }
        if (count != 0) {
            return null;
        }
        String note = "\u5982\u679c\u4f60\u540c\u65f6\u662fFlyme2.0\u53ca\u4e4b\u524d\u56fa\u4ef6\u7528\u6237\uff0c\u4ee5\u4e0b\u4fe1\u606f\u5bf9\u4f60\u975e\u5e38\u91cd\u8981\u3002\n\n\u5982\u4f60\u6240\u89c1\uff0cFlyme3.0\u4fbf\u7b7e\uff0c\u662f\u4e00\u6b21\u91cd\u5927\u5347\u7ea7\uff0c\u6d82\u9e26\u72ec\u7acb\u4e3a\u753b\u677f\uff0c\u5e76\u91cd\u65b0\u8bbe\u8ba1\u4e86\u51e0\u4e4e\u6240\u6709\u90e8\u5206\uff0c\u56e0\u6b64\u6570\u636e\u540c\u6b65\u662f\u4e00\u4e2a\u91cd\u8981\u6311\u6218\u3002\n\n\u9996\u6b21\u4f7f\u7528Flyme3.0\u540c\u6b65\u65f6\uff0c\u6211\u4eec\u4f1a\u5c06\u4e4b\u524d\u7684\u4fbf\u7b7e\uff0c\u5168\u90e8\u5347\u7ea7\u4e3aFlyme3.0\u683c\u5f0f\uff0c\u540c\u65f6\u6d82\u9e26\u4f1a\u8f6c\u6362\u4e3a\u6807\u51c6\u56fe\u7247\u3002\n\n\u5347\u7ea7\u5b8c\u6210\u540e\uff0cFlyme3.0\u4fbf\u7b7e\u5c06\u4e0d\u518d\u4e0e\u4e4b\u524d\u56fa\u4ef6\u540c\u6b65\u3002\u76f8\u540c\u56fa\u4ef6\u4e4b\u95f4\u7684\u540c\u6b65\uff0c\u53ca\u624b\u673a\u4e0e\u4e91\u670d\u52a1\u7684\u540c\u6b65\u4e0d\u53d7\u5f71\u54cd\u3002";
        try {
            JSONArray ja = new JSONArray();
            JSONObject jo = new JSONObject();
            jo.put(NoteUtil.JSON_STATE, FILE_COLUMN_ID);
            jo.put(NoteUtil.JSON_TEXT, note);
            ja.put(jo);
            note = ja.toString();
            Long now = Long.valueOf(System.currentTimeMillis());
            ContentValues cv = new ContentValues();
            cv.put(Notes.UUID, uuid);
            cv.put(Notes.NOTE, note);
            cv.put(Notes.CREATE_TIME, now);
            cv.put(Notes.MODIFIED_DATE, now);
            cv.put(Notes.PAPER, Integer.valueOf(FILE_COLUMN_ID));
            cv.put(Notes.TITLE, "\u4fbf\u7b7e\u540c\u6b65\u8bf4\u660e");
            return cv;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    //没搞懂，批量插入数据库
    public int bulkInsert(Uri uri, ContentValues[] values) {
/*        Log.d(TAG, "start ");
        if (sUriMatcher.match(uri) != OLD_NOTES) {
            return super.bulkInsert(uri, values);
        }
        int numValues = FILE_COLUMN_ID;
        if (values == null) {
            return FILE_COLUMN_ID;
        }
        int i$;
        ArrayList<ContentValues> fileList = new ArrayList();
        NoteUpgradeUtils nu = new NoteUpgradeUtils();
        ContentValues[] arr$ = values;
        int len$ = arr$.length;
        for (i$ = FILE_COLUMN_ID; i$ < len$; i$ += NOTES) {
            ContentValues cv = arr$[i$];
            Log.d(TAG, "cv:  " + cv);
            nu.upgradeContentValue(cv, fileList);
        }
        SQLiteDatabase db = this.mOpenHelper.getWritableDatabase();
        ContentValues syncData = generatInbuiltSyncNoteData(db);
        if (db == null) {
            return FILE_COLUMN_ID;
        }
        db.beginTransaction();
        arr$ = values;
        len$ = arr$.length;
        for (i$ = FILE_COLUMN_ID; i$ < len$; i$ += NOTES) {
            if (db.insert(NOTES_TABLE_NAME, Notes.NOTE, arr$[i$]) != -1) {
                numValues += NOTES;
            }
        }
        if (syncData != null) {
            db.insert(NOTES_TABLE_NAME, Notes.NOTE, syncData);
        }
        Iterator i$2 = fileList.iterator();
        while (i$2.hasNext()) {
            cv = (ContentValues) i$2.next();
            if (db.insert(FILES_TABLE_NAME, null, cv) == -1) {
                Log.d(TAG, "insert file table fail: " + cv.getAsString(NoteUtil.JSON_FILE_NAME));
            }
        }
        db.setTransactionSuccessful();
        db.endTransaction();
        Log.d(TAG, "end  bulkInsert");
        getContext().getContentResolver().notifyChange(Notes.CONTENT_URI, null);
        notifyAppWidgetUpdated(getContext());
        if (fileList.size() <= 0) {
            return numValues;
        }
        File parent = new File(NoteUtil.FILES_DIR);
        if (!parent.exists()) {
            return numValues;
        }
        getContext().sendBroadcast(new Intent("android.intent.action.MEDIA_SCANNER_SCAN_FILE", Uri.fromFile(parent)));
        return numValues;*/
        return 0;
    }

    //没搞懂，这是干什么的
    public ParcelFileDescriptor openFile(Uri uri, String mode) throws FileNotFoundException {
        ParcelFileDescriptor fileDescriptor = null;
/*        Cursor cursor;
        int imode = 268435456;
        if (mode != null) {
            if (!mode.equals("r")) {
                if (mode.equals("rw")) {
                    imode = 939524096;
                } else {
                    if (mode.equals("rwt")) {
                        imode = 1006632960;
                    }
                }
                fileDescriptor = null;
                cursor = null;
                switch (sUriMatcher.match(uri)) {
                    case FILE_ID *//*12*//*:
                        String fileId = (String) uri.getPathSegments().get(NOTES);
                        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
                        qb.setProjectionMap(sFilesProjectionMap);
                        qb.setTables(FILES_TABLE_NAME);
                        qb.appendWhere("_id=" + fileId);
                        SQLiteDatabase db = this.mOpenHelper.getReadableDatabase();
                        String[] strArr = new String[NOTE_ID];
                        strArr[FILE_COLUMN_ID] = NoteFiles.NOTE_UUID;
                        strArr[NOTES] = NoteUtil.JSON_FILE_NAME;
                        cursor = qb.query(db, strArr, null, null, null, null, null);
                        if (cursor == null && cursor.moveToFirst()) {
                            String path = NoteUtil.getFileName(cursor.getString(FILE_COLUMN_ID), cursor.getString(NOTES));
                            if (path != null) {
                                fileDescriptor = ParcelFileDescriptor.open(new File(path), imode);
                                break;
                            }
                        }
                        throw new FileNotFoundException("No relative database record for the file by provider at " + uri);
                }
                if (cursor != null) {
                    cursor.close();
                }
                return fileDescriptor;
            }
        }
        imode = 268435456;
        fileDescriptor = null;
        cursor = null;
        switch (sUriMatcher.match(uri)) {
            case FILE_ID *//*12*//*:
                String fileId2 = (String) uri.getPathSegments().get(NOTES);
                SQLiteQueryBuilder qb2 = new SQLiteQueryBuilder();
                qb2.setProjectionMap(sFilesProjectionMap);
                qb2.setTables(FILES_TABLE_NAME);
                qb2.appendWhere("_id=" + fileId2);
                SQLiteDatabase db2 = this.mOpenHelper.getReadableDatabase();
                String[] strArr2 = new String[NOTE_ID];
                strArr2[FILE_COLUMN_ID] = NoteFiles.NOTE_UUID;
                strArr2[NOTES] = NoteUtil.JSON_FILE_NAME;
                cursor = qb2.query(db2, strArr2, null, null, null, null, null);
                if (cursor == null) {
                    break;
                }
                throw new FileNotFoundException("No relative database record for the file by provider at " + uri);
        }
        if (cursor != null) {
            cursor.close();
        }*/
        return fileDescriptor;
    }

    //好像是通知更新界面
    public void notifyAppWidgetUpdated(Context context) {
    //    ((NoteApplication) context.getApplicationContext()).notifyWidgetUpdate();
    }

    static {
        sUriMatcher.addURI(NotePaper.AUTHORITY, NOTES_TABLE_NAME, NOTES);
        sUriMatcher.addURI(NotePaper.AUTHORITY, "notes/#", NOTE_ID);
        sUriMatcher.addURI(NotePaper.AUTHORITY, FILES_TABLE_NAME, FILES);
        sUriMatcher.addURI(NotePaper.AUTHORITY, "notefiles/#", FILE_ID);
        sUriMatcher.addURI(NotePaper.AUTHORITY, "live_folders/notes", LIVE_FOLDER_NOTES);
        sUriMatcher.addURI(NotePaper.AUTHORITY, "syncstate/#", SYNCSTATE_ID);
        sUriMatcher.addURI(NotePaper.AUTHORITY, "oldnotes", OLD_NOTES);
        sUriMatcher.addURI(NotePaper.AUTHORITY, "category", TAGS);
        sUriMatcher.addURI(NotePaper.AUTHORITY, "category/#", TAG_ID);
        sUriMatcher.addURI(NotePaper.AUTHORITY, ACCOUNT_TABLE_NAME, ACCOUNTS);
        sUriMatcher.addURI(NotePaper.AUTHORITY, "accounts/#", ACCOUNT_ID);
        sNotesProjectionMap.put(NoteFiles.DEFAULT_SORT_ORDER, NoteFiles.DEFAULT_SORT_ORDER);
        sNotesProjectionMap.put(Notes.NOTE, Notes.NOTE);
        sNotesProjectionMap.put(Notes.CREATE_TIME, Notes.CREATE_TIME);
        sNotesProjectionMap.put(Notes.MODIFIED_DATE, Notes.MODIFIED_DATE);
        sNotesProjectionMap.put(Notes.PAPER, Notes.PAPER);
        sNotesProjectionMap.put(Notes.TITLE, Notes.TITLE);
        sNotesProjectionMap.put(Notes.NOTE_WITH_STYLE, Notes.NOTE_WITH_STYLE);
        sNotesProjectionMap.put(Notes.FONT_COLOR, Notes.FONT_COLOR);
        sNotesProjectionMap.put(Notes.FONT_SIZE, Notes.FONT_SIZE);
        sNotesProjectionMap.put(Notes.UUID, Notes.UUID);
        sNotesProjectionMap.put(Notes.FIRST_IMAGE, Notes.FIRST_IMAGE);
        sNotesProjectionMap.put(Notes.FIRST_RECORD, Notes.FIRST_RECORD);
        sNotesProjectionMap.put(Notes.FILE_LIST, Notes.FILE_LIST);
        sNotesProjectionMap.put(Notes.DIRTY, Notes.DIRTY);
        sNotesProjectionMap.put(Notes.ENCRYPT, Notes.ENCRYPT);
        sNotesProjectionMap.put(Notes.TOP, Notes.TOP);
        sNotesProjectionMap.put(Notes.TAG, Notes.TAG);
        sNotesProjectionMap.put(Notes.DESKTOP, Notes.DESKTOP);
        sNotesProjectionMap.put(Notes.ACCOUNT_ID, Notes.ACCOUNT_ID);
        sNotesProjectionMap.put(Notes.SYNC_ID, Notes.SYNC_ID);
        sNotesProjectionMap.put(Notes.RESERVED1, Notes.RESERVED1);
        sNotesProjectionMap.put(Notes.RESERVED2, Notes.RESERVED2);
        sNotesProjectionMap.put(Notes.SYNC_DATA1, Notes.SYNC_DATA1);
        sNotesProjectionMap.put(Notes.SYNC_DATA1, Notes.SYNC_DATA2);
        sNotesProjectionMap.put(Notes.SYNC_DATA1, Notes.SYNC_DATA3);
        sNotesProjectionMap.put(Notes.SYNC_DATA1, Notes.SYNC_DATA4);
        sFilesProjectionMap.put(NoteFiles.DEFAULT_SORT_ORDER, NoteFiles.DEFAULT_SORT_ORDER);
        sFilesProjectionMap.put(NoteFiles.NOTE_UUID, NoteFiles.NOTE_UUID);
        sFilesProjectionMap.put(NoteUtil.JSON_FILE_NAME, NoteUtil.JSON_FILE_NAME);
        sFilesProjectionMap.put(NoteFiles.MD5, NoteFiles.MD5);
        sFilesProjectionMap.put(Constants.JSON_KEY_TYPE, Constants.JSON_KEY_TYPE);
        sFilesProjectionMap.put(NoteUtil.JSON_MTIME, NoteUtil.JSON_MTIME);
        sFilesProjectionMap.put(NoteFiles.DATA1, NoteFiles.DATA1);
        sFilesProjectionMap.put(NoteFiles.DATA2, NoteFiles.DATA2);
        sFilesProjectionMap.put(NoteFiles.DIRTY, NoteFiles.DIRTY);
        sFilesProjectionMap.put(NoteFiles.SYNC_DATA1, NoteFiles.SYNC_DATA1);
        sFilesProjectionMap.put(NoteFiles.SYNC_DATA2, NoteFiles.SYNC_DATA2);
        sFilesProjectionMap.put(NoteFiles.SYNC_DATA3, NoteFiles.SYNC_DATA3);
        sFilesProjectionMap.put(NoteFiles.SYNC_DATA4, NoteFiles.SYNC_DATA4);
        sTagProjectionMap.put(NoteFiles.DEFAULT_SORT_ORDER, NoteFiles.DEFAULT_SORT_ORDER);
        sTagProjectionMap.put(Notes.UUID, Notes.UUID);
        sTagProjectionMap.put(NoteUtil.JSON_FILE_NAME, NoteUtil.JSON_FILE_NAME);
        sTagProjectionMap.put(NoteCategory.CATEGORY_ORDER, NoteCategory.CATEGORY_ORDER);
        sTagProjectionMap.put(NoteCategory.DIRTY, NoteCategory.DIRTY);
        sTagProjectionMap.put(NoteCategory.DELETE, NoteCategory.DELETE);
        sTagProjectionMap.put(NoteCategory.DATA1, NoteCategory.DATA1);
        sTagProjectionMap.put(NoteCategory.DATA2, NoteCategory.DATA2);
        sTagProjectionMap.put(NoteCategory.SYNC_DATA1, NoteCategory.SYNC_DATA1);
        sTagProjectionMap.put(NoteCategory.SYNC_DATA2, NoteCategory.SYNC_DATA2);
        sTagProjectionMap.put(NoteCategory.SYNC_DATA3, NoteCategory.SYNC_DATA3);
        sTagProjectionMap.put(NoteCategory.SYNC_DATA4, NoteCategory.SYNC_DATA4);
        sAccountProjectionMap.put(NoteFiles.DEFAULT_SORT_ORDER, NoteFiles.DEFAULT_SORT_ORDER);
        sAccountProjectionMap.put(AccountConstract.ACCOUNT_NMAE, AccountConstract.ACCOUNT_NMAE);
    }

    private static void createTriggers(SQLiteDatabase db) {
        db.execSQL("DROP TRIGGER IF EXISTS files_cleanup");
    }

    private static String getNoteFilePath(String uuid) {
        return NoteUtil.FILES_DIR + NoteUtil.RECORD_DIV + uuid;
    }

    static void deleteSdcardFiles(String uuid) {
        Log.d(TAG, "begin remove " + uuid);
        String fileName = getNoteFilePath(uuid);
        File file = new File(fileName);
        if (file.exists()) {
            NoteUtil.deleteFile(file);
        } else {
            Log.d(TAG, "file not found " + fileName);
        }
        Log.d(TAG, "end remove " + fileName);
    }
}

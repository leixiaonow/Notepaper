package com.meizu.flyme.notepaper.utils;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.support.v7.appcompat.BuildConfig;
import android.util.Log;
import com.meizu.flyme.notepaper.Config;
import java.io.File;
import java.io.FileNotFoundException;

public class TempFileProvider extends ContentProvider {
    public static final String AUTHORITY = (Config.PACKAGE_NAME + ".TempFile");
    private static final int MMS_SCRAP_SPACE = 1;
    public static final Uri SCRAP_CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/scrapSpace");
    private static String TAG = "TempFileProvider";
    private static final UriMatcher sURLMatcher = new UriMatcher(-1);

    static {
        sURLMatcher.addURI(AUTHORITY, "scrapSpace", MMS_SCRAP_SPACE);
    }

    public boolean onCreate() {
        return true;
    }

    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        return null;
    }

    public Uri insert(Uri uri, ContentValues values) {
        return null;
    }

    public int delete(Uri uri, String selection, String[] selectionArgs) {
        return 0;
    }

    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        return 0;
    }

    private ParcelFileDescriptor getTempStoreFd() {
        File file = getScrapPath(getContext());
        ParcelFileDescriptor pfd = null;
        try {
            File parentFile = file.getParentFile();
            File pDataDir = new File(NoteUtil.FILES_ANDROID_DATA);
            if (pDataDir == null || !pDataDir.exists()) {
                Log.d(TAG, "Android data dir not exist.");
                return null;
            } else if (parentFile.exists() || parentFile.mkdirs()) {
                pfd = ParcelFileDescriptor.open(file, 939524096);
                return pfd;
            } else {
                Log.e(TAG, "[TempFileProvider] tempStoreFd: " + parentFile.getPath() + "does not exist!");
                return null;
            }
        } catch (Exception ex) {
            Log.e(TAG, "getTempStoreFd: error creating pfd for " + file.getAbsolutePath(), ex);
        }
    }

    public String getType(Uri uri) {
        return "*/*";
    }

    public ParcelFileDescriptor openFile(Uri uri, String mode) throws FileNotFoundException {
        switch (sURLMatcher.match(uri)) {
            case MMS_SCRAP_SPACE /*1*/:
                return getTempStoreFd();
            default:
                return null;
        }
    }

    public static File getScrapPath(Context context, String fileName) {
        return new File(context.getExternalCacheDir(), fileName);
    }

    public static File getScrapPath(Context context) {
        return getScrapPath(context, ".temp.jpg");
    }

    public static Uri renameScrapFile(String fileExtension, String uniqueIdentifier, Context context) {
        if (uniqueIdentifier == null) {
            uniqueIdentifier = BuildConfig.VERSION_NAME;
        }
        File newTempFile = getScrapPath(context, ".temp" + uniqueIdentifier + fileExtension);
        File oldTempFile = getScrapPath(context);
        newTempFile.delete();
        if (oldTempFile.renameTo(newTempFile)) {
            return Uri.fromFile(newTempFile);
        }
        return null;
    }
}

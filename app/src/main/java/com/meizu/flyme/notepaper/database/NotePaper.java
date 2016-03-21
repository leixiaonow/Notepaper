package com.meizu.flyme.notepaper.database;

import android.net.Uri;
import android.provider.BaseColumns;
import com.meizu.flyme.notepaper.Config;

public final class NotePaper {
    public static final String AUTHORITY = Config.URI_AUTHORITY;
    public static final char BLUE = 'b';
    public static final String EXTRAS_POSITION = "notepaper.extra.POSITION";
    public static final boolean FUNCTION_GROUP = true;
    public static final boolean FUNCTION_RESTORE = true;
    public static final char GREEN = 'g';
    public static final char PINK = 'p';
    public static final String PROPERTY_NOTE_GROUP = "debug.note.group";
    public static final String PROPERTY_NOTE_RESTORE = "debug.note.restore";
    public static final char WHITE = 'w';
    public static final char YELLOW = 'y';

    public static final class AccountConstract implements BaseColumns {
        public static final String ACCOUNT_NMAE = "account_name";
        public static final Uri CONTENT_URI = Uri.parse("content://" + NotePaper.AUTHORITY + "/accounts");

        private AccountConstract() {
        }
    }

    public static final class NoteCategory implements BaseColumns {
        public static final String CATEGORY_NAME = "name";
        public static final String CATEGORY_ORDER = "sort";
        public static final String CATEGORY_UUID = "uuid";
        public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.notepaper.category";
        public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.notepaper.category";
        public static final Uri CONTENT_URI = Uri.parse("content://" + NotePaper.AUTHORITY + "/category");
        public static String DATA1 = "data1";
        public static String DATA2 = "data2";
        public static final String DEFAULT_SORT_ORDER = "sort ASC,name ASC";
        public static String DELETE = "deleted";
        public static String DIRTY = "dirty";
        public static String SYNC_DATA1 = "sync_data1";
        public static String SYNC_DATA2 = "sync_data2";
        public static String SYNC_DATA3 = "sync_data3";
        public static String SYNC_DATA4 = "sync_data4";

        private NoteCategory() {
        }
    }

    public static final class NoteFiles implements BaseColumns {
        public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.notepaper.notefile";
        public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.notepaper.notefile";
        public static final Uri CONTENT_URI = Uri.parse("content://" + NotePaper.AUTHORITY + "/notefiles");
        public static String DATA1 = "data1";
        public static String DATA2 = "data2";
        public static final String DEFAULT_SORT_ORDER = "_id";
        public static String DIRTY = "dirty";
        public static final String FILE_MTIME = "mtime";
        public static final String FILE_NAME = "name";
        public static final String MD5 = "md5";
        public static final String NOTE_UUID = "note_uuid";
        public static String SYNC_DATA1 = "sync_data1";
        public static String SYNC_DATA2 = "sync_data2";
        public static String SYNC_DATA3 = "sync_data3";
        public static String SYNC_DATA4 = "sync_data4";
        public static final String TYPE = "type";
        public static final int TYPE_AUDIO = 1;
        public static final int TYPE_IMAGE = 0;

        private NoteFiles() {
        }
    }

    public static final class Notes implements BaseColumns {
        public static String ACCOUNT_ID = "account_id";
        public static final String COLOR = "color";
        public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.notepaper.note";
        public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.notepaper.note";
        public static final Uri CONTENT_URI = Uri.parse("content://" + NotePaper.AUTHORITY + "/notes");
        public static final String CREATE_TIME = "create_time";
        public static final String DEFAULT_SORT_ORDER = "top DESC,modified DESC";
        public static String DESKTOP = "desktop";
        public static String DIRTY = "dirty";
        public static String ENCRYPT = "encrypt";
        public static String FILE_LIST = "file_list";
        public static String FIRST_IMAGE = "first_img";
        public static String FIRST_RECORD = "first_record";
        public static String FONT_COLOR = "font_color";
        public static String FONT_SIZE = "font_size";
        public static final String MODIFIED_DATE = "modified";
        public static final String NOTE = "note";
        public static final String NOTE_WITH_STYLE = "note_with_style";
        public static final Uri OLD_CONTENT_URI = Uri.parse("content://" + NotePaper.AUTHORITY + "/oldnotes");
        public static final String PAPER = "paper";
        public static String RESERVED1 = "reserved1";
        public static String RESERVED2 = "reserved2";
        public static String SYNC_DATA1 = "sync_data1";
        public static String SYNC_DATA2 = "sync_data2";
        public static String SYNC_DATA3 = "sync_data3";
        public static String SYNC_DATA4 = "sync_data4";
        public static String SYNC_ID = "sync_id";
        public static String TAG = "tag";
        public static final String TITLE = "title";
        public static String TOP = "top";
        public static final String UUID = "uuid";
        public static final String VERSION = "version";

        private Notes() {
        }
    }

    private NotePaper() {
    }
}

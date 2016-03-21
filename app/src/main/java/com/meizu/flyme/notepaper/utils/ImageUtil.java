package com.meizu.flyme.notepaper.utils;

import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Environment;
import android.os.StatFs;
import android.support.v7.appcompat.BuildConfig;
import android.util.Log;
import com.android.volley.DefaultRetryPolicy;
import com.meizu.flyme.notepaper.reflect.ReflectUtils;
import com.meizu.text.format.DateTimeUtils;
import com.meizu.update.iresponse.MzUpdateResponseCode;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class ImageUtil {
    public static final float MAX_IMAGE_SIZE = 1800.0f;
    public static final int RESULT_DATA_DIR_FAIL = -4;
    public static final int RESULT_FILE_NOT_AVAILABLE = -2;
    public static final int RESULT_OTHER_FAILURE = -5;
    public static final int RESULT_SAVE_FAILURE = -3;
    public static final int RESULT_SPACE_NOT_ENOUGH = -1;
    public static final int RESULT_SUCCESS = 0;
    static final String TAG = "ImageUtil";
    static Config config = Config.ARGB_8888;

    public static class NoteImageGetter {
        Context mContext;
        int mWidth;

        public NoteImageGetter(Context context, int width) {
            this.mWidth = width;
            this.mContext = context;
        }

        public BitmapDrawable getDrawable(String source) {
            BitmapDrawable drawable = createFromPath(source);
            if (drawable != null) {
                drawable.setBounds(ImageUtil.RESULT_SUCCESS, ImageUtil.RESULT_SUCCESS, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
            }
            return drawable;
        }

        public BitmapDrawable createFromPath(String pathName) {
            Options options;
            if (pathName == null) {
                return null;
            }
            File image = new File(pathName);
            if (!image.exists()) {
                return null;
            }
            Bitmap bm = null;
            try {
                Options opts;
                Options opts1 = new Options();
                try {
                    opts1.inJustDecodeBounds = true;
                    BitmapFactory.decodeFile(image.toString(), opts1);
                    opts = new Options();
                } catch (Exception e) {
                    options = opts1;
                    if (bm != null) {
                        return null;
                    }
                    bm.recycle();
                    return null;
                }
                try {
                    opts.inSampleSize = opts1.outWidth / this.mWidth;
                    opts.inPreferredConfig = ImageUtil.config;
                    opts.inJustDecodeBounds = false;
                    opts.inInputShareable = true;
                    opts.inPurgeable = true;
                    bm = BitmapFactory.decodeFile(image.toString(), opts);
                    Bitmap pic = bm;
                    if (!(bm == null || pic == bm)) {
                        bm.recycle();
                    }
                    if (pic != null) {
                        return new BitmapDrawable(this.mContext.getResources(), pic);
                    }
                    return null;
                } catch (Exception e2) {
                    options = opts1;
                    Options options2 = opts;
                    if (bm != null) {
                        return null;
                    }
                    bm.recycle();
                    return null;
                }
            } catch (Exception e3) {
                if (bm != null) {
                    return null;
                }
                bm.recycle();
                return null;
            }
        }
    }

    public static class WidgetImageGetter {
        public Bitmap getBitmap(Context context, Uri uri, int maxWidth, int maxHeight) {
            Bitmap bmp = ImageUtil.decodeImageToBitmap(context, uri);
            if (bmp == null) {
                return null;
            }
            int originWidth = bmp.getWidth();
            int width = originWidth;
            int height = bmp.getHeight();
            Bitmap ret = bmp;
            if (width != maxWidth) {
                width = maxWidth;
                height = (int) Math.floor(((double) height) / ((double) ((((float) originWidth) * DefaultRetryPolicy.DEFAULT_BACKOFF_MULT) / ((float) width))));
                Bitmap pic = Bitmap.createScaledBitmap(bmp, width, height, true);
                if (pic != null) {
                    bmp.recycle();
                    ret = pic;
                }
            }
            if (height > maxHeight) {
                Bitmap bm = Bitmap.createBitmap(ret, ImageUtil.RESULT_SUCCESS, (height - maxHeight) / 2, width, maxHeight);
                if (bm != null) {
                    ret.recycle();
                    return bm;
                }
            }
            return ret;
        }
    }

    private static Bitmap decodeUriToBitmap(ContentResolver cr, Uri uri, Options options) {
        Bitmap bm = null;
        InputStream is = null;
        try {
            is = cr.openInputStream(uri);
            bm = BitmapFactory.decodeStream(is, null, options);
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                }
            }
        } catch (IOException e2) {
            Log.e("decodeImageToBitmap", e2.toString());
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e3) {
                }
            }
        } catch (Throwable th) {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e4) {
                }
            }
        }
        return bm;
    }

    public static Bitmap decodeImageToBitmap(Context context, Uri uri) {
        Options opts = new Options();
        opts.inJustDecodeBounds = true;
        opts.inPreferredConfig = config;
        opts.inPurgeable = true;
        opts.inInputShareable = true;
        Bitmap bm = decodeUriToBitmap(context.getContentResolver(), uri, opts);
        opts.inSampleSize = (int) Math.floor((double) Math.max(((float) opts.outWidth) / MAX_IMAGE_SIZE, ((float) opts.outHeight) / MAX_IMAGE_SIZE));
        opts.inJustDecodeBounds = false;
        return rotateBitmapwithRotater(decodeUriToBitmap(context.getContentResolver(), uri, opts), ReflectUtils.getOrientFromInputStream(context, uri));
    }

    public static Bitmap rotateBitmapwithRotater(Bitmap b, int rotater) {
        if (b == null) {
            return null;
        }
        Matrix matrix = new Matrix();
        switch (rotater) {
            case MzUpdateResponseCode.INSTALL_NOT_SUPPORT /*3*/:
                matrix.setRotate(180.0f);
                break;
            case DateTimeUtils.FORMAT_TYPE_PERSONAL_FOOTPRINT /*6*/:
                matrix.setRotate(90.0f);
                break;
            case DateTimeUtils.FORMAT_TYPE_CALENDAR_APPWIDGET /*8*/:
                matrix.setRotate(270.0f);
                break;
            default:
                return b;
        }
        Bitmap newbit = Bitmap.createBitmap(b, RESULT_SUCCESS, RESULT_SUCCESS, b.getWidth(), b.getHeight(), matrix, true);
        if (newbit != b) {
            b.recycle();
        }
        return newbit;
    }

    public static boolean saveBitmap2file(Bitmap bmp, String filename) {
        Exception e;
        Throwable th;
        boolean z = false;
        if (filename != null) {
            CompressFormat format = CompressFormat.JPEG;
            if (filename.endsWith(".png")) {
                format = CompressFormat.PNG;
            }
            OutputStream stream = null;
            try {
                OutputStream stream2 = new FileOutputStream(filename);
                try {
                    z = bmp.compress(format, 100, stream2);
                    if (stream2 != null) {
                        try {
                            stream2.close();
                        } catch (IOException e2) {
                            e2.printStackTrace();
                        }
                    }
                } catch (Exception e3) {
                    e = e3;
                    stream = stream2;
                    try {
                        e.printStackTrace();
                        if (stream != null) {
                            try {
                                stream.close();
                            } catch (IOException e22) {
                                e22.printStackTrace();
                            }
                        }
                        return z;
                    } catch (Throwable th2) {
                        th = th2;
                        if (stream != null) {
                            try {
                                stream.close();
                            } catch (IOException e222) {
                                e222.printStackTrace();
                            }
                        }
                        throw th;
                    }
                } catch (Throwable th3) {
                    th = th3;
                    stream = stream2;
                    if (stream != null) {
                        stream.close();
                    }
                    throw th;
                }
            } catch (Exception e4) {
                e = e4;
                e.printStackTrace();
                if (stream != null) {
                    stream.close();
                }
                return z;
            }
        }
        return z;
    }

    public static boolean checkSdcardAvailableSpace(long size) {
        if (!Environment.getExternalStorageState().equals("mounted")) {
            return false;
        }
        long availableSize = new StatFs(Environment.getExternalStorageDirectory().getPath()).getAvailableBytes();
        if (availableSize < size || availableSize == 0) {
            return false;
        }
        return true;
    }

    public static File getImageFile(Context context, Uri uri, String uuid) {
        if (uri == null) {
            return null;
        }
        File file = null;
        String prefix = "img_" + NoteUtil.getTimeString();
        int index = RESULT_SUCCESS;
        while (index < ActivityChooserViewAdapter.MAX_ACTIVITY_COUNT_UNLIMITED) {
            String indexStr;
            if (index == 0) {
                indexStr = BuildConfig.VERSION_NAME;
            } else {
                indexStr = "_" + String.valueOf(index);
            }
            file = NoteUtil.getFile(uuid, prefix + indexStr + ".jpg");
            if (file.exists()) {
                file = null;
                index++;
            } else if (file.getParentFile().exists()) {
                return file;
            } else {
                File pDataDir = new File(NoteUtil.FILES_ANDROID_DATA);
                if (pDataDir == null || !pDataDir.exists()) {
                    Log.d(TAG, "Android data dir not exist.");
                    return null;
                } else if (file.getParentFile().mkdirs()) {
                    return file;
                } else {
                    Log.d(TAG, "mkdirs fail: " + file.getParentFile().getPath());
                    return null;
                }
            }
        }
        return file;
    }

    public static int saveIntoFile(Context context, Uri uri, File file) {
        Exception e;
        Throwable th;
        if (file == null || uri == null) {
            return RESULT_SUCCESS;
        }
        Bitmap bmp = decodeImageToBitmap(context, uri);
        if (bmp == null) {
            return RESULT_FILE_NOT_AVAILABLE;
        }
        MemoByteArrayOutputStream baos = new MemoByteArrayOutputStream();
        bmp.compress(CompressFormat.JPEG, 75, baos);
        FileOutputStream os = null;
        try {
            FileOutputStream os2 = new FileOutputStream(file);
            try {
                baos.writeTo(os2);
                if (os2 != null) {
                    try {
                        os2.close();
                    } catch (IOException e2) {
                        e2.printStackTrace();
                    }
                }
                if (baos != null) {
                    try {
                        baos.close();
                    } catch (IOException e22) {
                        e22.printStackTrace();
                    }
                }
                if (bmp == null) {
                    return RESULT_SUCCESS;
                }
                bmp.recycle();
                return RESULT_SUCCESS;
            } catch (Exception e3) {
                e = e3;
                os = os2;
                try {
                    e.printStackTrace();
                    if (checkSdcardAvailableSpace((long) baos.getByteArrayCount())) {
                        if (os != null) {
                            try {
                                os.close();
                            } catch (IOException e222) {
                                e222.printStackTrace();
                            }
                        }
                        if (baos != null) {
                            try {
                                baos.close();
                            } catch (IOException e2222) {
                                e2222.printStackTrace();
                            }
                        }
                        if (bmp != null) {
                            return RESULT_SAVE_FAILURE;
                        }
                        bmp.recycle();
                        return RESULT_SAVE_FAILURE;
                    }
                    if (os != null) {
                        try {
                            os.close();
                        } catch (IOException e22222) {
                            e22222.printStackTrace();
                        }
                    }
                    if (baos != null) {
                        try {
                            baos.close();
                        } catch (Exception e4) {
                            e4.printStackTrace();
                        }
                    }
                    if (bmp != null) {
                        return RESULT_SPACE_NOT_ENOUGH;
                    }
                    bmp.recycle();
                    return RESULT_SPACE_NOT_ENOUGH;
                } catch (Throwable th2) {
                    th = th2;
                    if (os != null) {
                        try {
                            os.close();
                        } catch (IOException e222222) {
                            e222222.printStackTrace();
                        }
                    }
                    if (baos != null) {
                        try {
                            baos.close();
                        } catch (IOException e2222222) {
                            e2222222.printStackTrace();
                        }
                    }
                    if (bmp != null) {
                        bmp.recycle();
                    }
                    throw th;
                }
            } catch (Throwable th3) {
                th = th3;
                os = os2;
                if (os != null) {
                    os.close();
                }
                if (baos != null) {
                    baos.close();
                }
                if (bmp != null) {
                    bmp.recycle();
                }
                throw th;
            }
        } catch (Exception e5) {
            e4 = e5;
            e4.printStackTrace();
            if (checkSdcardAvailableSpace((long) baos.getByteArrayCount())) {
                if (os != null) {
                    os.close();
                }
                if (baos != null) {
                    baos.close();
                }
                if (bmp != null) {
                    return RESULT_SPACE_NOT_ENOUGH;
                }
                bmp.recycle();
                return RESULT_SPACE_NOT_ENOUGH;
            }
            if (os != null) {
                os.close();
            }
            if (baos != null) {
                baos.close();
            }
            if (bmp != null) {
                return RESULT_SAVE_FAILURE;
            }
            bmp.recycle();
            return RESULT_SAVE_FAILURE;
        }
    }

    public static void getImageSizeRect(String fileName, Rect rect) {
        Exception e;
        try {
            Options opts = new Options();
            try {
                opts.inJustDecodeBounds = true;
                Bitmap bm = BitmapFactory.decodeFile(fileName, opts);
                if (opts != null) {
                    rect.set(RESULT_SUCCESS, RESULT_SUCCESS, opts.outWidth, opts.outHeight);
                }
                if (bm != null) {
                    bm.recycle();
                }
            } catch (Exception e2) {
                e = e2;
                Options options = opts;
                e.printStackTrace();
            }
        } catch (Exception e3) {
            e = e3;
            e.printStackTrace();
        }
    }
}

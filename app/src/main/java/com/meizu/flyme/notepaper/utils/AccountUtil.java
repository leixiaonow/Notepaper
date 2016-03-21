package com.meizu.flyme.notepaper.utils;

import android.net.Uri;

public class AccountUtil {
    public static Uri ACCOUNT_URI = Uri.parse("content://com.meizu.account/account");
    public static final String FLYME_ACCOUNT_TYPE = "com.meizu.account";
    public static final String FLYME_SUFFIX = "@flyme.cn";
    public static final String KEY_FLYME_NAME = "flyme";
    public static final String TAG = "AccountUtil";

    public static long getMeizuAccountID(android.content.Context r20) {
        /* JADX: method processing error */
/*
Error: java.util.NoSuchElementException
	at java.util.HashMap$HashIterator.nextNode(HashMap.java:1431)
	at java.util.HashMap$KeyIterator.next(HashMap.java:1453)
	at jadx.core.dex.visitors.blocksmaker.BlockFinallyExtract.applyRemove(BlockFinallyExtract.java:535)
	at jadx.core.dex.visitors.blocksmaker.BlockFinallyExtract.extractFinally(BlockFinallyExtract.java:175)
	at jadx.core.dex.visitors.blocksmaker.BlockFinallyExtract.processExceptionHandler(BlockFinallyExtract.java:79)
	at jadx.core.dex.visitors.blocksmaker.BlockFinallyExtract.visit(BlockFinallyExtract.java:51)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.ProcessClass.process(ProcessClass.java:37)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:280)
	at jadx.api.JavaClass.decompile(JavaClass.java:62)
*/
        /*
        r11 = android.accounts.AccountManager.get(r20);
        r1 = "com.meizu.account";	 Catch:{ Exception -> 0x0014 }
        r10 = r11.getAccountsByType(r1);	 Catch:{ Exception -> 0x0014 }
        r19 = 0;
        if (r10 == 0) goto L_0x0011;
    L_0x000e:
        r1 = r10.length;
        if (r1 != 0) goto L_0x0022;
    L_0x0011:
        r8 = 0;
    L_0x0013:
        return r8;
    L_0x0014:
        r15 = move-exception;
        r15.printStackTrace();
        r1 = "AccountUtil";
        r2 = "getAccountsByType error!";
        android.util.Log.e(r1, r2);
        r8 = 0;
        goto L_0x0013;
    L_0x0022:
        r1 = 0;
        r1 = r10[r1];
        r0 = r1.name;
        r19 = r0;
        r8 = 0;
        r13 = 0;
        r1 = r20.getContentResolver();	 Catch:{ Exception -> 0x0192, all -> 0x019e }
        r2 = com.meizu.flyme.notepaper.database.NotePaper.AccountConstract.CONTENT_URI;	 Catch:{ Exception -> 0x0192, all -> 0x019e }
        r3 = 2;	 Catch:{ Exception -> 0x0192, all -> 0x019e }
        r3 = new java.lang.String[r3];	 Catch:{ Exception -> 0x0192, all -> 0x019e }
        r4 = 0;	 Catch:{ Exception -> 0x0192, all -> 0x019e }
        r5 = "_id";	 Catch:{ Exception -> 0x0192, all -> 0x019e }
        r3[r4] = r5;	 Catch:{ Exception -> 0x0192, all -> 0x019e }
        r4 = 1;	 Catch:{ Exception -> 0x0192, all -> 0x019e }
        r5 = "account_name";	 Catch:{ Exception -> 0x0192, all -> 0x019e }
        r3[r4] = r5;	 Catch:{ Exception -> 0x0192, all -> 0x019e }
        r4 = "account_name=?";	 Catch:{ Exception -> 0x0192, all -> 0x019e }
        r5 = 1;	 Catch:{ Exception -> 0x0192, all -> 0x019e }
        r5 = new java.lang.String[r5];	 Catch:{ Exception -> 0x0192, all -> 0x019e }
        r6 = 0;	 Catch:{ Exception -> 0x0192, all -> 0x019e }
        r5[r6] = r19;	 Catch:{ Exception -> 0x0192, all -> 0x019e }
        r6 = 0;	 Catch:{ Exception -> 0x0192, all -> 0x019e }
        r13 = r1.query(r2, r3, r4, r5, r6);	 Catch:{ Exception -> 0x0192, all -> 0x019e }
        if (r13 == 0) goto L_0x0084;	 Catch:{ Exception -> 0x0192, all -> 0x019e }
    L_0x004e:
        r1 = r13.moveToNext();	 Catch:{ Exception -> 0x0192, all -> 0x019e }
        if (r1 == 0) goto L_0x0084;	 Catch:{ Exception -> 0x0192, all -> 0x019e }
    L_0x0054:
        r1 = 0;	 Catch:{ Exception -> 0x0192, all -> 0x019e }
        r8 = r13.getLong(r1);	 Catch:{ Exception -> 0x0192, all -> 0x019e }
        r1 = "AccountUtil";	 Catch:{ Exception -> 0x0192, all -> 0x019e }
        r2 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x0192, all -> 0x019e }
        r2.<init>();	 Catch:{ Exception -> 0x0192, all -> 0x019e }
        r3 = "account table: query ";	 Catch:{ Exception -> 0x0192, all -> 0x019e }
        r2 = r2.append(r3);	 Catch:{ Exception -> 0x0192, all -> 0x019e }
        r0 = r19;	 Catch:{ Exception -> 0x0192, all -> 0x019e }
        r2 = r2.append(r0);	 Catch:{ Exception -> 0x0192, all -> 0x019e }
        r3 = ", id ";	 Catch:{ Exception -> 0x0192, all -> 0x019e }
        r2 = r2.append(r3);	 Catch:{ Exception -> 0x0192, all -> 0x019e }
        r2 = r2.append(r8);	 Catch:{ Exception -> 0x0192, all -> 0x019e }
        r2 = r2.toString();	 Catch:{ Exception -> 0x0192, all -> 0x019e }
        android.util.Log.i(r1, r2);	 Catch:{ Exception -> 0x0192, all -> 0x019e }
    L_0x007d:
        if (r13 == 0) goto L_0x0013;
    L_0x007f:
        r13.close();
        r13 = 0;
        goto L_0x0013;
    L_0x0084:
        r1 = r20.getContentResolver();	 Catch:{ Exception -> 0x0192, all -> 0x019e }
        r2 = ACCOUNT_URI;	 Catch:{ Exception -> 0x0192, all -> 0x019e }
        r3 = 1;	 Catch:{ Exception -> 0x0192, all -> 0x019e }
        r3 = new java.lang.String[r3];	 Catch:{ Exception -> 0x0192, all -> 0x019e }
        r4 = 0;	 Catch:{ Exception -> 0x0192, all -> 0x019e }
        r5 = "flyme";	 Catch:{ Exception -> 0x0192, all -> 0x019e }
        r3[r4] = r5;	 Catch:{ Exception -> 0x0192, all -> 0x019e }
        r4 = "userId=?";	 Catch:{ Exception -> 0x0192, all -> 0x019e }
        r5 = 1;	 Catch:{ Exception -> 0x0192, all -> 0x019e }
        r5 = new java.lang.String[r5];	 Catch:{ Exception -> 0x0192, all -> 0x019e }
        r6 = 0;	 Catch:{ Exception -> 0x0192, all -> 0x019e }
        r5[r6] = r19;	 Catch:{ Exception -> 0x0192, all -> 0x019e }
        r6 = 0;	 Catch:{ Exception -> 0x0192, all -> 0x019e }
        r7 = 0;	 Catch:{ Exception -> 0x0192, all -> 0x019e }
        r12 = r1.query(r2, r3, r4, r5, r6, r7);	 Catch:{ Exception -> 0x0192, all -> 0x019e }
        if (r12 == 0) goto L_0x014d;	 Catch:{ Exception -> 0x0192, all -> 0x019e }
    L_0x00a2:
        r1 = r12.moveToNext();	 Catch:{ Exception -> 0x0192, all -> 0x019e }
        if (r1 == 0) goto L_0x014d;	 Catch:{ Exception -> 0x0192, all -> 0x019e }
    L_0x00a8:
        r1 = "flyme";	 Catch:{ Exception -> 0x0192, all -> 0x019e }
        r1 = r12.getColumnIndex(r1);	 Catch:{ Exception -> 0x0192, all -> 0x019e }
        r16 = r12.getString(r1);	 Catch:{ Exception -> 0x0192, all -> 0x019e }
        if (r16 == 0) goto L_0x014d;	 Catch:{ Exception -> 0x0192, all -> 0x019e }
    L_0x00b4:
        r1 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x0192, all -> 0x019e }
        r1.<init>();	 Catch:{ Exception -> 0x0192, all -> 0x019e }
        r0 = r16;	 Catch:{ Exception -> 0x0192, all -> 0x019e }
        r1 = r1.append(r0);	 Catch:{ Exception -> 0x0192, all -> 0x019e }
        r2 = "@flyme.cn";	 Catch:{ Exception -> 0x0192, all -> 0x019e }
        r1 = r1.append(r2);	 Catch:{ Exception -> 0x0192, all -> 0x019e }
        r16 = r1.toString();	 Catch:{ Exception -> 0x0192, all -> 0x019e }
        r1 = r20.getContentResolver();	 Catch:{ Exception -> 0x0192, all -> 0x019e }
        r2 = com.meizu.flyme.notepaper.database.NotePaper.AccountConstract.CONTENT_URI;	 Catch:{ Exception -> 0x0192, all -> 0x019e }
        r3 = 2;	 Catch:{ Exception -> 0x0192, all -> 0x019e }
        r3 = new java.lang.String[r3];	 Catch:{ Exception -> 0x0192, all -> 0x019e }
        r4 = 0;	 Catch:{ Exception -> 0x0192, all -> 0x019e }
        r5 = "_id";	 Catch:{ Exception -> 0x0192, all -> 0x019e }
        r3[r4] = r5;	 Catch:{ Exception -> 0x0192, all -> 0x019e }
        r4 = 1;	 Catch:{ Exception -> 0x0192, all -> 0x019e }
        r5 = "account_name";	 Catch:{ Exception -> 0x0192, all -> 0x019e }
        r3[r4] = r5;	 Catch:{ Exception -> 0x0192, all -> 0x019e }
        r4 = "account_name=?";	 Catch:{ Exception -> 0x0192, all -> 0x019e }
        r5 = 1;	 Catch:{ Exception -> 0x0192, all -> 0x019e }
        r5 = new java.lang.String[r5];	 Catch:{ Exception -> 0x0192, all -> 0x019e }
        r6 = 0;	 Catch:{ Exception -> 0x0192, all -> 0x019e }
        r5[r6] = r16;	 Catch:{ Exception -> 0x0192, all -> 0x019e }
        r6 = 0;	 Catch:{ Exception -> 0x0192, all -> 0x019e }
        r17 = r1.query(r2, r3, r4, r5, r6);	 Catch:{ Exception -> 0x0192, all -> 0x019e }
        if (r17 == 0) goto L_0x0148;	 Catch:{ Exception -> 0x0192, all -> 0x019e }
    L_0x00eb:
        r1 = r17.moveToNext();	 Catch:{ Exception -> 0x0192, all -> 0x019e }
        if (r1 == 0) goto L_0x0148;	 Catch:{ Exception -> 0x0192, all -> 0x019e }
    L_0x00f1:
        r1 = 0;	 Catch:{ Exception -> 0x0192, all -> 0x019e }
        r0 = r17;	 Catch:{ Exception -> 0x0192, all -> 0x019e }
        r8 = r0.getLong(r1);	 Catch:{ Exception -> 0x0192, all -> 0x019e }
        r1 = "AccountUtil";	 Catch:{ Exception -> 0x0192, all -> 0x019e }
        r2 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x0192, all -> 0x019e }
        r2.<init>();	 Catch:{ Exception -> 0x0192, all -> 0x019e }
        r3 = "account table: query ";	 Catch:{ Exception -> 0x0192, all -> 0x019e }
        r2 = r2.append(r3);	 Catch:{ Exception -> 0x0192, all -> 0x019e }
        r0 = r16;	 Catch:{ Exception -> 0x0192, all -> 0x019e }
        r2 = r2.append(r0);	 Catch:{ Exception -> 0x0192, all -> 0x019e }
        r3 = ", id ";	 Catch:{ Exception -> 0x0192, all -> 0x019e }
        r2 = r2.append(r3);	 Catch:{ Exception -> 0x0192, all -> 0x019e }
        r2 = r2.append(r8);	 Catch:{ Exception -> 0x0192, all -> 0x019e }
        r2 = r2.toString();	 Catch:{ Exception -> 0x0192, all -> 0x019e }
        android.util.Log.i(r1, r2);	 Catch:{ Exception -> 0x0192, all -> 0x019e }
        r17.close();	 Catch:{ Exception -> 0x0192, all -> 0x019e }
        r17 = 0;	 Catch:{ Exception -> 0x0192, all -> 0x019e }
        r12.close();	 Catch:{ Exception -> 0x0192, all -> 0x019e }
        r12 = 0;	 Catch:{ Exception -> 0x0192, all -> 0x019e }
        r14 = new android.content.ContentValues;	 Catch:{ Exception -> 0x0192, all -> 0x019e }
        r14.<init>();	 Catch:{ Exception -> 0x0192, all -> 0x019e }
        r1 = "account_name";	 Catch:{ Exception -> 0x0192, all -> 0x019e }
        r0 = r19;	 Catch:{ Exception -> 0x0192, all -> 0x019e }
        r14.put(r1, r0);	 Catch:{ Exception -> 0x0192, all -> 0x019e }
        r1 = r20.getContentResolver();	 Catch:{ Exception -> 0x0192, all -> 0x019e }
        r2 = com.meizu.flyme.notepaper.database.NotePaper.AccountConstract.CONTENT_URI;	 Catch:{ Exception -> 0x0192, all -> 0x019e }
        r2 = android.content.ContentUris.withAppendedId(r2, r8);	 Catch:{ Exception -> 0x0192, all -> 0x019e }
        r3 = 0;	 Catch:{ Exception -> 0x0192, all -> 0x019e }
        r4 = 0;	 Catch:{ Exception -> 0x0192, all -> 0x019e }
        r1.update(r2, r14, r3, r4);	 Catch:{ Exception -> 0x0192, all -> 0x019e }
        if (r13 == 0) goto L_0x0013;
    L_0x0142:
        r13.close();
        r13 = 0;
        goto L_0x0013;
    L_0x0148:
        if (r17 == 0) goto L_0x014d;
    L_0x014a:
        r17.close();	 Catch:{ Exception -> 0x0192, all -> 0x019e }
    L_0x014d:
        r14 = new android.content.ContentValues;	 Catch:{ Exception -> 0x0192, all -> 0x019e }
        r14.<init>();	 Catch:{ Exception -> 0x0192, all -> 0x019e }
        r1 = "account_name";	 Catch:{ Exception -> 0x0192, all -> 0x019e }
        r0 = r19;	 Catch:{ Exception -> 0x0192, all -> 0x019e }
        r14.put(r1, r0);	 Catch:{ Exception -> 0x0192, all -> 0x019e }
        r1 = r20.getContentResolver();	 Catch:{ Exception -> 0x0192, all -> 0x019e }
        r2 = com.meizu.flyme.notepaper.database.NotePaper.AccountConstract.CONTENT_URI;	 Catch:{ Exception -> 0x0192, all -> 0x019e }
        r18 = r1.insert(r2, r14);	 Catch:{ Exception -> 0x0192, all -> 0x019e }
        r8 = android.content.ContentUris.parseId(r18);	 Catch:{ Exception -> 0x0192, all -> 0x019e }
        r1 = "AccountUtil";	 Catch:{ Exception -> 0x0192, all -> 0x019e }
        r2 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x0192, all -> 0x019e }
        r2.<init>();	 Catch:{ Exception -> 0x0192, all -> 0x019e }
        r3 = "account table: insert ";	 Catch:{ Exception -> 0x0192, all -> 0x019e }
        r2 = r2.append(r3);	 Catch:{ Exception -> 0x0192, all -> 0x019e }
        r0 = r19;	 Catch:{ Exception -> 0x0192, all -> 0x019e }
        r2 = r2.append(r0);	 Catch:{ Exception -> 0x0192, all -> 0x019e }
        r3 = ", id ";	 Catch:{ Exception -> 0x0192, all -> 0x019e }
        r2 = r2.append(r3);	 Catch:{ Exception -> 0x0192, all -> 0x019e }
        r2 = r2.append(r8);	 Catch:{ Exception -> 0x0192, all -> 0x019e }
        r2 = r2.toString();	 Catch:{ Exception -> 0x0192, all -> 0x019e }
        android.util.Log.i(r1, r2);	 Catch:{ Exception -> 0x0192, all -> 0x019e }
        if (r12 == 0) goto L_0x007d;	 Catch:{ Exception -> 0x0192, all -> 0x019e }
    L_0x018d:
        r12.close();	 Catch:{ Exception -> 0x0192, all -> 0x019e }
        goto L_0x007d;
    L_0x0192:
        r15 = move-exception;
        r15.printStackTrace();	 Catch:{ Exception -> 0x0192, all -> 0x019e }
        if (r13 == 0) goto L_0x0013;
    L_0x0198:
        r13.close();
        r13 = 0;
        goto L_0x0013;
    L_0x019e:
        r1 = move-exception;
        if (r13 == 0) goto L_0x01a5;
    L_0x01a1:
        r13.close();
        r13 = 0;
    L_0x01a5:
        throw r1;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.meizu.flyme.notepaper.utils.AccountUtil.getMeizuAccountID(android.content.Context):long");
    }
}

package com.meizu.flyme.notepaper.utils;

import android.content.Context;
import android.util.Log;
import com.meizu.stats.MobEventAgent;
import com.meizu.stats.MobEventAgent.Callbacks;

public class MobEventUtil {
    static final int MOB_EVENT_LEVEL = 10;
    static String TAG = "MobEventUtil";
    static boolean USAGE_STATE = false;
    static boolean debug = true;
    static long mMobEventSessionID = -1;

    public static void createMobEventSession(Context context) {
        if (!USAGE_STATE) {
            MobEventAgent eventAgent = MobEventAgent.getInstance(context.getApplicationContext());
            if (debug) {
                eventAgent.setDebug(true);
            }
            if (mMobEventSessionID != -1) {
                eventAgent.endSession(mMobEventSessionID);
                mMobEventSessionID = -1;
            }
            eventAgent.startSession("notepaper", new Callbacks() {
                public void onResult(long sessionId) {
                    MobEventUtil.mMobEventSessionID = sessionId;
                    if (MobEventUtil.debug) {
                        Log.d(MobEventUtil.TAG, "start session: " + MobEventUtil.mMobEventSessionID);
                    }
                }
            });
        }
    }

    public static void createMobEventSessionIfNotExist(Context context) {
        if (!USAGE_STATE) {
            MobEventAgent eventAgent = MobEventAgent.getInstance(context.getApplicationContext());
            if (mMobEventSessionID == -1) {
                if (debug) {
                    eventAgent.setDebug(true);
                }
                eventAgent.startSession("notepaper", new Callbacks() {
                    public void onResult(long sessionId) {
                        MobEventUtil.mMobEventSessionID = sessionId;
                        if (MobEventUtil.debug) {
                            Log.d(MobEventUtil.TAG, "start session: " + MobEventUtil.mMobEventSessionID);
                        }
                    }
                });
            }
        }
    }

    public static void onSendMobEvent(Context context, final String eventName, final String value) {
        if (!USAGE_STATE) {
            final MobEventAgent eventAgent = MobEventAgent.getInstance(context.getApplicationContext());
            if (mMobEventSessionID == -1) {
                if (debug) {
                    eventAgent.setDebug(true);
                }
                eventAgent.startSession("notepaper", new Callbacks() {
                    public void onResult(long sessionId) {
                        MobEventUtil.mMobEventSessionID = sessionId;
                        if (MobEventUtil.debug) {
                            Log.d(MobEventUtil.TAG, "start session: " + MobEventUtil.mMobEventSessionID);
                        }
                        if (MobEventUtil.mMobEventSessionID != -1) {
                            if (value == null) {
                                eventAgent.onAction(MobEventUtil.MOB_EVENT_LEVEL, MobEventUtil.mMobEventSessionID, eventName);
                            } else {
                                eventAgent.onAction((int) MobEventUtil.MOB_EVENT_LEVEL, MobEventUtil.mMobEventSessionID, eventName, value);
                            }
                            if (MobEventUtil.debug) {
                                Log.d(MobEventUtil.TAG, "mob event: " + eventName + "---" + value);
                            }
                        }
                    }
                });
                return;
            }
            if (value == null) {
                eventAgent.onAction(MOB_EVENT_LEVEL, mMobEventSessionID, eventName);
            } else {
                eventAgent.onAction((int) MOB_EVENT_LEVEL, mMobEventSessionID, eventName, value);
            }
            if (debug) {
                Log.d(TAG, "mob event: " + eventName + "---" + value);
            }
        }
    }

    public static void onMobPage(Context context, String pageName, long launch, long exit) {
        if (!USAGE_STATE) {
            final MobEventAgent eventAgent = MobEventAgent.getInstance(context.getApplicationContext());
            if (mMobEventSessionID == -1) {
                if (debug) {
                    eventAgent.setDebug(true);
                }
                final String str = pageName;
                final long j = launch;
                final long j2 = exit;
                eventAgent.startSession("notepaper", new Callbacks() {
                    public void onResult(long sessionId) {
                        MobEventUtil.mMobEventSessionID = sessionId;
                        if (MobEventUtil.debug) {
                            Log.d(MobEventUtil.TAG, "start session: " + MobEventUtil.mMobEventSessionID);
                        }
                        if (MobEventUtil.mMobEventSessionID != -1) {
                            eventAgent.onPage(MobEventUtil.MOB_EVENT_LEVEL, MobEventUtil.mMobEventSessionID, str, j, j2);
                            if (MobEventUtil.debug) {
                                Log.d(MobEventUtil.TAG, "mob page: " + str + "--launch:" + j + "--exit:" + j2);
                            }
                        }
                    }
                });
                return;
            }
            eventAgent.onPage(MOB_EVENT_LEVEL, mMobEventSessionID, pageName, launch, exit);
            if (debug) {
                Log.d(TAG, "mob page: " + pageName + "--launch:" + launch + "--exit:" + exit);
            }
        }
    }

    public static void endMobEventSession(Context context) {
        if (!USAGE_STATE && mMobEventSessionID != -1) {
            MobEventAgent eventAgent = MobEventAgent.getInstance(context.getApplicationContext());
            if (debug) {
                Log.d(TAG, "end session: " + mMobEventSessionID);
            }
            eventAgent.endSession(mMobEventSessionID);
            mMobEventSessionID = -1;
        }
    }
}

package com.joeyhe.passwordmanager.utils;

import android.annotation.TargetApi;
import android.app.ActivityManager;
import android.app.AppOpsManager;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Build;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by HGY on 2017/7/20.
 */

//Reference: https://github.com/wenmingvs/AndroidProcess

public class ProcessDetectUtil {

    public static String getCurrentProcess(Context context) {
        class RecentUseComparator implements Comparator<UsageStats> {
            @TargetApi(Build.VERSION_CODES.LOLLIPOP)
            @Override
            public int compare(UsageStats lhs, UsageStats rhs) {
                return (lhs.getLastTimeUsed() > rhs.getLastTimeUsed())
                        ? -1 : (lhs.getLastTimeUsed() == rhs.getLastTimeUsed()) ? 0 : 1;
            }
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (havePermissionForTest(context)) {
                RecentUseComparator mRecentComp = new RecentUseComparator();
                long ts = System.currentTimeMillis();
                UsageStatsManager mUsageStatsManager = (UsageStatsManager) context
                        .getSystemService(Context.USAGE_STATS_SERVICE);
                List<UsageStats> usageStats = mUsageStatsManager
                        .queryUsageStats(UsageStatsManager.INTERVAL_BEST, ts - 1000 * 3600 * 12, ts);
                if (usageStats == null || usageStats.size() == 0) {
                    return null;
                }
                Collections.sort(usageStats, mRecentComp);
                return usageStats.get(0).getPackageName();
            }
        } else {
            ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
            ComponentName cn = am.getRunningTasks(1).get(0).topActivity;
            return cn.getPackageName();
        }
        return null;
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private static boolean havePermissionForTest(Context context) {
        try {
            PackageManager packageManager = context.getPackageManager();
            ApplicationInfo applicationInfo = packageManager.getApplicationInfo(context.getPackageName(), 0);
            AppOpsManager appOpsManager = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
            int mode = appOpsManager.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS, applicationInfo.uid, applicationInfo.packageName);
            return (mode == AppOpsManager.MODE_ALLOWED);
        } catch (PackageManager.NameNotFoundException e) {
            return true;
        }
    }
}

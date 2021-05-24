package com.kiran.aidlrotationclient.utils;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;

import java.util.List;

public class Utils {

    public static Intent implicitIntentToExplicitIntent(Intent implicitIntent, Context context){

        PackageManager packageManager = context.getPackageManager();
        List<ResolveInfo> resolveInfoList = packageManager.queryIntentServices(implicitIntent, 0);
        if (resolveInfoList == null || resolveInfoList.size() != 1){
            return null;
        }
        ResolveInfo serviceInfo =resolveInfoList.get(0);
        ComponentName componentName = new ComponentName(serviceInfo.serviceInfo.packageName,
                serviceInfo.serviceInfo.name);
        Intent explicitIntent = new Intent(implicitIntent);
        explicitIntent.setComponent(componentName);
        return  explicitIntent;
    }
}

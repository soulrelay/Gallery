
package com.soulrelay.gallery.utils;

import android.content.Context;
import android.view.Gravity;
import android.widget.Toast;

/**
 * Author:    SuS
 * Version    V1.0
 * Date:      17/2/14
 * Description:  Toast工具类
 * Modification  History:
 * Date         	Author        		Version        	Description
 * -----------------------------------------------------------------------------------
 * 17/2/14          SuS                 1.0               1.0
 * Why & What is modified:
 */
public class ToastUtils {
    private static boolean is_Debug = false;

    private static Toast toast;

    public static void setLogEnable(boolean printLog) {
        is_Debug = printLog;
    }

    public static void toast(Context context, CharSequence text) {

//        View message = LayoutInflater.from(context).inflate(R.layout.bf_toast_layout, null);
//
//        TextView textView = (TextView) message.findViewById(R.id.bf_toast_message_text);
//        textView.setText(text);
        if (toast == null) {
            toast = Toast.makeText(context.getApplicationContext(), "", Toast.LENGTH_SHORT);
        }
//        toast.setGravity(Gravity.CENTER, 0, 0);
//        toast.setView(message);
        toast.setText(text);
        toast.show();

    }

    public static void toast(Context context, int resId) {
        ToastUtils.toast(context, context.getString(resId));
    }

    public static void toastDebug(Context context, int resId) {
        if (!is_Debug) {
            return;
        }
        ToastUtils.toast(context, context.getString(resId));
    }

    /**
     * 调试信息提示
     *
     * @param context
     * @param text
     */
    public static void toastDebug(Context context, CharSequence text) {
        if (!is_Debug) {
            return;
        }
        try {
            Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void toastCenter(Context context, int resId) {
        Toast toast = Toast.makeText(context, resId, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, 0, 0);// 设置Toast显示位置居中
        toast.show();
    }

    public static void toastLong(Context context, CharSequence text) {
        Toast.makeText(context, text, Toast.LENGTH_LONG).show();
    }

    public static void toastLong(Context context, int resId) {
        Toast.makeText(context, resId, Toast.LENGTH_LONG).show();
    }
}

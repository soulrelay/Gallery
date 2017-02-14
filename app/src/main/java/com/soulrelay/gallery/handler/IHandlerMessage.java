package com.soulrelay.gallery.handler;

import android.os.Message;

/**
 * Author:    SuS
 * Version    V1.0
 * Date:      17/2/14
 * Description:  Handler的消息回调
 * Modification  History:
 * Date         	Author        		Version        	Description
 * -----------------------------------------------------------------------------------
 * 17/2/14          SuS                 1.0               1.0
 * Why & What is modified:
 */
public interface IHandlerMessage {
    void handlerCallback(Message msg);
}

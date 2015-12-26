/** 
 *  
 * @author	xuxl
 * @email	leoxuxl@163.com
 * @version  
 *     1.0 2015年12月24日 下午2:47:35 
 */ 
package com.smartdevicesdk.ui;

import android.content.Context;
import android.content.Intent;

/** 
 * This class is used for : System control
 *  
 * @author	xuxl
 * @email	leoxuxl@163.com
 * @version  
 *     1.0 2015年12月24日 下午2:47:35 
 */
public class SystemControl {
	/**
	 * 禁用通知栏下拉<br/>
	 * disable notification bar down
	 */
	public static void disableNotificationBar(Context context){
		Intent intent = new Intent("android.intent.action.CLOSE_SYSTEM_DIALOGS");
		intent.putExtra("reason", "globalactions");
		context.sendBroadcast(intent);
	}
}

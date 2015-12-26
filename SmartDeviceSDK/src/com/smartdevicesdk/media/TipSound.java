/** 
 *  
 * @author	xuxl
 * @email	leoxuxl@163.com
 * @version  
 *     1.0 2015年12月24日 下午2:23:26 
 */ 
package com.smartdevicesdk.media;

import com.smartdevicesdk.sdk.R;

import android.content.Context;
import android.media.MediaPlayer;

/** 
 * This class is used for : 
 *  
 * @author	xuxl
 * @email	leoxuxl@163.com
 * @version  
 *     1.0 2015年12月24日 下午2:23:26 
 */
public class TipSound {
	private static void play(Context context,int rid){
		MediaPlayer mPlayer = MediaPlayer.create(context,rid);  
		mPlayer.setLooping(false);  
		mPlayer.start();
	}
	
	/**
	 * 播放扫描声音<br/>
	 * play scan sound
	 * @param context
	 */
	public static void playScanSound(Context context){
		play(context,R.raw.scan);
	}
}

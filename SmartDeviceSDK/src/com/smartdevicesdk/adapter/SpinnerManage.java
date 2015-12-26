/** 
 *  
 * @author	xuxl
 * @email	leoxuxl@163.com
 * @version  
 *     1.0 2015年12月23日 下午3:31:39 
 */ 
package com.smartdevicesdk.adapter;

import android.widget.Spinner;

/** 
 * This class is used for : 
 *  
 * @author	xuxl
 * @email	leoxuxl@163.com
 * @version  
 *     1.0 2015年12月23日 下午3:31:39 
 */
public class SpinnerManage {
	/**
	 * 设置Spinner默认值<br/>
	 * Set Spinner Default Value
	 * @param spinner
	 * @param text
	 */
	public static void setDefaultItem(Spinner spinner,Object text){
		for(int i=0;i<spinner.getCount();i++){
			if(spinner.getItemAtPosition(i).equals(text+""))
			{
				spinner.setSelection(i,true);
				break;
			}
		}
	}
}

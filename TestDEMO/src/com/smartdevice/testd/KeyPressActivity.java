/** 
 *  
 * @author	xuxl
 * @email	leoxuxl@163.com
 * @version  
 *     1.0 2015年12月25日 下午3:16:27 
 */ 
package com.smartdevice.testd;

import android.app.Activity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.EditText;
import android.widget.TextView;

/** 
 * @author	xuxl
 * @email	leoxuxl@163.com
 * @version  
 *     1.0 2015年12月25日 下午3:16:27 
 */
public class KeyPressActivity extends Activity {
	TextView textView_keypress;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_keypress);
		
		textView_keypress=(TextView)findViewById(R.id.textView_keypress);
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		textView_keypress.setText("KEY:"+keyCode);
		return super.onKeyDown(keyCode, event);
	}
}

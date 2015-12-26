/** 
 *  
 * @author	xuxl
 * @email	leoxuxl@163.com
 * @version  
 *     1.0 2015年12月23日 下午5:50:17 
 */ 
package com.smartdevice.testd;

import com.smartdevicesdk.scanner.ScannerHelper;
import com.smartdevicesdk.utils.HandlerMessage;

import android.app.Activity;
import android.inputmethodservice.Keyboard.Key;
import android.os.Bundle;
import android.os.Handler;
import android.security.KeyChain;
import android.util.Log;
import android.view.KeyCharacterMap.KeyData;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

/** 
 * This class is used for : 
 *  
 * @author	xuxl
 * @email	leoxuxl@163.com
 * @version  
 *     1.0 2015年12月23日 下午5:50:17 
 */
public class ScannerActivity extends Activity {
	private static final String TAG = "ScannerActivity";
	ScannerHelper scanner=null;
	Button btnScan;
	TextView textView;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_scanner);
		
		btnScan=(Button)findViewById(R.id.btnScan);
		btnScan.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				textView.setText("please on the bar code");
				scanner.scan();
			}
		});
		
		textView=(TextView)findViewById(R.id.textView_scan);
		
		Handler handler=new Handler(){
			public void handleMessage(android.os.Message msg) {
				if(msg.what==HandlerMessage.SCANNER_DATA_MSG)
				{
					textView.setText(msg.obj.toString());
				}
			};
		};

		String device=MainActivity.devInfo.getScannerSerialport();
		int baudrate=MainActivity.devInfo.getScannerBaudrate();
		scanner=new ScannerHelper(this,device, baudrate, handler);
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		Log.i(TAG, "key press code is "+keyCode);
		if(keyCode== 135){
			textView.setText("please on the bar code");
			scanner.scan();
		}
		return super.onKeyDown(keyCode, event);
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		scanner.Close();
	}
}

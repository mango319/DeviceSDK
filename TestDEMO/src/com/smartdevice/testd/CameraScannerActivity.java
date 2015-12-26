/** 
 *  
 * @author	xuxl
 * @email	leoxuxl@163.com
 * @version  
 *     1.0 2015年12月24日 下午6:12:50 
 */
package com.smartdevice.testd;

import com.smartdevicesdk.camerascanner.ZBarConstants;
import com.smartdevicesdk.camerascanner.ZBarScannerActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

/**
 * This class is used for :
 * 
 * @author xuxl
 * @email leoxuxl@163.com
 * @version 1.0 2015年12月24日 下午6:12:50
 */
public class CameraScannerActivity extends Activity {
	private static final int ZBAR_SCANNER_REQUEST = 0;
	private static final int ZBAR_QR_SCANNER_REQUEST = 1;
	TextView textView;
	Button buttonScan;
	Intent intent;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_camerascanner);


		intent = new Intent(this, ZBarScannerActivity.class);
		// intent.putExtra(ZBarConstants.SCAN_MODES, new int[]{Symbol.QRCODE,
		// Symbol.ISBN10, Symbol.ISBN13});
		
		textView = (TextView) findViewById(R.id.textView1);
		buttonScan=(Button)findViewById(R.id.button1);
		buttonScan.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivityForResult(intent, ZBAR_SCANNER_REQUEST);
			}
		});

		startActivityForResult(intent, ZBAR_SCANNER_REQUEST);
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(keyCode==135){
			startActivityForResult(intent, ZBAR_SCANNER_REQUEST);
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK) {
			// Scan result is available by making a call to
			// data.getStringExtra(ZBarConstants.SCAN_RESULT)
			// Type of the scan result is available by making a call to
			// data.getStringExtra(ZBarConstants.SCAN_RESULT_TYPE)
			String str = "Scan Result:\r\n"
					+ data.getStringExtra(ZBarConstants.SCAN_RESULT);
			str += "\r\nScan Result Type:\r\n"
					+ data.getIntExtra(ZBarConstants.SCAN_RESULT_TYPE, 0);
			// The value of type indicates one of the symbols listed in Advanced
			// Options below.

			textView.setText(str);
			
		} else if (resultCode == RESULT_CANCELED) {
			textView.setText("Camera unavailable");
		}
	}
}

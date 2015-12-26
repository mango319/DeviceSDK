package com.smartdevice.testd;

import com.smartdevicesdk.psam.PSAMhelper;
import com.zkc.i2ctools.i2c;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.Switch;
import android.widget.ToggleButton;

public class PSAMActivity extends Activity {

	protected static final String TAG = "MainActivity";
	Button btn_init, btn_random, button_send,button_power_on,button_power_off;
	EditText editText1, editText_cmd;
	RadioGroup radioGroupCard;

	int cardLocation = 2;

	int[] fd = new int[1];

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_psam);

		editText1 = (EditText) findViewById(R.id.editText1);
		editText_cmd = (EditText) findViewById(R.id.editText_cmd);

		
		button_power_on=(Button)findViewById(R.id.button_power_on);
		button_power_on.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) {
				int s = PSAMhelper.OpenCard(fd, cardLocation);
				Log.i(TAG, "psam fd=" + fd[0]);
				if (s != -1) {
					Log.i(TAG, "open success!");
				}
			}
		});
		
		button_power_off=(Button)findViewById(R.id.button_power_off);
		button_power_off.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				int s = PSAMhelper.CloseCard(fd[0]);
				fd[0] = 0;
				if (s != -1) {
					Log.i(TAG, "close success!");
				}
			}
		});

		btn_init = (Button) findViewById(R.id.button_init);
		btn_init.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (fd[0] > 0) {
					byte[] btRandom = new byte[] {};
					editText_cmd.setText(bytes2HexString(btRandom).toString());
					byte[] dataBuf = new byte[256];
					int[] dataBufLen = new int[1];
					// PSAMhelper.CardApdu(fd[0], btRandom,
					// btRandom.length,dataBuf, dataBufLen);
					PSAMhelper.ResetCard(fd[0], dataBuf, dataBufLen);
					if (dataBuf != null) {
						editText1.setText(bytes2HexString(dataBuf,
								dataBufLen[0]).toString());
					}
				}
			}
		});

		btn_random = (Button) findViewById(R.id.btn_random);
		btn_random.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (fd[0] > 0) {
					byte[] btRandom = new byte[] { (byte) 0x00, (byte) 0x84,
							(byte) 0x00, (byte) 0x00, (byte) 0x08 };
					editText_cmd.setText(bytes2HexString(btRandom).toString());
					byte[] dataBuf = new byte[256];
					int[] dataBufLen = new int[1];
					PSAMhelper.CardApdu(fd[0], btRandom, btRandom.length,
							dataBuf, dataBufLen);
					if (dataBuf != null) {
						editText1.setText(bytes2HexString(dataBuf,
								dataBufLen[0]).toString());
					}
				}
			}
		});

		button_send = (Button) findViewById(R.id.button_send);
		button_send.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (fd[0] > 0) {
					String hexString = editText_cmd.getText().toString();
					byte[] bt = hexString2Bytes(hexString);
					byte[] dataBuf = new byte[256];
					int[] dataBufLen = new int[1];
					PSAMhelper.CardApdu(fd[0], bt, bt.length, dataBuf,
							dataBufLen);
					if (dataBuf != null) {
						editText1.setText(bytes2HexString(dataBuf,
								dataBufLen[0]).toString());
					}
				}
			}
		});

		radioGroupCard = (RadioGroup) findViewById(R.id.radioGroupCard);
		radioGroupCard
				.setOnCheckedChangeListener(new OnCheckedChangeListener() {
					@Override
					public void onCheckedChanged(RadioGroup group, int checkedId) {
						RadioButton radiobutton = (RadioButton) findViewById(group
								.getCheckedRadioButtonId());
						cardLocation = 16 * Integer.parseInt(radiobutton
								.getTag().toString());
					}
				});
	}

	@Override
	protected void onDestroy() {
		PSAMhelper.CloseCard(fd[0]);
		super.onDestroy();
	}

	/**
	 * @Title:bytes2HexString
	 * @Description:字节数组转16进制字符串
	 * @param b
	 *            字节数组
	 * @return 16进制字符串
	 * @throws
	 */
	public static String bytes2HexString(byte[] b) {
		StringBuffer result = new StringBuffer();
		String hex;
		for (int i = 0; i < b.length; i++) {
			hex = Integer.toHexString(b[i] & 0xFF);
			if (hex.length() == 1) {
				hex = '0' + hex;
			}
			result.append(hex.toUpperCase());
		}
		return result.toString();
	}

	public static String bytes2HexString(byte[] b, int len) {
		StringBuffer result = new StringBuffer();
		String hex;
		for (int i = 0; i < len; i++) {
			hex = Integer.toHexString(b[i] & 0xFF);
			if (hex.length() == 1) {
				hex = '0' + hex;
			}
			result.append(hex.toUpperCase());
		}
		return result.toString();
	}

	/**
	 * @Title:hexString2Bytes
	 * @Description:16进制字符串转字节数组
	 * @param src
	 *            16进制字符串
	 * @return 字节数组
	 * @throws
	 */
	public static byte[] hexString2Bytes(String src) {
		int l = src.length() / 2;
		byte[] ret = new byte[l];
		for (int i = 0; i < l; i++) {
			ret[i] = (byte) Integer
					.valueOf(src.substring(i * 2, i * 2 + 2), 16).byteValue();
		}
		return ret;
	}

}

package com.smartdevice.testd;

import java.nio.charset.Charset;

import com.smartdevicesdk.stripcard.Stripcardhelper;
import com.zkc.i2ctools.i2c;

import android.app.Activity;
import android.app.ActionBar;
import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.os.Build;

public class MagneticCardActivity extends Activity {
	private static i2c i2c;
	private static int i2cHander;
	private static final String i2cDev[] = { "i2c-0", "i2c-1", "i2c-2", "i2c-3" };
	protected static final String TAG = "MainActivity";
	private static int slaveAddr = 0x38;

	Button btn_check, btn_readdata;
	EditText editText1;
	
	int cardLocation=0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_magneticcard);

		editText1 = (EditText) findViewById(R.id.editText1);
		
		btn_check = (Button) findViewById(R.id.btn_check);
		btn_check.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {				
				byte[] buffer=Stripcardhelper.ReadCard();
				byte[] track1=new byte[100];
				byte[] track2=new byte[140];
				byte[] track3=new byte[140];
				
				System.arraycopy(buffer, 0, track1, 0, track1.length);
				System.arraycopy(buffer, 100, track2, 0, track1.length);
				System.arraycopy(buffer, 240, track3, 0, track1.length);
				
				String str="";
				str="Track1:"+new String(track1,0,track1.length);
				str+="\r\n"+"Track2:"+new String(track2,0,track2.length);
				str+="\r\n"+"Track3:"+new String(track3,0,track3.length);
				
				editText1.setText(str);
			}
		});
		
		i2c = new i2c();
	}

	public static String printHexString(int[] b, int size) {
		String result = "";
		for (int i = 0; i < size; i++) {
			String hex = Integer.toHexString(b[i] & 0xFF);
			if (hex.length() == 1) {
				hex = '0' + hex;
			}
			result = result + hex.toUpperCase();
		}
		return result;
	}

	public int[] getData() {
		int[] bufReadData = ReadCommand(new int[] { 0x04 }, 380);
		return bufReadData;
	}

	private int[] ReadCommand(int[] bufReadReg, int readlen) {
		int[] bufReadData = new int[readlen];
		i2cHander = i2c.open("/dev/" + i2cDev[1]);
		i2c.write(i2cHander, slaveAddr, bufReadReg[0], bufReadReg,
				bufReadReg.length);
		i2c.read(i2cHander, slaveAddr, bufReadData, bufReadData.length);
		i2c.close(i2cHander);
		return bufReadData;
	}

	/**
	 * ָ���
	 * 
	 * @param bufWriteReg
	 * @param bufWriteData
	 */
	private void WriteCommand(int[] bufWriteReg, int[] bufWriteData) {
		int[] bufData = new int[bufWriteData.length + bufWriteReg.length];
		for (int j = 0; j < bufWriteReg.length; j++) {
			bufData[j] = bufWriteReg[j];
		}

		System.arraycopy(bufWriteData, 0, bufData, bufWriteReg.length,
				bufWriteData.length);
		i2cHander = i2c.open("/dev/" + i2cDev[1]);
		i2c.write(i2cHander, slaveAddr, bufWriteReg[0], bufData, bufData.length);
		i2c.close(i2cHander);
	}
}

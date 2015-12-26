package com.smartdevice.testd;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.InvalidParameterException;

import com.smartdevicesdk.idcard.IDCard;
import com.smartdevicesdk.idcard.IDCardHelper;
import com.synjones.bluetooth.DecodeWlt;

import android.app.Activity;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

public class IDCardActivity extends Activity {
	protected IDCardHelper idcardHelper;
	private IDCard idcard = null;
	TextView tvMessage;
	Spinner spinner_Serialport;
	String wltPath, bmpPath;

	ReadIDCard readIDCard = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_idcard);

		String companyFolder = Environment.getExternalStorageDirectory()
				.getPath() + "/ZKC/";// 配置文件文件夹
		File config = new File(companyFolder);
		if (!config.exists()) {
			config.mkdirs();
		}

		wltPath = companyFolder + "kk.jpg";
		bmpPath = companyFolder + "kk1.jpg";

		spinner_Serialport = (Spinner) findViewById(R.id.spinner_Serialport);
		spinner_Serialport
				.setOnItemSelectedListener(new OnItemSelectedListener() {
					@Override
					public void onItemSelected(AdapterView<?> parent,
							View view, int position, long id) {
						String device = spinner_Serialport.getItemAtPosition(
								position).toString();
						int baudrate = MainActivity.devInfo.getIdCardBaudrate();
						initIDCard(device, baudrate);
					}

					@Override
					public void onNothingSelected(AdapterView<?> parent) {

					}

				});

		String device = MainActivity.devInfo.getIdCardSerialport();
		int baudrate = MainActivity.devInfo.getIdCardBaudrate();
		initIDCard(device, baudrate);
	}

	Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (msg.what == 1) {
				readCard();
			}
			super.handleMessage(msg);
		}
	};

	private void initIDCard(String device, int baudrate) {
		idcardHelper = new IDCardHelper(device, baudrate);
		if (readIDCard == null || !readIDCard.isAlive()) {
			readIDCard = null;
			readIDCard = new ReadIDCard();
			readIDCard.start();
		}
	}

	class ReadIDCard extends Thread {
		@Override
		public void run() {
			super.run();
			while (true) {
				Message msg = mHandler.obtainMessage();
				msg.what = 1;
				msg.sendToTarget();
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}

	@Override
	protected void onDestroy() {
		if (readIDCard != null) {
			readIDCard.interrupt();
			readIDCard = null;
		}
		if (idcardHelper != null) {
			idcardHelper.close();
		}
		super.onDestroy();
	}

	private void readCard() {
		if (idcardHelper == null) {
			return;
		}
		TextView tv;
		ImageView imageViewPhoto = (ImageView) findViewById(R.id.imageViewPhoto);
		idcard = idcardHelper.getIDCard();
		if (idcard != null) {
			tv = (TextView) findViewById(R.id.textViewName);
			tv.setText(getString(R.string.sdtname) + idcard.getName());
			tv = (TextView) findViewById(R.id.textViewSex);
			tv.setText(getString(R.string.sdtsex) + idcard.getSex());
			tv = (TextView) findViewById(R.id.textViewNation);
			tv.setText(getString(R.string.sdtnation) + idcard.getNation());
			tv = (TextView) findViewById(R.id.textViewBirthday);
			tv.setText(getString(R.string.sdtbirthday)
					+ idcard.getBirthday().substring(0, 4) + "年"
					+ idcard.getBirthday().substring(4, 6) + "月"
					+ idcard.getBirthday().substring(6, 8) + "日");
			tv = (TextView) findViewById(R.id.textViewAddress);
			tv.setText(getString(R.string.sdtaddress) + idcard.getAddress());
			tv = (TextView) findViewById(R.id.textViewPIDNo);
			tv.setText(getString(R.string.sdtpidno) + idcard.getIDCardNo());
			tv = (TextView) findViewById(R.id.textViewGrantDept);
			tv.setText(getString(R.string.sdtgrantdept) + idcard.getGrantDept());
			tv = (TextView) findViewById(R.id.textViewUserLife);
			tv.setText(getString(R.string.sdtuserlife)
					+ idcard.getUserLifeBegin() + "-" + idcard.getUserLifeEnd());
			tv = (TextView) findViewById(R.id.textViewStatus);
			tv.setText(getString(R.string.sdtstatus));
			if (idcard.getWlt() == null) {
				return;
			}
			try {
				File wltFile = new File(wltPath);
				FileOutputStream fos = new FileOutputStream(wltFile);
				fos.write(idcard.getWlt());
				fos.close();

				int result = idcardHelper.decodeImage(wltPath, bmpPath);

				if (result == 1) {
					File f = new File(bmpPath);
					if (f.exists())
						imageViewPhoto.setImageBitmap(BitmapFactory
								.decodeFile(bmpPath));
					else
						imageViewPhoto.setImageResource(R.drawable.photo);
				} else {
					imageViewPhoto.setImageResource(R.drawable.photo);
				}
			} catch (IOException ioe) {
				ioe.printStackTrace();
			}
		} else {
			tv = (TextView) findViewById(R.id.textViewName);
			tv.setText(getString(R.string.sdtname));
			tv = (TextView) findViewById(R.id.textViewSex);
			tv.setText(getString(R.string.sdtsex));
			tv = (TextView) findViewById(R.id.textViewNation);
			tv.setText(getString(R.string.sdtnation));
			tv = (TextView) findViewById(R.id.textViewBirthday);
			tv.setText(getString(R.string.sdtbirthday));
			tv = (TextView) findViewById(R.id.textViewAddress);
			tv.setText(getString(R.string.sdtaddress));
			tv = (TextView) findViewById(R.id.textViewPIDNo);
			tv.setText(getString(R.string.sdtpidno));
			tv = (TextView) findViewById(R.id.textViewGrantDept);
			tv.setText(getString(R.string.sdtgrantdept));
			tv = (TextView) findViewById(R.id.textViewUserLife);
			tv.setText(getString(R.string.sdtuserlife));
			tv = (TextView) findViewById(R.id.textViewStatus);
			tv.setText(getString(R.string.sdtstatus) + " "
					+ Integer.toHexString(IDCard.SW1) + " "
					+ Integer.toHexString(IDCard.SW2) + " "
					+ Integer.toHexString(IDCard.SW3));
			imageViewPhoto.setImageDrawable(getResources().getDrawable(
					R.drawable.photo));
		}
	}

}
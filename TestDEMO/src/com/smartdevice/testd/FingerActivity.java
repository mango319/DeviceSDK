package com.smartdevice.testd;

import android.app.Activity;
import android.os.Bundle;
import android.os.SystemClock;
import android.preference.PreferenceActivity;
import android.serialport.api.SerialPortParam;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.util.Arrays;

import com.smartdevicesdk.fingerprint.DevComm;
import com.smartdevicesdk.fingerprint.IUsbConnState;


public class FingerActivity extends Activity implements OnClickListener {
	Spinner spinner_name;
	// final int IMAGE_WIDTH = 152;
	// final int IMAGE_HEIGHT = 200;
	final String TEMPLATE_PATH = "sdcard/template.bin";

	/** Called when the activity is first created. */
	private static DevComm m_usbComm;

	int m_nUserID, m_nParam, m_nImgWidth, m_nImgHeight, m_nMaxFpCount;
	long m_nPassedTime;
	byte[] m_binImage, m_bmpImage;
	String m_strPost;
	boolean m_bCancel, m_bConCapture;

	Button m_btnOpenDevice;
	Button m_btnCloseDevice;
	Button m_btnEnroll;
	Button m_btnVerify;
	Button m_btnIdentify;
	Button m_btnCaptureImage;
	Button m_btnCancel;
	Button m_btnGetUserCount;
	Button m_btnGetEmptyID;
	Button m_btnDeleteID;
	Button m_btnDeleteAll;
	EditText m_editUserID;
	EditText m_editParam;
	EditText m_editIDNote;
	EditText m_editModuleSN;
	TextView m_txtStatus;
	ImageView m_FpImageViewer;
	Spinner m_spFpCount;

	String[] entryValues = new String[] { "/dev/ttyUSB1", "/dev/ttyUSB2",
			"/dev/ttyMT0", "/dev/ttySAC0", "/dev/ttySAC1", "/dev/ttySAC2",
			"/dev/ttySAC3" };

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_fingermain);

		m_nMaxFpCount = 2000;

		InitWidget();

		SetInitialState();

		spinner_name.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				SerialPortParam.Path = (String) spinner_name
						.getItemAtPosition(position);
				SerialPortParam.Path = entryValues[position];
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
			}
		});

		m_spFpCount.setOnItemSelectedListener(new OnItemSelectedListener() {
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				if (position == 0)
					m_nMaxFpCount = 1700;
				else if (position == 1)
					m_nMaxFpCount = 2000;
				else
					// if (position == 2)
					m_nMaxFpCount = 3000;
				// Toast.makeText(MainActivity.this,
				// m_spMatchLevel.getItemAtPosition(position).toString() +
				// ";�� ���� �߽4ϴ�.", 1).show();
				// Toast.makeText(MainActivity.this, "Match Level" +
				// m_nMatchLevel, 1).show();
			}

			public void onNothingSelected(AdapterView<?> parent) {
			}
		});
	}

	/*
	 * @Override public boolean onCreateOptionsMenu(Menu menu) { // Inflate the
	 * menu; this adds items to the action bar if it is present.
	 * getMenuInflater().inflate(R.menu.activity_main, menu); return true; }
	 */

	@Override
	public void onClick(View view) {
		if (view == m_btnOpenDevice)
			OnOpenDeviceBtn();
		else if (view == m_btnCloseDevice)
			OnCloseDeviceBtn();
		else if (view == m_btnEnroll)
			OnEnrollBtn();
		else if (view == m_btnVerify)
			OnVerifyBtn();
		else if (view == m_btnIdentify)
			OnIdentifyBtn();
		else if (view == m_btnCaptureImage)
			OnGetImageBtn();
		else if (view == m_btnCancel)
			OnCancelBtn();
		else if (view == m_btnGetUserCount)
			OnGetUserCount();
		else if (view == m_btnGetEmptyID)
			OnGetEmptyID();
		else if (view == m_btnDeleteID)
			OnDeleteIDBtn();
		else if (view == m_btnDeleteAll)
			OnDeleteAllBtn();
	}

	public void InitWidget() {
		m_FpImageViewer = (ImageView) findViewById(R.id.ivImageViewer);
		m_btnOpenDevice = (Button) findViewById(R.id.btnOpenDevice);
		m_btnCloseDevice = (Button) findViewById(R.id.btnCloseDevice);
		m_btnEnroll = (Button) findViewById(R.id.btnEnroll);
		m_btnVerify = (Button) findViewById(R.id.btnVerify);
		m_btnIdentify = (Button) findViewById(R.id.btnIdentify);
		m_btnCaptureImage = (Button) findViewById(R.id.btnCaptureImage);
		m_btnCancel = (Button) findViewById(R.id.btnCancel);
		m_btnGetUserCount = (Button) findViewById(R.id.btnGetEnrollCount);
		m_btnGetEmptyID = (Button) findViewById(R.id.btnGetEmptyID);
		m_btnDeleteID = (Button) findViewById(R.id.btnRemoveTemplate);
		m_btnDeleteAll = (Button) findViewById(R.id.btnRemoveAll);
		m_txtStatus = (TextView) findViewById(R.id.txtStatus);
		m_editUserID = (EditText) findViewById(R.id.editUserID);
		m_spFpCount = (Spinner) findViewById(R.id.spinner);

		m_btnOpenDevice.setOnClickListener(this);
		m_btnCloseDevice.setOnClickListener(this);
		m_btnEnroll.setOnClickListener(this);
		m_btnVerify.setOnClickListener(this);
		m_btnIdentify.setOnClickListener(this);
		m_btnCaptureImage.setOnClickListener(this);
		m_btnCancel.setOnClickListener(this);
		m_btnGetUserCount.setOnClickListener(this);
		m_btnGetEmptyID.setOnClickListener(this);
		m_btnDeleteID.setOnClickListener(this);
		m_btnDeleteAll.setOnClickListener(this);

		if (m_usbComm == null) {
			m_usbComm = new DevComm(this, m_IConnectionHandler);
		}

		m_binImage = new byte[1024 * 100];
		m_bmpImage = new byte[1024 * 100];

		spinner_name = (Spinner) findViewById(R.id.spinner_serialport_name);
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, entryValues);
		spinner_name.setAdapter(adapter);
	}

	public void EnableCtrl(boolean bEnable) {
		m_btnEnroll.setEnabled(bEnable);
		m_btnVerify.setEnabled(bEnable);
		m_btnIdentify.setEnabled(bEnable);
		m_btnCancel.setEnabled(bEnable);
		m_btnGetUserCount.setEnabled(bEnable);
		m_btnGetEmptyID.setEnabled(bEnable);
		m_btnDeleteID.setEnabled(bEnable);
		m_btnDeleteAll.setEnabled(bEnable);
		m_btnCaptureImage.setEnabled(bEnable);

		// m_editUserID.setEnabled(bEnable);
	}

	public void SetInitialState() {
		m_txtStatus.setText("Please open device!");
		m_btnOpenDevice.setEnabled(true);
		m_btnCloseDevice.setEnabled(false);
		EnableCtrl(false);
	}

	// 打开
	public void OnOpenDeviceBtn() {
		String[] w_strInfo = new String[1];

		if (m_usbComm != null && m_usbComm.OpenComm()) {
			if (m_usbComm.Run_TestConnection() == DevComm.ERR_SUCCESS) {
				if (m_usbComm.Run_GetDeviceInfo(w_strInfo) == DevComm.ERR_SUCCESS) {
					m_txtStatus.setText("Open Success!\r\nDevice Info : "
							+ w_strInfo[0]);
					EnableCtrl(true);
					m_btnOpenDevice.setEnabled(false);
					m_btnCloseDevice.setEnabled(true);
				} else {
					m_txtStatus.setText("Can not connect to device!");
					m_usbComm.CloseComm();
				}
			} else {
				m_txtStatus.setText("Can not connect to device!");
				m_usbComm.CloseComm();
			}
		}
	}

	public void OnCloseDeviceBtn() {
		m_usbComm.CloseComm();

		// m_usbComm = null;

		SetInitialState();
	}

	// 登记
	public void OnEnrollBtn() {
		int w_nRet;
		int[] w_nState = new int[1];

		if (!CheckUserID())
			return;

		// Check if fp is exist
		w_nRet = m_usbComm.Run_GetStatus(m_nUserID, w_nState);

		if (w_nRet != DevComm.ERR_SUCCESS) {
			m_txtStatus.setText(GetErrorMsg(w_nRet));
			return;
		}

		if (w_nState[0] == DevComm.GD_TEMPLATE_NOT_EMPTY) {
			m_txtStatus.setText("Template is already exist");
			return;
		}

		m_txtStatus.setText("Press finger : " + m_nUserID);
		EnableCtrl(false);
		m_btnCloseDevice.setEnabled(false);
		m_btnCancel.setEnabled(true);
		m_usbComm.Run_SLEDControl(1);
		m_bCancel = false;

		new Thread(new Runnable() {
			int w_nRet, w_nUserID, w_nEnrollStep = 0, w_nGenCount = 3;
			int[] w_nDupID = new int[1];
			int[] w_nWidth = new int[1];
			int[] w_nHeight = new int[1];

			@Override
			public void run() {

				w_nUserID = m_nUserID;

				while (w_nEnrollStep < w_nGenCount) {
					m_strPost = String.format("Input finger #%d!",
							w_nEnrollStep + 1);
					m_FpImageViewer.post(runShowStatus);

					// Capture
					if (Capturing() < 0)
						return;

					m_strPost = "Release your finger.";
					m_FpImageViewer.post(runShowStatus);

					/*
					 * // Up Cpatured Image w_nRet = m_usbComm.Run_UpImage(0,
					 * m_binImage, w_nWidth, w_nHeight);
					 * 
					 * if (w_nRet != DevComm.ERR_SUCCESS) { m_strPost =
					 * GetErrorMsg(w_nRet); m_FpImageViewer.post(runShowStatus);
					 * m_FpImageViewer.post(runEnableCtrl); return; }
					 * 
					 * // Draw image m_nImgWidth = w_nWidth[0]; m_nImgHeight =
					 * w_nHeight[0]; m_FpImageViewer.post(runShowStatus);
					 * m_FpImageViewer.post(runDrawImage);
					 */

					// Create Template
					w_nRet = m_usbComm.Run_Generate(w_nEnrollStep);

					if (w_nRet != DevComm.ERR_SUCCESS) {
						if (w_nRet == DevComm.ERR_BAD_QUALITY) {
							m_strPost = "Bad quality. Try Again!";
							m_FpImageViewer.post(runShowStatus);
							continue;
						} else {
							m_strPost = GetErrorMsg(w_nRet);
							m_FpImageViewer.post(runShowStatus);
							m_FpImageViewer.post(runEnableCtrl);
							return;
						}
					}

					/*
					 * if(w_nEnrollStep == 0) { if (w_nGenCount == 3) m_strPost
					 * = "Two More"; else m_strPost = "One More"; } else
					 * if(w_nEnrollStep == 1) m_strPost = "One More";
					 * 
					 * m_FpImageViewer.post(runShowStatus);
					 */

					w_nEnrollStep++;
				}

				// m_strPost = "Release Finger";
				// m_FpImageViewer.post(runShowStatus);

				// Merge
				if (w_nGenCount != 1) {
					// . Merge Template
					w_nRet = m_usbComm.Run_Merge(0, w_nGenCount);

					if (w_nRet != DevComm.ERR_SUCCESS) {
						m_strPost = GetErrorMsg(w_nRet);
						m_FpImageViewer.post(runShowStatus);
						m_FpImageViewer.post(runEnableCtrl);
						return;
					}
				}

				// . Store template
				w_nRet = m_usbComm.Run_StoreChar(w_nUserID, 0, w_nDupID);

				if (w_nRet != DevComm.ERR_SUCCESS) {
					if (w_nRet == DevComm.ERR_DUPLICATION_ID)
						m_strPost = String.format(
								"Result : Fail\r\nDuplication ID = %d",
								w_nDupID[0]);
					else
						m_strPost = GetErrorMsg(w_nRet);
				} else
					m_strPost = String.format(
							"Result : Success\r\nTemplate No : %d", m_nUserID);

				m_FpImageViewer.post(runShowStatus);
				m_FpImageViewer.post(runEnableCtrl);
			}
		}).start();
	}

	// 验证
	public void OnIdentifyBtn() {

		EnableCtrl(false);
		m_btnCloseDevice.setEnabled(false);
		m_btnCancel.setEnabled(true);
		m_usbComm.Run_SLEDControl(1);
		m_bCancel = false;

		m_strPost = "";

		new Thread(new Runnable() {
			int w_nRet;
			int[] w_nID = new int[1];
			int[] w_nLearned = new int[1];
			int[] w_nWidth = new int[1];
			int[] w_nHeight = new int[1];

			@Override
			public void run() {

				while (true) {
					if (m_strPost.isEmpty())
						m_strPost = "Input your finger.";
					else
						m_strPost = m_strPost + "\r\nInput your finger.";
					m_FpImageViewer.post(runShowStatus);

					if (Capturing() < 0)
						return;

					m_strPost = "Release your finger.";
					m_FpImageViewer.post(runShowStatus);

					/*
					 * // Up Cpatured Image w_nRet = m_usbComm.Run_UpImage(0,
					 * m_binImage, w_nWidth, w_nHeight);
					 * 
					 * if (w_nRet != DevComm.ERR_SUCCESS) { m_strPost =
					 * GetErrorMsg(w_nRet); m_FpImageViewer.post(runShowStatus);
					 * m_FpImageViewer.post(runEnableCtrl); return; }
					 * 
					 * // Draw image m_nImgWidth = w_nWidth[0]; m_nImgHeight =
					 * w_nHeight[0]; m_FpImageViewer.post(runShowStatus);
					 * m_FpImageViewer.post(runDrawImage);
					 */

					// Create template
					m_nPassedTime = SystemClock.elapsedRealtime();
					w_nRet = m_usbComm.Run_Generate(0);

					if (w_nRet != DevComm.ERR_SUCCESS) {
						m_strPost = GetErrorMsg(w_nRet);
						m_FpImageViewer.post(runShowStatus);

						if (w_nRet == DevComm.ERR_CONNECTION)
							return;
						else {
							SystemClock.sleep(1000);
							continue;
						}
					}

					// Identify
					w_nRet = m_usbComm.Run_Search(0, 1, m_nMaxFpCount, w_nID,
							w_nLearned);
					m_nPassedTime = SystemClock.elapsedRealtime()
							- m_nPassedTime;

					if (w_nRet == DevComm.ERR_SUCCESS)
						m_strPost = String
								.format("Result : Success\r\nTemplate No : %d, Learn Result : %d\r\nMatch Time : %dms",
										w_nID[0], w_nLearned[0], m_nPassedTime);
					else {
						m_strPost = String.format("\r\nMatch Time : %dms",
								m_nPassedTime);
						m_strPost = GetErrorMsg(w_nRet) + m_strPost;
					}
				}
			}
		}).start();
	}

	public void OnVerifyBtn() {
		int w_nRet;
		int[] w_nState = new int[1];

		if (!CheckUserID())
			return;

		w_nRet = m_usbComm.Run_GetStatus(m_nUserID, w_nState);

		if (w_nRet != DevComm.ERR_SUCCESS) {
			m_txtStatus.setText(GetErrorMsg(w_nRet));
			return;
		}

		if (w_nState[0] == DevComm.GD_TEMPLATE_EMPTY) {
			m_txtStatus.setText("Template is empty");
			return;
		}

		m_txtStatus.setText("Press finger");
		EnableCtrl(false);
		m_btnCloseDevice.setEnabled(false);
		m_btnCancel.setEnabled(true);
		m_usbComm.Run_SLEDControl(1);
		m_bCancel = false;

		new Thread(new Runnable() {
			int w_nRet;
			int[] w_nLearned = new int[1];
			int[] w_nWidth = new int[1];
			int[] w_nHeight = new int[1];

			@Override
			public void run() {

				if (Capturing() < 0)
					return;

				m_strPost = "Release your finger.";
				m_FpImageViewer.post(runShowStatus);

				/*
				 * // Up Cpatured Image w_nRet = m_usbComm.Run_UpImage(0,
				 * m_binImage, w_nWidth, w_nHeight);
				 * 
				 * if (w_nRet != DevComm.ERR_SUCCESS) { m_strPost =
				 * GetErrorMsg(w_nRet); m_FpImageViewer.post(runShowStatus);
				 * m_FpImageViewer.post(runEnableCtrl); return; }
				 * 
				 * // Draw image m_nImgWidth = w_nWidth[0]; m_nImgHeight =
				 * w_nHeight[0]; m_FpImageViewer.post(runShowStatus);
				 * m_FpImageViewer.post(runDrawImage);
				 */

				// Create template
				m_nPassedTime = SystemClock.elapsedRealtime();
				w_nRet = m_usbComm.Run_Generate(0);

				if (w_nRet != DevComm.ERR_SUCCESS) {
					m_strPost = GetErrorMsg(w_nRet);
					m_FpImageViewer.post(runShowStatus);
					m_FpImageViewer.post(runEnableCtrl);
					return;
				}

				// Verify
				w_nRet = m_usbComm.Run_Verify(m_nUserID, 0, w_nLearned);
				m_nPassedTime = SystemClock.elapsedRealtime() - m_nPassedTime;

				if (w_nRet == DevComm.ERR_SUCCESS)
					m_strPost = String
							.format("Result : Success\r\nTemplate No : %d, Learn Result : %d\r\nMatch Time : %dms",
									m_nUserID, w_nLearned[0], m_nPassedTime);
				else
					m_strPost = GetErrorMsg(w_nRet);

				m_FpImageViewer.post(runShowStatus);
				m_FpImageViewer.post(runEnableCtrl);
			}
		}).start();
	}

	public void OnDetectFingerBtn() {
		/*
		 * if (!m_usbComm.IsInit()) return; m_usbComm.oem_cmos_led(true); if
		 * (m_usbComm.oem_is_press_finger() < 0 ){
		 * m_txtStatus.setText("Communication error!"); return; } if
		 * (m_usbComm.m_wLastAck == DevComm.NACK_INFO ){
		 * m_txtStatus.setText(DisplayErr( m_usbComm.m_nLastAckParam, 0 ));
		 * return; } if( m_usbComm.m_nLastAckParam != 0 ){
		 * m_txtStatus.setText("Finger is not pressed!");
		 * m_usbComm.oem_cmos_led(false); return; }
		 * 
		 * m_txtStatus.setText("Finger is pressed!");
		 * 
		 * m_usbComm.oem_cmos_led(false);
		 */
	}

	public void OnGetImageBtn() {
		GetConCaptureState();

		if (m_bConCapture) {
			EnableCtrl(false);
			m_btnCloseDevice.setEnabled(false);
			m_btnCancel.setEnabled(true);
		}

		EnableCtrl(false);
		m_btnCloseDevice.setEnabled(false);
		m_btnCancel.setEnabled(true);
		m_usbComm.Run_SLEDControl(1);
		m_bCancel = false;
		m_txtStatus.setText("Input finger!");

		new Thread(new Runnable() {
			int w_nRet;
			int[] width = new int[1];
			int[] height = new int[1];

			@Override
			public void run() {

				if (Capturing() < 0)
					return;

				w_nRet = m_usbComm.Run_UpImage(0, m_binImage, width, height);

				if (w_nRet != DevComm.ERR_SUCCESS) {
					m_strPost = GetErrorMsg(w_nRet);
					m_FpImageViewer.post(runShowStatus);
					m_FpImageViewer.post(runEnableCtrl);
					return;
				}

				m_nImgWidth = width[0];
				m_nImgHeight = height[0];
				m_strPost = "Get Image OK !";
				m_FpImageViewer.post(runShowStatus);
				m_FpImageViewer.post(runDrawImage);
				m_FpImageViewer.post(runEnableCtrl);
			}
		}).start();
	}

	public void OnCancelBtn() {
		m_bCancel = true;
	}

	public void OnGetUserCount() {

		int w_nRet;
		int[] w_nEnrollCount = new int[1];

		w_nRet = m_usbComm.Run_GetEnrollCount(1, m_nMaxFpCount, w_nEnrollCount);

		if (w_nRet != DevComm.ERR_SUCCESS) {
			m_txtStatus.setText(GetErrorMsg(w_nRet));
			return;
		}

		m_txtStatus.setText(String.format(
				"Result : Success\r\nEnroll Count = %d", w_nEnrollCount[0]));
	}

	public void OnGetEmptyID() {
		int w_nRet;
		int[] w_nEmptyID = new int[1];

		w_nRet = m_usbComm.Run_GetEmptyID(1, m_nMaxFpCount, w_nEmptyID);

		if (w_nRet != DevComm.ERR_SUCCESS) {
			m_txtStatus.setText(GetErrorMsg(w_nRet));
			return;
		}

		m_txtStatus.setText(String.format("Result : Success\r\nEmpty ID = %d",
				w_nEmptyID[0]));
		m_editUserID.setText(String.format("%d", w_nEmptyID[0]));
	}

	public void OnDeleteIDBtn() {
		int w_nRet;

		if (!CheckUserID())
			return;

		w_nRet = m_usbComm.Run_DelChar(m_nUserID, m_nUserID);

		if (w_nRet != DevComm.ERR_SUCCESS) {
			m_txtStatus.setText(GetErrorMsg(w_nRet));
			return;
		}

		m_txtStatus.setText("Delete OK !");
	}

	public void OnDeleteAllBtn() {
		int w_nRet;

		w_nRet = m_usbComm.Run_DelChar(1, m_nMaxFpCount);

		if (w_nRet != DevComm.ERR_SUCCESS) {
			m_txtStatus.setText(GetErrorMsg(w_nRet));
			return;
		}

		m_txtStatus.setText("Delete all OK !");
	}

	public boolean CheckUserID() {
		String str;

		str = m_editUserID.getText().toString();

		if (str == "") {
			m_txtStatus.setText("Please input user id");
			return false;
		}

		try {
			m_nUserID = Integer.parseInt(str);
		} catch (NumberFormatException e) {
			m_txtStatus.setText("Please input correct user id(1~"
					+ m_nMaxFpCount + ")");
			return false;
		}

		if (m_nUserID > (m_nMaxFpCount) || m_nUserID < 1) {
			m_txtStatus.setText("Please input correct user id(1~"
					+ m_nMaxFpCount + ")");
			return false;
		}

		return true;
	}

	public boolean CheckParam(int nMin, int nMax) {
		String str;

		str = m_editParam.getText().toString();

		if (str == "") {
			m_txtStatus.setText("Please input parameter!");
			return false;
		}

		try {
			m_nParam = Integer.parseInt(str);
		} catch (NumberFormatException e) {
			m_txtStatus.setText(String.format(
					"Please input parameter (%d~%d)!", nMin, nMax));
			return false;
		}

		if (m_nParam > nMax || m_nParam < nMin) {
			m_txtStatus.setText(String.format(
					"Please input correct parameter (%d~%d)!", nMin, nMax));
			return false;
		}

		return true;
	}

	private void GetConCaptureState() {

	}

	private int Capturing() {
		int w_nRet;
		while (true) {

			w_nRet = m_usbComm.Run_GetImage();

			if (w_nRet == DevComm.ERR_CONNECTION) {
				m_strPost = "Communication error!";
				m_FpImageViewer.post(runShowStatus);
				m_FpImageViewer.post(runEnableCtrl);
				return -1;
			} else if (w_nRet == DevComm.ERR_SUCCESS)
				break;

			if (m_bCancel) {
				StopOperation();
				return -1;
			}
		}

		return 0;
	}

	private void StopOperation() {
		m_strPost = "Canceled";
		m_FpImageViewer.post(runShowStatus);
		m_FpImageViewer.post(runEnableCtrl);
	}

	private String GetErrorMsg(int nErrorCode) {
		String str = new String("");

		switch (nErrorCode) {
		case DevComm.ERR_SUCCESS:
			str = "Succcess";
			break;
		case DevComm.ERR_VERIFY:
			str = "Verify NG";
			break;
		case DevComm.ERR_IDENTIFY:
			str = "Identify NG";
			break;
		case DevComm.ERR_EMPTY_ID_NOEXIST:
			str = "Empty Template no Exist";
			break;
		case DevComm.ERR_BROKEN_ID_NOEXIST:
			str = "Broken Template no Exist";
			break;
		case DevComm.ERR_TMPL_NOT_EMPTY:
			str = "Template of this ID Already Exist";
			break;
		case DevComm.ERR_TMPL_EMPTY:
			str = "This Template is Already Empty";
			break;
		case DevComm.ERR_INVALID_TMPL_NO:
			str = "Invalid Template No";
			break;
		case DevComm.ERR_ALL_TMPL_EMPTY:
			str = "All Templates are Empty";
			break;
		case DevComm.ERR_INVALID_TMPL_DATA:
			str = "Invalid Template Data";
			break;
		case DevComm.ERR_DUPLICATION_ID:
			str = "Duplicated ID : ";
			break;
		case DevComm.ERR_BAD_QUALITY:
			str = "Bad Quality Image";
			break;
		case DevComm.ERR_MERGE_FAIL:
			str = "Merge failed";
			break;
		case DevComm.ERR_NOT_AUTHORIZED:
			str = "Device not authorized.";
			break;
		case DevComm.ERR_MEMORY:
			str = "Memory Error ";
			break;
		case DevComm.ERR_INVALID_PARAM:
			str = "Invalid Parameter";
			break;
		case DevComm.ERR_GEN_COUNT:
			str = "Generation Count is invalid";
			break;
		case DevComm.ERR_INVALID_BUFFER_ID:
			str = "Ram Buffer ID is invalid.";
			break;
		case DevComm.ERR_INVALID_OPERATION_MODE:
			str = "Invalid Operation Mode!";
			break;
		case DevComm.ERR_FP_NOT_DETECTED:
			str = "Finger is not detected.";
			break;
		default:
			str = String.format("Fail, error code=%d", nErrorCode);
			break;
		}

		return str;
	}

	Runnable runShowStatus = new Runnable() {
		public void run() {
			m_txtStatus.setText(m_strPost);
		}
	};

	Runnable runDrawImage = new Runnable() {
		public void run() {
			int nSize;

			MakeBMPBuf(m_binImage, m_bmpImage, m_nImgWidth, m_nImgHeight);

			if ((m_nImgWidth % 4) != 0)
				nSize = m_nImgWidth + (4 - (m_nImgWidth % 4));
			else
				nSize = m_nImgWidth;

			nSize = 1078 + nSize * m_nImgHeight;

			// DebugManage.WriteBmp(m_bmpImage, nSize);

			Bitmap image = BitmapFactory.decodeByteArray(m_bmpImage, 0, nSize);

			m_FpImageViewer.setImageBitmap(image);
		}
	};

	Runnable runEnableCtrl = new Runnable() {
		public void run() {
			EnableCtrl(true);
			m_btnOpenDevice.setEnabled(false);
			m_btnCloseDevice.setEnabled(true);
			m_usbComm.Run_SLEDControl(0);
		}
	};

	private void MakeBMPBuf(byte[] Input, byte[] Output, int iImageX,
			int iImageY) {

		byte[] w_bTemp = new byte[4];
		byte[] head = new byte[1078];
		byte[] head2 = {
		/***************************/
				// file header
				0x42, 0x4d,// file type
				// 0x36,0x6c,0x01,0x00, //file size***
				0x0, 0x0, 0x0, 0x00, // file size***
				0x00, 0x00, // reserved
				0x00, 0x00,// reserved
				0x36, 0x4, 0x00, 0x00,// head byte***
				/***************************/
				// infoheader
				0x28, 0x00, 0x00, 0x00,// struct size

				// 0x00,0x01,0x00,0x00,//map width***
				0x00, 0x00, 0x0, 0x00,// map width***
				// 0x68,0x01,0x00,0x00,//map height***
				0x00, 0x00, 0x00, 0x00,// map height***

				0x01, 0x00,// must be 1
				0x08, 0x00,// color count***
				0x00, 0x00, 0x00, 0x00, // compression
				// 0x00,0x68,0x01,0x00,//data size***
				0x00, 0x00, 0x00, 0x00,// data size***
				0x00, 0x00, 0x00, 0x00, // dpix
				0x00, 0x00, 0x00, 0x00, // dpiy
				0x00, 0x00, 0x00, 0x00,// color used
				0x00, 0x00, 0x00, 0x00,// color important
		};

		int i, j, num, iImageStep;

		Arrays.fill(w_bTemp, (byte) 0);

		System.arraycopy(head2, 0, head, 0, head2.length);

		if ((iImageX % 4) != 0)
			iImageStep = iImageX + (4 - (iImageX % 4));
		else
			iImageStep = iImageX;

		num = iImageX;
		head[18] = (byte) (num & (byte) 0xFF);
		num = num >> 8;
		head[19] = (byte) (num & (byte) 0xFF);
		num = num >> 8;
		head[20] = (byte) (num & (byte) 0xFF);
		num = num >> 8;
		head[21] = (byte) (num & (byte) 0xFF);

		num = iImageY;
		head[22] = (byte) (num & (byte) 0xFF);
		num = num >> 8;
		head[23] = (byte) (num & (byte) 0xFF);
		num = num >> 8;
		head[24] = (byte) (num & (byte) 0xFF);
		num = num >> 8;
		head[25] = (byte) (num & (byte) 0xFF);

		j = 0;
		for (i = 54; i < 1078; i = i + 4) {
			head[i] = head[i + 1] = head[i + 2] = (byte) j;
			head[i + 3] = 0;
			j++;
		}

		System.arraycopy(head, 0, Output, 0, 1078);

		if (iImageStep == iImageX) {
			for (i = 0; i < iImageY; i++) {
				System.arraycopy(Input, i * iImageX, Output,
						1078 + i * iImageX, iImageX);
			}
		} else {
			iImageStep = iImageStep - iImageX;

			for (i = 0; i < iImageY; i++) {
				System.arraycopy(Input, i * iImageX, Output, 1078 + i
						* (iImageX + iImageStep), iImageX);
				System.arraycopy(w_bTemp, 0, Output, 1078 + i
						* (iImageX + iImageStep) + iImageX, iImageStep);
			}
		}
	}

	private final IUsbConnState m_IConnectionHandler = new IUsbConnState() {
		@Override
		public void onUsbConnected() {
			String[] w_strInfo = new String[1];

			if (m_usbComm.Run_TestConnection() == DevComm.ERR_SUCCESS) {
				if (m_usbComm.Run_GetDeviceInfo(w_strInfo) == DevComm.ERR_SUCCESS) {
					EnableCtrl(true);
					m_btnOpenDevice.setEnabled(false);
					m_btnCloseDevice.setEnabled(true);
					m_txtStatus.setText("Open Success!\r\nDevice Info : "
							+ w_strInfo[0]);
				}
			} else
				m_txtStatus.setText("Can not connect to device!");
		}

		@Override
		public void onUsbPermissionDenied() {
			m_txtStatus.setText("Permission denied!");
		}

		@Override
		public void onDeviceNotFound() {
			m_txtStatus.setText("Can not find usb device!");
		}
	};
}

package com.smartdevicesdk.fingerprint;

import android.app.Activity;
import android.os.Bundle;
import android.content.Context;
import android.os.SystemClock;
import android.serialport.api.SerialPortParam;
import android.util.Log;
import android.widget.Toast;

import java.util.Arrays;

/**
 * Created by Administrator on 7/24/13.
 */
public class DevComm {

	public static final int GD_RECORD_SIZE = 498;
	public static final int GD_MAX_RECORD_COUNT = 2000;
	public static final int ID_NOTE_SIZE = 64;
	public static final int MODULE_SN_LEN = 16;

	private static final int SCSI_TIMEOUT = 5000; // ms
	private static final int COMM_SLEEP_TIME = 40; // ms

	private static final int CMD_PACKET_LEN = 26;
	private static final int RCM_PACKET_LEN = 26;
	private static final int RCM_DATA_OFFSET = 10;

	/***************************************************************************/
	/***************************************************************************/
	private static final int CMD_PREFIX_CODE = 0xAA55;
	private static final int CMD_DATA_PREFIX_CODE = 0xA55A;
	private static final int RCM_PREFIX_CODE = 0x55AA;
	private static final int RCM_DATA_PREFIX_CODE = 0x5AA5;

	/***************************************************************************
	 * System Code (0x0000 ~ 0x001F, 0x0000 : Reserved)
	 ***************************************************************************/
	private static final short CMD_TEST_CONNECTION = 0x0001;
	private static final short CMD_SET_PARAM = 0x0002;
	private static final short CMD_GET_PARAM = 0x0003;
	private static final short CMD_GET_DEVICE_INFO = 0x0004;
	private static final short CMD_ENTER_ISPMODE = 0x0005;
	private static final short CMD_SET_ID_NOTE = 0x0006;
	private static final short CMD_GET_ID_NOTE = 0x0007;
	private static final short CMD_SET_MODULE_SN = 0x0008;
	private static final short CMD_GET_MODULE_SN = 0x0009;

	/***************************************************************************
	 * Sensor Code (0x0020 ~ 0x003F)
	 ***************************************************************************/
	private static final short CMD_GET_IMAGE = 0x0020;
	private static final short CMD_FINGER_DETECT = 0x0021;
	private static final short CMD_UP_IMAGE = 0x0022;
	private static final short CMD_DOWN_IMAGE = 0x0023;
	private static final short CMD_SLED_CTRL = 0x0024;

	/***************************************************************************
	 * Template Code (0x0040 ~ 0x005F)
	 ***************************************************************************/
	private static final short CMD_STORE_CHAR = 0x0040;
	private static final short CMD_LOAD_CHAR = 0x0041;
	private static final short CMD_UP_CHAR = 0x0042;
	private static final short CMD_DOWN_CHAR = 0x0043;
	private static final short CMD_DEL_CHAR = 0x0044;
	private static final short CMD_GET_EMPTY_ID = 0x0045;
	private static final short CMD_GET_STATUS = 0x0046;
	private static final short CMD_GET_BROKEN_ID = 0x0047;
	private static final short CMD_GET_ENROLL_COUNT = 0x0048;

	/***************************************************************************
	 * FingerPrint Alagorithm Code (0x0060 ~ 0x007F)
	 ***************************************************************************/
	private static final short CMD_GENERATE = 0x0060;
	private static final short CMD_MERGE = 0x0061;
	private static final short CMD_MATCH = 0x0062;
	private static final short CMD_SEARCH = 0x0063;
	private static final short CMD_VERIFY = 0x0064;

	/***************************************************************************
	 * Unknown Command
	 ***************************************************************************/
	private static final short RCM_INCORRECT_COMMAND = 0x00FF;

	/***************************************************************************
	 * Error Code
	 ***************************************************************************/
	public static final int ERR_SUCCESS = 0;
	public static final int ERR_FAIL = 1;
	public static final int ERR_CONNECTION = 2;
	public static final int ERR_VERIFY = 0x10;
	public static final int ERR_IDENTIFY = 0x11;
	public static final int ERR_TMPL_EMPTY = 0x12;
	public static final int ERR_TMPL_NOT_EMPTY = 0x13;
	public static final int ERR_ALL_TMPL_EMPTY = 0x14;
	public static final int ERR_EMPTY_ID_NOEXIST = 0x15;
	public static final int ERR_BROKEN_ID_NOEXIST = 0x16;
	public static final int ERR_INVALID_TMPL_DATA = 0x17;
	public static final int ERR_DUPLICATION_ID = 0x18;
	public static final int ERR_BAD_QUALITY = 0x19;
	public static final int ERR_MERGE_FAIL = 0x1A;
	public static final int ERR_NOT_AUTHORIZED = 0x1B;
	public static final int ERR_MEMORY = 0x1C;
	public static final int ERR_INVALID_TMPL_NO = 0x1D;
	public static final int ERR_INVALID_PARAM = 0x22;
	public static final int ERR_GEN_COUNT = 0x25;
	public static final int ERR_INVALID_BUFFER_ID = 0x26;
	public static final int ERR_INVALID_OPERATION_MODE = 0x27;
	public static final int ERR_FP_NOT_DETECTED = 0x28;

	/***************************************************************************
	 * Parameter Index
	 ***************************************************************************/
	public static final int DP_DEVICE_ID = 0;
	public static final int DP_SECURITY_LEVEL = 1;
	public static final int DP_DUP_CHECK = 2;
	public static final int DP_BAUDRATE = 3;
	public static final int DP_AUTO_LEARN = 4;

	/***************************************************************************
	 * Device ID, Security Level
	 ***************************************************************************/
	public static final int MIN_DEVICE_ID = 1;
	public static final int MAX_DEVICE_ID = 255;
	public static final int MIN_SECURITY_LEVEL = 1;
	public static final int MAX_SECURITY_LEVEL = 5;

	public static final int GD_TEMPLATE_NOT_EMPTY = 0x01;
	public static final int GD_TEMPLATE_EMPTY = 0x00;

	// --------------- For Usb Communication ------------//
	public int m_nPacketSize;
	public byte m_bySrcDeviceID = 0, m_byDstDeviceID = 0;
	// public byte[] m_abyPacket = new byte[64 * 1024];
	// public byte[] bt = new byte[64 * 1024];
	// --------------------------------------------------//

	private final Context mApplicationContext;
	private Activity m_parentAcitivity;
	private static final int VID = 0x2009;
	private static final int PID = 0x7638;
	private static final String TAG = null;

	FingerPrintHelper serialport = new FingerPrintHelper();

	public DevComm(Activity parentActivity, IUsbConnState usbConnState) {

		DebugManage.DeleteLog();

		m_parentAcitivity = parentActivity;
		mApplicationContext = parentActivity.getApplicationContext();
		serialport = new FingerPrintHelper();


	}

	public boolean OpenComm() {
		if(serialport.IsOpen())
		{
			return true;
		}
		if (serialport.OpenSerialPort(SerialPortParam.Path,
				SerialPortParam.Baudrate)) {
			Log.i(TAG, "serialport is open");
		}
		return true;
	}

	public boolean CloseComm() {
		serialport.CloseSerialPort();
		return true;
	}

	/************************************************************************/
	/************************************************************************/
	public int Run_TestConnection() {
		byte[] w_abyData = new byte[2];
		byte[] bt = InitCmdPacket(CMD_TEST_CONNECTION, m_bySrcDeviceID,
				m_byDstDeviceID,w_abyData, (short) 0);

		String str=FingerPrintHelper.byte2HexStr(bt);
		byte[] w_bRet = SendPackage(bt);
		

		if (w_bRet == null)
			return ERR_CONNECTION;

		return GetRetCode(w_bRet);
	}

	/************************************************************************/
	/************************************************************************/
	public int Run_SetParam(int p_nParamIndex, int p_nParamValue) {
		byte[] w_abyData = new byte[5];

		w_abyData[0] = (byte) p_nParamIndex;

		// memcpy(&w_abyData[1], &p_nParamValue, 4);
		w_abyData[1] = (byte) (p_nParamValue & 0x000000ff);
		w_abyData[2] = (byte) ((p_nParamValue & 0x0000ff00) >> 8);
		w_abyData[3] = (byte) ((p_nParamValue & 0x00ff0000) >> 16);
		w_abyData[4] = (byte) ((p_nParamValue & 0xff000000) >> 24);

		w_abyData = InitCmdPacket(CMD_SET_PARAM, m_bySrcDeviceID,
				m_byDstDeviceID,w_abyData, (short) 5);

		byte[] w_bRet = SendPackage(w_abyData);

		if (w_bRet == null)
			return ERR_CONNECTION;

		return GetRetCode(w_bRet);
	}

	/************************************************************************/
	/************************************************************************/
	public int Run_GetParam(int p_nParamIndex, int[] p_pnParamValue) {
		byte[] w_abyData = new byte[1];

		w_abyData[0] = (byte) p_nParamIndex;

		w_abyData = InitCmdPacket(CMD_GET_PARAM, m_bySrcDeviceID,
				m_byDstDeviceID,w_abyData, (short) 1);

		byte[] w_bRet = SendPackage(w_abyData);

		if (w_bRet == null)
			return ERR_CONNECTION;

		if (GetRetCode(w_bRet) != ERR_SUCCESS)
			return GetRetCode(w_bRet);

		return ERR_SUCCESS;
	}

	/************************************************************************/
	/************************************************************************/
	public int Run_GetDeviceInfo(String[] p_szDevInfo) {
		int w_nDevInfoLen;
		String w_strTmp;
		byte[] bt = null;
		byte[] w_abyData = new byte[1];

		bt = InitCmdPacket(CMD_GET_DEVICE_INFO, m_bySrcDeviceID,
				m_byDstDeviceID,w_abyData, 0);

		byte[] w_bRet = SendPackage(bt);

		if (w_bRet == null)
			return ERR_CONNECTION;

		if (GetRetCode(w_bRet) != ERR_SUCCESS)
			return GetRetCode(w_bRet);

		w_nDevInfoLen = MAKEWORD(bt[RCM_DATA_OFFSET + 0],
				bt[RCM_DATA_OFFSET + 1]);

		w_bRet = SendPackage(bt);

		if (w_bRet == null)
			return ERR_CONNECTION;

		if (GetRetCode(w_bRet) != ERR_SUCCESS)
			return GetRetCode(w_bRet);

		// memcpy(p_szDevInfo, g_pRcmPacket->m_abyData, w_wDevInfoLen);
		System.arraycopy(bt, RCM_DATA_OFFSET, bt, 0, w_nDevInfoLen);
		w_strTmp = new String(bt);
		p_szDevInfo[0] = w_strTmp;

		return ERR_SUCCESS;
	}

	/************************************************************************/
	/************************************************************************/
	public int Run_SetIDNote(int p_nTmplNo, String p_pstrNote) {
		byte[] w_abyData = new byte[ID_NOTE_SIZE + 2];
		byte[] w_abyData2 = new byte[2];
		byte[] w_abyNoteBuf = p_pstrNote.getBytes();

		// . Assemble command packet
		w_abyData2[0] = LOBYTE((short) (ID_NOTE_SIZE + 2));
		w_abyData2[1] = HIBYTE((short) (ID_NOTE_SIZE + 2));

		w_abyData2 = InitCmdPacket(CMD_SET_ID_NOTE, m_bySrcDeviceID,
				m_byDstDeviceID,w_abyData2, 2);

		// . Send command packet to target
		byte[] w_bRet = SendPackage(w_abyData2);

		if (w_bRet == null)
			return ERR_CONNECTION;

		if (GetRetCode(w_bRet) != ERR_SUCCESS)
			return GetRetCode(w_bRet);

		// . Assemble data packet
		memset(w_abyData, (byte) 0, ID_NOTE_SIZE + 2);
		w_abyData[0] = LOBYTE((short) p_nTmplNo);
		w_abyData[1] = HIBYTE((short) p_nTmplNo);
		System.arraycopy(w_abyNoteBuf, 0, w_abyData, 2, w_abyNoteBuf.length);

		InitCmdDataPacket(CMD_SET_ID_NOTE, m_bySrcDeviceID, m_byDstDeviceID,
				w_abyData, ID_NOTE_SIZE + 2);

		// . Send data packet to target
		w_bRet = SendPackage(w_abyData);

		if (w_bRet == null)
			return ERR_CONNECTION;

		if (GetRetCode(w_bRet) != ERR_SUCCESS)
			return GetRetCode(w_bRet);

		return ERR_SUCCESS;
	}

	/************************************************************************/
	/************************************************************************/
	public int Run_GetIDNote(int p_nTmplNo, String[] p_pstrNote) {
		byte[] w_abyData = new byte[2];
		String w_strTmp;
		byte[] bt = null;

		// . Assemble command packet
		w_abyData[0] = LOBYTE((short) p_nTmplNo);
		w_abyData[1] = HIBYTE((short) p_nTmplNo);
		w_abyData = InitCmdPacket(CMD_GET_ID_NOTE, m_bySrcDeviceID,
				m_byDstDeviceID,w_abyData, 2);

		// . Send command packet to target
		byte[] w_bRet = SendPackage(w_abyData);

		if (w_bRet == null)
			return ERR_CONNECTION;

		if (GetRetCode(w_bRet) != ERR_SUCCESS)
			return GetRetCode(w_bRet);

		w_bRet = SendPackage(w_abyData);

		if (w_bRet == null)
			return ERR_CONNECTION;

		if (GetRetCode(w_bRet) != ERR_SUCCESS)
			return GetRetCode(w_bRet);

		// memset(bt, (byte)0, ID_NOTE_SIZE+1);
		memset(bt, (byte) 0, 512);
		System.arraycopy(bt, RCM_DATA_OFFSET, bt, 0, ID_NOTE_SIZE);

		w_strTmp = new String(bt);
		p_pstrNote[0] = w_strTmp;

		return ERR_SUCCESS;
	}

	/************************************************************************/
	/************************************************************************/
	public int Run_SetModuleSN(String p_pstrModuleSN) {
		byte[] w_abyData = p_pstrModuleSN.getBytes();
		byte[] w_abyModuleSN = new byte[MODULE_SN_LEN];
		byte[] w_abyData2 = new byte[2];

		memset(w_abyModuleSN, (byte) 0, MODULE_SN_LEN);
		System.arraycopy(w_abyData, 0, w_abyModuleSN, 0, w_abyData.length);

		// . Assemble command packet
		w_abyData2[0] = LOBYTE((short) (MODULE_SN_LEN));
		w_abyData2[1] = HIBYTE((short) (MODULE_SN_LEN));

		w_abyData2 = InitCmdPacket(CMD_SET_MODULE_SN, m_bySrcDeviceID,
				m_byDstDeviceID,w_abyData2, 2);

		// . Send command packet to target
		byte[] w_bRet = SendPackage(w_abyData2);

		if (w_bRet == null)
			return ERR_CONNECTION;

		if (GetRetCode(w_bRet) != ERR_SUCCESS)
			return GetRetCode(w_bRet);

		// . Assemble data packet
		InitCmdDataPacket(CMD_SET_MODULE_SN, m_bySrcDeviceID, m_byDstDeviceID,
				w_abyModuleSN, MODULE_SN_LEN);

		// . Send data packet to target
		w_bRet = SendPackage(w_abyModuleSN);

		if (w_bRet == null)
			return ERR_CONNECTION;

		return GetRetCode(w_bRet);
	}

	/************************************************************************/
	/************************************************************************/
	public int Run_GetModuleSN(String[] p_pstrModuleSN) {
		String w_strTmp;
		byte[] bt = null;
		byte[] w_abyData = new byte[2];

		// . Assemble command packet
		bt = InitCmdPacket(CMD_GET_MODULE_SN, m_bySrcDeviceID, m_byDstDeviceID,w_abyData,
				0);

		// . Send command packet to target
		byte[] w_bRet = SendPackage(bt);

		if (w_bRet == null)
			return ERR_CONNECTION;

		if (GetRetCode(w_bRet) != ERR_SUCCESS)
			return GetRetCode(w_bRet);

		w_bRet = SendPackage(bt);

		if (w_bRet == null)
			return ERR_CONNECTION;

		if (GetRetCode(w_bRet) != ERR_SUCCESS)
			return GetRetCode(w_bRet);

		// memset(bt, (byte)0, MODULE_SN_LEN+1);
		memset(bt, (byte) 0, 512);
		System.arraycopy(w_bRet, RCM_DATA_OFFSET, bt, 0, MODULE_SN_LEN);

		w_strTmp = new String(bt);
		p_pstrModuleSN[0] = w_strTmp;

		return ERR_SUCCESS;
	}

	/************************************************************************/
	/************************************************************************/
	public int Run_GetImage() {
		byte[] w_abyData = new byte[2];

		byte[] bt = null;
		bt = InitCmdPacket(CMD_GET_IMAGE, m_bySrcDeviceID, m_byDstDeviceID,w_abyData, 0);

		byte[] w_bRet = SendPackage(bt);

		if (w_bRet == null)
			return ERR_CONNECTION;

		return GetRetCode(w_bRet);
	}

	/************************************************************************/
	/************************************************************************/
	public int Run_FingerDetect(int[] p_pnDetectResult) {
		byte[] w_abyData = new byte[2];

		byte[] bt = InitCmdPacket(CMD_FINGER_DETECT, m_bySrcDeviceID,
				m_byDstDeviceID,w_abyData, 0);

		byte[] w_bRet = SendPackage(bt);

		if (w_bRet == null)
			return ERR_CONNECTION;

		if (GetRetCode(w_bRet) != ERR_SUCCESS)
			return GetRetCode(w_bRet);

		p_pnDetectResult[0] = w_bRet[RCM_DATA_OFFSET];

		return ERR_SUCCESS;
	}

	/************************************************************************/
	/************************************************************************/
	public int Run_UpImage(int p_nType, byte[] p_pFpData, int[] p_pnImgWidth,
			int[] p_pnImgHeight) {
		int w, h;
		byte[] w_abyData = new byte[1];

		w_abyData[0] = (byte) p_nType;

		w_abyData=InitCmdPacket(CMD_UP_IMAGE, m_bySrcDeviceID, m_byDstDeviceID,w_abyData,
				1);

		byte[] w_bRet = SendPackage(w_abyData);

		if (w_bRet == null)
			return ERR_CONNECTION;

		if (GetRetCode(w_bRet) != ERR_SUCCESS)
			return GetRetCode(w_bRet);

		/*w = MAKEWORD(w_bRet[RCM_DATA_OFFSET + 0], w_bRet[RCM_DATA_OFFSET + 1]);
		h = MAKEWORD(w_bRet[RCM_DATA_OFFSET + 2], w_bRet[RCM_DATA_OFFSET + 3]);

		w_bRet = SendPackage(w_bRet);

		if (w_bRet == null)
			return ERR_CONNECTION;

		p_pnImgWidth[0] = w;
		p_pnImgHeight[0] = h;
*/
		return ERR_SUCCESS;
	}

	/************************************************************************/
	/************************************************************************/
	public int Run_DownImage(byte[] p_pData, int p_nWidth, int p_nHeight) {
		/*
		 * int i, n, r, w, h; boolean w_bRet; BYTE w_abyData[840];
		 * 
		 * w = p_nWidth; h = p_nHeight;
		 * 
		 * w_abyData[0] = LOBYTE(p_nWidth); w_abyData[1] = HIBYTE(p_nWidth);
		 * w_abyData[2] = LOBYTE(p_nHeight); w_abyData[3] = HIBYTE(p_nHeight);
		 * 
		 * //. Assemble command packet InitCmdPacket(CMD_DOWN_IMAGE,
		 * m_bySrcDeviceID, m_byDstDeviceID, w_abyData, 4);
		 * 
		 * USB_SendPacket(CMD_DOWN_IMAGE, w_bRet, m_bySrcDeviceID,
		 * m_byDstDeviceID);
		 * 
		 * if(!w_bRet) return ERR_CONNECTION;
		 * 
		 * if (GetRetCode() != ERR_SUCCESS) return GetRetCode();
		 * 
		 * n = (w*h)/DOWN_IMAGE_DATA_UINT; r = (w*h)%DOWN_IMAGE_DATA_UINT;
		 * 
		 * w_bRet = USB_DownImage(m_hUsbHandle, p_pData, p_nWidth*p_nHeight);
		 * 
		 * if(w_bRet == false) return ERR_CONNECTION;
		 */
		return ERR_SUCCESS;
	}

	/************************************************************************/
	/************************************************************************/
	public int Run_SLEDControl(int p_nState) {
		byte[] w_abyData = new byte[2];

		w_abyData[0] = LOBYTE((short) p_nState);
		w_abyData[1] = HIBYTE((short) p_nState);

		w_abyData=InitCmdPacket(CMD_SLED_CTRL, m_bySrcDeviceID, m_byDstDeviceID,w_abyData,
				 2);

		byte[] w_bRet = SendPackage(w_abyData);

		if (w_bRet == null)
			return ERR_CONNECTION;

		return GetRetCode(w_bRet);
	}

	/************************************************************************/
	/************************************************************************/
	public int Run_StoreChar(int p_nTmplNo, int p_nRamBufferID,
			int[] p_pnDupTmplNo) {
		byte[] w_abyData = new byte[4];

		w_abyData[0] = LOBYTE((short) p_nTmplNo);
		w_abyData[1] = HIBYTE((short) p_nTmplNo);
		w_abyData[2] = LOBYTE((short) p_nRamBufferID);
		w_abyData[3] = HIBYTE((short) p_nRamBufferID);

		w_abyData=InitCmdPacket(CMD_STORE_CHAR, m_bySrcDeviceID, m_byDstDeviceID,w_abyData,
				 4);

		// . Send command packet to target
		byte[] w_bRet = SendPackage(w_abyData);

		if (w_bRet == null)
			return ERR_CONNECTION;

		if (GetRetCode(w_bRet) != ERR_SUCCESS) {
			if (GetRetCode(w_bRet) == ERR_DUPLICATION_ID)
				p_pnDupTmplNo[0] = MAKEWORD(w_bRet[RCM_DATA_OFFSET + 0],
						w_bRet[RCM_DATA_OFFSET + 1]);

			return GetRetCode(w_bRet);
		}

		return GetRetCode(w_bRet);
	}

	/************************************************************************/
	/************************************************************************/
	public int Run_LoadChar(int p_nTmplNo, int p_nRamBufferID) {
		byte[] w_abyData = new byte[4];

		w_abyData[0] = LOBYTE((short) p_nTmplNo);
		w_abyData[1] = HIBYTE((short) p_nTmplNo);
		w_abyData[2] = LOBYTE((short) p_nRamBufferID);
		w_abyData[3] = HIBYTE((short) p_nRamBufferID);

		w_abyData=InitCmdPacket(CMD_LOAD_CHAR, m_bySrcDeviceID, m_byDstDeviceID,w_abyData,
				 4);

		// . Send command packet to target
		byte[] w_bRet = SendPackage(w_abyData);

		if (w_bRet == null)
			return ERR_CONNECTION;

		return GetRetCode(w_bRet);
	}

	/************************************************************************/
	/************************************************************************/
	public int Run_UpChar(int p_nRamBufferID, byte[] p_pbyTemplate) {
		byte[] w_abyData = new byte[2];

		// . Assemble command packet
		w_abyData[0] = LOBYTE((short) p_nRamBufferID);
		w_abyData[1] = HIBYTE((short) p_nRamBufferID);
		w_abyData=InitCmdPacket(CMD_UP_CHAR, m_bySrcDeviceID, m_byDstDeviceID, w_abyData,
				2);

		// . Send command packet to target
		byte[] w_bRet = SendPackage(w_abyData);

		if (w_bRet == null)
			return ERR_CONNECTION;

		if (GetRetCode(w_bRet) != ERR_SUCCESS)
			return GetRetCode(w_bRet);

		// memcpy(p_pbyTemplate, &g_pRcmPacket->m_abyData[0], GD_RECORD_SIZE);
		System.arraycopy(w_bRet, RCM_DATA_OFFSET, p_pbyTemplate, 0,
				GD_RECORD_SIZE);

		return ERR_SUCCESS;
	}

	/************************************************************************/
	/************************************************************************/
	public int Run_DownChar(int p_nRamBufferID, byte[] p_pbyTemplate) {
		byte[] w_abyData = new byte[GD_RECORD_SIZE + 2];
		byte[] w_abyData2 = new byte[2];

		// . Assemble command packet
		w_abyData2[0] = LOBYTE((short) (GD_RECORD_SIZE + 2));
		w_abyData2[1] = HIBYTE((short) (GD_RECORD_SIZE + 2));

		w_abyData2=InitCmdPacket(CMD_DOWN_CHAR, m_bySrcDeviceID, m_byDstDeviceID,w_abyData2,
				 2);

		// . Send command packet to target
		byte[] w_bRet = SendPackage(w_abyData);

		if (w_bRet == null)
			return ERR_CONNECTION;

		if (GetRetCode(w_bRet) != ERR_SUCCESS)
			return GetRetCode(w_bRet);

		// Sleep(10);

		// . Assemble data packet
		w_abyData[0] = LOBYTE((short) p_nRamBufferID);
		w_abyData[1] = HIBYTE((short) p_nRamBufferID);
		// memcpy(&w_abyData[2], p_pbyTemplate, GD_RECORD_SIZE);
		System.arraycopy(p_pbyTemplate, 0, w_abyData, 2, GD_RECORD_SIZE);

		InitCmdDataPacket(CMD_DOWN_CHAR, m_bySrcDeviceID, m_byDstDeviceID,
				w_abyData, GD_RECORD_SIZE + 2);

		// . Send data packet to target
		w_bRet = SendPackage(w_abyData);

		if (w_bRet == null)
			return ERR_CONNECTION;

		return GetRetCode(w_bRet);
	}

	/************************************************************************/
	/************************************************************************/
	public int Run_DelChar(int p_nSTmplNo, int p_nETmplNo) {
		byte[] w_abyData = new byte[4];

		w_abyData[0] = LOBYTE((short) p_nSTmplNo);
		w_abyData[1] = HIBYTE((short) p_nSTmplNo);
		w_abyData[2] = LOBYTE((short) p_nETmplNo);
		w_abyData[3] = HIBYTE((short) p_nETmplNo);

		// . Assemble command packet
		w_abyData=InitCmdPacket(CMD_DEL_CHAR, m_bySrcDeviceID, m_byDstDeviceID,w_abyData,
				 4);

		byte[] w_bRet = SendPackage(w_abyData);

		if (w_bRet == null)
			return ERR_CONNECTION;

		return GetRetCode(w_bRet);
	}

	/************************************************************************/
	/************************************************************************/
	public int Run_GetEmptyID(int p_nSTmplNo, int p_nETmplNo, int[] p_pnEmptyID) {
		byte[] w_abyData = new byte[4];

		w_abyData[0] = LOBYTE((short) p_nSTmplNo);
		w_abyData[1] = HIBYTE((short) p_nSTmplNo);
		w_abyData[2] = LOBYTE((short) p_nETmplNo);
		w_abyData[3] = HIBYTE((short) p_nETmplNo);

		// . Assemble command packet
		w_abyData=InitCmdPacket(CMD_GET_EMPTY_ID, m_bySrcDeviceID, m_byDstDeviceID,w_abyData,
				 4);

		byte[] w_bRet = SendPackage(w_abyData);

		if (w_bRet == null)
			return ERR_CONNECTION;

		if (GetRetCode(w_bRet) != ERR_SUCCESS)
			return GetRetCode(w_bRet);

		p_pnEmptyID[0] = MAKEWORD(w_bRet[RCM_DATA_OFFSET + 0],
				w_bRet[RCM_DATA_OFFSET + 1]);

		return ERR_SUCCESS;
	}

	/************************************************************************/
	/************************************************************************/
	public int Run_GetStatus(int p_nTmplNo, int[] p_pnStatus) {
		byte[] w_abyData = new byte[2];

		w_abyData[0] = LOBYTE((short) p_nTmplNo);
		w_abyData[1] = HIBYTE((short) p_nTmplNo);

		w_abyData=InitCmdPacket(CMD_GET_STATUS, m_bySrcDeviceID, m_byDstDeviceID,w_abyData,
				 2);

		byte[] w_bRet = SendPackage(w_abyData);

		if (w_bRet == null)
			return ERR_CONNECTION;

		short ss=GetRetCode(w_bRet);
		if (GetRetCode(w_bRet) != ERR_SUCCESS)
			return GetRetCode(w_bRet);

		p_pnStatus[0] = w_bRet[RCM_DATA_OFFSET + 0];

		return ERR_SUCCESS;
	}

	/************************************************************************/
	/************************************************************************/
	public int Run_GetBrokenID(int p_nSTmplNo, int p_nETmplNo, int[] p_pnCount,
			int[] p_pnFirstID) {
		byte[] w_abyData = new byte[4];

		w_abyData[0] = LOBYTE((short) p_nSTmplNo);
		w_abyData[1] = HIBYTE((short) p_nSTmplNo);
		w_abyData[2] = LOBYTE((short) p_nETmplNo);
		w_abyData[3] = HIBYTE((short) p_nETmplNo);

		// . Assemble command packet
		w_abyData=InitCmdPacket(CMD_GET_BROKEN_ID, m_bySrcDeviceID, m_byDstDeviceID,w_abyData,
				 4);

		byte[] w_bRet = SendPackage(w_abyData);

		if (w_bRet == null)
			return ERR_CONNECTION;

		if (GetRetCode(w_bRet) != ERR_SUCCESS)
			return GetRetCode(w_bRet);

		p_pnCount[0] = MAKEWORD(w_bRet[RCM_DATA_OFFSET + 0],
				w_bRet[RCM_DATA_OFFSET + 1]);
		p_pnFirstID[0] = MAKEWORD(w_bRet[RCM_DATA_OFFSET + 2],
				w_bRet[RCM_DATA_OFFSET + 3]);

		return ERR_SUCCESS;
	}

	/************************************************************************/
	/************************************************************************/
	public int Run_GetEnrollCount(int p_nSTmplNo, int p_nETmplNo,
			int[] p_pnEnrollCount) {
		byte[] w_abyData = new byte[4];

		w_abyData[0] = LOBYTE((short) p_nSTmplNo);
		w_abyData[1] = HIBYTE((short) p_nSTmplNo);
		w_abyData[2] = LOBYTE((short) p_nETmplNo);
		w_abyData[3] = HIBYTE((short) p_nETmplNo);

		// . Assemble command packet
		w_abyData=InitCmdPacket(CMD_GET_ENROLL_COUNT, m_bySrcDeviceID, m_byDstDeviceID,w_abyData,
				 4);

		byte[] w_bRet = SendPackage(w_abyData);

		if (w_bRet == null)
			return ERR_CONNECTION;

		if (GetRetCode(w_bRet) != ERR_SUCCESS)
			return GetRetCode(w_bRet);

		p_pnEnrollCount[0] = MAKEWORD(w_bRet[RCM_DATA_OFFSET + 0],
				w_bRet[RCM_DATA_OFFSET + 1]);

		return ERR_SUCCESS;
	}

	/************************************************************************/
	/************************************************************************/
	public int Run_Generate(int p_nRamBufferID) {
		byte[] w_abyData = new byte[2];

		w_abyData[0] = LOBYTE((short) p_nRamBufferID);
		w_abyData[1] = HIBYTE((short) p_nRamBufferID);

		w_abyData=InitCmdPacket(CMD_GENERATE, m_bySrcDeviceID, m_byDstDeviceID,w_abyData,
				 2);

		// . Send command packet to target
		byte[] w_bRet = SendPackage(w_abyData);

		if (w_bRet == null)
			return ERR_CONNECTION;

		return GetRetCode(w_bRet);
	}

	/************************************************************************/
	/************************************************************************/
	public int Run_Merge(int p_nRamBufferID, int p_nMergeCount) {
		byte[] w_abyData = new byte[3];

		w_abyData[0] = LOBYTE((short) p_nRamBufferID);
		w_abyData[1] = HIBYTE((short) p_nRamBufferID);
		w_abyData[2] = (byte) p_nMergeCount;

		w_abyData=InitCmdPacket(CMD_MERGE, m_bySrcDeviceID, m_byDstDeviceID,w_abyData, 3);

		byte[] w_bRet = SendPackage(w_abyData);

		if (w_bRet == null)
			return ERR_CONNECTION;

		return GetRetCode(w_bRet);
	}

	/************************************************************************/
	/************************************************************************/
	public int Run_Match(int p_nRamBufferID0, int p_nRamBufferID1) {
		byte[] w_abyData = new byte[4];

		w_abyData[0] = LOBYTE((short) p_nRamBufferID0);
		w_abyData[1] = HIBYTE((short) p_nRamBufferID0);
		w_abyData[2] = LOBYTE((short) p_nRamBufferID1);
		w_abyData[3] = HIBYTE((short) p_nRamBufferID1);

		w_abyData=InitCmdPacket(CMD_MATCH, m_bySrcDeviceID, m_byDstDeviceID,w_abyData, 4);

		byte[] w_bRet = SendPackage(w_abyData);

		if (w_bRet == null)
			return ERR_CONNECTION;

		return GetRetCode(w_bRet);
	}

	/************************************************************************/
	/************************************************************************/
	public int Run_Search(int p_nRamBufferID, int p_nStartID,
			int p_nSearchCount, int[] p_pnTmplNo, int[] p_pnLearnResult) {
		byte[] w_abyData = new byte[6];

		w_abyData[0] = LOBYTE((short) p_nRamBufferID);
		w_abyData[1] = HIBYTE((short) p_nRamBufferID);
		w_abyData[2] = LOBYTE((short) p_nStartID);
		w_abyData[3] = HIBYTE((short) p_nStartID);
		w_abyData[4] = LOBYTE((short) p_nSearchCount);
		w_abyData[5] = HIBYTE((short) p_nSearchCount);

		w_abyData=InitCmdPacket(CMD_SEARCH, m_bySrcDeviceID, m_byDstDeviceID,w_abyData, 6);

		// . Send command packet to target
		byte[] w_bRet = SendPackage(w_abyData);

		if (w_bRet == null)
			return ERR_CONNECTION;

		if (GetRetCode(w_bRet) != ERR_SUCCESS)
			return GetRetCode(w_bRet);

		p_pnTmplNo[0] = MAKEWORD(w_bRet[RCM_DATA_OFFSET + 0],
				w_bRet[RCM_DATA_OFFSET + 1]);
		p_pnLearnResult[0] = w_bRet[RCM_DATA_OFFSET + 2];

		return GetRetCode(w_bRet);
	}

	/************************************************************************/
	/************************************************************************/
	public int Run_Verify(int p_nTmplNo, int p_nRamBufferID,
			int[] p_pnLearnResult) {
		byte[] w_abyData = new byte[4];

		w_abyData[0] = LOBYTE((short) p_nTmplNo);
		w_abyData[1] = HIBYTE((short) p_nTmplNo);
		w_abyData[2] = LOBYTE((short) p_nRamBufferID);
		w_abyData[3] = HIBYTE((short) p_nRamBufferID);

		// . Assemble command packet
		w_abyData=InitCmdPacket(CMD_VERIFY, m_bySrcDeviceID, m_byDstDeviceID,w_abyData, 4);

		byte[] w_bRet = SendPackage(w_abyData);

		if (w_bRet == null)
			return ERR_CONNECTION;

		p_pnLearnResult[0] = w_bRet[RCM_DATA_OFFSET + 2];

		return GetRetCode(w_bRet);
	}

	public boolean GetDeviceInformation(String[] deviceInfo) {
		int[] w_nRecvLen = new int[1];
		byte[] w_abyPCCmd = new byte[6];
		byte[] w_abyData;

		String w_strTmp;
		boolean w_bRet;

		Arrays.fill(w_abyPCCmd, (byte) 0);

		w_abyPCCmd[2] = 0x04;

		w_abyData = SendPackage(w_abyPCCmd);

		// Toast.makeText(mApplicationContext,
		// "GetDeviceInformation, SendPackage ret = " + w_bRet,
		// Toast.LENGTH_SHORT).show();

		if (w_abyData == null) {
			return false;
		}

		w_bRet = RecvPackage(w_abyData, w_nRecvLen);

		// Toast.makeText(mApplicationContext,
		// "GetDeviceInformation, RecvPackage : " + w_bRet,
		// Toast.LENGTH_SHORT).show();

		if (!w_bRet) {
			return false;
		}

		w_strTmp = new String(w_abyData);
		deviceInfo[0] = w_strTmp;

		// Toast.makeText(mApplicationContext,
		// "GetDeviceInformation, Recv Data : " + w_strTmp,
		// Toast.LENGTH_SHORT).show();

		return true;
	}

	private byte[] SendPackage(byte[] pPCCmd) {
		//int nDataLen;

		//pPCCmd[0] = (byte) 0xEF;
		//pPCCmd[1] = 0x01;

		//nDataLen = (int) ((int) ((pPCCmd[5] << 8) & 0x0000FF00) | (int) (pPCCmd[4] & 0x000000FF));

		return serialport.SendPackage(pPCCmd);
	}

	private boolean RecvPackage(byte[] pData, int[] pLevRen) {
		int w_nLen;
		byte[] w_abyPCCmd = new byte[6];
		byte[] w_abyRespond;

		w_abyPCCmd[0] = (byte) 0xEF;
		w_abyPCCmd[1] = 0x02;
		w_abyPCCmd[2] = 0;
		w_abyPCCmd[3] = 0;
		w_abyPCCmd[4] = 0;
		w_abyPCCmd[5] = 0;

		// receive status
		w_abyRespond = serialport.SendPackage(w_abyPCCmd);

		if (w_abyRespond == null)
			return false;

		// receive data
		// w_nLen = (int)((w_abyRespond[3] << 8) | w_abyRespond[2]);
		w_nLen = (int) ((int) ((w_abyRespond[3] << 8) & 0x0000FF00) | (int) (w_abyRespond[2] & 0x000000FF));

		if (w_nLen > 0) {
			// w_nTime = SystemClock.elapsedRealtime();

			w_abyPCCmd[1] = 0x03;
			pData = serialport.SendPackage(w_abyPCCmd);

			// w_nTime = SystemClock.elapsedRealtime() - w_nTime;

			if (pData == null)
				return false;

			pLevRen[0] = w_nLen;
		}

		return true;
	}

	/***************************************************************************
	 * Get
	 ***************************************************************************/
	private short GetRetCode(byte[] buffer) {
		if(buffer.length<10)
		{
			return -1;
		}
		return (short) ((int) ((buffer[9] << 8) & 0x0000FF00) | (int) (buffer[8] & 0x000000FF));
	}

	/***************************************************************************
	 * Get 2bytes packet checksum(pDataPkt[0] + pDataPkt[1] + ....)
	 ***************************************************************************/
	private short CalcChkSumOfPkt(byte[] pDataPkt, int nSize) {
		int i, nChkSum = 0;

		for (i = 0; i < nSize; i++) {
			if ((int) pDataPkt[i] < 0)
				nChkSum = nChkSum + ((int) pDataPkt[i] + 256);
			else
				nChkSum = nChkSum + pDataPkt[i];
		}

		return (short) nChkSum;
	}

	/***************************************************************************
	 * Make Command Packet
	 ***************************************************************************/
	public byte[] InitCmdPacket(short wCMDCode, byte bySrcDeviceID, byte byDstDeviceID, byte[] pbyData, int nDataLen){
		short w_wCheckSum;
		byte[] m_abyPacket = new byte[CMD_PACKET_LEN];

		// g_pCmdPacket->m_wPrefix = CMD_PREFIX_CODE;
		m_abyPacket[0] = (byte) (CMD_PREFIX_CODE & 0xFF);
		m_abyPacket[1] = (byte) ((CMD_PREFIX_CODE >> 8) & 0xFF);

		// g_pCmdPacket->m_bySrcDeviceID = p_bySrcDeviceID;
		m_abyPacket[2] = bySrcDeviceID;

		// g_pCmdPacket->m_byDstDeviceID = p_byDstDeviceID;
		m_abyPacket[3] = byDstDeviceID;

		// g_pCmdPacket->m_wCMDCode = p_wCMDCode;
		m_abyPacket[4] = (byte) (wCMDCode & 0xFF);
		m_abyPacket[5] = (byte) ((wCMDCode >> 8) & 0xFF);

		// g_pCmdPacket->m_wDataLen = p_wDataLen;
		m_abyPacket[6] = (byte) (nDataLen & 0xFF);
		m_abyPacket[7] = (byte) ((nDataLen >> 8) & 0xFF);

		
		if (nDataLen > 0)
    		//memcpy(g_pCmdPacket->m_abyData, p_pbyData, wDataLen);
    		System.arraycopy(pbyData, 0, m_abyPacket, 8, nDataLen);


		w_wCheckSum = CalcChkSumOfPkt(m_abyPacket, CMD_PACKET_LEN - 2);

		// g_pCmdPacket->m_wCheckSum = w_wCheckSum;
		m_abyPacket[24] = (byte) (w_wCheckSum & 0xFF);
		m_abyPacket[25] = (byte) ((w_wCheckSum >> 8) & 0xFF);

		m_nPacketSize = CMD_PACKET_LEN;
		return m_abyPacket;
	}

	/***************************************************************************
	 * Make Data Packet
	 ***************************************************************************/
	public byte[] InitCmdDataPacket(short wCMDCode, byte bySrcDeviceID,
			byte byDstDeviceID, byte[] pbyData, int nDataLen) {
		short w_wCheckSum;

		byte[] m_abyPacket = new byte[CMD_PACKET_LEN];
		// g_pCmdPacket->m_wPrefix = CMD_DATA_PREFIX_CODE;
		m_abyPacket[0] = (byte) (CMD_DATA_PREFIX_CODE & 0xFF);
		m_abyPacket[1] = (byte) ((CMD_DATA_PREFIX_CODE >> 8) & 0xFF);

		// g_pCmdPacket->m_bySrcDeviceID = p_bySrcDeviceID;
		m_abyPacket[2] = bySrcDeviceID;

		// g_pCmdPacket->m_byDstDeviceID = p_byDstDeviceID;
		m_abyPacket[3] = byDstDeviceID;

		// g_pCmdPacket->m_wCMDCode = p_wCMDCode;
		m_abyPacket[4] = (byte) (wCMDCode & 0xFF);
		m_abyPacket[5] = (byte) ((wCMDCode >> 8) & 0xFF);

		// g_pCmdPacket->m_wDataLen = p_wDataLen;
		m_abyPacket[6] = (byte) (nDataLen & 0xFF);
		m_abyPacket[7] = (byte) ((nDataLen >> 8) & 0xFF);

		// memcpy(&g_pCmdPacket->m_abyData[0], p_pbyData, p_wDataLen);
		System.arraycopy(pbyData, 0, m_abyPacket, 8, nDataLen);

		// . Set checksum
		w_wCheckSum = CalcChkSumOfPkt(m_abyPacket, nDataLen + 8);

		m_abyPacket[nDataLen + 8] = (byte) (w_wCheckSum & 0xFF);
		m_abyPacket[nDataLen + 9] = (byte) ((w_wCheckSum >> 8) & 0xFF);

		m_nPacketSize = nDataLen + 10;
		return m_abyPacket;
	}

	/***************************************************************************
	 * Check Packet
	 ***************************************************************************/
	public boolean CheckReceive(byte[] pbyPacket, int nPacketLen,
			short wPrefix, short wCMDCode) {
		short w_wCalcCheckSum, w_wCheckSum, w_wTmp;

		// . Check prefix code
		w_wTmp = (short) ((int) ((pbyPacket[1] << 8) & 0x0000FF00) | (int) (pbyPacket[0] & 0x000000FF));

		if (wPrefix != w_wTmp) {
			// DebugManage.WriteLog2(String.format("CheckReceive error1, wPrefix=%d, w_wTmp=%d",
			// wPrefix, w_wTmp));
			return false;
		}

		// . Check checksum
		w_wCheckSum = (short) ((int) ((pbyPacket[nPacketLen - 1] << 8) & 0x0000FF00) | (int) (pbyPacket[nPacketLen - 2] & 0x000000FF));

		w_wCalcCheckSum = CalcChkSumOfPkt(pbyPacket, nPacketLen - 2);

		if (w_wCheckSum != w_wCalcCheckSum) {
			// DebugManage.WriteLog2(String.format("CheckReceive error2, w_wCheckSum=%d, w_wCalcCheckSum=%d",
			// w_wCheckSum, w_wCalcCheckSum));
			return false;
		}

		// . Check Command Code
		w_wTmp = (short) ((int) ((pbyPacket[5] << 8) & 0x0000FF00) | (int) (pbyPacket[4] & 0x000000FF));
		if (wCMDCode != w_wTmp) {
			// DebugManage.WriteLog2(String.format("CheckReceive error3, wCMDCode=%d, w_wCalcCheckSum=%d",
			// wCMDCode, w_wTmp));
			return false;
		}

		return true;
	}

	private boolean USB_ReceiveAck(short wCMD) {
		int c, w_nLen, w_nReadCount = 0;
		byte[] btCDB = new byte[8];
		byte[] w_abyWaitPacket = new byte[CMD_PACKET_LEN];

		byte[] m_abyPacket = new byte[CMD_PACKET_LEN];
		Arrays.fill(btCDB, (byte) 0);

		// w_nReadCount = GetReadWaitTime(p_byCMD);

		c = 0;
		Arrays.fill(w_abyWaitPacket, (byte) 0xAF);

		do {
			Arrays.fill(m_abyPacket, (byte) 0);

			btCDB[0] = (byte) 0xEF;
			btCDB[1] = (byte) 0x12;

			w_nLen = RCM_PACKET_LEN;

			if (serialport.Write(btCDB)) {
				return false;
			}

			SystemClock.sleep(COMM_SLEEP_TIME);

			c++;

			// if ( c > w_nReadCount)
			// {
			// return false;
			// }
		} while (memcmp(m_abyPacket, w_abyWaitPacket, CMD_PACKET_LEN) == true);

		m_nPacketSize = w_nLen;

		if (!CheckReceive(m_abyPacket, m_nPacketSize, (short) RCM_PREFIX_CODE,
				wCMD))
			return false;

		return true;
	}

	/***************************************************************************/
	/**************************************************************************
	 * public boolean USB_ReceiveDataAck(short wCMD) { byte[] btCDB = new
	 * byte[8]; byte[] w_WaitPacket = new byte[10]; byte[] m_abyPacket=new
	 * byte[CMD_PACKET_LEN]; int w_nLen;
	 * 
	 * memset(btCDB, (byte) 0, 8); memset(w_WaitPacket, (byte) 0xAF, 10);
	 * 
	 * do { btCDB[0] = (byte) 0xEF; btCDB[1] = 0x15; w_nLen = 8;
	 * 
	 * if (!serialport.Write(btCDB)) { return false; }
	 * 
	 * SystemClock.sleep(COMM_SLEEP_TIME);
	 * 
	 * } while (memcmp(m_abyPacket, w_WaitPacket, 8) == true);
	 * 
	 * // w_nLen = g_pRcmPacket->m_wDataLen + 2; w_nLen = (short) ((int)
	 * ((m_abyPacket[7] << 8) & 0x0000FF00) | (int) (m_abyPacket[6] &
	 * 0x000000FF)) + 2;
	 * 
	 * byte[] bt=SendPackage(pPCCmd);
	 * 
	 * System.arraycopy(bt, 0, m_abyPacket, 8, w_nLen);
	 * 
	 * m_nPacketSize = 8 + w_nLen;
	 * 
	 * if (!CheckReceive(m_abyPacket, m_nPacketSize, (short)
	 * RCM_DATA_PREFIX_CODE, wCMD)) { return false; }
	 * 
	 * return true; }
	 */
	/***************************************************************************/
	/***************************************************************************/
	public boolean USB_SendDataPacket(short wCMD) {
		byte[] btCDB = new byte[8];

		memset(btCDB, (byte) 0, 8);

		btCDB[0] = (byte) 0xEF;
		btCDB[1] = 0x13;

		btCDB[4] = (byte) (m_nPacketSize & 0xFF);
		btCDB[5] = (byte) ((m_nPacketSize >> 8) & 0xFF);

		if (!serialport.Write(btCDB))
			return false;

		return true;
	}

	/***************************************************************************/
	/*************************************************************************
	 * boolean USB_ReceiveDataPacket(short wCMD) { return
	 * USB_ReceiveDataAck(wCMD); }
	 **/
	/***************************************************************************/
	/***************************************************************************/
	public boolean USB_ReceiveRawData(byte[] pBuffer, int nDataLen) {
		byte[] btCDB = new byte[8];

		memset(btCDB, (byte) 0, 8);

		btCDB[0] = (byte) 0xEF;
		btCDB[1] = 0x14;

		if (!serialport.Write(btCDB))
			return false;

		return true;
	}

	/***************************************************************************/
	/***************************************************************************/
	public boolean USB_ReceiveImage(byte[] p_pBuffer, int nDataLen) {
		byte[] btCDB = new byte[8];
		byte[] w_WaitPacket = new byte[8];

		memset(btCDB, (byte) 0, 8);
		memset(w_WaitPacket, (byte) 0xAF, 8);

		if (nDataLen < 1024 * 64) {
			btCDB[0] = (byte) 0xEF;
			btCDB[1] = 0x16;

			if (!serialport.Write(btCDB))
				return false;
		} else if (nDataLen == 256 * 288) {
			btCDB[0] = (byte) 0xEF;
			btCDB[1] = 0x16;
			btCDB[2] = 0x00;

			if (!serialport.Write(btCDB))
				return false;

			btCDB[0] = (byte) 0xEF;
			btCDB[1] = 0x16;
			btCDB[2] = 0x01;

			if (!serialport.Write(btCDB))
				return false;

		}

		return true;
	}

	private boolean memcmp(byte[] p1, byte[] p2, int nLen) {
		int i;

		for (i = 0; i < nLen; i++) {
			if (p1[i] != p2[i])
				return false;
		}

		return true;
	}

	private void memset(byte[] p1, byte nValue, int nLen) {
		Arrays.fill(p1, 0, nLen, nValue);
	}

	private void memcpy(byte[] p1, byte nValue, int nLen) {
		Arrays.fill(p1, 0, nLen, nValue);
	}

	private short MAKEWORD(byte low, byte high) {
		short s;
		s = (short) ((int) ((high << 8) & 0x0000FF00) | (int) (low & 0x000000FF));
		return s;
	}

	private byte LOBYTE(short s) {
		return (byte) (s & 0xFF);
	}

	private byte HIBYTE(short s) {
		return (byte) ((s >> 8) & 0xFF);
	}
}

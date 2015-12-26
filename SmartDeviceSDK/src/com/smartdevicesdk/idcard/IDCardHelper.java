package com.smartdevicesdk.idcard;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

import com.synjones.bluetooth.DecodeWlt;

import android.serialport.api.SerialPort;
import android.util.Log;

public class IDCardHelper {
	private static final String TAG = "IDCardHelper";
	SerialPort serialport;
	private FileInputStream mFileInputStream;
	private FileOutputStream mFileOutputStream;
	private final long Tresponse = 1000;
	private IDCard idcard = new IDCard();
	private byte[] basemsg = null;

	public IDCardHelper(String device, int baudrate) {
		serialport = new SerialPort();
		if (serialport.open(device, baudrate)) {
			mFileInputStream = serialport.mFileInputStream;
			mFileOutputStream = serialport.mFileOutputStream;
			setMaxRFByte((byte) 0x50);
		} else {
			Log.e(TAG, "can not open device");
		}
	}

	public int decodeImage(String wltPath, String bmpPath) {
		DecodeWlt dw = new DecodeWlt();
		int result = dw.Wlt2Bmp(wltPath, bmpPath);
		return result;
	}

	public void close() {
		try {
			if (mFileInputStream != null) {
				mFileInputStream.close();
			}
			if (mFileOutputStream != null) {
				mFileOutputStream.close();
			}
			if (serialport != null && serialport.isOpen) {
				serialport.closePort();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private byte xorchk(byte[] b, int offset, int length) {
		byte chk = 0;
		int i;
		for (i = 0; i < length; i++) {
			chk ^= b[offset + i];
		}
		return chk;
	}

	private byte[] commandReader(byte[] cmd, long timeout) {
		byte[] recv = new byte[7], recvl = null;
		long TickCount;
		int recvlen = 0;

		try {
			// Log.i("串口调试", "Enter commandReader");
			mFileOutputStream.write(cmd);
			TickCount = System.currentTimeMillis();
			while (mFileInputStream.available() < 7
					&& (System.currentTimeMillis() - TickCount) < timeout)
				Thread.sleep(10);
			// Log.i("串口调试", "available="+mFileInputStream.available());
			if (mFileInputStream.available() < 10) {
				while (mFileInputStream.available() > 0)
					mFileInputStream.read();
				return null;
			}
			// Log.i("串口调试", "available="+mFileInputStream.available());
			if (mFileInputStream.read(recv) != recv.length
					|| recv[0] != (byte) 0xAA || recv[1] != (byte) 0xAA
					|| recv[2] != (byte) 0xAA || recv[3] != (byte) 0x96
					|| recv[4] != (byte) 0x69) {
				while (mFileInputStream.available() > 0)
					mFileInputStream.read();
				return null;
			}

			recvlen = recv[5] * 256 + recv[6];
			// Log.i("串口调试", "bytes are availabled! recvlen="+recvlen);
			while (mFileInputStream.available() < recvlen
					&& (System.currentTimeMillis() - TickCount) < timeout)
				Thread.sleep(10);
			if (mFileInputStream.available() < recvlen || recvlen < 4) {
				while (mFileInputStream.available() > 0)
					mFileInputStream.read();
				return null;
			}
			recvl = new byte[recv.length + recvlen];
			System.arraycopy(recv, 0, recvl, 0, recv.length);
			if (mFileInputStream.read(recvl, recv.length, recvlen) != recvlen) {
				while (mFileInputStream.available() > 0)
					mFileInputStream.read();
				return null;
			}

			/*
			 * if (recvlen <64) { String dbgmsg = "cmd=" +
			 * Integer.toHexString(cmd[7]) + " " + Integer.toHexString(cmd[8]) +
			 * ","; for (int i=0;i<recvl.length;i++) dbgmsg +=
			 * Integer.toHexString(recvl[i]&0x00FF) + " "; Log.i("串口调试",
			 * dbgmsg); }
			 */

			while (mFileInputStream.available() > 0)
				mFileInputStream.read();

			if (xorchk(recvl, 5, recvl.length - 5) != 0)
				return null;
		} catch (IOException ioe) {
			recvl = null;
		} catch (NullPointerException npe) {
			recvl = null;
		} catch (InterruptedException ie) {
			recvl = null;
		}

		return recvl;
	}

	public byte resetSAM() {
		byte Status = 0;
		byte[] cmd = { (byte) 0xAA, (byte) 0xAA, (byte) 0xAA, (byte) 0x96,
				(byte) 0x69, (byte) 0x00, (byte) 0x03, (byte) 0x10,
				(byte) 0xFF, (byte) 0xEC };
		byte[] recvl = commandReader(cmd, Tresponse);
		if (recvl == null)
			Status = 1;
		else if (recvl[7] != 0x00 || recvl[8] != 0x00 || recvl[9] != 0x90)
			Status = 2;
		return Status;
	}

	public byte setMaxRFByte(byte max) {
		byte Status = 0;
		byte[] cmd = { (byte) 0xAA, (byte) 0xAA, (byte) 0xAA, (byte) 0x96,
				(byte) 0x69, (byte) 0x00, (byte) 0x04, (byte) 0x61,
				(byte) 0xFF, (byte) 0x50, (byte) 0xCA };
		cmd[9] = max;
		byte[] recvl = commandReader(cmd, Tresponse);
		if (recvl == null)
			Status = 1;
		else if (recvl[7] != 0x00 || recvl[8] != 0x00 || recvl[9] != 0x90)
			Status = 2;
		return Status;
	}

	public byte getSAMStatus() {
		byte Status = 0;
		byte[] cmd = { (byte) 0xAA, (byte) 0xAA, (byte) 0xAA, (byte) 0x96,
				(byte) 0x69, (byte) 0x00, (byte) 0x03, (byte) 0x11,
				(byte) 0xFF, (byte) 0xED };
		byte[] recvl = commandReader(cmd, Tresponse);
		if (recvl == null)
			Status = 1;
		else if (recvl[7] != 0x00 || recvl[8] != 0x00 || recvl[9] != 0x90)
			Status = 2;
		return Status;
	}

	public byte getSAMID(byte[] samid) {
		byte Status = 0;
		byte[] cmd = { (byte) 0xAA, (byte) 0xAA, (byte) 0xAA, (byte) 0x96,
				(byte) 0x69, (byte) 0x00, (byte) 0x03, (byte) 0x12,
				(byte) 0xFF, (byte) 0xFE };
		byte[] recvl = commandReader(cmd, Tresponse);
		if (recvl == null)
			Status = 1;
		else if (recvl[7] != 0x00 || recvl[8] != 0x00 || recvl[9] != 0x90)
			Status = 2;
		else if (recvl.length < 27)
			Status = 3;
		else {
			samid = new byte[16];
			System.arraycopy(recvl, 10, samid, 0, samid.length);
		}
		return Status;
	}

	public byte startFindIDCard() {
		byte Status = 0;
		byte[] cmd = { (byte) 0xAA, (byte) 0xAA, (byte) 0xAA, (byte) 0x96,
				(byte) 0x69, (byte) 0x00, (byte) 0x03, (byte) 0x20,
				(byte) 0x01, (byte) 0x22 };
		byte[] recvl = commandReader(cmd, Tresponse);
		if (recvl == null)
			Status = 1;
		else if (recvl[7] != 0x00 || recvl[8] != 0x00
				|| (recvl[9] != (byte) 0x9F && recvl[9] != (byte) 0x80)) {
			Status = 2;
			idcard.SW1 = recvl[7];
			idcard.SW2 = recvl[8];
			idcard.SW3 = recvl[9];
		}
		// Log.i("串口调试", "startFindCard="+Status);
		return Status;
	}

	public byte selectIDCard() {
		byte Status = 0;
		byte[] cmd = { (byte) 0xAA, (byte) 0xAA, (byte) 0xAA, (byte) 0x96,
				(byte) 0x69, (byte) 0x00, (byte) 0x03, (byte) 0x20,
				(byte) 0x02, (byte) 0x21 };
		byte[] recvl = commandReader(cmd, Tresponse);
		if (recvl == null)
			Status = 1;
		else if (recvl[7] != 0x00 || recvl[8] != 0x00
				|| (recvl[9] != (byte) 0x90 && recvl[9] != (byte) 0x81)) {
			Status = 2;
			idcard.SW1 = recvl[7];
			idcard.SW2 = recvl[8];
			idcard.SW3 = recvl[9];
		}
		// Log.i("串口调试", "selectIDCard="+Status);
		return Status;
	}

	public byte readBaseMsg() {
		byte Status = 0;
		byte[] cmd = { (byte) 0xAA, (byte) 0xAA, (byte) 0xAA, (byte) 0x96,
				(byte) 0x69, (byte) 0x00, (byte) 0x03, (byte) 0x30,
				(byte) 0x01, (byte) 0x32 };
		byte[] recvl = commandReader(cmd, Tresponse * 10);
		if (recvl == null)
			Status = 1;
		else if (recvl[7] != 0x00 || recvl[8] != 0x00
				|| recvl[9] != (byte) 0x90) {
			Status = 2;
			idcard.SW1 = recvl[7];
			idcard.SW2 = recvl[8];
			idcard.SW3 = recvl[9];
		} else if (recvl.length < 1295)
			Status = 3;
		else {
			basemsg = new byte[4 + 256 + 1024];
			System.arraycopy(recvl, 10, basemsg, 0, basemsg.length);
			// Log.i("串口调试", "basemsg="+basemsg.length);
		}
		// Log.i("串口调试",
		// "readBaseMsg="+Status+" recvl="+(recvl==null?0:recvl.length));

		return Status;
	}

	public IDCard getIDCard() {
		short textlen, wltlen;
		String dbgmsg = "";
		if (startFindIDCard() == 0x00 && selectIDCard() == 0x00
				&& readBaseMsg() == 0) {
			try {
				textlen = (short) (basemsg[0] * 256 + basemsg[1]);
				wltlen = (short) (basemsg[2] * 256 + basemsg[3]);
				byte[] name = new byte[30];
				System.arraycopy(basemsg, 4, name, 0, name.length);
				idcard.setName(new String(name, "UTF-16LE").trim());
				byte[] sex = new byte[2];
				System.arraycopy(basemsg, 34, sex, 0, sex.length);
				idcard.setSex(new String(sex, "UTF-16LE"));
				if (idcard.getSex().equalsIgnoreCase("1"))
					idcard.setSex("男");
				else
					idcard.setSex("女");
				byte[] nation = new byte[4];
				System.arraycopy(basemsg, 36, nation, 0, nation.length);
				idcard.setNation(idcard.getNationName(new String(nation,
						"UTF-16LE")));
				byte[] birthday = new byte[16];
				System.arraycopy(basemsg, 40, birthday, 0, birthday.length);
				idcard.setBirthday(new String(birthday, "UTF-16LE"));
				byte[] address = new byte[70];
				System.arraycopy(basemsg, 56, address, 0, address.length);
				idcard.setAddress(new String(address, "UTF-16LE").trim());
				byte[] idcardno = new byte[36];
				System.arraycopy(basemsg, 126, idcardno, 0, idcardno.length);
				idcard.setIDCardNo(new String(idcardno, "UTF-16LE"));
				byte[] grantdept = new byte[30];
				System.arraycopy(basemsg, 162, grantdept, 0, grantdept.length);
				idcard.setGrantDept(new String(grantdept, "UTF-16LE").trim());
				byte[] userlifebegin = new byte[16];
				System.arraycopy(basemsg, 192, userlifebegin, 0,
						userlifebegin.length);
				idcard.setUserLifeBegin(new String(userlifebegin, "UTF-16LE"));
				byte[] userlifeend = new byte[16];
				System.arraycopy(basemsg, 208, userlifeend, 0,
						userlifeend.length);
				idcard.setUserLifeEnd(new String(userlifeend, "UTF-16LE")
						.trim());
				byte[] wlt = new byte[1024];
				System.arraycopy(basemsg, textlen + 4, wlt, 0, wlt.length);
				idcard.setWlt(wlt);
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			return null;
		}
		return idcard;
	}
}

package com.smartdevicesdk.fingerprint;

import android.serialport.api.SerialPort;
import android.serialport.api.SerialPortDataReceived;
import android.util.Log;

public class FingerPrintHelper {
	protected static final String TAG = "SerialPortHelper";
	protected SerialPort mSerialPort;
	SerialPortDataReceived serialportDataReceived = null;
	int buffersize=64*1024;
	byte[] btrec = new byte[buffersize];
	int datalen = 0;

	public FingerPrintHelper() {
		mSerialPort = new SerialPort();
		mSerialPort.setOnserialportDataReceived(new SerialPortDataReceived() {
			@Override
			public void onDataReceivedListener(byte[] buffer, int size) {
				if(datalen+size>=btrec.length){
					datalen=0;
					 btrec = new byte[buffersize];
				}
				System.arraycopy(buffer, 0, btrec, datalen, size);
				Log.i(TAG,"onDataReceivedListener:size"+size+",data:"+byte2HexStr(buffer));
				datalen += size;
				if (serialportDataReceived != null) {
					serialportDataReceived.onDataReceivedListener(buffer,
							buffer.length);
				}
			}
		});
	}

	public boolean IsOpen() {
		return mSerialPort.isOpen;
	}

	public void setOnserialportDataReceivedPrint(
			SerialPortDataReceived _serialportDataReceived) {
		this.serialportDataReceived = _serialportDataReceived;
	}

	public Boolean OpenSerialPort(String device, int baudrate) {
		try {
			mSerialPort.open(device, baudrate);
		} catch (SecurityException e) {
			Log.e(TAG, e.getMessage());
			return false;
		}
		return true;
	}

	public byte[] SendPackage(byte[] buffer) {
		btrec=new byte[buffersize];
		Write(buffer);
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		long stime = System.currentTimeMillis();
		int datacount=0;
		int samecount=0;
		while (System.currentTimeMillis() - stime < 5000) {
			if(datalen!=datacount){
				samecount=0;
				datacount=datalen;
				continue;
			}else if(datacount!=0&&datalen==datacount){
				samecount++;
			}
			if (samecount>1000) {
				samecount=0;
				byte[] bt=new byte[datalen];
				System.arraycopy(btrec, 0, bt, 0, datalen);
				
				datalen=0;
				btrec=new byte[buffersize];
				
				Log.i(TAG, "SendPackage onDataReceivedListener size:"+bt.length);
				return bt;
			}
		}
		datalen=0;
		btrec=new byte[buffersize];
		return null;
	}

	public Boolean Write(String str) {
		byte[] buffer = str.getBytes();
		return Write(buffer);
	}

	public Boolean Write(byte[] buffer) {
		Log.i(TAG, "SendPackage:"+byte2HexStr(buffer));
		int sendSize = 500;
		if (buffer.length <= sendSize) {
			mSerialPort.Write(buffer);
			return true;
		}
		for (int j = 0; j < buffer.length; j += sendSize) {

			byte[] btPackage = new byte[sendSize];
			if (buffer.length - j < sendSize) {
				btPackage = new byte[buffer.length - j];
			}
			System.arraycopy(buffer, j, btPackage, 0, btPackage.length);
			mSerialPort.Write(btPackage);
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		return true;
	}

	public Boolean CloseSerialPort() {
		if (mSerialPort == null) {
			return true;
		}
		return mSerialPort.closePort();
	}

	public static String byte2HexStr(byte[] b) {
		String stmp = "";
		StringBuilder sb = new StringBuilder("");
		for (int n = 0; n < b.length; n++) {
			stmp = Integer.toHexString(b[n] & 0xFF);
			sb.append((stmp.length() == 1) ? "0" + stmp : stmp);
			sb.append(" ");
		}
		return sb.toString().toUpperCase().trim();
	}

}

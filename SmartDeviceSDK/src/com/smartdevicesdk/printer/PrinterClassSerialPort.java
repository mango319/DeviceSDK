package com.smartdevicesdk.printer;

import java.util.ArrayList;
import java.util.List;

import com.smartdevicesdk.database.DatabaseHelper;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.serialport.api.SerialPort;
import android.serialport.api.SerialPortDataReceived;
import android.util.Log;

public class PrinterClassSerialPort implements PrinterClass {
	private static final String TAG = "PrinterClassSerialPort";
	public SerialPort mSerialPort = null;
	PrintService printservice = new PrintService();
	private Handler mHandler;
	public String device = "/dev/ttyMT0";
	public int baudrate = 115200;// 38400

	boolean iswrite = false;
	boolean canWrite = false;

	DatabaseHelper dbhelper = null;
	
	WriteThread threadWrite=null;

	public PrinterClassSerialPort(Context context, Handler _mHandler) {
		mHandler = _mHandler;
		mSerialPort = new SerialPort();

		dbhelper = new DatabaseHelper(context);

		mSerialPort.setOnserialportDataReceived(new SerialPortDataReceived() {
			@Override
			public void onDataReceivedListener(byte[] buffer, int size) {
				/*
				 * int size = msg.arg1; String dataStr = msg.obj.toString();
				 * byte[] buffer = dataStr.getBytes();
				 */
				Log.i(TAG,
						"get printer recive data:" + byteToString(buffer, size));
				if (size > 0) {
					if (iswrite) {
						if (buffer[0] == 0) {
							canWrite = true;
						} else {
							canWrite = false;
							mSerialPort.Write(new byte[] { 0x0a });
						}
						iswrite = false;
					}
					if (buffer[0] == 0x13) {
						PrintService.isFUll = true;
						Log.i(TAG, "0x13:");
					} else if (buffer[0] == 0x11) {
						PrintService.isFUll = false;
						Log.i(TAG, "0x11:");
					} else if (PrintService.getState) {
						PrintService.getState = false;
						PrintService.printState = buffer[0];
					}
					mHandler.obtainMessage(PrinterClass.MESSAGE_READ, size, -1,
							buffer).sendToTarget();
				}
			}
		});
	}
	
	protected class WriteThread extends Thread {
		@Override
		public void run() {
			super.run();
			while(true){
				Cursor cursor= dbhelper.getAll();
				while (cursor.moveToNext()) {  
					byte[] buffer=cursor.getBlob(cursor.getColumnIndex("bytearray"));
					if(mSerialPort.Write(buffer)){
						dbhelper.delete(cursor.getInt(cursor.getColumnIndex("id")));
					}
				}
				cursor.close();
			}
		}
	}

	private boolean getBufferState(final int sleepTime) {
		PrintService.getState = true;
		for (int i = 0; i < sleepTime / 10; i++) {
			mSerialPort.Write(new byte[] { 0x1b, 0x76 });
			if (PrintService.printState == 0) {
				PrintService.getState = false;
				return true;
			}
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		PrintService.getState = false;
		return false;
	}

	public boolean open() {
		if (mSerialPort.open(device, baudrate)) {
			return true;
		}
		return false;
	}

	public boolean close() {
		return mSerialPort.closePort();
	}

	public boolean write(byte[] buffer) {
		/*
		 * if (getBufferState(10000)) { return mSerialPort.Write(buffer); }
		 * return false;
		 */
		// return mSerialPort.Write(buffer);

		long res = dbhelper.add(buffer);
		if (res > 0) {
			if(threadWrite==null||!threadWrite.isAlive()){
				threadWrite=null;
				threadWrite=new WriteThread();
				threadWrite.start();
			}
			
			return true;
		}
		return false;
	}

	public boolean printText(String textStr) {
		byte[] buffer = printservice.getText(textStr);
		return write(buffer);
	}

	public boolean printImage(Bitmap bitmap) {
		return write(printservice.getImage(bitmap));
	}

	public boolean printUnicode(String textStr) {
		return write(printservice.getTextUnicode(textStr));
	}

	public static String byteToString(byte[] b, int size) {
		byte high, low;
		byte maskHigh = (byte) 0xf0;
		byte maskLow = 0x0f;

		StringBuffer buf = new StringBuffer();

		for (int i = 0; i < size; i++) {
			high = (byte) ((b[i] & maskHigh) >> 4);
			low = (byte) (b[i] & maskLow);
			buf.append(findHex(high));
			buf.append(findHex(low));
			buf.append(" ");
		}

		return buf.toString();
	}

	private static char findHex(byte b) {
		int t = new Byte(b).intValue();
		t = t < 0 ? t + 16 : t;

		if ((0 <= t) && (t <= 9)) {
			return (char) (t + '0');
		}

		return (char) (t - 10 + 'A');
	}

}

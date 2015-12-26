/** 
 *  
 * @author	xuxl
 * @email	leoxuxl@163.com
 * @version  
 *     1.0 2015年12月23日 下午5:16:57 
 */
package com.smartdevicesdk.scanner;

import java.io.UnsupportedEncodingException;

import com.smartdevicesdk.media.TipSound;
import com.smartdevicesdk.utils.HandlerMessage;

import android.content.Context;
import android.content.IntentSender.SendIntentException;
import android.os.Handler;
import android.os.Message;
import android.serialport.api.SerialPort;
import android.serialport.api.SerialPortDataReceived;

/**
 * This class is used for : Scanner
 * 
 * @author xuxl
 * @email leoxuxl@163.com
 * @version 1.0 2015年12月23日 下午5:16:57
 */
public class ScannerHelper {
	SerialPort serialport = null;
	ScanGpio scanGpio = new ScanGpio();
	byte[] btScan = new byte[500];
int totalsize=0;
	public ScannerHelper(final Context context,String device, int baudrate, final Handler handler) {
		serialport = new SerialPort(device, baudrate);
		serialport.open();
		serialport.setOnserialportDataReceived(new SerialPortDataReceived() {
			@Override
			public void onDataReceivedListener(byte[] buffer, int size) {
				System.arraycopy(buffer, 0, btScan, totalsize, size);
				totalsize+=size;
				if (buffer[size - 2] == 10 && buffer[size - 1] ==  13) {
					Message msg = handler.obtainMessage();
					msg.what = HandlerMessage.SCANNER_DATA_MSG;
					try {
						msg.obj = new String(btScan, 0, totalsize, "UTF-8");
					} catch (UnsupportedEncodingException e) {
						e.printStackTrace();
					}
					handler.sendMessage(msg);
					btScan = new byte[500];
					totalsize=0;
					TipSound.playScanSound(context);
				}
			}
		});
		scanGpio.openPower();
	}

	public void Close() {
		scanGpio.closePower();
		serialport.closePort();
		scanGpio.closeScan();
	}

	public void scan() {
		scanGpio.openScan();
		try {
			Thread.sleep(50);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}

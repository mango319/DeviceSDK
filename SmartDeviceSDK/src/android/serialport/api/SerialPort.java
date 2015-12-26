package android.serialport.api;

import java.io.DataOutputStream;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import android.util.Log;

public class SerialPort {

	private static final String TAG = "SerialPort";
	private ReadThread mReadThread;
	private boolean isReving = false;
	private FileDescriptor mFd;
	public FileInputStream mFileInputStream;
	public FileOutputStream mFileOutputStream;
	private byte[] buffer = null;
	private String device = "";
	private int baudrate = 115200;
	/**
	 * serialport open state
	 */
	public boolean isOpen = false;

	public SerialPort(String device, int baudrate) {
		this.device = device;
		this.baudrate = baudrate;
		if (!isOpen) {
			open();
		}
	}

	SerialPortDataReceived serialportDataReceived = null;

	/**
	 * register serialport receive event
	 * 
	 * @param _serialportDataReceived
	 */
	public void setOnserialportDataReceived(
			SerialPortDataReceived _serialportDataReceived) {
		this.serialportDataReceived = _serialportDataReceived;
	}

	public boolean RootCommand(String command) {
		Process process = null;
		DataOutputStream os = null;
		try {
			process = Runtime.getRuntime().exec("su");
			os = new DataOutputStream(process.getOutputStream());
			os.writeBytes(command + "\n");
			os.writeBytes("exit\n");
			os.flush();
			process.waitFor();
		} catch (Exception e) {
			Log.d("*** DEBUG ***", "ROOT REE" + e.getMessage());
			return false;
		} finally {
			try {
				if (os != null) {
					os.close();
				}
				process.destroy();
			} catch (Exception e) {
			}
		}
		return true;
	}

	public boolean open(String device, int baudrate) {
		this.device = device;
		this.baudrate = baudrate;
		if (!isOpen) {
			if (device == "") {
				Log.e(TAG, "serialport device is null");
				return false;
			}
			mFd = open(device, baudrate, 0, SerialPortParam.DataBits,
					SerialPortParam.StopBits, SerialPortParam.Parity,
					SerialPortParam.Flowcontrol);
			if (mFd == null) {
				Log.e(TAG, "can not open device " + device);
				return false;
			}
			mFileInputStream = new FileInputStream(mFd);
			mFileOutputStream = new FileOutputStream(mFd);
			isOpen = true;
			return true;
		} else {
			return true;
		}
	}

	public boolean open() {
		if (device == "") {
			Log.e(TAG, "serialport device is null");
			return false;
		}
		mFd = open(device, baudrate, 0, SerialPortParam.DataBits,
				SerialPortParam.StopBits, SerialPortParam.Parity,
				SerialPortParam.Flowcontrol);
		if (mFd == null) {
			Log.e(TAG, "can not open device " + device);
			return false;
		}
		mFileInputStream = new FileInputStream(mFd);
		mFileOutputStream = new FileOutputStream(mFd);
		mReadThread = new ReadThread();
		isReving = true;
		mReadThread.start();
		isOpen = true;
		return true;
	}

	public boolean closePort() {
		if (mFd != null) {
			try {
				isReving = false;
				if (mReadThread != null) {
					mReadThread.interrupt();
					mReadThread = null;
				}
				close();
				mFd = null;
				mFileInputStream = null;
				mFileOutputStream = null;
				isOpen = false;
			} catch (Exception ex) {
				Log.e(TAG, ex.getMessage());
			}
		}
		return true;
	}

	public boolean Write(byte[] buffer) {
		try {
			if (mFileOutputStream == null) {
				return false;
			}
			int sendSize = 1000;
			if (buffer.length <= sendSize) {
				mFileOutputStream.write(buffer);
				return true;
			}
			for (int j = 0; j < buffer.length; j += sendSize) {
				byte[] btPackage = new byte[sendSize];
				if (buffer.length - j < sendSize) {
					btPackage = new byte[buffer.length - j];
				}
				System.arraycopy(buffer, j, btPackage, 0, btPackage.length);
				mFileOutputStream.write(btPackage);
				Thread.sleep(10);
			}
		} catch (IOException e) {
			Log.e(TAG, e.getMessage());
		} catch (InterruptedException e) {
			Log.e(TAG, e.getMessage());
		}
		return true;
	}

	public boolean Write(String str) {
		byte[] buffer = str.getBytes();
		return Write(buffer);
	}

	public SerialPort() {
		String device = SerialPortParam.Path;
		String cmd = "chmod 777 " + device + "\n" + "exit\n";
		if (RootCommand(cmd)) {
			System.out.println("ok");
		} else {
			System.out.println("no");
		}
	}

	protected class ReadThread extends Thread {
		private static final String TAG = "ReadThread";

		@Override
		public void run() {
			super.run();
			while (isReving) {
				try {
					if (mFileInputStream == null) {
						return;
					}
					if (mFileInputStream.available() > 0) {
						byte[] buffer = new byte[4096];
						int size = mFileInputStream.read(buffer);
						if (size > 0) {
							onDataReceived(buffer, size);
							Log.i(TAG, "Rec Data Size:" + size);
						}
					}
				} catch (IOException e) {
					Log.e(TAG, e.getMessage());
				}
			}
		}
	}

	protected void onDataReceived(final byte[] bufferRec, final int size) {
		if (serialportDataReceived != null) {
			Log.i(TAG, "get received data size" + size);
			if (serialportDataReceived != null) {
				serialportDataReceived.onDataReceivedListener(bufferRec, size);
			} else {
				Log.i(TAG, "serialportDataReceived is null");
			}
		}
	}

	// JNI
	private native static FileDescriptor open(String path, int baudrate,
			int flags, int databits2, int stopbits, int parity2, int flowcontrol);

	private native void close();

	static {
		System.loadLibrary("serial_port");
	}

}

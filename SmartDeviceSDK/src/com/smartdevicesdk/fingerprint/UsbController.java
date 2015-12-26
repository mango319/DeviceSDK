/*
 * UsbController.java
 * This file is part of UsbController
 *
 * Copyright (C) 2012 - Manuel Di Cerbo
 *
 * UsbController is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * UsbController is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with UsbController. If not, see <http://www.gnu.org/licenses/>.
 */
package com.smartdevicesdk.fingerprint;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;


import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbConstants;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbEndpoint;
import android.hardware.usb.UsbInterface;
import android.hardware.usb.UsbManager;
import android.util.Log;
import android.os.SystemClock;
import android.widget.Toast;
//import android.os.

/**
 * (c) Neuxs-Computing GmbH Switzerland
 * @author Manuel Di Cerbo, 02.02.2012
 *
 */


public class UsbController {

	private final Context mApplicationContext;
	private final UsbManager mUsbManager;
	private final int VID;
	private final int PID;
    private int m_nEPInSize, m_nEPOutSize;

    private byte[] m_abyTransferBuf;
    private boolean m_bInit = false;
    private UsbDevice   m_usbDevice;
    private UsbDeviceConnection m_usbConn = null;
    private UsbInterface m_usbIf = null;
    private UsbEndpoint m_epIN = null;
    private UsbEndpoint m_epOUT = null;
    private final IUsbConnState mConnectionHandler;

	protected static final String ACTION_USB_PERMISSION = "ch.serverbox.android.USB";
    
	/**
	 * Activity is needed for onResult
	 * 
	 * @param parentActivity
	 */
	public UsbController(Activity parentActivity, IUsbConnState connectionHandler, int vid, int pid){
        mConnectionHandler = connectionHandler;
		mApplicationContext = parentActivity.getApplicationContext();
		mUsbManager = (UsbManager) mApplicationContext.getSystemService(Context.USB_SERVICE);
		VID = vid;
		PID = pid;
        m_abyTransferBuf = new byte[512];
		//init();
	}

	public void init(){
		enumerate(new IPermissionListener() {
			@Override
			public void onPermissionDenied(UsbDevice d) {
				UsbManager usbman = (UsbManager) mApplicationContext.getSystemService(Context.USB_SERVICE);
				PendingIntent pi = PendingIntent.getBroadcast(mApplicationContext, 0, new Intent(ACTION_USB_PERMISSION), 0);
				mApplicationContext.registerReceiver(mPermissionReceiver, new IntentFilter(ACTION_USB_PERMISSION));
				usbman.requestPermission(d, pi);
			}
		});
	}

    public void uninit(){
        if (m_usbConn != null)
        {
            m_usbConn.releaseInterface(m_usbIf);
            m_usbConn.close();
            m_usbConn = null;
            m_bInit = false;
        }

        //stop();
    }

	public void stop()
    {
		try{
			mApplicationContext.unregisterReceiver(mPermissionReceiver);
		}catch(IllegalArgumentException e){};//bravo
	}

    public boolean IsInit(){
        return m_bInit;
    }

	private void enumerate(IPermissionListener listener) {
		boolean bFound = false;
		l("enumerating");
		HashMap<String, UsbDevice> devlist = mUsbManager.getDeviceList();
		Iterator<UsbDevice> deviter = devlist.values().iterator();

		while (deviter.hasNext()) {
			UsbDevice d = deviter.next();
			l("Found device: " + String.format("%04X:%04X", d.getVendorId(), d.getProductId()));

            Toast.makeText(mApplicationContext, "Found device: " + String.format("%04X:%04X", d.getVendorId(), d.getProductId()), Toast.LENGTH_SHORT).show();
			DebugManage.WriteLog2("Found device: " + String.format("%04X:%04X", d.getVendorId(), d.getProductId()));

            if (d.getVendorId() == VID && d.getProductId() == PID) {
            	bFound = true;
				l("Device under: " + d.getDeviceName());
				if (!mUsbManager.hasPermission(d))
                {
                    Toast.makeText(mApplicationContext, "enumerate, hasPermission return false" , Toast.LENGTH_SHORT).show();
                    listener.onPermissionDenied(d);
                }
				else{
                    Toast.makeText(mApplicationContext, "enumerate, GetConnInerface start" , Toast.LENGTH_SHORT).show();
					//startHandler(d);
                    GetConnInerface(d);
                    //TestComm(d);
					return;
				}
				break;
			}
		}
		if (bFound == false)
		{
			Toast.makeText(mApplicationContext, "no more devices found" , Toast.LENGTH_SHORT).show();
			DebugManage.WriteLog2("no more devices found");
			mConnectionHandler.onDeviceNotFound();
		}        
	}

	private class PermissionReceiver extends BroadcastReceiver {
		private final IPermissionListener mPermissionListener;

		public PermissionReceiver(IPermissionListener permissionListener) {
			mPermissionListener = permissionListener;
		}

		@Override
		public void onReceive(Context context, Intent intent) {
			mApplicationContext.unregisterReceiver(this);
			if (intent.getAction().equals(ACTION_USB_PERMISSION)) {
				if (!intent.getBooleanExtra(
						UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
					mPermissionListener.onPermissionDenied((UsbDevice) intent
							.getParcelableExtra(UsbManager.EXTRA_DEVICE));
					
					mConnectionHandler.onUsbPermissionDenied();
				} else {
					l("Permission granted");
					UsbDevice dev = (UsbDevice) intent
							.getParcelableExtra(UsbManager.EXTRA_DEVICE);
					if (dev != null) {
						if (dev.getVendorId() == VID
								&& dev.getProductId() == PID) {
							//startHandler(dev);// has new thread
                            GetConnInerface(dev);
                            //TestComm(dev);
						}
					} else {
						DebugManage.WriteLog2("device not present!");
                        mConnectionHandler.onDeviceNotFound();
					}
				}
			}
		}
	}

    private void GetConnInerface(UsbDevice dev){
        int n;

        //Toast.makeText(mApplicationContext, "GetConnInerface start", Toast.LENGTH_SHORT).show();

        m_usbDevice = dev;

        m_usbConn = mUsbManager.openDevice(dev);

        n = dev.getInterfaceCount();

        if (n <= 0)
            return;

        if (!m_usbConn.claimInterface(dev.getInterface(0), true)) {
            return;
        }

        m_usbIf = dev.getInterface(0);

        n = m_usbIf.getEndpointCount();

        if (n < 2)
            return;

        for (int i = 0; i < n; i++) {
            if (m_usbIf.getEndpoint(i).getType() == UsbConstants.USB_ENDPOINT_XFER_BULK) {
                if (m_usbIf.getEndpoint(i).getDirection() == UsbConstants.USB_DIR_IN)
                    m_epIN = m_usbIf.getEndpoint(i);
                else
                    m_epOUT = m_usbIf.getEndpoint(i);
            }
        }

        m_nEPInSize = m_epIN.getMaxPacketSize();
        m_nEPOutSize = m_epOUT.getMaxPacketSize();

        m_bInit = true;

        //m_epOUT.getMaxPacketSize();

        //Toast.makeText(mApplicationContext, "GetConnInerface OK, Out Max Size="+m_nEPOutSize+" In Max Size=" + m_nEPInSize, Toast.LENGTH_SHORT).show();

        DebugManage.WriteLog2("device connected");
        mConnectionHandler.onUsbConnected();
    }

    public boolean OperationInternal(byte[] pData, int nDataLen, int nTimeOut, boolean bRead)
    {
        byte[] w_abyTmp = new byte[31];
        byte[] w_abyCSW = new byte[13];
        boolean w_bRet;

        Arrays.fill(w_abyTmp, (byte)0);
        w_abyTmp[0] = 0x55;
        w_abyTmp[1] = 0x53;
        w_abyTmp[2] = 0x42;
        w_abyTmp[3] = 0x43;
        w_abyTmp[4] = 0x28;
        w_abyTmp[5] = 0x2b;
        w_abyTmp[6] = 0x18;
        w_abyTmp[7] = (byte)0x89;
        w_abyTmp[8] = (byte)(nDataLen & 0xFF);
        w_abyTmp[9] = (byte)((nDataLen >> 8)& 0xFF);
        w_abyTmp[10] = (byte)((nDataLen >> 16)& 0xFF);
        w_abyTmp[11] = (byte)((nDataLen >> 24)& 0xFF);

        if(bRead)
            w_abyTmp[12] = (byte)0x80;
        else
            w_abyTmp[12] = 0x00;    //cCBWFlags

        w_abyTmp[13] = 0x00;    //cCBWlun
        w_abyTmp[14] = 0x0a;    //cCBWCBLength

        w_abyTmp[15] = (byte)0xef;
        if (bRead)
            w_abyTmp[16] = (byte)0xff;
        else
            w_abyTmp[16] = (byte)0xfe;

        // send 31bytes
        w_bRet = UsbBulkSend(w_abyTmp, 31, nTimeOut);

        if (!w_bRet)
            return false;

        // read or write real data
        if (bRead)
            w_bRet = UsbBulkReceive(pData, nDataLen, nTimeOut);
        else
            w_bRet = UsbBulkSend(pData, nDataLen, nTimeOut);

        if (!w_bRet)
            return false;

        // receive csw
        w_bRet = UsbBulkReceive(w_abyCSW, 13, nTimeOut);

        return w_bRet;
    }

    public boolean UsbSCSIWrite(byte[] pCDB, int nCDBLen, byte[] pData, int nDataLen, int nTimeOut)
    {
        byte[] w_abyTmp = new byte[31];
        byte[] w_abyCSW = new byte[13];
        boolean w_bRet;

        //Arrays.fill(w_abyTmp, (byte)0);
        w_abyTmp[0] = 0x55;
        w_abyTmp[1] = 0x53;
        w_abyTmp[2] = 0x42;
        w_abyTmp[3] = 0x43;
        w_abyTmp[4] = 0x28;
        w_abyTmp[5] = 0x2b;
        w_abyTmp[6] = 0x18;
        w_abyTmp[7] = (byte)0x89;
        w_abyTmp[8] = 0x00;
        w_abyTmp[9] = 0x00;
        w_abyTmp[10] = 0x00;
        w_abyTmp[11] = 0x00;
        w_abyTmp[12] = 0x00;    //cCBWFlags
        w_abyTmp[13] = 0x00;    //cCBWlun
        w_abyTmp[14] = 0x0a;    //cCBWCBLength

        System.arraycopy(pCDB, 0, w_abyTmp, 15, nCDBLen);
        //System.arraycopy(pData, 0, w_abyTmp, 31, nDataLen);

        w_bRet = UsbBulkSend(w_abyTmp, 31, nTimeOut);

        if (!w_bRet)
            return false;

        w_bRet = UsbBulkSend(pData, nDataLen, nTimeOut);

        if (!w_bRet)
            return false;

        // receive csw
        w_bRet = UsbBulkReceive(w_abyCSW, 13, nTimeOut);

        return w_bRet;
    }

    public boolean UsbSCSIRead(byte[] pCDB, int nCDBLen, byte[] pData, int nDataLen, int nTimeOut)
    {
        long    w_nTime;
        byte[] w_abyTmp = new byte[31];
        byte[] w_abyCSW = new byte[13];
        boolean w_bRet;

        //Arrays.fill(w_abyTmp, (byte)0);
        w_abyTmp[0] = 0x55;
        w_abyTmp[1] = 0x53;
        w_abyTmp[2] = 0x42;
        w_abyTmp[3] = 0x43;
        w_abyTmp[4] = 0x28;
        w_abyTmp[5] = 0x2b;
        w_abyTmp[6] = 0x18;
        w_abyTmp[7] = (byte)0x89;
        w_abyTmp[8] = 0x00;
        w_abyTmp[9] = 0x00;
        w_abyTmp[10] = 0x00;
        w_abyTmp[11] = 0x00;
        w_abyTmp[12] = (byte)0x80;    //cCBWFlags
        w_abyTmp[13] = 0x00;    //cCBWlun
        w_abyTmp[14] = 0x0a;    //cCBWCBLength

        System.arraycopy(pCDB, 0, w_abyTmp, 15, nCDBLen);

        w_bRet = UsbBulkSend(w_abyTmp, 31, nTimeOut);

        if (!w_bRet)
            return false;

        w_nTime = SystemClock.elapsedRealtime();

        w_bRet = UsbBulkReceive(pData, nDataLen, nTimeOut);

        w_nTime = SystemClock.elapsedRealtime() - w_nTime;

        //Toast.makeText(mApplicationContext, "UsbSCSIRead, UsbBulkReceive Time : " + w_nTime , Toast.LENGTH_SHORT).show();

        if (!w_bRet)
            return false;

        // receive csw
        w_bRet = UsbBulkReceive(w_abyCSW, 13, nTimeOut);

        return w_bRet;
    }

    private boolean UsbBulkSend(byte[] pBuf, int nLen, int nTimeOut)
    {
        int i, n, r, w_nRet;
        //byte[] w_abyTmp = new byte[m_nEPOutSize];

        n = nLen / m_nEPOutSize;
        r = nLen % m_nEPOutSize;

        for(i=0; i<n; i++)
        {
            System.arraycopy(pBuf, i*m_nEPOutSize, m_abyTransferBuf, 0, m_nEPOutSize);

            w_nRet = m_usbConn.bulkTransfer(m_epOUT, m_abyTransferBuf, m_nEPOutSize, nTimeOut);

            if (w_nRet != m_nEPOutSize)
                return false;
        }

        if (r > 0)
        {
            System.arraycopy(pBuf, i*m_nEPOutSize, m_abyTransferBuf, 0, r);

            w_nRet = m_usbConn.bulkTransfer(m_epOUT, m_abyTransferBuf, r, nTimeOut);

            if (w_nRet != r)
                return false;
        }

        return true;
    }

    private boolean UsbBulkReceive(byte[] pBuf, int nLen, int nTimeOut)
    {
        int i, n, r, w_nRet;
        //byte[] w_abyTmp = new byte[m_nEPInSize];

        //w_nRet = m_usbConn.bulkTransfer(m_epIN, pBuf, nLen, nTimeOut);

        //if (w_nRet != nLen)
        //    return false;

        n = nLen / m_nEPInSize;
        r = nLen % m_nEPInSize;

        //Toast.makeText(mApplicationContext, "UsbBulkReceive, Buf Len = " + pBuf.length, Toast.LENGTH_SHORT).show();

        for(i=0; i<n; i++)
        {
            w_nRet = m_usbConn.bulkTransfer(m_epIN, m_abyTransferBuf, m_nEPInSize, nTimeOut);

            if (w_nRet != m_nEPInSize)
                return false;

            System.arraycopy(m_abyTransferBuf, 0, pBuf, i*m_nEPInSize, m_nEPInSize);
        }

        if (r > 0)
        {
            w_nRet = m_usbConn.bulkTransfer(m_epIN, m_abyTransferBuf, r, nTimeOut);

            if (w_nRet != r)
                return false;

            System.arraycopy(m_abyTransferBuf, 0, pBuf, i*m_nEPInSize, r);
        }

        return true;
    }

	// END MAIN LOOP
	private BroadcastReceiver mPermissionReceiver = new PermissionReceiver(
			new IPermissionListener() {
				@Override
				public void onPermissionDenied(UsbDevice d) {
					l("Permission denied on " + d.getDeviceId());
				}
			});

	private static interface IPermissionListener {
		void onPermissionDenied(UsbDevice d);
	}

	public final static String TAG = "USBController";

	private void l(Object msg) {
		Log.d(TAG, ">==< " + msg.toString() + " >==<");
	}

	private void e(Object msg) {
		Log.e(TAG, ">==< " + msg.toString() + " >==<");
	}
}

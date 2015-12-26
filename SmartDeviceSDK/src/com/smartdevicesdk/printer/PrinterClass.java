package com.smartdevicesdk.printer;

import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;

public interface PrinterClass {
	
	// Constants that indicate the current connection state
	public static final int STATE_NONE = 0; // we're doing nothing
	public static final int STATE_LISTEN = 1; // now listening for incoming
												// connections
	public static final int STATE_CONNECTING = 2; // now initiating an outgoing
													// connection
	public static final int STATE_CONNECTED = 3; // now connected to a remote
													// device
	public static final int LOSE_CONNECT = 4;
	public static final int FAILED_CONNECT = 5;
	public static final int SUCCESS_CONNECT = 6; // now connected to a remote


	public static final int STATE_SCANING = 7;// ɨ��״̬
	public static final int STATE_SCAN_STOP = 8;

	public static final int MESSAGE_STATE_CHANGE = 1;
	public static final int MESSAGE_READ = 2;
	public static final int MESSAGE_WRITE = 3;
	
	/*
	 * ����ͺ�
	 */
	public static final byte[] CMD_CHECK_TYPE=new byte[]{0x1B,0x2B};	
	/*
	 * ˮƽ�Ʊ�
	 */
	public static final byte[] CMD_HORIZONTAL_TAB=new byte[]{0x09};	
	/*
	 * ����
	 */
	public static final byte[] CMD_NEWLINE=new byte[]{0x0A};
	/*
	 * ��ӡ��ǰ�洢����
	 */
	public static final byte[] CMD_PRINT_CURRENT_CONTEXT=new byte[]{0x0D};
	/*
	 * ��ʼ����ӡ��
	 */
	public static final byte[] CMD_INIT_PRINTER=new byte[]{0x1B,0x40};
	/*
	 * �����»��ߴ�ӡ
	 */
	public static final byte[] CMD_UNDERLINE_ON=new byte[]{0x1C,0x2D,0x01};
	/*
	 * ��ֹ�»��ߴ�ӡ
	 */
	public static final byte[] CMD_UNDERLINE_OFF =new byte[]{0x1C,0x2D,0x00};
	/*
	 * ��������ӡ
	 */
	public static final byte[] CMD_Blod_ON=new byte[]{0x1B,0x45,0x01};
	/*
	 * ��ֹ�����ӡ
	 */
	public static final byte[] CMD_BLOD_OFF=new byte[]{0x1B,0x45,0x00};
	/*
	 * ѡ�����壺ASCII(12*24) ���֣�24*24��
	 */
	public static final byte[] CMD_SET_FONT_24x24=new byte[]{0x1B,0x4D,0x00};
	/*
	 * ѡ�����壺ASCII(8*16)  ���֣�16*16��
	 */
	public static final byte[] CMD_SET_FONT_16x16=new byte[]{0x1B,0x4D,0x01};
	/*
	 * �ַ���  ���Ŵ�
	 */
	public static final byte[] CMD_FONTSIZE_NORMAL=new byte[]{0x1D,0x21,0x00};
	/*
	 * �ַ�2���ߣ�����Ŵ�
	 */
	public static final byte[] CMD_FONTSIZE_DOUBLE_HIGH=new byte[]{0x1D,0x21,0x01};
	/*
	 * �ַ�2���?����Ŵ�
	 */
	public static final byte[] CMD_FONTSIZE_DOUBLE_WIDTH=new byte[]{0x1D,0x21,0x10};
	/*
	 * �ַ�2������Ŵ�
	 */
	public static final byte[] CMD_FONTSIZE_DOUBLE=new byte[]{0x1D,0x21,0x11};
	/*
	 * �����
	 */
	public static final byte[] CMD_ALIGN_LEFT=new byte[]{0x1B,0x61,0x00};
	/*
	 * ���ж���
	 */
	public static final byte[] CMD_ALIGN_MIDDLE=new byte[]{0x1B,0x61,0x01};
	/*
	 * ���Ҷ���
	 */
	public static final byte[] CMD_ALIGN_RIGHT=new byte[]{0x1B,0x61,0x02};
	/*
	 * ҳ��ֽ/�ڱ궨λ
	 */
	public static final byte[] CMD_BLACK_LOCATION=new byte[]{0x0C};
	
}

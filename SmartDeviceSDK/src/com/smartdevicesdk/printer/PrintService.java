package com.smartdevicesdk.printer;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;










import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Bitmap.Config;

import com.smartdevicesdk.printer.PrinterLib;

/**
 * Print Service for image,GBK text,Unicode text
 * @author xuxl
 * @DateTime 2014-10-07 18:09:17
 */
public class PrintService{
	public static boolean getState=false;
	public static byte printState=0;
	public static boolean isFUll=false;

	/**
	 * Image Width<br/>
	 * 58mm paper please set imageWidth=48<br/>
	 * 80mm paper please set imageWidth=72
	 */
	public static int imageWidth=48;
	
	/**
	 * change the text to gbk byte array
	 * @param textStr String text
	 * @return gbk byte array
	 */
	public byte[] getText(String textStr) {
		// TODO Auto-generated method stubbyte[] send;
		byte[] send=null;
		try {
			send = textStr.getBytes("GBK");
		} catch (UnsupportedEncodingException e) {
			send = textStr.getBytes();
		}
		return send;
	}

	/**
	 * change the bitmap to byte array
	 * @param bitmap
	 * @return byte array
	 */
	public byte[] getImage(Bitmap bitmap) {
		// TODO Auto-generated method stub
		int mWidth = bitmap.getWidth();
		int mHeight = bitmap.getHeight();
		bitmap=resizeImage(bitmap, imageWidth * 8, mHeight);

		byte[]  bt =PrinterLib.getBitmapData(bitmap);
		
		
		/*try {//����ͼƬ����ļ�
			createFile("/sdcard/demo.txt",bt);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
		
		bitmap.recycle();
		return bt;
	}

	/**
	 * change the text to unicode byte array
	 * @param textStr String text
	 * @return unicode byte array
	 */
	public byte[] getTextUnicode(String textStr) {
		// TODO Auto-generated method stub
		byte[] send=string2Unicode(textStr);
		return send;
	}
	
	/**
	 * resize the bitmap
	 * @param bitmap
	 * @param w
	 * @param h
	 * @return
	 */
	private static Bitmap resizeImage(Bitmap bitmap, int w, int h) {
		Bitmap BitmapOrg = bitmap;
		int width = BitmapOrg.getWidth();
		int height = BitmapOrg.getHeight();

		if(width>w)
		{
			float scaleWidth = ((float) w) / width;
			float scaleHeight = ((float) h) / height+24;
			Matrix matrix = new Matrix();
			matrix.postScale(scaleWidth, scaleWidth);
			Bitmap resizedBitmap = Bitmap.createBitmap(BitmapOrg, 0, 0, width,
					height, matrix, true);
			return resizedBitmap;
		}else{
			Bitmap resizedBitmap = Bitmap.createBitmap(w, height+24, Config.RGB_565);
			Canvas canvas = new Canvas(resizedBitmap);
			Paint paint = new Paint();
			canvas.drawColor(Color.WHITE);
			canvas.drawBitmap(bitmap, (w-width)/2, 0, paint);
			return resizedBitmap;
		}
	}
	
	private static byte[] string2Unicode(String s) {   
	    try {   
	      StringBuffer out = new StringBuffer("");   
	      byte[] bytes = s.getBytes("unicode");   
	    	byte[] bt=new byte[bytes.length-2];
	      for (int i = 2,j=0; i < bytes.length - 1; i += 2,j += 2) {   
	    	  bt[j]= (byte)(bytes[i + 1] & 0xff);   
	    	  bt[j+1] = (byte)(bytes[i] & 0xff);    
	      }   
	      return bt;   
	    }   
	    catch (Exception e) {   
	      try {
			byte[] bt=s.getBytes("GBK");
			return bt;
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			return null;
		}
	    }   
	  }
	
	
	private byte[] StartBmpToPrintCode(Bitmap bitmap) {
		byte temp = 0;
		int j = 7;
		int start = 0;
		if (bitmap != null) {
			int mWidth = bitmap.getWidth();
			int mHeight = bitmap.getHeight();

			int[] mIntArray = new int[mWidth * mHeight];
			byte[] data = new byte[mWidth * mHeight];
			bitmap.getPixels(mIntArray, 0, mWidth, 0, 0, mWidth, mHeight);
			encodeYUV420SP(data, mIntArray, mWidth, mHeight);
			byte[] result = new byte[mWidth * mHeight / 8];
			for (int i = 0; i < mWidth * mHeight; i++) {
				temp = (byte) ((byte) (data[i] << j) + temp);
				j--;
				if (j < 0) {
					j = 7;
				}
				if (i % 8 == 7) {
					result[start++] = temp;
					temp = 0;
				}
			}
			if (j != 7) {
				result[start++] = temp;
			}

			int aHeight = 24 - mHeight % 24;
			int perline = mWidth / 8;
			byte[] add = new byte[aHeight * perline];
			byte[] nresult = new byte[mWidth * mHeight / 8 + aHeight * perline];
			System.arraycopy(result, 0, nresult, 0, result.length);
			System.arraycopy(add, 0, nresult, result.length, add.length);

			byte[] byteContent = new byte[(mWidth / 8 + 4)
					* (mHeight + aHeight)];// ��ӡ����
			byte[] bytehead = new byte[4];// ÿ�д�ӡͷ
			bytehead[0] = (byte) 0x1f;
			bytehead[1] = (byte) 0x10;
			bytehead[2] = (byte) (mWidth / 8);
			bytehead[3] = (byte) 0x00;
			for (int index = 0; index < mHeight + aHeight; index++) {
				System.arraycopy(bytehead, 0, byteContent, index
						* (perline + 4), 4);
				System.arraycopy(nresult, index * perline, byteContent, index
						* (perline + 4) + 4, perline);

			}
			return byteContent;
		}
		return null;

	}

	public void encodeYUV420SP(byte[] yuv420sp, int[] rgba, int width,
			int height) {
		final int frameSize = width * height;
		int[] U, V;
		U = new int[frameSize];
		V = new int[frameSize];
		final int uvwidth = width / 2;
		int r, g, b, y, u, v;
		int bits = 8;
		int index = 0;
		int f = 0;
		for (int j = 0; j < height; j++) {
			for (int i = 0; i < width; i++) {
				r = (rgba[index] & 0xff000000) >> 24;
				g = (rgba[index] & 0xff0000) >> 16;
				b = (rgba[index] & 0xff00) >> 8;
				// rgb to yuv
				y = ((66 * r + 129 * g + 25 * b + 128) >> 8) + 16;
				u = ((-38 * r - 74 * g + 112 * b + 128) >> 8) + 128;
				v = ((112 * r - 94 * g - 18 * b + 128) >> 8) + 128;
				// clip y
				// yuv420sp[index++] = (byte) ((y < 0) ? 0 : ((y > 255) ? 255 :
				// y));
				byte temp = (byte) ((y < 0) ? 0 : ((y > 255) ? 255 : y));
				yuv420sp[index++] = temp > 0 ? (byte) 1 : (byte) 0;

				// {
				// if (f == 0) {
				// yuv420sp[index++] = 0;
				// f = 1;
				// } else {
				// yuv420sp[index++] = 1;
				// f = 0;
				// }

				// }

			}

		}
		f = 0;
	}
	
	/**
	 * ׷���ļ���ʹ��FileOutputStream���ڹ���FileOutputStreamʱ���ѵڶ��������Ϊtrue
	 * 
	 * @param fileName
	 * @param content
	 */
	public void createFile(String path, byte[] content) throws IOException {
		FileOutputStream fos = new FileOutputStream(path);
		fos.write(content);
		fos.close();
	}
}

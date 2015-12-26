package com.smartdevicesdk.printer;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.HashMap;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PointF;
import android.view.Gravity;
import android.view.View.MeasureSpec;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

public abstract class BarcodeCreater {
	public static BarcodeFormat barcodeFormat = BarcodeFormat.CODE_128;

	/**
	 * create barcode
	 * @param context 	
	 * @param contents	code text
	 * @param desiredWidth	width
	 * @param desiredHeight	height
	 * @param displayCode	whether display text
	 * @param barType	barcode type
	 * @return
	 */
	public static Bitmap creatBarcode(Context context, String contents,
			int desiredWidth, int desiredHeight, boolean displayCode,
			int barType) {
		Bitmap ruseltBitmap = null;
		if (barType == 1) {
			barcodeFormat = BarcodeFormat.CODE_128;
		} else if (barType == 2) {
			barcodeFormat = BarcodeFormat.QR_CODE;
		}
		if (displayCode) {
			Bitmap barcodeBitmap = null;
			try {
				barcodeBitmap = encodeAsBitmap(contents, barcodeFormat,
						desiredWidth, desiredHeight);
			} catch (WriterException e) {
				e.printStackTrace();
			}
			Bitmap codeBitmap = creatCodeBitmap(contents, desiredWidth,
					desiredHeight, context);
			ruseltBitmap = mixtureBitmap(barcodeBitmap, codeBitmap, new PointF(
					0, desiredHeight));
		} else {
			try {
				ruseltBitmap = encodeAsBitmap(contents, barcodeFormat,
						desiredWidth, desiredHeight);
			} catch (WriterException e) {
				e.printStackTrace();
			}
		}

		return ruseltBitmap;
	}

	private static Bitmap creatCodeBitmap(String contents, int width,
			int height, Context context) {
		TextView tv = new TextView(context);
		LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
				width, height);
		tv.setLayoutParams(layoutParams);
		tv.setText(contents);
		//tv.setHeight(10);
		tv.setTextSize(10);
		tv.setGravity(Gravity.CENTER_HORIZONTAL);
		tv.setWidth(width);
		tv.setDrawingCacheEnabled(true);
		tv.setTextColor(Color.BLACK);
		tv.setBackgroundColor(Color.WHITE);
		tv.measure(MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED),
				MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
		tv.layout(0, 0, tv.getMeasuredWidth(), tv.getMeasuredHeight());

		tv.buildDrawingCache();
		Bitmap bitmapCode = tv.getDrawingCache();
		return bitmapCode;
	}

	/**
	 * create qrcode
	 * @param contents	code text
	 * @param desiredWidth	width
	 * @param desiredHeight	height
	 * @param barType code type
	 * @return
	 */
	public static Bitmap encode2dAsBitmap(String contents, int desiredWidth,
			int desiredHeight, int barType) {
		if (barType == 1) {
			barcodeFormat = BarcodeFormat.CODE_128;
		} else if (barType == 2) {
			barcodeFormat = BarcodeFormat.QR_CODE;
		}
		Bitmap barcodeBitmap = null;
		try {
			barcodeBitmap = encodeAsBitmap(contents, barcodeFormat,
					desiredWidth, desiredHeight);
		} catch (WriterException e) {
			e.printStackTrace();
		}
		return barcodeBitmap;
	}

	/**
	 * ������Bitmap�ϲ���һ��
	 * 
	 * @param first
	 * @param second
	 * @param fromPoint
	 *            �ڶ���Bitmap��ʼ���Ƶ���ʼλ�ã�����ڵ�һ��Bitmap��
	 * @return
	 */
	public static Bitmap mixtureBitmap(Bitmap first, Bitmap second,
			PointF fromPoint) {
		if (first == null || second == null || fromPoint == null) {
			return null;
		}

		Bitmap newBitmap = Bitmap.createBitmap(first.getWidth(),
				first.getHeight() + second.getHeight(), Config.ARGB_4444);
		Canvas cv = new Canvas(newBitmap);
		cv.drawBitmap(first, 0, 0, null);
		cv.drawBitmap(second, fromPoint.x, fromPoint.y, null);
		cv.save(Canvas.ALL_SAVE_FLAG);
		cv.restore();

		return newBitmap;
	}

	public static Bitmap encodeAsBitmap(String contents, BarcodeFormat format,
			int desiredWidth, int desiredHeight) throws WriterException {
		final int WHITE = 0xFFFFFFFF; // ����ָ��������ɫ���ö�ά���ɲ�ɫЧ��
		final int BLACK = 0xFF000000;

		HashMap<EncodeHintType, String> hints = null;
		String encoding = guessAppropriateEncoding(contents);
		if (encoding != null) {
			hints = new HashMap<EncodeHintType, String>(2);
			hints.put(EncodeHintType.CHARACTER_SET, encoding);
		}
		MultiFormatWriter writer = new MultiFormatWriter();
		BitMatrix result = writer.encode(contents, format, desiredWidth,
				desiredHeight, hints);
		int width = result.getWidth();
		int height = result.getHeight();
		int[] pixels = new int[width * height];
		// All are 0, or black, by default
		for (int y = 0; y < height; y++) {
			int offset = y * width;
			for (int x = 0; x < width; x++) {
				pixels[offset + x] = result.get(x, y) ? BLACK : WHITE;
			}
		}

		Bitmap bitmap = Bitmap.createBitmap(width, height,
				Bitmap.Config.ARGB_8888);
		bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
		return bitmap;
	}

	public static String guessAppropriateEncoding(CharSequence contents) {
		// Very crude at the moment
		for (int i = 0; i < contents.length(); i++) {
			if (contents.charAt(i) > 0xFF) {
				return "UTF-8";
			}
		}
		return null;
	}

	public static boolean saveBitmap2file(Bitmap bmp, String filename) {
		CompressFormat format = Bitmap.CompressFormat.JPEG;
		int quality = 100;
		OutputStream stream = null;
		try {
			stream = new FileOutputStream("/sdcard/" + filename);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		return bmp.compress(format, quality, stream);
	}
}
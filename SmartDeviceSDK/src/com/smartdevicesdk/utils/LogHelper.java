package com.smartdevicesdk.utils;

import android.graphics.Color;
import android.text.Spannable;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.widget.TextView;

/**
 * @author john 打印信息的格式控制类。 红色：出现的问题。 绿色：正常的信息。 黄色：可能出现的问题。 1 : color is black 2
 *         : color is yellow 3 : color is blue 4 : color is red other number :
 *         color is black;
 */
public class LogHelper {

	/**
	 * TestView changed color and info message. Called after the TestView is
	 * created and whenever the TextView changes. Set your TextView's message
	 * here.
	 * 
	 * @param TextView
	 *            text
	 * @param String
	 *            infoMsg : color is red < color name="red">#FF0000< /color><
	 *            !--红色 -->
	 */
	@Deprecated
	public static void infoException(TextView text, String infoMsg) {
		text.setText(infoMsg, TextView.BufferType.SPANNABLE);
		int start = text.getText().length();
		int end = start + infoMsg.length();
		Spannable style = (Spannable) text.getText();
		;
		style.setSpan(new ForegroundColorSpan(Color.RED), start, end,
				Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

	}

	/**
	 * @param TextView
	 *            text
	 * @param String
	 *            infoMsg : color is black
	 * 
	 * */
	@Deprecated
	public static void info(TextView text, String infoMsg) {
		text.setText(infoMsg);
	}

	/**
	 * @param TextView
	 *            text
	 * @param String
	 *            infoMsg : color is yellow
	 * */
	@Deprecated
	public static void infoWarning(TextView text, String infoMsg) {
		text.setText(infoMsg);
		Spannable style = (Spannable) text.getText();
		int start = text.getText().length();
		int end = start + infoMsg.length();
		style.setSpan(new ForegroundColorSpan(Color.YELLOW), start, end,
				Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
	}

	/**
	 * @param TextView
	 *            text
	 * @param String
	 *            infoMsg
	 * @param order
	 *            :set background color. 1 : color is black. 2 : color is
	 *            yellow. 3 : color is blue .4 : color is red .other number :
	 *            color is black;
	 * */
	public static void infoMsgAndColor(TextView text, String infoMsg, int order) {
		text.setText(infoMsg);
		Spannable style = (Spannable) text.getText();
		int start = 0;
		int end = start + infoMsg.length();
		ForegroundColorSpan color;
		switch (order) {
		case 1:
			color = new ForegroundColorSpan(Color.BLACK);
			break;
		case 2:
			color = new ForegroundColorSpan(Color.YELLOW);
			break;
		case 3:
			color = new ForegroundColorSpan(Color.BLUE);
			break;
		case 4:
			color = new ForegroundColorSpan(Color.RED);
			break;
		default:
			color = new ForegroundColorSpan(Color.BLACK);
			break;
		}
		style.setSpan(color, start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
	}
	

	
	public static void infoAppendMsgAndColor(TextView text, String infoMsg,
			int order) {
		int start = 0;
		if (text.getText().length() == 0) {
		} else {
			start = text.getText().length();
		}
		text.append(infoMsg);
		Spannable style = (Spannable) text.getText();
		
		int end = start + infoMsg.length();
		ForegroundColorSpan color;
		switch (order) {
		case 1:
			color = new ForegroundColorSpan(Color.BLACK);
			break;
		case 2:
			color = new ForegroundColorSpan(Color.YELLOW);
			break;
		case 3:
			color = new ForegroundColorSpan(Color.BLUE);
			break;
		case 4:
			color = new ForegroundColorSpan(Color.RED);
			break;
		default:
			color = new ForegroundColorSpan(Color.BLACK);
			break;
		}
		style.setSpan(color, start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
	}

	public static void printerLog(String log) {
		Log.i("debug", log);
	}
}

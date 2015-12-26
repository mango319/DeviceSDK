/** 
 *  
 * @author	xuxl
 * @email	xuxingliu922@163.com
 * @version  
 *     1.0 2015年12月22日 上午11:38:42 
 */ 
package com.smartdevice.testd;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.smartdevicesdk.adapter.SpinnerManage;
import com.smartdevicesdk.device.DeviceManage;
import com.smartdevicesdk.printer.BarcodeCreater;
import com.smartdevicesdk.printer.PrintService;
import com.smartdevicesdk.printer.PrinterClass;
import com.smartdevicesdk.printer.PrinterClassSerialPort;
import com.smartdevicesdk.utils.StringUtility;
import com.smartdevicesdk.utils.TypeConversion;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Bitmap.Config;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.Layout.Alignment;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

/** 
 * This class is used for : Printer
 *  
 * @author	xuxl
 * @email	xuxingliu922@163.com
 * @version  
 *     1.0 2015年12月22日 上午11:38:42 
 */
public class PrinterActivity extends Activity {
	PrinterClassSerialPort printerClass = null;
	List<Map<String, String>> listData = new ArrayList<Map<String, String>>();
	private static final int REQUEST_EX = 1;
	protected static final String TAG = "PrintDemo";

	private Thread autoprint_Thread;
	boolean isPrint = true;
	int times = 500;// Automatic print time interval


	private ImageView iv = null;

	private String picPath = "";
	private Bitmap btMap = null;
	private Button btnQrCode = null;
	private Button btnBarCode = null;
	private Button btnWordToPic = null;
	private Button btnUnicode;
	private Button btnOpenDevice;
	private Button btnPrint = null;
	private Button btnOpenPic = null;
	private Button btnPrintPic = null;
	
	private TextView textViewState=null;
	private EditText et_input = null;
	private CheckBox checkBoxAuto = null;
	
	private Spinner spinner_device;
	private Spinner spinner_baudrate;

	String thread = "readThread";
	String text = "打印测试\r\nabcdefghijklmnopqrstuvw\r\n";
	
	private String device = "/dev/ttyMT0";
	private int baudrate = 115200;// 38400

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_printer);
		
		getOverflowMenu();
		
		textViewState=(TextView)findViewById(R.id.textViewState);
		et_input = (EditText) findViewById(R.id.editText1);
		btnUnicode = (Button) findViewById(R.id.btnUnicode);
		btnPrint = (Button) findViewById(R.id.btnPrint);

		et_input.setText(text);

		btnOpenPic = (Button) findViewById(R.id.btnOpenPic);
		btnPrintPic = (Button) findViewById(R.id.btnPrintPic);

		checkBoxAuto = (CheckBox) findViewById(R.id.checkBoxTimer);
		iv = (ImageView) findViewById(R.id.iv_test);

		btnQrCode = (Button) findViewById(R.id.btnQrCode);
		btnBarCode = (Button) findViewById(R.id.btnBarCode);

		btnWordToPic = (Button) findViewById(R.id.btnWordToPic);
		
		btnOpenDevice=(Button)findViewById(R.id.btnopendevice);
		
		spinner_device=(Spinner)findViewById(R.id.spinner1);
		spinner_device.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				device=spinner_device.getItemAtPosition(position).toString();
				if(printerClass.mSerialPort.isOpen){
					printerClass.close();
					btnOpenDevice.setText(getResources().getString(R.string.opendevice));
				}
			}
			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				// TODO Auto-generated method stub
				
			}
			
		});
		spinner_baudrate=(Spinner)findViewById(R.id.spinner2);
		spinner_baudrate.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				String selectStr=spinner_baudrate.getItemAtPosition(position).toString();
				baudrate=Integer.parseInt(selectStr);
				if(printerClass.mSerialPort.isOpen){
					printerClass.close();
					btnOpenDevice.setText(getResources().getString(R.string.opendevice));
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {

			}
			
		});
		
		btnOpenDevice.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(printerClass.mSerialPort.isOpen){
					printerClass.close();
					btnOpenDevice.setText(getResources().getString(R.string.opendevice));
				}else{
					printerClass.device=device;
					printerClass.baudrate=baudrate;
					printerClass.open();
					btnOpenDevice.setText(getResources().getString(R.string.closedevice));
				}
			}
		});

		if (btnQrCode != null) {
			btnQrCode.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					String message = "http://www.google.com";
					if (message.length() > 0) {
						try {
							message = new String(message.getBytes("utf8"));
						} catch (UnsupportedEncodingException e) {
							Log.e(TAG, e.getMessage());
						}
						btMap = BarcodeCreater.encode2dAsBitmap(message, 384,
								384, 2);
						iv.setImageBitmap(btMap);
					}
				}
			});
		}
		if (btnWordToPic != null) {
			btnWordToPic.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					String str = et_input.getText().toString();
					btMap = Bitmap.createBitmap(384,
							et_input.getLineCount() * 25, Config.ARGB_8888);
					Canvas canvas = new Canvas(btMap);
					canvas.drawColor(Color.WHITE);
					TextPaint textPaint = new TextPaint();
					textPaint.setStyle(Paint.Style.FILL);
					textPaint.setColor(Color.BLACK);
					textPaint.setTextSize(25.0F);
					StaticLayout layout = new StaticLayout(str, textPaint,
							btMap.getWidth(), Alignment.ALIGN_NORMAL,
							(float) 1.0, (float) 0.0, true);

					layout.draw(canvas);

					iv.setImageBitmap(btMap);

				}
			});
		}

		if (btnBarCode != null) {
			btnBarCode.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					String message = "123456789";
					if (message.getBytes().length > message.length()) {
						Toast.makeText(
								PrinterActivity.this,"create error", 2000)
								.show();
						return;
					}
					if (message.length() > 0) {
						btMap = BarcodeCreater.creatBarcode(PrinterActivity.this,
								message, 384, 80, true, 1);
						iv.setImageBitmap(btMap);
					}
				}
			});
			if (btnPrint != null) {
				btnPrint.setOnClickListener(new OnClickListener() {
					public void onClick(View v) {
						String str = et_input.getText().toString();
						try {
							printerClass.printText(str);
						} catch (Exception e) {
							Log.e(TAG, e.getMessage());
						}

					}
				});
			}
			if (btnUnicode != null) {
				btnUnicode.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						String str = et_input.getText().toString();
						try {
							printerClass.printUnicode(str);
						} catch (Exception e) {
							Log.e(TAG, e.getMessage());
						}
					}
				});
			}
			if (btnOpenPic != null) {
				btnOpenPic.setOnClickListener(new View.OnClickListener() {
					public void onClick(View v) {
						Intent intent = new Intent(
								Intent.ACTION_PICK,
								android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
						startActivityForResult(intent, REQUEST_EX);
					}
				});
			}
			if (btnPrintPic != null) {
				btnPrintPic.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						new Thread() {
							public void run() {
								if (btMap != null) {
									printerClass.printImage(btMap);
									/*Message msgMessage = hanler.obtainMessage();
									msgMessage.what = 0;
									hanler.sendMessage(msgMessage);*/
								}
							}
						}.start();

						return;

					}
				});
			}
		}
		Handler mhandler = new Handler() {
			public void handleMessage(Message msg) {
				switch (msg.what) {
				case PrinterClass.MESSAGE_READ:
					byte[] readBuf = (byte[]) msg.obj;
					Log.i(TAG, "readBuf:" + readBuf[0]);
					if (readBuf[0] == 0x13) {
						PrintService.isFUll = true;
						textViewState.setText(getResources().getString(R.string.str_printer_state)+":"+getResources().getString(R.string.str_printer_bufferfull));
					} else if (readBuf[0] == 0x11) {
						PrintService.isFUll = false;
						textViewState.setText(getResources().getString(R.string.str_printer_state)+":"+getResources().getString(R.string.str_printer_buffernull));
					} else if (readBuf[0] == 0x08) {
						textViewState.setText(getResources().getString(R.string.str_printer_state)+":"+getResources().getString(R.string.str_printer_nopaper));
					} else if (readBuf[0] == 0x01) {
						textViewState.setText(getResources().getString(R.string.str_printer_state)+":"+getResources().getString(R.string.str_printer_printing));
					}  else if (readBuf[0] == 0x04) {
						textViewState.setText(getResources().getString(R.string.str_printer_state)+":"+getResources().getString(R.string.str_printer_hightemperature));
					} else if (readBuf[0] == 0x02) {
						textViewState.setText(getResources().getString(R.string.str_printer_state)+":"+getResources().getString(R.string.str_printer_lowpower));
					}else {
						String readMessage = new String(readBuf, 0, msg.arg1);
						if (readMessage.contains("800"))// 80mm paper
						{
							PrintService.imageWidth = 72;
							Toast.makeText(getApplicationContext(), "80mm",
									Toast.LENGTH_SHORT).show();
						} else if (readMessage.contains("580"))// 58mm paper
						{
							PrintService.imageWidth = 48;
							Toast.makeText(getApplicationContext(), "58mm",
									Toast.LENGTH_SHORT).show();
						} else {
							
						}
					}
					break;
				case PrinterClass.MESSAGE_STATE_CHANGE:// 6��l��״
					switch (msg.arg1) {
					case PrinterClass.STATE_CONNECTED:// �Ѿ�l��
						break;
					case PrinterClass.STATE_CONNECTING:// ����l��
						Toast.makeText(getApplicationContext(),
								"STATE_CONNECTING", Toast.LENGTH_SHORT).show();
						break;
					case PrinterClass.STATE_LISTEN:
					case PrinterClass.STATE_NONE:
						break;
					case PrinterClass.SUCCESS_CONNECT:
						printerClass.write(new byte[] { 0x1b, 0x2b });// ����ӡ���ͺ�
						Toast.makeText(getApplicationContext(),
								"SUCCESS_CONNECT", Toast.LENGTH_SHORT).show();
						break;
					case PrinterClass.FAILED_CONNECT:
						Toast.makeText(getApplicationContext(),
								"FAILED_CONNECT", Toast.LENGTH_SHORT).show();

						break;
					case PrinterClass.LOSE_CONNECT:
						Toast.makeText(getApplicationContext(), "LOSE_CONNECT",
								Toast.LENGTH_SHORT).show();
					}
					break;
				case PrinterClass.MESSAGE_WRITE:

					break;
				}
				super.handleMessage(msg);
			}
		};

		printerClass = new PrinterClassSerialPort(this,mhandler);
		printerClass.open();

		// Auto Print
		autoprint_Thread = new Thread() {
			public void run() {
				while (isPrint) {
					if (checkBoxAuto.isChecked()) {
						String message = et_input.getText().toString();
						printerClass.printText(message);
						try {
							Thread.sleep(times);
						} catch (InterruptedException e) {
							Log.e(TAG, e.getMessage());
						}
					}
				}
			}
		};
		autoprint_Thread.start();
		
		device=MainActivity.devInfo.getPrinterSerialport();
		baudrate=MainActivity.devInfo.getPrinterBaudrate();
		
		SpinnerManage.setDefaultItem(spinner_device, device);
		SpinnerManage.setDefaultItem(spinner_baudrate, baudrate);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		printerClass.close();
	}

	private Handler hanler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 0:
				btnPrintPic.setEnabled(true);
				btnOpenPic.setEnabled(true);
				btnBarCode.setEnabled(true);
				btnWordToPic.setEnabled(true);
				btnQrCode.setEnabled(true);
				btnPrint.setEnabled(true);
				break;

			default:
				break;
			}
		}
	};

	public static Bitmap resizeImage(Bitmap bitmap, int w, int h) {
		Bitmap BitmapOrg = bitmap;
		int width = BitmapOrg.getWidth();
		int height = BitmapOrg.getHeight();
		int newWidth = w;
		int newHeight = h;

		if (width >= newWidth) {
			float scaleWidth = ((float) newWidth) / width;
			Matrix matrix = new Matrix();
			matrix.postScale(scaleWidth, scaleWidth);
			Bitmap resizedBitmap = Bitmap.createBitmap(BitmapOrg, 0, 0, width,
					height, matrix, true);
			return resizedBitmap;
		} else {

			Bitmap bitmap2 = Bitmap.createBitmap(newWidth, newHeight,
					bitmap.getConfig());
			Canvas canvas = new Canvas(bitmap2);
			canvas.drawColor(Color.WHITE);

			canvas.drawBitmap(BitmapOrg, (newWidth - width) / 2, 0, null);

			return bitmap2;

		}
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == REQUEST_EX && resultCode == RESULT_OK
				&& null != data) {
			Uri selectedImage = data.getData();
			String[] filePathColumn = { MediaStore.Images.Media.DATA };
			Cursor cursor = getContentResolver().query(selectedImage,
					filePathColumn, null, null, null);
			cursor.moveToFirst();
			int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
			String picturePath = cursor.getString(columnIndex);
			picPath = picturePath;
			iv.setImageURI(selectedImage);
			btMap = BitmapFactory.decodeFile(picPath);
			if (btMap.getHeight() > 384) {
				btMap = BitmapFactory.decodeFile(picPath);
				iv.setImageBitmap(resizeImage(btMap, 384, 384));

			}
			cursor.close();
		}

	}

	static byte[] string2Unicode(String s) {
		try {
			byte[] bytes = s.getBytes("unicode");
			byte[] bt = new byte[bytes.length - 2];
			for (int i = 2, j = 0; i < bytes.length - 1; i += 2, j += 2) {
				bt[j] = (byte) (bytes[i + 1] & 0xff);
				bt[j + 1] = (byte) (bytes[i] & 0xff);
			}
			return bt;
		} catch (Exception e) {
			try {
				byte[] bt = s.getBytes("GBK");
				return bt;
			} catch (UnsupportedEncodingException e1) {
				Log.e(TAG, e.getMessage());
				return null;
			}
		}
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		Resources res = getResources();
		String[] cmdStr = res.getStringArray(R.array.cmd);
		for (int i = 0; i < cmdStr.length; i++) {
			String[] cmdArray = cmdStr[i].split(",");
			if (cmdArray.length == 2) {
				Map<String, String> map = new HashMap<String, String>();
				map.put("title", cmdArray[0]);
				map.put("description", cmdArray[1]);
				menu.add(0, i, i, cmdArray[0]);
				listData.add(map);
			}
		}

		return true;
	}
	private void getOverflowMenu() {
		try {
			ViewConfiguration config = ViewConfiguration.get(this);
			Field menuKeyField = ViewConfiguration.class
					.getDeclaredField("sHasPermanentMenuKey");
			if (menuKeyField != null) {
				menuKeyField.setAccessible(true);
				menuKeyField.setBoolean(config, false);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public boolean onOptionsItemSelected(android.view.MenuItem item) {
		Map map = listData.get(item.getItemId());
		String cmd = map.get("description").toString();

		byte[] bt =TypeConversion.hexStringToBytes(cmd);
		printerClass.write(bt);
		Toast toast = Toast.makeText(this, "send success！", Toast.LENGTH_SHORT);
		toast.show();
		return false;
	}

}
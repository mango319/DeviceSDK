package com.smartdevice.testd;

import com.smartdevicesdk.adapter.SpinnerManage;

import android.app.Activity;
import android.app.Application;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.serialport.api.MyApp;
import android.serialport.api.SerialPortFinder;
import android.serialport.api.SerialPortParam;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

public class SerialPortSettingActivity extends PreferenceActivity {
	protected MyApp mApplication;
	private SerialPortFinder mSerialPortFinder;
	Spinner spinner_name, spinner_baud, spinner_data, spinner_stop,
			spinner_check, spinner_flowcontrol;
	Button btnSave;
	EditText editTextView_space;
	public static String stop[] = new String[] { "1", "2" };
	public static String databits[] = new String[] { "7", "8" };
	public static String parityvalue[] = new String[] { "n", "o", "e" };// n��
																		// o��
																		// eż
	public static String flowcontrol[] = new String[] { "none", "CRTSCTS",
			"Xon/Xoff" };

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_serialport_setting);

		MyApp mApplication = (MyApp) getApplication();
		mSerialPortFinder = mApplication.mSerialPortFinder;
		String[] entries = mSerialPortFinder.getAllDevices();
		final String[] entryValues = mSerialPortFinder.getAllDevicesPath();

		editTextView_space = (EditText) findViewById(R.id.editTextView_space);
		btnSave = (Button) findViewById(R.id.btnSave);
		btnSave.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				String str = editTextView_space.getText().toString();
				if (str.equals("")) {
					str = "0";
				}
				SerialPortParam.SpaceTime = Integer.parseInt(str);
				finish();
			}
		});

		spinner_name = (Spinner) findViewById(R.id.spinner_serialport_name);
		spinner_name.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				SerialPortParam.Name = (String) spinner_name
						.getItemAtPosition(position);
				SerialPortParam.Path = entryValues[position];
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
			}
		});
		spinner_baud = (Spinner) findViewById(R.id.spinner_serialport_baud);
		spinner_baud.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				String dataStr = spinner_baud.getItemAtPosition(position)
						.toString();
				SerialPortParam.Baudrate = Integer.parseInt(dataStr);
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
			}
		});
		spinner_data = (Spinner) findViewById(R.id.spinner_serialport_data);
		spinner_data.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				String dataStr = spinner_data.getItemAtPosition(position)
						.toString();
				SerialPortParam.DataBits = Integer.parseInt(dataStr);
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
			}
		});
		spinner_stop = (Spinner) findViewById(R.id.spinner_serialport_stop);
		spinner_stop.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				String dataStr = spinner_stop.getItemAtPosition(position)
						.toString();
				SerialPortParam.StopBits = Integer.parseInt(dataStr);
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
			}
		});
		spinner_check = (Spinner) findViewById(R.id.spinner_serialport_check);
		spinner_check.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				char[] dataStr = spinner_check.getItemAtPosition(position)
						.toString().toCharArray();
				SerialPortParam.Parity = dataStr[0];
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
			}
		});

		spinner_flowcontrol = (Spinner) findViewById(R.id.spinner_serialport_flowcontrol);
		spinner_flowcontrol
				.setOnItemSelectedListener(new OnItemSelectedListener() {
					@Override
					public void onItemSelected(AdapterView<?> parent,
							View view, int position, long id) {
						SerialPortParam.Flowcontrol = position;
					}

					@Override
					public void onNothingSelected(AdapterView<?> parent) {
					}
				});

		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, entries);
		spinner_name.setAdapter(adapter);

		final String[] baudValues = getResources().getStringArray(
				R.array.baudrates_value);
		adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, baudValues);
		spinner_baud.setAdapter(adapter);

		adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, databits);
		spinner_data.setAdapter(adapter);

		adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, stop);
		spinner_stop.setAdapter(adapter);

		adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, parityvalue);
		spinner_check.setAdapter(adapter);

		adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, flowcontrol);
		spinner_flowcontrol.setAdapter(adapter);

		SharedPreferences sp = getSharedPreferences(
				"com.example.serialtest_preferences", Application.MODE_PRIVATE);

		SpinnerManage.setDefaultItem(spinner_name, sp.getString("name", ""));
		SpinnerManage.setDefaultItem(spinner_baud, sp.getString("baudValues", ""));
		SpinnerManage.setDefaultItem(spinner_data, sp.getString("databits", ""));
		SpinnerManage.setDefaultItem(spinner_stop, sp.getString("stop", ""));
		SpinnerManage.setDefaultItem(spinner_check, sp.getString("parityvalue", ""));

		if (sp.getString("flowcontrol", "") != "") {
			int flowcontrolIndex = Integer.parseInt(sp.getString("flowcontrol",
					""));
			spinner_flowcontrol.setSelection(flowcontrolIndex);
		}
		editTextView_space.setText(sp.getString("spacetime", ""));

		spinner_data.setSelection(1);
	}


	@Override
	protected void onDestroy() {
		super.onDestroy();
		SharedPreferences sp = getSharedPreferences(
				"com.example.serialtest_preferences", Application.MODE_PRIVATE);
		SharedPreferences.Editor editor = sp.edit();
		editor.putString("name", SerialPortParam.Name);
		editor.putString("path", SerialPortParam.Path);
		editor.putString("baudValues", SerialPortParam.Baudrate + "");
		editor.putString("databits", SerialPortParam.DataBits + "");
		editor.putString("stop", SerialPortParam.StopBits + "");
		editor.putString("parityvalue", SerialPortParam.Parity + "");
		editor.putString("flowcontrol", SerialPortParam.Flowcontrol + "");
		editor.commit();
	}
}

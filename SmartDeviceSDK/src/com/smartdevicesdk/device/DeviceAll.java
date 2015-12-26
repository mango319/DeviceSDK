/** 
 *  
 * @author	xuxl
 * @email	leoxuxl@163.com
 * @version  
 *     1.0 2015年12月22日 下午5:47:18 
 */ 
package com.smartdevicesdk.device;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/** 
 * @author	xuxl
 * @email	leoxuxl@163.com
 * @version  
 *     1.0 2015年12月22日 下午5:47:18 
 */
public class DeviceAll implements DeviceInfo {
	@Override
	public List<HashMap<String, String>> getFunctionList() {
		List<HashMap<String, String>> data = new ArrayList<HashMap<String, String>>();

		HashMap<String, String> map = new HashMap<String, String>();
		map.put("id", "printer");
		map.put("name", "打印机");
		data.add(map);

		map = new HashMap<String, String>();
		map.put("id", "scanner");
		map.put("name", "扫描-扫描模块");
		data.add(map);

		map = new HashMap<String, String>();
		map.put("id", "camerascanner");
		map.put("name", "扫描-摄像头");
		data.add(map);

		map = new HashMap<String, String>();
		map.put("id", "psam");
		map.put("name", "接触式PSAM卡");
		data.add(map);

		map = new HashMap<String, String>();
		map.put("id", "magneticcard");
		map.put("name", "磁条卡(I2C)");
		data.add(map);

		map = new HashMap<String, String>();
		map.put("id", "fingerprint");
		map.put("name", "指纹");
		data.add(map);

		map = new HashMap<String, String>();
		map.put("id", "idcard");
		map.put("name", "二代身份证-公安部模块");
		data.add(map);
		
		map = new HashMap<String, String>();
		map.put("id", "serialport");
		map.put("name", "串口工具");
		data.add(map);
		
		return data;
	}
	@Override
	public String getPrinterSerialport() {
		String serialport_name="/dev/ttySAC3";
		return serialport_name;
	}
	@Override
	public int getPrinterBaudrate() {
		int serialport_baudrate=38400;
		return serialport_baudrate;
	}
	@Override
	public String getScannerSerialport() {
		String serialport_name="/dev/ttyMT0";
		return serialport_name;
	}
	@Override
	public int getScannerBaudrate() {
		int serialport_baudrate=9600;
		return serialport_baudrate;
	}
	@Override
	public String getIdCardSerialport() {
		String serialport_name="/dev/ttySAC1";
		return serialport_name;
	}
	@Override
	public int getIdCardBaudrate() {
		int serialport_baudrate=115200;
		return serialport_baudrate;
	}
	@Override
	public String getFingerSerialport() {
		String serialport_name="/dev/ttyMT0";
		return serialport_name;
	}
	@Override
	public int getFingerBaudrate() {
		int serialport_baudrate=115200;
		return serialport_baudrate;
	}
	@Override
	public String getUHFSerialport() {
		String serialport_name="/dev/ttyMT0";
		return serialport_name;
	}
	@Override
	public int getUHFBaudrate() {
		int serialport_baudrate=115200;
		return serialport_baudrate;
	}
	
}

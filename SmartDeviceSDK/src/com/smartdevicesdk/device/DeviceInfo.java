/** 
 *  
 * @author	xuxl
 * @email	leoxuxl@163.com
 * @version  
 *     1.0 2015年12月23日 上午11:17:27 
 */ 
package com.smartdevicesdk.device;

import java.util.HashMap;
import java.util.List;

/** 
 * This class is used for DeviceInfo
 *  
 * @author	xuxl
 * @email	leoxuxl@163.com
 * @version  
 *     1.0 2015年12月23日 上午11:17:27 
 */
public interface DeviceInfo {
	public List<HashMap<String, String>> getFunctionList();
	
	/**
	 * 获取打印串口名称<br/>
	 * Get print port name
	 * @return String
	 */
	public String getPrinterSerialport();
	/**
	 * 获取打印串口波特率<br/>
	 * Get print port baud rate
	 * @return int
	 */
	public int getPrinterBaudrate();
	
	/**
	 * 获取扫描模块串口名称<br/>
	 * Get scanning module serial name
	 * @return String
	 */
	public String getScannerSerialport();
	
	/**
	 * 获取扫描模块串口波特率<br/>
	 * Get scanning module baud rate
	 * @return int
	 */
	public int getScannerBaudrate();
	
	/**
	 * 获取二代身份证串口名称<br/>
	 * Get name of the second generation ID card serial
	 * @return String
	 */
	public String getIdCardSerialport();
	/**
	 * 获取二代身份证串口波特率<br/>
	 * Get second-generation ID card serial port baud rate
	 * @return int
	 */
	public int getIdCardBaudrate();
	
	/**
	 * 获取指纹模块串口名称<br/>
	 * Get fingerprint module Serial Name
	 * @return String
	 */
	public String getFingerSerialport();
	/**
	 * 获取指纹模块串口波特率<br/>
	 * Get fingerprint module baud rate
	 * @return int
	 */
	public int getFingerBaudrate();
	
	/**
	 * 获取超高频串口名称<br/>
	 * Get UHF serial name
	 * @return String
	 */
	public String getUHFSerialport();
	/**
	 * 获取超高频串口波特率<br/>
	 * Get UHF baud rate
	 * @return int
	 */
	public int getUHFBaudrate();
}

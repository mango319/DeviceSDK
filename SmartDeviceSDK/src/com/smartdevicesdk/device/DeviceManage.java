/** 
 *  
 * @author	xuxl
 * @email	leoxuxl@163.com
 * @version  
 *     1.0 2015年12月22日 下午5:34:51 
 */
package com.smartdevicesdk.device;

/**
 * @author xuxl
 * @email leoxuxl@163.com
 * @version 1.0 2015年12月22日 下午5:34:51
 */
public class DeviceManage {
	public static DeviceInfo getDevInfo(String devType) {
		if (devType.equals("PC700")) {
			return new PC700();
		} else if (devType.equals("PDA3501")) {
			return new PDA3501();
		}else{
			return new DeviceAll();
		}
	}
}

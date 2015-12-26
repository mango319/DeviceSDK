package android.serialport.api;

public class SerialPortParam {
	public static String Name="";
	public static String Path="";
	public static int Baudrate=115200;
	public static int DataBits=8;
	public static int StopBits=1;
	public static int Parity='n';
	public static int SpaceTime=0;
	/**
	 * Flowcontrol
	 * 0 ~CRTSCTS
	 * 1 CRTSCTS
	 * 2 IXON/IXOFF/IXANY
	 */
	public static int Flowcontrol = 0;
	/**
	 * 
	 * n,无效验(No Parity)
	 * o,奇效验(Odd)
	 * e,偶效验(Even)
	 * s,空效验(Space)
	 *  
	 * @author	xuxl
	 * @email	leoxuxl@163.com
	 * @version  
	 *     1.0 2015年12月23日 下午3:00:10
	 */
	public static enum ParityEnum{
		n,
		o,
		e,
		s,
	}
	
}

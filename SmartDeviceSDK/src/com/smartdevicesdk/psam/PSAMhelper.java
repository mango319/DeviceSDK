package com.smartdevicesdk.psam;

public class PSAMhelper {
	
	/*1.       
	 功能：	打开设备
	 参数：[in]int  slotno,卡槽编号，如果传入0，自动适配第一个可打开卡的卡槽
	       [out]unsigned long * fd 传出设备句柄
	 返回值：正确为0，错误为非0  
	*/
	public static native int OpenCard(int[] fd,int slotno);
	
	
	/*2.       
		功能：	关闭设备
	 	参数： [in]unsigned long fd传入要关闭的设备句柄
	  返回值：正确为0，错误为非0              
	*/
	public static native int CloseCard(long fd);

	/*3.       
		功能：	设备复位
	 	参数： [in]unsigned long fd传入要关闭的设备句柄
	         [out]unsigned char *atr 传出设备复位信息
		       [in/out]int *atrLen 传出设备复位信息长度
	  返回值：正确为0，错误为非0
	*/
	public static native int ResetCard(long fd, byte[] atr,int[] atrLen);

	/*4.       
		功能：	发送命令
	 	参数： [in]unsigned long fd传入设备句柄
	         [in]unsigned char *apdu 要发送的apdu指令
	         [in]int apduLength 要发送的apdu指令长度
	         [out]unsigned char*response 返回数据内容
	         [in/out]int* respLength 返回数据长度
	  返回值：正确为0，错误为非0
	   注意：  此接口不执行自动取响应数据（即此接口不自动发送“00c0”这种取响应指令）
	*/
	public static native int CardApdu(long fd, byte[] apdu,int apduLength, byte[] response,int[] respLength);


	/*5.       
		
		功能：	检查卡片在位状态
	 	参数： [in]unsigned long fd传入检测的设备句柄
	  返回值：正确为0，错误为非0
	*/
	public static native int CheckCard(long fd);
	
	static {
        System.loadLibrary("psamdev");
    }
}

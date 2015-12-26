# DeviceSDK
sdk for android device



项目说明：

导入选择DeviceSDK文件夹，导入两个项目，导入后效果如下


	Eclipse导入方式：File->Import->Android->Existing Android Code Into Workspace->Chose ‘DeviceSDK’->Finish
	


SDK包说明：
	android.serialport.api
		串口操作接口，主要用到SerialPort类
	
	SerialPort函数说明：
	
		函数	说明	参数	备注
		SerialPort(String, int)	实例化，并自动打开串口，启动接收返回数据线程	String
		串口设备名称，如/dev/ttySAC1
		int
		串口波特率，如115200	
		SerialPort()	实例化		需要调用open(String, int)打开串口，并使用mFileInputStream接收返回数据
		setOnserialportDataReceived(SerialPortDataReceived)	注册返回数据接收事件		如果注册该函数，需要使用SerialPort(String, int)方法实例化
		open(String, int)	打开串口	String
		串口设备名称，如/dev/ttySAC1
		int
		串口波特率，如115200	
		open()	打开串口		
		closePort()	关闭串口，释放资源		
		Write(byte[])	发送串口数据	16进制数据	
		Write(String)	发送串口数据	字符数据，以UTF-8编码发送	
		setOnserialportDataReceived(SerialPortDataReceived)	注册返回数据监听事件		
			



	com.smartdevicesdk.adapter
	UI适配器管理
	com.smartdevicesdk.camerascanner
	摄像头扫描接口

	使用方法：
	//需要注册权限
		<uses-feature android:name="android.hardware.camera" />
	
	//打开扫描窗口
		Intent intent = new Intent(this, ZBarScannerActivity.class);
		startActivityForResult(intent, ZBAR_SCANNER_REQUEST);


	//接收扫描返回数据
	
	
		@Override
		protected void onActivityResult(int requestCode, int resultCode, Intent data) {
			if (resultCode == RESULT_OK) {
				// Scan result is available by making a call to
				// data.getStringExtra(ZBarConstants.SCAN_RESULT)
				// Type of the scan result is available by making a call to
				// data.getStringExtra(ZBarConstants.SCAN_RESULT_TYPE)
				String str = "Scan Result:\r\n"
						+ data.getStringExtra(ZBarConstants.SCAN_RESULT);
				str += "\r\nScan Result Type:\r\n"
						+ data.getIntExtra(ZBarConstants.SCAN_RESULT_TYPE, 0);
				// The value of type indicates one of the symbols listed in Advanced
				// Options below.
	
				textView.setText(str);
				
			} else if (resultCode == RESULT_CANCELED) {
				textView.setText("Camera unavailable");
			}
		}
		
		
	com.smartdevicesdk.database
	数据库SQLite接口

	com.smartdevicesdk.device
	不同Android设备管理，功能选择,主要用于设备默认串口选择，了解设备串口对应关系后，无需调用此接口

	com.smartdevicesdk.fingerprint
	指纹模块接口，调用方法请参见TestDEMO中FingerActivity类

	com.smartdevicesdk.idcard
	二代身份证接口

		使用方法：
		初始化接口IDCardHelper(String, int)，传入串口名称与波特率后，调用getIDCard()即可获取二代证信息

	com.smartdevicesdk.media
	多媒体声音播放接口

	com.smartdevicesdk.printer
	打印机接口(串口)

		使用方法：
		主要用到PrinterClassSerialPort 类
		函数	说明	参数	备注
		PrinterClassSerialPort(Context, Handler)	初始化	Context
		上下文实例
		Handler
		打印机返回数据句柄	
		open()	打开设备		
		close()	关闭设备		
		printText(String)	发送文字数据打印，默认GBK编码		
		printImage(Bitmap)	发送图片数据打印		
		printUnicode(String)	发送Unicode打印数据		
		write(byte[])	发送16进制数据		打印指令数据，可以使用此函数
					
		device : String	串口设备名称		
		baudrate : int	串口波特率		


	com.smartdevicesdk.psam
	接触式PSAM卡接口

		函数	说明	参数	备注
		OpenCard(int[], int)	打开设备	参数：[in]int  slotno,卡槽编号，如果传入0，自动适配第一个可打开卡的卡槽
			       [out]unsigned long * fd 传出设备句柄	
		CloseCard(long)	关闭设备	参数： [in]unsigned long fd传入要关闭的设备句柄
			  返回值：正确为0，错误为非0	
		ResetCard(long, byte[], int[])	设备复位	参数： [in]unsigned long fd传入要关闭的设备句柄
			         [out]unsigned char *atr 传出设备复位信息
				       [in/out]int *atrLen 传出设备复位信息长度
			  返回值：正确为0，错误为非0	
		CardApdu(long, byte[], int, byte[], int[])	发送命令	参数： [in]unsigned long fd传入设备句柄
			         [in]unsigned char *apdu 要发送的apdu指令
			         [in]int apduLength 要发送的apdu指令长度
			         [out]unsigned char*response 返回数据内容
			         [in/out]int* respLength 返回数据长度
			  返回值：正确为0，错误为非0	
		CheckCard(long)	检查卡片在位状态	参数： [in]unsigned long fd传入检测的设备句柄
			  返回值：正确为0，错误为非0	功能未启用


	com.smartdevicesdk.scanner
	一维二维扫描模块接口

		函数	说明	参数	备注
		ScannerHelper(Context, String, int, Handler)	实例化	Context
		上下文实例
		String
		串口设备名称
		int
		串口波特率
		Handler
		接收扫描数据句柄	
		Close()	关闭模块		
		scan()	启动扫描		
			


	com.smartdevicesdk.stripcard
	磁条卡接口(I2C通讯)
	调用Stripcardhelper.ReadCard()即可获取刷卡后的数据，详细参见TestDEMO中MagneticCardActivity

	com.smartdevicesdk.ui
	UI控制接口
	
	com.smartdevicesdk.utils
	字符串函数处理接口



#project instruction:

Import Select DeviceSDK folder, import two projects after the import results are as follows


Import Eclipse way: File-> Import-> Android-> Existing Android Code Into Workspace-> Chose 'DeviceSDK' -> Finish




SDK package:
android.serialport.api
Serial user interface, the main use SerialPort class

SerialPort Function Description:

Function Description Parameter Remarks
SerialPort (String, int) is instantiated, and automatically open the serial port, start to receive data back Thread String
Serial devices, such as / dev / ttySAC1
int
Serial port baud rate, such as 115200
SerialPort () call instantiates open (String, int) to open the serial port, and to receive data back using mFileInputStream
setOnserialportDataReceived (SerialPortDataReceived) registered to receive event data is returned if the registration of the function, you need to use SerialPort (String, int) method to instantiate
open (String, int) to open the serial String
Serial devices, such as / dev / ttySAC1
int
Serial port baud rate, such as 115200
open () open the serial port
closePort () to close the serial port, the release of resources
Write (byte []) to send serial data in hex data
Write (String) transmit serial data character data in UTF-8 encoding to send
setOnserialportDataReceived (SerialPortDataReceived) registered the event listener returns the data




com.smartdevicesdk.adapter
UI adapter management
com.smartdevicesdk.camerascanner
Camera Scan Interface

Instructions:
// Need to register Permissions
<Uses-feature android: name = "android.hardware.camera" />

// Open the scan window
Intent intent = new Intent (this, ZBarScannerActivity.class);
startActivityForResult (intent, ZBAR_SCANNER_REQUEST);


// Return receive the scan data
Override
protected void onActivityResult (int requestCode, int resultCode, Intent data) {
if (resultCode == RESULT_OK) {
// Scan result is available by making a call to
// Data.getStringExtra (ZBarConstants.SCAN_RESULT)
// Type of the scan result is available by making a call to
// Data.getStringExtra (ZBarConstants.SCAN_RESULT_TYPE)
String str = "Scan Result: \ r \ n"
+ Data.getStringExtra (ZBarConstants.SCAN_RESULT);
str + = "\ r \ nScan Result Type: \ r \ n"
+ Data.getIntExtra (ZBarConstants.SCAN_RESULT_TYPE, 0);
// The value of type indicates one of the symbols listed in Advanced
// Options below.

textView.setText (str);

} Else if (resultCode == RESULT_CANCELED) {
textView.setText ("Camera unavailable");
}
}
com.smartdevicesdk.database
SQLite Database Interface

com.smartdevicesdk.device
Different Android device management, function selection, primarily for equipment default serial port selection, understanding the correspondence between the serial device, the need to call this interface

com.smartdevicesdk.fingerprint
Fingerprint module interface, call the method, see TestDEMO in FingerActivity class

com.smartdevicesdk.idcard
Second-generation ID card Interface

Instructions:
Initialization Interface IDCardHelper (String, int), after the name of the incoming serial port baud rate, call getIDCard () to get the second generation ID card information

com.smartdevicesdk.media
Multimedia Sound playback Interface

com.smartdevicesdk.printer
Printer interface (serial)

Instructions:
The main use PrinterClassSerialPort class
Function Description Parameter Remarks
PrinterClassSerialPort (Context, Handler) initialization Context
Context instances
Handler
The printer returns data handle
open () to open the device
close () Closes equipment
printText (String) send text data to print, the default GBK coding
printImage (Bitmap) send a picture data printing
printUnicode (String) print send Unicode data
write (byte []) to send hexadecimal data print instruction data, you can use this function

device: String serial device name
baudrate: int baud rate


com.smartdevicesdk.psam
Contact PSAM card interface

Function Description Parameter Remarks
OpenCard (int [], int) to open the device parameters: [in] int slotno, slot number, if you pass 0, automatically adapts to the first open card slot
[Out] unsigned long * fd outgoing device handle
CloseCard (long) Turn off the device parameters: [in] unsigned long fd incoming To turn off the device handle
Returns: the right to 0, the error is non-zero
ResetCard (long, byte [], int []) device reset Parameters: [in] unsigned long fd incoming To turn off the device handle
[Out] unsigned char * atr device reset outgoing information
[In / out] int * atrLen device reset outgoing message length
Returns: the right to 0, the error is non-zero
CardApdu (long, byte [], int, byte [], int []) to send the command parameters: [in] unsigned long fd handle incoming equipment
Apdu instruction [in] unsigned char * apdu to send
[In] int apdu instruction length to be sent apduLength
[Out] unsigned char * response returns the data content
[In / out] int * respLength return data length
Returns: the right to 0, the error is non-zero
CheckCard (long) cards in place to check the status parameters: [in] unsigned long fd detection device handle incoming
Returns: the right to 0, the error is not enabled nonzero


com.smartdevicesdk.scanner
One-dimensional two-dimensional scanning module interface

Function Description Parameter Remarks
ScannerHelper (Context, String, int, Handler) examples of Context
Context instances
String
Serial device names
int
Baud rate
Handler
Receives the scan data handle
Close () you Close the module
scan () to start scanning



com.smartdevicesdk.stripcard
Magnetic stripe card interface (I2C communication)
Call Stripcardhelper.ReadCard () to get the data card after detailed see TestDEMO in MagneticCardActivity

com.smartdevicesdk.ui
UI control interface

com.smartdevicesdk.utils
String Functions Process Interface

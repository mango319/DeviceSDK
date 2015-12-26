# DeviceSDK
sdk for android device

project instruction:

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

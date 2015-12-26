package com.smartdevicesdk.scanner;


import com.zkc.io.EmGpio;

public class ScanGpio {

	// �򿪵�Դ
	public void openPower() {
		try {
			if (true == EmGpio.gpioInit()) {
				// ��Դ����
				EmGpio.setGpioOutput(111);
				EmGpio.setGpioDataLow(111);
				Thread.sleep(100);
				// ��Դ����
				EmGpio.setGpioOutput(111);
				EmGpio.setGpioDataHigh(111);
				Thread.sleep(100);
			}
			EmGpio.gpioUnInit();
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	public void closePower() {
		try {
			if (true == EmGpio.gpioInit()) {
				// ��Դ����
				EmGpio.setGpioOutput(111);
				EmGpio.setGpioDataLow(111);
				Thread.sleep(100);
				EmGpio.setGpioInput(111);
			}
			EmGpio.gpioUnInit();
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	// ��ɨ��
	public void openScan() {
		// ����ɨ��
		try {
			if (true == EmGpio.gpioInit()) {
				EmGpio.setGpioOutput(110);
				EmGpio.setGpioDataHigh(110);
				Thread.sleep(100);
				EmGpio.setGpioDataLow(110);
			}
			EmGpio.gpioUnInit();
		} catch (Exception e) {
			// TODO: handle exception
		}

	}

	// �ر�ɨ��
	public void closeScan() {
		// ����ɨ��
		try {
			if (true == EmGpio.gpioInit()) {
				EmGpio.setGpioOutput(110);
				EmGpio.setGpioDataHigh(110);
			}
			EmGpio.gpioUnInit();
		} catch (Exception e) {
			// TODO: handle exception
		}

	}
}
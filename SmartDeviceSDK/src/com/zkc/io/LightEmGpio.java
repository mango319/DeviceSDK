package com.zkc.io;

public class LightEmGpio {
	
	/**
	 * RED LED Powered Control
	 * @param flg
	 */
	public static void RedLightPower(boolean flg) {
		if (true == EmGpio.gpioInit()) {
			EmGpio.setGpioOutput(102);
			if (flg) {
				EmGpio.setGpioDataHigh(102);
			} else {
				EmGpio.setGpioDataLow(102);
			}
			EmGpio.gpioUnInit();
		}
	}
	
	/**
	 * Green LED Powered Control
	 * @param flg
	 */
	public static void GreenLightPower(boolean flg) {
		if (true == EmGpio.gpioInit()) {
			EmGpio.setGpioOutput(101);
			if (flg) {
				EmGpio.setGpioDataHigh(101);
			} else {
				EmGpio.setGpioDataLow(101);
			}
			EmGpio.gpioUnInit();
		}
	}
}

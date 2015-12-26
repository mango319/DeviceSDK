package com.smartdevicesdk.fingerprint;

public interface IUsbConnState {
    void onUsbConnected();

	void onUsbPermissionDenied();

	void onDeviceNotFound();
}

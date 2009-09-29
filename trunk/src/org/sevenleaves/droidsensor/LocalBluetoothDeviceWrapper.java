package org.sevenleaves.droidsensor;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import android.content.Context;
import android.util.Log;

public class LocalBluetoothDeviceWrapper extends LocalBluetoothDeviceAdapter {

	public static LocalBluetoothDeviceWrapper createInstance(Context context)
			throws Exception {

		LocalBluetoothDevice device = LocalBluetoothDevice
				.initLocalDevice(context);
		LocalBluetoothDeviceWrapper wrapper = new LocalBluetoothDeviceWrapper(
				device);

		return wrapper;
	}

	private LocalBluetoothDeviceWrapper(LocalBluetoothDevice target) {

		super(target);
	}

	public int getDiscoverableTimeout() throws Exception {
		Method getDiscoverableTimeoutMethod = exposeBluetoothServiceClass()
				.getMethod("getDiscoverableTimeout", new Class[] {});
		Integer returnValue = (Integer) getDiscoverableTimeoutMethod.invoke(
				exposeBluetoothService(), new Object[] {});
		return returnValue.intValue();
	}

	public void setDiscoverableTimeout(int timeout) throws Exception {
		Method setDiscoverableTimeoutMethod = exposeBluetoothServiceClass()
				.getMethod("setDiscoverableTimeout", new Class[] { int.class });
		setDiscoverableTimeoutMethod.invoke(exposeBluetoothService(),
				new Object[] { timeout });
	}

	public void setScanMode(int mode) throws Exception {
		Method setScanModeMethod = exposeBluetoothServiceClass().getMethod(
				"setScanMode", new Class[] { int.class });
		Log.d("setScanModeMethod", setScanModeMethod.toString());
		setScanModeMethod.invoke(exposeBluetoothService(),
				new Object[] { mode });
	}

	public int getScanMode() throws Exception {
		Method getScanModeMethod = exposeBluetoothServiceClass().getMethod(
				"getScanMode", new Class[] {});
		Integer returnValue = (Integer) getScanModeMethod.invoke(
				exposeBluetoothService(), new Object[] {});
		return returnValue.intValue();
	}

	public int getScanModeConnectableDiscoverable() throws Exception {
		Field scanModeConnectableDiscoverableField = exposeBluetoothServiceClass()
				.getField("SCAN_MODE_CONNECTABLE_DISCOVERABLE");
		Integer returnValue = (Integer) scanModeConnectableDiscoverableField
				.get(exposeBluetoothService());
		return returnValue.intValue();
	}

	public void periodicScan() throws Exception {
		Object receiver = exposeBluetoothBroadcastReceiver(); //
		// LocalBluetoothDevice device = exposeLocalBluetoothDevice();
		replaceDeclaredField(receiver, "didIstartedScan", Boolean.TRUE);
		Method startDiscoveryMethod = exposeBluetoothServiceClass().getMethod(
				"startPeriodicDiscovery", new Class[] {});
		startDiscoveryMethod.invoke(exposeBluetoothService(), new Object[] {});
		// bluetoothBroadcastReceiver.didIstartedScan = true;
		exposeDevices().clear();
	}

	public boolean isPeriodicScanning() throws Exception {

		Method isScanningMethod = exposeBluetoothServiceClass().getMethod(
				"isPeriodicDiscovery", new Class[] {});
		Boolean returnValue = (Boolean) isScanningMethod.invoke(
				exposeBluetoothService(), new Object[] {});
		return returnValue.booleanValue();
	}

	public void stopPeriodicScanning() throws Exception {

		// if (isScanning() && bluetoothBroadcastReceiver.didIstartedScan) {
		Method cancelDiscoveryMethod = exposeBluetoothServiceClass().getMethod(
				"stopPeriodicDiscovery", new Class[] {});
		cancelDiscoveryMethod.invoke(exposeBluetoothService(), new Object[] {});
		// }
	}

}

package org.sevenleaves.droidsensor.handlers;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.sevenleaves.droidsensor.bluetooth.BluetoothDeviceStub;
import org.sevenleaves.droidsensor.bluetooth.BluetoothDeviceStubFactory;

import android.content.Context;
import android.util.Log;

public class BluetoothEventControllerImpl implements BluetoothEventHandler,
		BluetoothEventController {

	private static final String TAG = BluetoothEventController.class.getSimpleName();
	
	private Map<BluetoothState, BluetoothEventHandler> _handlers;

	private BluetoothDeviceStub _bluetoothDevice;

	/**
	 * default pseudo handler
	 */
	private BluetoothEventHandler _activeHandler = new AbstractBluetoothEventHandler() {

		public BluetoothState getResponsibility() {

			return null;
		}
	};

	public BluetoothEventControllerImpl(Context context) {

		int size = BluetoothState.values().length;
		_handlers = new ConcurrentHashMap<BluetoothState, BluetoothEventHandler>(
				size);
		_bluetoothDevice = BluetoothDeviceStubFactory
				.createBluetoothServiceStub(context);
	}

	public BluetoothDeviceStub getBluetoothDevice() {

		return _bluetoothDevice;
	}

	public void setBluetoothEventController(BluetoothEventController controller) {

		throw new UnsupportedOperationException();
	}

	public BluetoothState getResponsibility() {

		throw new UnsupportedOperationException();
	}

	public void addHandler(BluetoothEventHandler handler) {

		handler.setBluetoothEventController(this);
		_handlers.put(handler.getResponsibility(), handler);
	}

	public void setCurrentState(BluetoothState currentState) {

		if(!_handlers.containsKey(currentState)){
			
			return;
		}
		
		_activeHandler = _handlers.get(currentState);
	}

	public void onRemoteDeviceDisappeared(String address) {

		Log.d(TAG, _activeHandler.toString() + "#onRemoteDeviceDisappeared");
		
		_activeHandler.onRemoteDeviceDisappeared(address);
	}

	public void onRemoteDeviceFound(String address) {

		Log.d(TAG, _activeHandler.toString() + "#onRemoteDeviceFound");
		
		_activeHandler.onRemoteDeviceFound(address);
	}

	public void onRemoteNameUpdated(String address, String name) {

		Log.d(TAG, _activeHandler.toString() + "#onRemoteNameUpdate");
		
		_activeHandler.onRemoteNameUpdated(address, name);
	}

	public void onScanModeChangedConnectable() {

		Log.d(TAG, _activeHandler.toString() + "#onScanModeChangedConnectable");
		
		BluetoothEventHandler h = _activeHandler;
		setCurrentState(BluetoothState.SCAN_MODE_CONNECTABLE);
		h.onScanModeChangedConnectable();
	}

	public void onScanModeChangedConnectableDiscoverable() {

		Log.d(TAG, _activeHandler.toString() + "#onScanModeChangedConnectableDisable");

		BluetoothEventHandler h = _activeHandler;
		setCurrentState(BluetoothState.SCAN_MODE_CONNECTABLE_DISCOVERABLE);
		h.onScanModeChangedConnectableDiscoverable();
	}

	public void onScanModeChangedNone() {

		Log.d(TAG, _activeHandler.toString() + "#onScanModeChangedNone");

		BluetoothEventHandler h = _activeHandler;
		setCurrentState(BluetoothState.SCAN_MODE_NONE);
		h.onScanModeChangedNone();
	}

	public void onStateChangedOff() {

		Log.d(TAG, _activeHandler.toString() + "#onStateChangedOff");

		BluetoothEventHandler h = _activeHandler;
		setCurrentState(BluetoothState.STATE_OFF);
		h.onStateChangedOff();
	}

	public void onStateChangedOn() {

		Log.d(TAG, _activeHandler.toString() + "#onStateChangedOn");

		BluetoothEventHandler h = _activeHandler;
		setCurrentState(BluetoothState.STATE_ON);
		h.onStateChangedOn();
	}

	public void onStateChangedTurningOff() {

		Log.d(TAG, _activeHandler.toString() + "#onStateChangedTurningOff");
		
		BluetoothEventHandler h = _activeHandler;
		setCurrentState(BluetoothState.STATE_TURNING_OFF);
		h.onStateChangedTurningOff();
	}

	public void onStateChangedTurningOn() {

		Log.d(TAG, _activeHandler.toString() + "#onStateChangedTurningOn");
		
		BluetoothEventHandler h = _activeHandler;
		setCurrentState(BluetoothState.STATE_TURNING_ON);
		h.onStateChangedTurningOn();
	}
	
	public void onDiscoveryStarted() {
	
		Log.d(TAG, _activeHandler.toString() + "#onDiscoveryStarted");
		
		BluetoothEventHandler h = _activeHandler;
		setCurrentState(BluetoothState.DISCOVERY_STARTED);
		h.onDiscoveryStarted();
	}
	
	public void onDiscoveryCompleted() {
	
		Log.d(TAG, _activeHandler.toString() + "#onDiscoveryCompleted");	

		BluetoothEventHandler h = _activeHandler;
		setCurrentState(BluetoothState.DISCOVERY_COMPLETED);
		h.onDiscoveryCompleted();
	}
}

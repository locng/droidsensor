package org.sevenleaves.droidsensor.bluetooth;

import static org.sevenleaves.droidsensor.bluetooth.BluetoothIntentConstants.ADDRESS;
import static org.sevenleaves.droidsensor.bluetooth.BluetoothIntentConstants.BLUETOOTH_STATE;
import static org.sevenleaves.droidsensor.bluetooth.BluetoothIntentConstants.BLUETOOTH_STATE_CHANGED_ACTION;
import static org.sevenleaves.droidsensor.bluetooth.BluetoothIntentConstants.REMOTE_DEVICE_DISAPPEARED_ACTION;
import static org.sevenleaves.droidsensor.bluetooth.BluetoothIntentConstants.REMOTE_DEVICE_FOUND_ACTION;
import static org.sevenleaves.droidsensor.bluetooth.BluetoothIntentConstants.RSSI;
import static org.sevenleaves.droidsensor.bluetooth.BluetoothIntentConstants.SCAN_MODE;
import static org.sevenleaves.droidsensor.bluetooth.BluetoothIntentConstants.SCAN_MODE_CHANGED_ACTION;
import static org.sevenleaves.droidsensor.bluetooth.BluetoothServiceStub.BLUETOOTH_STATE_OFF;
import static org.sevenleaves.droidsensor.bluetooth.BluetoothServiceStub.BLUETOOTH_STATE_ON;
import static org.sevenleaves.droidsensor.bluetooth.BluetoothServiceStub.SCAN_MODE_CONNECTABLE;
import static org.sevenleaves.droidsensor.bluetooth.BluetoothServiceStub.SCAN_MODE_CONNECTABLE_DISCOVERABLE;
import static org.sevenleaves.droidsensor.bluetooth.BluetoothServiceStub.SCAN_MODE_NONE;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

public class BluetoothBroadcastReceiver extends BroadcastReceiver {

	private interface IntentHandler {

		void handleIntent(Context context, Intent intent);
	}

	private interface ListenerInvoker {

		void invokeListenr(BluetoothDeviceListener listener);
	}

	private static final BluetoothBroadcastReceiver SINGLETON = new BluetoothBroadcastReceiver();

	private Map<String, IntentHandler> _handlerMapping;

	private List<BluetoothDeviceListener> _listeners;

	public static BluetoothBroadcastReceiver getInstance() {

		return SINGLETON;
	}

	private BluetoothBroadcastReceiver() {

		_handlerMapping = Collections
				.synchronizedMap(new HashMap<String, IntentHandler>());
		_listeners = Collections
				.synchronizedList(new ArrayList<BluetoothDeviceListener>());
		registerHandlers();
	}

	private void registerHandler(String action, IntentHandler handler) {

		_handlerMapping.put(action, handler);
	}

	private BluetoothServiceStub getStub(Context context) {

		BluetoothServiceStub res = BluetoothServiceStubFactory
				.createBluetoothServiceStub(context);

		return res;
	}

	private void invokeListeners(ListenerInvoker invoker) {

		for (int i = 0, size = _listeners.size(); i < size; ++i) {

			BluetoothDeviceListener listener = _listeners.get(i);
			invoker.invokeListenr(listener);
		}
	}

	private RemoteBluetoothDevice createRemoteBluetoothDevice(Context context, Intent intent) {

		String address = intent.getStringExtra(ADDRESS);
		BluetoothServiceStub stub = getStub(context);
		String name = stub.getRemoteName(address);
		short rssi = intent.getShortExtra(RSSI, Short.MIN_VALUE);
		RemoteBluetoothDeviceImpl res = new RemoteBluetoothDeviceImpl(address,
				rssi);
		res.setName(name);

		return res;
	}

	private void registerHandlers() {

		registerHandler(REMOTE_DEVICE_FOUND_ACTION, new IntentHandler() {

			public void handleIntent(final Context context, final Intent intent) {
				
				invokeListeners(new ListenerInvoker() {

					public void invokeListenr(BluetoothDeviceListener listener) {

						RemoteBluetoothDevice device = createRemoteBluetoothDevice(context, intent);
						listener.onRemoteDeviceFound(context, device);
					}
				});
			}
		});

		// DISCOVERY_COMPLETED_ACTION

		// DISCOVERY_STARTED_ACTION

		// BLUETOOTH_STATE_CHANGED_ACTION
		// BLUETOOTH_STATE_OFF
		// BLUETOOTH_STATE_ON
		// BLUETOOTH_STATE_TURNING_OFF
		// BLUETOOTH_STATE_TURNING_ON
		registerHandler(BLUETOOTH_STATE_CHANGED_ACTION, new IntentHandler() {

			public void handleIntent(final Context context, final Intent intent) {

				invokeListeners(new ListenerInvoker() {

					public void invokeListenr(BluetoothDeviceListener listener) {

						int bluetoothState = intent.getIntExtra(
								BLUETOOTH_STATE, -1);

						switch (bluetoothState) {

						case BLUETOOTH_STATE_ON:

							startPeriodicDiscovery(context);
							listener.onEnabled(context);

							break;

						case BLUETOOTH_STATE_OFF:

							listener.onDisabled(context);

							break;

						default:
							break;
						}
					}
				});
			}
		});

		// NAME_CHANGED_ACTION

		// SCAN_MODE_CHANGED_ACTION
		// SCAN_MODE_CONNECTABLE
		// SCAN_MODE_CONNECTABLE_DISCOVERABLE
		// SCAN_MODE_NONE
		registerHandler(SCAN_MODE_CHANGED_ACTION, new IntentHandler() {

			public void handleIntent(final Context context, final Intent intent) {

				invokeListeners(new ListenerInvoker() {

					public void invokeListenr(BluetoothDeviceListener listener) {

						int scanMode = intent.getIntExtra(SCAN_MODE, -1);

						switch (scanMode) {

						case SCAN_MODE_CONNECTABLE:

							listener.onScanModeConnectable(context);

							break;

						case SCAN_MODE_CONNECTABLE_DISCOVERABLE:

							listener.onScanModeConnectableDiscoverable(context);

							break;

						case SCAN_MODE_NONE:

							listener.onScanModeNone(context);

							break;

						default:
							break;
						}
					}
				});
			}
		});

		// PAIRING_REQUEST_ACTION

		// PAIRING_CANCEL_ACTION

		// REMOTE_DEVICE_DISAPPEARED_ACTION
		// void onRemoteDeviceDisappeared(BluetoothDevice device);
		registerHandler(REMOTE_DEVICE_DISAPPEARED_ACTION, new IntentHandler() {

			public void handleIntent(final Context context, final Intent intent) {
				
				final String address = intent.getStringExtra(ADDRESS);
				
				invokeListeners(new ListenerInvoker() {

					public void invokeListenr(BluetoothDeviceListener listener) {

						
						listener.onRemoteDeviceDisappeared(context, address);
					}
				});
			}
		});
	}

	@Override
	public void onReceive(final Context context, final Intent intent) {

		Log.d("BluetoothBroadcastReceiver", intent.getAction());
		
		String action = intent.getAction();

		if (!_handlerMapping.containsKey(action)) {

			return;
		}

		IntentHandler handler = _handlerMapping.get(action);
		handler.handleIntent(context, intent);
	}

	public synchronized void addListener(BluetoothDeviceListener listener) {

		// if (_listeners.contains(listener)) {
		//
		// return;
		// }
		_listeners.clear();

		_listeners.add(listener);
	}

	public void removeListener(BluetoothDeviceListener listener) {

		_listeners.remove(listener);
	}

	// public boolean isRegisterd() {
	//
	// return _registerd;
	// }

	private void startPeriodicDiscovery(Context context) {

		BluetoothServiceStub stub = getStub(context);

		// if(stub.isDiscovering()){
		//			
		// stub.cancelDiscovery();
		// }
		//		
		// if(stub.isPeriodicDiscovery()){
		//			
		// stub.stopPeriodicDiscovery();
		// }

		if (stub.getScanMode() != SCAN_MODE_CONNECTABLE_DISCOVERABLE) {

			stub.setScanMode(SCAN_MODE_CONNECTABLE_DISCOVERABLE);
		}

		if (stub.getDiscoverableTimeout() > 0) {

			stub.setDiscoverableTimeout(0);
		}

		if (!stub.isPeriodicDiscovery()) {

			stub.startPeriodicDiscovery();
		}
	}

	public synchronized void unregisterSelf(Context context,
			BluetoothSettings settings) {

		Log.d("BluetoothBroadcastReceiver", "unregister receiver");

		BluetoothServiceStub stub = getStub(context);

		try {

			context.unregisterReceiver(this);
		} catch (Exception e) {

			; // nop.
		}

		_listeners.clear();
		settings.load(stub);
	}

	public synchronized void registerSelf(Context context,
			BluetoothSettings settings) {

		BluetoothServiceStub stub = getStub(context);
		IntentFilter filter = new IntentFilter();

		for (String action : _handlerMapping.keySet()) {

			filter.addAction(action);
		}

		try {

			Log.d("BluetoothBroadcastReceiver", "unregister receiver");
			
			context.unregisterReceiver(this);
		} catch (Exception e) {

			// 登録されてなければ例外を吐くので、その時に登録.
			// という良くないやり方。
			
			Log.d("BluetoothBroadcastReceiver", "registerReceiver");
			
			context.registerReceiver(this, filter);

			if (stub.isEnabled()) {

				settings.save(stub);
			}
		}

		if (stub.isEnabled()) {

			startPeriodicDiscovery(context);

			return;
		}

		stub.enable();
	}
}

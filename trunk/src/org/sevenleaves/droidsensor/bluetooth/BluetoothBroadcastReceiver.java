package org.sevenleaves.droidsensor.bluetooth;

import static org.sevenleaves.droidsensor.bluetooth.BluetoothDeviceStub.BLUETOOTH_STATE_OFF;
import static org.sevenleaves.droidsensor.bluetooth.BluetoothDeviceStub.BLUETOOTH_STATE_ON;
import static org.sevenleaves.droidsensor.bluetooth.BluetoothDeviceStub.SCAN_MODE_CONNECTABLE;
import static org.sevenleaves.droidsensor.bluetooth.BluetoothDeviceStub.SCAN_MODE_CONNECTABLE_DISCOVERABLE;
import static org.sevenleaves.droidsensor.bluetooth.BluetoothDeviceStub.SCAN_MODE_NONE;
import static org.sevenleaves.droidsensor.bluetooth.BluetoothIntentConstants.ADDRESS;
import static org.sevenleaves.droidsensor.bluetooth.BluetoothIntentConstants.BLUETOOTH_STATE;
import static org.sevenleaves.droidsensor.bluetooth.BluetoothIntentConstants.NAME;
import static org.sevenleaves.droidsensor.bluetooth.BluetoothIntentConstants.RSSI;
import static org.sevenleaves.droidsensor.bluetooth.BluetoothIntentConstants.SCAN_MODE;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

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

	private Map<String, RemoteBluetoothDeviceImpl> _devices;

	public static BluetoothBroadcastReceiver getInstance() {

		return SINGLETON;
	}

	private BluetoothBroadcastReceiver() {

		_handlerMapping = Collections
				.synchronizedMap(new HashMap<String, IntentHandler>());
		_listeners = Collections
				.synchronizedList(new ArrayList<BluetoothDeviceListener>());
		_devices = new ConcurrentHashMap<String, RemoteBluetoothDeviceImpl>();
		registerHandlers();
	}

	private void registerHandler(BluetoothIntent intent, IntentHandler handler) {

		_handlerMapping.put(intent.getAction(), handler);
	}

	private BluetoothDeviceStub getStub(Context context) {

		BluetoothDeviceStub res = BluetoothDeviceStubFactory
				.createBluetoothServiceStub(context);

		return res;
	}

	private void invokeListeners(ListenerInvoker invoker) {

		for (int i = 0, size = _listeners.size(); i < size; ++i) {

			BluetoothDeviceListener listener = _listeners.get(i);
			invoker.invokeListenr(listener);
		}
	}

	private RemoteBluetoothDeviceImpl createRemoteDeviceIfNeccesary(
			Context context, Intent intent) {

		String address = intent.getStringExtra(ADDRESS);
		short rssi = intent.getShortExtra(RSSI, Short.MIN_VALUE);

		if (_devices.containsKey(address)) {

			RemoteBluetoothDeviceImpl res = _devices.get(address);
			res.setRssi(rssi);

			return res;
		}

		BluetoothDeviceStub stub = getStub(context);
		RemoteBluetoothDeviceImpl res = new RemoteBluetoothDeviceImpl(stub,
				address, rssi);

		return res;
	}

	private RemoteBluetoothDevice updateRemoteName(Context context,
			Intent intent) {

		RemoteBluetoothDeviceImpl res = createRemoteDeviceIfNeccesary(context,
				intent);
		String name = intent.getStringExtra(NAME);
		res.setName(name);

		return res;
	}

	private RemoteBluetoothDevice createRemoteBluetoothDevice(Context context,
			Intent intent) {

		RemoteBluetoothDeviceImpl res = createRemoteDeviceIfNeccesary(context,
				intent);

		return res;
	}

	private void registerHandlers() {

		registerHandler(BluetoothIntent.REMOTE_DEVICE_FOUND,
				new IntentHandler() {

					public void handleIntent(final Context context,
							final Intent intent) {

						invokeListeners(new ListenerInvoker() {

							public void invokeListenr(
									BluetoothDeviceListener listener) {

								RemoteBluetoothDevice device = createRemoteBluetoothDevice(
										context, intent);
								listener.onRemoteDeviceFound(context, device);
							}
						});
					}
				});

		registerHandler(BluetoothIntent.REMOTE_NAME_UPDATED,
				new IntentHandler() {

					public void handleIntent(final Context context,
							final Intent intent) {

						invokeListeners(new ListenerInvoker() {

							public void invokeListenr(
									BluetoothDeviceListener listener) {

								RemoteBluetoothDevice device = updateRemoteName(
										context, intent);
								listener.onRemoteNameUpdated(context, device);
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
		registerHandler(BluetoothIntent.BLUETOOTH_STATE_CHANGED,
				new IntentHandler() {

					public void handleIntent(final Context context,
							final Intent intent) {

						invokeListeners(new ListenerInvoker() {

							public void invokeListenr(
									BluetoothDeviceListener listener) {

								int bluetoothState = intent.getIntExtra(
										BLUETOOTH_STATE, -1);

								switch (bluetoothState) {

								case BLUETOOTH_STATE_ON:

									listener.onEnabled(context);
									startDiscovery(context);

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
		registerHandler(BluetoothIntent.SCAN_MODE_CHANGED, new IntentHandler() {

			public void handleIntent(final Context context, final Intent intent) {

				final BluetoothDeviceStub stub = BluetoothDeviceStubFactory
						.createBluetoothServiceStub(context);

				invokeListeners(new ListenerInvoker() {

					public void invokeListenr(BluetoothDeviceListener listener) {

						int scanMode = intent.getIntExtra(SCAN_MODE, -1);

						switch (scanMode) {

						case SCAN_MODE_CONNECTABLE:

							if (stub.isPeriodicDiscovery()) {

								stub.stopPeriodicDiscovery();
							}

							stub.setScanMode(0x3);
							
							listener.onScanModeConnectable(context);

							break;

						case SCAN_MODE_CONNECTABLE_DISCOVERABLE:

							// if (stub.isDiscovering()) {
							//
							// stub.cancelDiscovery();
							// }

							// if (!stub.isPeriodicDiscovery()) {

							stub.startPeriodicDiscovery();
							// }

							listener.onScanModeConnectableDiscoverable(context);

							break;

						case SCAN_MODE_NONE:

							if (stub.isPeriodicDiscovery()) {

								stub.stopPeriodicDiscovery();
							}

							stub.setScanMode(0x3);
							
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
		registerHandler(BluetoothIntent.REMOTE_DEVICE_DISAPPEARED,
				new IntentHandler() {

					public void handleIntent(final Context context,
							final Intent intent) {

						final String address = intent.getStringExtra(ADDRESS);

						_devices.remove(address);

						invokeListeners(new ListenerInvoker() {

							public void invokeListenr(
									BluetoothDeviceListener listener) {

								listener.onRemoteDeviceDisappeared(context,
										address);
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

		if (_listeners.contains(listener)) {

			return;
		}

		_listeners.add(listener);
	}

	public void removeListener(BluetoothDeviceListener listener) {

		_listeners.remove(listener);
	}

	private void startDiscovery(Context context) {

		Log.d("BluetoothBroadcastReceiver", "startDiscovery");

		BluetoothDeviceStub stub = getStub(context);

		if (stub.isDiscovering()) {

			stub.cancelDiscovery();
		}

		if (stub.getScanMode() != SCAN_MODE_CONNECTABLE_DISCOVERABLE) {

			stub.setScanMode(SCAN_MODE_CONNECTABLE_DISCOVERABLE);
		}

		Log.d("BluetoothBroadcastReceiver", "startDiscovery");
	}

	public synchronized void unregisterSelf(Context context,
			BluetoothSettings settings) {

		Log.d("BluetoothBroadcastReceiver", "unregister receiver");

		BluetoothDeviceStub stub = getStub(context);
		stub.stopPeriodicDiscovery();

		try {

			context.unregisterReceiver(this);
		} catch (Exception e) {

			; // nop.
		}

		_listeners.clear();
		settings.load(stub);
	}

	public synchronized void registerSelf(final Context context,
			BluetoothSettings settings) {

		BluetoothDeviceStub stub = getStub(context);

		// if (_registered) {
		//
		// if (stub.isEnabled()) {
		//
		// startPeriodicDiscovery(context);
		//
		// return;
		// }
		//
		// return;
		// }

		IntentFilter filter = new IntentFilter();

		for (String action : _handlerMapping.keySet()) {

			filter.addAction(action);
		}

		// 登録されてなければ例外を吐くので、その時に登録.
		// という良くないやり方。

		Log.d("BluetoothBroadcastReceiver", "register receiver:"
				+ this.toString());

		context.registerReceiver(this, filter);

		if (stub.isEnabled()) {

			settings.save(stub);
		}

		if (stub.isEnabled()) {

			invokeListeners(new ListenerInvoker() {

				public void invokeListenr(BluetoothDeviceListener listener) {

					listener.onEnabled(context);
				}
			});

			startDiscovery(context);

			return;
		}

		stub.enable();
	}

	public void restart(Context context) {

		startDiscovery(context);
	}
}

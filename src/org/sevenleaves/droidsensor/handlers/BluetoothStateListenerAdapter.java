/*
 * Copyright (C) 2009, DroidSensor - http://code.google.com/p/droidsensor/
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.sevenleaves.droidsensor.handlers;

import static org.sevenleaves.droidsensor.bluetooth.BluetoothDeviceStub.BLUETOOTH_STATE_OFF;
import static org.sevenleaves.droidsensor.bluetooth.BluetoothDeviceStub.BLUETOOTH_STATE_ON;
import static org.sevenleaves.droidsensor.bluetooth.BluetoothDeviceStub.BLUETOOTH_STATE_TURNING_OFF;
import static org.sevenleaves.droidsensor.bluetooth.BluetoothDeviceStub.BLUETOOTH_STATE_TURNING_ON;
import static org.sevenleaves.droidsensor.bluetooth.BluetoothDeviceStub.SCAN_MODE_CONNECTABLE;
import static org.sevenleaves.droidsensor.bluetooth.BluetoothDeviceStub.SCAN_MODE_CONNECTABLE_DISCOVERABLE;
import static org.sevenleaves.droidsensor.bluetooth.BluetoothDeviceStub.SCAN_MODE_NONE;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.sevenleaves.droidsensor.bluetooth.BluetoothIntent;
import org.sevenleaves.droidsensor.bluetooth.BluetoothUtils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

/**
 * Bluetoothインテントによる通知を{@link BluetoothStateHandler}にdispatchするためのアダプタクラス.
 * 
 * @author esmasui@gmail.com
 * 
 */
public class BluetoothStateListenerAdapter extends BroadcastReceiver {

	/**
	 * Logのカテゴリ.
	 */
	private static final String TAG = BluetoothStateListenerAdapter.class
			.getSimpleName();

	private interface IntentHandler {

		void handleIntent(Context context, Intent intent);
	}

	private interface HandlerInvoker {

		void invokeHandler(BluetoothStateHandler hanlder);
	}

	private Map<BluetoothIntent, IntentHandler> _handlerMapping;

	private BluetoothStateHandler _handler;

	public BluetoothStateListenerAdapter() {

		int size = BluetoothIntent.values().length;
		_handlerMapping = new ConcurrentHashMap<BluetoothIntent, IntentHandler>(
				size);
		registerEvents();
	}

	@Override
	public void onReceive(final Context context, final Intent intent) {

		Log.d(TAG, intent.getAction());

		final String action = intent.getAction();
		BluetoothIntent key = BluetoothIntent.fromAction(action);

		if (!_handlerMapping.containsKey(key)) {

			return;
		}

		IntentHandler handler = _handlerMapping.get(key);
		handler.handleIntent(context, intent);
	}

	public synchronized void unregisterSelf(Context context) {

		Log.d(TAG, "unregister receiver");

		try {

			context.unregisterReceiver(this);
		} catch (Exception e) {

			; // nop.
		}
	}

	public synchronized void registerSelf(final Context context) {

		Log.d(TAG, "register receiver:" + this.toString());

		IntentFilter filter = new IntentFilter();

		for (BluetoothIntent e : _handlerMapping.keySet()) {

			filter.addAction(e.getAction());
		}

		context.registerReceiver(this, filter);
	}

	public void setHandler(BluetoothStateHandler handler) {

		_handler = handler;
	}

	private void invokeHandler(HandlerInvoker invoker) {

		invoker.invokeHandler(_handler);
	}

	private void registerHandler(BluetoothIntent intent, IntentHandler handler) {

		_handlerMapping.put(intent, handler);
	}

	private void registerRemoteDeviceFound() {

		registerHandler(BluetoothIntent.REMOTE_DEVICE_FOUND,
				new IntentHandler() {

					public void handleIntent(final Context context,
							final Intent intent) {

						final String address = BluetoothUtils
								.getAddress(intent);

						invokeHandler(new HandlerInvoker() {

							public void invokeHandler(
									BluetoothStateHandler handler) {

								handler.onRemoteDeviceFound(address);
							}
						});
					}
				});
	}

	private void registerRemoteNameUpdated() {

		registerHandler(BluetoothIntent.REMOTE_NAME_UPDATED,
				new IntentHandler() {

					public void handleIntent(final Context context,
							final Intent intent) {

						final String address = BluetoothUtils
								.getAddress(intent);
						final String name = BluetoothUtils.getName(intent);

						invokeHandler(new HandlerInvoker() {

							public void invokeHandler(
									BluetoothStateHandler handler) {

								handler.onRemoteNameUpdated(address, name);
							}
						});
					}
				});
	}

	private void registerStateChanged() {

		registerHandler(BluetoothIntent.BLUETOOTH_STATE_CHANGED,
				new IntentHandler() {

					public void handleIntent(final Context context,
							final Intent intent) {

						final int state = BluetoothUtils.getState(intent);

						invokeHandler(new HandlerInvoker() {

							public void invokeHandler(
									BluetoothStateHandler handler) {

								switch (state) {

								case BLUETOOTH_STATE_OFF:

									handler.onStateChangedOff();

									break;

								case BLUETOOTH_STATE_TURNING_ON:

									handler.onStateChangedTurningOn();

									break;

								case BLUETOOTH_STATE_ON:

									handler.onStateChangedOn();

									break;

								case BLUETOOTH_STATE_TURNING_OFF:

									handler.onStateChangedTurningOff();

									break;

								default:
									break;
								}
							}
						});
					}
				});
	}

	private void registerScanModeChanged() {

		registerHandler(BluetoothIntent.SCAN_MODE_CHANGED, new IntentHandler() {

			public void handleIntent(final Context context, final Intent intent) {

				final int scanMode = BluetoothUtils.getScanMode(intent);

				invokeHandler(new HandlerInvoker() {

					public void invokeHandler(BluetoothStateHandler handler) {

						switch (scanMode) {

						case SCAN_MODE_CONNECTABLE:

							handler.onScanModeChangedConnectable();

							break;

						case SCAN_MODE_CONNECTABLE_DISCOVERABLE:

							handler.onScanModeChangedConnectableDiscoverable();

							break;

						case SCAN_MODE_NONE:

							handler.onScanModeChangedNone();

							break;

						default:
							break;
						}
					}
				});
			}
		});
	}

	private void registerRemoteDeviceDisappeared() {

		registerHandler(BluetoothIntent.REMOTE_DEVICE_DISAPPEARED,
				new IntentHandler() {

					public void handleIntent(final Context context,
							final Intent intent) {

						final String address = BluetoothUtils
								.getAddress(intent);

						invokeHandler(new HandlerInvoker() {

							public void invokeHandler(
									BluetoothStateHandler handler) {

								handler.onRemoteDeviceDisappeared(address);
							}
						});
					}
				});
	}

	private void registerDiscoveryStarted() {

		registerHandler(BluetoothIntent.DISCOVERY_STARTED, new IntentHandler() {

			public void handleIntent(final Context context, final Intent intent) {

				invokeHandler(new HandlerInvoker() {

					public void invokeHandler(BluetoothStateHandler handler) {

						handler.onDiscoveryStarted();
					}
				});
			}
		});
	}

	private void registerDiscoveryCompleted() {

		registerHandler(BluetoothIntent.DISCOVERY_COMPLETED,
				new IntentHandler() {

					public void handleIntent(final Context context,
							final Intent intent) {

						invokeHandler(new HandlerInvoker() {

							public void invokeHandler(
									BluetoothStateHandler handler) {

								handler.onDiscoveryCompleted();
							}
						});
					}
				});
	}

	private void registerEvents() {

		registerStateChanged();
		registerScanModeChanged();
		registerRemoteDeviceFound();
		registerRemoteDeviceDisappeared();
		registerRemoteNameUpdated();
		registerDiscoveryStarted();
		registerDiscoveryCompleted();
	}

}

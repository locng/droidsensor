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

package org.sevenleaves.droidsensor;

import org.sevenleaves.droidsensor.OptionsMenuHelper.MenuItemCallback;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

/**
 * TODO プログレスのキャンセル処理.
 * 
 * @author smasui@gmail.com
 * 
 */
public class DroidSensorActivity extends ListActivitySupport {

	private static final String TAG = DroidSensorActivity.class.getSimpleName();

	private static final int CALLBACK_MESSAGE = 1;

	private ProgressDialog _progressDialog;

	private IDroidSensorService _service;

	private Handler _handler = new Handler() {

		@Override
		public void dispatchMessage(Message msg) {

			if (msg.what != CALLBACK_MESSAGE) {

				super.dispatchMessage(msg);

				return;
			}

			Toast.makeText(DroidSensorActivity.this, (CharSequence) msg.obj,
					Toast.LENGTH_SHORT).show();
		};
	};

	private IDroidSensorCallbackListener _listener = new IDroidSensorCallbackListener.Stub() {

		public void deviceFound(String message) throws RemoteException {

			_handler.sendMessage(_handler.obtainMessage(CALLBACK_MESSAGE,
					message));
		}
	};

	private ServiceConnection _serviceConnection = new ServiceConnection() {

		public void onServiceConnected(ComponentName name, IBinder service) {

			_service = IDroidSensorService.Stub.asInterface(service);

			try {

				_service.addListener(_listener);
			} catch (RemoteException e) {

				Log.e(TAG, e.getLocalizedMessage(), e);
			}
		}

		public void onServiceDisconnected(ComponentName name) {

			_service = null;
		}
	};

	private Runnable _bindCallback = new Runnable() {

		public void run() {

			SettingsManager s = SettingsManager
					.getInstance(DroidSensorActivity.this);
			boolean verified = true;

			boolean basicAccountUses = s.isBasicAccountUses();

			if (basicAccountUses) {

				verified = TwitterUtils.verifyCredentials(s.getTwitterId(), s
						.getTwitterPassword());
			}

			boolean optionalAccountUses = s.isOptionalAccountUses();

			if (verified && optionalAccountUses) {

				verified = TwitterUtils
						.verifyCredentials(s.getOptionalTwitterId(), s
								.getOptionalTwitterPassword());
			}

			_progressDialog.dismiss();

			if (!verified) {

				Runnable alert = new Runnable() {

					public void run() {

						AlertDialog alertDialog = new AlertDialog.Builder(
								DroidSensorActivity.this).setTitle(
								"Authentication failed.").setPositiveButton(
								"Open Settings", new OnClickListener() {

									public void onClick(DialogInterface dialog,
											int which) {

										Intent settings = new Intent(
												SettingsActivity.class
														.getName());
										startActivity(settings);
									}
								}).create();
						alertDialog.show();
					}
				};

				new Handler().post(alert);

				return;
			}

			// Intent si = new Intent(DroidSensorActivity.this,
			// IDroidSensorService.class);
			// si.setAction(ServiceUtils.START_ACTION);
			Intent si = new Intent(IDroidSensorService.class.getName());
			startService(si);
		}
	};

	/**
	 * Discoveryメニューを登録する.
	 * 
	 * @param helper
	 */
	private void addDiscoveryMenu(OptionsMenuHelper helper) {

		helper.addItem(R.string.menu_discover, android.R.drawable.ic_menu_view,

		new MenuItemCallback() {

			public void onOpend(MenuItem item) {

				onDiscoveryMenuOpened(item);
			}

			public void onSelected(MenuItem item) {

				onDiscoveryMenuSelected(item);
			}
		});
	}

	/**
	 * Settingsメニューを登録する.
	 * 
	 * @param helper
	 */
	private void addSettingsMenu(OptionsMenuHelper helper) {

		helper.addItem(R.string.menu_settings,
				android.R.drawable.ic_menu_preferences,

				new MenuItemCallback() {

					public void onOpend(MenuItem item) {

						onSettingsMenuOpened(item);
					}

					public void onSelected(MenuItem item) {

						onSettingsMenuSelected(item);
					}
				});
	}

	private ProgressDialog createProgressDialog(OnClickListener cancelListener) {

		ProgressDialog dialog = new ProgressDialog(DroidSensorActivity.this);
		dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		// dialog.setCancelable(false);
		// dialog.setCancelable(true);
		dialog.setTitle("Processing...");
		dialog.setMessage("Verify Credentials");
		// dialog.setButton("Cancel", cancelListener);

		return dialog;
	}

	private boolean isServiceStarted() {

		try {

			return _service.isStarted();
		} catch (RemoteException e) {

			return false;
		}
	};

	// @Override
	// protected void onListItemClick(ListView l, View v, int position, long id)
	// {
	// super.onListItemClick(l, v, position, id);
	// Toast.makeText(this,
	// String.format("l:%s, v:%s, position:%d, id:%d", l.getClass(),
	// v.getClass(), position, id),
	// Toast.LENGTH_LONG).show();
	// }

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.droid_sensor);
		Intent bi = new Intent(IDroidSensorService.class.getName());
		bindService(bi, _serviceConnection, BIND_AUTO_CREATE);

		// ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
		// android.R.layout.simple_list_item_1, new String[] { "Led Zeppelin",
		// "Jimi Hendrix",
		// "The Black Crowes" });
		//	        
		// setListAdapter(adapter);
	}

	@Override
	protected void onCreateOptionMenuInternal(OptionsMenuHelper helper) {

		addDiscoveryMenu(helper);
		addSettingsMenu(helper);
	}

	@Override
	protected void onDestroy() {

		super.onDestroy();
		unbindService(_serviceConnection);
	}

	/**
	 * Discoverメニューが開かれた時に呼ばれるコールバック.
	 * 
	 * @param item
	 */
	private void onDiscoveryMenuOpened(MenuItem item) {

		boolean scanning = isServiceStarted();

		if (scanning) {

			item.setIcon(android.R.drawable.ic_menu_close_clear_cancel);
			item.setTitle(R.string.menu_stop_discover);

			return;
		}

		item.setIcon(android.R.drawable.ic_menu_search);
		item.setTitle(R.string.menu_discover);
	}

	/**
	 * Discoverメニューが押された時に呼ばれるコールバック.
	 * 
	 * @param item
	 */
	private void onDiscoveryMenuSelected(MenuItem item) {

		boolean scanning = isServiceStarted();

		if (scanning) {

			stopService();

			return;
		}

		startService();
	}

	/**
	 * Settingsメニューが開かれた時に呼ばれるコールバック.
	 * 
	 * @param item
	 */
	private void onSettingsMenuOpened(MenuItem item) {

		; // nop
	}

	/**
	 * Settingsメニューが押された時に呼ばれるコールバック.
	 * 
	 * @param item
	 */
	private void onSettingsMenuSelected(MenuItem item) {

		Intent intent = new Intent(SettingsActivity.class.getName());
		startActivity(intent);
	}

	private void showError(String message) {

		// Toast toast = Toast.makeText(DroidSensorActivity.this, message,
		// Toast.LENGTH_SHORT);
		// toast.show();
	}

	private void startService() {

		_progressDialog = createProgressDialog(new OnClickListener() {

			public void onClick(DialogInterface dialog, int which) {

				dialog.cancel();
			}
		});

		_progressDialog.show();

		new Thread() {

			public void run() {

				// _handler.post(_bindCallback);
				_bindCallback.run();
			};
		}.start();
	}

	private void stopService() {

		Log.d("DroidSensorAcivity", "stopService");
		try {

			_service.stopService();
		} catch (RemoteException e) {

			; // nop.
		}
	}
}

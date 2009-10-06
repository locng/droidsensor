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
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

/**
 * TODO プログレスのキャンセル処理.
 * 
 * @author smasui@gmail.com
 * 
 */
public class DroidSensorActivity extends ListActivity {

	private OptionsMenuHelper _menuHelper;

	private ProgressDialog _progressDialog;

	private Handler _handler = new Handler();

	private IDroidSensorService _service;

	private ServiceConnection _serviceConnection = new ServiceConnection() {

		public void onServiceConnected(ComponentName name, IBinder service) {

			_service = IDroidSensorService.Stub.asInterface(service);
		}

		public void onServiceDisconnected(ComponentName name) {

			_service = null;
		}
	};

	private boolean isOptionalAccountUses(DroidSensorSettings settings) {

		if (settings.getDispatchUser() > 1) {

			return true;
		}

		if (!settings.isAllBluetoothDevices()) {

			return false;
		}

		return settings.getDispatchDevice() > 1;
	}

	private boolean isBasicAccountUses(DroidSensorSettings settings) {

		if (settings.getDispatchUser() != 2) {

			return true;
		}

		if (!settings.isAllBluetoothDevices()) {

			return false;
		}

		return settings.getDispatchDevice() != 2;
	}

	private Runnable _bindCallback = new Runnable() {

		public void run() {

			DroidSensorSettings s = DroidSensorSettings
					.getInstance(DroidSensorActivity.this);
			boolean verified = true;

			boolean basicAccountUses = isBasicAccountUses(s);

			if (basicAccountUses) {

				verified = TwitterUtils.verifyCredentials(s.getTwitterId(), s
						.getTwitterPassword());
			}

			boolean optionalAccountUses = isOptionalAccountUses(s);

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

				_handler.post(alert);

				return;
			}

			// Intent si = new Intent(DroidSensorActivity.this,
			// IDroidSensorService.class);
			// si.setAction(ServiceUtils.START_ACTION);
			Intent si = new Intent(IDroidSensorService.class.getName());
			startService(si);
		}
	};

	private void showError(String message) {

		// Toast toast = Toast.makeText(DroidSensorActivity.this, message,
		// Toast.LENGTH_SHORT);
		// toast.show();
	}

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

	// @Override
	// protected void onListItemClick(ListView l, View v, int position, long id)
	// {
	// super.onListItemClick(l, v, position, id);
	// Toast.makeText(this,
	// String.format("l:%s, v:%s, position:%d, id:%d", l.getClass(),
	// v.getClass(), position, id),
	// Toast.LENGTH_LONG).show();
	// }

	private boolean isServiceStarted() {

		try {

			return _service.isStarted();
		} catch (RemoteException e) {

			return false;
		}
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

			_service = null;
		}
	}

	private MenuItemCallback _discoverMenuCallback = new MenuItemCallback() {

		public void onSelected(MenuItem item) {

			boolean scanning = isServiceStarted();

			if (scanning) {

				stopService();

				return;
			}

			startService();
		}

		public void onOpend(MenuItem item) {

			boolean scanning = isServiceStarted();

			if (scanning) {

				item.setIcon(android.R.drawable.ic_menu_close_clear_cancel);
				item.setTitle(R.string.menu_stop_discover);

				return;
			}

			item.setIcon(android.R.drawable.ic_menu_search);
			item.setTitle(R.string.menu_discover);
		}
	};

	private MenuItemCallback _settingsMenuCallback = new MenuItemCallback() {

		public void onSelected(MenuItem item) {

			Intent intent = new Intent(SettingsActivity.class.getName());
			startActivity(intent);
		}

		public void onOpend(MenuItem item) {

			;// nop
		}
	};

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		boolean res = super.onCreateOptionsMenu(menu);

		if (_menuHelper == null) {

			_menuHelper = new OptionsMenuHelper(DroidSensorActivity.this, menu);
		}

		onCreateOptionMenuInternal(menu);

		return res;
	}

	private void onCreateOptionMenuInternal(Menu menu) {

		_menuHelper.addItem(_discoverMenuCallback, R.string.menu_discover,
				android.R.drawable.ic_menu_view);

		_menuHelper.addItem(_settingsMenuCallback, R.string.menu_settings,
				android.R.drawable.ic_menu_preferences);
	}

	@Override
	public boolean onMenuOpened(int featureId, Menu menu) {

		_menuHelper.menuOpened(menu);

		return true;
	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {

		_menuHelper.menuSelected(item);

		return true;
	}

	protected void onDestroy() {

		super.onDestroy();
		unbindService(_serviceConnection);
	};
}

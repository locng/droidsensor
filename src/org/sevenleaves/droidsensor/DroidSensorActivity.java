package org.sevenleaves.droidsensor;

import org.sevenleaves.droidsensor.OptionsMenuHelper.MenuItemCallback;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;

public class DroidSensorActivity extends ListActivity {

	private static final Intent SERVICE_INTENT = new Intent(
			IDroidSensorService.class.getName());

	private volatile IDroidSensorService _service;

	private OptionsMenuHelper _menuHelper;

	private ProgressDialog _progressDialog;

	private Handler _handler = new Handler();

	private Runnable _bindCallback = new Runnable() {

		public void run() {

			SharedPreferences preferences = PreferenceManager
					.getDefaultSharedPreferences(DroidSensorActivity.this);
			String twitterId = preferences.getString("droidsensor_twitter_id",
					"");
			String twitterPassword = preferences.getString(
					"droidsensor_twitter_password", "");

			boolean verified = TwitterUtils.verifyCredentials(twitterId,
					twitterPassword);

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
												MainPreferenceActivity.class.getName());
										startActivity(settings);
									}
								}).create();
						alertDialog.show();
					}
				};

				_handler.post(alert);
				
				return;
			}

			if (_service == null) {

				bindService(SERVICE_INTENT, _serviceConnection,
						BIND_AUTO_CREATE);
			}

			startService(SERVICE_INTENT);
		}
	};

	private ServiceConnection _serviceConnection = new ServiceConnection() {

		public void onServiceConnected(ComponentName name, IBinder service) {

			_service = IDroidSensorService.Stub.asInterface(service);
		}

		public void onServiceDisconnected(ComponentName name) {

			_service = null;
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

		// ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
		// android.R.layout.simple_list_item_1, new String[] { "Led Zeppelin",
		// "Jimi Hendrix",
		// "The Black Crowes" });
		//	        
		// setListAdapter(adapter);

		onResume();
	}

	@Override
	protected void onResume() {

		super.onResume();

		if (_service == null) {

			bindService(SERVICE_INTENT, _serviceConnection, BIND_AUTO_CREATE);
		}
	}

	@Override
	protected void onPause() {

		if (_service != null) {

			unbindService(_serviceConnection);
			// 何故だかServiceConnection#onDisconnectedに来ないので、ここでnull
			_service = null;
		}

		super.onPause();
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

	private boolean isServiceScanning() {

		try {

			if (_service.isStarted()) {

				return true;
			}
		} catch (RemoteException e) {

			return false;
		}

		return false;
	}

	private void startScanning() {

		_progressDialog = new ProgressDialog(DroidSensorActivity.this);
		_progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		//_progressDialog.setCancelable(false);
		_progressDialog.setTitle("Processing...");
		_progressDialog.setMessage("Verify Credentials");
		_progressDialog.show();

		new Thread() {

			public void run() {

				//_handler.post(_bindCallback);
				_bindCallback.run();
			};
		}.start();
	}

	private void stopScanning() {

		try {

			_service.stopScanning();

			if (_service != null) {

				unbindService(_serviceConnection);
				// 何故だかServiceConnection#onDisconnectedに来ないので、ここでnull
				_service = null;
			}
		} catch (RemoteException e) {

			showError(e.getMessage());
		}
	}

	private MenuItemCallback _discoverMenuCallback = new MenuItemCallback() {

		public void onSelected(MenuItem item) {

			boolean scanning = isServiceScanning();

			if (scanning) {

				stopScanning();

				return;
			}

			startScanning();
		}

		public void onOpend(MenuItem item) {

			boolean scanning = isServiceScanning();

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

			Intent intent = new Intent(MainPreferenceActivity.class.getName());
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
}

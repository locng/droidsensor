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

import java.util.List;

import org.sevenleaves.droidsensor.bluetooth.BluetoothUtils;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.DialogInterface.OnClickListener;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.RemoteException;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

/**
 * TODO プログレスのキャンセル処理.
 * 
 * @author smasui@gmail.com
 * 
 */
public class DroidSensorActivity extends DroidSensorActivitySupport {

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

			String address = (String) msg.obj;
			BluetoothDeviceEntity entity = getRemoteBluetoothDevice(address);
			addDeviceToList(entity);
		};
	};

	private IDroidSensorCallbackListener _listener = new IDroidSensorCallbackListener.Stub() {

		public void deviceFound(String address) throws RemoteException {

			_handler.sendMessage(_handler.obtainMessage(CALLBACK_MESSAGE,
					address));
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

	private void addDeviceToList(BluetoothDeviceEntity entity) {

		BluetoothDeviceAdapter adapter = (BluetoothDeviceAdapter) getListAdapter();
		adapter.addBluetoothDevice(entity);
		adapter.notifyDataSetChanged();
	}

	private void clearList() {

		BluetoothDeviceAdapter adapter = (BluetoothDeviceAdapter) getListAdapter();
		adapter.clear();

		DroidSensorDatabaseOpenHelper dbHelper = new DroidSensorDatabaseOpenHelper(
				DroidSensorActivity.this);
		SQLiteDatabase db = null;

		try {

			db = dbHelper.getWritableDatabase();
			BluetoothDeviceEntityDAO dao = new BluetoothDeviceEntityDAO(db);
			dao.deleteAll();
		} finally {

			if (db != null) {

				db.close();
			}
		}

		adapter.notifyDataSetChanged();
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

	private BluetoothDeviceEntity getRemoteBluetoothDevice(String address) {

		DroidSensorDatabaseOpenHelper dbHelper = new DroidSensorDatabaseOpenHelper(
				DroidSensorActivity.this);
		SQLiteDatabase db = null;

		try {

			db = dbHelper.getWritableDatabase();
			BluetoothDeviceEntityDAO dao = new BluetoothDeviceEntityDAO(db);
			BluetoothDeviceEntity entity = dao.findByAddress(address);

			return entity;
		} finally {

			if (db != null) {

				db.close();
			}
		}
	}

	private void initList() {

		DroidSensorDatabaseOpenHelper dbHelper = new DroidSensorDatabaseOpenHelper(
				DroidSensorActivity.this);
		SQLiteDatabase db = null;

		try {

			db = dbHelper.getWritableDatabase();
			BluetoothDeviceEntityDAO dao = new BluetoothDeviceEntityDAO(db);
			List<BluetoothDeviceEntity> devices = dao.findAll();

			for (BluetoothDeviceEntity e : devices) {

				addDeviceToList(e);
			}
		} finally {

			if (db != null) {

				db.close();
			}
		}
	}

	private boolean isServiceStarted() {

		try {

			return _service.isStarted();
		} catch (RemoteException e) {

			return false;
		}
	}

	private void showError(String message) {

		// Toast toast = Toast.makeText(DroidSensorActivity.this, message,
		// Toast.LENGTH_SHORT);
		// toast.show();
	}

	private void startService() {

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

			verified = TwitterUtils.verifyCredentials(s.getOptionalTwitterId(),
					s.getOptionalTwitterPassword());
		}

		if (!verified) {

			AlertDialog alertDialog = new AlertDialog.Builder(
					DroidSensorActivity.this)
					.setTitle("Authentication failed.").setPositiveButton(
							"Open Settings", new OnClickListener() {

								public void onClick(DialogInterface dialog,
										int which) {

									Intent settings = new Intent(
											SettingsActivity.class.getName());
									startActivity(settings);
								}
							}).create();
			alertDialog.show();

			return;
		}

		// Intent si = new Intent(DroidSensorActivity.this,
		// IDroidSensorService.class);
		// si.setAction(ServiceUtils.START_ACTION);
		Intent si = new Intent(IDroidSensorService.class.getName());
		startService(si);
	}

	private void stopService() {

		Log.d("DroidSensorAcivity", "stopService");
		try {

			_service.stopService();
		} catch (RemoteException e) {

			; // nop.
		}
	}

	@Override
	protected void onClearAllDialogRejected() {

		; // nop.
	}

	@Override
	protected void onClearAllDialogAccepted() {

		clearList();
	}

	@Override
	protected void onClearAllMenuOpened(MenuItem item) {

		; // nop
	}

	@Override
	protected void onClearAllMenuSelected(MenuItem item) {

		buildClearAllConfirm().show();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.droid_sensor);
		Intent bi = new Intent(IDroidSensorService.class.getName());
		bindService(bi, _serviceConnection, BIND_AUTO_CREATE);

		BluetoothDeviceAdapter adapter = new BluetoothDeviceAdapter(
				DroidSensorActivity.this);
		setListAdapter(adapter);
		initList();
	}

	@Override
	protected void onDestroy() {

		super.onDestroy();
		unbindService(_serviceConnection);
	}

	@Override
	protected void onDiscoveryMenuOpened(MenuItem item) {

		boolean scanning = isServiceStarted();

		if (scanning) {

			item.setIcon(android.R.drawable.ic_menu_close_clear_cancel);
			item.setTitle(R.string.menu_stop_discover);

			return;
		}

		item.setIcon(android.R.drawable.ic_menu_search);
		item.setTitle(R.string.menu_discover);
	}

	@Override
	protected void onDiscoveryMenuSelected(MenuItem item) {

		boolean scanning = isServiceStarted();

		if (scanning) {

			stopService();

			return;
		}

		startService();
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {

		BluetoothDeviceAdapter adapter = (BluetoothDeviceAdapter) getListAdapter();
		BluetoothDeviceEntity entity = (BluetoothDeviceEntity) adapter
				.getItem(position);
		Intent intent = new Intent(RemoteBluetoothDeviceActivity.class
				.getName());
		BluetoothUtils.putAddress(intent, entity.getAddress());
		startActivity(intent);
	}

	@Override
	protected void onSettingsMenuOpened(MenuItem item) {

		; // nop
	}

	@Override
	protected void onSettingsMenuSelected(MenuItem item) {

		Intent intent = new Intent(SettingsActivity.class.getName());
		startActivity(intent);
	}
}
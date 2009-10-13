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

import org.sevenleaves.droidsensor.DatabaseManipulation.ManipulationScope;
import org.sevenleaves.droidsensor.bluetooth.BluetoothUtils;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Message;
import android.os.RemoteException;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

/**
 * TODO onCreate、onDestroy、onClearAllなどのタイミングでプログレスダイアログを表示.
 * 
 * @author smasui@gmail.com
 * 
 */
public class DroidSensorActivity extends DroidSensorActivitySupport {

	private static final String TAG = DroidSensorActivity.class.getSimpleName();

	private BluetoothDeviceEntity getRemoteBluetoothDevice(final String address) {

		final HardReference<BluetoothDeviceEntity> h = HardReference.create();

		DatabaseManipulation.manipulate(DroidSensorActivity.this,

		new ManipulationScope() {

			public void execute(SQLiteDatabase db) {

				BluetoothDeviceEntityDAO dao = new BluetoothDeviceEntityDAO(db);
				BluetoothDeviceEntity entity = dao.findByAddress(address);
				h.put(entity);
			}
		});

		return h.get();
	}

	private void initList() {

		DatabaseManipulation.manipulate(DroidSensorActivity.this,

		new ManipulationScope() {

			public void execute(SQLiteDatabase db) {

				BluetoothDeviceEntityDAO dao = new BluetoothDeviceEntityDAO(db);
				List<BluetoothDeviceEntity> devices = dao.findAll();

				for (BluetoothDeviceEntity e : devices) {

					addDeviceToList(e);
				}
			}
		});
	}

	private boolean isServiceStarted() {

		try {

			return getDroidSensorService().isStarted();
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

		indeterminate("Stopping service", new Runnable() {

			public void run() {

				try {

					getDroidSensorService().stopService();
				} catch (RemoteException e) {

					; // nop.
				}
			}
		});
	}

	private void addDeviceToList(BluetoothDeviceEntity entity) {

		BluetoothDeviceAdapter adapter = (BluetoothDeviceAdapter) getListAdapter();
		adapter.addBluetoothDevice(entity);
		adapter.notifyDataSetChanged();
	}

	@Override
	protected void onClearAllDialogAccepted() {

		indeterminate("Deleting list", new Runnable() {

			public void run() {

				BluetoothDeviceAdapter adapter = (BluetoothDeviceAdapter) getListAdapter();
				adapter.clear();

				DatabaseManipulation.manipulate(DroidSensorActivity.this,

				new ManipulationScope() {

					public void execute(SQLiteDatabase db) {

						BluetoothDeviceEntityDAO dao = new BluetoothDeviceEntityDAO(
								db);
						dao.deleteAll();
					}
				});

				adapter.notifyDataSetChanged();
			}
		});
	}

	@Override
	protected void onClearAllDialogRejected() {

		; // nop.
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
		BluetoothDeviceAdapter adapter = new BluetoothDeviceAdapter(
				DroidSensorActivity.this);
		setListAdapter(adapter);
		indeterminate("Loading list", new Runnable() {

			public void run() {

				initList();
			}
		});
	}

	@Override
	protected void onStart() {

		super.onStart();
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
	protected void onMessageDispatched(Message msg) {

		final String address = (String) msg.obj;
		indeterminate("Updating list", new Runnable() {

			public void run() {

				BluetoothDeviceEntity entity = getRemoteBluetoothDevice(address);
				addDeviceToList(entity);
			}
		});
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

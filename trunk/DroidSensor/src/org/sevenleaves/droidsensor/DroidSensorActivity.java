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
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;
import android.content.DialogInterface.OnDismissListener;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Message;
import android.os.RemoteException;
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

		final BluetoothDeviceAdapter adapter = (BluetoothDeviceAdapter) getListAdapter();
		DatabaseManipulation.manipulate(DroidSensorActivity.this,

		new ManipulationScope() {

			public void execute(SQLiteDatabase db) {

				BluetoothDeviceEntityDAO dao = new BluetoothDeviceEntityDAO(db);
				List<BluetoothDeviceEntity> devices = dao.findAll();

				for (BluetoothDeviceEntity e : devices) {

					adapter.addBluetoothDevice(e);
				}
			}
		});
	}

	private boolean isServiceStarted() {

		try {

			IDroidSensorService service = getDroidSensorService();

			if (service == null) {

				return false;
			}

			return service.isStarted();
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

		final SettingsManager setting = SettingsManager
				.getInstance(DroidSensorActivity.this);
		final HardReference<Boolean> verifiedRef = HardReference.create();

		indeterminate("Starting Service", new Runnable() {

			public void run() {

				boolean basicAccountUses = setting.isBasicAccountUses();
				boolean verified = true;

				if (basicAccountUses) {

					verified = TwitterUtils.verifyCredentials(
							setting.getTwitterId(),
							setting.getTwitterPassword());

				}

				boolean optionalAccountUses = setting.isOptionalAccountUses();

				if (verified && optionalAccountUses) {

					verified = TwitterUtils.verifyCredentials(
							setting.getOptionalTwitterId(),
							setting.getOptionalTwitterPassword());
				}

				verifiedRef.put(verified);
			}
		}, new OnDismissListener() {

			public void onDismiss(DialogInterface dialog) {

				boolean b = verifiedRef.get();

				if (!b) {

					AlertDialog alertDialog = new AlertDialog.Builder(
							DroidSensorActivity.this)
							.setTitle("Authentication failed.")
							.setPositiveButton("Open Settings",
									new OnClickListener() {

										public void onClick(
												DialogInterface dialog,
												int which) {

											Intent settings = new Intent(
													SettingsActivity.class
															.getName());
											startActivity(settings);
										}
									}).create();
					alertDialog.show();

					return;
				}

				Intent si = new Intent(IDroidSensorService.class.getName());
				startService(si);
			}
		});

	}

	private void stopService() {

		indeterminate("Stopping service", new Runnable() {

			public void run() {

				try {

					getDroidSensorService().stopService();
				} catch (RemoteException e) {

					; // nop.
				}
			}
		}, null);
	}

	@Override
	protected void onClearAllDialogAccepted() {

		indeterminate("Deleting list", new Runnable() {

			public void run() {

				DatabaseManipulation.manipulate(DroidSensorActivity.this,

				new ManipulationScope() {

					public void execute(SQLiteDatabase db) {

						BluetoothDeviceEntityDAO dao = new BluetoothDeviceEntityDAO(
								db);
						dao.deleteAll();
					}
				});
			}
		}, new OnDismissListener() {

			public void onDismiss(DialogInterface dialog) {

				BluetoothDeviceAdapter adapter = (BluetoothDeviceAdapter) getListAdapter();
				adapter.clear();
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
		}, new OnDismissListener() {

			public void onDismiss(DialogInterface dialog) {

				final BluetoothDeviceAdapter adapter = (BluetoothDeviceAdapter) getListAdapter();
				adapter.notifyDataSetChanged();
				Builder builder = new AlertDialog.Builder(
						DroidSensorActivity.this);
				StringBuilder b = new StringBuilder();
				b.append("本アプリケーションのサポートは2010年8月末で終了します。\n\n");
				b.append("8月末以降は使用できなくなりますのでアンインストールしてください。");

				builder.setPositiveButton(android.R.string.ok,
						new OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								dialog.dismiss();
							}
						});
				builder.setMessage(b.toString());

				builder.show();
			}
		});
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
		Intent intent = new Intent(
				RemoteBluetoothDeviceActivity.class.getName());
		intent.putExtra("ROW_ID", entity.getRowID());
		startActivity(intent);
	}

	@Override
	protected void onMessageDispatched(Message msg) {

		final String address = (String) msg.obj;
		// indeterminate("Updating list", new Runnable() {
		//
		// public void run() {

		BluetoothDeviceEntity entity = getRemoteBluetoothDevice(address);
		BluetoothDeviceAdapter adapter = (BluetoothDeviceAdapter) getListAdapter();
		adapter.addBluetoothDevice(entity);
		// }
		// }, new OnDismissListener() {
		//
		// public void onDismiss(DialogInterface dialog) {

		// BluetoothDeviceAdapter adapter = (BluetoothDeviceAdapter)
		// getListAdapter();
		adapter.notifyDataSetChanged();
		// }
		// });
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

	@Override
	protected void onStart() {

		super.onStart();
	}

	@Override
	protected void onInfoMenuOpened(MenuItem item) {

		; // nop
	}

	@Override
	protected void onInfoMenuSelected(MenuItem item) {

		Intent intent = new Intent(LocalDeviceActivity.class.getName());
		startActivity(intent);
	}

	@Override
	protected void onScreenshotMenuOpened(MenuItem item) {

		; // nop
	}

	@Override
	protected void onScreenshotMenuSelected(MenuItem item) {

		ActivityUtils.takeScreenshot(this);
	}
}

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

import java.text.SimpleDateFormat;
import java.util.Calendar;

import org.sevenleaves.droidsensor.DatabaseManipulation.ManipulationScope;
import org.sevenleaves.droidsensor.bluetooth.BluetoothUtils;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

public class RemoteBluetoothDeviceActivity extends
		RemoteBluetoothDeviceActivitySupport {

	private String _twitterID;

	private String _address;

	private String emptyToNothing(String s) {

		if (s == null || s.trim().length() == 0) {

			// return "*unknown*";
			return "";
		}

		return s;
	}

	private static boolean isEmpty(String s) {

		if (s == null) {

			return true;
		}

		if (s.trim().length() == 0) {

			return true;
		}

		return false;
	}

	private String formatDate(long time) {

		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(time);
		SimpleDateFormat fmt = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		String res = fmt.format(cal.getTime());

		return res;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

		setContentView(R.layout.bluetooth_device_info);

		final HardReference<BluetoothDeviceEntity> ref = HardReference.create();

		DatabaseManipulation.manipulate(this, new ManipulationScope() {

			public void execute(SQLiteDatabase db) {

				Intent intent = getIntent();
				int rowId = intent.getExtras().getInt("ROW_ID");
				// String address = BluetoothUtils.getAddress(intent);

				BluetoothDeviceEntityDAO dao = new BluetoothDeviceEntityDAO(db);
				ref.put(dao.findById(rowId));
			}
		});

		BluetoothDeviceEntity entity = ref.get();
		_twitterID = entity.getTwitterID();
		_address = entity.getAddress();

		TextView view;
		view = (TextView) findViewById(R.id.remoteDeviceTwitterID);
		view.setText(emptyToNothing(_twitterID));
		LinearLayout layout = (LinearLayout) findViewById(R.id.message_layout);
		view = (TextView) findViewById(R.id.message);

		if (isEmpty(entity.getMessage())) {

			layout.setVisibility(View.GONE);
			view.setVisibility(View.GONE);
		} else {

			layout.setVisibility(View.VISIBLE);
			view.setVisibility(View.VISIBLE);
		}

		view.setText(emptyToNothing(entity.getMessage()));
		view = (TextView) findViewById(R.id.remoteDeviceFriendlyName);
		view.setText(emptyToNothing(entity.getName()));
		view = (TextView) findViewById(R.id.remoteDeviceClass);
		view.setText(BluetoothUtils.getMajorDeviceClassName(this, entity
				.getDeviceClass()));
		view = (TextView) findViewById(R.id.remoteDeviceAddress);
		view.setText(BluetoothUtils.getMaskedAddress(_address));
		view = (TextView) findViewById(R.id.remoteDeviceCompany);
		view.setText(emptyToNothing(entity.getCompany()));
		view = (TextView) findViewById(R.id.remoteDeviceManufacturer);
		view.setText(emptyToNothing(entity.getManufacturer()));
		view = (TextView) findViewById(R.id.remoteDeviceUpdated);
		view.setText(formatDate(entity.getUpdated()));
		// 要望とりこみなう.
		// ohgro:現状の表示形式の方がお手間だったとは思いますが、個別ログ表示の方が前回がいつか？とか解って良いかもです　つぶやいたかつぶやいてないかも解りますし〜　#droidsensor
		// view = (TextView) findViewById(R.id.remoteDeviceCount);
		// view.setText("Count: " + Integer.toString(entity.getCount()));
	}

	@Override
	protected void onPostMessageMenuOpened(MenuItem item) {

	}

	@Override
	protected void onPostMessageMenuSelected(MenuItem item) {

		Intent intent = new Intent(PostMessageActivity.class.getName());
		startActivity(intent);
	}

	@Override
	protected void onOUICodeMenuOpened(MenuItem item) {

		; // nop
	}

	@Override
	protected void onOUICodeMenuSelected(MenuItem item) {

		Intent intent = new Intent(OUISearchActivity.class.getName());
		BluetoothUtils.putAddress(intent, _address);
		startActivity(intent);
	}

	@Override
	protected void onProfileMenuOpened(MenuItem item) {

		item.setEnabled(!isEmpty(_twitterID));
	}

	private String stripPrefix(String s) {

		if (!s.startsWith("@")) {

			return s;
		}

		return s.substring(1);
	}

	@Override
	protected void onProfileMenuSelected(MenuItem item) {

		String id = stripPrefix(_twitterID);
		String path = TwitterUtils.getProfileUri(id);
		Uri uri = Uri.parse(path);
		Intent intent = new Intent(Intent.ACTION_VIEW, uri);
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

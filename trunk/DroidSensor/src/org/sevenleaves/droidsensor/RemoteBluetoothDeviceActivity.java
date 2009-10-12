package org.sevenleaves.droidsensor;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import org.sevenleaves.droidsensor.bluetooth.BluetoothUtils;

import android.app.Activity;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.TextView;

public class RemoteBluetoothDeviceActivity extends Activity {

	private static final String TAG = RemoteBluetoothDeviceActivity.class
			.getSimpleName();

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

		setContentView(R.layout.bluetooth_device_info);
		DroidSensorDatabaseOpenHelper dbHelper = new DroidSensorDatabaseOpenHelper(
				RemoteBluetoothDeviceActivity.this);
		SQLiteDatabase db = null;
		Intent intent = getIntent();
		String address = BluetoothUtils.getAddress(intent);
		BluetoothDeviceEntity entity;

		try {

			db = dbHelper.getWritableDatabase();
			BluetoothDeviceEntityDAO dao = new BluetoothDeviceEntityDAO(db);
			entity = dao.findByAddress(address);
		} finally {

			if (db != null) {

				db.close();
			}
		}

		TextView view;
		view = (TextView) findViewById(R.id.remoteDeviceTwitterID);
		view.setText("User: " + emptyToNothing(entity.getTwitterID()));
		view = (TextView) findViewById(R.id.remoteDeviceFriendlyName);
		view.setText("Name: " + emptyToNothing(entity.getName()));
		view = (TextView) findViewById(R.id.remoteDeviceAddress);
		view.setText("Address: "
				+ BluetoothUtils.getMaskedAddress(entity.getAddress()));
		view = (TextView) findViewById(R.id.remoteDeviceCompany);
		view.setText("Company: " + emptyToNothing(entity.getCompany()));
		view = (TextView) findViewById(R.id.remoteDeviceManufacturer);
		view.setText("Manufacturer: "
				+ emptyToNothing(entity.getManufacturer()));
		view = (TextView) findViewById(R.id.remoteDeviceUpdated);
		view.setText("Updated: " + formatDate(entity.getUpdated()));
		// 要望とりこみなう.
		// ohgro:現状の表示形式の方がお手間だったとは思いますが、個別ログ表示の方が前回がいつか？とか解って良いかもです　つぶやいたかつぶやいてないかも解りますし〜　#droidsensor
		//view = (TextView) findViewById(R.id.remoteDeviceCount);
		//view.setText("Count: " + Integer.toString(entity.getCount()));
	}

	private String emptyToNothing(String s) {

		if (s == null || s.trim().length() == 0) {

			return "*unknown*";
		}

		return s;
	}

	private String formatDate(long time) {

		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(time);
		SimpleDateFormat fmt = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		String res = fmt.format(cal.getTime());

		return res;
	}
}

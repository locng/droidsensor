package org.sevenleaves.droidsensor;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class BluetoothDeviceEntityDAO {

	private static final String TABLE_NAME = "BLUETOOTH_DEVICE";

	private static final/* int */String ROW_ID = "ROW_ID";

	private static final/* String */String ADDRESS = "ADDRESS";

	private static final/* int */String RSSI = "RSSI";

	private static final/* String */String NAME = "NAME";

	private static final/* int */String DEVICE_CLASS = "CLASS";

	private static final/* String */String COMPANY = "COMPANY";

	private static final/* String */String MANUFACTURER = "MANUFACTURER";

	private static final/* String */String TWITTER_ID = "TWITTER_ID";

	private static final/* String */String MESSAGE = "MESSAGE";

	private static final/* double */String LONGITUDE = "LONGITUDE";

	private static final/* double */String LATITUDE = "LATITUDE";

	private static final/* int */String COUNT = "COUNT";

	private static final/* int */String STATUS = "STATUS";

	private static final/* long */String UPDATED = "UPDATED";

	private static final String[] COLUMNS = new String[] { ROW_ID, ADDRESS,
			RSSI, NAME, DEVICE_CLASS, COMPANY, MANUFACTURER, TWITTER_ID,
			MESSAGE, LONGITUDE, LATITUDE, COUNT, STATUS, UPDATED };

	private SQLiteDatabase _db;

	public BluetoothDeviceEntityDAO(SQLiteDatabase db) {

		_db = db;
	}

	public long insert(BluetoothDeviceEntity entity) {

		ContentValues values = new ContentValues();
		values.put(ADDRESS, entity.getAddress());
		values.put(RSSI, entity.getRSSI());
		values.put(NAME, entity.getName());
		values.put(DEVICE_CLASS, entity.getDeviceClass());
		values.put(COMPANY, entity.getCompany());
		values.put(MANUFACTURER, entity.getManufacturer());
		values.put(TWITTER_ID, entity.getTwitterID());
		values.put(MESSAGE, entity.getMessage());
		values.put(LONGITUDE, entity.getLongitude());
		values.put(LATITUDE, entity.getLatitude());
		values.put(COUNT, entity.getCount());
		values.put(STATUS, entity.getStatus());
		values.put(UPDATED, entity.getUpdated());
		long res = _db.insert(TABLE_NAME, null, values);

		return res;
	}

	public long update(BluetoothDeviceEntity entity) {

		ContentValues values = new ContentValues();
		values.put(ADDRESS, entity.getAddress());
		values.put(RSSI, entity.getRSSI());
		values.put(NAME, entity.getName());
		values.put(DEVICE_CLASS, entity.getDeviceClass());
		values.put(COMPANY, entity.getCompany());
		values.put(MANUFACTURER, entity.getManufacturer());
		values.put(TWITTER_ID, entity.getTwitterID());
		values.put(MESSAGE, entity.getMessage());
		values.put(LONGITUDE, entity.getLongitude());
		values.put(LATITUDE, entity.getLatitude());
		values.put(COUNT, entity.getCount());
		values.put(STATUS, entity.getStatus());
		values.put(UPDATED, entity.getUpdated());
		String whereClause = "ROW_ID=" + entity.getRowID(); 
		long res = _db.update(TABLE_NAME, values, whereClause, null);

		return res;
	}

	public BluetoothDeviceEntity findByAddress(String address) {

		String selection = "ADDRESS='" + address + "'";
		Cursor c = _db.query(TABLE_NAME, COLUMNS, selection, null, null, null,
				UPDATED + " desc");

		while (c.moveToNext()) {

			BluetoothDeviceEntity res = new BluetoothDeviceEntity();
			res.setRowID(c.getInt(0));
			res.setAddress(c.getString(1));
			res.setRSSI(c.getInt(2));
			res.setName(c.getString(3));
			res.setDeviceClass(c.getInt(4));
			res.setCompany(c.getString(5));
			res.setManufacturer(c.getString(6));
			res.setTwitterID(c.getString(7));
			res.setMessage(c.getString(8));
			res.setLongitude(c.getDouble(9));
			res.setLatitude(c.getDouble(10));
			res.setCount(c.getInt(11));
			res.setStatus(c.getInt(12));
			res.setUpdated(c.getLong(13));

			return res;
		}

		return null;
	}

	public List<BluetoothDeviceEntity> findAll() {

		List<BluetoothDeviceEntity> res = new ArrayList<BluetoothDeviceEntity>();

		Cursor c = _db.query(TABLE_NAME, COLUMNS, null, null, null, null,
				ROW_ID + " desc");

		while (c.moveToNext()) {

			BluetoothDeviceEntity e = new BluetoothDeviceEntity();
			e.setRowID(c.getInt(0));
			e.setAddress(c.getString(1));
			e.setRSSI(c.getInt(2));
			e.setName(c.getString(3));
			e.setDeviceClass(c.getInt(4));
			e.setCompany(c.getString(5));
			e.setManufacturer(c.getString(6));
			e.setTwitterID(c.getString(7));
			e.setMessage(c.getString(8));
			e.setLongitude(c.getDouble(9));
			e.setLatitude(c.getDouble(10));
			e.setCount(c.getInt(11));
			e.setStatus(c.getInt(12));
			e.setUpdated(c.getLong(13));
			res.add(e);
		}

		return res;
	}

	public int deleteAll() {

		int res = _db.delete(TABLE_NAME, null, null);

		return res;
	}

}

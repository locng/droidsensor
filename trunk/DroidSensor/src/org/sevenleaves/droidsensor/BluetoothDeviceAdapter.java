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

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class BluetoothDeviceAdapter extends BaseAdapter {

	private Context _context;

	private List<BluetoothDeviceEntity> _devices;

	public BluetoothDeviceAdapter(Context context) {

		_context = context;
		_devices = Collections
				.synchronizedList(new LinkedList<BluetoothDeviceEntity>());
	}

	public void addBluetoothDevice(BluetoothDeviceEntity entity) {

		if (_devices.contains(entity)) {

			_devices.remove(entity);
		}

		_devices.add(0, entity);
	}

	public void clear() {

		_devices.clear();
	}

	public int getCount() {

		return _devices.size();
	}

	public Object getItem(int position) {

		return _devices.get(position);
	}

	public long getItemId(int position) {

		String address = _devices.get(position).getAddress();

		return address.hashCode();
	}

	public View getView(int position, View convertView, ViewGroup parent) {

		View view;

		if (convertView == null) {

			LayoutInflater inflater = (LayoutInflater) _context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			view = inflater.inflate(R.layout.bluetooth_device, null);
		} else {

			view = convertView;
		}

		BluetoothDeviceEntity entity = _devices.get(position);

		TextView textView;
		textView = (TextView) view.findViewById(R.id.name);
		textView.setText(entity.getName());

		textView = (TextView) view.findViewById(R.id.twitterUser);
		textView.setText(entity.getTwitterID());

		textView = (TextView) view.findViewById(R.id.updated);
		textView.setText(formatDate(entity.getUpdated()));

		// 要望とりこみなう.
		// ohgro:現状の表示形式の方がお手間だったとは思いますが、個別ログ表示の方が前回がいつか？とか解って良いかもです　つぶやいたかつぶやいてないかも解りますし〜　#droidsensor
		// textView = (TextView) view.findViewById(R.id.count);
		// textView.setText(formatCount(entity.getCount()) + "(times)");

		ImageView imageView = (ImageView) view.findViewById(R.id.androidIcon);
		imageView.setVisibility(isUser(entity.getTwitterID())
				|| isPassedByUser(entity.getTwitterID()) ? View.VISIBLE
				: View.INVISIBLE);

		if (isUser(entity.getTwitterID())) {

			imageView.setImageResource(R.drawable.android);
		} else {

			imageView.setImageResource(R.drawable.android_mono);
		}

		imageView = (ImageView) view.findViewById(R.id.twitterIcon);
		imageView.setVisibility(entity.getStatus() == 1 ? View.VISIBLE
				: View.INVISIBLE);

		return view;
	}

	private String formatCount(int count) {

		DecimalFormat fmt = new DecimalFormat("   ");

		return fmt.format(count);
	}

	private String formatDate(long time) {

		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(time);
		// SimpleDateFormat fmt = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		SimpleDateFormat fmt = new SimpleDateFormat("yyyy/MM/dd HH:mm");
		String res = fmt.format(cal.getTime());

		return res;
	}

	private boolean isPassedByUser(String id) {

		if (id == null || id.trim().length() == 0) {

			return false;
		}

		return id.startsWith("@");
	}

	private boolean isUser(String id) {

		if (id == null || id.trim().length() == 0) {

			return false;
		}

		return !id.startsWith("@");
	}

}

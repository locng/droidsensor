package com.example.bluetooth;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

public class BluetoothBroadcastReceiver extends BroadcastReceiver {

	Handler handler = new Handler();

	@Override
	public void onReceive(final Context context, final Intent intent) {

		handler.post(new Runnable() {

			public void run() {

				Log.d("@"+intent.getAction(), intent.getExtras() != null ? intent.getExtras().keySet().toString() : "null");
				Toast.makeText(context, intent.getAction(), Toast.LENGTH_SHORT)
						.show();
			}
		});
	}

	public void registerSelf(Context context) {

		IntentFilter filter = new IntentFilter();

		for (BluetoothIntent i : BluetoothIntent.values()) {

			String act = i.getAction();
			filter.addAction(act);
		}

		context.registerReceiver(BluetoothBroadcastReceiver.this, filter);
	}

	public void unregisterSelf(Context context) {

		context.unregisterReceiver(BluetoothBroadcastReceiver.this);
	}

}

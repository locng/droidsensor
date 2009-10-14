package org.sevenleaves.droidsensor;

import org.sevenleaves.droidsensor.bluetooth.BluetoothDeviceStub;
import org.sevenleaves.droidsensor.bluetooth.BluetoothDeviceStubFactory;

import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.CompoundButton.OnCheckedChangeListener;

public class LocalDeviceActivity extends LocalDeviceActivitySupport {

	private Handler _handler = new Handler();

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		Window w = getWindow();
		w.requestFeature(Window.FEATURE_LEFT_ICON);
		setContentView(R.layout.local_device);
		w.setFeatureDrawableResource(Window.FEATURE_LEFT_ICON,
				android.R.drawable.ic_dialog_alert);
		final SettingsManager setting = SettingsManager
				.getInstance(LocalDeviceActivity.this);

		CheckBox noticeCheck = (CheckBox) findViewById(R.id.notice_check);
		noticeCheck.setChecked(setting.isNoticeCheck());
		noticeCheck.setEnabled(!setting.isNoticeCheck());

		noticeCheck.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {

				setting.setNoticeCheck(isChecked);
				setting.save(LocalDeviceActivity.this);

				if (isChecked) {

					LocalDeviceActivity.this.finish();
				}
			}
		});

		initView();
	}

	private void updateView() {

		BluetoothDeviceStub stub = BluetoothDeviceStubFactory
				.createBluetoothServiceStub(this);
		TextView textView;

		String v = stub.getName();
		textView = (TextView) findViewById(R.id.local_name);
		textView.setText(v);

		v = stub.getAddress();
		textView = (TextView) findViewById(R.id.local_address);
		textView.setText(v);

		// v = stub.getCompany();
		// textView = (TextView) findViewById(R.id.local_company);
		// textView.setText(v);
		//
		// v = stub.getManufacturer();
		// textView = (TextView) findViewById(R.id.local_manufacturer);
		// textView.setText(v);
		//
		// v = stub.getVersion();
		// textView = (TextView) findViewById(R.id.local_version);
		// textView.setText(v);
		//
		// v = stub.getRevision();
		// textView = (TextView) findViewById(R.id.local_revision);
		// textView.setText(v);

	}

	private void initView() {

		final BluetoothDeviceStub stub = BluetoothDeviceStubFactory
				.createBluetoothServiceStub(this);
		final boolean enabled = stub.isEnabled();

		indeterminate("Checking Bluetooth", new Runnable() {

			public void run() {

				if (stub.isEnabled()) {

					return;
				}

				try {

					stub.enable();
					boolean ready = false;
					int count = 0;

					for (;;) {

						if (stub.isEnabled()) {

							ready = true;

						}

						if (count >= 10) {

							new Thread() {

								@Override
								public void run() {

									_handler.post(new Runnable() {

										public void run() {

											finish();
										}
									});
								}
							}.start();

							return;
						}

						++count;

						Thread.sleep(1000);

						if (ready) {

							stub.disable();
							break;
						}
					}
				} catch (InterruptedException e) {

					;
				}

			}
		}, new OnDismissListener() {

			public void onDismiss(DialogInterface dialog) {

				updateView();

				if (!enabled) {

					stub.disable();
				}
			}
		});
	}
}

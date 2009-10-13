package org.sevenleaves.droidsensor;

import org.sevenleaves.droidsensor.OptionsMenuHelper.MenuItemCallback;

import android.app.Activity;
import android.view.Menu;
import android.view.MenuItem;

public abstract class RemoteBluetoothDeviceActivitySupport extends Activity {

	private static final String TAG = RemoteBluetoothDeviceActivitySupport.class
	.getSimpleName();

	private OptionsMenuHelper _menuHelper;

	@Override
	public final boolean onCreateOptionsMenu(Menu menu) {

		boolean res = super.onCreateOptionsMenu(menu);

		if (_menuHelper == null) {

			_menuHelper = new OptionsMenuHelper(this, menu);
		}

		registerOptionsMenu(_menuHelper);

		return res;
	};

	@Override
	public final boolean onMenuItemSelected(int featureId, MenuItem item) {

		_menuHelper.menuSelected(item);

		return true;
	}

	@Override
	public final boolean onMenuOpened(int featureId, Menu menu) {

		_menuHelper.menuOpened(menu);

		return true;
	}

	/**
	 * PostMessageメニューを登録する.
	 * 
	 * @param helper
	 */
	private void addPostMessageMenu(OptionsMenuHelper helper) {

		helper.addItem(R.string.menu_delete, android.R.drawable.ic_menu_send,

		new MenuItemCallback() {

			public void onOpend(MenuItem item) {

				onPostMessageMenuOpened(item);
			}

			public void onSelected(MenuItem item) {

				onPostMessageMenuSelected(item);
			}
		});
	}

	/**
	 * OptionsMenuを登録する.
	 * 
	 * @param helper
	 */
	private void registerOptionsMenu(OptionsMenuHelper helper) {

		//addPostMessageMenu(helper);
	}

	protected abstract void onPostMessageMenuSelected(MenuItem item);

	protected abstract void onPostMessageMenuOpened(MenuItem item);
}

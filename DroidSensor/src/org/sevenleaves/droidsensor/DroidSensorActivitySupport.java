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

import org.sevenleaves.droidsensor.OptionsMenuHelper.MenuItemCallback;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.view.Menu;
import android.view.MenuItem;

/**
 * {@link DroidSensorActivity}のテンプレート.
 * 
 * @author esmasui@gmail.com
 * 
 */
public abstract class DroidSensorActivitySupport extends ListActivity {

	private OptionsMenuHelper _menuHelper;

	@Override
	public final boolean onCreateOptionsMenu(Menu menu) {

		boolean res = super.onCreateOptionsMenu(menu);

		if (_menuHelper == null) {

			_menuHelper = new OptionsMenuHelper(this, menu);
		}

		registerOptionsMenu(_menuHelper);

		return res;
	}

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
	 * ClearAllメニューを登録する.
	 * 
	 * @param helper
	 */
	private void addDeleteMenu(OptionsMenuHelper helper) {

		helper.addItem(R.string.menu_delete, android.R.drawable.ic_menu_delete,

		new MenuItemCallback() {

			public void onOpend(MenuItem item) {

				onClearAllMenuOpened(item);
			}

			public void onSelected(MenuItem item) {

				onClearAllMenuSelected(item);
			}
		});
	}

	/**
	 * Discoveryメニューを登録する.
	 * 
	 * @param helper
	 */
	private void addDiscoveryMenu(OptionsMenuHelper helper) {

		helper.addItem(R.string.menu_discover, android.R.drawable.ic_menu_view,

		new MenuItemCallback() {

			public void onOpend(MenuItem item) {

				onDiscoveryMenuOpened(item);
			}

			public void onSelected(MenuItem item) {

				onDiscoveryMenuSelected(item);
			}
		});
	}

	/**
	 * Settingsメニューを登録する.
	 * 
	 * @param helper
	 */
	private void addSettingsMenu(OptionsMenuHelper helper) {

		helper.addItem(R.string.menu_settings,
				android.R.drawable.ic_menu_preferences,

				new MenuItemCallback() {

					public void onOpend(MenuItem item) {

						onSettingsMenuOpened(item);
					}

					public void onSelected(MenuItem item) {

						onSettingsMenuSelected(item);
					}
				});
	}

	/**
	 * OptionsMenuを登録する.
	 * 
	 * @param helper
	 */
	private void registerOptionsMenu(OptionsMenuHelper helper) {

		addDiscoveryMenu(helper);
		addSettingsMenu(helper);
		addDeleteMenu(helper);
	}

	/**
	 * ClearAllメニューが押された時に表示するダイアログを構成する.
	 * 
	 * @return
	 */
	protected AlertDialog buildClearAllConfirm() {

		Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(R.string.delete_confirm_title);
		builder.setPositiveButton(R.string.delete_confirm_yes,
				new OnClickListener() {

					public void onClick(DialogInterface dialog, int which) {

						onClearAllDialogAccepted();
					}
				});
		builder.setNegativeButton(R.string.delete_confirm_no,
				new OnClickListener() {

					public void onClick(DialogInterface dialog, int which) {

						onClearAllDialogRejected();
					}
				});

		return builder.create();
	}

	/**
	 * ClearAllメニューを拒否した時の処理を実装する.
	 */
	protected abstract void onClearAllDialogRejected();

	/**
	 * ClearAllメニューを許可した時の処理を実装する.
	 */
	protected abstract void onClearAllDialogAccepted();

	/**
	 * ClearAllメニューが開かれた時の処理を実装する.
	 * 
	 * @param item
	 */
	protected abstract void onClearAllMenuOpened(MenuItem item);

	/**
	 * ClearAllメニューが押された時の処理を実装する.
	 * 
	 * @param item
	 */
	protected abstract void onClearAllMenuSelected(MenuItem item);

	/**
	 * Discoveryメニューが開かれた時の処理を実装する.
	 * 
	 * @param item
	 */
	protected abstract void onDiscoveryMenuOpened(MenuItem item);

	/**
	 * Discoveryメニューが押された時の処理を実装する.
	 * 
	 * @param item
	 */
	protected abstract void onDiscoveryMenuSelected(MenuItem item);

	/**
	 * Settingsメニューが開かれた時の処理を実装する.
	 * 
	 * @param item
	 */
	protected abstract void onSettingsMenuOpened(MenuItem item);

	/**
	 * Discoveryメニューが押された時の処理を実装する.
	 * 
	 * @param item
	 */
	protected abstract void onSettingsMenuSelected(MenuItem item);

}

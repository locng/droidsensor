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

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;

/**
 * オプションメニューのヘルパークラス.
 * 
 * @author esmasui@gmail.com
 * 
 */
class OptionsMenuHelper {

	interface MenuItemCallback {

		void onSelected(MenuItem item);

		void onOpend(MenuItem item);
	}

	private Context _context;

	private Menu _menu;

	private int _itemId = 0;

	private List<MenuItemCallback> _callbacks;

	public OptionsMenuHelper(Context context, Menu menu) {

		_context = context;
		_menu = menu;
		_callbacks = new ArrayList<MenuItemCallback>();
	}

	public void menuOpened(Menu menu) {
		
		int size = _callbacks.size();
		
		for(int i = 0; i < size; ++i){
			
			MenuItem item = _menu.getItem(i);
			MenuItemCallback callback = _callbacks.get(i);
			callback.onOpend(item);
		}
	}

	public void menuSelected(MenuItem item) {

		int itemId = item.getItemId();
		MenuItemCallback callback = _callbacks.get(itemId);
		callback.onSelected(item);
	}

	public MenuItem addItem(MenuItemCallback callback, int titleId, int iconId) {

		Intent intent = new Intent(OptionsMenuHelper.class.getName());
		_callbacks.add(callback);

		return addItem(callback, intent, titleId, iconId);
	}

	private MenuItem addItem(MenuItemCallback callback, Intent intent, int titleId, int iconId) {

		String title = _context.getString(titleId);
		MenuItem item = _menu.add(0, _itemId++, 0, title);
		item.setIntent(intent);
		item.setIcon(iconId);
		callback.onOpend(item);
		
		return item;
	}
}

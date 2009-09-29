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

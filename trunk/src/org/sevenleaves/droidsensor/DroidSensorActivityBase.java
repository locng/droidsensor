package org.sevenleaves.droidsensor;

import android.app.ListActivity;
import android.view.Menu;
import android.view.MenuItem;

public abstract class DroidSensorActivityBase extends ListActivity {

	private OptionsMenuHelper _menuHelper;

	@Override
	public final boolean onCreateOptionsMenu(Menu menu) {

		boolean res = super.onCreateOptionsMenu(menu);

		if (_menuHelper == null) {

			_menuHelper = new OptionsMenuHelper(this, menu);
		}

		onCreateOptionMenuInternal(_menuHelper);

		return res;
	}

	@Override
	public final boolean onMenuOpened(int featureId, Menu menu) {

		_menuHelper.menuOpened(menu);

		return true;
	}

	@Override
	public final boolean onMenuItemSelected(int featureId, MenuItem item) {

		_menuHelper.menuSelected(item);

		return true;
	}

	abstract protected void onCreateOptionMenuInternal(OptionsMenuHelper helper);
}

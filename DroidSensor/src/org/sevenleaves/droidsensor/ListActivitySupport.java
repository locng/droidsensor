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

import android.app.ListActivity;
import android.view.Menu;
import android.view.MenuItem;

/**
 * @author esmasui@gmail.com
 *
 */
public abstract class ListActivitySupport extends ListActivity {

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

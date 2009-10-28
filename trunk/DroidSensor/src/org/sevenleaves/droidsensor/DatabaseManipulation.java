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

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

public abstract class DatabaseManipulation {

	public static interface ManipulationScope {

		void execute(SQLiteDatabase db);
	}

	public synchronized static void manipulate(Context context,
			ManipulationScope scope) {

		DroidSensorDatabaseOpenHelper dbHelper = new DroidSensorDatabaseOpenHelper(
				context);
		SQLiteDatabase db = null;

		try {

			db = dbHelper.getWritableDatabase();
			scope.execute(db);
		} finally {

			if (db != null) {

				db.close();
			}
		}
	}
}

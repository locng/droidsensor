package org.sevenleaves.droidsensor;

import android.app.Activity;
import android.content.DialogInterface.OnDismissListener;
import android.os.Handler;

public class LocalDeviceActivitySupport extends Activity {

	private Handler _handler = new Handler();
	
	protected void indeterminate(String message, final Runnable runnable,
			OnDismissListener dismissListener) {

		ActivityUtils.indeterminate(this, _handler, message, runnable,
				dismissListener, false);
	}
	
	

}

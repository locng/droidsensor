package org.sevenleaves.droidsensor;

import org.sevenleaves.droidsensor.OptionsMenuHelper.MenuItemCallback;
import org.sevenleaves.droidsensor.bluetooth.BluetoothUtils;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebChromeClient;
import android.webkit.WebView;

public class OUISearchActivity extends Activity {

	private OptionsMenuHelper _menuHelper;
	boolean _loaded;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		WebView webView = new WebView(this);
		
		setContentView(webView);
		Intent intent = getIntent();
		String address = BluetoothUtils.getAddress(intent);
		String x = address.substring(0, 8).replace(":", "");
		String path = "http://standards.ieee.org/cgi-bin/ouisearch";
		StringBuilder html = new StringBuilder();
		html.append("<html><head></head><body>");
		html
				.append("<p>loading from IEEE Standards OUI Public Database...</p>");
		html.append("<form method=\"POST\" action=\"");
		html.append(path);
		html.append("\">");
		html
				.append("<input type=\"hidden\" name=\"submit2\" value=\"Search!\"/>");
		html.append("<input type=\"hidden\" name=\"x\" value=\"");
		html.append(x);
		html.append("\" />");
		html
				.append("</form><script type=\"text/javascript\">document.forms[0].submit();</script></body></html>");
		webView.getSettings().setJavaScriptEnabled(true);
		webView.loadData(html.toString(), "text/html", "utf-8");
		webView.setWebChromeClient(new WebChromeClient() {
			@Override
			public void onReceivedTitle(WebView view, String title) {

				super.onReceivedTitle(view, title);
				setTitle(title);
				_loaded = true;
			}
		});

	}

	/**
	 * OptionsMenuを登録する.
	 * 
	 * @param helper
	 */
	private void registerOptionsMenu(OptionsMenuHelper helper) {

		addScreenshotMenu(helper);
	}

	private void addScreenshotMenu(OptionsMenuHelper helper) {
		helper.addItem(R.string.menu_screenshot,
				android.R.drawable.ic_menu_camera,

				new MenuItemCallback() {

					public void onOpend(MenuItem item) {

						onScreenshotMenuOpened(item);
					}

					public void onSelected(MenuItem item) {

						onScreenshotMenuSelected(item);
					}
				});
	}

	@Override
	public final boolean onCreateOptionsMenu(Menu menu) {
		if (!_loaded) {
			return false;
		}

		boolean res = super.onCreateOptionsMenu(menu);

		if (_menuHelper == null) {

			_menuHelper = new OptionsMenuHelper(OUISearchActivity.this, menu);
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

	protected void onScreenshotMenuOpened(MenuItem item) {

		; // nop
	}

	protected void onScreenshotMenuSelected(MenuItem item) {

		ActivityUtils.takeScreenshot(OUISearchActivity.this);
	}

}

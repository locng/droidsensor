package org.sevenleaves.droidsensor;

import org.sevenleaves.droidsensor.bluetooth.BluetoothUtils;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class OUISearchActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

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
		WebView webView = new WebView(this);
		webView.getSettings().setJavaScriptEnabled(true);
		webView.loadData(html.toString(), "text/html", "utf-8");
		webView.setWebChromeClient(new WebChromeClient(){
			@Override
			public void onReceivedTitle(WebView view, String title) {
			
				super.onReceivedTitle(view, title);
				setTitle(title);
			}
		});
		setContentView(webView);
	}
}

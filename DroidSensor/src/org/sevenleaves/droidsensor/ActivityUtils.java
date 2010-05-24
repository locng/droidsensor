package org.sevenleaves.droidsensor;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.DialogInterface.OnDismissListener;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Bitmap.CompressFormat;
import android.net.Uri;
import android.os.Handler;
import android.provider.MediaStore.MediaColumns;
import android.provider.MediaStore.Images.ImageColumns;
import android.provider.MediaStore.Images.Media;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

public abstract class ActivityUtils {

	public static void indeterminate(Context context, Handler handler,
			String message, final Runnable runnable,
			OnDismissListener dismissListener) {

		try {

			indeterminateInternal(context, handler, message, runnable,
					dismissListener, true);
		} catch (Exception e) {

			; // nop.
		}
	}

	public static void indeterminate(Context context, Handler handler,
			String message, final Runnable runnable,
			OnDismissListener dismissListener, boolean cancelable) {

		try {

			indeterminateInternal(context, handler, message, runnable,
					dismissListener, cancelable);
		} catch (Exception e) {

			; // nop.
		}
	}

	/**
	 * Progressダイアログを構成する.
	 * 
	 * @param cancelListener
	 * @return
	 */
	private static ProgressDialog createProgressDialog(Context context,
			String message) {

		ProgressDialog dialog = new ProgressDialog(context);
		dialog.setIndeterminate(false);
		dialog.setMessage(message);

		return dialog;
	}

	private static void indeterminateInternal(Context context,
			final Handler handler, String message, final Runnable runnable,
			OnDismissListener dismissListener, boolean cancelable) {

		final ProgressDialog dialog = createProgressDialog(context, message);
		dialog.setCancelable(cancelable);

		if (dismissListener != null) {

			dialog.setOnDismissListener(dismissListener);
		}

		dialog.show();

		new Thread() {

			@Override
			public void run() {

				// 関数オブジェクトつくるのめんどーだった.
				runnable.run();

				handler.post(new Runnable() {

					public void run() {

						try {

							dialog.dismiss();
						} catch (Exception e) {

							; // nop.
						}

					}
				});
			};
		}.start();
	}

	public static void takeScreenshot(Activity context) {
		Window window = context.getWindow();
		WindowManager windowManager = context.getWindowManager();
		Display display = windowManager.getDefaultDisplay();
		DisplayMetrics metrics = new DisplayMetrics();
		display.getMetrics(metrics);

		float density = metrics.density;
		int height = (int) Math.ceil(((float) display.getHeight()) * density);
		int width = (int) Math.ceil(((float) display.getWidth()) * density);

		Bitmap bitmap = Bitmap.createBitmap(width, height,
				Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(bitmap);
		canvas.scale(density, density);

		View decorView = window.getDecorView();

		decorView.draw(canvas);
		SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
		String date = df.format(Calendar.getInstance().getTime());
		String title = "DroidSensor" + date;
		ContentValues values = new ContentValues();
		values.put(MediaColumns.TITLE, title);
		values.put(ImageColumns.DESCRIPTION, "DroidSensor screenshot");
		values.put(ImageColumns.MIME_TYPE, "image/png");

		ContentResolver contentResolver = context.getContentResolver();

		Uri uri = contentResolver.insert(Media.EXTERNAL_CONTENT_URI, values);
		OutputStream outStream = null;
		try {
			outStream = contentResolver.openOutputStream(uri);
			bitmap.compress(CompressFormat.PNG, 100, outStream);
			outStream.flush();
			outStream.close();
		} catch (FileNotFoundException e) {
			Toast.makeText(context, e.getLocalizedMessage(), Toast.LENGTH_SHORT);
			e.printStackTrace();
			return;
		} catch (IOException e) {
			Toast.makeText(context, e.getLocalizedMessage(), Toast.LENGTH_SHORT);
			e.printStackTrace();
			return;
		}
		
		Toast.makeText(context, "Complete", Toast.LENGTH_SHORT).show();

		//Intent intent = new Intent(Intent.ACTION_SEND, uri);
		//intent.setType("image/png");
		//intent.putExtra(Intent.EXTRA_STREAM, uri);
		//Intent chooser = Intent.createChooser(intent, "Send");

		//context.startActivity(chooser);
	}
}

/**
 * Copyright (C) 2013 Jason Scott
 */
package edu.nyit.pocketslateUtils;

import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.util.LruCache;
import android.widget.ImageView;

/**
 * 
 * @author jasonscott
 *
 */
public class BitmapWorkerTask extends AsyncTask<String, Void, Bitmap> {
	private final WeakReference<ImageView> imageViewReference;
	private int width = 0;
	private int height = 0;
	public String mUrl = new String();
	private final static int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
	private final static int cacheSize = maxMemory / 8;
	private static LruCache<String, Bitmap> mMemoryCache = new LruCache<String, Bitmap>(cacheSize){
		@Override
		protected int sizeOf(String key, Bitmap bitmap) {
			// The cache size will be measured in kilobytes rather than
			// number of items.
			return bitmap.getByteCount() / 1024;
		}
	};

	public BitmapWorkerTask(ImageView imageView, String u, int reqWidth, int reqHeight) {
		// Use a WeakReference to ensure the ImageView can be garbage collected
		imageViewReference = new WeakReference<ImageView>(imageView);
		mUrl = u;
		width = reqWidth;
		height = reqHeight;

	}

	// Decode image in background.
	@Override
	protected Bitmap doInBackground(String... url) {

		final Bitmap bitmap = decodeBitmapFromNetwork(url[0], width, height);
		addBitmapToMemoryCache(String.valueOf(url[0]), bitmap);
		return bitmap;

	}

	// Once complete, see if ImageView is still around and set bitmap.
	@Override
	protected void onPostExecute(Bitmap bitmap) {

		if (isCancelled()) {
			bitmap = null;
		}

		if (imageViewReference != null && bitmap != null) {
			final ImageView imageView = imageViewReference.get();
			final BitmapWorkerTask bitmapWorkerTask =
					getBitmapWorkerTask(imageView);
			if (this == bitmapWorkerTask && imageView != null) {
				imageView.setImageBitmap(bitmap);
			}

		}
	}

	public void addBitmapToMemoryCache(String key, Bitmap bitmap) {
		if (getBitmapFromMemCache(key) == null) {
			mMemoryCache.put(key, bitmap);
		}
	}

	public static Bitmap getBitmapFromMemCache(String key) {
		return mMemoryCache.get(key);
	}

	/**
	 * 
	 * @param url
	 * @param imageView
	 * @return
	 */
	public static boolean cancelPotentialWork(String url, ImageView imageView) {
		final BitmapWorkerTask bitmapWorkerTask = getBitmapWorkerTask(imageView);

		if (bitmapWorkerTask != null) {
			final String bitmapUrl = bitmapWorkerTask.mUrl;
			if (!bitmapUrl.equals(url)) {
				// Cancel previous task
				bitmapWorkerTask.cancel(true);
			} else {
				// The same work is already in progress
				return false;
			}
		}
		// No task associated with the ImageView, or an existing task was cancelled
		return true;
	}

	/**
	 * 
	 * @param imageView
	 * @return
	 */
	private static BitmapWorkerTask getBitmapWorkerTask(ImageView imageView) {
		if (imageView != null) {
			final Drawable drawable = imageView.getDrawable();
			if (drawable instanceof AsyncDrawable) {
				final AsyncDrawable asyncDrawable = (AsyncDrawable) drawable;
				return asyncDrawable.getBitmapWorkerTask();
			}
		}
		return null;
	}


	/**
	 * 
	 * @param options
	 * @param reqWidth
	 * @param reqHeight
	 * @return
	 */
	public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
		final int height = options.outHeight;
		final int width = options.outWidth;
		int inSampleSize = 1;

		if (height > reqHeight || width > reqWidth) {

			final int halfHeight = height / 2;
			final int halfWidth = width / 2;

			// Calculate the largest inSampleSize value that is a power of 2 and keeps both
			// height and width larger than the requested height and width.
			while ((halfHeight / inSampleSize) > reqHeight
					&& (halfWidth / inSampleSize) > reqWidth) {
				inSampleSize *= 2;
			}
		}

		return inSampleSize;
	}

	/**
	 * 
	 * @param url
	 * @param reqWidth
	 * @param reqHeight
	 * @return
	 */
	public static Bitmap decodeBitmapFromNetwork(String url, int reqWidth, int reqHeight) {

		try {
			// First decode with inJustDecodeBounds=true to check dimensions
			final BitmapFactory.Options options = new BitmapFactory.Options();
			options.inJustDecodeBounds = true;
			options.inScaled = false;
			BitmapFactory.decodeStream(downloadUrl(url), null, options);

			// Calculate inSampleSize
			options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

			// Decode bitmap with inSampleSize set
			options.inJustDecodeBounds = false;
			return BitmapFactory.decodeStream(downloadUrl(url), null, options); 
		} catch(IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * 
	 * @param res
	 * @param resId
	 * @param reqWidth
	 * @param reqHeight
	 * @return
	 */
	public static Bitmap decodeBitmapFromResource(Resources res, int resId,
			int reqWidth, int reqHeight) {

		// First decode with inJustDecodeBounds=true to check dimensions
		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeResource(res, resId, options);

		// Calculate inSampleSize
		options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

		// Decode bitmap with inSampleSize set
		options.inJustDecodeBounds = false;
		return BitmapFactory.decodeResource(res, resId, options);
	}

	/**
	 * Makes a connection to a given URL and returns InputStream
	 * @param urlString
	 * @return
	 * @throws IOException
	 */
	private static InputStream downloadUrl(String urlString) throws IOException {
		URL url = new URL(urlString);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setReadTimeout(5000 /* milliseconds */);
		conn.setConnectTimeout(5000/*milliseconds*/);
		conn.setRequestMethod("GET");
		conn.setDoInput(true);
		conn.connect();
		InputStream stream = conn.getInputStream();
		return stream;
	}


	/**
	 * 
	 * @author jasonscott
	 *
	 */
	public static class AsyncDrawable extends BitmapDrawable {
		private final WeakReference<BitmapWorkerTask> bitmapWorkerTaskReference;

		public AsyncDrawable(Resources res, Bitmap bitmap,
				BitmapWorkerTask bitmapWorkerTask) {
			super(res, bitmap);
			bitmapWorkerTaskReference =
					new WeakReference<BitmapWorkerTask>(bitmapWorkerTask);
		}

		public BitmapWorkerTask getBitmapWorkerTask() {
			return bitmapWorkerTaskReference.get();
		}
	}

}

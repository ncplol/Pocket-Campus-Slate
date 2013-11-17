package edu.nyit.pocketslate;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import edu.nyit.pocketslateUtils.BitmapWorkerTask;
import edu.nyit.pocketslateUtils.PocketSlateDbHelper;
import edu.nyit.pocketslateUtils.PocketSlateReaderContract.ItemEntry;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.widget.ImageView;
import android.widget.TextView;

public class OrganizationActivity extends Activity {

	private TextView mTitle;
	private ImageView mLogo;
	private TextView mDescription;
	private int mPosition;
	private Item mOrganization;
	private PocketSlateDbHelper mPocketDbHelper;
	private Point mLogoRes;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_organization);

		getActionBar().setHomeButtonEnabled(true);
		
		Point resolution = new Point();
		getWindowManager().getDefaultDisplay().getSize(resolution);

		mTitle = (TextView)findViewById(R.id.organization_title);
		mLogo = (ImageView)findViewById(R.id.organization_logo);
		mDescription = (TextView)findViewById(R.id.organization_description);

		Bundle bundle = getIntent().getBundleExtra("organization");
		mPosition = bundle.getInt("position");

		mPocketDbHelper = PocketSlateDbHelper.getInstance(this);
		mOrganization =  mPocketDbHelper.getItem("clubs_and_organizations", ItemEntry._ID, "" + mPosition);

		mTitle.setText(mOrganization.title);

		Spanned spanned = Html.fromHtml(mOrganization.content);
		mDescription.setText(spanned);

		if(mOrganization.imageUrl != null) {
			BitmapWorkerTask task = new BitmapWorkerTask(mLogo, 200, 200);
			task.execute(mOrganization.imageUrl);
		} else {
			mLogo.setImageResource(R.drawable.splash_horizontal);
		}
	}

	@Override
	protected void onRestart() {
		super.onRestart();
	}

	@Override
	protected void onStart() {
		super.onStart();
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	protected void onPause() {
		super.onPause();
	}

	@Override
	protected void onStop() {
		super.onStart();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	@Override
	protected void onSaveInstanceState(Bundle savedInstanceState) {
		super.onSaveInstanceState(savedInstanceState);
	}
}

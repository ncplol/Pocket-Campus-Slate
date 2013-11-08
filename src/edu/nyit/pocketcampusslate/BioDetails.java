package edu.nyit.pocketcampusslate;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

public class BioDetails extends Activity {
	
	public ImageView mDetailsLogo;

	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.biodetails);

        TextView detailName = (TextView) findViewById(R.id.biosname);
        TextView detailMajor = (TextView) findViewById(R.id.biosmajor);
        TextView detailPos = (TextView) findViewById(R.id.biopos);
        mDetailsLogo = (ImageView) findViewById(R.id.biosimage);

        Intent intent = getIntent();

        detailName.setText(intent.getStringExtra("keyName"));
        detailMajor.setText(intent.getStringExtra("keyMajor"));
        detailPos.setText(intent.getStringExtra("keyPos"));
        String str_url = intent.getStringExtra("keyLogo");
        GetImageFromServer asyObj = new GetImageFromServer(str_url);
        asyObj.execute("");
    }
    
    /**
     * ****************************************************************
     * Async Task
     * ****************************************************************
     */
    class GetImageFromServer extends AsyncTask<String, Void, Bitmap> {

        final String url;
        private Bitmap image;
        //final ImageView detailsLogo = (ImageView) findViewById(R.id.logo);

        public GetImageFromServer(String url) {
            this.url = url;

        }

        @Override
        protected Bitmap doInBackground(String... params) {
            try {
                URL urli = new URL(this.url);
                URLConnection ucon = urli.openConnection();
                image = BitmapFactory.decodeStream(ucon.getInputStream());
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return image;  //<<< return Bitmap
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            mDetailsLogo.setImageBitmap(result);
        }

    }
    /**
     * *****************************************************************
     * End Async Task
     *****************************************************************
     */

}

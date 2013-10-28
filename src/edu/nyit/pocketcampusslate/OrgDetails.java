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

public class OrgDetails extends Activity {

    public ImageView detailsLogo;

	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.orgdetails);

        TextView detailsLname = (TextView) findViewById(R.id.detailsLname);
        TextView detailsDescription = (TextView) findViewById(R.id.detailsDescription);
        detailsLogo = (ImageView) findViewById(R.id.detailsLogo);

        Intent intent = getIntent();

        detailsLname.setText(intent.getStringExtra("keyLname"));
        detailsDescription.setText(intent.getStringExtra("keyDescription"));
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
            detailsLogo.setImageBitmap(result);
        }

    }
    /**
     * *****************************************************************
     * End Async Task
     *****************************************************************
     */
}



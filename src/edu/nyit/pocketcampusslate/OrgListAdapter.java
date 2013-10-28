package edu.nyit.pocketcampusslate;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;

class OrgListAdapter extends ArrayAdapter<Orgs> {

    private final List<Orgs> orgList;
    private View rowView;


    public OrgListAdapter(Context context, List<Orgs> orgList) {
        super(context, 0, orgList);
        this.orgList = orgList;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        Activity activity = (Activity) getContext();
        LayoutInflater inflater = activity.getLayoutInflater();

        // Inflate the views from XML
        rowView = inflater.inflate(R.layout.roworgs, null);

        TextView orgName = null;
        if (rowView != null) {
            orgName = (TextView) rowView.findViewById(R.id.orgname);
        }
        if (orgName != null) {
            orgName.setText(orgList.get(position).getLname());
        }

//        if (rowView != null) {
//            ImageView logo = (ImageView)rowView.findViewById(R.id.logo);
//        }
        String str_url = orgList.get(position).getLogo();
        GetImageFromServer asyObj = new GetImageFromServer(str_url);
        asyObj.execute("");

        return rowView;

    }

    /**
     * ****************************************************************
     * Async Task
     * ****************************************************************
     */
    private class GetImageFromServer extends AsyncTask<String, Void, Bitmap> {

        final String url;
        private Bitmap image;
        final ImageView detailsLogo = (ImageView) rowView.findViewById(R.id.logo);

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

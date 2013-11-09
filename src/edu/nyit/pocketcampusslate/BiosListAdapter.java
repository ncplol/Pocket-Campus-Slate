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

/**
 * Created by Nick Passaro on 10/5/13.
 */
class BiosListAdapter extends ArrayAdapter<Bios> {

    private final List<Bios> mBioList;
    private View mRowView;


    public BiosListAdapter(Context context, List<Bios> bioList) {
        super(context, 0, bioList);
        this.mBioList = bioList;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        Activity activity = (Activity) getContext();
        LayoutInflater inflater = activity.getLayoutInflater();

        // Inflate the views from XML
        mRowView = inflater.inflate(R.layout.rowbios, null);

        TextView name = null;
        TextView major = null;
        TextView pos = null;

        if (mRowView != null) {
            name = (TextView) mRowView.findViewById(R.id.bioname);
            major = (TextView) mRowView.findViewById(R.id.biomajor);
            pos = (TextView) mRowView.findViewById(R.id.biopos);
        }
        if (name != null) {
            name.setText(mBioList.get(position).getName());
        }
        if (major != null) {
            major.setText(mBioList.get(position).getMajor());
        }
        if (pos != null) {
            pos.setText(mBioList.get(position).getPosition());
        }

//        if (rowView != null) {
//            ImageView logo = (ImageView)rowView.findViewById(R.id.logo);
//        }
        String str_url = mBioList.get(position).getImg();
        GetImageFromServer asyObj = new GetImageFromServer(str_url);
        asyObj.execute("");

        return mRowView;

    }

    /**
     * ****************************************************************
     * Async Task
     * ****************************************************************
     */
    private class GetImageFromServer extends AsyncTask<String, Void, Bitmap> {

        final String url;
        private Bitmap image;
        final ImageView bioImg = (ImageView) mRowView.findViewById(R.id.biosimage);

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
            bioImg.setImageBitmap(result);
        }

    }
    /**
     * *****************************************************************
     * End Async Task
     *****************************************************************
     */
}

package edu.nyit.pocketcampusslate;

import android.app.Activity;
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


class ArticleListAdapter extends ArrayAdapter<Article> {

    private final List<Article> articleList;
    private View rowView;

    public ArticleListAdapter(Activity activity, List<Article> articleList) {
        super(activity, 0, articleList);
        this.articleList = articleList;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        Activity activity = (Activity) getContext();
        LayoutInflater inflater = activity.getLayoutInflater();

        // Inflate the views from XML
        rowView = inflater.inflate(R.layout.rowstories, null);

        TextView listTitle = null;
        if (rowView != null) {
            listTitle = (TextView) rowView.findViewById(R.id.articletitle);
        }
        if (listTitle != null) {
            listTitle.setText(articleList.get(position).getTitle());
        }
        TextView listPubdate = null;
        if (rowView != null) {
            listPubdate = (TextView) rowView.findViewById(R.id.articledate);
        }
        if (listPubdate != null) {
            listPubdate.setText(articleList.get(position).getPubDate());
        }

        String str_url = articleList.get(position).getImg();
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
        final ImageView articleImg = (ImageView) rowView.findViewById(R.id.articleimage);

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
            articleImg.setImageBitmap(result);
        }

    }
    /*******************************************************************
     * End Async Task
     ******************************************************************/

}
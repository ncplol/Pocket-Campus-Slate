package edu.nyit.pocketcampusslate;

import android.app.Fragment;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.List;

public class AllStoriesFragment extends Fragment implements AdapterView.OnItemClickListener {

    private final static String KEYTITLE = "keyTitle";
    private final static String KEYPUBDATE = "keyPubDate";
    //private final static String KEYLINK = "keyLink";
    //private final static String KEYAUTHOR = "keyAuthor";
    private final static String KEYCONTENT = "keyContent";

    private final ArticleHandler myArticleHandler = new ArticleHandler();
    private List<Article> articleList;
    private ListView allStories;

    /**
     * ****************************************************************
     * RSS Async Task
     * ****************************************************************
     */
    private class ArticleTask extends AsyncTask<Void, Integer, Void> {
        @Override
        protected void onPreExecute() {
            Toast.makeText(getActivity(), "Loading articles...", Toast.LENGTH_LONG).show();
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            String url = "http://www.campusslate.com/feed/";
            if (articleList == null)
                articleList = myArticleHandler.getLatestArticles(url);
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            displayList();
            super.onPostExecute(result);
        }

    }

    /**
     * ****************************************************************
     * End RSS Async Task
     * ****************************************************************
     */

    /*
     * Called when the fragment is first created.
     */
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_allstories, null);
        if (rootView != null) {
            allStories = (ListView) rootView.findViewById(R.id.allstories);
        }
        if (articleList == null) {
            new ArticleTask().execute();
        }
        return rootView;
    }

    void displayList() {
        ArticleListAdapter adapter = new ArticleListAdapter(getActivity(), articleList);
        allStories.setAdapter(adapter);
        allStories.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getActivity(), ArticleDetails.class);
                intent.putExtra(KEYPUBDATE, myArticleHandler.getArticle(position).getPubDate());
                intent.putExtra(KEYTITLE, myArticleHandler.getArticle(position).getTitle());
                //intent.putExtra(KEYAUTHOR, myArticleHandler.getArticle(position).getAuthor());
                intent.putExtra(KEYCONTENT, myArticleHandler.getArticle(position).getEncodedContent());
                startActivity(intent);
            }
        });
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

    }
}


package edu.nyit.pocketcampusslate;

import java.util.List;

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

public class OrgsFragment extends Fragment implements AdapterView.OnItemClickListener {

    private final OrgHandler mHandler = new OrgHandler();
    private List<Orgs> mOrglist;
    private ListView mOrgs;

    private final static String KEYSNAME = "keySname";
    private final static String KEYDESCRIPTION = "keyDescription";
    private final static String KEYLNAME = "keyLname";
    private final static String KEYLOGO = "keyLogo";

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

    }

    /**
     * ****************************************************************
     * Async Task
     * ****************************************************************
     */
    private class OrgTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            Toast.makeText(getActivity(), "Loading organizations...", Toast.LENGTH_LONG).show();
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            String url = "http://www.campusslate.com/orgs.xml";
            mOrglist = mHandler.getOrgs(url);
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
     * End Async Task
     * ****************************************************************
     */

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_organizations, null);
        if (rootView != null) {
            mOrgs = (ListView) rootView.findViewById(R.id.orgslist);
        }
        if (mOrglist == null)
            new OrgTask().execute();
        return rootView;
    }

    void displayList() {
        OrgListAdapter adapter = new OrgListAdapter(getActivity(), mOrglist);
        mOrgs.setAdapter(adapter);
        mOrgs.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getActivity(), OrgDetails.class);
                intent.putExtra(KEYSNAME, mHandler.getOrg(position).getSname());
                intent.putExtra(KEYLNAME, mHandler.getOrg(position).getLname());
                intent.putExtra(KEYDESCRIPTION, mHandler.getOrg(position).getDescription());
                intent.putExtra(KEYLOGO, mHandler.getOrg(position).getLogo());
                startActivity(intent);
            }
        });
    }

}

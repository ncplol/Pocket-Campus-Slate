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

class OrgsFragment extends Fragment implements AdapterView.OnItemClickListener {

    private final OrgHandler handler = new OrgHandler();
    private List<Orgs> orglist;
    private ListView orgs;

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
            orglist = handler.getOrgs(url);
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
            orgs = (ListView) rootView.findViewById(R.id.orgs);
        }
        if (orglist == null)
            new OrgTask().execute();
        return rootView;
    }

    void displayList() {
        OrgListAdapter adapter = new OrgListAdapter(getActivity(), orglist);
        orgs.setAdapter(adapter);
        orgs.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getActivity(), OrgDetails.class);
                intent.putExtra(KEYSNAME, handler.getOrg(position).getSname());
                intent.putExtra(KEYLNAME, handler.getOrg(position).getLname());
                intent.putExtra(KEYDESCRIPTION, handler.getOrg(position).getDescription());
                intent.putExtra(KEYLOGO, handler.getOrg(position).getLogo());
                startActivity(intent);
            }
        });
    }

}

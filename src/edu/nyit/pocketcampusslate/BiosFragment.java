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

public class BiosFragment extends Fragment {
	
	private final static String KEYNAME = "keyName";
    private final static String KEYMAJOR = "keyMajor";
    private final static String KEYPOS = "keyPos";
    private final static String KEYLOGO = "keyLogo";
    private final static String KEYCONTENT = "keyContent";

    private final BioHandler handler = new BioHandler();
    private List<Bios> bioList;
    private ListView bios;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_bios, null);
        if (rootView != null) {
            bios = (ListView) rootView.findViewById(R.id.bioslist);
        }
        if (bioList == null)
            new OrgTask().execute();
        return rootView;
    }

    void displayList() {
        BiosListAdapter adapter = new BiosListAdapter(getActivity(), bioList);
        bios.setAdapter(adapter);
        bios.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getActivity(), OrgDetails.class);
                intent.putExtra(KEYNAME, handler.getBio(position).getName());
                intent.putExtra(KEYMAJOR, handler.getBio(position).getMajor());
                intent.putExtra(KEYPOS, handler.getBio(position).getPosition());
                intent.putExtra(KEYLOGO, handler.getBio(position).getImg());
                //intent.putExtra(KEYCONTENT, handler.getBio(position).getEncodedContent());
                startActivity(intent);
            }
        });
    }

    /**
     * ****************************************************************
     * Async Task
     * ****************************************************************
     */
    private class OrgTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            Toast.makeText(getActivity(), "Loading bios...", Toast.LENGTH_LONG).show();
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            String url = "http://www.campusslate.com/bios.xml";
            if (bioList == null) {
                bioList = handler.getBios(url);
            }
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
}

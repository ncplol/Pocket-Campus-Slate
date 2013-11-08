package edu.nyit.pocketcampusslate;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.widget.TextView;

public class BiosDetails extends Activity{
	
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.biodetails);

        TextView bioName = (TextView) findViewById(R.id.bioname);
        TextView bioMajor = (TextView) findViewById(R.id.biomajor);
        TextView bioPos = (TextView) findViewById(R.id.biopos);
        //ImageView bioArticles = (ImageView) findViewById(R.id.biosarticles);
        

        Intent intent = getIntent();
        bioName.setText(intent.getStringExtra("keyTitle"));
        bioMajor.setText(Html.fromHtml(intent.getStringExtra("keyContent")));
        bioPos.setText(intent.getStringExtra("keyPubDate"));

    }

}

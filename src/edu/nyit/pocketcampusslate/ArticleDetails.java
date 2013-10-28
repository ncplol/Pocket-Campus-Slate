package edu.nyit.pocketcampusslate;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.widget.TextView;

public class ArticleDetails extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.articledetails);

        TextView detailsTitle = (TextView) findViewById(R.id.detailstitle);
        TextView detailsContent = (TextView) findViewById(R.id.detailscontent);
        TextView detailsPubdate = (TextView) findViewById(R.id.detailspubdate);

        Intent intent = getIntent();
        detailsTitle.setText(intent.getStringExtra("keyTitle"));
        //detailsContent.setMovementMethod(new ScrollingMovementMethod());
        detailsContent.setText(Html.fromHtml(intent.getStringExtra("keyContent")));
        detailsPubdate.setText(intent.getStringExtra("keyPubDate"));

    }

}

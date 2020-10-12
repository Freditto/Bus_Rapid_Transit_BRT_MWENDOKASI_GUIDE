package com.example.fredherbert.brt;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.RelativeLayout;

public class AboutActivity extends AppCompatActivity {

    CardView contactCard,shareCard,termsCard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        contactCard = (CardView)findViewById(R.id.contactcard);
        shareCard = (CardView)findViewById(R.id.sharecard);
        termsCard = (CardView)findViewById(R.id.termscard);


        contactCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AboutActivity.this,ContactActivity.class));
            }
        });


        termsCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AboutActivity.this,TermsActivity.class));
            }
        });
    }
}

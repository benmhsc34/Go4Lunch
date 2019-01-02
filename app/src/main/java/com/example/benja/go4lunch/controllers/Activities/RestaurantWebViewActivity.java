package com.example.benja.go4lunch.controllers.Activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.example.benja.go4lunch.R;

public class RestaurantWebViewActivity extends AppCompatActivity {
    private WebView mWebView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant_web_view);
        Intent intent = getIntent();
        String website = intent.getStringExtra("websiteUrl");

        mWebView = findViewById(R.id.spiderWebview);


        mWebView.setVisibility(View.VISIBLE);
        mWebView.setWebViewClient(new WebViewClient());
        mWebView.loadUrl(website);


        Log.d("website", website);

    }


}

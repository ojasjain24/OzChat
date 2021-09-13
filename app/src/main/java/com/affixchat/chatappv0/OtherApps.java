package com.affixchat.chatappv0;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class OtherApps extends AppCompatActivity {

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_other_apps);
//        WebView webView = findViewById(R.id.webView);
//        webView.setWebViewClient(new WebViewClient());
//        webView.loadUrl("https://play.google.com/store/apps/developer?id=Anchal");
//        WebSettings webSettings = webView.getSettings();
//        webSettings.setJavaScriptEnabled(true);
    }
}
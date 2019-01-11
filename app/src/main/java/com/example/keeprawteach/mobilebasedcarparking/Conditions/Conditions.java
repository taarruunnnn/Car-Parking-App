package com.example.keeprawteach.mobilebasedcarparking.Conditions;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;
import android.widget.Toast;

import com.example.keeprawteach.mobilebasedcarparking.Ip.Ip;
import com.example.keeprawteach.mobilebasedcarparking.R;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

public class Conditions extends AppCompatActivity {


    private Toolbar toolbar;

    private WebView webView;

    private TextView Error;

    SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conditions);

        toolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);

//        getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        getSupportActionBar().setTitle("Terms and Conditions");

        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.Swipe);

        webView = (WebView) findViewById(R.id.load);

        Error = (TextView) findViewById(R.id.error);

        Error.setVisibility(View.GONE);

        Background background = new Background(this);

        background.execute();

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                load();
            }
        });
    }

    private void load() {


        Background background=new Background(this);

        background.execute();
    }


    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();

    }

    public class Background extends AsyncTask<String, String, String> {

        Context context;

        HttpURLConnection httpURLConnection;

        BufferedReader bufferedReader;

        StringBuilder stringBuilder;

        public Background(Context context) {
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {
            return DownloadData();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            swipeRefreshLayout.setRefreshing(false);
            if (s == null) {
                Toast.makeText(context, "Error Encountered", Toast.LENGTH_SHORT).show();

                Error.setVisibility(View.VISIBLE);
            } else {

                Error.setVisibility(View.GONE);

                String sign_url = new Ip(context).terms();

                webView.setWebViewClient(new WebViewClient());

                webView.loadUrl(sign_url);
            }
        }

        public String DownloadData() {
            stringBuilder = new StringBuilder();
            String line;
            httpURLConnection = new GetConnected().GetConnected();
            try {
                httpURLConnection.setRequestMethod("GET");
                httpURLConnection.setDoInput(true);
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setConnectTimeout(120000);
                httpURLConnection.connect();

                bufferedReader = new BufferedReader(new InputStreamReader(new BufferedInputStream(httpURLConnection.getInputStream())));
                while ((line = bufferedReader.readLine()) != null) {
                    stringBuilder.append(line).append("\n");
                }
                bufferedReader.close();
                httpURLConnection.disconnect();
            } catch (ProtocolException e) {
                return null;
            } catch (IOException e) {
                return null;
            }

            return stringBuilder.toString();
        }

        protected class GetConnected {
            URL url;
            HttpURLConnection httpU;
            String sign_url = new Ip(context).terms();

            public HttpURLConnection GetConnected() {
                if (sign_url != null) {
                    try {
                        url = new URL(sign_url);
                        httpU = (HttpURLConnection) url.openConnection();
                    } catch (MalformedURLException e) {
                        return null;
                    } catch (IOException e) {
                        return null;
                    }
                    return httpU;
                }
                return null;
            }
        }

    }
}

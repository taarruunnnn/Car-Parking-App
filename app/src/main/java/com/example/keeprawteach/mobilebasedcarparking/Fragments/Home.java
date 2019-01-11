package com.example.keeprawteach.mobilebasedcarparking.Fragments;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.keeprawteach.mobilebasedcarparking.Database.Database;
import com.example.keeprawteach.mobilebasedcarparking.Ip.Ip;
import com.example.keeprawteach.mobilebasedcarparking.Login.JoinUs;
import com.example.keeprawteach.mobilebasedcarparking.Login.Login;
import com.example.keeprawteach.mobilebasedcarparking.R;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * A simple {@link Fragment} subclass.
 */
public class Home extends Fragment {



    private Button JoinNow, Sign;

    Database database;

    private ListView listView;

    SwipeRefreshLayout swipeRefreshLayout;
    public Home() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_home, container, false);
        database = new Database(getContext());

        swipeRefreshLayout=(SwipeRefreshLayout)view.findViewById(R.id.Swipe);

        JoinNow = (Button) view.findViewById(R.id.join);

        Sign = (Button) view.findViewById(R.id.signupnow);

        listView = (ListView) view.findViewById(R.id.list);

        actionListener();

        check();

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                download();
            }
        });
        return view;
    }

    private void check() {
        String id = "1";

        Cursor cursor = database.check(id);

        if (cursor != null && cursor.getCount() > 0) {

            while (cursor.moveToNext()) {

                String em = cursor.getString(4);

                if (!em.isEmpty()) {

                    JoinNow.setVisibility(View.GONE);

                    Sign.setVisibility(View.GONE);

                    download();
                }
            }

        } else {
            //create:

            JoinNow.setVisibility(View.VISIBLE);

            Sign.setVisibility(View.VISIBLE);

        }

    }

    private void download() {


        Background background = new Background(getContext(), listView);
        background.execute();

    }

    private class Background extends AsyncTask<String, String, String> {

        Context context;

        ListView listView;

        ProgressDialog progressDialog;

        HttpURLConnection httpURLConnection;

        BufferedReader bufferedReader;

        StringBuilder stringBuilder;

        public Background(Context context, ListView listView) {

            this.context = context;

            this.listView = listView;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

//            progressDialog = new ProgressDialog(context);
//
//            progressDialog.setMessage("Searching.....");
//
//            progressDialog.show();

            swipeRefreshLayout.setRefreshing(true);


        }

        @Override
        protected String doInBackground(String... params) {
            return DownloadData();
        }

        private String DownloadData() {
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
            String sign_url = new Ip(context).home();

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

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
//            progressDialog.dismiss();
            if (s != null) {

                ParserData parserData = new ParserData(context, listView, s);

                parserData.execute(s);

            } else {
                Toast.makeText(context, "Error Encountered\nCan't reach the Server", Toast.LENGTH_SHORT).show();

//                Intent intent=new Intent(context, Connect.class);
//
//                context.startActivity(intent);
            }
        }

        private class ParserData extends AsyncTask<String, Void, Boolean> {

            Context context;

            ListView listView;

            ProgressDialog progressDialog;

            String jsondata;

            ArrayList<Spacecraft> arrayList = new ArrayList<>();

            public ParserData(Context context, ListView listView, String jsondata) {
                this.context = context;
                this.listView = listView;
                this.jsondata = jsondata;
            }

            @Override
            protected void onPreExecute() {
                super.onPreExecute();

//                progressDialog = new ProgressDialog(context);

//                progressDialog.setMessage("Parsing......");

//                progressDialog.show();
            }

            @Override
            protected Boolean doInBackground(String... params) {

                jsondata = params[0];

                return ParseJsonDt();
            }

            @Override
            protected void onPostExecute(Boolean bool) {

                super.onPostExecute(bool);

                swipeRefreshLayout.setRefreshing(false);

//                progressDialog.dismiss();

                if (bool) {

                    AdapterClass adapterClass = new AdapterClass(arrayList, context, listView);

                    listView.setAdapter(adapterClass);
                } else {
                    Toast.makeText(context, "Encountered Error During processing", Toast.LENGTH_LONG).show();
                }
            }




            private Boolean ParseJsonDt() {

                boolean vall = false;

                try {

                    JSONArray jsonArray = new JSONArray(jsondata);

                    JSONObject jsonObject = null;

                    int i = 0;

                    Spacecraft spacecraft;

                    while (i < jsonArray.length()) {


                        jsonObject = jsonArray.getJSONObject(i);

                        spacecraft = new Spacecraft();

                        spacecraft.setName(jsonObject.getString("name"));

                        spacecraft.setId(jsonObject.getString("slots"));

                        spacecraft.setLocation(jsonObject.getString("image"));

                        arrayList.add(spacecraft);

                        i++;

                    }

                    vall = true;

                } catch (JSONException e) {

                }

                return vall;
            }
        }

        private class Spacecraft {

            public String name, id, location;

            public Spacecraft() {
            }

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }

            public String getId() {
                return id;
            }

            public void setId(String id) {
                this.id = id;
            }

            public String getLocation() {
                return location;
            }

            public void setLocation(String location) {
                this.location = location;
            }
        }

        private class AdapterClass extends BaseAdapter {

            ArrayList<Spacecraft> spacecraftArrayList;

            Context context;

            ListView listView;

            public AdapterClass(ArrayList<Spacecraft> spacecraftArrayList, Context context, ListView listView) {

                this.spacecraftArrayList = spacecraftArrayList;

                this.context = context;

                this.listView = listView;
            }

            @Override
            public int getCount() {
                return spacecraftArrayList.size();
            }

            @Override
            public Object getItem(int position) {
                return spacecraftArrayList.get(position);
            }

            @Override
            public long getItemId(int position) {
                return position;
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {

                ViewHolder viewHolder = null;

                if (viewHolder == null) {

                    convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.modeleach, null);

                    viewHolder = new ViewHolder(convertView);

                    convertView.setTag(viewHolder);

                } else {

                    viewHolder = (ViewHolder) convertView.getTag();
                }

                Spacecraft spacecraft = spacecraftArrayList.get(position);

                viewHolder.nameTxt.setText(spacecraft.getName());

                viewHolder.idTxt.setText(spacecraft.getId());

//                add image

                String sign_url = new Ip(context).getimage();

                if (sign_url != null) {

                    String image = sign_url + spacecraft.getLocation();

                    Picasso.with(context)
                            .load(image)
                            .into(viewHolder.imageView);
                }

                return convertView;
            }


            public class ViewHolder {

                TextView nameTxt, idTxt;

                View view;

                CircleImageView imageView;

                public ViewHolder(View view) {

                    this.view = view;

                    nameTxt = (TextView) view.findViewById(R.id.txtName);

                    idTxt = (TextView) view.findViewById(R.id.txtId);

                    imageView = (CircleImageView) view.findViewById(R.id.imageView);
                }
            }
        }
    }

    private void actionListener() {
        JoinNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getContext(), JoinUs.class);

                startActivity(intent);
            }
        });
        Sign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getContext(), Login.class);

                startActivity(intent);
            }
        });
    }
}

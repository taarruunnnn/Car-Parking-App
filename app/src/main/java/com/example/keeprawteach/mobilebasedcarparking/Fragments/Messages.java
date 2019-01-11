package com.example.keeprawteach.mobilebasedcarparking.Fragments;


import android.app.ProgressDialog;
import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.keeprawteach.mobilebasedcarparking.Database.Database;
import com.example.keeprawteach.mobilebasedcarparking.Ip.Ip;
import com.example.keeprawteach.mobilebasedcarparking.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class Messages extends Fragment {



    SwipeRefreshLayout swipeRefreshLayout;

    EditText editText;

    ImageView imageView;

    Database sqLitedb;

    ListView listView;

    public Messages() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_messages, container, false);
        sqLitedb=new Database(getContext());


        listView = (ListView) view.findViewById(R.id.list);

        editText = (EditText) view.findViewById(R.id.mes);

        imageView = (ImageView) view.findViewById(R.id.ss);

        imageView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                check_message();
            }
        });

        refresh();


        return view;
    }

    private void refresh() {
        String id = "1";
        Cursor cursor = sqLitedb.check(id);
        if (cursor != null && cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                String email = cursor.getString(4);
                email_found_send(email);
            }
        }
    }

    private void email_found_send(String email) {

        DownloadMessage downloader = new DownloadMessage(getContext(), listView);
        downloader.execute(email);

    }

    private void check_message() {

        String message = editText.getText().toString();
        if (message.isEmpty()) {
            Toast.makeText(getContext(), "Can't send empty message", Toast.LENGTH_SHORT).show();
            editText.requestFocus();
        } else {
            //fetch user mail:
            fetch_email();
        }

    }

    private void fetch_email() {
        String id = "1";
        Cursor cursor = sqLitedb.check(id);
        if (cursor != null && cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                String email = cursor.getString(4);
                email_found(email);
            }
        }
    }

    private void email_found(String email) {
        String message = editText.getText().toString();
        String user = email;

        Background background = new Background(getContext());
        background.execute(message, user);
    }

    private class Background extends AsyncTask<String, String, String> {
        Context context;

        public Background(Context context) {
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {

            String message = params[0];
            String email = params[1];

            String message_url = new Ip(context).sendmessage();
            if (message_url != null) {
                try {
                    URL url = new URL(message_url);
                    HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                    httpURLConnection.setRequestMethod("POST");
                    httpURLConnection.setDoOutput(true);
                    OutputStream outputStream = httpURLConnection.getOutputStream();
                    OutputStreamWriter outputStreamWriter = new OutputStreamWriter(outputStream, "UTF-8");
                    BufferedWriter bufferedWriter = new BufferedWriter(outputStreamWriter);
                    String post_data = URLEncoder.encode("message", "UTF-8") + "=" + URLEncoder.encode(message, "UTF-8") + "&"
                            + URLEncoder.encode("email", "UTF-8") + "=" + URLEncoder.encode(email, "UTF-8");
                    bufferedWriter.write(post_data);
                    bufferedWriter.flush();
                    bufferedWriter.close();
                    InputStream inputStream = httpURLConnection.getInputStream();
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "iso-8859-1"));
                    String result = "";
                    String line = "";
                    while ((line = bufferedReader.readLine()) != null) {
                        result += line;
                    }
                    bufferedReader.close();
                    inputStream.close();
                    httpURLConnection.disconnect();
                    return result;
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override

        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if (result == null) {
                Toast.makeText(context, "Error Encountered\nCan't reach the Server", Toast.LENGTH_SHORT).show();
            } else if (result != null && result.equalsIgnoreCase("Message sent Successfully")) {
                Toast.makeText(context, result, Toast.LENGTH_SHORT).show();
                editText.setText("");
                refresh();

            } else {
                Toast.makeText(context, result, Toast.LENGTH_SHORT).show();
            }

        }

    }


    public class DownloadMessage extends AsyncTask<String, Void, String> {
        Context context;
        ListView listView;
        ProgressDialog progressDialog;
        HttpURLConnection httpURLConnection;
        BufferedReader bufferedReader;
        StringBuilder stringBuilder;

        public DownloadMessage(Context context, ListView listView) {
            this.context = context;
            this.listView = listView;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(context);
            progressDialog.setMessage("Downloading......");
            progressDialog.show();
        }
        protected String doInBackground(String... params) {

            String message = params[0];

            String message_url = new Ip(context).message();
            if (message_url != null) {
                try {
                    URL url = new URL(message_url);
                    HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                    httpURLConnection.setRequestMethod("POST");
                    httpURLConnection.setDoOutput(true);
                    OutputStream outputStream = httpURLConnection.getOutputStream();
                    OutputStreamWriter outputStreamWriter = new OutputStreamWriter(outputStream, "UTF-8");
                    BufferedWriter bufferedWriter = new BufferedWriter(outputStreamWriter);
                    String post_data = URLEncoder.encode("message", "UTF-8") + "=" + URLEncoder.encode(message, "UTF-8");
                    bufferedWriter.write(post_data);
                    bufferedWriter.flush();
                    bufferedWriter.close();
                    InputStream inputStream = httpURLConnection.getInputStream();
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "iso-8859-1"));
                    String result = "";
                    String line = "";
                    while ((line = bufferedReader.readLine()) != null) {
                        result += line;
                    }
                    bufferedReader.close();
                    inputStream.close();
                    httpURLConnection.disconnect();
                    return result;
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            progressDialog.dismiss();
            if (s != null) {
                ParserData parserData = new ParserData(context, listView, s);
                parserData.execute(s);
            } else {
                Toast.makeText(context, "Error Encountered\nCan't reach the Server", Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            Toast.makeText(context, "Cancelled by user", Toast.LENGTH_LONG).show();

        }

        public String DownloadData() {
            stringBuilder = new StringBuilder();
            String line;
            httpURLConnection = new GetConnected().GetConnected();
            try {
                httpURLConnection.setRequestMethod("GET");
                httpURLConnection.setDoInput(true);
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setConnectTimeout(12000);
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
            String sign_url = new Ip(context).message();

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
                progressDialog = new ProgressDialog(context);
                progressDialog.setMessage("parsing......");
                progressDialog.show();
            }

            @Override
            protected Boolean doInBackground(String... params) {
                jsondata = params[0];
                return ParseJsonDt();
            }


            @Override
            protected void onPostExecute(Boolean bool) {
                super.onPostExecute(bool);
                progressDialog.dismiss();
                if (bool) {
                    AdapterClass adapterClass = new AdapterClass(arrayList, context, listView);
                    listView.setAdapter(adapterClass);
                } else {
                    Toast.makeText(context, "Encountered Error During processing", Toast.LENGTH_LONG).show();
                }
            }


            protected Boolean ParseJsonDt() {
                // jsondata=data;
                boolean vall = false;
                try {
                    JSONArray jsonArray = new JSONArray(jsondata);
                    JSONObject jsonObject = null;
                    int i = 0;
                    Spacecraft spacecraft;
                    while (i < jsonArray.length()) {

                        jsonObject = jsonArray.getJSONObject(i);
                        spacecraft = new Spacecraft();
                        spacecraft.setName(jsonObject.getString("content"));
                        spacecraft.setId(jsonObject.getString("reply"));
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
            public String name, id, contact, code, county, subcounty, location;

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

            public String getContact() {
                return contact;
            }

            public void setContact(String contact) {
                this.contact = contact;
            }

            public String getCode() {
                return code;
            }

            public void setCode(String code) {
                this.code = code;
            }

            public String getCounty() {
                return county;
            }

            public void setCounty(String county) {
                this.county = county;
            }

            public String getSubcounty() {
                return subcounty;
            }

            public void setSubcounty(String subcounty) {
                this.subcounty = subcounty;
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

                    convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.messagelist, null);

                    viewHolder = new ViewHolder(convertView);

                    convertView.setTag(viewHolder);
                } else {

                    viewHolder = (ViewHolder) convertView.getTag();

                }

                Spacecraft spacecraft = spacecraftArrayList.get(position);

                viewHolder.nameTxt.setText(spacecraft.getName());

                viewHolder.idTxt.setText(spacecraft.getId());

                return convertView;
            }


        }

        public class ViewHolder {

            TextView nameTxt, idTxt;

            View view;

            public ViewHolder(View view) {

                this.view = view;

                nameTxt = (TextView) view.findViewById(R.id.textView3);

                idTxt = (TextView) view.findViewById(R.id.textView4);
            }
        }
    }

}




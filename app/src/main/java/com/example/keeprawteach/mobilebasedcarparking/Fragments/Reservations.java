package com.example.keeprawteach.mobilebasedcarparking.Fragments;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.PopupMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.keeprawteach.mobilebasedcarparking.Database.Database;
import com.example.keeprawteach.mobilebasedcarparking.Delays.Delays;
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
public class Reservations extends Fragment {

    private ListView listView;

    SwipeRefreshLayout swipeRefreshLayout;

    Database database;

    public Reservations() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_reservations, container, false);
        database = new Database(getContext());

        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.Swipe);
        listView = (ListView) view.findViewById(R.id.list);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                load();
            }
        });

        load();

        return view;
    }

    private void load() {
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

            swipeRefreshLayout.setRefreshing(true);

//            progressDialog = new ProgressDialog(context);

//            progressDialog.setMessage("Downloading...");

//            progressDialog.show();
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

            String sign_url = new Ip(context).mybookings();

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
        protected void onPostExecute(String result) {

            super.onPostExecute(result);

//            progressDialog.dismiss();

            swipeRefreshLayout.setRefreshing(false);

            if (result != null) {

                ParserData parserData = new ParserData(context, listView, result);

                parserData.execute(result);

            } else {
                Toast.makeText(context, "Error Encountered\nCan't reach the Server", Toast.LENGTH_SHORT).show();
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

                        String phone = jsonObject.getString("phone");

                        String user = getuserPhone();

                        if (phone.equalsIgnoreCase(user)) {

                            spacecraft.setSlot(jsonObject.getString("slot"));

                            spacecraft.setStarting_time(jsonObject.getString("starting_time"));

                            spacecraft.setEnd_time(jsonObject.getString("end_time"));

                            spacecraft.setStatus(jsonObject.getString("status"));

                            spacecraft.setCurrentdate(jsonObject.getString("curentdate"));

                            arrayList.add(spacecraft);
                        }

                        i++;

                    }

                    vall = true;

                } catch (JSONException e) {

                }

                return vall;
            }
        }

        private class Spacecraft {
            String slot, starting_time, end_time, status, currentdate;


            public Spacecraft() {
            }

            public String getCurrentdate() {
                return currentdate;
            }

            public void setCurrentdate(String currentdate) {
                this.currentdate = currentdate;
            }

            public String getSlot() {
                return slot;
            }

            public void setSlot(String slot) {
                this.slot = slot;
            }

            public String getStarting_time() {
                return starting_time;
            }

            public void setStarting_time(String starting_time) {
                this.starting_time = starting_time;
            }

            public String getEnd_time() {
                return end_time;
            }

            public void setEnd_time(String end_time) {
                this.end_time = end_time;
            }

            public String getStatus() {
                return status;
            }

            public void setStatus(String status) {
                this.status = status;
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

                    convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.reservation, null);

                    viewHolder = new ViewHolder(convertView);

                    convertView.setTag(viewHolder);

                } else {

                    viewHolder = (ViewHolder) convertView.getTag();
                }

                Spacecraft spacecraft = spacecraftArrayList.get(position);

                viewHolder.starting_time.setText(spacecraft.getStarting_time());

                viewHolder.end_time.setText(spacecraft.getEnd_time());

                viewHolder.slot.setText(spacecraft.getSlot());

                viewHolder.status.setText(spacecraft.getStatus());

                viewHolder.date.setText(spacecraft.getCurrentdate());


                final String slot = viewHolder.slot.getText().toString();

                final String time = viewHolder.starting_time.getText().toString();

                final ViewHolder finalViewHolder = viewHolder;

                viewHolder.imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {


                        PopupMenu popupMenu = new PopupMenu(context, finalViewHolder.imageView);

                        popupMenu.getMenuInflater().inflate(R.menu.reserve, popupMenu.getMenu());

                        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(MenuItem item) {

                                int id = item.getItemId();

                                if (id == R.id.view) {

//                                    Toast.makeText(context, "You want to cancel this reservation", Toast.LENGTH_SHORT).show();

                                    cancelReservation(slot, time);
                                } else if (id == R.id.delete) {

                                    dleteReservation(slot, time);
//                                    Toast.makeText(context, "You want to delete this reservation", Toast.LENGTH_SHORT).show();

                                } else if (id == R.id.rel) {

                                    releaseReservation(slot, time);
//                                    Toast.makeText(context, "You want to delete this reservation", Toast.LENGTH_SHORT).show();

                                }


                                return false;
                            }
                        });

                        popupMenu.show();
                    }
                });


                return convertView;

            }


            public class ViewHolder {

                TextView slot, starting_time, end_time, status, date;

                View view;

                ImageView imageView;

                public ViewHolder(View view) {

                    this.view = view;

                    starting_time = (TextView) view.findViewById(R.id.txtName);

                    end_time = (TextView) view.findViewById(R.id.txtId);

                    slot = (TextView) view.findViewById(R.id.aa);

                    status = (TextView) view.findViewById(R.id.bb);

                    date = (TextView) view.findViewById(R.id.cc);

                    imageView = (ImageView) view.findViewById(R.id.ViewBookings);

                }
            }


        }
    }

    public void releaseReservation(final String slot, final String time) {


        final String type = "Release";

        AlertDialog.Builder al = new AlertDialog.Builder(getContext());

        al.setMessage("Are you sure You want to Leave?");

        al.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                ReservationsRelease reservationsOperations = new ReservationsRelease(getContext());

                reservationsOperations.execute(type, slot, time);


            }
        });
        al.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        AlertDialog alertDialog = al.create();

        alertDialog.show();

    }

    private void dleteReservation(final String slot, final String time) {

        final String type = "Delete";

        AlertDialog.Builder al = new AlertDialog.Builder(getContext());

        al.setMessage("Are you sure You want to Delete?");

        al.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                ReservationsOperations reservationsOperations = new ReservationsOperations(getContext());

                reservationsOperations.execute(type, slot, time);


            }
        });
        al.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        AlertDialog alertDialog = al.create();

        alertDialog.show();

    }

    private void cancelReservation(final String slot, final String time) {

        final String type = "Cancel";

        AlertDialog.Builder al = new AlertDialog.Builder(getContext());

        al.setMessage("Are you sure You want to Cancel?");

        al.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                ReservationsOperations reservationsOperations = new ReservationsOperations(getContext());

                reservationsOperations.execute(type, slot, time);


            }
        });
        al.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        AlertDialog alertDialog = al.create();

        alertDialog.show();
    }


    private String getuserPhone() {
        String id = "1";
        String phone = "";

        Cursor cursor = database.check(id);

        if (cursor != null && cursor.getCount() > 0) {

            while (cursor.moveToNext()) {

                phone = cursor.getString(3);
            }
        }
        return phone;

    }

    private class ReservationsOperations extends AsyncTask<String, String, String> {

        Context context;

        public ReservationsOperations(Context context) {
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {

            String type = params[0];

            String delete = params[1];

            String time = params[2];

            if (type == "Delete") {


                String sign_url = new Ip(context).delete();

                if (sign_url != null) {

                    try {

                        URL url = new URL(sign_url);

                        HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();

                        httpURLConnection.setRequestMethod("POST");

                        httpURLConnection.setDoOutput(true);

                        OutputStream outputStream = httpURLConnection.getOutputStream();

                        OutputStreamWriter outputStreamWriter = new OutputStreamWriter(outputStream, "UTF-8");

                        BufferedWriter bufferedWriter = new BufferedWriter(outputStreamWriter);

                        String post_data = URLEncoder.encode("delete", "UTF-8") + "=" + URLEncoder.encode(delete, "UTF-8") + "&"
                                + URLEncoder.encode("time", "UTF-8") + "=" + URLEncoder.encode(time, "UTF-8");

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
            } else if (type == "Cancel") {

                String sign_url = new Ip(context).cancel();

                if (sign_url != null) {

                    try {

                        URL url = new URL(sign_url);

                        HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();

                        httpURLConnection.setRequestMethod("POST");

                        httpURLConnection.setDoOutput(true);

                        OutputStream outputStream = httpURLConnection.getOutputStream();

                        OutputStreamWriter outputStreamWriter = new OutputStreamWriter(outputStream, "UTF-8");

                        BufferedWriter bufferedWriter = new BufferedWriter(outputStreamWriter);

                        String post_data = URLEncoder.encode("delete", "UTF-8") + "=" + URLEncoder.encode(delete, "UTF-8") + "&"
                                + URLEncoder.encode("time", "UTF-8") + "=" + URLEncoder.encode(time, "UTF-8");

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

            }


            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            if (s == null) {

            } else {
                if (s.equalsIgnoreCase("Successfully Deleted")) {

                    Toast.makeText(context, s, Toast.LENGTH_SHORT).show();

                    load();

                } else if (s.equalsIgnoreCase("Successfully Cancelled")) {

                    Toast.makeText(context, s, Toast.LENGTH_SHORT).show();

                    load();

                } else if (s.equalsIgnoreCase("Successfully Left.....Thank you")) {

                    Toast.makeText(context, s, Toast.LENGTH_SHORT).show();

                    load();

                } else {

//                    Toast.makeText(context, s, Toast.LENGTH_SHORT).show();
//                    delayed(s);
                }
            }
        }
    }

    private void delayed(final String result) {

        final ProgressDialog progressDialog = new ProgressDialog(getContext());

        progressDialog.setIndeterminate(true);

        progressDialog.setMessage("Calculating, please wait...");

        progressDialog.show();

        new android.os.Handler().postDelayed(

                new Runnable() {

                    public void run() {
                        if (result != null && result.equalsIgnoreCase("Successfully Left.....Thank you")) {
                            Toast.makeText(getContext(), result, Toast.LENGTH_SHORT).show();
                        } else if (result != null && result.equalsIgnoreCase("Successfully Deleted")) {
                            Toast.makeText(getContext(), result, Toast.LENGTH_SHORT).show();
                        } else {
                            Totalfound(result);
                        }
                        progressDialog.dismiss();

                    }
                }, 3000);
    }

    private void Totalfound(final String result) {

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getContext());

        alertDialog.setTitle("Penalty");

        alertDialog.setMessage("You're Charged:\nKsh " + result + "for the delay\nPlease make Payment to\n0724743788");

        alertDialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

                Intent intent = new Intent(getContext(), Delays.class);

                Bundle bundle = new Bundle();

                bundle.putString("email_key", result);

                intent.putExtras(bundle);

                startActivity(intent);

            }
        });
        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                Toast.makeText(getContext(), "Cancelled", Toast.LENGTH_SHORT).show();
            }
        });

        AlertDialog al = alertDialog.create();

        al.show();
    }

    private class ReservationsRelease extends AsyncTask<String, String, String> {
        Context context;

        public ReservationsRelease(Context context) {
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {

            String type = params[0];

            String delete = params[1];

            String time = params[2];

            if (type == "Release") {

                String sign_url = new Ip(context).release();

                if (sign_url != null) {

                    try {

                        URL url = new URL(sign_url);

                        HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();

                        httpURLConnection.setRequestMethod("POST");

                        httpURLConnection.setDoOutput(true);

                        OutputStream outputStream = httpURLConnection.getOutputStream();

                        OutputStreamWriter outputStreamWriter = new OutputStreamWriter(outputStream, "UTF-8");

                        BufferedWriter bufferedWriter = new BufferedWriter(outputStreamWriter);

                        String post_data = URLEncoder.encode("delete", "UTF-8") + "=" + URLEncoder.encode(delete, "UTF-8") + "&"
                                + URLEncoder.encode("time", "UTF-8") + "=" + URLEncoder.encode(time, "UTF-8");

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

            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (s==null){

            }else {
                if (s.equalsIgnoreCase("Successfully Left.....Thank you")){
                    Toast.makeText(context, ""+s, Toast.LENGTH_SHORT).show();
                }else {
                    delayed(s);
                }
            }
        }
    }
}

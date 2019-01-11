package com.example.keeprawteach.mobilebasedcarparking.Fragments;


import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.keeprawteach.mobilebasedcarparking.Calling.Calling;
import com.example.keeprawteach.mobilebasedcarparking.Conditions.Conditions;
import com.example.keeprawteach.mobilebasedcarparking.Database.Database;
import com.example.keeprawteach.mobilebasedcarparking.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class Settings extends Fragment {

    Database database;

    private ListView home;

    String[] values=new String[]{"Account Settings","General ","Notification","Contact Us","Terms and Privacy Policy","App Info","Logout"};

    int[] images = {
            R.drawable.user,
            R.drawable.cog,
            R.drawable.note,
            R.drawable.phone,
            R.drawable.terms,
            R.drawable.infors,
            R.drawable.logout};

    public Settings() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_settings, container, false);
        database=new Database(getContext());

        home=(ListView)view.findViewById(R.id.homelist);

//        ArrayAdapter adapter = new ArrayAdapter<String>(getContext(),android.R.layout.simple_list_item_1, values);
        MyAdapter adapter=new MyAdapter(getContext(),values,images);
        home.setAdapter(adapter);


        home.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position){
                    case 0:
                        Toast.makeText(getContext(), "Account Settings", Toast.LENGTH_SHORT).show();
                        break;
                    case 1:
//                        Toast.makeText(getContext(), "General", Toast.LENGTH_SHORT).show();
                        ipset();
                        break;
                    case 2:
                        Toast.makeText(getContext(), "Notification", Toast.LENGTH_SHORT).show();
                        break;
                    case 3:

                        Intent call = new Intent(getContext(), Calling.class);

                        startActivity(call);

                        break;
                    case 4:

                        Intent intent = new Intent(getContext(), Conditions.class);

                        startActivity(intent);
                        break;
                    case 5:
                        Toast.makeText(getContext(), "App Info", Toast.LENGTH_SHORT).show();


//                        Intent a = new Intent(getContext(), MpesaPay.class);
//
//                        startActivity(a);

                        break;

                    case 6:
                        promptLogout();
                        break;
                }

            }
        });

        return view;
    }

    private void ipset() {


        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

        builder.setTitle("Enter Ip Address");

        final EditText input = new EditText(getContext());

        input.setInputType(InputType.TYPE_CLASS_TEXT);

        builder.setView(input);

        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

                String name = input.getText().toString();

                Success(name);
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                dialog.dismiss();
            }
        });
        builder.show();
    }

    private void Success(String name) {
        String id = "1";
        if (name.isEmpty()) {
            Toast.makeText(getContext(), "Empty Ip Addres", Toast.LENGTH_SHORT).show();
        } else {

            boolean isInserted = database.updateIp(name, id);
            if (isInserted == true) {
                Toast.makeText(getContext(), "Success", Toast.LENGTH_SHORT).show();

            } else {
                Toast.makeText(getContext(), "Failed", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void promptLogout() {
        AlertDialog.Builder al = new AlertDialog.Builder(getContext());

        al.setTitle("Exit");

        al.setMessage("Are you sure You want to Logout?");

        al.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                loggedout();

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

    private void loggedout() {

    }


    private class MyAdapter extends ArrayAdapter {


        Context context;

        String[] values;

        int [] imageArray;

        public MyAdapter(Context context, String[] values, int[] imageArray) {
            super(context,R.layout.settings,R.id.bb,values);

            this.context = context;

            this.values = values;

            this.imageArray = imageArray;

        }

        @NonNull
        @Override
        public View getView(int position,  @NonNull View convertView,@NonNull ViewGroup parent) {

            LayoutInflater inflater= (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            View row=inflater.inflate(R.layout.settings,parent,false);

            ImageView imageView=(ImageView) row.findViewById(R.id.aa);

            TextView textView=(TextView)row.findViewById(R.id.bb);

            imageView.setImageResource(imageArray[position]);

            textView.setText(values[position]);

            return row;
        }
    }
}

package com.example.keeprawteach.mobilebasedcarparking.Ip;

import android.content.Context;
import android.database.Cursor;

import com.example.keeprawteach.mobilebasedcarparking.Database.Database;

public class Ip{
    Context context;
    String folder = "Booking";
    String apk = "Apk";
    String images = "upload";
    Database sqLitedb;

    public Ip(Context context) {
        this.context = context;
        sqLitedb = new Database(context);
    }


    public String IpAddressFromSqlite() {

        String id = "1";

        Cursor cursor = sqLitedb.checkIp(id);

        if (cursor != null && cursor.getCount() > 0) {

            while (cursor.moveToNext()) {

                String obtainedIp = cursor.getString(1);

                String ip = obtainedIp;
                if (ip != null) {
                    return ip;
                }

            }

        }

        return null;
    }

    public String CallBackFromSqlite() {

        String id = "1";

        Cursor cursor = sqLitedb.checkCallback(id);

        if (cursor != null && cursor.getCount() > 0) {

            while (cursor.moveToNext()) {

                String obtainedIp = cursor.getString(1);

                String ip = obtainedIp;
                if (ip != null) {
                    return ip;
                }

            }

        }

        return null;
    }

    public String login() {
        String ip = "http://" + IpAddressFromSqlite() + "/" + folder + "/login.php";
        if (ip != null) {
            return ip;
        }
        return null;
    }

    public String location() {
        String ip = "http://" + IpAddressFromSqlite() + "/" + folder + "/" + apk + "/locations.php";
        if (ip != null) {
            return ip;
        }
        return null;
    }

    public String signup() {
        String ip = "http://" + IpAddressFromSqlite() + "/" + folder + "/" + apk + "/sign.php";
        if (ip != null) {
            return ip;
        }
        return null;
    }

    public String images() {
        String ip = "http://" + IpAddressFromSqlite() + "/" + folder + "";
        if (ip != null) {
            return ip;
        }
        return null;
    }


    public String message() {
        String ip = "http://" + IpAddressFromSqlite() + "/" + folder + "/" + apk + "/fetchmessage.php";
        if (ip != null) {
            return ip;
        }
        return null;
    }

    public String home() {
        String ip = "http://" + IpAddressFromSqlite() + "/" + folder + "/" + apk + "/locations.php";
        if (ip != null) {
            return ip;
        }
        return null;
    }

    public String sendmessage() {
        String ip = "http://" + IpAddressFromSqlite() + "/" + folder + "/" + apk + "/message.php";
        if (ip != null) {
            return ip;
        }
        return null;
    }

    public String slots() {
        String ip = "http://" + IpAddressFromSqlite() + "/" + folder + "/" + apk + "/slots.php";
        if (ip != null) {
            return ip;
        }
        return null;
    }

    public String searchslot() {
        String ip = "http://" + IpAddressFromSqlite() + "/" + folder + "/" + apk + "/searchslot.php";
        if (ip != null) {
            return ip;
        }
        return null;
    }

    public String transaction() {
        String ip = "http://" + IpAddressFromSqlite() + "/" + folder + "/" + apk + "/trans.php";
        if (ip != null) {
            return ip;
        }
        return null;
    }

    public String fetchstatus() {
        String ip = "http://" + IpAddressFromSqlite() + "/" + folder + "/" + apk + "/status.php";
        if (ip != null) {
            return ip;
        }
        return null;
    }

    public String getimage() {
        String ip = "http://" + IpAddressFromSqlite() + "/" + folder + "/" + images + "/";
        if (ip != null) {
            return ip;
        }
        return null;
    }

    public String bookSlot() {
        String ip = "http://" + IpAddressFromSqlite() + "/" + folder + "/" + apk + "/updateslot.php";
        if (ip != null) {
            return ip;
        }
        return null;
    }

    public String fetchcharges() {
        String ip = "http://" + IpAddressFromSqlite() + "/" + folder + "/" + apk + "/charges.php";
        if (ip != null) {
            return ip;
        }
        return null;
    }

    public String changepass() {


        String ip = "http://" + IpAddressFromSqlite() + "/" + folder + "/" + apk + "/change.php";

        if (ip != null) {

            return ip;
        }

        return null;
    }

    public String uploadProfile() {

        String ip = "http://" + IpAddressFromSqlite() + "/" + folder + "/" + apk + "/upload.php";

        if (ip != null) {

            return ip;
        }

        return null;
    }

    public String profile() {

        String ip = "http://" + IpAddressFromSqlite() + "/" + folder + "/" + apk + "/profile.php";

        if (ip != null) {

            return ip;
        }
        return null;
    }

    public String sendcar() {
        String ip = "http://" + IpAddressFromSqlite() + "/" + folder + "/" + apk + "/car.php";
        if (ip != null) {
            return ip;
        }
        return null;
    }

    public String car() {
        String ip = "http://" + IpAddressFromSqlite() + "/" + folder + "/" + apk + "/cardetails.php";
        if (ip != null) {
            return ip;
        }
        return null;
    }

    public String release() {
        String ip = "http://" + IpAddressFromSqlite() + "/" + folder + "/" + apk + "/release.php";
        if (ip != null) {
            return ip;
        }
        return null;
    }

    public String terms() {
        String ip = "http://" + IpAddressFromSqlite() + "/" + folder + "/" + apk + "/index.php";

        if (ip != null) {

            return ip;
        }

        return null;
    }

    public String mybookings() {

        String ip = "http://" + IpAddressFromSqlite() + "/" + folder + "/" + apk + "/reservations.php";

        if (ip != null) {

            return ip;
        }

        return null;

    }

    public String delete() {

        String ip = "http://" + IpAddressFromSqlite() + "/" + folder + "/" + apk + "/delete.php";

        if (ip != null) {

            return ip;
        }

        return null;
    }

    public String cancel() {

        String ip = "http://" + IpAddressFromSqlite() + "/" + folder + "/" + apk + "/cancel.php";

        if (ip != null) {

            return ip;
        }

        return null;
    }

    public String updatecar() {

        String ip = "http://" + IpAddressFromSqlite() + "/" + folder + "/" + apk + "/updatecar.php";

        if (ip != null) {

            return ip;
        }

        return null;
    }

    public String mpesa() {

        String ip = "http://" + CallBackFromSqlite() + "/" + folder + "/" + apk + "/mpesa.php";
//        String ip="http://192.168.137.193/jeff1";


        if (ip != null) {

            return ip;
        }

        return null;
    }
}



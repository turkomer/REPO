package com.come335.dosyapaylasim;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class giris extends AppCompatActivity {

    private ProgressDialog pDialog;

    private String site_url = "";

    private String site_giris_url = "";

    SharedPreferences preferences;
    SharedPreferences.Editor editor;

    TextView txtuser, txtpwd;

    boolean giris_yapildi_mi = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.giris);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getString(R.string.actionbar_title));

        site_url = getString(R.string.webservice) + "giris&";

        preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        editor = preferences.edit();

        String username = preferences.getString("username", null);

        if(username != null){
            startActivity(new Intent(giris.this, ana_ekran.class));
            finish();
        }

        txtuser = (TextView)findViewById(R.id.giris_username);
        txtpwd = (TextView)findViewById(R.id.giris_password);
    }

    public void GirisYap(){
        if(InternetKontrol()) {
            site_giris_url = site_url + "username=" + txtuser.getText().toString().trim() + "&password=" + md5(txtpwd.getText().toString().trim());
            new GirisBilgileriniDene().execute();
        }else{
            Toast.makeText(getApplicationContext(), "İnternet bağlıntısı yok!", Toast.LENGTH_SHORT).show();
        }
    }


    public static final String md5(final String s) {
        final String MD5 = "MD5";
        try {
            // Create MD5 Hash
            MessageDigest digest = java.security.MessageDigest.getInstance(MD5);
            digest.update(s.getBytes());
            byte messageDigest[] = digest.digest();

            // Create Hex String
            StringBuilder hexString = new StringBuilder();
            for (byte aMessageDigest : messageDigest) {
                String h = Integer.toHexString(0xFF & aMessageDigest);
                while (h.length() < 2)
                    h = "0" + h;
                hexString.append(h);
            }
            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }

    private class GirisBilgileriniDene extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(giris.this);
            pDialog.setMessage("Yükleniyor...");
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            HttpHandler sh = new HttpHandler();
            String jsonStr = sh.makeServiceCall(site_giris_url).trim();

            if (jsonStr != null) {
                if(jsonStr.equals("true")){
                    giris_yapildi_mi = true;
                }
            } else {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(), "Sunucu ile bağlantı kurulurken hata oluştu!", Toast.LENGTH_LONG).show();
                        finish();
                    }
                });
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            if (pDialog.isShowing())
                pDialog.dismiss();
            if(giris_yapildi_mi){
                editor.putString("username", txtuser.getText().toString().trim());
                editor.commit();
                startActivity(new Intent(giris.this, ana_ekran.class));
                finish();
            }else{
                Toast.makeText(getApplicationContext(), "Kullanıcı adı ya da şifre hatalı!", Toast.LENGTH_LONG).show();
            }
        }

    }

    protected boolean InternetKontrol() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            return true;
        }
        return false;
    }

    public void cikisYap(){
        AlertDialog.Builder alertdialog=new AlertDialog.Builder(this);
        alertdialog.setMessage("Çıkmak istediğinizden emin misiniz?");
        alertdialog.setCancelable(false).setPositiveButton("Evet", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                System.exit(0);

            }
        }).setNegativeButton("Hayır", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        AlertDialog alert=alertdialog.create();
        alert.show();
    }

    @Override
    public void onBackPressed() {
        cikisYap();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                cikisYap();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void islemYap(View v){
        switch (v.getId()){
            case R.id.giris_btngiris:
                GirisYap();
                break;
        }
    }
}

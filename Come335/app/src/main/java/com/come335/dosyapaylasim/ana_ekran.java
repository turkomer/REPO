package com.come335.dosyapaylasim;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ana_ekran extends AppCompatActivity {

    private MyAppAdapter myAppAdapter;
    private ArrayList<Post> postArrayList;

    ListView lv;

    LinearLayout linearana;
    LinearLayout linearinternetyok;

    private ProgressDialog pDialog;

    private String site_url = "";

    private String yasal_uyari = "";

    SharedPreferences preferences;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ana_ekran);

        preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getString(R.string.actionbar_title));

        site_url = getString(R.string.webservice);

        lv = (ListView) findViewById(R.id.list);

        linearana = (LinearLayout) findViewById(R.id.anaekran_linearekran);
        linearinternetyok = (LinearLayout) findViewById(R.id.anaekran_linearbaglantiyok);

        final SwipeRefreshLayout swipeRefresh = (SwipeRefreshLayout) findViewById(R.id.swiperefresh);

        swipeRefresh.setColorSchemeResources(R.color.colorPrimary);

        postArrayList = new ArrayList<>();

        InternetKontrolBilgiCek();

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TextView list_id = (TextView) view.findViewById(R.id.list_id);
                TextView list_title = (TextView) view.findViewById(R.id.list_title);
                TextView list_subtitle = (TextView) view.findViewById(R.id.list_subtitle);
                TextView list_url = (TextView) view.findViewById(R.id.list_url);
                TextView list_date = (TextView) view.findViewById(R.id.list_date);
                ozelDialog(list_id.getText().toString(), list_title.getText().toString(), list_subtitle.getText().toString(), list_url.getText().toString(), list_date.getText().toString());
            }
        });

        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefresh.setRefreshing(false);
                InternetKontrolBilgiCek();
            }
        });

        String username = preferences.getString("username", null);

        if(username != null){
            Toast.makeText(getApplicationContext(), "Sayın " + username + " hoşgeldiniz.", Toast.LENGTH_SHORT).show();
        }
    }

    public void InternetKontrolBilgiCek(){
        if(InternetKontrol()) {
            linearana.setVisibility(View.VISIBLE);
            linearinternetyok.setVisibility(View.GONE);
            new DosyalariCek().execute();
        }else{
            linearana.setVisibility(View.GONE);
            linearinternetyok.setVisibility(View.VISIBLE);
        }
    }

    public class MyAppAdapter extends BaseAdapter {

        public class ViewHolder {
            TextView txtId, txtTitle, txtSubTitle, txtUrl, txtDate, txtExt;
        }

        public List<Post> parkingList;

        public Context context;
        ArrayList<Post> arraylist;

        private MyAppAdapter(List<Post> apps, Context context) {
            this.parkingList = apps;
            this.context = context;
            arraylist = new ArrayList<Post>();
            arraylist.addAll(parkingList);

        }

        @Override
        public int getCount() {
            return parkingList.size();
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {

            View rowView = convertView;
            ViewHolder viewHolder;

            if (rowView == null) {
                LayoutInflater inflater = getLayoutInflater();
                rowView = inflater.inflate(R.layout.list_layout, null);
                viewHolder = new ViewHolder();
                viewHolder.txtId = (TextView) rowView.findViewById(R.id.list_id);
                viewHolder.txtTitle = (TextView) rowView.findViewById(R.id.list_title);
                viewHolder.txtSubTitle = (TextView) rowView.findViewById(R.id.list_subtitle);
                viewHolder.txtUrl = (TextView) rowView.findViewById(R.id.list_url);
                viewHolder.txtDate = (TextView) rowView.findViewById(R.id.list_date);
                viewHolder.txtExt = (TextView) rowView.findViewById(R.id.list_ext);
                rowView.setTag(viewHolder);

            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            viewHolder.txtId.setText(parkingList.get(position).getPostId() + "");
            viewHolder.txtTitle.setText(parkingList.get(position).getPostTitle() + "");
            viewHolder.txtSubTitle.setText(parkingList.get(position).getPostSubTitle() + "");
            viewHolder.txtUrl.setText(parkingList.get(position).getPostFileUrl() + "");
            viewHolder.txtDate.setText(parkingList.get(position).getPostDate() + "");
            viewHolder.txtExt.setText(parkingList.get(position).getPostFileExt() + "");
            return rowView;
        }

        public void filter(String charText) {

            charText = charText.toLowerCase(Locale.getDefault());

            parkingList.clear();
            if (charText.length() == 0) {
                parkingList.addAll(arraylist);

            } else {
                for (Post postDetail : arraylist) {
                    if (charText.length() != 0 && postDetail.getPostTitle().toLowerCase(Locale.getDefault()).contains(charText)) {
                        parkingList.add(postDetail);
                    } else if (charText.length() != 0 && postDetail.getPostSubTitle().toLowerCase(Locale.getDefault()).contains(charText)) {
                        parkingList.add(postDetail);
                    }
                }
            }
            notifyDataSetChanged();
        }
    }

    public void ozelDialog(final String postid, String baslik, String aciklama, String url, String tarih){
        final Dialog dialog = new Dialog(ana_ekran.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.custom_dialog);

        final String dosya_uzantisi = url.substring(url.lastIndexOf(".")+1);

        TextView txtBaslik = (TextView) dialog.findViewById(R.id.custom_title);
        TextView txtAciklama = (TextView) dialog.findViewById(R.id.custom_subtitle);

        txtBaslik.setText(Html.fromHtml("<b>Dosya Adı:</b> " + baslik));
        txtAciklama.setText(Html.fromHtml("<b>Açıklama:</b> " + aciklama + "<br><br><b>Tarih:</b> " + tarih + "<br><br><b>Dosya Türü:</b> " + dosya_uzantisi));

        dialog.show();

        Button btnok = (Button) dialog.findViewById(R.id.btncustomtmm);
        Button btniptal = (Button) dialog.findViewById(R.id.btncustomiptal);

        btnok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(site_url + "indir&id=" + postid)));
                dialog.dismiss();
            }
        });

        btniptal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }

    public void uyari(String baslik, String mesaj){
        final Dialog dialog = new Dialog(ana_ekran.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); //Dialog başlığını gizle
        dialog.setContentView(R.layout.custom_dialog);

        TextView txtUstBaslik = (TextView) dialog.findViewById(R.id.custom_maintitle);
        TextView txtBaslik = (TextView) dialog.findViewById(R.id.custom_title);
        TextView txtAciklama = (TextView) dialog.findViewById(R.id.custom_subtitle);

        txtUstBaslik.setText(baslik);
        txtBaslik.setVisibility(View.GONE);
        txtAciklama.setText(Html.fromHtml(mesaj));

        dialog.show();

        Button btnok = (Button) dialog.findViewById(R.id.btncustomtmm);
        Button btniptal = (Button) dialog.findViewById(R.id.btncustomiptal);
        btniptal.setVisibility(View.GONE);
        btnok.setText("Kapat");

        btnok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setQueryHint("Ara...");
        //*** setOnQueryTextFocusChangeListener ***
        searchView.setOnQueryTextFocusChangeListener(new View.OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {

            }
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String query) {

                return false;
            }

            @Override
            public boolean onQueryTextChange(String searchQuery) {
                myAppAdapter.filter(searchQuery.toString().trim());
                lv.invalidate();
                return true;
            }
        });

        MenuItemCompat.setOnActionExpandListener(searchItem, new MenuItemCompat.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                // Do something when collapsed
                return true;  // Return true to collapse action view
            }

            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                // Do something when expanded
                return true;  // Return true to expand action view
            }
        });
        return true;
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

            case R.id.dosya_yukle:
                startActivity(new Intent(getApplicationContext(), dosya_yukle.class));
                break;
            case R.id.anaekran_tekrardene:
                InternetKontrolBilgiCek();
                break;
        }
    }

    private class DosyalariCek extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(ana_ekran.this);
            pDialog.setMessage("Yükleniyor...");
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            HttpHandler sh = new HttpHandler();
            String jsonStr = sh.makeServiceCall(site_url + "bilgi");

            if (jsonStr != null) {
                postArrayList.clear();
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);

                    JSONArray jBilgiler = jsonObj.getJSONArray("mobil");
                    JSONObject b = jBilgiler.getJSONObject(0);
                    yasal_uyari = b.getString("info");

                    JSONArray jDosyalar = jsonObj.getJSONArray("dosyalar");
                    for (int i = 0; i < jDosyalar.length(); i++) {
                        JSONObject c = jDosyalar.getJSONObject(i);
                        postArrayList.add(new Post(c.getString("id"), c.getString("name"), c.getString("description"), c.getString("file_url"), c.getString("file_ext"), c.getString("date")));
                    }
                } catch (final JSONException e) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(), "Veriler işlenirken hata oluştu!", Toast.LENGTH_LONG).show();
                            finish();
                        }
                    });
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

            myAppAdapter = new MyAppAdapter(postArrayList, ana_ekran.this);
            lv.setAdapter(myAppAdapter);
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

}

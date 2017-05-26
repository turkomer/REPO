package com.come335.dosyapaylasim;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.github.lzyzsd.circleprogress.DonutProgress;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;
import java.util.Random;

public class dosya_yukle extends AppCompatActivity {

    String selectedFilePath;
    File sourceFile;
    int totalSize = 0;
    private static final int PICK_FILE_REQUEST = 1;
    String FILE_UPLOAD_URL = "";
    public DonutProgress donut_progress;
    ImageView logo;

    EditText edtDosyaAdi;
    EditText edtDosyaAciklama;
    EditText edtDosyaSec;
    EditText edtGuvenlik;

    LinearLayout llust;
    LinearLayout llyukleniyor;

    int guvenlikSayi1 = 0;
    int guvenlikSayi2 = 0;
    int guvenlikCevap = 0;

    int girilenGuvenlikCevabi = 0;

    String[] ext = {"txt", "pdf", "docx", "xlsx", "pptx", "doc", "xls", "ppt"};

    String send_name="", send_desc="", send_ext="";

    View view;

    boolean yukleniyormu = false;

    MenuItem save_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dosya_yukle);

        FILE_UPLOAD_URL = getString(R.string.upload);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getString(R.string.actionbar_title));

        donut_progress = (DonutProgress) findViewById(R.id.donut_progress);

        logo = (ImageView) findViewById(R.id.upload_logo);

        llust = (LinearLayout) findViewById(R.id.dosyayukle_layoutust);
        llyukleniyor = (LinearLayout) findViewById(R.id.dosyayukle_layoutyukleniyor);

        edtDosyaAdi = (EditText) findViewById(R.id.dosyayukle_dosyaadi);
        edtDosyaAciklama = (EditText) findViewById(R.id.dosyayukle_dosyaaciklama);
        edtDosyaSec = (EditText) findViewById(R.id.dosyayukle_gozat);
        edtGuvenlik = (EditText) findViewById(R.id.dosyayukle_guvenlik);

        Random generator = new Random();
        guvenlikSayi1 = generator.nextInt(10) + 1;
        guvenlikSayi2 = generator.nextInt(10) + 1;
        guvenlikCevap = guvenlikSayi1 + guvenlikSayi2;

        edtGuvenlik.setHint(guvenlikSayi1 + " + " + guvenlikSayi2 + " = ?");
    }

    private void dosyaSec() {
        Intent intent = new Intent();
        intent.setType("*/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Dosya Seç..."), PICK_FILE_REQUEST);
    }

    public boolean alanlarDoluMu(){
        try{
            girilenGuvenlikCevabi = Integer.parseInt(edtGuvenlik.getText().toString().trim());
            if(edtDosyaAdi.getText().toString().trim().length() < 3){
                Toast.makeText(dosya_yukle.this, "Dosya adı en az 3 karakter olmalıdır!", Toast.LENGTH_SHORT).show();
                return  false;
            }else if(edtDosyaAciklama.getText().toString().trim().length() < 8){
                Toast.makeText(dosya_yukle.this, "Dosya açıklaması en az 8 karakter olmalıdır!", Toast.LENGTH_SHORT).show();
                return  false;
            }else if(girilenGuvenlikCevabi < 0){
                Toast.makeText(dosya_yukle.this, "Güvenlik sorusu yanıtını giriniz!", Toast.LENGTH_SHORT).show();
                return  false;
            }else if(guvenlikCevap != girilenGuvenlikCevabi){
                Toast.makeText(dosya_yukle.this, "Güvenlik sorusu yanıtı hatalı!", Toast.LENGTH_SHORT).show();
                return  false;
            }else{
                send_name = edtDosyaAdi.getText().toString().trim();
                send_desc = edtDosyaAciklama.getText().toString().trim();
                return true;
            }
        }catch(Exception e){}

        Toast.makeText(dosya_yukle.this, "Lütfen tüm alanları doldurun!", Toast.LENGTH_SHORT).show();
        return  false;
    }

    public void islemYap(View v){
        switch (v.getId()){
            case R.id.dosyayukle_dosyasecbtn:
                dosyaSec();
                break;

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == PICK_FILE_REQUEST) {
                if (data == null) {
                    Toast.makeText(this, "Hata oluştu!", Toast.LENGTH_SHORT).show();
                    return;
                }

                try{
                    Uri selectedFileUri = data.getData();
                    selectedFilePath = FilePath.getPath(this, selectedFileUri);

                    if (selectedFilePath != null && !selectedFilePath.equals("")) {
                        String[] isimBolunmus = selectedFilePath.split("/");
                        String dosyaAdi = isimBolunmus[isimBolunmus.length-1];
                        String dosyaUzanti = dosyaAdi.substring(dosyaAdi.lastIndexOf(".")+1).toLowerCase();

                        if(Arrays.asList(ext).contains(dosyaUzanti)){
                            edtDosyaSec.setText(dosyaAdi);
                            send_ext = dosyaUzanti;
                        }else{
                            Toast.makeText(this, "Yüklenen dosya formatı hatalı.\n(txt, pdf, docx, xlsx, pptx, doc, xls, ppt uzantılı dosyalar desteklenmektedir.)", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(this, "Dosya yüklenemiyor!", Toast.LENGTH_SHORT).show();
                    }
                }catch (Exception e){
                    Toast.makeText(this, "Hata oluştu!", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.upload, menu);
        save_btn = (MenuItem) menu.findItem(R.id.action_save);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.action_save:
                if(yukleniyormu == false){
                    if(alanlarDoluMu()){
                        try{
                            view = this.getCurrentFocus();
                            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                        }catch (Exception e){}
                        if (send_ext.length() > 1 && edtDosyaSec.getText().toString().length() > 3) {
                            new UploadFileToServer().execute();
                        } else {
                            Toast.makeText(dosya_yukle.this, "Lütfen dosya seçin!", Toast.LENGTH_SHORT).show();
                        }
                    }
                }else{
                    Toast.makeText(dosya_yukle.this, "Yükleme işlemi devam ediyor!", Toast.LENGTH_SHORT).show();
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    String[] search_key = {"Ç","ç","Ğ","ğ","ı","İ","Ö","ö","Ş","ş","Ü","ü"};
    String[] replace_key = {"--101--","--102--","--103--","--104--","--105--","--106--","--107--","--108--","--109--","--110--","--111--","--112--"};

    public String StringDecode(String str) {
        for (int i = 0; i <= 11; i++){
            str = str.replaceAll(search_key[i], replace_key[i]);
        }
        return str;
    }

    private class UploadFileToServer extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            save_btn.setVisible(false);
            donut_progress.setProgress(0);
            llust.setVisibility(View.GONE);
            llyukleniyor.setVisibility(View.VISIBLE);
            sourceFile = new File(selectedFilePath);
            totalSize = (int) sourceFile.length();

            try {
                Animation anim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.anim);
                logo.startAnimation(anim);
            }catch (Exception e){ }

            super.onPreExecute();
        }

        @Override
        protected void onProgressUpdate(String... progress) {
            donut_progress.setProgress(Integer.parseInt(progress[0]));
        }

        @Override
        protected String doInBackground(String... args) {
            String sonuc = "";
            if(InternetKontrol() == false){
                return "internet yok";
            }else if(totalSize > 31500000){ //30mb'dan büyük
                return "dosya büyük";
            }else{
                HttpURLConnection.setFollowRedirects(false);
                HttpURLConnection connection = null;
                String fileName = sourceFile.getName();

                try {
                    connection = (HttpURLConnection) new URL(FILE_UPLOAD_URL).openConnection();
                    connection.setRequestMethod("POST");
                    String boundary = "---------------------------boundary";
                    String tail = "\r\n--" + boundary + "--\r\n";
                    connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);
                    connection.setDoOutput(true);

                    String metadataPart1 = "--" + boundary + "\r\n"
                            + "Content-Disposition: form-data; name=\"name\"\r\n\r\n"
                            + StringDecode(send_name) + "\r\n";

                    String metadataPart2 = "--" + boundary + "\r\n"
                            + "Content-Disposition: form-data; name=\"desc\"\r\n\r\n"
                            + StringDecode(send_desc) + "\r\n";

                    String metadataPart3 = "--" + boundary + "\r\n"
                            + "Content-Disposition: form-data; name=\"ext\"\r\n\r\n"
                            + send_ext + "\r\n";

                    String metadataPart = metadataPart1 + metadataPart2 + metadataPart3;

                    String fileHeader1 = "--" + boundary + "\r\n"
                            + "Content-Disposition: form-data; name=\"uploadfile\"; filename=\""
                            + fileName + "\"\r\n"
                            + "Content-Type: application/octet-stream\r\n"
                            + "Content-Transfer-Encoding: binary\r\n";

                    long fileLength = sourceFile.length() + tail.length();
                    String fileHeader2 = "Content-length: " + fileLength + "\r\n";
                    String fileHeader = fileHeader1 + fileHeader2 + "\r\n";
                    String stringData = metadataPart + fileHeader;

                    long requestLength = stringData.length() + fileLength;
                    connection.setRequestProperty("Content-length", "" + requestLength);
                    connection.setFixedLengthStreamingMode((int) requestLength);
                    connection.connect();

                    DataOutputStream out = new DataOutputStream(connection.getOutputStream());
                    out.writeBytes(stringData);
                    out.flush();

                    int progress = 0;
                    int bytesRead = 0;
                    byte buf[] = new byte[1024];
                    BufferedInputStream bufInput = new BufferedInputStream(new FileInputStream(sourceFile));
                    while ((bytesRead = bufInput.read(buf)) != -1) {
                        out.write(buf, 0, bytesRead);
                        out.flush();
                        progress += bytesRead;
                        int yuzde = (int) ((((float)progress / (float)totalSize) * 100));
                        publishProgress("" + yuzde);
                    }

                    out.writeBytes(tail);
                    out.flush();
                    out.close();

                    BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    String line = reader.readLine().toString().trim();

                    if(line.equals("1")){
                        sonuc = "basarili";
                    }else{
                        sonuc = "basarisiz";
                    }
                } catch (Exception e) {
                    sonuc = "hata";
                } finally {
                    if (connection != null) connection.disconnect();
                }
            }
            return sonuc;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            if(result.equals("basarili")){
                Toast.makeText(getApplicationContext(), "Yükleme başarıyla gerçekleştirildi.", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(getApplicationContext(), ana_ekran.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }else if(result.equals("internet yok")){
                Toast.makeText(getApplicationContext(), "Lütfen internet bağlantınızı kontrol edin.", Toast.LENGTH_SHORT).show();
            }else if(result.equals("dosya büyük")){
                Toast.makeText(getApplicationContext(), "Dosya boyutu max 30mb olabilir!", Toast.LENGTH_SHORT).show();
            }else if(result.equals("basarisiz")){
                Toast.makeText(getApplicationContext(), "Dosya sunucuya yüklenirken hata oluştu!", Toast.LENGTH_LONG).show();
            }else{
                Toast.makeText(getApplicationContext(), "Hata oluştu!", Toast.LENGTH_LONG).show();
            }

            llust.setVisibility(View.VISIBLE);
            llyukleniyor.setVisibility(View.GONE);

            yukleniyormu = false;
            save_btn.setVisible(true);
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
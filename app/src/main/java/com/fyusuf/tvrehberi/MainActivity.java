package com.fyusuf.tvrehberi;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.InputStream;

public class MainActivity extends AppCompatActivity {
    private ImageView film1Img,film2Img,film1Logo,film2Logo;
    private ImageView dizi1Img,dizi2Img,dizi1Logo,dizi2Logo;
    private ImageView haber1Img,haber2Img,haber1Logo,haber2Logo;
    private ImageView spor1Img,spor2Img,spor1Logo,spor2Logo;
    private ImageView egl1Img,egl2Img,egl1Logo,egl2Logo;
    private TextView film1Txt,film2Txt,dizi1Txt,dizi2Txt,haber1Txt,haber2Txt,spor1Txt,spor2Txt,egl1Txt,egl2Txt;
    private ProgressDialog progressDialog;
    private static String URL = "http://www.hurriyet.com.tr/tv-rehberi/";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if(isOnline())
            new Program().execute();
        else{
            Toast.makeText(getApplicationContext(), "İnternet bağlantısı sağlanamadı!", Toast.LENGTH_SHORT).show();
        }
    }

    public void showAll(View view){
        Intent intent = new Intent(this,DetailActivity.class);
        if(view.getId() == R.id.btnFilm) {
            intent.putExtra("url", "http://www.hurriyet.com.tr/tv-rehberi/program-akisi/4/film");
            startActivity(intent);
        }
        else if (view.getId() == R.id.btnDizi){
            intent.putExtra("url", "http://www.hurriyet.com.tr/tv-rehberi/program-akisi/5/dizi");
            startActivity(intent);
        }
        else if (view.getId() == R.id.btnHaber){
            intent.putExtra("url", "http://www.hurriyet.com.tr/tv-rehberi/program-akisi/1/haber");
            startActivity(intent);
        }
        else if (view.getId() == R.id.btnSpor){
            intent.putExtra("url", "http://www.hurriyet.com.tr/tv-rehberi/program-akisi/3/spor");
            startActivity(intent);
        }
        else if (view.getId() == R.id.btnEgl){
            intent.putExtra("url", "http://www.hurriyet.com.tr/tv-rehberi/program-akisi/7/eğlence");
            startActivity(intent);
        }
        else {
            Toast.makeText(this, "wrong button", Toast.LENGTH_SHORT).show();
        }
    }

    private class Program extends AsyncTask<Void, Void, Void> {
        String film1,film2,dizi1,dizi2,haber1,haber2,spor1,spor2,egl1,egl2;
        Bitmap bitmapFilmLogo1,bitmapFilmLogo2,bitmapFilmImg1,bitmapFilmImg2;
        Bitmap bitmapDiziLogo1,bitmapDiziLogo2,bitmapDiziImg1,bitmapDiziImg2;
        Bitmap bitmapHaberLogo1,bitmapHaberLogo2,bitmapHaberImg1,bitmapHaberImg2;
        Bitmap bitmapSporLogo1,bitmapSporLogo2,bitmapSporImg1,bitmapSporImg2;
        Bitmap bitmapEglLogo1,bitmapEglLogo2,bitmapEglImg1,bitmapEglImg2;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(MainActivity.this);
            progressDialog.setTitle("TvRehberi");
            progressDialog.setMessage("Veri Çekiliyor...");
            progressDialog.setIndeterminate(false);
            progressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            try{
                Document doc  = Jsoup.connect(URL).get();
                //film parse
                Element film = doc.select("div.TVCategory").get(0);
                Elements filmTxt = film.select("div.txt");
                Elements filmTime = film.select("div.time");
                Elements filmImg = film.select("div[class=image FL] img[src]");
                Elements filmLogo = film.select("div[class=Logo] img[src]");
                InputStream input;
                if(filmTxt.size() > 0 && filmTime.size() > 0 && filmImg.size() > 0 && filmLogo.size() > 0) {
                    film1 = filmTime.get(0).text() + "\n" + filmTxt.get(0).text();
                    film2 = filmTime.get(1).text() + "\n" + filmTxt.get(1).text();
                    input = new java.net.URL(filmLogo.get(0).getElementsByTag("img").attr("src")).openStream();
                    bitmapFilmLogo1 = BitmapFactory.decodeStream(input);
                    input = new java.net.URL(filmLogo.get(1).getElementsByTag("img").attr("src")).openStream();
                    bitmapFilmLogo2 = BitmapFactory.decodeStream(input);
                    input = new java.net.URL(filmImg.get(0).getElementsByTag("img").attr("src")).openStream();
                    bitmapFilmImg1 = BitmapFactory.decodeStream(input);
                    input = new java.net.URL(filmImg.get(3).getElementsByTag("img").attr("src")).openStream();
                    bitmapFilmImg2 = BitmapFactory.decodeStream(input);
                }
                else{
                    film1 = "Film Bulunamadı";
                    film2 = "Film Bulunamadı";
                }
                //dizi parse
                Element dizi = doc.select("div.TVCategory").get(1);
                Elements diziTxt = dizi.select("div.txt");
                Elements diziTime = dizi.select("div.time");
                Elements diziImg = dizi.select("div[class=image FL] img[src]");
                Elements diziLogo = dizi.select("div[class=Logo] img[src]");
                if(diziTxt.size() > 0 && diziTime.size() > 0 && diziImg.size() > 0 && diziLogo.size() > 0) {
                    dizi1 = diziTime.get(0).text() + "\n" + diziTxt.get(0).text();
                    dizi2 = diziTime.get(1).text() + "\n" + diziTxt.get(1).text();
                    input = new java.net.URL(diziLogo.get(0).getElementsByTag("img").attr("src")).openStream();
                    bitmapDiziLogo1 = BitmapFactory.decodeStream(input);
                    input = new java.net.URL(diziLogo.get(1).getElementsByTag("img").attr("src")).openStream();
                    bitmapDiziLogo2 = BitmapFactory.decodeStream(input);
                    input = new java.net.URL(diziImg.get(0).getElementsByTag("img").attr("src")).openStream();
                    bitmapDiziImg1 = BitmapFactory.decodeStream(input);
                    input = new java.net.URL(diziImg.get(3).getElementsByTag("img").attr("src")).openStream();
                    bitmapDiziImg2 = BitmapFactory.decodeStream(input);
                }
                else{
                    dizi1 = "Dizi Bulunamadı";
                    dizi2 = "Dizi Bulunamadı";
                }
                //haber parse
                Element haber = doc.select("div.TVCategory").get(2);
                Elements haberTxt = haber.select("div.txt");
                Elements haberTime = haber.select("div.time");
                Elements haberImg = haber.select("div[class=image FL] img[src]");
                Elements haberLogo = haber.select("div[class=Logo] img[src]");
                if(haberTxt.size() > 0 && haberTime.size() > 0 && haberImg.size() > 0 && haberLogo.size() > 0) {
                    haber1 = haberTime.get(0).text() + "\n" + haberTxt.get(0).text();
                    haber2 = haberTime.get(1).text() + "\n" + haberTxt.get(1).text();
                    input = new java.net.URL(haberLogo.get(0).getElementsByTag("img").attr("src")).openStream();
                    bitmapHaberLogo1 = BitmapFactory.decodeStream(input);
                    input = new java.net.URL(haberLogo.get(1).getElementsByTag("img").attr("src")).openStream();
                    bitmapHaberLogo2 = BitmapFactory.decodeStream(input);
                    input = new java.net.URL(haberImg.get(0).getElementsByTag("img").attr("src")).openStream();
                    bitmapHaberImg1 = BitmapFactory.decodeStream(input);
                    input = new java.net.URL(haberImg.get(3).getElementsByTag("img").attr("src")).openStream();
                    bitmapHaberImg2 = BitmapFactory.decodeStream(input);
                }
                else{
                    haber1 = "Haber Bulunamadı";
                    haber2 = "Haber Bulunamadı";
                }
                //spor parse
                Element spor = doc.select("div.TVCategory").get(3);
                Elements sporTxt = spor.select("div.txt");
                Elements sporTime = spor.select("div.time");
                Elements sporImg = spor.select("div[class=image FL] img[src]");
                Elements sporLogo = spor.select("div[class=Logo] img[src]");
                if(sporTxt.size() > 0 && sporTime.size() > 0 && sporImg.size() > 0 && sporLogo.size() > 0) {
                    spor1 = sporTime.get(0).text() + "\n" + sporTxt.get(0).text();
                    spor2 = sporTime.get(1).text() + "\n" + sporTxt.get(1).text();
                    input = new java.net.URL(sporLogo.get(0).getElementsByTag("img").attr("src")).openStream();
                    bitmapSporLogo1 = BitmapFactory.decodeStream(input);
                    input = new java.net.URL(sporLogo.get(1).getElementsByTag("img").attr("src")).openStream();
                    bitmapSporLogo2 = BitmapFactory.decodeStream(input);
                    input = new java.net.URL(sporImg.get(0).getElementsByTag("img").attr("src")).openStream();
                    bitmapSporImg1 = BitmapFactory.decodeStream(input);
                    input = new java.net.URL(sporImg.get(3).getElementsByTag("img").attr("src")).openStream();
                    bitmapSporImg2 = BitmapFactory.decodeStream(input);
                }
                else{
                    spor1 = "Spor Bulunamadı";
                    spor2 = "Spor Bulunamadı";
                }
                //eglence parse
                Element egl = doc.select("div.TVCategory").get(4);
                Elements eglTxt = egl.select("div.txt");
                Elements eglTime = egl.select("div.time");
                Elements eglImg = egl.select("div[class=image FL] img[src]");
                Elements eglLogo = egl.select("div[class=Logo] img[src]");
                if(eglTxt.size() > 0 && eglTime.size() > 0 && eglImg.size() > 0 && eglLogo.size() > 0) {
                    egl1 = eglTime.get(0).text() + "\n" + eglTxt.get(0).text();
                    egl2 = eglTime.get(1).text() + "\n" + eglTxt.get(1).text();
                    input = new java.net.URL(eglLogo.get(0).getElementsByTag("img").attr("src")).openStream();
                    bitmapEglLogo1 = BitmapFactory.decodeStream(input);
                    input = new java.net.URL(eglLogo.get(1).getElementsByTag("img").attr("src")).openStream();
                    bitmapEglLogo2 = BitmapFactory.decodeStream(input);
                    input = new java.net.URL(eglImg.get(0).getElementsByTag("img").attr("src")).openStream();
                    bitmapEglImg1 = BitmapFactory.decodeStream(input);
                    input = new java.net.URL(eglImg.get(3).getElementsByTag("img").attr("src")).openStream();
                    bitmapEglImg2 = BitmapFactory.decodeStream(input);
                }
                else{
                    egl1 = "Eğlence Bulunamadı";
                    egl2 = "Eğlence Bulunamadı";
                }
            }catch (Exception e){
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            //film
            film1Img = (ImageView) findViewById(R.id.film1Image);
            film2Img = (ImageView) findViewById(R.id.film2Image);
            film1Logo = (ImageView) findViewById(R.id.film1Logo);
            film2Logo = (ImageView) findViewById(R.id.film2Logo);
            film1Txt = (TextView) findViewById(R.id.film1Txt);
            film2Txt = (TextView) findViewById(R.id.film2Txt);
            //dizi
            dizi1Txt = (TextView) findViewById(R.id.dizi1Txt);
            dizi2Txt = (TextView) findViewById(R.id.dizi2Txt);
            dizi1Img = (ImageView) findViewById(R.id.dizi1Image);
            dizi2Img = (ImageView) findViewById(R.id.dizi2Image);
            dizi1Logo = (ImageView) findViewById(R.id.dizi1Logo);
            dizi2Logo = (ImageView) findViewById(R.id.dizi2Logo);
            //haber
            haber1Img = (ImageView) findViewById(R.id.haber1Image);
            haber2Img = (ImageView) findViewById(R.id.haber2Image);
            haber1Logo = (ImageView) findViewById(R.id.haber1Logo);
            haber2Logo = (ImageView) findViewById(R.id.haber2Logo);
            haber1Txt = (TextView) findViewById(R.id.haber1Txt);
            haber2Txt = (TextView) findViewById(R.id.haber2Txt);
            //spor
            spor1Img = (ImageView) findViewById(R.id.spor1Image);
            spor2Img = (ImageView) findViewById(R.id.spor2Image);
            spor1Logo = (ImageView) findViewById(R.id.spor1Logo);
            spor2Logo = (ImageView) findViewById(R.id.spor2Logo);
            spor1Txt = (TextView) findViewById(R.id.spor1Txt);
            spor2Txt = (TextView) findViewById(R.id.spor2Txt);
            //eglence
            egl1Img = (ImageView) findViewById(R.id.egl1Image);
            egl2Img = (ImageView) findViewById(R.id.egl2Image);
            egl1Logo = (ImageView) findViewById(R.id.egl1Logo);
            egl2Logo = (ImageView) findViewById(R.id.egl2Logo);
            egl1Txt = (TextView) findViewById(R.id.egl1Txt);
            egl2Txt = (TextView) findViewById(R.id.egl2Txt);

            //----------------
            film1Txt.setText(film1);
            film2Txt.setText(film2);
            dizi1Txt.setText(dizi1);
            dizi2Txt.setText(dizi2);
            haber1Txt.setText(haber1);
            haber2Txt.setText(haber2);
            spor1Txt.setText(spor1);
            spor2Txt.setText(spor2);
            egl1Txt.setText(egl1);
            egl2Txt.setText(egl2);
            if(film1Logo != null)
                film1Logo.setImageBitmap(bitmapFilmLogo1);
            if(film2Logo != null)
                film2Logo.setImageBitmap(bitmapFilmLogo2);
            if(film1Img != null)
                film1Img.setImageBitmap(bitmapFilmImg1);
            if(film2Img != null)
                film2Img.setImageBitmap(bitmapFilmImg2);
            dizi1Logo.setImageBitmap(bitmapDiziLogo1);
            dizi2Logo.setImageBitmap(bitmapDiziLogo2);
            dizi1Img.setImageBitmap(bitmapDiziImg1);
            dizi2Img.setImageBitmap(bitmapDiziImg2);
            haber1Logo.setImageBitmap(bitmapHaberLogo1);
            haber2Logo.setImageBitmap(bitmapHaberLogo2);
            haber1Img.setImageBitmap(bitmapHaberImg1);
            haber2Img.setImageBitmap(bitmapHaberImg2);
            spor1Logo.setImageBitmap(bitmapSporLogo1);
            spor2Logo.setImageBitmap(bitmapSporLogo2);
            spor1Img.setImageBitmap(bitmapSporImg1);
            spor2Img.setImageBitmap(bitmapSporImg2);
            egl1Logo.setImageBitmap(bitmapEglLogo1);
            egl2Logo.setImageBitmap(bitmapEglLogo2);
            egl1Img.setImageBitmap(bitmapEglImg1);
            egl2Img.setImageBitmap(bitmapEglImg2);
            progressDialog.dismiss();
        }
    }

    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }


}

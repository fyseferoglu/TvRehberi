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
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerFilm;
    private RecyclerView recyclerDizi;
    private RecyclerView recyclerHaber;
    private RecyclerView recyclerSpor;
    private RecyclerView recyclerEgl;
    private ArrayList<Program> filmList;
    private ArrayList<Program> diziList;
    private ArrayList<Program> haberList;
    private ArrayList<Program> sporList;
    private ArrayList<Program> eglList;
    private ProgressDialog progressDialog;
    private static String url = "http://www.hurriyet.com.tr/tv-rehberi/";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        recyclerFilm = (RecyclerView) findViewById(R.id.recycler_view_film);
        recyclerDizi = (RecyclerView) findViewById(R.id.recycler_view_dizi);
        recyclerHaber = (RecyclerView) findViewById(R.id.recycler_view_haber);
        recyclerSpor = (RecyclerView) findViewById(R.id.recycler_view_spor);
        recyclerEgl = (RecyclerView) findViewById(R.id.recycler_view_eglence);
        filmList =  new ArrayList<>();
        diziList =  new ArrayList<>();
        haberList =  new ArrayList<>();
        sporList =  new ArrayList<>();
        eglList =  new ArrayList<>();
        GridLayoutManager layoutManager = new GridLayoutManager(this,2);
        GridLayoutManager layoutManager1 = new GridLayoutManager(this,2);
        GridLayoutManager layoutManager2 = new GridLayoutManager(this,2);
        GridLayoutManager layoutManager3 = new GridLayoutManager(this,2);
        GridLayoutManager layoutManager4 = new GridLayoutManager(this,2);

        layoutManager.scrollToPosition(0);
        layoutManager1.scrollToPosition(0);
        layoutManager2.scrollToPosition(0);
        layoutManager3.scrollToPosition(0);
        layoutManager4.scrollToPosition(0);
        recyclerFilm.setLayoutManager(layoutManager);
        recyclerDizi.setLayoutManager(layoutManager1);
        recyclerHaber.setLayoutManager(layoutManager2);
        recyclerSpor.setLayoutManager(layoutManager3);
        recyclerEgl.setLayoutManager(layoutManager4);
        MainRecyclerAdapter adapterFilm = new MainRecyclerAdapter(filmList);
        recyclerFilm.setHasFixedSize(true);
        recyclerFilm.setAdapter(adapterFilm);
        recyclerFilm.setItemAnimator(new DefaultItemAnimator());

        MainRecyclerAdapter adapterDizi = new MainRecyclerAdapter(diziList);
        recyclerDizi.setHasFixedSize(true);
        recyclerDizi.setAdapter(adapterDizi);
        recyclerDizi.setItemAnimator(new DefaultItemAnimator());

        MainRecyclerAdapter adapterHaber = new MainRecyclerAdapter(haberList);
        recyclerHaber.setHasFixedSize(true);
        recyclerHaber.setAdapter(adapterHaber);
        recyclerHaber.setItemAnimator(new DefaultItemAnimator());

        MainRecyclerAdapter adapterSpor = new MainRecyclerAdapter(sporList);
        recyclerSpor.setHasFixedSize(true);
        recyclerSpor.setAdapter(adapterSpor);
        recyclerSpor.setItemAnimator(new DefaultItemAnimator());

        MainRecyclerAdapter adapterEgl = new MainRecyclerAdapter(eglList);
        recyclerEgl.setHasFixedSize(true);
        recyclerEgl.setAdapter(adapterEgl);
        recyclerEgl.setItemAnimator(new DefaultItemAnimator());
        if(isOnline())
            new ProgramClass().execute();
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

    private class ProgramClass extends AsyncTask<Void, Void, Void> {
        Bitmap bitmapLogo,bitmapImg;
        ArrayList<String> myList = new ArrayList<>();
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
                Document doc  = Jsoup.connect(url).get();
                //film parse
                Element film = doc.select("div.TVCategory").get(0);
                Elements filmTxt = film.select("div.txt");
                Elements filmTime = film.select("div.time");
                Elements filmImg = film.select("div[class=image FL] img[src]");
                Elements filmLogo = film.select("div[class=Logo] img[src]");
                InputStream input;
                for(int i = 0; i < filmImg.size(); i+=3){
                    myList.add(filmImg.get(i).getElementsByTag("img").attr("src"));
                }

                int size = filmTxt.size();
                if(size >= 2)
                    size = 2;
                for(int i = 0; i < size; i++){
                    if(exists(myList.get(i))) {
                        input = new java.net.URL(myList.get(i)).openStream();
                        bitmapImg = BitmapFactory.decodeStream(input);
                    }
                    if(exists(filmLogo.get(i).getElementsByTag("img").attr("src"))) {
                        input = new java.net.URL(filmLogo.get(i).getElementsByTag("img").attr("src")).openStream();
                        bitmapLogo = BitmapFactory.decodeStream(input);
                    }
                    filmList.add(new Program(filmTxt.get(i).text(),filmTime.get(i).text(),bitmapImg,bitmapLogo));
                }
                //dizi parse
                Element dizi = doc.select("div.TVCategory").get(1);
                Elements diziTxt = dizi.select("div.txt");
                Elements diziTime = dizi.select("div.time");
                Elements diziImg = dizi.select("div[class=image FL] img[src]");
                Elements diziLogo = dizi.select("div[class=Logo] img[src]");
                myList.clear();
                for(int i = 0; i < diziImg.size(); i+=3){
                    myList.add(diziImg.get(i).getElementsByTag("img").attr("src"));
                }

                size = diziTxt.size();
                if(size >= 2)
                    size = 2;
                for(int i = 0; i < size; i++){
                    if(exists(myList.get(i))) {
                        input = new java.net.URL(myList.get(i)).openStream();
                        bitmapImg = BitmapFactory.decodeStream(input);
                    }
                    if(exists(diziLogo.get(i).getElementsByTag("img").attr("src"))) {
                        input = new java.net.URL(diziLogo.get(i).getElementsByTag("img").attr("src")).openStream();
                        bitmapLogo = BitmapFactory.decodeStream(input);
                    }
                    diziList.add(new Program(diziTxt.get(i).text(),diziTime.get(i).text(),bitmapImg,bitmapLogo));
                }
                //haber parse
                Element haber = doc.select("div.TVCategory").get(2);
                Elements haberTxt = haber.select("div.txt");
                Elements haberTime = haber.select("div.time");
                Elements haberImg = haber.select("div[class=image FL] img[src]");
                Elements haberLogo = haber.select("div[class=Logo] img[src]");
                myList.clear();
                for(int i = 0; i < haberImg.size(); i+=3){
                    myList.add(haberImg.get(i).getElementsByTag("img").attr("src"));
                }

                size = haberTxt.size();
                if(size >= 2)
                    size = 2;
                for(int i = 0; i < size; i++){
                    if(exists(myList.get(i))) {
                        input = new java.net.URL(myList.get(i)).openStream();
                        bitmapImg = BitmapFactory.decodeStream(input);
                    }
                    if(exists(haberLogo.get(i).getElementsByTag("img").attr("src"))) {
                        input = new java.net.URL(haberLogo.get(i).getElementsByTag("img").attr("src")).openStream();
                        bitmapLogo = BitmapFactory.decodeStream(input);
                    }
                    haberList.add(new Program(haberTxt.get(i).text(),haberTime.get(i).text(),bitmapImg,bitmapLogo));
                }
                //spor parse
                Element spor = doc.select("div.TVCategory").get(3);
                Elements sporTxt = spor.select("div.txt");
                Elements sporTime = spor.select("div.time");
                Elements sporImg = spor.select("div[class=image FL] img[src]");
                Elements sporLogo = spor.select("div[class=Logo] img[src]");
                myList.clear();
                for(int i = 0; i < sporImg.size(); i+=3){
                    myList.add(sporImg.get(i).getElementsByTag("img").attr("src"));
                }

                size = sporTxt.size();
                if(size >= 2)
                    size = 2;
                for(int i = 0; i < size; i++){
                    if(exists(myList.get(i))) {
                        input = new java.net.URL(myList.get(i)).openStream();
                        bitmapImg = BitmapFactory.decodeStream(input);
                    }
                    if(exists(sporLogo.get(i).getElementsByTag("img").attr("src"))) {
                        input = new java.net.URL(sporLogo.get(i).getElementsByTag("img").attr("src")).openStream();
                        bitmapLogo = BitmapFactory.decodeStream(input);
                    }
                    sporList.add(new Program(sporTxt.get(i).text(),sporTime.get(i).text(),bitmapImg,bitmapLogo));
                }
                //eglence parse
                Element egl = doc.select("div.TVCategory").get(4);
                Elements eglTxt = egl.select("div.txt");
                Elements eglTime = egl.select("div.time");
                Elements eglImg = egl.select("div[class=image FL] img[src]");
                Elements eglLogo = egl.select("div[class=Logo] img[src]");
                myList.clear();
                for(int i = 0; i < eglImg.size(); i+=3){
                    myList.add(eglImg.get(i).getElementsByTag("img").attr("src"));
                }

                size = eglTxt.size();
                if(size >= 2)
                    size = 2;
                for(int i = 0; i < size; i++){

                    if(exists(myList.get(i))) {
                        input = new java.net.URL(myList.get(i)).openStream();
                        bitmapImg = BitmapFactory.decodeStream(input);
                    }
                    if(exists(eglLogo.get(i).getElementsByTag("img").attr("src"))){
                        input = new java.net.URL(eglLogo.get(i).getElementsByTag("img").attr("src")).openStream();
                        bitmapLogo = BitmapFactory.decodeStream(input);
                    }
                    eglList.add(new Program(eglTxt.get(i).text(),eglTime.get(i).text(),bitmapImg,bitmapLogo));
                }
            }catch (Exception e){
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            MainRecyclerAdapter adapterFilm = new MainRecyclerAdapter(filmList);
            recyclerFilm.setHasFixedSize(true);
            recyclerFilm.setAdapter(adapterFilm);
            recyclerFilm.setItemAnimator(new DefaultItemAnimator());

            MainRecyclerAdapter adapterDizi = new MainRecyclerAdapter(diziList);
            recyclerDizi.setHasFixedSize(true);
            recyclerDizi.setAdapter(adapterDizi);
            recyclerDizi.setItemAnimator(new DefaultItemAnimator());

            MainRecyclerAdapter adapterHaber = new MainRecyclerAdapter(haberList);
            recyclerHaber.setHasFixedSize(true);
            recyclerHaber.setAdapter(adapterHaber);
            recyclerHaber.setItemAnimator(new DefaultItemAnimator());

            MainRecyclerAdapter adapterSpor = new MainRecyclerAdapter(sporList);
            recyclerSpor.setHasFixedSize(true);
            recyclerSpor.setAdapter(adapterSpor);
            recyclerSpor.setItemAnimator(new DefaultItemAnimator());

            MainRecyclerAdapter adapterEgl = new MainRecyclerAdapter(eglList);
            recyclerEgl.setHasFixedSize(true);
            recyclerEgl.setAdapter(adapterEgl);
            recyclerEgl.setItemAnimator(new DefaultItemAnimator());
            progressDialog.dismiss();
        }
    }

    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    public static boolean exists(String URLName){
        try {
            HttpURLConnection.setFollowRedirects(false);
            // note : you may also need
            //        HttpURLConnection.setInstanceFollowRedirects(false)
            HttpURLConnection con = (HttpURLConnection) new URL(URLName).openConnection();
            con.setRequestMethod("HEAD");
            return (con.getResponseCode() == HttpURLConnection.HTTP_OK);
        }
        catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

}

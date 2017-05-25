package com.fyusuf.tvrehberi;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class DetailActivity extends AppCompatActivity {
    private RecyclerView recycler_view;
    private List<Program> program_list;
    private List<String> sp_list; //list of selected program
    private ProgressDialog progressDialog;
    private Gson gson;
    private SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    private static String URL;
    private Bitmap bitmapKanalLogo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        recycler_view = (RecyclerView)findViewById(R.id.recycler_view);
        Intent intent = getIntent();
        URL = intent.getStringExtra("url");
        bitmapKanalLogo = intent.getParcelableExtra("logo");
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        layoutManager.scrollToPosition(0);
        recycler_view.setLayoutManager(layoutManager);
        gson = new Gson();
        program_list = new ArrayList<>();
        sp_list = new ArrayList<>();
        sharedPreferences = this.getSharedPreferences("SelectFile", MODE_PRIVATE);
        editor = sharedPreferences.edit();
        String json = sharedPreferences.getString("program", null);
        Type type = new TypeToken<ArrayList<String>>() {}.getType();
        ArrayList<String> arrayList = gson.fromJson(json, type);
        if(arrayList !=null) {
            for (int i = 0; i < arrayList.size(); i++) {
                sp_list.add(arrayList.get(i));
            }
        }

        final DetailRecyclerAdapter adapter_items = new DetailRecyclerAdapter(program_list);
        recycler_view.setHasFixedSize(true);
        recycler_view.setAdapter(adapter_items);
        recycler_view.setItemAnimator(new DefaultItemAnimator());
        recycler_view.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(), recycler_view, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {

            }

            @Override
            public void onLongClick(View view, int position) {
                ImageView starView = (ImageView) view.findViewById(R.id.star_icon);
                Program program = program_list.get(position);
                Intent intent = new Intent(getBaseContext(), AlarmReceiver.class);
                intent.putExtra("programName", program.getTxt());
                intent.putExtra("programTime",program.getTime());
                intent.putExtra("programImg", program.getImage());
                PendingIntent pendingIntent = PendingIntent.getBroadcast(getBaseContext(), program.getTxt().hashCode(), intent, 0);
                Log.i("hashcode", String.valueOf(program.getTxt().hashCode()));
                AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                String time = program.getTime();
                Log.i("time: ", time);
                String[] token = time.split(":");
                int hourOfDay = Integer.valueOf(token[0]);
                int minute = Integer.valueOf(token[1]);
                Log.i("hour: ", String.valueOf(hourOfDay));
                Log.i("minute: ", String.valueOf(minute));
                int programTime = Integer.parseInt(token[0] + token[1]);
                Log.i("minute: ", token[0] + token[1]);
                Calendar calendar = Calendar.getInstance();
                SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
                String formatTime = sdf.format(calendar.getTime());
                String[] token1 = formatTime.split(":");
                int currentTime = Integer.parseInt(token1[0] + token1[1]);
                calendar.setTimeInMillis(System.currentTimeMillis());
                if (programTime > currentTime) {
                    if (starView.getTag().equals("grey")) {
                        Toast.makeText(getApplicationContext(), program.getTxt() + " seçildi", Toast.LENGTH_SHORT).show();
                        sp_list.add(program.getTime()+program.getTxt());
                        Log.i("size",String.valueOf(sp_list.size()));
                        String json = gson.toJson(sp_list);
                        editor.putString("program", json);
                        editor.apply();
                        starView.setImageResource(android.R.drawable.btn_star_big_on);
                        starView.setTag("yellow");
                        adapter_items.notifyItemChanged(position);
                        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                        calendar.set(Calendar.MINUTE, minute);
                        calendar.set(Calendar.SECOND, 0);
                        calendar.set(Calendar.MILLISECOND, 0);
                        alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
                    } else {
                        Toast.makeText(getApplicationContext(), program.getTxt() + " silindi", Toast.LENGTH_SHORT).show();
                        sp_list.remove(program.getTime()+program.getTxt());
                        Log.i("size", String.valueOf(sp_list.size()));
                        String json = gson.toJson(sp_list);
                        editor.putString("program", json);
                        editor.apply();
                        starView.setTag("grey");
                        starView.setImageResource(android.R.drawable.btn_star_big_off);
                        adapter_items.notifyItemChanged(position);
                        alarmManager.cancel(pendingIntent);

                    }

                } else {
                    Toast.makeText(getApplicationContext(), "Bu program bitti veya devam ediyor.", Toast.LENGTH_SHORT).show();
                }

            }
        }));
        new ProgramClass().execute();
    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    private class ProgramClass extends AsyncTask<Void, Void, Void> {
        Bitmap bitmapFilmLogo,bitmapFilmImg;
        ArrayList<String> myList = new ArrayList<>();
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(DetailActivity.this);
            progressDialog.setTitle("TVRehberi");
            progressDialog.setMessage("Veri Çekiliyor...");
            progressDialog.setIndeterminate(false);
            progressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            try{
                Document doc  = Jsoup.connect(URL).get();
                //film parse
                Element film = doc.select("div[class=TVProgram FL]").get(0);
                Elements filmTxt = film.select("div.txt");
                Elements filmTime = film.select("div.time");
                Elements filmImg = film.select("div[class=TVProgram FL] img[src]");
                Elements filmLogo = film.select("div[class=Logo] img[src]");
                for(int i = 0; i < filmImg.size(); i+=3){
                    myList.add(filmImg.get(i).getElementsByTag("img").attr("src"));
                }
                for(int i = 0; i < filmTxt.size(); i++){
                    InputStream input = new java.net.URL(filmLogo.get(i).getElementsByTag("img").attr("src")).openStream();
                    if(input != null){
                        bitmapFilmLogo = BitmapFactory.decodeStream(input);
                    }
                    input = new java.net.URL(myList.get(i)).openStream();
                    if( input != null){
                        bitmapFilmImg = BitmapFactory.decodeStream(input);
                    }
                    program_list.add(new Program(filmTxt.get(i).text(),filmTime.get(i).text(),bitmapFilmImg,bitmapFilmLogo));
                }

            }catch (Exception e){
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            DetailRecyclerAdapter adapter_items = new DetailRecyclerAdapter(program_list);
            recycler_view.setHasFixedSize(true);
            recycler_view.setAdapter(adapter_items);
            recycler_view.setItemAnimator(new DefaultItemAnimator());
            progressDialog.dismiss();
        }
    }

}

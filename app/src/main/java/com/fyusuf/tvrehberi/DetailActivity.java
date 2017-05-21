package com.fyusuf.tvrehberi;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class DetailActivity extends AppCompatActivity {
	private RecyclerView recycler_view;
    private List<Program> program_list;
    private ProgressDialog progressDialog;
    private final static int REQUEST_CODE = 1;
    private static String URL;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
		recycler_view = (RecyclerView)findViewById(R.id.recycler_view);
        Intent intent = getIntent();
        URL = intent.getStringExtra("url");
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        layoutManager.scrollToPosition(0);
 
        recycler_view.setLayoutManager(layoutManager);
 
        program_list = new ArrayList<>();
 
       /* program_list.add(new Program("program1","time1",R.drawable.film1, R.drawable.logoatv));
        program_list.add(new Program("program2","time2",R.drawable.film1, R.drawable.logo));
        program_list.add(new Program("program3","time3",R.drawable.film1, R.drawable.logoatv));
        program_list.add(new Program("program4","time4",R.drawable.film1, R.drawable.logo));*/
        SimpleRecyclerAdapter adapter_items = new SimpleRecyclerAdapter(program_list);
        recycler_view.setHasFixedSize(true);
        recycler_view.setAdapter(adapter_items);
        recycler_view.setItemAnimator(new DefaultItemAnimator());
        recycler_view.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(), recycler_view, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                Program program = program_list.get(position);
                Toast.makeText(getApplicationContext(), program.getTxt() + " is selected!", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getBaseContext(), AlarmReceiver.class);
                intent.putExtra("programName", program.getTxt());
                intent.putExtra("programImg", program.getImage());
                PendingIntent pendingIntent = PendingIntent.getBroadcast(getBaseContext(), REQUEST_CODE, intent, 0);
                AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                String time = program.getTime();
                Log.i("time: ", time);
                String[] token = time.split(":");
                int hourOfDay = Integer.valueOf(token[0]);
                int minute = Integer.valueOf(token[1]);
                int programTime = Integer.parseInt(token[0] + token[1]);
                Calendar calendar = Calendar.getInstance();
                SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
                String formatTime = sdf.format(calendar.getTime());
                String[] token1 = formatTime.split(":");
                int currentTime = Integer.parseInt(token1[0] + token1[1]);
                calendar.setTimeInMillis(System.currentTimeMillis());
                if (programTime > currentTime) {
                    calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                    calendar.set(Calendar.MINUTE, minute);
                    calendar.set(Calendar.SECOND, 0);
                    calendar.set(Calendar.MILLISECOND, 0);
                /*if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
                } else {
                    alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
                }*/
                    alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
                } else {
                    Toast.makeText(getApplicationContext(),"Program devam ediyor...",Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));
        new Film().execute();
    }

    private class Film extends AsyncTask<Void, Void, Void> {
        Bitmap bitmapFilmLogo,bitmapFilmImg;
        ArrayList<String> myList = new ArrayList<>();
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(DetailActivity.this);
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
            SimpleRecyclerAdapter adapter_items = new SimpleRecyclerAdapter(program_list);
            recycler_view.setHasFixedSize(true);
            recycler_view.setAdapter(adapter_items);
            recycler_view.setItemAnimator(new DefaultItemAnimator());
            progressDialog.dismiss();
        }
    }
}

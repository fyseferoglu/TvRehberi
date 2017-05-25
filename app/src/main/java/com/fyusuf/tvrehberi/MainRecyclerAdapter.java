package com.fyusuf.tvrehberi;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Pc on 23.5.2017.
 */
public class MainRecyclerAdapter extends RecyclerView.Adapter<MainRecyclerAdapter.ViewHolder> {

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView program_name;
        public ImageView program_logo;
        public TextView program_time;
        public ImageView program_image;
        public CardView card_view;


        public ViewHolder(View view) {
            super(view);
            card_view = (CardView)view.findViewById(R.id.card_view2);
            program_name = (TextView)view.findViewById(R.id.program_name);
            program_time = (TextView)view.findViewById(R.id.program_time);
            program_image = (ImageView)view.findViewById(R.id.program_image);
            program_logo = (ImageView)view.findViewById(R.id.program_logo);

        }
    }

    List<Program> list_kanal;
    public MainRecyclerAdapter(List<Program> list_kanal) {

        this.list_kanal = list_kanal;
    }


    @Override
    public MainRecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_view_layout_main, parent, false);

        ViewHolder view_holder = new ViewHolder(v);
        return view_holder;
    }


    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        holder.program_name.setText(list_kanal.get(position).getTxt());
        holder.program_time.setText(list_kanal.get(position).getTime());
        holder.program_image.setImageBitmap(list_kanal.get(position).getImage());
        holder.program_logo.setImageBitmap(list_kanal.get(position).getLogo());

    }

    @Override
    public int getItemCount() {
        return list_kanal.size();
    }
    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }


}

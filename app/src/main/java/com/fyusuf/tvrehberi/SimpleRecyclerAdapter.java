package com.fyusuf.tvrehberi;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;


public class SimpleRecyclerAdapter extends RecyclerView.Adapter<SimpleRecyclerAdapter.ViewHolder> {

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView pTxt;
        public TextView pTime;
        public ImageView pImage;
        public ImageView pLogo;
        public CardView card_view;


        public ViewHolder(View view) {
            super(view);

            card_view = (CardView)view.findViewById(R.id.card_view);
            pTxt = (TextView)view.findViewById(R.id.programTxt);
            pTime = (TextView)view.findViewById(R.id.programTime);
            pImage = (ImageView)view.findViewById(R.id.programImage);
            pLogo = (ImageView)view.findViewById(R.id.programLogo);

        }
    }

    List<Program> list_program;
    public SimpleRecyclerAdapter(List<Program> list_program) {

        this.list_program = list_program;
    }


    @Override
    public SimpleRecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_view_layout, parent, false);

        ViewHolder view_holder = new ViewHolder(v);
        return view_holder;
    }


    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        holder.pTxt.setText(list_program.get(position).getTxt());
        holder.pTime.setText(list_program.get(position).getTime());
        holder.pImage.setImageBitmap(list_program.get(position).getImage());
        holder.pLogo.setImageBitmap(list_program.get(position).getLogo());

    }

    @Override
    public int getItemCount() {
        return list_program.size();
    }
    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }


}
package com.dolbik.pavel.translater.adapters;


import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.dolbik.pavel.translater.R;
import com.dolbik.pavel.translater.model.History;

import java.util.ArrayList;
import java.util.List;

public class HistoryAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {


    public interface OnItemClickListener {
        void itemClick(History history);
        void favoriteChange(History history, int position);
    }

    private OnItemClickListener onItemClickListener;
    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }


    /** Коллекция содержит списов объектов History.
     *  Collections contains the list of History objects. */
    private ArrayList<History> items = new ArrayList<>();


    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final ImageView favorite;
        private final TextView  text;
        private final TextView  translate;
        private final TextView  direction;

        public ViewHolder(View itemView) {
            super(itemView);
            favorite  = (ImageView) itemView.findViewById(R.id.favorite);
            text      = (TextView)  itemView.findViewById(R.id.text);
            translate = (TextView)  itemView.findViewById(R.id.translate);
            direction = (TextView)  itemView.findViewById(R.id.direction);
        }
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_history, parent, false);
        RecyclerView.ViewHolder holder =  new ViewHolder(view);

        view.setOnClickListener(v -> {
            int adapterPosition = holder.getAdapterPosition();
            if (adapterPosition != RecyclerView.NO_POSITION) {
                if (onItemClickListener != null) {
                    onItemClickListener.itemClick(items.get(adapterPosition));
                }
            }
        });

        view.findViewById(R.id.favorite).setOnClickListener(v -> {
            int adapterPosition = holder.getAdapterPosition();
            if (adapterPosition != RecyclerView.NO_POSITION) {
                if (onItemClickListener != null) {
                    onItemClickListener.favoriteChange(items.get(adapterPosition), adapterPosition);
                }
            }
        });

        return holder;
    }


    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        History history = items.get(position);
        ViewHolder holder = (ViewHolder) viewHolder;
        holder.text.setText(history.getText());
        holder.translate.setText(history.getTranslate());
        holder.direction.setText(history.getDirection());

        if (history.isFavorite()) {
            holder.favorite.setImageDrawable(ContextCompat.getDrawable(holder.favorite.getContext(), R.drawable.ic_bookmark_yellow));
        } else {
            holder.favorite.setImageDrawable(ContextCompat.getDrawable(holder.favorite.getContext(), R.drawable.ic_bookmark_grey));
        }
    }


    @Override
    public int getItemCount() {
        return items.size();
    }


    public void setData(List<History> data) {
        items.clear();
        items.addAll(data);
        notifyDataSetChanged();
    }


    public void removeItemByPosition(int position) {
        items.remove(position);
        notifyItemRemoved(position);
    }

}

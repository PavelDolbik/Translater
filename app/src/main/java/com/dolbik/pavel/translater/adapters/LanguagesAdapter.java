package com.dolbik.pavel.translater.adapters;

import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.dolbik.pavel.translater.R;
import com.dolbik.pavel.translater.model.Language;

import java.util.ArrayList;
import java.util.List;


public class LanguagesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {


    public interface OnItemClickListener {
        void itemClick(Language language);
    }

    private OnItemClickListener onItemClickListener;
    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }


    /** Коллекция содержит списов объектов Language.
     *  Collections contains the list of Language objects. */
    private ArrayList<Language> items = new ArrayList<>();


    /** Текущий выбранный код языка. <br>
     *  The currently selected language code. */
    private String code;


    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView language;

        public ViewHolder(View itemView) {
            super(itemView);
            language = (TextView) itemView.findViewById(R.id.language);
        }
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_language, parent, false);
        RecyclerView.ViewHolder holder =  new ViewHolder(view);
        view.setOnClickListener(v -> {
            int adapterPosition = holder.getAdapterPosition();
            if (adapterPosition != RecyclerView.NO_POSITION) {
                if (onItemClickListener != null) {
                    onItemClickListener.itemClick(items.get(adapterPosition));
                }
            }
        });
        return holder;
    }


    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        final Language language = items.get(position);
        ViewHolder holder  = (ViewHolder) viewHolder;
        holder.language.setText(language.getName());
        markAsSelected(holder.language, language.getCode());
    }


    @Override
    public int getItemCount() {
        return items.size();
    }


    public void setData(List<Language> data) {
        items.clear();
        items.addAll(data);
        notifyDataSetChanged();
    }


    public void setSelectedCode(String code) {
        this.code = code;
    }


    private void markAsSelected(TextView textView, String itemCode) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            textView.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, itemCode.equals(code) ? R.drawable.check : 0, 0);
        } else {
            textView.setCompoundDrawablesWithIntrinsicBounds(0, 0, itemCode.equals(code) ? R.drawable.check : 0, 0);
        }
    }

}

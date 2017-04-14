package com.dolbik.pavel.translater.fragments.history;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.arellomobile.mvp.MvpAppCompatFragment;
import com.arellomobile.mvp.presenter.InjectPresenter;
import com.dolbik.pavel.translater.R;
import com.dolbik.pavel.translater.adapters.HistoryAdapter;
import com.dolbik.pavel.translater.events.HistoryEvent;
import com.dolbik.pavel.translater.model.History;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

public class HistoryFragment extends MvpAppCompatFragment
        implements
        HistoryView,
        HistoryAdapter.OnItemClickListener {


    @InjectPresenter  HistoryPresenter presenter;

    private CoordinatorLayout coordinatorLayout;
    private ProgressBar       progressBar;
    private HistoryAdapter    adapter;
    private TextView          empty;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_history, container, false);

        coordinatorLayout = (CoordinatorLayout) view.findViewById(R.id.coordinatorLayout);
        progressBar       = (ProgressBar)       view.findViewById(R.id.progressBar);
        empty             = (TextView)          view.findViewById(R.id.empty);

        RecyclerView list = (RecyclerView) view.findViewById(R.id.list);
        list.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new HistoryAdapter();
        adapter.setOnItemClickListener(this);
        list.setAdapter(adapter);
        DividerItemDecoration itemDecoration = new DividerItemDecoration(getContext(), LinearLayout.VERTICAL);
        itemDecoration.setDrawable(ContextCompat.getDrawable(getContext(), R.drawable.divider_items));
        list.addItemDecoration(itemDecoration);
        list.setHasFixedSize(true);

        return view;
    }


    @Override
    public void showHideProgress(boolean flag) {
        progressBar.setVisibility(flag ? View.VISIBLE : View.GONE);
    }


    @Override
    public void showSnakeBar(String message) {
        Snackbar snackbar = Snackbar.make(coordinatorLayout, message, Snackbar.LENGTH_LONG);
        snackbar.show();
    }


    @Override
    public void setData(List<History> data) {
        adapter.setData(data);
    }


    @Override
    public void showHideEmpty(boolean flag) {
        empty.setVisibility(flag ? View.VISIBLE : View.GONE);
    }


    @Override
    public void itemClick(History history) {
        //Отлавливается в TranslatePresenter. (Catch in TranslatePresenter.)
        EventBus.getDefault().postSticky(new HistoryEvent.Click(history));
        getActivity().onBackPressed();
    }


    @Override
    public void favoriteChange(History history) {
        Log.d("Pasha", "favoriteChange "+history.getText());
    }

}

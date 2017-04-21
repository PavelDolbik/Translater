package com.dolbik.pavel.translater.activity.changelang;


import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.arellomobile.mvp.MvpAppCompatActivity;
import com.arellomobile.mvp.presenter.InjectPresenter;
import com.dolbik.pavel.translater.R;
import com.dolbik.pavel.translater.adapters.LanguagesAdapter;
import com.dolbik.pavel.translater.model.Language;
import com.dolbik.pavel.translater.utils.Constants;

import java.util.List;

public class ChangeLanguageActivity extends MvpAppCompatActivity
        implements
        ChangeLanguageView,
        LanguagesAdapter.OnItemClickListener {


    @InjectPresenter ChangeLanguagePresenter presenter;

    private CoordinatorLayout coordinatorLayout;
    private ProgressBar       progressBar;
    private LanguagesAdapter  adapter;
    private String            fromCode;
    private String            toCode;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_language);

        fromCode = getIntent().getStringExtra(Constants.DIRC_FROM_CODE);
        toCode   = getIntent().getStringExtra(Constants.DIRC_TO_CODE);

        initToolbar();
        initView();
    }


    private void initToolbar() {
        String title = getResources().getString(R.string.cla_title);
        presenter.setDirection(1);
        if (!TextUtils.isEmpty(toCode)) {
            title = getResources().getString(R.string.cla_translate_title);
            presenter.setDirection(2);
        }
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(title);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> onBackPressed());
    }


    private void initView() {
        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinatorLayout);
        progressBar       = (ProgressBar)       findViewById(R.id.progressBar);

        RecyclerView list = (RecyclerView) findViewById(R.id.list);
        list.setLayoutManager(new LinearLayoutManager(this));
        adapter = new LanguagesAdapter();
        adapter.setOnItemClickListener(this);
        list.setAdapter(adapter);
        DividerItemDecoration itemDecoration = new DividerItemDecoration(this, LinearLayout.VERTICAL);
        itemDecoration.setDrawable(ContextCompat.getDrawable(this, R.drawable.divider_items));
        list.addItemDecoration(itemDecoration);
        list.setHasFixedSize(true);
    }


    @Override
    public void showSnakeBar(String message) {
        Snackbar snackbar = Snackbar.make(coordinatorLayout, message, Snackbar.LENGTH_LONG);
        snackbar.show();
    }


    @Override
    public void hideProgress() {
        progressBar.setVisibility(View.GONE);
    }


    @Override
    public void setData(List<Language> data) {
        adapter.setData(data);
        adapter.setSelectedCode(TextUtils.isEmpty(fromCode) ? toCode : fromCode);
    }


    @Override
    public void itemClick(Language language) {
        presenter.changeLanguage(language);
        finish();
    }

}

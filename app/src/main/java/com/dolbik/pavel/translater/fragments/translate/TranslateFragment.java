package com.dolbik.pavel.translater.fragments.translate;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.transition.TransitionManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.arellomobile.mvp.MvpAppCompatFragment;
import com.arellomobile.mvp.presenter.InjectPresenter;
import com.dolbik.pavel.translater.R;

public class TranslateFragment
        extends MvpAppCompatFragment
        implements TranslateView {


    @InjectPresenter TranslatePresenter presenter;

    private ProgressBar  progressBar;
    private LinearLayout toolbarView;
    private TextView     fromDirection;
    private TextView     toDirection;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_translate, container, false);


        Toolbar toolbar = (Toolbar) view.findViewById(R.id.toolbar);
        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);
        progressBar   = (ProgressBar)  toolbar.findViewById(R.id.progressBar);
        toolbarView   = (LinearLayout) toolbar.findViewById(R.id.toolbar_view);
        fromDirection = (TextView)     toolbar.findViewById(R.id.from);
        toDirection   = (TextView)     toolbar.findViewById(R.id.to);

        return view;
    }


    @Override
    public void showToolbarView() {
        TransitionManager.beginDelayedTransition(toolbarView);
        progressBar.setVisibility(View.GONE);
        toolbarView.setVisibility(View.VISIBLE);
    }


    @Override
    public void updateTranslateDirection(String from, String to) {
        TransitionManager.beginDelayedTransition(toolbarView);
        fromDirection.setText(from);
        toDirection.setText(to);
    }

}

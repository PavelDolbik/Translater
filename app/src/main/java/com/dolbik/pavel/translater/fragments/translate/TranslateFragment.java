package com.dolbik.pavel.translater.fragments.translate;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.transition.TransitionManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.arellomobile.mvp.MvpAppCompatFragment;
import com.arellomobile.mvp.presenter.InjectPresenter;
import com.dolbik.pavel.translater.R;

public class TranslateFragment
        extends MvpAppCompatFragment
        implements TranslateView, View.OnClickListener {


    @InjectPresenter TranslatePresenter presenter;

    private ProgressBar       progressBar;
    private LinearLayout      toolbarView;
    private TextView          fromDirection;
    private TextView          toDirection;
    private ImageView         swap;
    private CoordinatorLayout coordinatorLayout;
    private RelativeLayout    translateContainer;
    private EditText          translate;

    private long lastClickFrom = 0L;
    private long lastClickTo   = 0L;
    private long lastClickSwap = 0L;


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
        swap          = (ImageView)    toolbar.findViewById(R.id.swap);

        fromDirection.setOnClickListener(this);
        toDirection.setOnClickListener(this);
        swap.setOnClickListener(this);

        coordinatorLayout  = (CoordinatorLayout) view.findViewById(R.id.coordinatorLayout);
        translateContainer = (RelativeLayout)    view.findViewById(R.id.translate_container);
        translate          = (EditText)          view.findViewById(R.id.translate);
        translate.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    translateContainer.setBackground(ContextCompat.getDrawable(
                            getContext(), R.drawable.translate_container_focus_shape));
                } else {
                    translateContainer.setBackground(ContextCompat.getDrawable(
                            getContext(), R.drawable.translate_container_shape));
                }
            }
        });

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


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.from:
                if ((System.currentTimeMillis() - lastClickFrom) < 1000) { break; }
                lastClickFrom = System.currentTimeMillis();
                Log.d("Pasha", "from");
                break;
            case R.id.to:
                if ((System.currentTimeMillis() - lastClickTo) < 1000) { break; }
                lastClickTo = System.currentTimeMillis();
                Log.d("Pasha", "to");
                break;
            case R.id.swap:
                if ((System.currentTimeMillis() - lastClickSwap) < 1000) { break; }
                lastClickSwap = System.currentTimeMillis();
                Log.d("Pasha", "swap");
                break;
        }
    }


    @Override
    public void showSnakeBar(String message) {
        Snackbar snackbar = Snackbar.make(coordinatorLayout, message, Snackbar.LENGTH_LONG);
        snackbar.show();
    }

}

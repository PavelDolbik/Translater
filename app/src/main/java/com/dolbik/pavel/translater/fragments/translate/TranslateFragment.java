package com.dolbik.pavel.translater.fragments.translate;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.transition.TransitionManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
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
import com.dolbik.pavel.translater.activity.changelang.ChangeLanguage;
import com.dolbik.pavel.translater.utils.Constants;
import com.jakewharton.rxbinding.widget.RxTextView;

import java.util.concurrent.TimeUnit;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;

public class TranslateFragment
        extends MvpAppCompatFragment
        implements TranslateView, View.OnClickListener {


    @InjectPresenter TranslatePresenter presenter;

    private ProgressBar       progressBar;
    private LinearLayout      toolbarView;
    private TextView          fromDirection;
    private TextView          toDirection;
    private CoordinatorLayout coordinatorLayout;
    private RelativeLayout    translateContainer;
    private EditText          translate;
    private ImageView         clear;
    private TextView          license;

    private long lastClickFrom  = 0L;
    private long lastClickTo    = 0L;
    private long lastClickSwap  = 0L;
    private long lastClickClear = 0L;

    private ImageView   favorite;
    private ProgressBar resultPrg;
    private TextView    resultTranslate;
    private TextView    resultError;

    private Subscription translateSbs;



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_translate, container, false);

        Toolbar toolbar = (Toolbar) view.findViewById(R.id.toolbar);
        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);
        progressBar    = (ProgressBar)  toolbar.findViewById(R.id.progressBar);
        toolbarView    = (LinearLayout) toolbar.findViewById(R.id.toolbar_view);
        fromDirection  = (TextView)     toolbar.findViewById(R.id.from);
        toDirection    = (TextView)     toolbar.findViewById(R.id.to);
        ImageView swap = (ImageView)    toolbar.findViewById(R.id.swap);

        fromDirection.setOnClickListener(this);
        toDirection.setOnClickListener(this);
        swap.setOnClickListener(this);

        coordinatorLayout  = (CoordinatorLayout) view.findViewById(R.id.coordinatorLayout);
        translateContainer = (RelativeLayout)    view.findViewById(R.id.translate_container);
        translate          = (EditText)          view.findViewById(R.id.translate);

        translate.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                translateContainer.setBackground(ContextCompat.getDrawable(
                        getContext(), R.drawable.translate_container_focus_shape));
            } else {
                translateContainer.setBackground(ContextCompat.getDrawable(
                        getContext(), R.drawable.translate_container_shape));
            }
        });

        translateSbs = RxTextView.textChanges(translate)
                .debounce(1000, TimeUnit.MILLISECONDS)
                .map(charSequence -> charSequence.toString().replaceAll("[\r\n]+", "\r").trim())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(s -> presenter.translateText(s, false));


        license = (TextView)  view.findViewById(R.id.license);
        clear   = (ImageView) view.findViewById(R.id.clear);
        clear.setOnClickListener(this);

        resultPrg       = (ProgressBar) view.findViewById(R.id.resultPrg);
        resultTranslate = (TextView)    view.findViewById(R.id.resultTranslate);
        resultError     = (TextView)    view.findViewById(R.id.resultError);
        favorite        = (ImageView)   view.findViewById(R.id.favorite);

        return view;
    }


    @Override
    public void showToolbarView() {
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
    public void showSnakeBar(String message) {
        Snackbar snackbar = Snackbar.make(coordinatorLayout, message, Snackbar.LENGTH_LONG);
        snackbar.show();
    }


    @Override
    public void showCleanBtn() {
        if (clear.getVisibility() != View.VISIBLE) {
            TransitionManager.beginDelayedTransition(coordinatorLayout);
            clear.setVisibility(View.VISIBLE);
            license.setVisibility(View.VISIBLE);

        }
    }


    @Override
    public void hideCleanBtn() {
        TransitionManager.beginDelayedTransition(coordinatorLayout);
        clear.setVisibility(View.GONE);
        license.setVisibility(View.GONE);
        translate.getText().clear();
    }


    @Override
    public void setTextForTranslate(String text) {
        translate.setText(text);
    }


    @Override
    public void showHideFavoriteBtn(boolean flag, boolean isFavorite) {
        if (flag) {
            if (isFavorite) {
                favorite.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.ic_bookmark_yellow));
            } else {
                favorite.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.ic_bookmark_grey));
            }
            favorite.setVisibility(View.VISIBLE);
        } else {
            favorite.setVisibility(View.GONE);
        }
    }


    @Override
    public void showViewStub(@TranslateFragmentState.State int state, String translate) {
        switch (state) {
            case TranslateFragmentState.IDLE:
                TransitionManager.beginDelayedTransition(coordinatorLayout);
                resultPrg.setVisibility(View.GONE);
                resultTranslate.setVisibility(View.GONE);
                resultError.setVisibility(View.GONE);
                break;
            case TranslateFragmentState.SHOW_PROGRESS:
                resultPrg.setVisibility(View.VISIBLE);
                resultTranslate.setVisibility(View.GONE);
                resultError.setVisibility(View.GONE);
                break;
            case TranslateFragmentState.SHOW_TRANSLATE:
                resultTranslate.setText(translate);
                resultTranslate.setVisibility(View.VISIBLE);
                resultPrg.setVisibility(View.GONE);
                resultError.setVisibility(View.GONE);
                break;
            case TranslateFragmentState.SHOW_ERROR:
                resultError.setVisibility(View.VISIBLE);
                resultPrg.setVisibility(View.GONE);
                resultTranslate.setVisibility(View.GONE);
                break;
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.from:
                if ((System.currentTimeMillis() - lastClickFrom) < 1000) { break; }
                lastClickFrom = System.currentTimeMillis();
                changeFromLang();
                break;
            case R.id.to:
                if ((System.currentTimeMillis() - lastClickTo) < 1000) { break; }
                lastClickTo = System.currentTimeMillis();
                changeToLang();
                break;
            case R.id.swap:
                if ((System.currentTimeMillis() - lastClickSwap) < 1000) { break; }
                lastClickSwap = System.currentTimeMillis();
                presenter.swap();
                break;
            case R.id.clear:
                if ((System.currentTimeMillis() - lastClickClear) < 1000) { break; }
                lastClickClear = System.currentTimeMillis();
                presenter.clear();
                break;
        }
    }


    private void changeFromLang() {
        String fromCode = presenter.getLanguagePair().first.getCode();
        Intent intent = new Intent(getActivity(), ChangeLanguage.class);
        intent.putExtra(Constants.DIRC_FROM_CODE, fromCode);
        getActivity().startActivity(intent);
    }


    private void changeToLang() {
        String toCode = presenter.getLanguagePair().second.getCode();
        Intent intent = new Intent(getActivity(), ChangeLanguage.class);
        intent.putExtra(Constants.DIRC_TO_CODE, toCode);
        getActivity().startActivity(intent);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if (translateSbs != null) {
            translateSbs.unsubscribe();
            translateSbs = null;
        }
    }

}

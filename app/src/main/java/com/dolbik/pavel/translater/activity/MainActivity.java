package com.dolbik.pavel.translater.activity;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatDelegate;
import android.view.MenuItem;
import android.view.WindowManager;

import com.arellomobile.mvp.MvpAppCompatActivity;
import com.arellomobile.mvp.presenter.InjectPresenter;
import com.dolbik.pavel.translater.BuildConfig;
import com.dolbik.pavel.translater.R;
import com.dolbik.pavel.translater.TApplication;
import com.dolbik.pavel.translater.fragments.note.NoteFragment;
import com.dolbik.pavel.translater.fragments.translate.TranslateFragment;

public class MainActivity
        extends MvpAppCompatActivity
        implements MainActivityView {


    static {
        // Для использования vector на pre lollipop. (To use a vector to pre lollipop.)
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }


    @InjectPresenter MainActivityPresenter presenter;
    private BottomNavigationView navigation;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING);

        navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(item -> {
            selectFragment(item.getItemId());
            return true;
        });
    }


    @Override
    public void selectFragment(int menuItemId) {
        Fragment fragment = null;

        switch (menuItemId) {
            case R.id.navigation_translate:
                fragment = new TranslateFragment();
                break;
            case R.id.navigation_note:
                fragment = new NoteFragment();
                break;
        }

        if (fragment != null) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.content, fragment, fragment.getTag());
            transaction.commit();
        }
    }


    @Override
    public void onBackPressed() {
        MenuItem item = navigation.getMenu().getItem(0);
        if (item.getItemId() != navigation.getSelectedItemId()) {
            navigation.setSelectedItemId(item.getItemId());
        } else {
            super.onBackPressed();
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (BuildConfig.DEBUG) {
            TApplication.getRefWatcher(this).watch(this);
        }
    }

}

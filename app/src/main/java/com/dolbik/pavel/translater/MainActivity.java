package com.dolbik.pavel.translater;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.dolbik.pavel.translater.fragments.note.NoteFragment;
import com.dolbik.pavel.translater.fragments.translate.TranslateFragment;

public class MainActivity extends AppCompatActivity {

    static {
        // Для использования vector на pre lollipop. (To use a vector to pre lollipop.)
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }


    private BottomNavigationView navigation;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                selectFragment(item);
                return true;
            }
        });
    }


    private void selectFragment(MenuItem item) {
        Fragment fragment = null;

        switch (item.getItemId()) {
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

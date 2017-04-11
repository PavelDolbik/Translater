package com.dolbik.pavel.translater.fragments.translate;


import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class TranslateFragmentState {

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({IDLE, SHOW_PROGRESS, SHOW_TRANSLATE, SHOW_ERROR})
    public @interface State {}
    public static final int IDLE           = 0;
    public static final int SHOW_PROGRESS  = 1;
    public static final int SHOW_TRANSLATE = 2;
    public static final int SHOW_ERROR     = 3;
}

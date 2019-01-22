package com.jeffrey.studio.jeffeystudio.common.behavior;

import android.content.Context;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

/**
 * Copyright (C), 2019, TP-LINK TECHNOLOGIES CO., LTD.
 *
 * @author caizhenghe
 * @ClassName: FooterDependBehavior
 * @Description: Version 1.0.0, 2019-01-22, caizhenghe create file.
 */

public class FooterDependBehavior extends CoordinatorLayout.Behavior<View> {
    public static final String TAG = FooterDependBehavior.class.getSimpleName();

    public FooterDependBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    //当 dependency instanceof AppBarLayout 返回TRUE，将会调用onDependentViewChanged（）方法
    @Override
    public boolean layoutDependsOn(CoordinatorLayout parent, View child, View dependency) {
        return dependency instanceof AppBarLayout;
    }

    @Override
    public boolean onDependentViewChanged(CoordinatorLayout parent, View child, View dependency) {
        //根据dependency top值的变化改变 child 的 translationY
        float translationY = Math.abs(dependency.getBottom());
        child.setTranslationY(translationY);
        Log.i(TAG, "onDependentViewChanged: dependency.top = " + dependency.getBottom() + "; translationY = " + translationY);
        return true;

    }
}

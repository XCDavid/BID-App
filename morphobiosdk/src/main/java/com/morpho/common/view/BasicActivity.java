package com.morpho.common.view;

/**
 * Created by J. Alfredo Hernández Alarcón on 01/06/17.
 * Basic Activity
 */

public interface BasicActivity {
    /**
     * Called just after setContentView.
     * Override this method to configure your activity or initialize views
     */
    void onPrepareActivity();

    /**
     * Called before to initialize all the presenter instances linked to the component lifecycle.
     * Override this method to configure your presenter with extra data if needed.
     */
    void onPreparePresenter();
}

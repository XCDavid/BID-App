package com.teknei.bid1;

/**
 * Created by Desarrollo on 10/07/2017.
 */

public interface BaseAction {
    void logOut();
    void goNext();
    void sendPetition();
    void cancelOperation();
    void goStep(int flowStep);
    void goNextTwo();
}

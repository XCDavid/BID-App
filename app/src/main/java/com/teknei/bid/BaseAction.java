package com.teknei.bid;

/**
 * Created by Desarrollo on 10/07/2017.
 */

public interface BaseAction {
    void logOut();
    void goNext();
    void sendPetition();
    void cancelOperation();
    void goStep(int flowStep);
}

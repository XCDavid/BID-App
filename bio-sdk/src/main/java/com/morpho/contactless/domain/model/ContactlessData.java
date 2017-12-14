package com.morpho.contactless.domain.model;

/**
 * Data Buffer
 * Created by Alfredo on 31/07/17.
 */
public class ContactlessData {

    // Instance
    private static final ContactlessData ourInstance = new ContactlessData();

    // Constructor
    public static ContactlessData getInstance() {
        return ourInstance;
    }

    private ContactlessData() {
        leftHand = new Hand();
        rightHand = new Hand();
    }

    // Left Hand
    private Hand leftHand;
    private Hand rightHand;

    private boolean isLeftHand;

    public Hand getLeftHand() {
        return leftHand;
    }

    public void setLeftHand(Hand leftHand) {
        this.leftHand = leftHand;
    }

    public Hand getRightHand() {
        return rightHand;
    }

    public void setRightHand(Hand rightHand) {
        this.rightHand = rightHand;
    }

    public boolean isLeftHand() {
        return isLeftHand;
    }

    public void setLeftHand(boolean leftHand) {
        isLeftHand = leftHand;
    }
}

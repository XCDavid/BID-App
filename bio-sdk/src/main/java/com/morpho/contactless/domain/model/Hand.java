package com.morpho.contactless.domain.model;

/**
 * Created by alfredo on 31/07/17.
 */

public class Hand {

    // WSQ Images
    private byte[] thumb;
    private byte[] index;
    private byte[] middle;
    private byte[] ring;
    private byte[] little;

    // JPG Images
    private byte[] thumbJPG;
    private byte[] indexJPG;
    private byte[] middleJPG;
    private byte[] ringJPG;
    private byte[] littleJPG;


    public Hand() {
    }

    public Hand(byte[] thumb, byte[] index, byte[] middle, byte[] ring, byte[] little) {
        this.thumb = thumb;
        this.index = index;
        this.middle = middle;
        this.ring = ring;
        this.little = little;
    }

    public byte[] getThumb() {
        return thumb;
    }

    public void setThumb(byte[] thumb) {
        this.thumb = thumb;
    }

    public byte[] getIndex() {
        return index;
    }

    public void setIndex(byte[] index) {
        this.index = index;
    }

    public byte[] getMiddle() {
        return middle;
    }

    public void setMiddle(byte[] middle) {
        this.middle = middle;
    }

    public byte[] getRing() {
        return ring;
    }

    public void setRing(byte[] ring) {
        this.ring = ring;
    }

    public byte[] getLittle() {
        return little;
    }

    public void setLittle(byte[] little) {
        this.little = little;
    }

    public byte[] getThumbJPG() {
        return thumbJPG;
    }

    public void setThumbJPG(byte[] thumbJPG) {
        this.thumbJPG = thumbJPG;
    }

    public byte[] getIndexJPG() {
        return indexJPG;
    }

    public void setIndexJPG(byte[] indexJPG) {
        this.indexJPG = indexJPG;
    }

    public byte[] getMiddleJPG() {
        return middleJPG;
    }

    public void setMiddleJPG(byte[] middleJPG) {
        this.middleJPG = middleJPG;
    }

    public byte[] getRingJPG() {
        return ringJPG;
    }

    public void setRingJPG(byte[] ringJPG) {
        this.ringJPG = ringJPG;
    }

    public byte[] getLittleJPG() {
        return littleJPG;
    }

    public void setLittleJPG(byte[] littleJPG) {
        this.littleJPG = littleJPG;
    }
}

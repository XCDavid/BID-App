package com.morpho.mrz.data.model

/**
 * Created by alfredohernandez on 01/06/17.
 * mzr
 */

open class Point {
    open var x: Float = 0.toFloat()
    open var y: Float = 0.toFloat()

    constructor(x: Float, y: Float) {
        this.x = x
        this.y = y
    }

    constructor() {

    }
}

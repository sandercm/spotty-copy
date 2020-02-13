package com.example.spotty.service.model;

import android.graphics.Bitmap;

/**
 * The Spotty Picture Data Transfer Object
 */
public class SpottyPicture {
    /**
     * The Bitmap describing the picture
     */
    public Bitmap bitmap;

    /**
     * Instantiates a new Spotty picture.
     *
     * @param bitmap the bitmap
     */
    public SpottyPicture(Bitmap bitmap) {
        this.bitmap = bitmap;
    }
}

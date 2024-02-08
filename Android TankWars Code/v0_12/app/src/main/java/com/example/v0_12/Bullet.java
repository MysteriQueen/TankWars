package com.example.v0_12;

/*****************************************************************************************************
 * Bullet Class
 * The class is responsible for creating a bullet using the parameters in the constructor.
 *
 * @author Riya Mayor
 *****************************************************************************************************/

import android.graphics.*;

public class Bullet {
    //Initialise global variables
    protected Bitmap oBulletBitmap;
    protected int iBulletType;
    protected float fBulletX;
    protected float fBulletY;
    protected float fBulletSpeed;
    protected float fBulletSpeedX;
    protected float fBulletSpeedY;

    /*****************************************************************************************************
     * CONSTRUCTOR
     * The constructor holds properties of a single bullet.
     *
     * @param oBulletPic Bitmap of bullet
     * @param iType BulletType
     * @param fXPosition
     * @param fYPosition
     * @param fSpeed
     * @param fSpeedX X direction bullet is travelling in
     * @param fSpeedY y direction bullet is travelling in
     *****************************************************************************************************/
    public Bullet(Bitmap oBulletPic, int iType, float fXPosition, float fYPosition, float fSpeed, float fSpeedX, float fSpeedY) {
        oBulletBitmap = oBulletPic;
        iBulletType = iType;
        fBulletX = fXPosition;
        fBulletY = fYPosition;
        fBulletSpeed = fSpeed;
        fBulletSpeedX = fSpeedX;
        fBulletSpeedY = fSpeedY;
    }
}

package edu.uw.jonanv.motiongame;

import android.graphics.Paint;

import java.util.Random;

/**
 * Created by vincentjoe on 5/12/16.
 * a class that holds the information for a ball
 */
public class Ball {
    public float cx;
    public float cy;
    public float radius;
    public float dx;
    public float dy;
    public Paint color;

    /**
     * ball constructor
     * @param cx the center location in x plot
     * @param cy the center location in y plot
     */
    public Ball(float cx, float cy) {
        this.cx = cx;
        this.cy = cy;
        this.dx = 0; // default to 0 velocity.
        this.dy = 0;
        color = new Paint(Paint.ANTI_ALIAS_FLAG);

        // set random color for the balls
        color.setARGB(
                255,
                (new Random()).nextInt(256),
                (new Random()).nextInt(256),
                (new Random()).nextInt(256)
        );

        // set random radius for the ball
        radius = (int) (Math.random() * 200);
    }

    public float getRadius() {
        return this.radius;
    }

    public void setRadius(float radius){
        this.radius = radius;
    }

    public void setX(float cx){
        this.cx = cx;
    }

    public float getX(){
        return this.cx;
    }

    public void setY(float cy){
        this.cy = cy;
    }

    public float getY() {
        return this.cy;
    }
}

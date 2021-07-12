package com.example.wreckingball;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import java.util.Random;


public class Ball
{
    private float cx, cy;
    private float radius;
    private float dx,dy;
    private Paint pen;

    public Ball(float cx, float cy)
    {
        this.radius = 20;
        this.cx = cx;
        this.cy = cy;
        this.pen = new Paint(Paint.ANTI_ALIAS_FLAG);
    }

    public void draw(Canvas canvas)
    {
        pen.setColor(Color.WHITE);
        canvas.drawCircle(cx, cy, radius, pen);
    }

    public void move(int w, int h)
    {
        this.cx+= dx;
        this.cy+= dy;

        // check if ball out of left or right side
        if((cx-radius)<=0 || (cx+radius)>=w)
        {
            dx = -dx;
        }

        // check if ball out of up side
        if((cy-radius)<=0)
        {
            dy = -dy;
        }
    }

    public boolean checkBottom(float h)
    {
        // check if ball out of bottom side - ball isn't "live" anymore
        if((cy-radius)>=h)
        {
            return true;
        }
        return false;
    }

    public boolean collideWith(Bubble bubble)
    {
        double dist = Math.sqrt(Math.pow((this.cx - bubble.getCx()), 2) + Math.pow((this.cy - bubble.getCy()), 2));

        return (dist <= this.radius + Bubble.getRadius());
    }

    public float getCx() {
        return cx;
    }

    public float getCy() {
        return cy;
    }

    public float getDx() {
        return dx;
    }

    public void setDx(float dx) {
        this.dx = dx;
    }

    public float getDy() {
        return dy;
    }

    public void setDy(float dy) {
        this.dy = dy;
    }

    public float getRadius() {
        return radius;
    }

    public void setBallSteps(float dx, float dy)
    {
        setDx(dx);
        setDy(dy);
    }
	
}

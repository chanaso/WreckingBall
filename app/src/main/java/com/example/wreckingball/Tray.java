package com.example.wreckingball;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;


public class Tray{

    // X, Y are the fars left and top of the rectangle which forms the tray
    private float x;
    private float y;

    // The dimensions of the tray
    private float length;
    private float height;

    // for tray movements, by gyroscope sensor
    private float dx;

    private Paint pen;


    public Tray(int canvasWidth, int canvasHeight){

        // defines tray wide and high
        length = canvasWidth / 5;
        height = 40;

        // Start Tray in the screen centrer
        x = (canvasWidth - length) / 2;
        y = canvasHeight - height;

        // How fast the tray will move
        dx = 0;

        this.pen = new Paint(Paint.ANTI_ALIAS_FLAG);
    }

    public void draw(Canvas canvas)
    {
        pen.setColor(Color.rgb(91,89,89));  // gray color
        canvas.drawRect(x, y, x + length, y + height, pen);
    }

    public void move(int w, float traySpeed)
    {
        this.x += dx;

        if((traySpeed < 0 && x <= 0) || (traySpeed > 0 && (x+length) >= w)) // try to move left in left edge, or try to move right in right edge
        {
            dx = 0;
        }
        else
        {
            dx = traySpeed;
        }
    }

    public boolean checkCollision(float cx, float cy, float radius)
    {
        if(cx + radius >= this.x && cx - radius <= this.x + length && cy + radius >= this.y && cy + radius <= (this.y + height/5))
            return true;
        return false;
    }

    public float getX(){ return  this.x; }

    public  float getY() { return this.y; }

    public float getLength()
    {
        return this.length;
    }

    public float getHeight()
    {
        return this.height;
    }

    public void setLength(float length)
    {
        this.length += length;
    }

}

package com.example.wreckingball;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;


public class Surprise {
    private static final int[] surpriseIds = {R.drawable.grow_tray, R.drawable.little_tray, R.drawable.inc_speed, R.drawable.dec_speed, R.drawable.add_balls};

    private int id;
    private float cx;
    private float cy;
    private int radius;
    private Bitmap image;
    private float dy;
    private Paint pen;

    public Surprise(Context context, int id, float cx, float cy)
    {
        this.id = id;
        this.cx = cx;
        this.cy = cy;
        this.radius = 30;
//        BitmapFactory.Options opt = new BitmapFactory.Options();
//        opt.inMutable = true;
        this.image = BitmapFactory.decodeResource(context.getResources(), surpriseIds[id-1]);
        this.image = Bitmap.createScaledBitmap(this.image,2*radius,2*radius, true);
        this.dy = 8;

        this.pen = new Paint(Paint.ANTI_ALIAS_FLAG);
    }

    public void draw(Canvas canvas)
    {
        canvas.drawBitmap(this.image, cx, cy, pen);
    }

    public void move()
    {
        this.cy += dy;
    }

    public boolean checkBottom(int h)
    {
        // check if surprise out of bottom side - surprise isn't available anymore
        if((cy-radius) >= h)
            return true;
        return false;
    }

    public int getId() { return id; }

    public float getCx() {
        return cx;
    }

    public float getCy() {
        return cy;
    }

    public int getRadius()
    {
        return radius;
    }

}

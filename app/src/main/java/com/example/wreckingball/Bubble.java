package com.example.wreckingball;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import java.util.Random;

public class Bubble {

    private static Random rnd = new Random();
    private static int[] colors = {Color.RED, Color.YELLOW, Color.WHITE, Color.MAGENTA, Color.GREEN, Color.rgb(255, 88, 15), Color.rgb(255, 126, 38), Color.rgb(255, 156, 89)};
    // set the borders of the locations of bubbles that will draw
    private static final int OFFSET = 20;
    private static final int NUM_BALLS = 17;
    private static int left;
    private static int right;
    private static int top;
    private static int bottom;
    private static float delta;
    private static float radius;

    private float cx;
    private float cy;
    private int color;
    private int hardness;
    private int score;
    private Surprise surprise;
    private Paint pen;


    public static void initBubblesBuild(int width, int height)
    {
        right = width;
        bottom = 3 * height / 5;

        delta = (right - 2*OFFSET) / NUM_BALLS;
        radius = 2 * delta / 5;

        left = OFFSET + (int)radius;
        top = OFFSET + (int)radius;
    }

    public Bubble(Context context)
    {
        this.cx = left;
        this.cy = top;
        this.color = colors[rnd.nextInt(colors.length)];
        // random the hardness level, between 1-4, with high probability to 4
        this.hardness = rnd.nextInt(10) + 1;
        if(this.hardness > 4)
            this.hardness = 4;
        this.score = this.hardness * 5;
		
		// random for take a surprise or not
		int s_id = rnd.nextInt(25);
		if(s_id >= 1 && s_id <= 5){ // for random if taking a surprise or not (if 1-5 -> define some surprise, else -> not)
            this.surprise = new Surprise(context, s_id, cx, cy);
        }
		else {
            this.surprise = null;
        }

        this.pen = new Paint(Paint.ANTI_ALIAS_FLAG);

        // update location for next bubble
		left += delta;
        if(left >= right){
            left = OFFSET + (int)radius;
            top += delta;
        }
    }

    public static boolean isBuildDone()
    {
        return top >= bottom;
    }
	
	public void draw(Canvas canvas)
    {
        pen.setColor(this.color);
        canvas.drawCircle(cx, cy, radius, pen);
    }

    public Surprise getSurprise()
    {
        return this.surprise;
    }

    public float getCx() {
        return this.cx;
    }

    public float getCy() {
        return this.cy;
    }

    public  static float getRadius()
    {
        return radius;
    }

    public int getScore()
    {
        return score;
    }

    public int getHardness()
    {
        return hardness;
    }
    public void decHardness()
    {
        this.hardness--;
    }

}

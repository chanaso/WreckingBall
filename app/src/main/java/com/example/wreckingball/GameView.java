package com.example.wreckingball;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;

public class GameView extends View implements SensorEventListener
{
    // finals
    private static final int CANVAS_BG_COLOR = Color.BLACK;
    private static final int CHANGE_BALL_SPEED = 5;
    private static final int BALL_DX = 6, BALL_DY = -9;

    // states
    private enum State {GET_READY, PLAYING, GAME_OVER};

    // Holds an instance of the SensorManager system service.
    private SensorManager mSensorManager;
    // gyroscope sensor, as retrieved from the sensor manager.
    private Sensor mSensor;

    private float traySpeed;

    // objects in the game screen
    private Tray tray;
    private ArrayList<Ball> balls;
    private ArrayList<Bubble> bubbles;
	private ArrayList<Surprise> surprises;

    private static int canvasWidth;
    private static int canvasHeight;
    private Paint penMsg, penScore;

    // current state
    private State state;
    private boolean is_win;

    // sounds
    MediaPlayer mpGood, mpBad, mpTray;

    private int game_score, curr_score;
	private boolean show_score;
	private float score_txt_x, score_txt_y;
    private Context context;

    public GameView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        this.context = context;
    }

    private void initGame()
    {
        // gyroscope sensor handle
        mSensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
        traySpeed = 0;  // will gat speed by gyroscope sensor

        game_score = 0;
        show_score = false;
        curr_score = 0;
        score_txt_x = 0;
        score_txt_y = 0;

        // load sounds
        mpGood = MediaPlayer.create(context,R.raw.blow);
        mpBad = MediaPlayer.create(context,R.raw.bubble);
        mpTray = MediaPlayer.create(context,R.raw.tray);

        state = State.GET_READY;

        // create tray in the bottom of the screen
        tray = new Tray(canvasWidth, canvasHeight);

        // create jumping ball in the middle of tray
        balls = new ArrayList<>();
        balls.add(new Ball(tray.getX() + tray.getLength()/2, tray.getY() - 20));
        balls.get(0).setBallSteps(BALL_DX, BALL_DY);

        // create "matrix" of bubbles in a screen
        Bubble.initBubblesBuild(canvasWidth, canvasHeight);
        bubbles = new ArrayList<>();
        bubbles.add(new Bubble(context));
        while(!Bubble.isBuildDone())
        {
            bubbles.add(new Bubble(context));
        }
		
		surprises = new ArrayList<>();

        // paint for messages text
        penMsg = new Paint(Paint.ANTI_ALIAS_FLAG);
        penMsg.setTextAlign(Paint.Align.CENTER);
        penMsg.setColor(Color.BLUE);
        penMsg.setStyle(Paint.Style.FILL);
        penMsg.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
        penMsg.setTextSize(85);

        // paint for score text
        penScore = new Paint(Paint.ANTI_ALIAS_FLAG);
        penScore.setTextAlign(Paint.Align.CENTER);
        penScore.setColor(Color.BLUE);
        penScore.setStyle(Paint.Style.STROKE);
        penScore.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
        penScore.setTextSize(85);
        penScore.setStrokeWidth(2);
    }

    @Override
    protected void onDraw(Canvas canvas)
    {
        super.onDraw(canvas);

        // clear canvas with color
        canvas.drawColor(CANVAS_BG_COLOR);

       // draw the tray
        tray.draw(canvas);

        // draw all jumping balls
        for (int i = 0; i < balls.size() ; i++)
            balls.get(i).draw(canvas);
        Log.d("ball draw", Integer.toString(balls.size()));

        // draw all bubbles
        for (int i = 0; i < bubbles.size() ; i++)
            bubbles.get(i).draw(canvas);
		
		// draw all surprises available
        for (int i = 0; i < surprises.size() ; i++)
            surprises.get(i).draw(canvas);

        // state machine
        switch (state)
        {
            case GET_READY:
            {
                canvas.drawText("Click to PLAY!", canvasWidth / 2, canvasHeight / 2, penMsg);
                break;
            }

            case PLAYING:
            {
                // start gyroscope sensor listener
                startSensorsListener();

                if(show_score == true)
                    canvas.drawText("+"+curr_score, score_txt_x, score_txt_y, penScore);	// write the current score on the screen

                // move the tray, by gyroscope sensor
                tray.move(canvasWidth, traySpeed);

                // move the jumping balls
                for (int i = 0; i < balls.size(); i++) {
                    balls.get(i).move(canvasWidth, canvasHeight);
                    Log.d("debugID", Integer.toString(i));
                }
				
				// move the surprises available
                for (int i = 0; i < surprises.size(); i++)
                    surprises.get(i).move();

                // check collision between jumping balls and any of the bubbles
                for (int i = 0; i < balls.size(); i++) {
                    for (int j = 0; j < bubbles.size(); j++) {
						Bubble bubble = bubbles.get(j);
                        if (balls.get(i).collideWith(bubble))
                        {
                            balls.get(i).setDy(-balls.get(i).getDy());  // change the ball angle
							bubble.decHardness(); // decrease the hardness of this bubble
                            if(bubble.getHardness() == 0)
                            {
                                // play "good" sound
                                mpGood.start();

								game_score += bubble.getScore();    // keep the score of this bubble
                                curr_score = bubble.getScore();
                                score_txt_x = bubble.getCx();
                                score_txt_y = bubble.getCy();
                                showScore();	// show the score during 2 seconds - start a timer
								
								if(bubble.getSurprise() != null)   // if this bubble held a surprise - add it to available surprises
								{
									surprises.add(bubble.getSurprise());
								}
								bubbles.remove(j);  // delete this bubble
                            }
                            else{
                                // play "bad" sound
                                mpBad.start();
                            }
                            break;
                        }
                    }
                }

                // run over all surprises
                for (int i = 0; i < surprises.size(); i++) {
					Surprise surprise = surprises.get(i);
                    // check collision between the tray and any of the available surprises
					boolean is_coll = tray.checkCollision(surprise.getCx(), surprise.getCy(), (float)surprise.getRadius());
					if (is_coll) {	// in case of catch the surprise
						surpriseHandle(surprise.getId());
                        surprises.remove(i);
						break;
					}
                    // check collision between surprises and the bottom of the screen
                    if(surprises.get(i).checkBottom(canvasHeight))  //  return true when the surprise is out of the screen (from bottom)
                        surprises.remove(i);
				}

                // run over all balls
                for (int i = 0; i < balls.size(); i++) {
                    Ball ball = balls.get(i);
                    // check collision between the tray and any of the available balls
                    boolean is_coll = tray.checkCollision(ball.getCx(), ball.getCy(), ball.getRadius());
                    if (is_coll) {	// the ball hit the tray
                        ball.setDy(-ball.getDy());
                        // play sound of collision
                        mpTray.start();
                        break;
                    }
                    // check collision between balls and the bottom of the screen
                    if(ball.checkBottom(tray.getY() + tray.getHeight()/2)){    //  return true when the ball is out of the screen (from bottom)
                        balls.remove(i);
                        Log.d("ball removed", Integer.toString(balls.size()));
                    }
                }

                // check if no more jumping balls available
                if (balls.size() == 0) {
                    state = State.GAME_OVER;
                    is_win = false;
                }
				
				// check if no more bubbles to break
                if (bubbles.size() == 0) {
                    state = State.GAME_OVER;
                    is_win = true;
                }
				
                break;
            }

            case GAME_OVER:
            {
                String message;
                if(is_win)
                    message = "YOU WON!!!";
                else
                    message = "GAME OVER!!!";

                canvas.drawText(message + "\n\nYour Score: " + Integer.toString(game_score), canvasWidth / 2, canvasHeight / 2, penMsg);

                // save max game score in scoreFile
                SharedPreferences sp = context.getSharedPreferences("scoreFile", Context.MODE_PRIVATE);
                int top_score = sp.getInt("score", 0);
                if(game_score > top_score)
                {
                    SharedPreferences.Editor editor = sp.edit();
                    editor.putInt("score", game_score);
                    editor.commit();
                }

                // unregisterListener all sensors
                stopSensorsListener();

                break;
            }
        }

        // Animation loop (redraw this view by invalidate - call onDraw() - for animation loop
        invalidate();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        if (event.getAction() == MotionEvent.ACTION_DOWN)
        {
            if (state == State.GET_READY)
            {
                state = State.PLAYING;
            } else
            {
                if (state == State.PLAYING)
                {
                    state = State.GET_READY;
                } else // start a new game
                {
                    initGame();
                }
            }
        }
        return true;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh)
    {
        super.onSizeChanged(w, h, oldw, oldh);
        canvasWidth = w;
        canvasHeight = h;
        initGame();
    }

    private void surpriseHandle(int s_id)
    {
		switch (s_id)
        {
            case 1: tray.setLength(tray.getLength()/2); break;  // increase tray length
            case 2: tray.setLength(-tray.getLength()/2); break; // decrease tray length
            case 3: for (int i = 0; i < balls.size(); i++) { // increase balls speed
                balls.get(i).setBallSteps(BALL_DX + CHANGE_BALL_SPEED, BALL_DY - CHANGE_BALL_SPEED);
            }
            break;
            case 4: for (int i = 0; i < balls.size(); i++) { // decrease balls speed
                balls.get(i).setBallSteps(BALL_DX - CHANGE_BALL_SPEED, BALL_DY + CHANGE_BALL_SPEED);
            }
            break;
            case 5: for (int i = 0; i < 3; i++) { // add 3 balls to the game
                balls.add(new Ball(tray.getX() + tray.getLength() / 2, tray.getY() - 20));
            }
            // define dx, dy to the 3 new balls
            for (int i = balls.size()-3; i <  balls.size(); i++) {
                    balls.get(i).setBallSteps(BALL_DX, BALL_DY);
                }
            break;
            default: Log.d("debug", "no surprise id");
        }
    }

	private void showScore()
	{
        show_score = true;
        Timer timer = new Timer(1);  // show score text during 1 seconds
	    timer.start();
	}


    private void startSensorsListener()
    {
        // Listeners for the sensors are registered in this callback
        if (mSensor != null)
            mSensorManager.registerListener(this, mSensor, mSensorManager.SENSOR_DELAY_GAME);
    }

    private void stopSensorsListener()
    {
        // Unregister all sensor listeners in this callback so they don't
        // continue to use resources when the app is paused.
        mSensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent)
    {
        // The new data value of the sensor.
        // sensors report one value at a time, which is always the first
        // element in the values array.
        float axisX = sensorEvent.values[0];
//        float axisY = sensorEvent.values[1];
//        float axisZ = sensorEvent.values[2];

        traySpeed = axisX * 100;

        Log.d("debug", "x="+Float.toString(axisX));
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }


    private class Timer {

        private int seconds;

        public Timer(int seconds)
        {
            this.seconds = seconds;
        }

        public void start()
        {
            new Thread(new Runnable()
            {
                @Override
                public void run()
                {
                    while(seconds > 0)
                    {
                        SystemClock.sleep(1000);
                        seconds--;
                    }
                    // stop showing the score_txt
                    show_score = false;

                }
            }).start();
        }

    }
}



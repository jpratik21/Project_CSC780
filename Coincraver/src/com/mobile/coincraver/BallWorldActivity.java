/**
 * Android Application/Game: Coincraver
 * 
 */

package com.mobile.coincraver;

import java.util.Random;
import org.jbox2d.collision.AABB;
import org.jbox2d.collision.CircleDef;
import org.jbox2d.collision.PolygonDef;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.World;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.view.View;

/**
 * This BallWorld class/activity is responsible for creating the world that
 * shows 12 balls falling randomly on to the screen. This is achieved with the
 * help of Box2D which is a physics game engine and is compatible with android
 * SDK. This activity extends View class of android and we set this as a default
 * view for the AboutActivity.You can download the jar file from the link
 * https://code.google.com/p/obrik/downloads/detail?name=jbox2d-2.0.1-full.jar
 * &can=2&q= You will need to reference this library in your project(check
 * referenced libraries folder).
 * 
 * @aurthor jpratik
 * 
 */

public class BallWorldActivity extends View {

	public int targetFPS = 40, iterations = 5;
	public float timeStep = 10.0f / targetFPS;

	private Body[] bodies;
	private int count = 0;

	private AABB worldAABB;
	public World world;
	private PolygonDef groundShapeDef;

	public int World_W, World_H;

	private Paint paint, info;

	private float radius = 50;

	public BallWorldActivity(Context context, int W, int H) {
		super(context);
		World_W = W;
		World_H = H;

		/*
		 * 1. Create the physics world boundaries. The Vec2 class is provided by
		 * the Box2D library
		 */
		worldAABB = new AABB();

		Vec2 min = new Vec2(-50, -50);
		Vec2 max = new Vec2(World_W + 50, World_H + 50);

		worldAABB.lowerBound.set(min);
		worldAABB.upperBound.set(max);

		/*
		 * 2. Create the physics world. Also, specify the gravity whose values
		 * can be specified as a parameter to the Vec2 class's object
		 */

		Vec2 gravity = new Vec2((float) -2.0, (float) -10.0);
		boolean doSleep = true;
		world = new World(worldAABB, gravity, doSleep);

		// Create the ground surface for the screen
		BodyDef bodyDef = new BodyDef();
		bodyDef.position.set(new Vec2((float) 0.0, (float) -10.0));
		Body groundBody = world.createBody(bodyDef);
		groundShapeDef = new PolygonDef();
		groundShapeDef.setAsBox(World_W, 10);
		groundBody.createShape(groundShapeDef);

		// Create the top surface for the screen
		bodyDef = new BodyDef();
		bodyDef.position.set(new Vec2((float) 0.0, (float) (World_H + 10.0)));
		groundBody = world.createBody(bodyDef);
		groundShapeDef = new PolygonDef();
		groundShapeDef.setAsBox(World_W, 10);
		groundBody.createShape(groundShapeDef);

		// Creat the left-side surface for the screen
		bodyDef = new BodyDef();
		bodyDef.position.set(new Vec2(-10, (float) 0.0));
		groundBody = world.createBody(bodyDef);
		groundShapeDef = new PolygonDef();
		groundShapeDef.setAsBox(10, World_H);
		groundBody.createShape(groundShapeDef);

		// Create the right-side surface for the screen
		bodyDef = new BodyDef();
		bodyDef.position.set(new Vec2((float) World_W + 10, (float) 0.0));
		groundBody = world.createBody(bodyDef);
		groundShapeDef = new PolygonDef();
		groundShapeDef.setAsBox(10, World_H);
		groundBody.createShape(groundShapeDef);

		/*
		 * Initialize the bodies. This is specified as an array and can store
		 * the number of shapes equal to the index
		 */
		bodies = new Body[50];

		/*
		 * paint and info are the objects of Paint class used by canvas to draw
		 * the balls and text into the ball world respectively
		 */

		paint = new Paint();
		paint.setStyle(Style.FILL);
		paint.setColor(Color.GREEN);

		info = new Paint();
		info.setAntiAlias(true);
		info.setARGB(255, 255, 0, 0);
		info.setFakeBoldText(true);
		info.setTextSize(50.0f);
	}

	/*
	 * addBall() takes care of creating and adding the balls(with properties)
	 * into the above created world. They initially occupy random positions(made
	 * use of Random class). This is a thread-safe class.
	 */

	public void addBall() {

		BodyDef bodyDef = new BodyDef();
		Random rnd = new Random();
		bodyDef.position.set(
				radius * 2 + rnd.nextInt((int) (World_W - radius * 4)), 2
						* radius + rnd.nextInt((int) (World_H - radius * 4)));
		bodies[count] = world.createBody(bodyDef);

		CircleDef circle = new CircleDef();
		circle.radius = radius;
		circle.density = (float) 1.0;
		circle.restitution = 0.5f;

		/*
		 * Assigning the above created shape to the body. setMassFromShapes()
		 * takes care of setting the mass properties and handles the center of
		 * mass of a body correctly.
		 */
		bodies[count].createShape(circle);
		bodies[count].setMassFromShapes();

		count += 1;
	}

	/*
	 * Step() performs collision detection between the balls. Timestep (the
	 * first parameter) is the amount of time you want to simulate the bodies.
	 * postInvalidate() is used to invalidate the view if called from a non-UI
	 * thread.
	 */

	public void update() {

		world.step(timeStep, iterations);
		postInvalidate();
	}

	/*
	 * onDraw() draws the text on the screen with the help of canvas class. At
	 * the end, by applying for loop we draw 12 balls at their appropriate x and
	 * y positions with the defined radius.
	 */

	@Override
	protected void onDraw(Canvas canvas) {
		canvas.drawText(
				"Application was developed by Pratik J. and Royce B. as a part of Computer ",
				50, 100, info);
		canvas.drawText(
				"Science course 780 at San Francisco State University under Dr.Puder for the ",
				50, 180, info);
		canvas.drawText(
				"term Fall 2014. First of all, thank you for installing our little, tiny and sweet ",
				50, 260, info);
		canvas.drawText(
				"Coincraver application for Android. There was no use of external library ",
				50, 340, info);
		canvas.drawText(
				"for Coincraver. The app makes use of fundamentals of Java & Android. Should ",
				50, 420, info);
		canvas.drawText(
				"you have any questions/concerns, shoot us an email/query on the address : ",
				50, 500, info);
		canvas.drawText("coincraver@gmail.com." + "" + ""
				+ "Thanks, Pratik and Royce! ", 50, 580, info);

		for (int j = 0; j < 12; j++)
			canvas.drawCircle(bodies[j].getPosition().x,
					World_H - bodies[j].getPosition().y, radius, paint);
	}
}

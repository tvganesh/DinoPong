package com.tvganesh.dinopong;

import org.andengine.engine.camera.Camera;
import org.andengine.engine.camera.hud.controls.AnalogOnScreenControl;
import org.andengine.engine.camera.hud.controls.DigitalOnScreenControl;
import org.andengine.engine.camera.hud.controls.AnalogOnScreenControl.IAnalogOnScreenControlListener;
import org.andengine.engine.camera.hud.controls.BaseOnScreenControl.IOnScreenControlListener;
import org.andengine.engine.camera.hud.controls.BaseOnScreenControl;
import org.andengine.engine.handler.physics.PhysicsHandler;
import org.andengine.engine.options.EngineOptions;
import org.andengine.engine.options.ScreenOrientation;
import org.andengine.engine.options.resolutionpolicy.RatioResolutionPolicy;
import org.andengine.entity.primitive.Line;
import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.scene.IOnSceneTouchListener;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.scene.background.Background;
import org.andengine.entity.scene.menu.MenuScene;
import org.andengine.entity.scene.menu.MenuScene.IOnMenuItemClickListener;
import org.andengine.entity.scene.menu.item.IMenuItem;
import org.andengine.entity.scene.menu.item.SpriteMenuItem;
import org.andengine.entity.sprite.AnimatedSprite;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.text.Text;
import org.andengine.entity.text.TextOptions;
import org.andengine.entity.util.FPSCounter;
import org.andengine.entity.util.FPSLogger;
import org.andengine.entity.util.ScreenCapture;
import org.andengine.entity.util.ScreenCapture.IScreenCaptureCallback;


import org.andengine.input.touch.TouchEvent;
import org.andengine.input.touch.controller.MultiTouch;
import org.andengine.opengl.font.Font;
import org.andengine.opengl.font.FontFactory;
import org.andengine.opengl.texture.TextureOptions;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.texture.region.TiledTextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;
import org.andengine.ui.activity.SimpleBaseGameActivity;
import org.andengine.util.FileUtils;
import org.andengine.util.HorizontalAlign;
import org.andengine.util.math.MathUtils;

import android.graphics.Typeface;
import android.opengl.GLES20;
import android.util.Log;
/**********************************************************************************************
 * Dino Pong - Designed and developed by Tinniam V Ganesh using AndEngine
 * Date: 28 Dec 2012
 * Blog: http://gigadom.wordpress.com
 * 
 * AndEngine developed by  (c) 2010 Nicolas Gramlich
 * (c) 2011 Zynga
 * 
 */



/**
 * (c) 2010 Nicolas Gramlich
 * (c) 2011 Zynga
 *
 * @author Nicolas Gramlich
 * @since 00:06:23 - 11.07.2010
 */
public class DinoPong extends SimpleBaseGameActivity {
	// ===========================================================
	// Constants
	// ===========================================================

	private static final int CAMERA_WIDTH = 320;
	private static final int CAMERA_HEIGHT = 480;

	protected static final int MENU_RESET = 0;
	protected static final int MENU_QUIT = MENU_RESET + 1;
	// ===========================================================
	// Fields
	// ===========================================================

	private Camera mCamera;

	private BitmapTextureAtlas mBitmapTextureAtlas;
	private ITextureRegion mPaddleTextureRegion;
	private TiledTextureRegion mFaceTextureRegion;
	private TiledTextureRegion mBrontTextureRegion;
	private TiledTextureRegion mBoxFaceTextureRegion;
	
	private BitmapTextureAtlas mOnScreenControlTexture;
	private ITextureRegion mOnScreenControlBaseTextureRegion;
	private ITextureRegion mOnScreenControlKnobTextureRegion;

	private boolean mPlaceOnScreenControlsAtDifferentVerticalLocations = false;
	
	private DigitalOnScreenControl mDigitalOnScreenControl;
	
	static Font mFont;
	
	private static final float BRONT_VELOCITY = 100.0f;
	private static final float BALL_VELOCITY = 140.0f;
	private static final float BOX_VELOCITY = 120.0f;
	
	protected Scene scene;
	
	protected MenuScene mMenuScene;

	private BitmapTextureAtlas mMenuTexture;
	protected ITextureRegion mMenuResetTextureRegion;
	protected ITextureRegion mMenuQuitTextureRegion;
	
	static Text bText;
	static Text hitsText;
	static Text missesText;
	static Text scoreText;
	
	static int hits;
	static int misses;
	static int score;
	
	static Sprite paddle;
	static Bront bront;
	static Ball ball;
	static Box box;
	// ===========================================================
	// Constructors
	// ===========================================================

	// ===========================================================
	// Getter & Setter
	// ===========================================================

	// ===========================================================
	// Methods for/from SuperClass/Interfaces
	// ===========================================================

	@Override
	public EngineOptions onCreateEngineOptions() {
		this.mCamera = new Camera(0, 0, CAMERA_WIDTH, CAMERA_HEIGHT);

		final EngineOptions engineOptions = new EngineOptions(true, ScreenOrientation.PORTRAIT_FIXED, new RatioResolutionPolicy(CAMERA_WIDTH, CAMERA_HEIGHT), this.mCamera);
		//engineOptions.getTouchOptions().setNeedsMultiTouch(false);
		return engineOptions;
	}

	@Override
	public void onCreateResources() {
		
		// Create Font
		this.mFont = FontFactory.create(this.getFontManager(), this.getTextureManager(), 256, 256, Typeface.create(Typeface.DEFAULT, Typeface.BOLD), 32);
		this.mFont.load();

		BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/");
		
		
		// Create a ball
		this.mBitmapTextureAtlas = new BitmapTextureAtlas(this.getTextureManager(), 64, 32, TextureOptions.BILINEAR);
		this.mFaceTextureRegion = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(this.mBitmapTextureAtlas, this, "face_circle_tiled.png", 0, 0, 2, 1);
		this.mBitmapTextureAtlas.load();
		
		// Create a bront
		this.mBitmapTextureAtlas = new BitmapTextureAtlas(this.getTextureManager(), 160, 64, TextureOptions.BILINEAR);
		this.mBrontTextureRegion = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(this.mBitmapTextureAtlas, this, "bront2_tiled.png", 0, 0, 5, 1); // 
		this.mBitmapTextureAtlas.load();

		// Create a paddle
		this.mBitmapTextureAtlas = new BitmapTextureAtlas(this.getTextureManager(), 90, 30, TextureOptions.BILINEAR);
		this.mPaddleTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(this.mBitmapTextureAtlas, this, "paddle1.png", 0, 0);
		this.mBitmapTextureAtlas.load();
		
		// Create a Box face
		this.mBitmapTextureAtlas = new BitmapTextureAtlas(this.getTextureManager(), 64, 64, TextureOptions.BILINEAR);
		this.mBoxFaceTextureRegion = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(this.mBitmapTextureAtlas, this, "face_box_tiled.png", 0, 0, 2, 1); // 64x32
		this.mBitmapTextureAtlas.load();

		this.mOnScreenControlTexture = new BitmapTextureAtlas(this.getTextureManager(), 128, 64, TextureOptions.BILINEAR);
		this.mOnScreenControlBaseTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(this.mOnScreenControlTexture, this, "onscreen_control_base.png", 0, 0);
		this.mOnScreenControlKnobTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(this.mOnScreenControlTexture, this, "onscreen_control_knob.png", 64, 0);
		this.mOnScreenControlTexture.load();
		hits = misses = score = 0;
	}

	@Override
	public Scene onCreateScene() {
		this.mEngine.registerUpdateHandler(new FPSLogger());

		
		scene = new Scene();
		scene.setBackground(new Background(0.09804f, 0.6274f, 0.8784f));
		
		// Create a bounded rectangle
		
		bText = new Text(150, 40, this.mFont, "Dino Pong!", new TextOptions(HorizontalAlign.CENTER), this.getVertexBufferObjectManager());
		scene.attachChild(bText);
		bText.setScale((float)0.75);
		

		
		hitsText =   new Text(130, 405, this.mFont, "Hits:  ", "Hits: XXXXX".length(), this.getVertexBufferObjectManager());
		missesText = new Text(130, 425, this.mFont, "Misses:", "Misses: XXXXX".length(), this.getVertexBufferObjectManager());
		scoreText = new Text(130, 445, this.mFont,  "Score: ", "Score: XXXXX".length(), this.getVertexBufferObjectManager());
		hitsText.setScale((float) 0.75);
		hitsText.setColor(0,0,1);
		missesText.setScale((float) 0.75);
		missesText.setColor(0,0,1);
		scoreText.setScale((float) 0.75);
		scoreText.setColor(0,0,1);
		
		
		scene.attachChild(hitsText);
		scene.attachChild(missesText);
		scene.attachChild(scoreText);
		
		final Line line1 = new Line(0, 0, 320, 0, 5, this.getVertexBufferObjectManager());
		final Line line2 = new Line(320, 0, 320, 400, 5, this.getVertexBufferObjectManager());
		final Line line3 = new Line(320, 400, 0, 400, 5, this.getVertexBufferObjectManager());
		final Line line4 = new Line(0, 400, 0, 0, 5, this.getVertexBufferObjectManager());
		
		// Set color to black
		line1.setColor(0,0,0);
		line2.setColor(0,0,0);
		line3.setColor(0,0,0);
		line4.setColor(0,0,0);
		
		
		// Add bounded rectangle to scene
		scene.attachChild(line1);
		scene.attachChild(line2);
		scene.attachChild(line3);
		scene.attachChild(line4);

		// Add ball to scene
		final float X = (CAMERA_WIDTH - this.mFaceTextureRegion.getWidth()) / 2;
		final float Y = (CAMERA_HEIGHT - this.mFaceTextureRegion.getHeight()) / 2;
		ball = new Ball(X, Y, this.mFaceTextureRegion, this.getVertexBufferObjectManager());
		scene.attachChild(ball);

		// Add box to scene
		final float X1 = (CAMERA_WIDTH - this.mBoxFaceTextureRegion.getWidth()) / 2;
		final float Y1 = 270;
		box = new Box(X1, Y1, this.mBoxFaceTextureRegion, this.getVertexBufferObjectManager());
		scene.attachChild(box);
		
		// Add paddle 
		final float centerX = (CAMERA_WIDTH - this.mPaddleTextureRegion.getWidth()) / 2;
		
		
		float centerY = 320;
		paddle = new Sprite(centerX, centerY, this.mPaddleTextureRegion, this.getVertexBufferObjectManager());
		final PhysicsHandler physicsHandler = new PhysicsHandler(paddle);
		paddle.registerUpdateHandler(physicsHandler);

		scene.attachChild(paddle);

		// Create a shaking brontosaurus
		final float cX = (CAMERA_WIDTH - this.mBrontTextureRegion.getWidth())/2;
		final float cY = 50;
		bront = new Bront(cX, cY, this.mBrontTextureRegion, this.getVertexBufferObjectManager());
		bront.registerUpdateHandler(physicsHandler);		
		scene.attachChild(bront);
        
        
        
		// Add a digital on screen control
		this.mDigitalOnScreenControl = new DigitalOnScreenControl(50, CAMERA_HEIGHT - this.mOnScreenControlBaseTextureRegion.getHeight()  + 20, this.mCamera, this.mOnScreenControlBaseTextureRegion, this.mOnScreenControlKnobTextureRegion, 0.1f, this.getVertexBufferObjectManager(), new IOnScreenControlListener() {
			@Override
			public void onControlChange(final BaseOnScreenControl pBaseOnScreenControl, final float pValueX, final float pValueY) {
				physicsHandler.setVelocity(pValueX * 100, 0);
			}
			
			
		});
		this.mDigitalOnScreenControl.getControlBase().setBlendFunction(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
		this.mDigitalOnScreenControl.getControlBase().setAlpha(0.5f);
		this.mDigitalOnScreenControl.getControlBase().setScaleCenter(0, 128);
		this.mDigitalOnScreenControl.getControlBase().setScale(1.25f);
		this.mDigitalOnScreenControl.getControlKnob().setScale(1.25f);
		this.mDigitalOnScreenControl.refreshControlKnobPosition();

		scene.setChildScene(this.mDigitalOnScreenControl);

		return scene;
	}
	


	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
	
	private static class Bront extends AnimatedSprite {
		private final PhysicsHandler mPhysicsHandler;
        float x,y;
        
		public Bront(final float pX, final float pY, final TiledTextureRegion pTextureRegion, final VertexBufferObjectManager pVertexBufferObjectManager) {

			
			super(pX, pY, pTextureRegion, pVertexBufferObjectManager);
			this.animate(100);
			this.mPhysicsHandler = new PhysicsHandler(this);
			this.registerUpdateHandler(this.mPhysicsHandler);
			
			// Change the angle to the horizontal
			this.mPhysicsHandler.setVelocity(BRONT_VELOCITY, BRONT_VELOCITY);
			
		}

		@Override
		protected void onManagedUpdate(final float pSecondsElapsed) {
			if(this.mX < 0) {

				this.mPhysicsHandler.setVelocityX(BRONT_VELOCITY);
			} else if(this.mX + this.getWidth() > CAMERA_WIDTH) {
			
				this.mPhysicsHandler.setVelocityX(-BRONT_VELOCITY);
			}

			if(this.mY < 0) {
				
				this.mPhysicsHandler.setVelocityY(BRONT_VELOCITY);
				//bText.setText("");
			} else if(this.mY + this.getHeight() + 80 > CAMERA_HEIGHT) { // Edge of box
				x = this.getX();
				y = this.getY();
				bText.setPosition(x-10,y + 20);
  				bText.setText("-1");	
  				misses = misses - 1;
  				score = score -1;
  				missesText.setText("Misses: "+ misses);
  				scoreText.setText("Score: " + score);
  				
  				// At bottom. Restart from the top
  				this.setPosition(x, 0);
  				
				this.mPhysicsHandler.setVelocityY(-BRONT_VELOCITY);
				
			}
            // Check collisions
			if(paddle.collidesWith(this) || this.collidesWith(paddle)){
				x = this.getX();
				y = this.getY();
				bText.setPosition(x+10,y+10);				
				bText.setText("+1");				
				hits = hits + 1;
				score = score + 1;
  				hitsText.setText("Hits: "+ hits);
  				scoreText.setText("Score: " + score);
				float vx = this.mPhysicsHandler.getVelocityX();
				float vy = this.mPhysicsHandler.getVelocityY();
				this.mPhysicsHandler.setVelocity(-vx,-vy);	
				
			}
			
			if(ball.collidesWith(this)){
				
				float vx = this.mPhysicsHandler.getVelocityX();
				float vy = this.mPhysicsHandler.getVelocityY();
				this.mPhysicsHandler.setVelocity(-vx,-vy);			
			}
			
			if(box.collidesWith(this)){
				
				float vx = this.mPhysicsHandler.getVelocityX();
				float vy = this.mPhysicsHandler.getVelocityY();
				this.mPhysicsHandler.setVelocity(-vx,-vy);			
			}
			
			super.onManagedUpdate(pSecondsElapsed);
		}
	}
	
	private static class Box extends AnimatedSprite {
		private final PhysicsHandler mPhysicsHandler;
		float x,y;
		
		public Box(final float pX, final float pY, final TiledTextureRegion pTextureRegion, final VertexBufferObjectManager pVertexBufferObjectManager) {
			super(pX, pY, pTextureRegion, pVertexBufferObjectManager);
			this.mPhysicsHandler = new PhysicsHandler(this);
			this.registerUpdateHandler(this.mPhysicsHandler);
			this.mPhysicsHandler.setVelocity(BOX_VELOCITY,2 *  BOX_VELOCITY);
		}

		@Override
		protected void onManagedUpdate(final float pSecondsElapsed) {
			if(this.mX < 0) {
				this.mPhysicsHandler.setVelocityX(BOX_VELOCITY);
			} else if(this.mX + this.getWidth() > CAMERA_WIDTH) {
				this.mPhysicsHandler.setVelocityX(-BOX_VELOCITY);
			}

			if(this.mY < 0) {
	
				this.mPhysicsHandler.setVelocityY(BOX_VELOCITY);
			} else if(this.mY + this.getHeight() + 80 > CAMERA_HEIGHT) { // Edge of bounded rectangle
				x = this.getX();
				y = this.getY();				
				bText.setPosition(x - 10,y - 10);				
				bText.setText("-2");
				misses = misses - 2;
				score = score - 2;
 				missesText.setText("Misses: "+ misses);
  				scoreText.setText("Score: " + score);
  				
 				// At bottom. Restart from the top
  				this.setPosition(x, 0);
  				
				this.mPhysicsHandler.setVelocityY(-BOX_VELOCITY);
			}
			
			// Check collisions
			if(paddle.collidesWith(this)  || this.collidesWith(paddle)){
				x = this.getX();
				y = this.getY();				
				bText.setPosition(x, y);				
				bText.setText("+2");
				hits = hits + 2;
				score = score + 2;
  				hitsText.setText("Hits: "+ hits);
  				scoreText.setText("Score: " + score);
				float vx = this.mPhysicsHandler.getVelocityX();
				float vy = this.mPhysicsHandler.getVelocityY();
				this.mPhysicsHandler.setVelocity(-vx,-vy);			
			}
			
			if(bront.collidesWith(this)) {
				float vx = this.mPhysicsHandler.getVelocityX();
				float vy = this.mPhysicsHandler.getVelocityY();
				this.mPhysicsHandler.setVelocity(-vx,-vy);			
			}	
			
			if(ball.collidesWith(this)) {
				float vx = this.mPhysicsHandler.getVelocityX();
				float vy = this.mPhysicsHandler.getVelocityY();
				this.mPhysicsHandler.setVelocity(-vx,-vy);			
			}

			super.onManagedUpdate(pSecondsElapsed);
		}
	}
	
	private static class Ball extends AnimatedSprite {
		private final PhysicsHandler mPhysicsHandler;
		float x,y;
		
		public Ball(final float pX, final float pY, final TiledTextureRegion pTextureRegion, final VertexBufferObjectManager pVertexBufferObjectManager) {
			super(pX, pY, pTextureRegion, pVertexBufferObjectManager);
			this.mPhysicsHandler = new PhysicsHandler(this);
			this.registerUpdateHandler(this.mPhysicsHandler);
			// Set differential x & y component for angle other than 45
			this.mPhysicsHandler.setVelocity(BALL_VELOCITY,(float) 0.5 * BALL_VELOCITY);
		}

		@Override
		protected void onManagedUpdate(final float pSecondsElapsed) {
			if(this.mX < 0) {
				this.mPhysicsHandler.setVelocityX(BALL_VELOCITY);
			} else if(this.mX + this.getWidth() > CAMERA_WIDTH) {
				this.mPhysicsHandler.setVelocityX(-BALL_VELOCITY);
			}

			if(this.mY < 0) {
	
				this.mPhysicsHandler.setVelocityY(BALL_VELOCITY);
			} else if(this.mY + this.getHeight() + 80 > CAMERA_HEIGHT) { // Edge of bounded rectangle
				x = this.getX();
				y = this.getY();
				
				bText.setPosition(x - 10, y - 10);				
				bText.setText("-3");	
				misses = misses  -3;
				score = score -3;
 				missesText.setText("Misses: "+ misses);
  				scoreText.setText("Score: " + score);
  				
 				// At bottom. Restart from the top
  				this.setPosition(x, 0);
  				
				this.mPhysicsHandler.setVelocityY(-BALL_VELOCITY);
			}
			// Check collisions
			if(paddle.collidesWith(this)  || this.collidesWith(paddle)){
				x = this.getX();
				y = this.getY();				
				bText.setPosition(x, y);				
				bText.setText("+3");	
				hits = hits + 3;
				score = score + 3;
  				hitsText.setText("Hits: "+ hits);
  				scoreText.setText("Score: " + score);
				float vx = this.mPhysicsHandler.getVelocityX();
				float vy = this.mPhysicsHandler.getVelocityY();
				this.mPhysicsHandler.setVelocity(-vx,-vy);			
			}
			
			if(bront.collidesWith(this)) {
				float vx = this.mPhysicsHandler.getVelocityX();
				float vy = this.mPhysicsHandler.getVelocityY();
				this.mPhysicsHandler.setVelocity(-vx,-vy);			
			}	
			
			if(box.collidesWith(this)) {
				float vx = this.mPhysicsHandler.getVelocityX();
				float vy = this.mPhysicsHandler.getVelocityY();
				this.mPhysicsHandler.setVelocity(-vx,-vy);			
			}

			super.onManagedUpdate(pSecondsElapsed);
			
		}
	}

	
}

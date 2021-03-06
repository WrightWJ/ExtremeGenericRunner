package com.brackeen.javagamebook.tilegame;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.Iterator;

import javax.sound.midi.Sequence;
import javax.sound.midi.Sequencer;
import javax.sound.sampled.AudioFormat;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.border.Border;

import com.brackeen.javagamebook.graphics.*;
import com.brackeen.javagamebook.sound.*;
import com.brackeen.javagamebook.input.*;
import com.brackeen.javagamebook.test.GameCore;
import com.brackeen.javagamebook.tilegame.sprites.*;

/**
    GameManager manages all parts of the game.
 */
public class GameManager extends GameCore {

	public static void main(String[] args) {
		new GameManager().run();
	}

	// uncompressed, 44100Hz, 16-bit, mono, signed, little-endian
	private static final AudioFormat PLAYBACK_FORMAT =
			new AudioFormat(44100, 16, 1, true, false);

	private static final int DRUM_TRACK = 1;

	public static final float GRAVITY = 0.003f;

	private Point pointCache = new Point();
	private Map map;
	private MidiPlayer midiPlayer;
	private SoundManager soundManager;
	private ResourceManager resourceManager;
	private Sound prizeSound;
	private Sound boopSound;
	private InputManager inputManager;
	private BuildingRenderer renderer;

	private GameAction moveLeft;
	private GameAction moveRight;
	private GameAction jump;
	private GameAction exit;

	private GameAction pause;

	private boolean paused = true;
	private boolean ded = false;

	private JPanel pauseMenu;
	private JPanel end;

	private boolean xCollision;


	public void init() {
		super.init();

		// set up input manager
		initInput();
		// start resource manager
		resourceManager = new ResourceManager(
				screen.getFullScreenWindow().getGraphicsConfiguration());

		// load resources
		//		renderer = new TileMapRenderer();
		//		renderer.setBackground(
		//				resourceManager.loadImage("background.png"));
		renderer = new BuildingRenderer();
		renderer.setBackground(
				resourceManager.loadImage("background.png"));


		// load first map
		//		map = resourceManager.loadNextMap();

		map = resourceManager.initializeMap();

		// load sounds
		soundManager = new SoundManager(PLAYBACK_FORMAT);
		prizeSound = soundManager.getSound("sounds/prize.wav");
		boopSound = soundManager.getSound("sounds/boop2.wav");

		// start music
		midiPlayer = new MidiPlayer();
		Sequence sequence =
				midiPlayer.getSequence("sounds/music.midi");
		midiPlayer.play(sequence, true);
		toggleDrumPlayback();


		//creates final screen
		end = new JPanel();
		end.setBounds(0, 0, screen.getWidth(), screen.getHeight());
		JButton died = new JButton();
		died.setPreferredSize(new Dimension(screen.getWidth(), screen.getHeight()));
		ImageIcon image = new ImageIcon("images/Finish Screen.png");
		died.setIcon(image);
		died.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				stop();
			}
		});
		end.add(died);
		

		
		
		
		
		
		pauseMenu = new JPanel();
		JButton resume = new JButton("resume");
		resume.setFocusable(false);
		resume.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				pause.press();
			}
		});
		JButton exit = new JButton("exit");
		exit.setFocusable(false);
		exit.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				stop();
			}
		});
		
		//create the pause menu
		Border border = BorderFactory.createLineBorder(Color.BLACK);
		pauseMenu.add(resume);
		pauseMenu.add(exit);
		pauseMenu.setBorder(border);

		pauseMenu.setSize(pauseMenu.getPreferredSize());

		//makes pause menu in the center
		//        pauseMenu.setLocation(
		//        		(screen.getWidth() - pauseMenu.getWidth()) /2,
		//        		(screen.getHeight() - pauseMenu.getHeight()) /2);

		screen.getFullScreenWindow().getLayeredPane().add(pauseMenu, JLayeredPane.MODAL_LAYER);
	}


	/**
        Closes any resurces used by the GameManager.
	 */
	public void stop() {
		super.stop();
		midiPlayer.close();
		soundManager.close();
	}


	private void initInput() {
		moveLeft = new GameAction("moveLeft");
		moveRight = new GameAction("moveRight");
		jump = new GameAction("jump", GameAction.DETECT_INITAL_PRESS_ONLY);
		//        exit = new GameAction("exit",
		//            GameAction.DETECT_INITAL_PRESS_ONLY);
		pause = new GameAction("paused",GameAction.DETECT_INITAL_PRESS_ONLY);

		inputManager = new InputManager(
				screen.getFullScreenWindow());
		//        inputManager.setCursor(InputManager.INVISIBLE_CURSOR);

		inputManager.mapToKey(moveLeft, KeyEvent.VK_LEFT);
		inputManager.mapToKey(moveRight, KeyEvent.VK_RIGHT);
		inputManager.mapToKey(jump, KeyEvent.VK_UP);
		//        inputManager.mapToKey(exit, KeyEvent.VK_ESCAPE);
		inputManager.mapToKey(pause, KeyEvent.VK_ESCAPE);

	}


	private void checkInput(long elapsedTime) {

		//        if (exit.isPressed()) {
		//            stop();
		//        }
		if (pause.isPressed()) {
			paused =!paused;
			//    		inputManager.resetAllGameActions();
			pauseMenu.setVisible(paused);
			pause.reset();
		}
		if(!paused) {
			Player player = (Player)map.getPlayer();

			if (player.isAlive()) {
				float velocityX = 0;
				velocityX+=player.getMaxSpeed();
				player.setVelocityX(velocityX);
			}
			if (jump.isPressed()) {
				player.jump(false);
			} else if (jump.isReleased()){
				player.setVelocityY(0);
			}
		}
	}




	public void draw(Graphics2D g) {
		renderer.draw(g, map, screen.getWidth(), screen.getHeight());

		if(paused){
			pauseMenu.paint(g);
		}
		if(ded){
			screen.getFullScreenWindow().getLayeredPane().add(end, JLayeredPane.MODAL_LAYER);
			end.paint(g);	
		}
	}


	/**
        Gets the current map.
	 */
	public Map getMap() {
		return map;
	}


	/**
        Turns on/off drum playback in the midi music (track 1).
	 */
	public void toggleDrumPlayback() {
		Sequencer sequencer = midiPlayer.getSequencer();
		if (sequencer != null) {
			sequencer.setTrackMute(DRUM_TRACK,
					!sequencer.getTrackMute(DRUM_TRACK));
		}
	}


	/**
        Gets the tile that a Sprites collides with. Only the
        Sprite's X or Y should be changed, not both. Returns null
        if no collision is detected.
	 */
	//	public Point getTileCollision(Sprite sprite,
	//			float newX, float newY)
	//	{
	//		float fromX = Math.min(sprite.getX(), newX);
	//		float fromY = Math.min(sprite.getY(), newY);
	//		float toX = Math.max(sprite.getX(), newX);
	//		float toY = Math.max(sprite.getY(), newY);
	//
	//		// get the tile locations
	//		int fromTileX = TileMapRenderer.pixelsToTiles(fromX);
	//		int fromTileY = TileMapRenderer.pixelsToTiles(fromY);
	//		int toTileX = TileMapRenderer.pixelsToTiles(
	//				toX + sprite.getWidth() - 1);
	//		int toTileY = TileMapRenderer.pixelsToTiles(
	//				toY + sprite.getHeight() - 1);
	//
	//		// check each tile for a collision
	//		for (int x=fromTileX; x<=toTileX; x++) {
	//			for (int y=fromTileY; y<=toTileY; y++) {
	//				if (x < 0 || x >= map.getWidth() ||
	//						map.getTile(x, y) != null)
	//				{
	//					// collision found, return the tile
	//					pointCache.setLocation(x, y);
	//					return pointCache;
	//				}
	//			}
	//		}
	//
	//		// no collision found
	//		return null;
	//	}




	/**
        Checks if two Sprites collide with one another. Returns
        false if the two Sprites are the same. Returns false if
        one of the Sprites is a Creature that is not alive.
	 */
	public boolean isCollision(Sprite s1, Sprite s2) {
		// if the Sprites are the same, return false
		if (s1 == s2) {
			return false;
		}

		// if one of the Sprites is a dead Creature, return false
		if (s1 instanceof Creature && !((Creature)s1).isAlive()) {
			return false;
		}
		if (s2 instanceof Creature && !((Creature)s2).isAlive()) {
			return false;
		}

		// get the pixel location of the Sprites
		int s1x = Math.round(s1.getX());
		int s1y = Math.round(s1.getY());
		int s2x = Math.round(s2.getX());
		int s2y = Math.round(s2.getY());

		// check if the two sprites' boundaries intersect
		return (s1x < s2x + s2.getWidth() &&
				s2x < s1x + s1.getWidth() &&
				s1y < s2y + s2.getHeight() &&
				s2y < s1y + s1.getHeight());
	}


	/**
        Gets the Sprite that collides with the specified Sprite,
        or null if no Sprite collides with the specified Sprite.
	 */
	//	public Sprite getSpriteCollision(Sprite sprite) {
	//
	//		// run through the list of Sprites
	//		Iterator i = map.getSprites();
	//		while (i.hasNext()) {
	//			Sprite otherSprite = (Sprite)i.next();
	//			if (isCollision(sprite, otherSprite)) {
	//				// collision found, return the Sprite
	//				return otherSprite;
	//			}
	//		}
	//
	//		// no collision found
	//		return null;
	//	}


	/**
        Updates Animation, position, and velocity of all Sprites
        in the current map.
	 */
	public void update(long elapsedTime) {
		Creature player = (Creature)map.getPlayer();


		// player is dead! start map over
		if (player.getState() == Creature.STATE_DEAD) {
			//			map = resourceManager.reloadMap();
			ded = true;
			return;
		}

		// get keyboard/mouse input
		checkInput(elapsedTime);

		if(!paused){
			// update player
			updateCreature(player, elapsedTime);
			player.update(elapsedTime);

			map.updateBuildings(elapsedTime);

			// update other sprites
			//			Iterator i = map.getSprites();
			//			while (i.hasNext()) {
			//				Sprite sprite = (Sprite)i.next();
			//				if (sprite instanceof Creature) {
			//					Creature creature = (Creature)sprite;
			//					if (creature.getState() == Creature.STATE_DEAD) {
			//						i.remove();
			//					}
			//					else {
			//						updateCreature(creature, elapsedTime);
			//					}
			//				}
			//				// normal update
			//				sprite.update(elapsedTime);
			//			}

		}
	}


	/**
        Updates the creature, applying gravity for creatures that
        aren't flying, and checks collisions.
	 */
	private void updateCreature(Creature creature,
			long elapsedTime)
	{

		// apply gravity
		if (!creature.isFlying()) {
			creature.setVelocityY(creature.getVelocityY() +
					GRAVITY * elapsedTime);
		}

		// change x
		float dx = creature.getVelocityX();
		float oldX = creature.getX();
		float newX = oldX + dx * elapsedTime;
		Building building = getBuildingCollisionX(creature, creature.getX(), creature.getY());
		if(building==null){
			map.setVel(map.getVel()+0.001);
		}else if(creature.getY()>building.getY()){
			map.setVel(0);
			xCollision=true;
			creature.collideHorizontal();
		}
		//		Point tile =getTileCollision(creature, newX, creature.getY());
		//		if (tile == null) {
		//			creature.setX(newX);
		//		}
		//		else {
		//			// line up with the tile boundary
		//			if (dx > 0) {
		//				creature.setX(
		//						TileMapRenderer.tilesToPixels(tile.x) -
		//						creature.getWidth());
		//			}
		//			else if (dx < 0) {
		//				creature.setX(
		//						TileMapRenderer.tilesToPixels(tile.x + 1));
		//			}
		//			creature.collideHorizontal();
		//		}
		if (creature instanceof Player) {
			checkPlayerCollision((Player)creature, false);
		}

		// change y
		float dy = creature.getVelocityY();
		float oldY = creature.getY();
		float newY = oldY + dy * elapsedTime;
		building = getbuildingCollisionY(creature, creature.getX(), newY);
		if(building==null){
			((Player)creature).onGround=false;
			creature.setY(newY);
		}else {
			if(dy>0&&((Player)creature).onGround){
				creature.setY(building.getY()-creature.getHeight());
				creature.collideVertical();
			}else if(dy>0&&creature.getY()+creature.getHeight()<building.getY()){
				creature.setY(newY);
			}else if(dy>0&&creature.getY()+creature.getHeight()>=building.getY()){
				creature.setY(building.getY()-creature.getHeight());
				creature.collideVertical();
			}else{

				creature.setY(newY);
			}
			System.out.println("after dy="+creature.getVelocityY());
			System.out.println("after    "+((creature instanceof Player) ? ((Player)creature).onGround:"not palyer"));
		}


		//		tile = getTileCollision(creature, creature.getX(), newY);
		//		if (tile == null) {
		//			creature.setY(newY);
		//		}
		//		else {
		//			// line up with the tile boundary
		//			if (dy > 0) {
		//				creature.setY(
		//						TileMapRenderer.tilesToPixels(tile.y) -
		//						creature.getHeight());
		//			}
		//			else if (dy < 0) {
		//				creature.setY(
		//						TileMapRenderer.tilesToPixels(tile.y + 1));
		//			}
		//			creature.collideVertical();
		//		}
		if (creature instanceof Player) {
			boolean canKill = (oldY < creature.getY());
			checkPlayerCollision((Player)creature, canKill);
		}

	}


	private Building getBuildingCollisionX(Creature creature, float x, float y) {
		for(int i = 0; i<2; i++){
			if(map.buildings.get(i).getX()<=x+creature.getWidth()){
				if(map.buildings.get(i).getY()<y+creature.getHeight()){
					return map.buildings.get(i);
				}return null;
			}return null;
		}return null;
	}


	private Building getbuildingCollisionY(Creature creature, float f, float newY) {
		for(int i = 0; i<2; i++){
			double bldgMinX = map.buildings.get(i).getX();
			double bldgMaxX=map.buildings.get(i).getEndPoint();
			if(bldgMinX<(creature.getX()+(creature.getWidth()/2))&&bldgMaxX>creature.getX()+(creature.getWidth()/2)&&!xCollision){
				if(map.buildings.get(i).getY()<=creature.getY()+creature.getHeight()){
					return map.buildings.get(i);
				}else return null;
			}else return null;
		}
		return null;
	}


	/**
        Checks for Player collision with other Sprites. If
        canKill is true, collisions with Creatures will kill
        them.
	 */
	public void checkPlayerCollision(Player player,
			boolean canKill)
	{
		if (!player.isAlive()) {
			return;
		}
		if(player.getY()+player.getHeight()>screen.getHeight()){
			player.setState(2);
		}

		// check for player collision with other sprites
		//		Sprite collisionSprite = getSpriteCollision(player);
		//		if (collisionSprite instanceof PowerUp) {
		//			acquirePowerUp((PowerUp)collisionSprite);
		//		}
		//		else if (collisionSprite instanceof Creature) {
		//			Creature badguy = (Creature)collisionSprite;
		//			if (canKill) {
		//				// kill the badguy and make player bounce
		//				soundManager.play(boopSound);
		//				badguy.setState(Creature.STATE_DYING);
		//				player.setY(badguy.getY() - player.getHeight());
		//				player.jump(true);
		//			}
		//			else {
		//				// player dies!
		//				player.setState(Creature.STATE_DYING);
		//			}
		//		}
	}


	/**
        Gives the player the speicifed power up and removes it
        from the map.
	 */
	public void acquirePowerUp(PowerUp powerUp) {
		// remove it from the map
		map.removeSprite(powerUp);

		if (powerUp instanceof PowerUp.Star) {
			// do something here, like give the player points
			soundManager.play(prizeSound);
		}
		else if (powerUp instanceof PowerUp.Music) {
			// change the music
			soundManager.play(prizeSound);
			toggleDrumPlayback();
		}
		else if (powerUp instanceof PowerUp.Goal) {
			// advance to next map
			soundManager.play(prizeSound,
					new EchoFilter(2000, .7f), false);
			//			map = resourceManager.loadNextMap();
		}
	}

}

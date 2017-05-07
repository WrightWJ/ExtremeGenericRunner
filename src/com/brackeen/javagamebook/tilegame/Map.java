package com.brackeen.javagamebook.tilegame;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Iterator;

import com.brackeen.javagamebook.graphics.Sprite;

public class Map {

	private double bldgVel = 0.5;
	ArrayList<Building> buildings = new ArrayList<Building>();
    private Sprite player;
	private ArrayList<Sprite> sprites;


	/**
    Gets the player Sprite.
	 */
	public Sprite getPlayer() {
		return player;
	}


	/**
    Sets the player Sprite.
	 */
	public void setPlayer(Sprite player) {
		this.player = player;
	}


	/**
    Adds a Sprite object to this map.
	 */
	public void addSprite(Sprite sprite) {
		sprites.add(sprite);
	}


	/**
    Removes a Sprite object from this map.
	 */
	public void removeSprite(Sprite sprite) {
		sprites.remove(sprite);
	}


	/**
    Gets an Iterator of all the Sprites in this map,
    excluding the player Sprite.
	 */
	public Iterator getSprites() {
		return sprites.iterator();
	}
}

package com.brackeen.javagamebook.tilegame;


import java.util.ArrayList;


import com.brackeen.javagamebook.graphics.Sprite;

public class Map {

	private double bldgVel = 0.5;
	public ArrayList<Building> buildings = new ArrayList<Building>();
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
	
	public void setVel(double newVel){
		this.bldgVel=newVel;
	}


	/**
    Gets an Iterator of all the Sprites in this map,
    excluding the player Sprite.
	 */
//	public Iterator getSprites() {
//		return sprites.iterator();
//	}
	
	public void init(){
		double lastBldg = 0;
		for(int i = 0; i<5; i++){
			
			buildings.add(new Building(bldgVel, lastBldg));
			lastBldg=buildings.get(i).getEndPoint();
		}
	}


	public void updateBuildings(long elapsedTime) {
		if(buildings.get(0).getEndPoint()<0){
			this.buildings.remove(0);
			this.buildings.add(new Building(bldgVel, buildings.get(buildings.size()-1).getEndPoint()));
		}
		for(Building bldg:buildings){
			bldg.updatePos(elapsedTime, this.bldgVel);
		}
	}


	public double getVel() {
		return bldgVel;
	}
}

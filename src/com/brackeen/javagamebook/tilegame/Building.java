package com.brackeen.javagamebook.tilegame;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

import javax.imageio.ImageIO;

public class Building {
	private double x;
	private double y;
	int bldgWidth;
	int numMidPieces;
	

	
	public static final int WIDTH_LEFT_PIECE = 80;
	public static final int WIDTH_RIGHT_PIECE = 81;
	public static final int WIDTH_MID_PIECE = 78;
	public static ArrayList<BufferedImage> buildingMidPieces = new ArrayList<BufferedImage>();
	public static ArrayList<BufferedImage> buildingLeftPieces = new ArrayList<BufferedImage>();
	public static ArrayList<BufferedImage> buildingRightPieces = new ArrayList<BufferedImage>();
	Random rand = new Random();
	private double bldgVel;
	private double lastBldg;
	public BufferedImage[] bldgImg;

	int bldgLength = rand.nextInt(9)+2;


	public Building(double bldgVel, double lastBldg){
		this.bldgVel=bldgVel;
		this.lastBldg=lastBldg;
		bldgImg = new BufferedImage[bldgLength+2];
		generate();
	}

	public void generate(){
		this.y=rand.nextInt(75)+350;
		this.x=this.lastBldg+rand.nextInt(200)+(bldgVel*250);
		bldgImg[0]=getLeftPiece();
		bldgImg[bldgImg.length-1]=getRightPiece();
		for(int i = 1; i<bldgLength; i++){
			bldgImg[i]=getMidPiece();
		}
	}
	
	
	public static void loadLeftPieces() {
		int i = 1;
		boolean morePieces=true;
		while(morePieces){
			BufferedImage img;
			try {
				img = ImageIO.read(new File("images/buildings/BLDG_Left_"+i+".png"));
				buildingLeftPieces.add(img);
				System.out.println("Success!");
			} catch (IOException e1) {
				morePieces=false;
			}
			i++;
		}		
	}

	public static void loadRightPieces() {
		int i = 1;
		boolean morePieces=true;
		while(morePieces){
			BufferedImage img;
			try {
				img = ImageIO.read(new File("images/buildings/BLDG_Right_"+i+".png"));
				buildingRightPieces.add(img);
				System.out.println("Success!");
			} catch (IOException e1) {
				morePieces=false;
			}
			i++;
		}		
	}

	public static void loadMidPieces(){
		int i = 1;
		boolean morePieces=true;
		while(morePieces){
			BufferedImage img;
			try {
				img = ImageIO.read(new File("images/buildings/BLDG_Middle_"+i+".png"));
				buildingMidPieces.add(img);
				System.out.println("Success!");
			} catch (IOException e1) {
				morePieces=false;
			}

			i++;
		}
	}
	
	public BufferedImage getLeftPiece(){
		return buildingLeftPieces.get(0); //change to random index if more than one option
	}

	public BufferedImage getMidPiece(){
		return buildingMidPieces.get(rand.nextInt(buildingMidPieces.size()));
	}
	
	public BufferedImage getRightPiece(){
		return buildingRightPieces.get(0);	//change to random index if more than one option
	}

	public void updatePos(long elapsedTime, double bldgVel) {
		this.bldgVel=bldgVel;
		this.x=this.x-(bldgVel*elapsedTime);

	}
	
	public double getX() {
		return this.x;
	}
	
	public double getY() {
		return this.y;
	}
	

	public int getWidth(){
		return WIDTH_LEFT_PIECE+(WIDTH_MID_PIECE*bldgLength)+WIDTH_RIGHT_PIECE;
	}

	public int getLength(){
		return this.bldgLength;
	}

	public double getEndPoint() {
		return x+getWidth();
	}

	public double getVel() {
		// TODO Auto-generated method stub
		return bldgVel;
	}

	




}


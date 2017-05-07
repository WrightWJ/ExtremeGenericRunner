package com.brackeen.javagamebook.tilegame;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.util.Iterator;

import com.brackeen.javagamebook.graphics.Sprite;
import com.brackeen.javagamebook.tilegame.sprites.Creature;

public class BuildingRenderer {
	
    private Image background;
	private int backgroundX;

    public void setBackground(Image background) {
        this.background = background;
    }
    
	public void draw(Graphics2D g, Map map,
			int screenWidth, int screenHeight){

        // draw black background, if needed
        if (background == null ||
            screenHeight > background.getHeight(null))
        {
            g.setColor(Color.black);
            g.fillRect(0, 0, screenWidth, screenHeight);
        }

        // draw parallax background image
        if (background != null) {
            int x = backgroundX;
            int y = screenHeight - background.getHeight(null);

            g.drawImage(background, x, y, null);
        }

        
		// draw sprites
		Iterator i = map.getSprites();
		while (i.hasNext()) {
			Sprite sprite = (Sprite)i.next();
			int x = Math.round(sprite.getX());
			int y = Math.round(sprite.getY());
			g.drawImage(sprite.getImage(), x, y, null);

			// wake up the creature when it's on screen
			if (sprite instanceof Creature &&x >= 0 && x < screenWidth)
			{
				((Creature)sprite).wakeUp();
			}
		}
	}

}

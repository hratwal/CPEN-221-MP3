package ca.ubc.ece.cpen221.mp4.items.buildings;

import javax.swing.ImageIcon;

import ca.ubc.ece.cpen221.mp4.Location;
import ca.ubc.ece.cpen221.mp4.Util;
import ca.ubc.ece.cpen221.mp4.items.Item;

/**
 * Spiderweb will be built by the {@link Human} whenever the Human has enough energy
 */

public class SpiderWeb implements Item {
    private final static ImageIcon spiderWeb = Util.loadImage("web.gif");
    private static final int INITIAL_ENERGY = 20;
    private static final int STRENGTH = 20;
    private int energy = INITIAL_ENERGY; 
    private Location location;
    private boolean isDead;

    /**
     * Build a spiderweb at <code> location </code>. The location must be valid and
     * empty
     *
     * @param location
     *            : the location where this grass will be created
     */
    public void SpiderWeb(Location location) {
        this.location = location;
        this.isDead = false;
    }

    @Override
    public ImageIcon getImage() {
        return spiderWeb;
    }

    @Override
    public String getName() {
        return "condo";
    }

    @Override
    public Location getLocation() {
        return location;
    }

    @Override
    public int getPlantCalories() {
        return 0;
    }

    @Override
    public int getMeatCalories() {
        return 0;
    }

    @Override
    public void loseEnergy(int energy) {
        this.energy -= energy;
    }

    @Override
    public boolean isDead() {
        return isDead;
    }
    
    @Override
    public int getStrength(){
        return STRENGTH;
    }

}
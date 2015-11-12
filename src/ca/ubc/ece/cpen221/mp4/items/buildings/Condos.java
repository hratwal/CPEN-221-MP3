package ca.ubc.ece.cpen221.mp4.items.buildings;

import javax.swing.ImageIcon;

import ca.ubc.ece.cpen221.mp4.Location;
import ca.ubc.ece.cpen221.mp4.Util;
import ca.ubc.ece.cpen221.mp4.ai.AI;
import ca.ubc.ece.cpen221.mp4.items.Item;

/**
 * Condos will be built by the {@link Human} whenever the Human has enough energy
 */
public class Condos implements Item {
	private final static ImageIcon condoImage = Util.loadImage("hunter.gif");
	private static final int INITIAL_ENERGY = 500;
	private static final int STRENGTH = 1000;
	private int energy = INITIAL_ENERGY; 
	private Location location;
	private boolean isDead;
	private final AI ai;

	/**
	 * Build a condo at <code> location </code>. The location must be valid and
	 * empty
	 *
	 * @param location
	 *            : the location where this grass will be created
	 */
	public Condos(AI buildingAI, Location location) {
		ai = buildingAI;
		this.location = location;
		this.isDead = false;
	}

	@Override
	public ImageIcon getImage() {
		return condoImage;
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
	public int getStrength(){
		return STRENGTH; 
	}
	
	@Override
	public void loseEnergy(int energy) {
		this.energy -= energy;
	}

	@Override
	public boolean isDead() {
		return isDead;
	}

}
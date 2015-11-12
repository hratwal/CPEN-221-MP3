package ca.ubc.ece.cpen221.mp4.items.buildings;

import javax.swing.ImageIcon;

import ca.ubc.ece.cpen221.mp4.Location;
import ca.ubc.ece.cpen221.mp4.Util;
import ca.ubc.ece.cpen221.mp4.items.Item;

/**
 * Factory will be built by the {@link Human} whenever the Human has enough energy
 */
public class Factories implements Item {
	private final static ImageIcon factoryImage = Util.loadImage("tiger.gif");
	private static final int INITIAL_ENERGY = 250;
	private static final int STRENGTH = 1000;
	private int energy = INITIAL_ENERGY; 
	private Location location;
	private boolean isDead;

	/**
	 * Build a factory at <code> location </code>. The location must be valid and
	 * empty
	 *
	 * @param location
	 *            : the location where this grass will be created
	 */
	public Factories(Location location) {
		this.location = location;
		this.isDead = false;
	}

	@Override
	public ImageIcon getImage() {
		return factoryImage;
	}

	@Override
	public String getName() {
		return "factory";
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
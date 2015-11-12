package ca.ubc.ece.cpen221.mp4.items.animals;

import javax.swing.ImageIcon;

import ca.ubc.ece.cpen221.mp4.Food;
import ca.ubc.ece.cpen221.mp4.Location;
import ca.ubc.ece.cpen221.mp4.Util;
import ca.ubc.ece.cpen221.mp4.World;
import ca.ubc.ece.cpen221.mp4.ai.AI;
import ca.ubc.ece.cpen221.mp4.ai.PredatorAI;
import ca.ubc.ece.cpen221.mp4.commands.Command;
import ca.ubc.ece.cpen221.mp4.items.LivingItem;

/**
 * The {@link Spider} is an {@link ArenaAnimal} that tries to eat {@link Gnat}s.
 */
public class Spider implements ArenaAnimal {

	private static final int INITIAL_ENERGY = 5;
	private static final int MAX_ENERGY = 10;
	private static final int STRENGTH = 10;
	private static final int VIEW_RANGE = 2;
	private static final int MIN_BREEDING_ENERGY = 5;
	private static final int COOLDOWN = 1;
	private static final ImageIcon spiderImage = Util.loadImage("spider.gif");

	private final AI ai;

	private Location location;
	private int energy;

	/**
	 * Create a new {@link Spider} with an {@link PredatorAI} at
	 * <code>initialLocation</code>. The <code> initialLocation </code> must be
	 * valid and empty
	 *
	 * @param predatorAI
	 *            the AI designed for predators
	 * @param initialLocation
	 *            the location where this Fox will be created
	 */
	public Spider(AI predatorAI, Location initialLocation) {
		this.ai = predatorAI;
		this.location = initialLocation;
		this.energy = INITIAL_ENERGY;
	}

	@Override
	public LivingItem breed() {
		Spider child = new Spider(ai, location);
		child.energy = energy / 2;
		this.energy = energy / 2;
		return child;
	}

	@Override
	public void eat(Food food) {
		// Note that energy does not exceed energy limit.
		energy = Math.min(MAX_ENERGY, energy + food.getMeatCalories());
	}

	@Override
	public int getCoolDownPeriod() {
		return COOLDOWN;
	}

	@Override
	public int getEnergy() {
		return energy;
	}

	@Override
	public ImageIcon getImage() {
		return spiderImage;
	}

	@Override
	public Location getLocation() {
		return location;
	}

	@Override
	public int getMaxEnergy() {
		return MAX_ENERGY;
	}

	@Override
	public int getMeatCalories() {
		// The amount of meat calories it provides is equal to its current
		// energy.
		return energy;
	}

	@Override
	public int getMinimumBreedingEnergy() {
		return MIN_BREEDING_ENERGY;
	}

	@Override
	public int getMovingRange() {
		return 1; // Can only move to adjacent locations.
	}

	@Override
	public String getName() {
		return "Spider";
	}

	@Override
	public Command getNextAction(World world) {
		Command nextAction = ai.getNextAction(world, this);
		this.energy--; // Loses 1 energy regardless of action.
		return nextAction;
	}

	@Override
	public int getPlantCalories() {
		// This Spider is not a plant.
		return 0;
	}

	@Override
	public int getStrength() {
		return STRENGTH;
	}

	@Override
	public int getViewRange() {
		return VIEW_RANGE;
	}

	@Override
	public boolean isDead() {
		return energy <= 0;
	}

	@Override
	public void loseEnergy(int energyLoss) {
		this.energy = this.energy - energyLoss;
	}

	@Override
	public void moveTo(Location targetLocation) {
		location = targetLocation;
	}
}
package ca.ubc.ece.cpen221.mp4.items.vehicles;

import java.util.Set;

import javax.swing.ImageIcon;

import ca.ubc.ece.cpen221.mp4.Direction;
import ca.ubc.ece.cpen221.mp4.Location;
import ca.ubc.ece.cpen221.mp4.ai.AI;
import ca.ubc.ece.cpen221.mp4.ai.VehicleAI;
import ca.ubc.ece.cpen221.mp4.Util;

public class Truck extends Vehicle{

    private static final int INITIAL_ENERGY = 200;
    private static final int STRENGTH = 700;
    private static final int VIEW_RANGE = 4;
    private static final int START_COOLDOWN = 6;
    private static final int MIN_COOLDOWN = 3;
    private static final ImageIcon IMAGE = Util.loadImage("trucks.gif");
    private static final String NAME = "Truck";
    private static final int ACCEL_TURNS = 3;
    
    private VehicleAI ai;

    private Location location;
    private int energy = INITIAL_ENERGY;
    private boolean isDead = false;
    private int currentCooldown = START_COOLDOWN;
    
    /**
     * Create a new {@link Truck} with an {@link AI} at
     * <code>initialLocation</code>. The <code> initialLocation </code> must be
     * valid and empty
     *
     * @param truckAI
     *            the AI designed for trucks
     * @param initialLocation
     *            the location where this Truck will be created
     */
    public Truck(VehicleAI truckAI, Location initialLocation) {
        super.setINITIAL_ENERGY(INITIAL_ENERGY);
        super.setSTRENGTH(STRENGTH);
        super.setVIEW_RANGE(VIEW_RANGE);
        super.setLocation(initialLocation);
        super.setIMAGE(IMAGE);
        super.setNAME(NAME);
        super.setACCEL_TURNS(ACCEL_TURNS);
        super.setSTART_COOLDOWN(START_COOLDOWN);
        super.setMIN_COOLDOWN(MIN_COOLDOWN);
        super.setCurrentCooldown(START_COOLDOWN);
        super.setEnergy(INITIAL_ENERGY);
        super.setIsDead(false);
        
        super.setAI(truckAI);
        super.setDirection(super.randomDirection());
    }
    
}

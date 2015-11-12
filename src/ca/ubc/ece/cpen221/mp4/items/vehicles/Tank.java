package ca.ubc.ece.cpen221.mp4.items.vehicles;

import java.util.Set;

import javax.swing.ImageIcon;

import ca.ubc.ece.cpen221.mp4.Direction;
import ca.ubc.ece.cpen221.mp4.Location;
import ca.ubc.ece.cpen221.mp4.ai.AI;
import ca.ubc.ece.cpen221.mp4.ai.VehicleAI;
import ca.ubc.ece.cpen221.mp4.items.animals.Fox;
import ca.ubc.ece.cpen221.mp4.Util;

public class Tank extends Vehicle{
    
    private static final int INITIAL_ENERGY = 200;
    private static final int STRENGTH = 1200;
    private static final int VIEW_RANGE = 6;
    private static final int START_COOLDOWN = 12;
    private static final int MIN_COOLDOWN = 6;
    private static final ImageIcon IMAGE = Util.loadImage("tank.gif");
    private static final String NAME = "Tank";
    private static final int ACCEL_TURNS = 6;
    
    private VehicleAI ai;

    private Location location;
    private int energy = INITIAL_ENERGY;
    private boolean isDead = false;
    private int currentCooldown = START_COOLDOWN;
    
    /**
     * Create a new {@link Tank} with an {@link AI} at
     * <code>initialLocation</code>. The <code> initialLocation </code> must be
     * valid and empty
     *
     * @param tankAI
     *            the AI designed for tanks
     * @param initialLocation
     *            the location where this Tank will be created
     */
    public Tank(VehicleAI tankAI, Location initialLocation) {
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
        
        super.setAI(tankAI);
        super.setDirection(super.randomDirection());
    }
}
package ca.ubc.ece.cpen221.mp4.items.vehicles;

import javax.swing.ImageIcon;
import java.util.*;

import ca.ubc.ece.cpen221.mp4.Actor;
import ca.ubc.ece.cpen221.mp4.Direction;
import ca.ubc.ece.cpen221.mp4.Location;
import ca.ubc.ece.cpen221.mp4.World;
import ca.ubc.ece.cpen221.mp4.ai.AI;
import ca.ubc.ece.cpen221.mp4.ai.VehicleAI;
import ca.ubc.ece.cpen221.mp4.commands.Command;
import ca.ubc.ece.cpen221.mp4.items.*;


public abstract class Vehicle implements MoveableItem, Actor{
    //abstract class for all vehicles
    
    private int INITIAL_ENERGY;
    private int STRENGTH;
    private int VIEW_RANGE;
    private int START_COOLDOWN;
    private int MIN_COOLDOWN;
    private ImageIcon IMAGE;
    private String NAME;
    private int ACCEL_TURNS;
    
    private VehicleAI ai;
    private boolean isDead;
    private Location location;
    private int energy;
    private int currentCooldown;
    private Direction dir;
    private int stepsSinceTurn = 0;
    
    public int getCoolDownPeriod(){
        return currentCooldown;
    }
    
    /**
     * Vehicles cannot be eaten and do not provide any calories to anything else.
     * 
     * This method must be here to satisfy the Food interface.
     * 
     * @return 0
     */
    public int getMeatCalories(){
        return 0;
    }
    
    /**
     * Vehicles cannot be eaten and do not provide any calories to anything else.
     * 
     * This method must be here to satisfy the Food interface.
     * 
     * @return 0
     */
    public int getPlantCalories(){
        return 0;
    }
    
    /**
     * Move to the target location. The <code> targetLocation </code> must be
     * valid and empty.
     *
     * @param targetLocation
     *            the location that this item is moving to
     */
    public void moveTo(Location targetLocation){
        location = targetLocation;
    }
    
    /**
     * Reduces cooldown by 1/ACCEL_TURNS * (START_COOLDOWN - MIN_COOLDOWN)
     */
    public void accelerate(){
        if(currentCooldown > MIN_COOLDOWN){
            int newCooldown = currentCooldown - (int)(1.0/ACCEL_TURNS * 
                    (START_COOLDOWN - MIN_COOLDOWN));
            currentCooldown = Math.min(MIN_COOLDOWN, newCooldown);
        }
    }


    /**
     * Increases cooldown by 1/ACCEL_TURNS * (START_COOLDOWN - MIN_COOLDOWN)
     */
    public void decelerate(){
        if(currentCooldown < START_COOLDOWN){
            int newCooldown = currentCooldown + (int)(1.0/ACCEL_TURNS * 
                    (START_COOLDOWN - MIN_COOLDOWN));
            currentCooldown = Math.min(START_COOLDOWN, newCooldown);
        }
    }
    
    /**
     * Returns the maximum distance that this item can move in one step. For
     * example, a {@link MoveableItem} with moving range 1 can only move to
     * adjacent locations.
     *
     * For all Vehicles, their movement range is always 1. When they change 
     * speeds by accelerating or decelerating, their cooldown time changes
     * instead of movement range changing. 
     * 
     * @return the maximum moving distance
     */
    public int getMovingRange(){
        return 1;
    }
    
    /**
     * The visualization of this Item in the world.
     *
     * @return the image of this Item
     */
    public ImageIcon getImage(){
        return IMAGE;
    }

    /**
     * Gets a String that serves as a unique identifier for this type of Item.
     *
     * @return the name of this item
     */
    public String getName(){
        return NAME;
    }

    /**
     * Gets the location of this Item in the World.
     *
     * @return the location in the world
     */
    public Location getLocation(){
        return location;
    }

    /**
     * Returns the strength of this Item. Generally, if an item possesses
     * greater strength than another, then it can eliminate the other. For
     * example, a {@link Vehicle} can run over everything that has a lower
     * strength.
     *
     * @return the strength of this Item
     */
    public int getStrength(){
        return STRENGTH;
    }
    
    /**
     * Returns the viewing range of this Vehicle. The viewing range (in Manhattan
     * distance) gives the distance that the vehicle can see. Vehicles can't 
     * react to Items outside their viewing range.
     * 
     * @return the viewing range of this Vehicle
     */
    public int getViewRange(){
        return VIEW_RANGE;
    }
    
    public int getCurrentCooldown(){
        return currentCooldown;
    }
    
    public int getMAX_COOLDOWN(){
        return START_COOLDOWN;
    }
    
    public int getMIN_COOLDOWN(){
        return MIN_COOLDOWN;
    }
    
    public Direction getDir(){
        return dir;
    }
    
    public int getINITIAL_ENERGY(){
        return INITIAL_ENERGY;
    }
    
    public int getACCEL_TURNS(){
        return ACCEL_TURNS;
    }
    
    /**
     * 
     * @return number of steps since the vehicle turned
     */
    public int getStepsSinceTurn(){
        return stepsSinceTurn;
    }
    
    public void resetStepsSinceTurn(){
        stepsSinceTurn = 0;
    }
    
    public void incrementStepsSinceTurn(){
        stepsSinceTurn++;
    }
    
    

    /**
     * Causes this Item to lose energy. 
     *
     * @param energy
     *            the amount of energy lost
     */
    public void loseEnergy(int energyLoss) {
        this.energy -= energyLoss;
    }

    /**
     * Returns whether or not this Item is dead. If this Item is dead, it will
     * be removed from the World. An item is dead if it is eaten, run over by a
     * Vehicle, loses all its energy and energy level drops below or equal 0,
     * etc.
     *
     * @return true if this Item is dead, false if alive
     */
    public boolean isDead(){
        return isDead;
    }
    
    public Command getNextAction(World world) {
        Command nextAction = ai.getNextAction(world, this);
        this.energy--; // Loses 1 energy regardless of action.
        if(energy <= 0){
            isDead = true;
        }
        return nextAction;
    }
    
    protected void setINITIAL_ENERGY(int i) {
        this.INITIAL_ENERGY = i;
    }

    protected void setEnergy(int i) {
        this.energy = i;
    }

    protected void setSTRENGTH(int i) {
        this.STRENGTH = i;
    }

    protected void setVIEW_RANGE(int i) {
        this.VIEW_RANGE = i;
    }

    protected void setCurrentCooldown(int i) {
        this.currentCooldown = i;
    }

    protected void setLocation(Location l) {
        this.location = l;
    }
    
    protected void setSTART_COOLDOWN(int i){
        this.START_COOLDOWN = i;
    }
    
    protected void setMIN_COOLDOWN(int i){
        this.MIN_COOLDOWN = i;
    }
    
    protected void setACCEL_TURNS(int i){
        this.ACCEL_TURNS = i;
    }
    
    protected void setIMAGE(ImageIcon image){
        this.IMAGE = image;
    }
    
    protected void setNAME(String name){
        this.NAME = name;
    }
    
    protected void setIsDead(boolean isDead){
        this.isDead = isDead;
    }
    
    protected void setAI(VehicleAI ai){
        this.ai = ai;
    }
    
    public void setDirection(Direction dir){
        this.dir = dir;
    }
    
    public Direction randomDirection(){
        ArrayList<Direction> directions = new ArrayList<Direction>();
        directions.add(Direction.EAST);
        directions.add(Direction.WEST);
        directions.add(Direction.NORTH);
        directions.add(Direction.SOUTH);
        double rand = Math.random() * 4;
        return directions.get((int) rand);
    }
}
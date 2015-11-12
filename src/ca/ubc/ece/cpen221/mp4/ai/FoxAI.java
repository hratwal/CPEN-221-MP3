package ca.ubc.ece.cpen221.mp4.ai;

import java.util.*;

import ca.ubc.ece.cpen221.mp4.ArenaWorld;
import ca.ubc.ece.cpen221.mp4.Direction;
import ca.ubc.ece.cpen221.mp4.Location;
import ca.ubc.ece.cpen221.mp4.Util;
import ca.ubc.ece.cpen221.mp4.World;
import ca.ubc.ece.cpen221.mp4.commands.BreedCommand;
import ca.ubc.ece.cpen221.mp4.commands.Command;
import ca.ubc.ece.cpen221.mp4.commands.EatCommand;
import ca.ubc.ece.cpen221.mp4.commands.MoveCommand;
import ca.ubc.ece.cpen221.mp4.commands.WaitCommand;
import ca.ubc.ece.cpen221.mp4.items.Item;
import ca.ubc.ece.cpen221.mp4.items.animals.*;

/**
 * Your Fox AI.
 */
public class FoxAI extends ArenaAnimalAI{
    private int energy;
    
    public FoxAI(){
        super(200);
    }
    
    @Override
    public Command getNextAction(ArenaWorld world, ArenaAnimal predator) {
        Set<Item> surroundingThings = world.searchSurroundings(predator);
        Set<Location> dangers = identifyDangers(world, predator, surroundingThings);
        Set<Location> foods = identifyFood(world, predator, surroundingThings);
        if (dangers.size() > 0 && predator.getEnergy() > (predator.getMaxEnergy() / 10)) {
            return runAway(world, predator, dangers);
        }	else {
        		if(predator.getEnergy() > predator.getMinimumBreedingEnergy()){
        			return breed(world, predator, foods, surroundingThings);
        		}	else {
        			return tryToEat(world, predator, foods, surroundingThings);
        			}
        }
    }
    
    /**
     * returns a set of locations that contain potential food Items in 
     * range of the predator
     * @param world: the world the predator exists in
     * @param surroundings: a set of all Items that the predator can see
     * @return: a set of locations containing edible items within eyesight
     * of the predator
     */
    private Set<Location> identifyFood(ArenaWorld world, ArenaAnimal predator, 
            Set<Item> surroundings) {
        Set<Location> foodZones = new HashSet<Location>();
        Iterator<Item> it = surroundings.iterator();
        while (it.hasNext()) {
            Item item = it.next();
            if (item.getStrength() < predator.getStrength()
                    && item.getMeatCalories() > 0
                    && !item.getName().equals("Gnat")
                    && !item.getName().equals("Spider")) { //Should foxes eat spiders?
                foodZones.add(item.getLocation());
            }
        }
        return foodZones;
    }
    
}
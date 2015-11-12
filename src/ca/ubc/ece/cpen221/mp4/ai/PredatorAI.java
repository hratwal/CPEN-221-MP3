package ca.ubc.ece.cpen221.mp4.ai;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import ca.ubc.ece.cpen221.mp4.ArenaWorld;
import ca.ubc.ece.cpen221.mp4.Location;
import ca.ubc.ece.cpen221.mp4.commands.Command;
import ca.ubc.ece.cpen221.mp4.items.Item;
import ca.ubc.ece.cpen221.mp4.items.animals.ArenaAnimal;

public class PredatorAI extends ArenaAnimalAI{
    private int energy;
    
    public PredatorAI(){
        super(200);
    }
    
    @Override
    public Command getNextAction(ArenaWorld world, ArenaAnimal predator) {
        Set<Item> surroundingThings = world.searchSurroundings(predator);
        Set<Location> dangers = identifyDangers(world, predator, surroundingThings);
        Set<Location> foods = identifyFood(world, predator, surroundingThings);
        if (dangers.size() > 0 && predator.getEnergy() > (predator.getMaxEnergy() / 10)) {
            return runAway(world, predator, dangers);
        }else if(predator.getEnergy() > predator.getMinimumBreedingEnergy()){
            return breed(world, predator, foods, surroundingThings);
        }
        return tryToEat(world, predator, foods, surroundingThings);
    }
    
    /**
     * returns a set of locations that contain potential food Items in 
     * eyesight of the predator
     * @param world: the world the predator exists in
     * @param human: the predator in question
     * @param surroundings: a set of all Items that the predator can see
     * @return: a set of locations containing edible items within eyesight
     * of the predator
     */
    protected Set<Location> identifyFood(ArenaWorld world, ArenaAnimal predator, 
            Set<Item> surroundings) {
        Set<Location> foodZones = new HashSet<Location>();
        Iterator<Item> it = surroundings.iterator();
        while (it.hasNext()) {
            Item item = it.next();
            if (item.getStrength() < predator.getStrength()
                    && item.getMeatCalories() > 0
                    && !item.getName().equals("Gnat")
                    /*&& !item.getName().equals("Spider")*/) {
                foodZones.add(item.getLocation());
            }
        }
        return foodZones;
    }
    
}
package ca.ubc.ece.cpen221.mp4.ai;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import ca.ubc.ece.cpen221.mp4.ArenaWorld;
import ca.ubc.ece.cpen221.mp4.Direction;
import ca.ubc.ece.cpen221.mp4.Location;
import ca.ubc.ece.cpen221.mp4.Util;
import ca.ubc.ece.cpen221.mp4.commands.BreedCommand;
import ca.ubc.ece.cpen221.mp4.commands.Command;
import ca.ubc.ece.cpen221.mp4.commands.EatCommand;
import ca.ubc.ece.cpen221.mp4.commands.MoveCommand;
import ca.ubc.ece.cpen221.mp4.commands.WaitCommand;
import ca.ubc.ece.cpen221.mp4.items.Item;
import ca.ubc.ece.cpen221.mp4.items.animals.ArenaAnimal;
import ca.ubc.ece.cpen221.mp4.items.animals.Fox;
import ca.ubc.ece.cpen221.mp4.items.animals.Rabbit;

/**
 * Your Rabbit AI.
 */
public class RabbitAI extends ArenaAnimalAI {
    private int energy = 200;

	public RabbitAI() {
	    super(200);
	}

	@Override
	public Command getNextAction(ArenaWorld world, ArenaAnimal rabbit) {
        Set<Item> surroundingThings = world.searchSurroundings(rabbit);
        Set<Location> dangers = identifyDangers(world, rabbit, surroundingThings);
        Set<Location> foods = identifyFood(world, rabbit, surroundingThings);
        if (dangers.size() > 0 && rabbit.getEnergy() > (rabbit.getMaxEnergy() / 10)) {
            return runAway(world, rabbit, dangers);
        }else if(rabbit.getEnergy() > rabbit.getMinimumBreedingEnergy()){
            return breed(world, rabbit, foods, surroundingThings);
        }
        return tryToEat(world, rabbit, foods, surroundingThings);
    }
	
	/**
     * returns a set of locations that contain potential food Items in 
     * eyesight of the rabbit
     * @param world: the world the rabbit exists in
     * @param human: the rabbit in question
     * @param surroundings: a set of all Items that the rabbit can see
     * @return: a set of locations containing edible items within eyesight
     * of the human
     */
    private Set<Location> identifyFood(ArenaWorld world, ArenaAnimal rabbit, 
            Set<Item> surroundings) {
        Set<Location> foodZones = new HashSet<Location>();
        Iterator<Item> it = surroundings.iterator();
        while (it.hasNext()) {
            Item item = it.next();
            if (item.getPlantCalories() > 0) {
                foodZones.add(item.getLocation());
            }
        }
        return foodZones;
    }
}

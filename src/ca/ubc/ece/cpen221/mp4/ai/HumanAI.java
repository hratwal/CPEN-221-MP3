package ca.ubc.ece.cpen221.mp4.ai;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import ca.ubc.ece.cpen221.mp4.items.animals.*;
import ca.ubc.ece.cpen221.mp4.items.buildings.*;
import ca.ubc.ece.cpen221.mp4.ArenaWorld;
import ca.ubc.ece.cpen221.mp4.Direction;
import ca.ubc.ece.cpen221.mp4.Direction.*;
import ca.ubc.ece.cpen221.mp4.Location;
import ca.ubc.ece.cpen221.mp4.Util;
import ca.ubc.ece.cpen221.mp4.World;
import ca.ubc.ece.cpen221.mp4.commands.BreedCommand;
import ca.ubc.ece.cpen221.mp4.commands.BuildCommand;
import ca.ubc.ece.cpen221.mp4.commands.Command;
import ca.ubc.ece.cpen221.mp4.commands.EatCommand;
import ca.ubc.ece.cpen221.mp4.commands.MoveCommand;
import ca.ubc.ece.cpen221.mp4.commands.WaitCommand;
import ca.ubc.ece.cpen221.mp4.items.*;

public class HumanAI extends ArenaAnimalAI {
    private int energy;
    private boolean breedTime;

    public HumanAI(int energy) {
        super(energy);
        breedTime = true;
    }

    // why does the ArenaAnimalAI constructor need energy? It's not used for
    // anything
    public HumanAI() {
        super(200);
        breedTime = true;
    }

    @Override
    public Command getNextAction(ArenaWorld world, ArenaAnimal human) {
        Set<Item> surroundingThings = world.searchSurroundings(human);
        Set<Location> dangers = identifyDangers(world, human, surroundingThings);
        Set<Location> foods = identifyFood(world, human, surroundingThings);
        if (dangers.size() > 0 && human.getEnergy() > (human.getMaxEnergy() / 10)) {
            return runAway(world, human, dangers);
        }else if(human.getEnergy() > human.getMinimumBreedingEnergy()){
            return breedOrBuild(world, human, foods, surroundingThings);
        }
        return tryToEat(world, human, foods, surroundingThings);
    }
    
    /**
     * returns a set of locations that contain potential food Items in 
     * eyesight of the human
     * @param world: the world the human exists in
     * @param human: the human in question
     * @param surroundings: a set of all Items that the human can see
     * @return: a set of locations containing edible items within eyesight
     * of the human
     */
    private Set<Location> identifyFood(ArenaWorld world, ArenaAnimal human, 
            Set<Item> surroundings) {
        Set<Location> foodZones = new HashSet<Location>();
        Iterator<Item> it = surroundings.iterator();
        while (it.hasNext()) {
            Item item = it.next();
            if (item.getStrength() < human.getStrength()
                    && item.getMeatCalories() > 0
                    && !item.getName().equals("Gnat")
                    && !item.getName().equals("Spider")) {
                foodZones.add(item.getLocation());
            }
        }
        return foodZones;
    }
    
    /**
     * Causes the human to try to either breed or build a Condo or Factory (they
     * will alternate between breeding and building). If the human is blocked 
     * from doing so, it will try to eat instead.
     * 
     * @param world: the world the human exists in
     * @param human: the human in question
     * @param foods: a Set of Locations containing viable foods within eyesight
     * of the human
     * @param surroundingThings: a Set of all the Items visible to the human
     * @return either a Breed or BuildCommand, or if neither of those is possible
     * the resultant command from the human trying to eat instead
     */
    private Command breedOrBuild(ArenaWorld world, ArenaAnimal human, 
            Set<Location> foods, Set<Item> surroundingThings){
        Location target = new Location(human.getLocation());
        ArrayList<Direction> directions = new ArrayList<Direction>();
        directions.add(Direction.EAST);
        directions.add(Direction.WEST);
        directions.add(Direction.NORTH);
        directions.add(Direction.SOUTH);
        int counter = 0;
        while(counter < 4){
            target = new Location(human.getLocation(), directions.get(counter));
            if(Util.isValidLocation(world, target) && 
                    isLocationEmpty(world, human, target)){
                counter = 4;
            }
            counter++;
        }
        if(target.equals(human.getLocation())){
            return tryToEat(world, human, foods, surroundingThings);
        }
        if(breedTime){
            breedTime = false;
            return new BreedCommand(human, target);
        }else{
            breedTime = true;
            return new BuildCommand(human, target);
        }
    }
}
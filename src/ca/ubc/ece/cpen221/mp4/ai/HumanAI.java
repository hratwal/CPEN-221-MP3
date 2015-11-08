package ca.ubc.ece.cpen221.mp4.ai;

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
import ca.ubc.ece.cpen221.mp4.commands.Command;
import ca.ubc.ece.cpen221.mp4.commands.EatCommand;
import ca.ubc.ece.cpen221.mp4.commands.MoveCommand;
import ca.ubc.ece.cpen221.mp4.commands.WaitCommand;
import ca.ubc.ece.cpen221.mp4.items.*;
import ca.ubc.ece.cpen221.mp4.items.animals.*;

public class HumanAI extends ArenaAnimalAI {
    private int energy;

    public HumanAI(int energy) {
        super(energy);
    }

    // why does the ArenaAnimalAI constructor need energy? It's not used for
    // anything
    public HumanAI() {
        super(200);
    }

    @Override
    public Command getNextAction(ArenaWorld world, ArenaAnimal human) {
        Set<Item> surroundingThings = world.searchSurroundings(human);
        Set<Location> dangers = identifyDangers(world, human, surroundingThings);
        Set<Location> foods = identifyFood(world, human, surroundingThings);
        if (dangers.size() > 0 && human.getEnergy() > (human.getMaxEnergy() / 10)) {
            return runAway(world, human, dangers);
        }
        return new WaitCommand();
    }

    public Set<Location> identifyDangers(ArenaWorld world, ArenaAnimal human, Set<Item> surroundings) {
        Set<Location> dangerZones = new HashSet<Location>();
        Iterator<Item> it = surroundings.iterator();
        while (it.hasNext()) {
            Item item = it.next();
            if (item.getStrength() > human.getStrength() && !(item instanceof Condos) && !(item instanceof Factory)) {
                dangerZones.add(item.getLocation());
            }
        }
        return dangerZones;
    }

    public Set<Location> identifyFood(ArenaWorld world, ArenaAnimal human, Set<Item> surroundings) {
        Set<Location> foodZones = new HashSet<Location>();
        Iterator<Item> it = surroundings.iterator();
        while (it.hasNext()) {
            Item item = it.next();
            if (item.getStrength() < human.getStrength()
                    && (item.getPlantCalories() > 0 || item.getMeatCalories() > 0)) {
                foodZones.add(item.getLocation());
            }
        }
        return foodZones;
    }

    public Command runAway(ArenaWorld world, ArenaAnimal human, Set<Location> dangers) {
        Iterator<Location> it = dangers.iterator();
        Location nearestDanger = it.next();
        int currentClosestDistance = human.getLocation().getDistance(nearestDanger);
        while (it.hasNext()) {
            Location curr = it.next();
            if (human.getLocation().getDistance(curr) > currentClosestDistance) {
                nearestDanger = curr;
                currentClosestDistance = human.getLocation().getDistance(curr);
            }
        }
        int xDiff = human.getLocation().getX() - nearestDanger.getX();
        //positive if danger is to the WEST
        int yDiff = human.getLocation().getY() - nearestDanger.getY();
        //positive if danger is to the NORTH
        
        Location target = new Location(human.getLocation());
        if (xDiff != 0) {
            
            if (xDiff > 0) {// case: danger is to the WEST
                target = new Location(human.getLocation(), Direction.EAST);
            }else if(xDiff < 0){// case: danger is to the EAST
                target = new Location(human.getLocation(), Direction.WEST);
            }if(isLocationEmpty(world, human, target)){
                return new MoveCommand(human, target);
            }
        }
        if(yDiff <= 0){
            target = new Location(human.getLocation(), Direction.NORTH);
        }else{
            target = new Location(human.getLocation(), Direction.SOUTH);
        }
        if(isLocationEmpty(world, human, target)){
            return new MoveCommand(human, target);
        }
        return panic(world, human);
    }
    
    /**
     * governs behavior if blocked from moving away from danger. Intent is for humans
     * to try to eat the obstruction if possible, and if not, to try to build
     * something to save themselves.
     * 
     * @param world
     * @param human
     * @return
     */

    public Command panic(ArenaWorld world, ArenaAnimal human){
        
    }

}



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
import ca.ubc.ece.cpen221.mp4.items.animals.*;

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

    private Set<Location> identifyDangers(ArenaWorld world, ArenaAnimal human, 
            Set<Item> surroundings) {
        Set<Location> dangerZones = new HashSet<Location>();
        Iterator<Item> it = surroundings.iterator();
        while (it.hasNext()) {
            Item item = it.next();
            if (item.getStrength() > human.getStrength() && 
                    !(item instanceof Condo) && 
                    !(item instanceof Factory)) {
                dangerZones.add(item.getLocation());
            }
        }
        return dangerZones;
    }

    private Set<Location> identifyFood(ArenaWorld world, ArenaAnimal human, 
            Set<Item> surroundings) {
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

    private Command runAway(ArenaWorld world, ArenaAnimal human, Set<Location> dangers) {
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
            }if(Util.isValidLocation(world, target) && 
                    isLocationEmpty(world, human, target)){
                return new MoveCommand(human, target);
            }
        }
        if(yDiff <= 0){
            target = new Location(human.getLocation(), Direction.NORTH);
        }else{
            target = new Location(human.getLocation(), Direction.SOUTH);
        }
        if(Util.isValidLocation(world, target) && 
                isLocationEmpty(world, human, target)){
            return new MoveCommand(human, target);
        }
        return panic(world, human, nearestDanger);
    }
    
    /**
     * governs behavior if blocked from moving away from danger. Intent is for humans
     * to try to build something to save themselves. Currently they just wait
     * 
     * @param world
     * @param human
     * @return
     */

    private Command panic(ArenaWorld world, ArenaAnimal human, Location danger){
        return new WaitCommand();
    }
    
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
    
    private Command tryToEat(ArenaWorld world, ArenaAnimal human, 
            Set<Location> foods, Set<Item> surroundingThings){
        if(foods.size() == 0){
            return wanderAimlessly(world, human);
        }
        Iterator<Location> locationIt = foods.iterator();
        Location nearest = locationIt.next();
        int lowestDistance = nearest.getDistance(human.getLocation());
        while(locationIt.hasNext()){
            Location current = locationIt.next();
            int currentDistance = current.getDistance(human.getLocation());
            if(currentDistance < lowestDistance){
                nearest = current;
                lowestDistance = currentDistance;
            }
        }
        Iterator<Item> itemIt = surroundingThings.iterator();
        Item nearestFood = itemIt.next();
        while(itemIt.hasNext()){
            Item currentItem = itemIt.next();
            if(currentItem.getLocation().equals(nearest))
                nearestFood = currentItem;
        }
        if(lowestDistance == 1){
            return new EatCommand(human, nearestFood);
        }else{
            int xDiff = human.getLocation().getX() - nearest.getX();
            //positive if food is to the WEST
            int yDiff = human.getLocation().getY() - nearest.getY();
            //positive if food is to the NORTH
            
            Location target = new Location(human.getLocation());
            if (xDiff != 0) {
                
                if (xDiff > 0) {// case: food is to the WEST
                    target = new Location(human.getLocation(), Direction.WEST);
                }else if(xDiff < 0){// case: food is to the EAST
                    target = new Location(human.getLocation(), Direction.EAST);
                }if(Util.isValidLocation(world, target) && 
                        isLocationEmpty(world, human, target)){
                    return new MoveCommand(human, target);
                }
            }
            if(yDiff <= 0){
                target = new Location(human.getLocation(), Direction.SOUTH);
            }else{
                target = new Location(human.getLocation(), Direction.NORTH);
            }
            if(Util.isValidLocation(world, target) &&
                    isLocationEmpty(world, human, target)){
                return new MoveCommand(human, target);
            }
        }
        return new WaitCommand();
        //return wanderAimlessly(world, human);
    }
    
    private Command wanderAimlessly(ArenaWorld world, ArenaAnimal human){
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
            return new WaitCommand();
        }else{
            System.out.println("testing for bugs");
            return new MoveCommand(human, target);
        }
    }

}



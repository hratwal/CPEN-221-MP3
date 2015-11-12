package ca.ubc.ece.cpen221.mp4.ai;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import ca.ubc.ece.cpen221.mp4.ArenaWorld;
import ca.ubc.ece.cpen221.mp4.Direction;
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
import ca.ubc.ece.cpen221.mp4.items.buildings.Condos;
import ca.ubc.ece.cpen221.mp4.items.buildings.Factories;

public class ArenaAnimalAI implements AI {
	private int energy;

	public ArenaAnimalAI(int energy) {
		this.energy = energy;
	}

	public boolean isLocationEmpty(ArenaWorld world, ArenaAnimal animal, Location location) {
		if (!Util.isValidLocation(world, location)) {
			return false;
		}
		Set<Item> possibleMoves = world.searchSurroundings(animal);
		Iterator<Item> it = possibleMoves.iterator();
		while (it.hasNext()) {
			Item item = it.next();
			if (item.getLocation().equals(location)) {
				return false;
			}
		}
		return true;
	}

	@Override
	public Command getNextAction(ArenaWorld world, ArenaAnimal animal) {
		Direction dir = Util.getRandomDirection();
		Location targetLocation = new Location(animal.getLocation(), dir);
		Set<Item> possibleEats = world.searchSurroundings(animal);
		Location current = animal.getLocation();
		Iterator<Item> it = possibleEats.iterator();
		while (it.hasNext()) {
			Item item = it.next();
			if ((item.getName().equals("Gnat") || item.getName().equals("Rabbit")
					|| item.getName().equals("Fox") || item.getName().equals("Human")
					|| item.getName().equals("Spider"))
					&& (current.getDistance(item.getLocation()) == 1)) {
				return new EatCommand(animal, item); // arena animals eat gnats
														// and rabbits
			}
		}
		if (Util.isValidLocation(world, targetLocation) && 
		        this.isLocationEmpty(world, animal, targetLocation)) {
			return new MoveCommand(animal, targetLocation);
		}
		return new WaitCommand();
	}
		
	/**
     * returns a set of locations that contain potential dangerous Items in 
     * eyesight of the animal
     * @param world: the world the animal exists in
     * @param animal: the animal in question
     * @param surroundings: a set of all Items that the animal can see
     * @return: a set of locations containing potentially deadly Items within
     * eyesight of the animal
     */
	protected Set<Location> identifyDangers(ArenaWorld world, ArenaAnimal animal, 
            Set<Item> surroundings) {
        Set<Location> dangerZones = new HashSet<Location>();
        Iterator<Item> it = surroundings.iterator();
        while (it.hasNext()) {
            Item item = it.next();
            if (item.getStrength() > animal.getStrength() && 
                    !(item instanceof Condos) && 
                    !(item instanceof Factories)) {
                dangerZones.add(item.getLocation());
            }
        }
        return dangerZones;
    }
	
	/**
     * Causes the animal to attempt to run away from potential dangers
     * (vehicles or predators)
     * @param world: the world the animal exists in
     * @param animal: the animal in question
     * @param dangers: a set of locations containing Items dangerous to the animal
     * @return: a MoveCommand in a direction away from the danger, or a 
     * WaitCommand if the animal is blocked from running away
     */
    protected Command runAway(ArenaWorld world, ArenaAnimal animal, Set<Location> dangers) {
        Iterator<Location> it = dangers.iterator();
        Location nearestDanger = it.next();
        int currentClosestDistance = animal.getLocation().getDistance(nearestDanger);
        while (it.hasNext()) {
            Location curr = it.next();
            if (animal.getLocation().getDistance(curr) > currentClosestDistance) {
                nearestDanger = curr;
                currentClosestDistance = animal.getLocation().getDistance(curr);
            }
        }
        int xDiff = animal.getLocation().getX() - nearestDanger.getX();
        //positive if danger is to the WEST
        int yDiff = animal.getLocation().getY() - nearestDanger.getY();
        //positive if danger is to the NORTH
        
        Location target = new Location(animal.getLocation());
        if (xDiff != 0) {
            
            if (xDiff > 0) {// case: danger is to the WEST
                target = new Location(animal.getLocation(), Direction.EAST);
            }else if(xDiff < 0){// case: danger is to the EAST
                target = new Location(animal.getLocation(), Direction.WEST);
            }if(Util.isValidLocation(world, target) && 
                    isLocationEmpty(world, animal, target)){
                return new MoveCommand(animal, target);
            }
        }
        if(yDiff <= 0){
            target = new Location(animal.getLocation(), Direction.NORTH);
        }else{
            target = new Location(animal.getLocation(), Direction.SOUTH);
        }
        if(Util.isValidLocation(world, target) && 
                isLocationEmpty(world, animal, target)){
            return new MoveCommand(animal, target);
        }
        return new WaitCommand();
    }
    
    /**
     * Causes the animal to eat food if there is food adjacent to it, or move
     * towards food if possible. If no food is in sight, the animal wanders
     * aimlessly
     * 
     * @param world: the world the animal exists in
     * @param animal: the animal in question
     * @param foods: a set of Locations containing potential food for the animal
     * @param surroundingThings: the set of Items containing everything the
     * animal can see
     * @return an EatCommand if the animal is adjacent to food, or a MoveCommand
     * otherwise
     */
    protected Command tryToEat(ArenaWorld world, ArenaAnimal animal, 
            Set<Location> foods, Set<Item> surroundingThings){
        if(foods.size() == 0){
            return wanderAimlessly(world, animal);
        }
        Iterator<Location> locationIt = foods.iterator();
        Location nearest = locationIt.next();
        int lowestDistance = nearest.getDistance(animal.getLocation());
        while(locationIt.hasNext()){
            Location current = locationIt.next();
            int currentDistance = current.getDistance(animal.getLocation());
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
            return new EatCommand(animal, nearestFood);
        }else{
            int xDiff = animal.getLocation().getX() - nearest.getX();
            //positive if food is to the WEST
            int yDiff = animal.getLocation().getY() - nearest.getY();
            //positive if food is to the NORTH
            
            Location target = new Location(animal.getLocation());
            if (xDiff != 0) {
                
                if (xDiff > 0) {// case: food is to the WEST
                    target = new Location(animal.getLocation(), Direction.WEST);
                }else if(xDiff < 0){// case: food is to the EAST
                    target = new Location(animal.getLocation(), Direction.EAST);
                }if(Util.isValidLocation(world, target) && 
                        isLocationEmpty(world, animal, target)){
                    return new MoveCommand(animal, target);
                }
            }
            if(yDiff <= 0){
                target = new Location(animal.getLocation(), Direction.SOUTH);
            }else{
                target = new Location(animal.getLocation(), Direction.NORTH);
            }
            if(Util.isValidLocation(world, target) &&
                    isLocationEmpty(world, animal, target)){
                return new MoveCommand(animal, target);
            }
        }
        //return new WaitCommand();
        return wanderAimlessly(world, animal);
    }
    
    /**
     * Causes the animal to wander aimlessly. Is called when the animal has nothing
     * else to do.
     * @param world: the world the animal exists in
     * @param human: the animal in question
     * @return a MoveCommand in a random direction
     */
    private Command wanderAimlessly(ArenaWorld world, ArenaAnimal animal){
        Location target = new Location(animal.getLocation());
        ArrayList<Direction> directions = new ArrayList<Direction>();
        directions.add(Direction.EAST);
        directions.add(Direction.WEST);
        directions.add(Direction.NORTH);
        directions.add(Direction.SOUTH);
        int counter = 0;
        while(counter < 4){
            target = new Location(animal.getLocation(), directions.get(counter));
            if(Util.isValidLocation(world, target) && 
                    isLocationEmpty(world, animal, target)){
                counter = 4;
            }
            counter++;
        }
        if(target.equals(animal.getLocation())){            
            return new WaitCommand();
        }else{
            return new MoveCommand(animal, target);
        }
    }
    
    /**
     * Commands the animal to breed in an adjacent square if possible. If there
     * are no empty adjacent squares, it will attempt to eat instead.
     * 
     * @param world: the world containing the animal
     * @param animal: the animal in question
     * @param foods: a Set of Locations containing foods visible to the animal
     * @param surroundingThings: the Set of Items that are visible to the animal
     * @return a BreedCommand if there is an empty adjacent square, or the result
     * of the animal trying to eat otherwise
     */
    protected Command breed(ArenaWorld world, ArenaAnimal animal, 
            Set<Location> foods, Set<Item> surroundingThings){
        Location target = new Location(animal.getLocation());
        ArrayList<Direction> directions = new ArrayList<Direction>();
        directions.add(Direction.EAST);
        directions.add(Direction.WEST);
        directions.add(Direction.NORTH);
        directions.add(Direction.SOUTH);
        int counter = 0;
        while(counter < 4){
            target = new Location(animal.getLocation(), directions.get(counter));
            if(Util.isValidLocation(world, target) && 
                    isLocationEmpty(world, animal, target)){
                counter = 4;
            }
            counter++;
        }
        if(target.equals(animal.getLocation()) || 
                canSeeSameType(world, animal, surroundingThings)){
            /*Animals won't breed if they can see other animals of the same species
             * because otherwise there are problems (tons and tons of 
             * Invalid MoveCommand errors) with foxes breeding like
             * rabbits and trying to move into spaces that their brethren
             * are giving birth in. If we could change the Fox implementation
             * so they weren't born with enough energy to immediately breed
             * again, we wouldn't need this, but we're not supposed to change
             * the Fox implementation. 
             * */
            return tryToEat(world, animal, foods, surroundingThings);
        }
        return new BreedCommand(animal, target);
    }
    
    /**
     * returns true if the given animal can see other animals of the same species.
     * 
     * @param world
     * @param animal
     * @param surroundingThings
     * @return
     */
    private boolean canSeeSameType(ArenaWorld world, ArenaAnimal animal, 
            Set<Item> surroundingThings){
        Iterator<Item> it = surroundingThings.iterator();
        int thisCounter = 0;
        while(it.hasNext()){
            Item item = it.next();
            if(item.getName().equals(animal.getName()))
                thisCounter++;
            /*thisCounter is necessary because searchSurroundings also includes
             * the animal doing the searching. If thisCounter wasn't there,
             * when calling this method, the animal would see itself,
             * and the method would always return true.
             * */
        }
        if(thisCounter > 1){
            return true;
        }
        return false;
    }

}
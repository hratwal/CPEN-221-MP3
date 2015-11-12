package ca.ubc.ece.cpen221.mp4.ai;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;

import ca.ubc.ece.cpen221.mp4.ArenaWorld;
import ca.ubc.ece.cpen221.mp4.Direction;
import ca.ubc.ece.cpen221.mp4.Location;
import ca.ubc.ece.cpen221.mp4.Util;
import ca.ubc.ece.cpen221.mp4.World;
import ca.ubc.ece.cpen221.mp4.commands.BreedCommand;
import ca.ubc.ece.cpen221.mp4.commands.BuildVehicleCommand;
import ca.ubc.ece.cpen221.mp4.commands.Command;
import ca.ubc.ece.cpen221.mp4.commands.SpawnRaptorCommand;
import ca.ubc.ece.cpen221.mp4.commands.WaitCommand;
import ca.ubc.ece.cpen221.mp4.items.Item;
import ca.ubc.ece.cpen221.mp4.items.animals.*;

public class BuildingAI{
    private int doStuffCounter = 0;
    
    public BuildingAI(){
        doStuffCounter = 0;
    }
    
    public boolean isLocationEmpty(World world, Item building, Location location) {
        if (!Util.isValidLocation(world, location)) {
            return false;
        }
        Set<Item> possibleMoves = world.searchSurroundings(building.getLocation(), 1);
        Iterator<Item> it = possibleMoves.iterator();
        while (it.hasNext()) {
            Item item = it.next();
            if (item.getLocation().equals(location)) {
                return false;
            }
        }
        return true;
    }
    
	public Command getNextAction(World world, Item building) {
		doStuffCounter++;
		Location target = new Location(building.getLocation());
        ArrayList<Direction> directions = new ArrayList<Direction>();
        directions.add(Direction.EAST);
        directions.add(Direction.WEST);
        directions.add(Direction.NORTH);
        directions.add(Direction.SOUTH);
        int counter = 0;
        while(counter < 4){
            target = new Location(building.getLocation(), directions.get(counter));
            if(Util.isValidLocation(world, target) && 
                    isLocationEmpty(world, building, target)){
                counter = 4;
            }
            counter++;
        }
		if(doStuffCounter > 50 && building.getName().equals("Factory")){
		    doStuffCounter = 0;
		    return new BuildVehicleCommand(building, target);
		}if(doStuffCounter > 50 && building.getName().equals("JurassicPark")){
		    doStuffCounter = 0;
		    return new SpawnRaptorCommand(building, target);
		}
		return new WaitCommand();
	}

}

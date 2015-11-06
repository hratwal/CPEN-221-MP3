package ca.ubc.ece.cpen221.mp4.ai;

import java.util.*;

import ca.ubc.ece.cpen221.mp4.Direction;
import ca.ubc.ece.cpen221.mp4.Location;
import ca.ubc.ece.cpen221.mp4.Util;
import ca.ubc.ece.cpen221.mp4.World;
import ca.ubc.ece.cpen221.mp4.commands.*;
import ca.ubc.ece.cpen221.mp4.items.Item;
import ca.ubc.ece.cpen221.mp4.items.vehicles.Vehicle;

public class VehicleAI{

    public boolean isLocationEmpty(World world, Vehicle vehicle, Location location) {

        if (!Util.isValidLocation(world, location)) {
            return false;
        }
        Set<Item> possibleMoves = world.searchSurroundings(vehicle.getLocation(), vehicle.getViewRange());
        Iterator<Item> it = possibleMoves.iterator();
        while (it.hasNext()) {
            Item item = it.next();
            if (item.getLocation().equals(location)) {
                return false;
            }
        }
        return true;
    }

    public Command getNextAction(World world, Vehicle vehicle) {
        Location curr = vehicle.getLocation();
        Direction dir = vehicle.getDir();
        Set<Location> dangerZones = getNearbyDangerZones(world, vehicle);
        Iterator<Location> it = dangerZones.iterator();
        boolean needToChangeDir = false;
        // checks if there are any dangers in the direction it's currently
        // moving

        while (it.hasNext() && !needToChangeDir) {
            Location loc = it.next();
            switch (dir) {
            case NORTH:
                if (loc.getY() < curr.getY())
                    needToChangeDir = true;
                break;
            case SOUTH:
                if (loc.getY() > curr.getY())
                    needToChangeDir = true;
                break;
            case EAST:
                if (loc.getX() > curr.getX())
                    needToChangeDir = true;
                break;
            case WEST:
                if (loc.getX() < curr.getX())
                    needToChangeDir = true;
                break;
            default:
                break;
            }
        }
        if (!needToChangeDir) {
            if (vehicle.getCurrentCooldown() > vehicle.getMIN_COOLDOWN()) {
                vehicle.accelerate();
            }
            Location target = new Location(curr, dir);
            return vehicleMovement(world, target, vehicle);
        } else {
            if (canTurn(vehicle)) {
                dir = newDirection(vehicle, world);
                Location target = new Location(curr, dir);
                return vehicleMovement(world, target, vehicle);    
                
            } else {
                vehicle.decelerate();
                Location target = new Location(curr, dir);
                return vehicleMovement(world, target, vehicle);
            }
        }
    }

    private Set<Item> getNearbyItems(World world, Vehicle vehicle) {
        return world.searchSurroundings(vehicle.getLocation(), vehicle.getViewRange());
    }

    private Set<Location> getNearbyDangerZones(World world, Vehicle vehicle) {
        Set<Item> nearbyItems = getNearbyItems(world, vehicle);
        Set<Location> dangerZones = new HashSet<Location>();
        Iterator<Item> it = nearbyItems.iterator();
        while (it.hasNext()) {
            Item item = it.next();
            if (item.getStrength() >= vehicle.getStrength()
                    && (item.getLocation().getX() == vehicle.getLocation().getX()
                            || item.getLocation().getY() == vehicle.getLocation().getY())) {
                dangerZones.add(item.getLocation());
            }
        }
        if (nearEdge(world, vehicle)) {
            dangerZones.add(new Location(vehicle.getLocation(), vehicle.getDir()));
        }
        return dangerZones;
    }

    private boolean nearEdge(World world, Vehicle vehicle) {
        Direction dir = vehicle.getDir();
        int viewRange = vehicle.getViewRange();
        Location curr = vehicle.getLocation();
        switch (dir) {
        case NORTH:
            if ((curr.getY() - viewRange) < 0)
                return true;
        case SOUTH:
            if ((curr.getY() + viewRange) > world.getHeight())
                return true;
        case EAST:
            if ((curr.getX() + viewRange) > world.getWidth())
                return true;
        case WEST:
            if ((curr.getX() - viewRange) < 0)
                return true;
        default:
            return false;
        }
    }

    private Item findTargetItem(World world, Location loc, Vehicle vehicle) {
        Set<Item> adjacentItems = world.searchSurroundings(vehicle.getLocation(), 1);
        Iterator<Item> it = adjacentItems.iterator();
        while (it.hasNext()) {
            Item item = it.next();
            if (item.getLocation().equals(loc))
                return item;
        }
        return null;
    }
    
    //BUG: get an Invalid MoveCommand when a Truck and Motorcycle collide
    //Probably shows up when other vehicles collide as well
    private void collision(Vehicle vehicle, Item occupant) {
        if (occupant.getStrength() < vehicle.getStrength()){
            occupant.loseEnergy(100000);// should kill it
        }else if (occupant.getStrength() == vehicle.getStrength()){
            occupant.loseEnergy(100000);
            vehicle.loseEnergy(vehicle.getINITIAL_ENERGY());
        }else
            vehicle.loseEnergy(vehicle.getINITIAL_ENERGY());
    }

    private boolean canTurn(Vehicle vehicle) {
        int turnSpeed = vehicle.getMAX_COOLDOWN()
                - ((vehicle.getMAX_COOLDOWN() - vehicle.getMIN_COOLDOWN()) 
                        / vehicle.getACCEL_TURNS());
        return (vehicle.getCurrentCooldown() >= turnSpeed);
    }

    private Command vehicleMovement(World world, Location target, Vehicle vehicle) {
        if (Util.isValidLocation(world, target)) {
            if (!Util.isLocationEmpty(world, target)) {
                Item occupant = findTargetItem(world, target, vehicle);
                // if this returns null we got a problem
                collision(vehicle, occupant);
            }
            return new MoveCommand(vehicle, target);
        } else {
            vehicle.loseEnergy(vehicle.getINITIAL_ENERGY());
            // currently vehicles die if they to drive off the edge
            // of the world
            return new WaitCommand();
        }
    }

    private Direction newDirection(Vehicle vehicle, World world){
        if(vehicle.getLocation().getX() < 6){
            return Direction.EAST;
        }else if(vehicle.getLocation().getX() > world.getWidth() - 6){
            return Direction.WEST;
        }else if(vehicle.getLocation().getY() < 6){
            return Direction.SOUTH;
        }else if(vehicle.getLocation().getY() > world.getHeight() - 6){
            return Direction.NORTH;
        }
        Direction currDir = vehicle.getDir();
        ArrayList<Direction> directions = new ArrayList<Direction>();
        directions.add(Direction.EAST);
        directions.add(Direction.WEST);
        directions.add(Direction.NORTH);
        directions.add(Direction.SOUTH);
        directions.remove(currDir);
        double rand = Math.random() * 3;
        return directions.get((int) rand);
        
    }
}

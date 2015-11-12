package ca.ubc.ece.cpen221.mp4.ai;

import java.util.*;

import ca.ubc.ece.cpen221.mp4.Direction;
import ca.ubc.ece.cpen221.mp4.Location;
import ca.ubc.ece.cpen221.mp4.Util;
import ca.ubc.ece.cpen221.mp4.World;
import ca.ubc.ece.cpen221.mp4.commands.*;
import ca.ubc.ece.cpen221.mp4.items.Item;
import ca.ubc.ece.cpen221.mp4.items.vehicles.Vehicle;

public class VehicleAI {

    /**
     * Returns true if the location the vehicle is inspecting is empty
     * 
     * @param world:
     *            the world that the vehicle is in
     * @param vehicle:
     *            the vehicle which is inspecting possible locations
     * @param location:
     *            the location to be inspected
     * 
     * @return true if location already has an occupant, false otherwise
     */

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

    /**
     * Determines the next Command that the given vehicle will send to the
     * world, according to the vehicle's AI behavior.
     * 
     * @param world:
     *            the world the vehicle inhabits
     * @param vehicle:
     *            the vehicle giving the Command
     * 
     * @return the next Command that the vehicle will execute
     */

    public Command getNextAction(World world, Vehicle vehicle) {
        vehicle.incrementStepsSinceTurn();
        Location curr = vehicle.getLocation();
        Direction dir = vehicle.getDir();
        Set<Location> dangerZones = getNearbyDangerZones(world, vehicle);
        Iterator<Location> it = dangerZones.iterator();
        boolean needToChangeDir = (vehicle.getStepsSinceTurn() > 15 ||
                nearBuilding(world, vehicle));
        // automatically changes directions after 15 turns for variety

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
                vehicle.setDirection(dir);
                Location target = new Location(curr, dir);
                vehicle.resetStepsSinceTurn();
                return vehicleMovement(world, target, vehicle);

            } else {
                vehicle.decelerate();
                Location target = new Location(curr, dir);
                return vehicleMovement(world, target, vehicle);
            }
        }
    }

    /**
     * Returns a Set of the Items that the given Vehicle can see
     * 
     * @param world:
     *            the world the vehicle is currently inhabiting
     * @param vehicle:
     *            the vehicle that is doing the viewing
     * 
     * @return a Set of the Items that the vehicle can see
     */
    private Set<Item> getNearbyItems(World world, Vehicle vehicle) {
        return world.searchSurroundings(vehicle.getLocation(), vehicle.getViewRange());
    }

    /**
     * Determines which locations the given vehicle wants to avoid. These
     * locations are locations with entities with a higher strength than the
     * vehicle that also are on the same x- or y-line as the vehicle (i.e. the
     * vehicle ignores anything that is diagonal to it; it only considers Items
     * that are directly North, South, East, or West of it) or locations next to
     * the edge of the wall (which is deadly to vehicles)
     * 
     * @param world:
     *            the world the vehicle inhabits
     * @param vehicle:
     *            the vehicle that is determining danger
     * 
     * @return a set of Locations that satisfy the criteria of a danger zone as
     *         listed above
     */
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

    /**
     * Returns true if the given vehicle is near the edge of the world and
     * moving towards it
     * 
     * @param world:
     *            the world the vehicle is inhabiting
     * @param vehicle:
     *            the vehicle in question
     * 
     * @return true if the vehicle is within viewing distance of the edge and
     *         heading towards it, false otherwise
     */
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

    // I don't think I use this method?
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

    /**
     * Called when a vehicle tries to move into a space that is already
     * occupied, should destroy whichever of the two have lower strength (or
     * both if their strength is equal)
     * 
     * @param vehicle:
     *            the vehicle in question
     * @param occupant:
     *            the occupant of the location that the vehicle is trying to
     *            move into
     */
    private void collision(Vehicle vehicle, Item occupant) {
        if (occupant.getStrength() < vehicle.getStrength()) {
            occupant.loseEnergy(Integer.MAX_VALUE);
        } else if (occupant.getStrength() == vehicle.getStrength()) {
            occupant.loseEnergy(Integer.MAX_VALUE);
            vehicle.loseEnergy(vehicle.getINITIAL_ENERGY());
        } else
            vehicle.loseEnergy(vehicle.getINITIAL_ENERGY());
    }

    /**
     * Returns true if the vehicle is moving slow enough to turn
     * 
     * @param vehicle:
     *            the vehicle in question
     * @return true if the vehicle is able to turn, false otherwise
     */
    private boolean canTurn(Vehicle vehicle) {
        int turnSpeed = vehicle.getMAX_COOLDOWN()
                - ((vehicle.getMAX_COOLDOWN() - vehicle.getMIN_COOLDOWN()) / 
                        vehicle.getACCEL_TURNS());
        return (vehicle.getCurrentCooldown() >= turnSpeed);
    }

    /**
     * Once the vehicle has decided to move, deals with the movement, by
     * determining whether or not the space to move to is a valid move location
     * and if it is occupied. If it's not a valid move location (i.e. the
     * vehicle is driving into the edge), then the vehicle will be destroyed. If
     * it is occupied, either the vehicle and/or the occupant will be destroyed
     * 
     * @param world:
     *            the world the vehicle inhabits
     * @param target:
     *            the space the vehicle is trying to move into
     * @param vehicle:
     *            the vehicle in question
     * @return the correct Command to deal with the intended movement
     */
    private Command vehicleMovement(World world, Location target, Vehicle vehicle) {
        if (Util.isValidLocation(world, target)) {
            if (!Util.isLocationEmpty(world, target)) {
                Item occupant = findTargetItem(world, target, vehicle);
                collision(vehicle, occupant);

                if (occupant instanceof Vehicle) {
                    return new WaitCommand();
                } else {
                    if (Util.isValidLocation(world, target) 
                            && Util.isLocationEmpty(world, target))
                        return new MoveCommand(vehicle, target);
                }
                /*
                 * This if-else block is somewhat kludgy, but otherwise it
                 * throws an error if two vehicles collide. This is because the
                 * stronger vehicle tries to move into the space occupied by the
                 * weaker vehicle before the world can get rid of the dead
                 * weaker vehicle. This way, the stronger vehicle politely waits
                 * for the body to disappear before continuing on its way.
                 * 
                 * For some reason, this doesn't happen with collisions with
                 * other Items. Will look into it later.
                 */

            } else {
                if (Util.isValidLocation(world, target) 
                        && Util.isLocationEmpty(world, target)) {
                    return new MoveCommand(vehicle, target);
                }
            }
        } else {
            vehicle.loseEnergy(vehicle.getINITIAL_ENERGY());
            // currently vehicles die if they drive off the edge
            // of the world
            return new WaitCommand();

        }
        return new WaitCommand();
    }

    /**
     * Returns a new direction to move in after the vehicle turns
     * 
     * @param vehicle:
     *            the vehicle in question
     * @param world:
     *            the world the vehicle inhabits
     * 
     * @return the Direction the vehicle will now be moving in
     */
    private Direction newDirection(Vehicle vehicle, World world) {
        if (vehicle.getLocation().getX() < 6) {
            return Direction.EAST;
        } else if (vehicle.getLocation().getX() > world.getWidth() - 6) {
            return Direction.WEST;
        } else if (vehicle.getLocation().getY() < 6) {
            return Direction.SOUTH;
        } else if (vehicle.getLocation().getY() > world.getHeight() - 6) {
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
    /**
     * returns true if the given vehicle is near a building, false otherwise
     * @param world: the world the vehicle inhabits
     * @param vehicle: the vehicle in question
     * 
     * @return true if the given vehicle is within 1 space of a building, false 
     * otherwise
     */
    private boolean nearBuilding(World world, Vehicle vehicle){
        Set<Item> nearbyItems = world.searchSurroundings(vehicle.getLocation(), 1);
        Iterator<Item> it = nearbyItems.iterator();
        while(it.hasNext()){
            Item item = it.next();
            if(item.getName().equals("Condo") ||
                    item.getName().equals("Factory") ||
                    item.getName().equals("JurassicPark")){
                return true;
            }
        }
        return false;
    }
}
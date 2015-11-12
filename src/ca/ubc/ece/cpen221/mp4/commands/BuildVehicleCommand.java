package ca.ubc.ece.cpen221.mp4.commands;

import ca.ubc.ece.cpen221.mp4.Location;
import ca.ubc.ece.cpen221.mp4.Util;
import ca.ubc.ece.cpen221.mp4.World;
import ca.ubc.ece.cpen221.mp4.ai.VehicleAI;
import ca.ubc.ece.cpen221.mp4.items.Item;
import ca.ubc.ece.cpen221.mp4.items.LivingItem;
import ca.ubc.ece.cpen221.mp4.items.buildings.Condos;
import ca.ubc.ece.cpen221.mp4.items.buildings.Factories;
import ca.ubc.ece.cpen221.mp4.items.vehicles.*;

public class BuildVehicleCommand implements Command {
    private final Item item;
    private final Location target;

    /**
     * Constructor where <code>item</code> is the LivingItem that is breeding
     * and <code>target</code> is the location where the child will appear. The
     * child must be born at an empty location adjacent to the parent.
     *
     * @param item
     *            the parent LivingItem
     * @param target
     *            the location where child will appear
     */
    public BuildVehicleCommand(Item item, Location target) {
        this.item = item;
        this.target = target;
    }

    @Override
    public void execute(World world) throws InvalidCommandException {
        if (!Util.isValidLocation(world, target) || !Util.isLocationEmpty(world, target)) {
            throw new InvalidCommandException("Invalid BuildVehicleCommand: "
                    + "Invalid/non-empty building target location");
        }
        double randomValue = Math.random() * 10;
        if(randomValue < 3.33){
            Motorcycle motorcycle = new Motorcycle(new VehicleAI(), target);
            motorcycle.moveTo(target);
            world.addItem(motorcycle);
            world.addActor(motorcycle);
        }else if(randomValue < 6.67){
            Truck truck = new Truck(new VehicleAI(), target);
            truck.moveTo(target); 
            world.addItem(truck);
            world.addActor(truck);
        }else{
            Tank tank = new Tank(new VehicleAI(), target);
            tank.moveTo(target); 
            world.addItem(tank);
            world.addActor(tank);
        }
        item.loseEnergy(40);
    }
}
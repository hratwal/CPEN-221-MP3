package ca.ubc.ece.cpen221.mp4.commands;

import ca.ubc.ece.cpen221.mp4.Location;
import ca.ubc.ece.cpen221.mp4.Util;
import ca.ubc.ece.cpen221.mp4.World;
import ca.ubc.ece.cpen221.mp4.items.LivingItem;
import ca.ubc.ece.cpen221.mp4.items.buildings.*;

public class BuildCommand implements Command{
    private final LivingItem item;
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
    public BuildCommand(LivingItem item, Location target) {
        this.item = item;
        this.target = target;
    }

    @Override
    public void execute(World world) throws InvalidCommandException {
        if (!Util.isValidLocation(world, target) || !Util.isLocationEmpty(world, target)) {
            throw new InvalidCommandException("Invalid BuildCommand: "
                    + "Invalid/non-empty building target location");
        }
        double randomValue = Math.random() * 10;
        if(randomValue < 3.33){
            Condos building = new Condos(target);
            building.moveTo(target); 
            world.addItem(building);
            world.addActor(building);
        }else if(randomValue < 6.67){
            Factories building = new Factories(target);
            building.moveTo(target); 
            world.addItem(building);
            world.addActor(building);
        }else{
            JurassicPark building = new JurassicPark(target);
            building.moveTo(target); 
            world.addItem(building);
            world.addActor(building);
        }
        item.loseEnergy(item.getEnergy() / 2);
    }
}
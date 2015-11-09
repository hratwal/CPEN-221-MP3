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
            throw new InvalidCommandException("Invalid BreedCommand: Invalid/non-empty breeding target location");
        }
        double randomValue = Math.random() * 10;
        if(randomValue < 8){
            Condo building = new Condo(target);
            building.moveTo(target);
            world.addItem(building);
            //maybe put in an actor method if they need to lose health?
        }else{
            //switch this to factories once they're implemented
            Condo building = new Condo(target);
            building.moveTo(target);
            world.addItem(building);
        }
        item.loseEnergy(item.getEnergy() / 2);
    }
}

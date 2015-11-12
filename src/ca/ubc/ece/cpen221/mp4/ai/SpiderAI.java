package ca.ubc.ece.cpen221.mp4.ai;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import ca.ubc.ece.cpen221.mp4.ArenaWorld;
import ca.ubc.ece.cpen221.mp4.Location;
import ca.ubc.ece.cpen221.mp4.items.Item;
import ca.ubc.ece.cpen221.mp4.items.animals.ArenaAnimal;

public class SpiderAI extends PredatorAI{
    public SpiderAI(){
        
    }
    
    /**
     * returns a set of locations that contain potential food Items in 
     * eyesight of the spider
     * @param world: the world the spider exists in
     * @param human: the spider in question
     * @param surroundings: a set of all Items that the spider can see
     * @return: a set of locations containing edible items within eyesight
     * of the spider
     */
    @Override
    protected Set<Location> identifyFood(ArenaWorld world, ArenaAnimal predator, 
            Set<Item> surroundings) {
        Set<Location> foodZones = new HashSet<Location>();
        Iterator<Item> it = surroundings.iterator();
        while (it.hasNext()) {
            Item item = it.next();
            if (item.getName().equals("Gnat")) {
                foodZones.add(item.getLocation());
            }
        }
        return foodZones;
    }
}

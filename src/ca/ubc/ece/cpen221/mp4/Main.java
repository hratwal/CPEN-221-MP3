package ca.ubc.ece.cpen221.mp4;

import javax.swing.SwingUtilities;

import ca.ubc.ece.cpen221.mp4.ai.*;
import ca.ubc.ece.cpen221.mp4.items.Gardener;
import ca.ubc.ece.cpen221.mp4.items.Grass;
import ca.ubc.ece.cpen221.mp4.items.animals.*;
import ca.ubc.ece.cpen221.mp4.items.buildings.Condos;
import ca.ubc.ece.cpen221.mp4.items.buildings.Factories;
import ca.ubc.ece.cpen221.mp4.staff.WorldImpl;
import ca.ubc.ece.cpen221.mp4.staff.WorldUI;
import ca.ubc.ece.cpen221.mp4.items.vehicles.*;

/**
 * The Main class initialize a world with some {@link Grass}, {@link Rabbit}s,
 * {@link Fox}es, {@link Gnat}s, {@link Gardener}, etc.
 *
 * You may modify or add Items/Actors to the World.
 *
 */
public class Main {

	static final int X_DIM = 40;
	static final int Y_DIM = 40;
	static final int SPACES_PER_GRASS = 7;
	static final int INITIAL_GRASS = X_DIM * Y_DIM / SPACES_PER_GRASS;
	static final int INITIAL_GNATS = INITIAL_GRASS / 4;
	static final int INITIAL_SPIDERS = INITIAL_GRASS / 12;
	static final int INITIAL_RABBITS = INITIAL_GRASS / 4;
	static final int INITIAL_FOXES = INITIAL_GRASS / 32;
	//static final int INITIAL_TIGERS = INITIAL_GRASS / 32;
	//static final int INITIAL_BEARS = INITIAL_GRASS / 40;
	static final int INITIAL_RAPTORS = INITIAL_GRASS / 64;
	static final int INITIAL_TANKS = INITIAL_GRASS / 200;
	static final int INITIAL_TRUCKS = INITIAL_GRASS / 150;
	static final int INITIAL_MOTORCYCLES = INITIAL_GRASS / 64;
	static final int INITIAL_MANS = INITIAL_GRASS / 32;
	//static final int INITIAL_WOMANS = INITIAL_GRASS / 100;
	static final int INITIAL_HUNTERS = INITIAL_GRASS / 150;
	static final int INITIAL_CONDOS = 0;
	static final int INITIAL_FACTORIES = 0;

	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				new Main().createAndShowWorld();
			}
		});
	}

	public void createAndShowWorld() {
		World world = new WorldImpl(X_DIM, Y_DIM);
		initialize(world);
		new WorldUI(world).show();
	}

	public void initialize(World world) {
		addGrass(world);
		world.addActor(new Gardener());
		addSpider(world);
		addGnats(world);
		addRabbits(world);
		addFoxes(world);
		addTrucks(world);
		addMotorcycles(world);
		addTanks(world);
		addHumans(world);
		addVelociraptors(world);
		addCondos(world);
		addFactories(world);
		// TODO: You may add your own creatures here!
	}

	private void addGrass(World world) {
		for (int i = 0; i < INITIAL_GRASS; i++) {
			Location loc = Util.getRandomEmptyLocation(world);
			world.addItem(new Grass(loc));
		}
	}

	private void addGnats(World world) {
		for (int i = 0; i < INITIAL_GNATS; i++) {
			Location loc = Util.getRandomEmptyLocation(world);
			Gnat gnat = new Gnat(loc);
			world.addItem(gnat);
			world.addActor(gnat);
		}
	}

	private void addFoxes(World world) {
		PredatorAI foxAI = new PredatorAI();
		for (int i = 0; i < INITIAL_FOXES; i++) {
			Location loc = Util.getRandomEmptyLocation(world);
			Fox fox = new Fox(foxAI, loc);
			world.addItem(fox);
			world.addActor(fox);
		}
	}

	private void addRabbits(World world) {
		RabbitAI rabbitAI = new RabbitAI();
		for (int i = 0; i < INITIAL_RABBITS; i++) {
			Location loc = Util.getRandomEmptyLocation(world);
			Rabbit rabbit = new Rabbit(rabbitAI, loc);
			world.addItem(rabbit);
			world.addActor(rabbit);
		}
	}

	private void addMotorcycles(World world){
	    VehicleAI vehicleAI = new VehicleAI();
	    for(int i = 0; i < INITIAL_MOTORCYCLES; i++){
	        Location loc = Util.getRandomEmptyLocation(world);
	        Motorcycle motorcycle = new Motorcycle(vehicleAI, loc);
	        world.addItem(motorcycle);
	        world.addActor(motorcycle);
	    }
	}

    private void addTanks(World world){
        VehicleAI vehicleAI = new VehicleAI();
        for(int i = 0; i < INITIAL_TANKS; i++){
            Location loc = Util.getRandomEmptyLocation(world);
            Tank tank = new Tank(vehicleAI, loc);
            world.addItem(tank);
            world.addActor(tank);
        }
    }	
    
    private void addTrucks(World world){
        VehicleAI vehicleAI = new VehicleAI();
        for(int i = 0; i < INITIAL_TRUCKS; i++){
            Location loc = Util.getRandomEmptyLocation(world);
            Truck truck = new Truck(vehicleAI, loc);
            world.addItem(truck);
            world.addActor(truck);
        }
    }
    
    private void addHumans(World world){
        HumanAI humanAI = new HumanAI();
        for(int i = 0; i < INITIAL_MANS; i++){
            Location loc = Util.getRandomEmptyLocation(world);
            Humans human = new Humans(humanAI, loc);
            world.addItem(human);
            world.addActor(human);
        }
    }
    
    private void addVelociraptors(World world){
        PredatorAI predatorAI = new PredatorAI();
        for (int i = 0; i < INITIAL_RAPTORS; i++) {
            Location loc = Util.getRandomEmptyLocation(world);
            Velociraptor raptor = new Velociraptor(predatorAI, loc);
            world.addItem(raptor);
            world.addActor(raptor);
        }
    }
    
	private void addSpider(World world) {
		SpiderAI spiderAI = new SpiderAI();
		for (int i = 0; i < INITIAL_SPIDERS; i++) {
			Location loc = Util.getRandomEmptyLocation(world);
			Spider spider = new Spider(spiderAI, loc);
			world.addItem(spider);
			world.addActor(spider);
		}
	}
	
	private void addCondos(World world) {
        for (int i = 0; i < INITIAL_CONDOS; i++) {
            Location loc = Util.getRandomEmptyLocation(world);
            Condos condo = new Condos(loc);
            world.addItem(condo);
            world.addActor(condo);
        }
    }
    
    private void addFactories(World world) {
        for (int i = 0; i < INITIAL_FACTORIES; i++) {
            Location loc = Util.getRandomEmptyLocation(world);
            Factories factory = new Factories(loc);
            world.addItem(factory);
            world.addActor(factory);
        }
    }
}
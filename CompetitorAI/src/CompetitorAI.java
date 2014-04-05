import bonzai.api.*;
import java.util.*;

public class CompetitorAI implements AI {
	
	private WeightComparator pathWeight = new CompetitorWeightComparator();
	private boolean firstTurn = true;
    private ArrayList<Node> wizardPath;
    private ArrayList<Node> explorePoints;
    private int wizardExploreIndex;
	
	/**
	 * You must have this function, all of the other functions in 
	 * this class are optional.
	 */
	@Override
	public void takeTurn(AIGameState state) {
		if(firstTurn) {
            explorePoints = new ArrayList<Node>();
            explorePoints.add(state.getNode(state.getWidth()/2, state.getHeight()/2));
            explorePoints.add(state.getNode(state.getWidth()/2, 0));
            explorePoints.add(state.getNode(0, state.getHeight()/2));
            explorePoints.add(state.getNode(state.getWidth()/2, state.getHeight()-1));
            explorePoints.add(state.getNode(state.getWidth()-1, state.getHeight()/2));
            wizardExploreIndex = 0;
			takeFirstTurn(state);
			firstTurn = false;
		}
		
		this.wizardBrain(state);
		this.moveBlockers(state);
		this.moveCleaners(state);
		this.moveScouts(state);
		this.moveHats(state);
	}
	
	/**
	 * Executes only on your first turn.
	 * @param state
	 */
	private void takeFirstTurn(AIGameState state) {
		for(Actor a : state.getMyActors()) {
			a.shout("THIS. IS. HOGWAARRTTSS!!!!"); //Shout "Go Team!" for the first turn
		}
	}
	
	/**
	 * Move or castMagic with your Wizard
	 * @param state
	 */
	private void wizardBrain(AIGameState state) {
		Wizard wizard = state.getMyWizard();

        //Can't see anything, so explore
        if(state.getNeutralActors().size() == 0
                && state.getEnemyActors().size() == 0){
            if(wizardPath == null){
                if(wizard.getLocation().getX() == explorePoints.get(wizardExploreIndex).getX() &&
                   wizard.getLocation().getY() == explorePoints.get(wizardExploreIndex).getY()){
                    wizardExploreIndex++;
                }
                if(wizardExploreIndex >= explorePoints.size()) wizardExploreIndex = 0;
                wizardPath = state.getPath(wizard, explorePoints.get(wizardExploreIndex), pathWeight);
            }
            if(wizard.canMove(wizard.getDirection(wizardPath)))
                wizard.move(wizard.getDirection(wizardPath));
            else
                wizardPath = null;
        }
		//Wizard Pathfinding
//		int moveDirection = wizard.getDirection(state.getNode(1, 1), pathWeight);
//		if(moveDirection != -1) {
//			if(wizard.canMove(moveDirection)) {
//				wizard.move(moveDirection);
//				wizard.shout("Moving");
//			}
//		}
		
		//Iterate through all visible enemy actors
//		for(Actor e : state.getEnemyActors()) {
//			if(wizard.canCast( e)) {
//				wizard.castMagic(e);
//			}
//		}
		
//		for(Actor e : state.getNeutralActors()) {
//			if(wizard.canCast(e)) {
//				wizard.castMagic(e);
//			}
//		}
	}
	
	/**
	 * Move, block, or unBlock with your blockers.
	 * @param state
	 */
	private void moveBlockers(AIGameState state) {
		for(Blocker blocker : state.getMyBlockers()) {
			if(Math.random() > .5) { 	//50% chance to use block()
				blocker.block();
			} else {					//50% chance to use unBlock()
				blocker.unBlock();
			}
		}
	}
	
	/**
	 * Move or sweep with your cleaners.
	 * @param state
	 */
	private void moveCleaners(AIGameState state) {
		for(Cleaner cleaner : state.getMyCleaners()) {
			int moveDirection = cleaner.getDirection(state.getNode(2, 2), pathWeight);
			
			//Move your cleaner one step closer to the node (1, 1)
			if(!cleaner.move(moveDirection)) {
				cleaner.shout("I am unable to move in that direction!");
			} else {
				if(!cleaner.canMove(moveDirection)) {
					//There is a blocking blocker in the direction of 'moveDirection'
				}
			}
			
			//If the sweeper can, it uses it's ability on a blocker instead of moving.
			for(Blocker enemyBlocker : state.getEnemyBlockers()) {
				if(cleaner.isAdjacent(enemyBlocker)) {
					cleaner.sweep(enemyBlocker);
				}
			}
		}
	}
	
	/**
	 * Move with your scouts.
	 * @param state
	 */
	private void moveScouts(AIGameState state) {
		for(Scout scout : state.getMyScouts()) {
			if(Math.random() > .8) { //80% chance to move randomly
				scout.doubleMove((int)(Math.random()*4), (int)(Math.random()*4));
			} else { //20% chance to move closer to your base.
				scout.doubleMove(state.getPath(scout, state.getMyBase(), pathWeight));
			}
		}
	}
	
	/**
	 * Do something with your hats!!!
	 * @param state
	 */
	private void moveHats(AIGameState state) {
		for(Hat hat : state.getMyHats()) {
			//TODO: Your hat should probably do something
		}
	}
}
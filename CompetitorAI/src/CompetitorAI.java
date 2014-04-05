import bonzai.api.*;
import java.util.*;

public class CompetitorAI implements AI {
	
	private WeightComparator pathWeight = new CompetitorWeightComparator();
	private boolean firstTurn = true;
	public static int myTeam = -1;
	public static int scores[] = new int[4];
	
	/**
	 * You must have this function, all of the other functions in 
	 * this class are optional.
	 */
	@Override
	public void takeTurn(AIGameState state) {
		if(firstTurn) {
			takeFirstTurn(state);
			firstTurn = false;
		}
		
		//Get all of your actors
		for(Actor a : state.getMyActors()) {
			//Move your actor in a random direction
			//Note: Only the last action is applied to the game, with the
			//      exception of a.shout().
			//      Example, if you call a.move(Node.DOWN) and then a.move(Node.UP)
			//      and then a.shout(), a.move(Node.UP) and a.shout() will 
			//      be executed and a.move(Node.DOWN) will be ignored. 
			a.move((int)(Math.random() * 4));
			
			//Type casting
			if(a instanceof Wizard) {
				//You probably shouldn't try to cast magic on yourself...
				if(((Wizard)a).canCast(a)) {
					if(((Wizard)a).castMagic(a)) {
						((Wizard)a).shout("**Ouch**");
					}
				}
			}
			
			if(a.isStunned()) {
				a.shout("**Ouch**");
			}
		}
		
		this.moveWizard(state);
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
			a.shout("Go Team!"); //Shout "Go Team!" for the first turn
		}
		
		myTeam = state.getMyTeamNumber();
	}
	
	/**
	 * Move or castMagic with your Wizard
	 * @param state
	 */
	private void moveWizard(AIGameState state) {
		Wizard wizard = state.getMyWizard();
		
		//Wizard Pathfinding
		int moveDirection = wizard.getDirection(state.getNode(1, 1), pathWeight);
		if(moveDirection != -1) { 
			if(wizard.canMove(moveDirection)) {
				wizard.move(moveDirection);
				wizard.shout("Moving");
			}
		}
		
		//Iterate through all visible enemy actors
		for(Actor e : state.getEnemyActors()) {
			if(wizard.canCast( e)) {
				wizard.castMagic(e);
			}
		}
		
		for(Actor e : state.getNeutralActors()) {
			if(wizard.canCast(e)) {
				wizard.castMagic(e);
			}
		}
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
	
	private int getStrat(AIGameState state){
		int count = 0;
		int highscore = 0;
		for(int i = 0; i < state.getNumberOfPlayers(); i++){
			if(state.getPlayerScore(i) > highscore){
				highscore = state.getPlayerScore(i);
			}
		}
		
		if(state.getPlayerScore(myTeam) >= highscore && state.getMyMana() > 400){
			return 4;//we are the best so let's screw with people
		}
		else if(state.getPlayerScore(myTeam) <= highscore && state.getMyMana() > 400){
			return 3; // low pts  high mana
		}
		else if(state.getPlayerScore(myTeam) > highscore && state.getMyMana() <= 400){
			return 2; // high pts  low mana
		}
		else{
			return 1; //low pts  low mana
		}
	}
	
	/**
	 * Do something with your hats!!!
	 * @param state
	 */
	private void moveHats(AIGameState state) {
		ArrayList<Node> hatPaths = new ArrayList<Node>();
		//ArrayList<Actor> enemyActors = new ArrayList<Actor>();
		state.getEnemyActors();
		for(Hat hat : state.getMyHats()) {
			while(hat.canAct()){
				hatPaths = state.getPath(hat, state.getBase(state.getMyTeamNumber()), pathWeight);
				for(Node n : hatPaths){ //loops through all nodes through given path										
					if(n.isVisible() && n.isPassable()){
						hat.move(hatPaths);
						return;
					}
					else{
						//find a new path
						//hatPaths = state.getPath(hat, state.getBase(state.getMyTeamNumber()), pathWeight);
						return;
					}
				}
			}
		}
	}
}
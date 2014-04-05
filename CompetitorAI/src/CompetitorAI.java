import bonzai.api.*;

import java.util.*;

public class CompetitorAI implements AI {
	
	private WeightComparator pathWeight = new CompetitorWeightComparator();
	private boolean firstTurn = true;

	public static int myTeam = -1;
	public static int scores[] = new int[4];

    private ArrayList<Node> wizardPath;
    private ArrayList<Node> explorePoints;
    private int exploreIndex;
	
	private Node mapGuess[][];
	private Node bases[];

	
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
            exploreIndex = 0;
			takeFirstTurn(state);
			firstTurn = false;
		}
		
		updateGuess(state);
		
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
		
		myTeam = state.getMyTeamNumber();
		
		//setup the map-saving
		mapGuess = new Node[state.getWidth()][state.getHeight()];
		bases = new Node[state.getNumberOfPlayers()];
		//save all spaces we can see at the start
	}
	
	/**
	 * Move or castMagic with your Wizard
	 * @param state
	 */
	private void wizardBrain(AIGameState state) {
		Wizard wizard = state.getMyWizard();

        //Can't see anything, so explore
        if(state.getNeutralActors().size() == 0){  	
            if(wizardPath == null){
                if(wizard.getLocation().getX() == explorePoints.get(exploreIndex).getX() &&
                   wizard.getLocation().getY() == explorePoints.get(exploreIndex).getY()){
                    exploreIndex++;
                }
                if(exploreIndex >= explorePoints.size()) exploreIndex = 0;
                wizardPath = state.getPath(wizard, explorePoints.get(exploreIndex), pathWeight);
            }
            if(wizard.canMove(wizard.getDirection(wizardPath)))
                wizard.move(wizard.getDirection(wizardPath));
            else
                wizardPath = null;
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
		
		int numScouts = state.getMyScouts().size();
		boolean firstScout = true;
		Node set;
		
		for(Scout scout : state.getMyScouts()) {
			
			if( firstScout ){
				
				//follow wizard so you can see people
				scout.doubleMove(state.getPath(scout, state.getMyWizard().getLocation(), pathWeight));
				
				firstScout = false;
				
			}
			
			//move randomly and see what's up
			scout.doubleMove((int)(Math.random()*4), (int)(Math.random()*4));


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
	
	private void updateGuess(AIGameState state) {
		
		Node cur;
		
		for( int i=0; i<state.getWidth(); i++ )
			for(int j=0; j<state.getHeight(); j++ ){
				cur = state.getNode(i, j);
				
				if( cur == null || !cur.isVisible() ) continue;
				
				mapGuess[cur.getX()][cur.getY()] = cur;
			}
		
	}
}
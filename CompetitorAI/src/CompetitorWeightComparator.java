import java.util.ArrayList;

import bonzai.api.*;


public class CompetitorWeightComparator implements WeightComparator
{
	public CompetitorWeightComparator() {
		super();
	}
	
	@Override
	public double compare(Node arg0) {
		ArrayList<Actor> actorsOnNode = new ArrayList<Actor>();
		ArrayList<Actor> actorsBelow = new ArrayList<Actor>();
		ArrayList<Actor> actorsRight = new ArrayList<Actor>();
		ArrayList<Actor> actorsLeft = new ArrayList<Actor>();
		ArrayList<Actor> actorsAbove = new ArrayList<Actor>();
		actorsOnNode = arg0.getActors();
		actorsBelow = arg0.getNearby(arg0.DOWN).getActors();
		actorsLeft = arg0.getNearby(arg0.LEFT).getActors();
		actorsAbove = arg0.getNearby(arg0.UP).getActors();
		actorsRight = arg0.getNearby(arg0.RIGHT).getActors();
		
		for(Actor a : actorsOnNode){
			if( (a.getTeam() != CompetitorAI.myTeam && a.WIZARD == 1) || !arg0.isPassable()){
				//bad path 
				return 100;
			}
		}
		for(Actor a : actorsBelow){
			if(a.getTeam() != CompetitorAI.myTeam && a.WIZARD == 1){
				//bad path 
				return 50;
			}
		}
		for(Actor a : actorsLeft){
			if(a.getTeam() != CompetitorAI.myTeam && a.WIZARD == 1){
				//bad path 
				return 50;
			}
		}
		for(Actor a : actorsAbove){
			if(a.getTeam() != CompetitorAI.myTeam && a.WIZARD == 1){
				//bad path 
				return 50;
			}
		}
		for(Actor a : actorsRight){
			if(a.getTeam() != CompetitorAI.myTeam && a.WIZARD == 1){
				//bad path 
				return 50;
			}
		}
		/**if(arg0.getNearby(arg0.DOWN).getActors().contains(a) || arg0.getNearby(arg0.LEFT).getActors().contains(a) 
				|| arg0.getNearby(arg0.UP).getActors().contains(a) || arg0.getNearby(arg0.RIGHT).getActors().contains(a)){
			//caution!!! that path contains an enemy
			
		}*/
		
		return 0;

	}
}
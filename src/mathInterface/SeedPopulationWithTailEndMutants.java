package mathInterface;

public class SeedPopulationWithTailEndMutants extends GAShakeupStrategy {
   //1. on construction:
	/* prepare a large population of individuals, using strictly the automated portion of the
	 * genetic algorithm.
	 * when invoked, take the tail end of that population.
	 * allow for more mutations than the other one does.
	 * get a reference to the processing app's current population (or the next population, to be provided by the iterator actually-- need to do that)
	 * and, delete n random chromosomes from that population,
	 * insert the tail ends in their place.
	 * remove the tail ends from the stored population (if called again, we can just make use of whatever else we have. we'll create enough individuals that we won't run out for... a long time)
	
	 */
	

	
	@Override
	public void shakeUp() {
		

	}

	@Override
	public void undo() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean hasUndoneChanges() {
		// TODO Auto-generated method stub
		return false;
	}

}

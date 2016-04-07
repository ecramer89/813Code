package mathInterface;

public class RewardDeviationFromTemplates extends GAShakeupStrategy {

	//substitue the fitness function that is currently used, for our fitness function.
	//(but when to switch back?)
	//both this oe, and also the strategy that change weights uses... may need to be "rolled back" at some point.
	//so, we need to store infor that is needed to undo our changes
	//and, everything needsto be able to "undo" what it did...
	//(may be -- impossible for-- the- no, it will save a copy of the original population from which it branched, and then it will just swap the next for the original one entirely. so as though the branching never happened)
	
	
	@Override
	public void shakeUp() {
		// TODO Auto-generated method stub

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

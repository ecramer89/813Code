package mathInterface;

public class RewardDeviationFromTemplates extends GAShakeupStrategy {

	boolean changed=false;
	
	public RewardDeviationFromTemplates(IECFitnessFunction fitnessFunction) {
		super(fitnessFunction);
	}

	@Override
	public void shakeUp() {
		fitnessFunction.invertSign();
		changed=true;

	}

	@Override
	public void undo() {
		fitnessFunction.invertSign();
		changed=false;
		
	}

	@Override
	public boolean hasUndoneChanges() {
		// TODO Auto-generated method stub
		return changed;
	}

}

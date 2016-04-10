package mathInterface;

public class RewardDeviationFromTemplates extends GAShakeupStrategy {

	
	public RewardDeviationFromTemplates(JGAPAdapter jgapAdaptor) {
		super(jgapAdaptor);
	}



	@Override
	public void applyChange() {
		jgapAdaptor.getFitnessFunction().invertSign();
		
	}



	@Override
	protected void revertChange() {
		jgapAdaptor.getFitnessFunction().invertSign();
		
	}

	
}

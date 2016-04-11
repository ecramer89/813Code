package mathInterface;

public class IncreaseMutationRate extends GAShakeupStrategy {
	
	
	int mutationRateScale=2;
	
	public IncreaseMutationRate(JGAPAdapter jgapAdaptor) {
		super(jgapAdaptor);
		
	}


	@Override
	protected void applyChange() {
		jgapAdaptor.accelerateMutationRate(mutationRateScale);
		
	}

	@Override
	protected void revertChange() {
		jgapAdaptor.deaccelerateMutationRate(mutationRateScale);
		
	}

}

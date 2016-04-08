package mathInterface;

public class AdjustWeights extends GAShakeupStrategy {

	public AdjustWeights(IECFitnessFunction fitnessFunction) {
		super(fitnessFunction);
		// TODO Auto-generated constructor stub
	}

	private static final double UNCHANGED=13;
	double last_adjust=UNCHANGED;
	
	
	@Override
	public void shakeUp() {
		last_adjust=Math.random();
		if(Math.random()<.5) last_adjust*=-1;
		System.out.println("---Message from AdjustWeights---");
		System.out.println("adjust weight value: "+last_adjust);
		fitnessFunction.adjustWeights(last_adjust);

	}

	@Override
	public void undo() {
		last_adjust*=-1;
		fitnessFunction.adjustWeights(last_adjust);
		last_adjust=UNCHANGED;
		
		
	}

	@Override
	public boolean hasUndoneChanges() {
		System.out.println("---Message from AdjustWeights---");
		System.out.println("---checked whether we have undone changes---");
		boolean result=last_adjust!=UNCHANGED;
		System.out.println("has undone changes: "+result);
		return result;
	}

}

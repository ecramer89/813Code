package mathInterface;

public abstract class GAShakeupStrategy {
	
	IECFitnessFunction fitnessFunction;
	
	
	public GAShakeupStrategy(IECFitnessFunction fitnessFunction){
		this.fitnessFunction=fitnessFunction;
	}

	
	public abstract void shakeUp();
	
	public abstract void undo();

	public abstract boolean hasUndoneChanges();

}



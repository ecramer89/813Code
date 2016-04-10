package mathInterface;

public abstract class GAShakeupStrategy {
	
	JGAPAdapter jgapAdaptor;
	boolean changed;
	
	
	public GAShakeupStrategy(JGAPAdapter jgapAdaptor){
		this.jgapAdaptor=jgapAdaptor;
	}

	
	public final void shakeUp(){
		changed=true;
		applyChange();
	}
	
	public final void undo(){
		changed=false;
		revertChange();
	}
	
	protected abstract void applyChange();
	protected abstract void revertChange();

	public final boolean hasUndoneChanges(){
		return changed;
	}

}



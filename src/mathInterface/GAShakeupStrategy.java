package mathInterface;

import org.jgap.Population;

public abstract class GAShakeupStrategy {
	
	Population populationBeforeAction;
	JGAPAdapter jgapAdaptor;
	boolean changed;
	
	
	public GAShakeupStrategy(JGAPAdapter jgapAdaptor){
		this.jgapAdaptor=jgapAdaptor;
	}

	
	public final void shakeUp(){
		changed=true;
		populationBeforeAction=(Population)jgapAdaptor.getCurrentPopulation().clone();
		applyChange();
	}
	
	public final void undo(){
		changed=false;
		populationBeforeAction=(Population)jgapAdaptor.getCurrentPopulation().clone();
		revertChange();
	}
	
	protected abstract void applyChange();
	protected abstract void revertChange();

	public final boolean hasUndoneChanges(){
		return changed;
	}
	
	public final Population getPopulationBeforeAction(){
		return populationBeforeAction;
	}


	public abstract String getAuditInformation();

}



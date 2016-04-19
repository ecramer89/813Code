package mathInterface;

import org.jgap.Configuration;
import org.jgap.InvalidConfigurationException;
import org.jgap.impl.DefaultConfiguration;
import org.jgap.impl.MutationOperator;

public class SeedPopulationWithTailEnd extends GAShakeupStrategy {

	
	UserChromosomeSelector selector;
    double mostToLeastFitScale=-.5;
	
	public SeedPopulationWithTailEnd(JGAPAdapter jgapAdaptor) {
		super(jgapAdaptor);
        selector=jgapAdaptor.getUserChromosomeSelector();
	}



	@Override
	public void applyChange() {
     /* System.out.println("message from seed population with tail end:");
      System.out.println("called apply change");*/
		selector.adjustProportionsOfFittestToLeastFit(mostToLeastFitScale);
		
		
	}



	@Override
	protected void revertChange() {
		/* System.out.println("message from seed population with tail end:");
	     System.out.println("called undo change");*/
		selector.adjustProportionsOfFittestToLeastFit(-mostToLeastFitScale);
	}



	@Override
	public String getAuditInformation() {
		// TODO Auto-generated method stub
		return " We've made it so the set of feedbacks you see includes more of the so-called 'least fit' feedbacks and fewer of the so-called 'most fit'";
	}



}

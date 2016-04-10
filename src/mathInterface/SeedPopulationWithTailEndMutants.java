package mathInterface;

import org.jgap.Configuration;
import org.jgap.InvalidConfigurationException;
import org.jgap.impl.DefaultConfiguration;
import org.jgap.impl.MutationOperator;

public class SeedPopulationWithTailEndMutants extends GAShakeupStrategy {

	int mutationRateScale=2;
	UserChromosomeSelector selector;
    double mostToLeastFitScale=.3;
	
	public SeedPopulationWithTailEndMutants(JGAPAdapter jgapAdaptor) {
		super(jgapAdaptor);
        selector=jgapAdaptor.getUserChromosomeSelector();
	}



	@Override
	public void applyChange() {
		//incease the mutation rate...
		jgapAdaptor.accelerateMutationRate(mutationRateScale);
		//...but now... need also to seed the population with the tail end mutants for
		//one generation.
		selector.adjustProportionsOfFittestToLeastFit(mostToLeastFitScale);
		
		
	}



	@Override
	protected void revertChange() {
		//decrease the mutation rate 
		jgapAdaptor.deaccelerateMutationRate(mutationRateScale);
		selector.adjustProportionsOfFittestToLeastFit(-mostToLeastFitScale);
	}



}

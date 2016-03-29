package mathInterfaceBaseCode;

import org.jgap.FitnessFunction;
import org.jgap.IChromosome;

/*
 * Maps the fitness of this individual to the rating that the user assigned to it.
 * (*assumes that we will set the fitness value of each chromosome to be the userès rating)
 */
public class IECFitnessFunction extends FitnessFunction {
	
	

	@Override
	protected double evaluate(IChromosome a_subject) {
		Integer rating= (Integer)a_subject.getGene(IECChromosomeToUIManifester.USER_RATING_GENE_POS).getAllele();
	    return rating.intValue();
	}

}

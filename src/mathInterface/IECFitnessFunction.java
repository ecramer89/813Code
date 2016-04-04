package mathInterface;

import org.jgap.FitnessFunction;
import org.jgap.IChromosome;

/*
 * Maps the fitness of this individual to the rating that the user assigned to it.
 * (*assumes that we will set the fitness value of each chromosome to be the userès rating)
 */
public class IECFitnessFunction extends FitnessFunction {

	private boolean incorporateUserRating=true;

	@Override
	protected double evaluate(IChromosome a_subject) {
		double expectedFitness=FeedbackTemplate.calculateFitnessForChildAptitude(ChromosomeToFeedbackManifester.createFeedback(a_subject));
		
		
		double userScore=0;
		if(incorporateUserRating){
			userScore= (Double)a_subject.getGene(GenePosition.USER_SCORE.ordinal()).getAllele();
		}
		return userScore;
		//to do... maybe incorporate distance from prototype feedback strategies?
	}

	public void setIncorporateUserRating(boolean incorporate){
		incorporateUserRating=incorporate;
	}



	public static void setUserScore(IChromosome a_subject, double score){
		a_subject.getGene(GenePosition.USER_SCORE.ordinal()).setAllele(score);	
	}
	
	
	
}



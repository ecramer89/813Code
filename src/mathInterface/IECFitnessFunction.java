package mathInterface;

import org.jgap.FitnessFunction;
import org.jgap.IChromosome;

/*
 * Maps the fitness of this individual to the rating that the user assigned to it.
 * (*assumes that we will set the fitness value of each chromosome to be the userès rating)
 */
public class IECFitnessFunction extends FitnessFunction {
    private double SCALE_FACTOR=50;
	private boolean incorporateUserRating=true;

	@Override
	protected double evaluate(IChromosome a_subject) {
		double expectedFitness=FeedbackTemplate.getInstance().calculateExpectedFitness(ChromosomeToFeedbackManifester.createFeedback(a_subject));
		
		
		double userScore=0;
		if(incorporateUserRating){
			userScore= (Double)a_subject.getGene(GenePosition.USER_SCORE.ordinal()).getAllele();
		}
		
	
		//JGAP forbids negative fitness values... originally i took the min of 0 and...
		//but i found that resulted in a lot of 0s.
		//so instead i need to reward distance from negatives a lot.
		//instead of punishing proximity to bad a lot (withn negatives)
		//which was my original strategy
		
		return expectedFitness+userScore;
		
	}
	
	
	






	public void setIncorporateUserRating(boolean incorporate){
		incorporateUserRating=incorporate;
	}



	public static void setUserScore(IChromosome a_subject, double score){
		a_subject.getGene(GenePosition.USER_SCORE.ordinal()).setAllele(score);	
	}
	
	
	
}



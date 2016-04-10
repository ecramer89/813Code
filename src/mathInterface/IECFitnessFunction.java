package mathInterface;

import org.jgap.FitnessFunction;
import org.jgap.IChromosome;

/*
 * Maps the fitness of this individual to the rating that the user assigned to it.
 * (*assumes that we will set the fitness value of each chromosome to be the userès rating)
 */
public class IECFitnessFunction extends FitnessFunction {
	private double SCALE_FACTOR=50;
	

	private double default_user_score_weight=.8;
	private double default_expected_fitness_weight=1-default_user_score_weight;

	private int sign=1;

	@Override
	protected double evaluate(IChromosome a_subject) {
		double expec_weight=default_expected_fitness_weight;
		double user_score_weight=default_user_score_weight;
		
		
		double expectedFitness=FeedbackTemplate.getInstance().calculateExpectedFitness(ChromosomeToFeedbackManifester.createFeedback(a_subject), sign);

	    double userScore= (Double)a_subject.getGene(GenePosition.USER_SCORE.ordinal()).getAllele();
		
	    
	    
	    //just use the automated part of the fitness function if the individual lacks a user rating.
	    if(Feedback.discount(userScore)){
	    	default_expected_fitness_weight=1.0;
			user_score_weight=0;
			
		}


		//JGAP forbids negative fitness values... originally i took the min of 0 and...
		//but i found that resulted in a lot of 0s.
		//so instead i need to reward distance from negatives a lot.
		//instead of punishing proximity to bad a lot (withn negatives)
		//which was my original strategy

		return default_expected_fitness_weight*expectedFitness+default_user_score_weight*userScore;

	}



	public void adjustWeights(double adjust_actual_fitness_weight){
		/*System.out.println("--Mesage fro IECFitnessFunction--");
		System.out.println("PRE-Adjusted actual fitness weight: "+default_user_score_weight);
		System.out.println("PRE-Adjusted expected fitness weight: "+default_expected_fitness_weight);*/
		default_user_score_weight=ProcessingApplication.getInstance().constrain(default_user_score_weight+adjust_actual_fitness_weight,0,1);
		default_expected_fitness_weight=1-default_user_score_weight;
		/*System.out.println("Adjusted actual fitness weight: "+default_user_score_weight);
		System.out.println("Adjusted expected fitness weight: "+default_expected_fitness_weight);*/
	}

	public void invertSign(){
		sign=(sign<1? 1:-1);
	}






	public static void setUserScore(IChromosome a_subject, double score){
		a_subject.getGene(GenePosition.USER_SCORE.ordinal()).setAllele(score);	
	}



}



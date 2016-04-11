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
	//boolean done_testing=false;
	@Override
	protected double evaluate(IChromosome a_subject) {
		double expec_weight=default_expected_fitness_weight;
		double user_score_weight=default_user_score_weight;


		double expectedFitness=FeedbackTemplate.getInstance().calculateExpectedFitness(ChromosomeToFeedbackManifester.createFeedback(a_subject), sign);
		double userScore= (Double)a_subject.getGene(GenePosition.USER_SCORE.ordinal()).getAllele();


		//just use the automated part of the fitness function if the individual lacks a user rating.
		if(Feedback.discount(userScore)){
			expec_weight=1.0;
			user_score_weight=0;

		}
		else {
			System.out.println("someone has a non negative user score...");
		}


	    //put user score (raw, between 0 and 1) on same scale as 
		//expected fitness; 
		//weight the two constituents of the overall fitness value
		//(i elected to allow for abolute large values of the fitness function
		//over normalizing; greater ranges seem to yield better performance of the GA
		double weighted_exp_fitness=expec_weight*expectedFitness;
		double scaled_user_score=userScore*FeedbackTemplate.getMaximumExpectedFitness();
		double weighted_user_score_fitness=user_score_weight*scaled_user_score;

		return weighted_exp_fitness+weighted_user_score_fitness;

	}



	public void adjustWeights(double adjust_actual_fitness_weight){
		System.out.println("--Mesage fro IECFitnessFunction--");
		System.out.println("PRE-Adjusted actual fitness weight: "+default_user_score_weight);
		System.out.println("PRE-Adjusted expected fitness weight: "+default_expected_fitness_weight);
		default_user_score_weight=ProcessingApplication.getInstance().constrain(default_user_score_weight+adjust_actual_fitness_weight,0,1);
		default_expected_fitness_weight=1-default_user_score_weight;
		System.out.println("Adjusted actual fitness weight: "+default_user_score_weight);
		System.out.println("Adjusted expected fitness weight: "+default_expected_fitness_weight);
	}

	public void invertSign(){
		System.out.println("--Mesage fro IECFitnessFunction--");
		System.out.println("--pre adjust sign: "+sign);
		sign=(sign<1? 1:-1);
		System.out.println("--post adjust sign: "+sign);
	}






	public static void setUserScore(IChromosome a_subject, double score){
		a_subject.getGene(GenePosition.USER_SCORE.ordinal()).setAllele(score);	
	}



}



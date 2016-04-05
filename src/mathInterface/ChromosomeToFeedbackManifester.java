package mathInterface;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import org.jgap.Chromosome;
import org.jgap.Configuration;
import org.jgap.Gene;
import org.jgap.IChromosome;
import org.jgap.InvalidConfigurationException;
import org.jgap.impl.DoubleGene;
import org.jgap.impl.IntegerGene;

import processing.core.PImage;



/*
 * class that is responsible for translating an individual of the population 
 * of chromosomes into feedback variables 
 *  
 */
public class ChromosomeToFeedbackManifester  {
	static ProcessingApplication processing;
	static ChromosomeToFeedbackManifester theInstance;
	private static IChromosome curr_genotype;
	private static Feedback curr_phenotype;

   public String currFeedback(){
	   return curr_phenotype.toString();
   }

	private ChromosomeToFeedbackManifester(){
		processing=(ProcessingApplication) ProcessingApplication.getInstance();

	}


	public static ChromosomeToFeedbackManifester getInstance(){
		if(theInstance==null)
			theInstance=new ChromosomeToFeedbackManifester();
		return theInstance;
	}

	public void setCurrentFeedbackChromosome(IChromosome iChromosome){
		this.curr_genotype=iChromosome;
		this.curr_phenotype=new Feedback();
		
		setStaticFeedbackParameters(curr_phenotype,curr_genotype);
	}


	/* caches the values of the feedback parametes, with respect to the current chromosome (individual) being evaluated */
	private static void setStaticFeedbackParameters(Feedback phenotype, IChromosome genotype){
		updateFeedbackDelay(phenotype, genotype);

		updateVerificationParameters(phenotype, genotype);
		updateElaborationParameters(phenotype, genotype);
		updateDirectiveFeedbackParameters(phenotype, genotype);

		updateAllowingResubmission(phenotype, genotype);
		
		
		updateIdentificationColor(phenotype, genotype);
	}

	
	
	private static void updateIdentificationColor(Feedback phenotype, IChromosome genotype) {
		phenotype.updateIdentificationColor(new int[]{getIntegerAllelle(GenePosition.TEXT_COLOR_R, genotype),getIntegerAllelle(GenePosition.TEXT_COLOR_G, genotype),getIntegerAllelle(GenePosition.TEXT_COLOR_B, genotype)});
		
	}

	private static void updateAllowingResubmission(Feedback phenotype, IChromosome genotype){
		phenotype.updateAllowingResubmission(getDoubleAllelle(GenePosition.P_ALLOW_RESUBMIT, genotype));

	}

	private static void updateDirectiveFeedbackParameters(Feedback phenotype, IChromosome genotype) {
		phenotype.updateDirectiveFeedbackParameters(getDoubleAllelle(GenePosition.P_DIRECTIVE, genotype));
		/* types of directive feedback */
		//provide the correct response
		phenotype.updateProvideCorrectAnswerParameters(getDoubleAllelle(GenePosition.P_CORRECT_RESPONSE,genotype),getIntegerAllelle(GenePosition.CORRECT_RESPONSE_DELAY, genotype));

		//highlight the errors in the childs solution

		phenotype.updateErrorFlagParameters(getDoubleAllelle(GenePosition.P_ERROR_FLAG, genotype), getIntegerAllelle(GenePosition.ERROR_FLAG_DELAY, genotype));

	}

	private static void updateElaborationParameters(Feedback phenotype, IChromosome genotype) {
		phenotype.updateElaborationParameters(getDoubleAllelle(GenePosition.P_ELABORATE, genotype));

		phenotype.updateAttributeIsolationParameters(getDoubleAllelle(GenePosition.P_ATTRIBUTE_ISOLATION, genotype), getIntegerAllelle(GenePosition.ATTRIBUTE_ISOLATION_DURATION, genotype));
	}


	private static void updateVerificationParameters(Feedback phenotype, IChromosome genotype) {

		phenotype.updateVerificationParameters(getIntegerAllelle(GenePosition.VERIFICATION_TYPE, genotype),getIntegerAllelle(GenePosition.VERIFICATION_MODALITY, genotype));
	}



	private static void updateFeedbackDelay(Feedback phenotype, IChromosome genotype) {
		phenotype.updateFeedbackDelay(getIntegerAllelle(GenePosition.FEEDBACK_DELAY, genotype));
	}



	/* methods for providing the feedback, assuming the variables were set */
	public void provideFeedback(MathProblem problem) {
		curr_phenotype.provideFeedback(problem);
		processing.mathProblemUI.setFeedbackScreens(getFeedbackScreens());
	}




	public List<DisplayScreen> getFeedbackScreens() {
		return curr_phenotype.getFeedbackScreens();
	}

	public void initializeFeedbackScreens() {
		curr_phenotype.initializeVerificationScreen(processing.makeEmptyScreenSizedToApplication());
		curr_phenotype.initializeCorrectAnswerScreen(processing.mathProblemUI.getAnswerScreenTransform());
		curr_phenotype.initializeErrorFlagScreen(processing.mathProblemUI.getAnswerScreenTransform());
		curr_phenotype.initializeAttributeIsolationScreen(processing.mathProblemUI.getProblemScreenTransform());
		curr_phenotype.initializeResubmitScreen(processing.makeEmptyScreenSizedToApplication());
	}


	public boolean acceptingResponse(){
		return !feedbackInProcess();
	}

	private boolean feedbackInProcess(){
		return curr_phenotype.feedbackInProcess();

	}

	
	private static double getDoubleAllelle(GenePosition pos,IChromosome genotype){
		return (Double)genotype.getGene(pos.ordinal()).getAllele();

	}

	private static int getIntegerAllelle(GenePosition pos, IChromosome genotype){
		return (Integer)genotype.getGene(pos.ordinal()).getAllele();

	}


	public void resetFeedbackScreens() {
		curr_phenotype.resetFeedbackScreens();
		
	}

	public int[] currFeedbackColor() {
		return curr_phenotype.getIdColor();
	}

	public static Feedback createFeedback(IChromosome a_subject) {
		Feedback result=new Feedback();
		
		setStaticFeedbackParameters(result,a_subject);
		return result;
	}





}

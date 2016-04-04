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
	private IChromosome curr_genotype;
	private Feedback curr_phenotype;

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

	public void updateFeedbackChromosome(IChromosome iChromosome){
		this.curr_genotype=iChromosome;
		this.curr_phenotype=new Feedback();
		
		updateCurrentFeedbackParameters();
	}


	/* caches the values of the feedback parametes, with respect to the current chromosome (individual) being evaluated */
	private void updateCurrentFeedbackParameters(){
		updateFeedbackDelay();

		updateVerificationParameters();
		updateElaborationParameters();
		updateDirectiveFeedbackParameters();

		updateAllowingResubmission();
		
		
		updateIdentificationColor();
	}

	
	
	private void updateIdentificationColor() {
		curr_phenotype.updateIdentificationColor(new int[]{getIntegerAllelle(GenePosition.TEXT_COLOR_R),getIntegerAllelle(GenePosition.TEXT_COLOR_G),getIntegerAllelle(GenePosition.TEXT_COLOR_B)});
		
	}

	private void updateAllowingResubmission(){
		curr_phenotype.updateAllowingResubmission(getDoubleAllelle(GenePosition.P_ALLOW_RESUBMIT));

	}

	private void updateDirectiveFeedbackParameters() {
		curr_phenotype.updateDirectiveFeedbackParameters(getDoubleAllelle(GenePosition.P_DIRECTIVE), getIntegerAllelle(GenePosition.DIRECTIVE_DELAY));
		/* types of directive feedback */
		//provide the correct response
		curr_phenotype.updateProvideCorrectAnswerParameters(getDoubleAllelle(GenePosition.P_CORRECT_RESPONSE),getIntegerAllelle(GenePosition.CORRECT_RESPONSE_DELAY));

		//highlight the errors in the childs solution

		curr_phenotype.updateErrorFlagParameters(getDoubleAllelle(GenePosition.P_ERROR_FLAG), getIntegerAllelle(GenePosition.ERROR_FLAG_DELAY));

	}

	private void updateElaborationParameters() {
		curr_phenotype.updateElaborationParameters(getDoubleAllelle(GenePosition.P_ELABORATE), getIntegerAllelle(GenePosition.DELAY_UNTIL_ELABORATE));

		curr_phenotype.updateAttributeIsolationParameters(getDoubleAllelle(GenePosition.P_ATTRIBUTE_ISOLATION), getIntegerAllelle(GenePosition.ATTRIBUTE_ISOLATION_DURATION));
	}


	private void updateVerificationParameters() {

		curr_phenotype.updateVerificationParameters(getIntegerAllelle(GenePosition.VERIFICATION_TYPE),getIntegerAllelle(GenePosition.VERIFICATION_MODALITY));
	}



	private void updateFeedbackDelay() {
		curr_phenotype.updateFeedbackDelay(getIntegerAllelle(GenePosition.FEEDBACK_DELAY));
	}



	/* methods for providing the feedback, assuming the variables were set */
	public void provideFeedback(MathProblem problem) {
		curr_phenotype.provideFeedback(problem);
		processing.mathProblemUI.setFeedbackScreens(getFeedbackScreens());
	}


	private boolean eventOccurs(double p_event) {
		return Math.random()<p_event;
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

	
	private double getDoubleAllelle(GenePosition pos){
		return (Double)curr_genotype.getGene(pos.ordinal()).getAllele();

	}

	private int getIntegerAllelle(GenePosition pos){
		return (Integer)curr_genotype.getGene(pos.ordinal()).getAllele();

	}



	public void resetFeedbackScreens() {
		curr_phenotype.resetFeedbackScreens();
		
	}

	public int[] currFeedbackColor() {
		// TODO Auto-generated method stub
		return curr_phenotype.getIdColor();
	}





}

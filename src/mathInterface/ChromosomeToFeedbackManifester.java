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
public class ChromosomeToFeedbackManifester implements Observer {
	static ProcessingApplication processing;
	static ChromosomeToFeedbackManifester theInstance;
	/* some constants... these should include some basic timing parameters such as the duration
	 * of the feedback events, and probably the font colours for specific kinds of feedbacks
	 * i.e., likely, convention to map green to correct and red to incorrect...
	 * so map yellow to attribute isolation colour and such.
	 */
	private static final int DEFAULT_SCREEN_DURATION = 2000;
	private static final int[] ATTRIBUTE_HIGHLIGHT_COLOR = new int[]{255,255,0};
	private static final int[] CORRECT_VERIFICATION_COLOR=new int[]{0,255,0};
	private static final int[] INCORRECT_VERIFICATION_COLOR=new int[]{255,0,0};
	private static final int[] ERROR_COLOR = INCORRECT_VERIFICATION_COLOR;


	/*for verification*/
	//leave this at 1 for now (always verify; sometimes elaborate)
	private double p_verify=1.0;

	//verification type int codes
	static final int EXPLICIT_VERIFICATION=0;
	static final int IMPLICIT_CORRECT_VERIFICATION=1;
	static final int IMPLICIT_INCORRECT_VERIFICATION=2;
	//verification modality int codes
	private static final int TEXT_IMAGE_VERIFICATION = 0;
	private static final int ALL_VERIFICATION = 1;
	private static final int IMAGE_AUDIO_VERIFICATION = 2;
	private static final int TEXT_VERIFICATION = 3;
	private static final int TEXT_AUDIO_VERIFICATION = 4;
	private static final int IMAGE_VERIFICATION = 5;

	static final String DEFAULT_CORRECT_VERIFICATION="Correct";
	static final String DEFAULT_INCORRECT_VERIFICATION="Incorrect";
	static final String IMPLICIT_VERIFICATION_MESSAGE = "";
	private static final int PRE_FEEDBACK = -1;



	static PImage DEFAULT_CORRECT_VERIFICATION_IMAGE=null;
	static PImage DEFAULT_INCORRECT_VERIFICATION_IMAGE=null;
	
	
	static final String RESUBMIT_MESSAGE="Try again!";
	private VariableText allowResubmitText=new VariableText(RESUBMIT_MESSAGE,ProcessingApplication.UI_FONT_COLOR,0,0,ProcessingApplication.UI_FONT_SIZE);

	IChromosome individual;
	/*parameters that are interpreted from the individual (chromosome)*/
	private double feedback_delay;
	
	/* for directive feedback */
	private double p_directive=1.0;
	private int directive_delay;

	private double p_correct_answer=1.0;
	private int correct_answer_delay;

	private double p_error_flag=1.0;
	private int error_flag_delay;


	/* for elaboration */
	private double p_elaborate=1.0;
	private int elaboration_delay;


	private double p_attribute_isolation=1.0;
	private int attributeIsolationDuration;
	private int attributeIsolationDelay;


	private double p_allow_resubmit;
	private boolean allowing_resubmission;
	
	
	/*variables particular to different feedback events*/
	/*general*/
	private int[] text_color=new int[]{0,0,0};

	/*verification*/
	private int verificationType;
	private int verificationModality;
	private boolean includeVerificationText;
	private boolean includeVerificationImage;

	private VariableText verification_text=new VariableText("",INCORRECT_VERIFICATION_COLOR,0,0,processing.UI_FONT_SIZE);
	private String correctVerificationMessage=DEFAULT_CORRECT_VERIFICATION;
	private String incorrectVerificationMessage=DEFAULT_INCORRECT_VERIFICATION;

	private PImage correctVerificationImage;
	private PImage incorrectVerificationImage;
	private PImage PLACEHOLDER_IMAGE; 


	private int verification_duration=300; //ms


	private DisplayScreen verificationScreen;

	private DisplayScreen correctAnswerScreen;
	private DisplayScreen errorFlagScreen;

	private DisplayScreen attributeIsolationScreen;
	private DisplayScreen allowResubmitScreen;

	private List<DisplayScreen> feedbackScreens;
	private int numScreensCompleted=PRE_FEEDBACK;
	
	
	
	





	private ChromosomeToFeedbackManifester(){
		processing=(ProcessingApplication) ProcessingApplication.getInstance();

		loadImages();
	}

	private void loadImages() {
		DEFAULT_INCORRECT_VERIFICATION_IMAGE=processing.loadImage("C:/Users/root960/Desktop/emily/IAT813/project/applicationData/images/incorrect.png");
		DEFAULT_CORRECT_VERIFICATION_IMAGE=processing.loadImage("C:/Users/root960/Desktop/emily/IAT813/project/applicationData/images/correct.png");


		PLACEHOLDER_IMAGE=processing.loadImage("C:/Users/root960/Desktop/emily/IAT813/project/applicationData/images/placeholder.png");

		correctVerificationImage=DEFAULT_CORRECT_VERIFICATION_IMAGE;
		incorrectVerificationImage=DEFAULT_INCORRECT_VERIFICATION_IMAGE;

	}

	public static ChromosomeToFeedbackManifester getInstance(){
		if(theInstance==null)
			theInstance=new ChromosomeToFeedbackManifester();
		return theInstance;
	}

	public void updateFeedbackChromosome(IChromosome iChromosome){
		this.individual=iChromosome;
		updateCurrentFeedbackParameters();
	}


	/* caches the values of the feedback parametes, with respect to the current chromosome (individual) being evaluated */
	private void updateCurrentFeedbackParameters(){
		updateFeedbackDelay();
		updateFeedbackTextColor();
		updateVerificationParameters();
		updateElaborationParameters();
		updateDirectiveFeedbackParameters();
		
		updateAllowingResubmission();
	}
	
	private void updateAllowingResubmission(){
		p_allow_resubmit=getDoubleAllelle(GenePosition.P_ALLOW_RESUBMIT);
	}

	private void updateDirectiveFeedbackParameters() {
		p_directive=getDoubleAllelle(GenePosition.P_DIRECTIVE);
		directive_delay=getIntegerAllelle(GenePosition.DIRECTIVE_DELAY);

		/* types of directive feedback */
		//provide the correct response
		p_correct_answer=getDoubleAllelle(GenePosition.P_CORRECT_RESPONSE);
		correct_answer_delay=getIntegerAllelle(GenePosition.CORRECT_RESPONSE_DELAY);

		//highlight the errors in the childs solution
		p_error_flag=getDoubleAllelle(GenePosition.P_ERROR_FLAG);
		error_flag_delay=getIntegerAllelle(GenePosition.ERROR_FLAG_DELAY);

	}

	private void updateElaborationParameters() {
		p_elaborate=getDoubleAllelle(GenePosition.P_ELABORATE);
		elaboration_delay=getIntegerAllelle(GenePosition.DELAY_UNTIL_ELABORATE);

		//different types of elaborative feedback
		p_attribute_isolation=getDoubleAllelle(GenePosition.P_ATTRIBUTE_ISOLATION);
		attributeIsolationDuration=getIntegerAllelle(GenePosition.ATTRIBUTE_ISOLATION_DURATION);
	}


	private void updateVerificationParameters() {

		verificationType=(Integer)individual.getGene(GenePosition.VERIFICATION_TYPE.ordinal()).getAllele();


		verificationModality=(Integer)individual.getGene(GenePosition.VERIFICATION_MODALITY.ordinal()).getAllele();


		includeVerificationText=verificationModality==ALL_VERIFICATION||verificationModality==TEXT_IMAGE_VERIFICATION||verificationModality==TEXT_AUDIO_VERIFICATION||verificationModality==TEXT_VERIFICATION;
		includeVerificationImage=verificationModality==ALL_VERIFICATION||verificationModality==TEXT_IMAGE_VERIFICATION||verificationModality==IMAGE_AUDIO_VERIFICATION||verificationModality==IMAGE_VERIFICATION;


		//set defaults according to which modalities of verification we will include
		if(includeVerificationText){
			correctVerificationMessage=DEFAULT_CORRECT_VERIFICATION;
			incorrectVerificationMessage=DEFAULT_INCORRECT_VERIFICATION;
		}
		else {
			incorrectVerificationMessage=IMPLICIT_VERIFICATION_MESSAGE;
			correctVerificationMessage=IMPLICIT_VERIFICATION_MESSAGE;
		}
		if(includeVerificationImage){
			correctVerificationImage=DEFAULT_CORRECT_VERIFICATION_IMAGE;
			incorrectVerificationImage=DEFAULT_INCORRECT_VERIFICATION_IMAGE;
		}

		//set correct or incorrect feedbacks to nothing, depending on the type of verification
		switch(verificationType){
		case IMPLICIT_CORRECT_VERIFICATION:
			incorrectVerificationMessage="";
			//incorrectVerificationImage ideally set the transparency to 0 instead of setting to null.
			break;
		case IMPLICIT_INCORRECT_VERIFICATION:
			correctVerificationMessage="";
			//correctVerificationImage ideally set the transparency to 0 instead of setting to null.
			break;
		}



	}

	private void updateFeedbackTextColor() {
		text_color[0]=getIntegerAllelle(GenePosition.TEXT_COLOR_R);
		text_color[1]=getIntegerAllelle(GenePosition.TEXT_COLOR_G);
		text_color[2]=getIntegerAllelle(GenePosition.TEXT_COLOR_B);
	}

	private void updateFeedbackDelay() {
		feedback_delay=(Double)individual.getGene(GenePosition.FEEDBACK_DELAY.ordinal()).getAllele();
	}



	/* methods for providing the feedback, assuming the variables were set */
	public void provideFeedback(MathProblem problem) {
		resetPerFeedbackVariables();
		updateFeedbackScreensAccordingToProblemData(problem);
		startFeedbackScreens(problem);
	}

	private void resetPerFeedbackVariables(){
		numScreensCompleted=0;
		allowing_resubmission=false;

	}

	private void updateFeedbackScreensAccordingToProblemData(MathProblem problem) {
		updateVerificationScreen(problem);
		updateElaborationScreens(problem);
		updateDirectiveFeedbackScreens(problem);
	}


	private void updateDirectiveFeedbackScreens(MathProblem problem){
		updateCorrectAnswerScreen(problem);
		updateErrorFlagScreen(problem);
	}



	//need to adjust how the variable text objects work... probably would be easier to just get 
	//the references to the screens variables texts. thats what we wanted anyway.
	private void updateErrorFlagScreen(MathProblem problem) {


		//figure out which digits don't match the solution
		//highlight those digits in red.
		int[] userAnswerDigits=problem.getAnswerDigits();
		int[] solutionDigits=problem.getSolutionDigits(MathProblemHandler.MAX_DIGITS_IN_ANSWER);

		int in_solution=0;
		int in_answer=0;
		for(;in_answer<userAnswerDigits.length;in_answer++){
			int ans_digit=userAnswerDigits[in_answer];
			VariableText digit=errorFlagScreen.getVariableText(in_answer);
			digit.update(ans_digit);
			if(in_answer>=solutionDigits.length){
				digit.updateColor(ERROR_COLOR);
			}
			else {
				int sol_digit=solutionDigits[in_solution];
				if(ans_digit!=sol_digit){
					digit.updateColor(ERROR_COLOR);
				}
			}
		}

	}

	private void updateCorrectAnswerScreen(MathProblem problem) {
		correctAnswerScreen.addVariableText(new VariableText(problem.solution, CORRECT_VERIFICATION_COLOR, 0,0,processing.UI_FONT_SIZE));
	}

	private void updateElaborationScreens(MathProblem problem) {
		updateAttributeIsolationScreen(problem);
	}


	//"highlight relevant attributes of the problem"
	//in this toy example, we will just highlight the operator.
	//using one of two highlight strategies
	//color 
	//resize TODO after change the text thingy
	//god plz refactor this
	private void updateAttributeIsolationScreen(MathProblem problem) {
		attributeIsolationScreen.addVariableText(new VariableText(problem.arg1, processing.UI_FONT_COLOR,-processing.UI_FONT_SIZE*2,0,processing.UI_FONT_SIZE));
		attributeIsolationScreen.addVariableText(new VariableText(" ", processing.UI_FONT_COLOR,-processing.UI_FONT_SIZE*2+processing.UI_FONT_SIZE/2,0,processing.UI_FONT_SIZE));
		attributeIsolationScreen.addVariableText(new VariableText(problem.op, ATTRIBUTE_HIGHLIGHT_COLOR,-processing.UI_FONT_SIZE*2+processing.UI_FONT_SIZE,0,processing.UI_FONT_SIZE));
		attributeIsolationScreen.addVariableText(new VariableText(" ", processing.UI_FONT_COLOR,-processing.UI_FONT_SIZE*2+processing.UI_FONT_SIZE/2*3,0,processing.UI_FONT_SIZE));
		attributeIsolationScreen.addVariableText(new VariableText(problem.arg2, processing.UI_FONT_COLOR,-processing.UI_FONT_SIZE*2+processing.UI_FONT_SIZE*2,0,processing.UI_FONT_SIZE));
		attributeIsolationScreen.addVariableText(new VariableText(" ", processing.UI_FONT_COLOR,-processing.UI_FONT_SIZE*2+processing.UI_FONT_SIZE/2*5,0,processing.UI_FONT_SIZE));
		attributeIsolationScreen.addVariableText(new VariableText(problem.EQUALS, processing.UI_FONT_COLOR,-processing.UI_FONT_SIZE*2+processing.UI_FONT_SIZE*3,0,processing.UI_FONT_SIZE));
	}

	/*set displayable fields of the verification screen according to the verification parameters and whether or not the student was correct on the last problerm  */
	private void updateVerificationScreen(MathProblem problem) {
		ProblemData data=problem.getData();
		String verificationMessage =(data.correct? correctVerificationMessage : incorrectVerificationMessage);
		int[] verificationColor=(data.correct? CORRECT_VERIFICATION_COLOR: INCORRECT_VERIFICATION_COLOR);
		verification_text.update(verificationMessage);
		verification_text.updateColor(verificationColor);
		verificationScreen.setImage(data.correct? correctVerificationImage : incorrectVerificationImage);

	}

	private void startFeedbackScreens(MathProblem problem){
		//always verify
		activateScreen(verificationScreen);

		startDirectiveScreens();
		startElaborationScreens();
		
		//sometimes allow resubmission
		
		decideIfAllowResubmission(problem);

	}


	private void decideIfAllowResubmission(MathProblem problem) {
		allowing_resubmission=!problem.currentAnswerIsCorrect()&&eventOccurs(p_allow_resubmit);
		
	}

	private void startElaborationScreens() {
		if(eventOccurs(p_elaborate)){
			boolean nothingActivated=true;
			//ensure that at least one elaboration event occurs.
			while(nothingActivated){
				if(eventOccurs(p_attribute_isolation)) {
					activateScreen(attributeIsolationScreen);
					nothingActivated=false;
				}

			}
		}

	}

	private void startDirectiveScreens() {
		if(eventOccurs(p_directive)){
			boolean nothingActivated=true;
			//ensure that at least one elaboration event occurs.
			while(nothingActivated){
				if(eventOccurs(p_correct_answer)) {
					activateScreen(correctAnswerScreen);
					nothingActivated=false;
				}

				if(eventOccurs(p_error_flag)) {
					activateScreen(errorFlagScreen);
					nothingActivated=false;
				}		

			}
		}

	}

	private void activateScreen(DisplayScreen screen){
		screen.addObserver(this);
		screen.activate();
	}


	private boolean eventOccurs(double p_event) {
		return Math.random()<p_event;
	}


	public List<DisplayScreen> getFeedbackScreens() {
		return feedbackScreens;
	}

	public void initializeFeedbackScreens() {
		feedbackScreens=new LinkedList<DisplayScreen>();
		initializeVerificationScreen();
		initializeDirectiveFeedbackScreens();
		initializeElaborativeFeedbackScreens();
		initializeResubmitScreen();
	}

	private void initializeElaborativeFeedbackScreens() {
		initializeAttributeIsolationScreen();

	}

	private void initializeDirectiveFeedbackScreens() {
		initializeCorrectAnswerScreen();
		initializeErrorFlagScreen();

	}

	private void initializeErrorFlagScreen() {
		DisplayScreen template=processing.mathProblemUI.getAnswerScreenTransform();
		//template.translate(0,-processing.UI_FONT_SIZE/2);
		errorFlagScreen=setupEmptyFeedbackScreenAndAddToListOfFeedbackScreens(template);
		errorFlagScreen.setDelayBeforeDisplay((int)directive_delay+error_flag_delay);
		errorFlagScreen.setDurationOfDisplay((int)DEFAULT_SCREEN_DURATION);
		errorFlagScreen.setName("error flag");
		VariableText usersAnswerFirstDigit=new VariableText("",processing.UI_FONT_COLOR,-processing.UI_FONT_SIZE/2-processing.UI_FONT_SIZE/4,0,processing.UI_FONT_SIZE);
		VariableText usersAnswerSecondDigit=new VariableText("",processing.UI_FONT_COLOR,0,0,processing.UI_FONT_SIZE);
		VariableText usersAnswerThirdDigit=new VariableText("",processing.UI_FONT_COLOR,processing.UI_FONT_SIZE/2+processing.UI_FONT_SIZE/4,0,processing.UI_FONT_SIZE);
		errorFlagScreen.addVariableText(usersAnswerFirstDigit);
		errorFlagScreen.addVariableText(usersAnswerSecondDigit);
		errorFlagScreen.addVariableText(usersAnswerThirdDigit);
		errorFlagScreen.setBackgroundColor(processing.UI_BACKGROUND_COLOR);
		errorFlagScreen.setTransparent(false);

	}

	private void initializeCorrectAnswerScreen() {
		DisplayScreen template=processing.mathProblemUI.getAnswerScreenTransform();
		template.translate(0, -template.getHeight());
		correctAnswerScreen=setupEmptyFeedbackScreenAndAddToListOfFeedbackScreens(template);
		correctAnswerScreen.setDelayBeforeDisplay((int)directive_delay+correct_answer_delay);
		correctAnswerScreen.setDurationOfDisplay((int)DEFAULT_SCREEN_DURATION);
		correctAnswerScreen.setName("correct answer");
	}

	private void initializeAttributeIsolationScreen() {
		attributeIsolationScreen=setupEmptyFeedbackScreenAndAddToListOfFeedbackScreens(processing.mathProblemUI.getProblemScreenTransform());
		attributeIsolationScreen.setDelayBeforeDisplay((int)elaboration_delay+attributeIsolationDelay);
		attributeIsolationScreen.setDurationOfDisplay((int)DEFAULT_SCREEN_DURATION);
		attributeIsolationScreen.setName("attributeIsolation");
	}

	private void initializeVerificationScreen() {
		verificationScreen=setupEmptyFeedbackScreenAndAddToListOfFeedbackScreens(processing.makeEmptyScreenSizedToApplication());
		verificationScreen.setDelayBeforeDisplay((int)feedback_delay);
		verificationScreen.setDurationOfDisplay((int)DEFAULT_SCREEN_DURATION);
		verificationScreen.setName("verification");
		verificationScreen.addVariableText(verification_text);	
	}
	
	
	
	private void initializeResubmitScreen(){
	  allowResubmitScreen=processing.makeEmptyScreenSizedToApplication();
      allowResubmitScreen.setTransparent(false);
      allowResubmitScreen.setBackgroundColor(processing.UI_BACKGROUND_COLOR);
	  allowResubmitScreen.setDurationOfDisplay(DEFAULT_SCREEN_DURATION*2);
	  allowResubmitScreen.addVariableText(allowResubmitText);
	
	
	}

	DisplayScreen setupEmptyFeedbackScreenAndAddToListOfFeedbackScreens(DisplayScreen screen) {
		screen.setTransparent(true);
		feedbackScreens.add(screen);

		return screen;
	}

	@Override
	public void update(Observable arg0, Object arg1) {

		if(arg0 instanceof DisplayScreen){

			DisplayScreen done = (DisplayScreen)arg0;
			numScreensCompleted++;
			done.deleteObserver(this); //de register or else we keep receiving updates.
			if(numScreensCompleted==feedbackScreens.size()){
				//feedback done; tell processing app.
				numScreensCompleted=PRE_FEEDBACK;
				if(!allowing_resubmission){
					processing.feedbackDone();
				}
				else {
					allowResubmitScreen.activate();
				}
				
			}

		}

	}


	public boolean acceptingResponse(){
		return !allowResubmitScreen.isActive()&&!feedbackInProcess();//numScreensCompleted!=PRE_FEEDBACK&&numScreensCompleted<feedbackScreens.size();
	}
	
	private boolean feedbackInProcess(){
		return numScreensCompleted!=PRE_FEEDBACK&&numScreensCompleted<feedbackScreens.size();

	}



	private double getDoubleAllelle(GenePosition pos){
		return (Double)individual.getGene(pos.ordinal()).getAllele();

	}

	private int getIntegerAllelle(GenePosition pos){
		return (Integer)individual.getGene(pos.ordinal()).getAllele();

	}





}

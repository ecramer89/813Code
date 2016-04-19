package mathInterface;

import java.util.LinkedList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import org.jgap.IChromosome;

import processing.core.PImage;



/* class that represents a feedback event. the object that encapsulates the individual
 * (chromosome) that results from the genetic algorithm */

public class Feedback extends Observable implements Observer  {
	public static final int DISCOUNT=-1;
	//verification type int codes
	static final int EXPLICIT_VERIFICATION=0;
	static final int IMPLICIT_CORRECT_VERIFICATION=1;
	static final int IMPLICIT_INCORRECT_VERIFICATION=2;
	//verification modality int codes
	public static final int TEXT_IMAGE_VERIFICATION = 0;
	public static final int ALL_VERIFICATION = 1;
	public static final int IMAGE_AUDIO_VERIFICATION = 2;
	public static final int TEXT_VERIFICATION = 3;
	public static final int TEXT_AUDIO_VERIFICATION = 4;
	public static final int IMAGE_VERIFICATION = 5;
	private static final int DEFAULT_SCREEN_DURATION = 2000;
	private static final int[] ATTRIBUTE_HIGHLIGHT_COLOR = new int[]{255,255,0};
	private static final int[] CORRECT_VERIFICATION_COLOR=new int[]{0,255,0};
	private static final int[] INCORRECT_VERIFICATION_COLOR=new int[]{255,0,0};
	private static final int[] ERROR_COLOR = INCORRECT_VERIFICATION_COLOR;
	static final String DEFAULT_CORRECT_VERIFICATION="Correct";
	static final String DEFAULT_INCORRECT_VERIFICATION="Incorrect";
	static final String IMPLICIT_VERIFICATION_MESSAGE = "";
	private static final int PRE_FEEDBACK = -1;
	static final String RESUBMIT_MESSAGE="Try again!";
	
	private static VariableText allowResubmitText;

	public static PImage DEFAULT_CORRECT_VERIFICATION_IMAGE=null;
	public static PImage DEFAULT_INCORRECT_VERIFICATION_IMAGE=null;


	private static final double UNINITIALIZED = -1000000;
	private double[] staticFields=new double[FeedbackGeneType.values().length];
	private String name;

	/*parameters that are interpreted from the individual (chromosome)*/
	private int base_feedback_delay;

	/* for directive feedback */
	private double p_directive=1.0;


	private double p_correct_answer=1.0;
	private int correct_answer_delay;

	private double p_error_flag=1.0;
	private int error_flag_delay;


	/* for elaboration */
	private double p_elaborate=1.0;


	private double p_attribute_isolation=1.0;

	private int attributeIsolationDelay;


	private double p_allow_resubmit;
	private boolean allowing_resubmission;


	/*variables particular to different feedback events*/
	/*verification*/
	private int verificationType;
	private int verificationModality;
	private boolean includeVerificationText;
	private boolean includeVerificationImage;

	private VariableText verification_text=new VariableText("",INCORRECT_VERIFICATION_COLOR,0,0,ProcessingApplication.UI_FONT_SIZE);
	private String correctVerificationMessage=DEFAULT_CORRECT_VERIFICATION;
	private String incorrectVerificationMessage=DEFAULT_INCORRECT_VERIFICATION;

	private PImage correctVerificationImage;
	private PImage incorrectVerificationImage;


	private DisplayScreen verificationScreen;

	private DisplayScreen correctAnswerScreen;
	private DisplayScreen errorFlagScreen;

	private DisplayScreen attributeIsolationScreen;
	private DisplayScreen allowResubmitScreen;

	private List<DisplayScreen> feedbackScreens;
	private int numScreensCompleted=PRE_FEEDBACK;



	private int totalRunTimeOfFeedbackScreens;
	private int[] id_color=new int[]{255,255,255};



	public Feedback(){
		this("");
	}




	public Feedback(String name) {

		this.name=name;
		allowResubmitText=new VariableText(RESUBMIT_MESSAGE,ProcessingApplication.UI_FONT_COLOR,0,0,ProcessingApplication.UI_FONT_SIZE);

		DEFAULT_CORRECT_VERIFICATION_IMAGE=ChromosomeToFeedbackManifester.DEFAULT_CORRECT_VERIFICATION_IMAGE;
		DEFAULT_INCORRECT_VERIFICATION_IMAGE=ChromosomeToFeedbackManifester.DEFAULT_INCORRECT_VERIFICATION_IMAGE;

		correctVerificationImage=DEFAULT_CORRECT_VERIFICATION_IMAGE;
		incorrectVerificationImage=DEFAULT_INCORRECT_VERIFICATION_IMAGE;
		feedbackScreens=new LinkedList<DisplayScreen>();
		
		
		//set static fields to have a dummy value indicating they haven't been set yet.
		for(int i=0;i<staticFields.length;i++){
			staticFields[i]=UNINITIALIZED;
		}
		
	}




	public void updateAllowingResubmission(double p_allow_resubmission){
		p_allow_resubmit=p_allow_resubmission;
		staticFields[FeedbackGeneType.P_ALLOW_RESUBMIT.ordinal()]=p_allow_resubmit;
	}

	public void updateDirectiveFeedbackParameters(double p_directive_) {
		p_directive=p_directive_;

		staticFields[FeedbackGeneType.P_DIRECTIVE.ordinal()]=p_directive;


	}

	public void updateProvideCorrectAnswerParameters(double p_correct_answer_, int correct_answer_delay_){
		/* types of directive feedback */
		//provide the correct response
		p_correct_answer=p_correct_answer_;
		correct_answer_delay=correct_answer_delay_;
		staticFields[FeedbackGeneType.P_CORRECT_RESPONSE.ordinal()]=p_correct_answer;
		staticFields[FeedbackGeneType.CORRECT_RESPONSE_DELAY.ordinal()]=correct_answer_delay_;
	}

	public void updateErrorFlagParameters(double p_flag_error_, int error_flag_delay_){
		//highlight the errors in the childs solution
		p_error_flag=p_flag_error_;
		error_flag_delay=error_flag_delay_;
		staticFields[FeedbackGeneType.P_ERROR_FLAG.ordinal()]=p_error_flag;
		staticFields[FeedbackGeneType.ERROR_FLAG_DELAY.ordinal()]=error_flag_delay_;

	}

	public void updateElaborationParameters(double p_elaborate_) {
		p_elaborate=p_elaborate_;

		staticFields[FeedbackGeneType.P_ELABORATE.ordinal()]=p_elaborate;

	}

	public void updateAttributeIsolationParameters(double p_attribute_isolation_, int attribute_isolation_delay){

		//different types of elaborative feedback
		p_attribute_isolation=p_attribute_isolation_;
		attributeIsolationDelay=attribute_isolation_delay;
		staticFields[FeedbackGeneType.P_ATTRIBUTE_ISOLATION.ordinal()]=p_attribute_isolation;
		staticFields[FeedbackGeneType.ATTRIBUTE_ISOLATION_DELAY.ordinal()]=attribute_isolation_delay;
	}


	public void updateVerificationParameters(int verificationType_, int verificationModality_) {

		verificationType=verificationType_;


		verificationModality=verificationModality_;

		staticFields[FeedbackGeneType.VERIFICATION_TYPE.ordinal()]=verificationType;
		staticFields[FeedbackGeneType.VERIFICATION_MODALITY.ordinal()]=verificationModality;

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


	public void updateFeedbackDelay(int feedback_delay_) {
		base_feedback_delay=feedback_delay_;
		staticFields[FeedbackGeneType.FEEDBACK_DELAY.ordinal()]=base_feedback_delay;
	}





	public void resetPerFeedbackVariables(){
		allowResubmitScreen.setDelayBeforeDisplay(base_feedback_delay);
		numScreensCompleted=0;
		allowing_resubmission=false;
		totalRunTimeOfFeedbackScreens=0;
	}





	//need to adjust how the variable text objects work... probably would be easier to just get 
	//the references to the screens variables texts. thats what we wanted anyway.
	public void updateErrorFlagScreen(MathProblem problem) {


		//figure out which digits don't match the solution
		//highlight those digits in red.
		int[] userAnswerDigits=problem.getAnswerDigits();
		int[] solutionDigits=problem.getSolutionDigits(MathProblemSetHandler.MAX_DIGITS_IN_ANSWER);

		int in_solution=0;
		int in_answer=0;
		for(;in_answer<userAnswerDigits.length;in_answer++, in_solution++){
			int ans_digit=userAnswerDigits[in_answer];
			VariableText digit=errorFlagScreen.getVariableText(in_answer);
			digit.update(ans_digit);
			digit.updateColor(ProcessingApplication.UI_FONT_COLOR);
			if(in_answer>=solutionDigits.length){
               
				digit.updateColor(ERROR_COLOR);
			}
			else {
				int sol_digit=solutionDigits[in_solution];
				if(ans_digit!=sol_digit){
					digit.updateColor(ERROR_COLOR);
					
				}
				else {
					digit.updateColor(ProcessingApplication.UI_FONT_COLOR);
				}
			}
		}

		//clear out digits that might be saved in the VT objects from previous answer.
		for(;in_answer<MathProblemSetHandler.MAX_DIGITS_IN_ANSWER;in_answer++){
			errorFlagScreen.getVariableText(in_answer).update("");	
		}

	}

	public void updateCorrectAnswerScreen(MathProblem problem) {
		int[] solutionDigits=problem.getSolutionDigits(MathProblemSetHandler.MAX_DIGITS_IN_ANSWER);
		//System.out.println("message from feedback; solu digis");
		//for(int i=0;i<solutionDigits.length;i++)
		//System.out.println(solutionDigits[i]);
		int in_solu=0;
		for(;in_solu<solutionDigits.length;in_solu++){   
			//correctAnswerScreen.clearText();
			VariableText digit=correctAnswerScreen.getVariableText(in_solu);
			digit.update(solutionDigits[in_solu]);
		}
		for(;in_solu<correctAnswerScreen.numVariableText();in_solu++){
			VariableText digit=correctAnswerScreen.getVariableText(in_solu);
			digit.update(" ");
		}

	}

	public void updateElaborationScreens(MathProblem problem) {
		updateAttributeIsolationScreen(problem);
	}


	public void updateAttributeIsolationScreen(MathProblem problem) {
		attributeIsolationScreen.clearText();
		attributeIsolationScreen.addVariableText(new VariableText(problem.arg1, ProcessingApplication.UI_FONT_COLOR,-ProcessingApplication.UI_FONT_SIZE*2,0,ProcessingApplication.UI_FONT_SIZE));
		attributeIsolationScreen.addVariableText(new VariableText(" ", ProcessingApplication.UI_FONT_COLOR,-ProcessingApplication.UI_FONT_SIZE*2+ProcessingApplication.UI_FONT_SIZE/2,0,ProcessingApplication.UI_FONT_SIZE));
		attributeIsolationScreen.addVariableText(new VariableText(problem.op, ATTRIBUTE_HIGHLIGHT_COLOR,-ProcessingApplication.UI_FONT_SIZE*2+ProcessingApplication.UI_FONT_SIZE,0,ProcessingApplication.UI_FONT_SIZE));
		attributeIsolationScreen.addVariableText(new VariableText(" ", ProcessingApplication.UI_FONT_COLOR,-ProcessingApplication.UI_FONT_SIZE*2+ProcessingApplication.UI_FONT_SIZE/2*3,0,ProcessingApplication.UI_FONT_SIZE));
		attributeIsolationScreen.addVariableText(new VariableText(problem.arg2, ProcessingApplication.UI_FONT_COLOR,-ProcessingApplication.UI_FONT_SIZE*2+ProcessingApplication.UI_FONT_SIZE*2,0,ProcessingApplication.UI_FONT_SIZE));
		attributeIsolationScreen.addVariableText(new VariableText(" ", ProcessingApplication.UI_FONT_COLOR,-ProcessingApplication.UI_FONT_SIZE*2+ProcessingApplication.UI_FONT_SIZE/2*5,0,ProcessingApplication.UI_FONT_SIZE));
		attributeIsolationScreen.addVariableText(new VariableText(problem.EQUALS, ProcessingApplication.UI_FONT_COLOR,-ProcessingApplication.UI_FONT_SIZE*2+ProcessingApplication.UI_FONT_SIZE*3,0,ProcessingApplication.UI_FONT_SIZE));
	}

	/*set displayable fields of the verification screen according to the verification parameters and whether or not the student was correct on the last problerm  */
	public void updateVerificationScreen(MathProblem problem) {
		ProblemData data=problem.getData();
		String verificationMessage =(data.correct? correctVerificationMessage : incorrectVerificationMessage);
		int[] verificationColor=(data.correct? CORRECT_VERIFICATION_COLOR: INCORRECT_VERIFICATION_COLOR);
		verification_text.update(verificationMessage);
		verification_text.updateColor(verificationColor);
		verificationScreen.setImage((data.correct? correctVerificationImage : incorrectVerificationImage), 0, correctVerificationImage.height);

	}


	public void startFeedbackScreens(MathProblem problem){
		feedbackScreens=new LinkedList<DisplayScreen>();
	
		activateScreen(verificationScreen);



		startDirectiveScreens();
		startElaborationScreens();

		//sometimes allow resubmission

		decideIfAllowResubmission(problem);


	}


	public void decideIfAllowResubmission(MathProblem problem) {
		allowing_resubmission=!problem.currentAnswerIsCorrect()&&eventOccurs(p_allow_resubmit);
		if(allowing_resubmission){
			allowResubmitScreen.adjustDelayBeforeDisplay(totalRunTimeOfFeedbackScreens);

			activateScreen(allowResubmitScreen);
		}
	}

	public void startElaborationScreens() {
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

	public void startDirectiveScreens() {
		if(eventOccurs(p_directive)){
			boolean nothingActivated=true;
			//ensure that at least one elaboration event occurs.
			while(nothingActivated){
				if(eventOccurs(p_correct_answer)) {
					activateScreen(correctAnswerScreen);
					nothingActivated=false;
					//System.out.println("correct answer");

				}

				if(eventOccurs(p_error_flag)) {
					activateScreen(errorFlagScreen);
					nothingActivated=false;
					//System.out.println("error flag");

				}		

			}
		}

	}

	public void activateScreen(DisplayScreen screen){
		int duration=screen.getDurationOfDisplay();
		int extraDelay=screen.getDelayBeforeDisplay()-base_feedback_delay;
		if(duration+extraDelay>totalRunTimeOfFeedbackScreens){
			totalRunTimeOfFeedbackScreens=duration+extraDelay;
		}

		feedbackScreens.add(screen);
		screen.addObserver(this);
		screen.activate();

	}


	private boolean eventOccurs(double p_event) {
		return Math.random()<p_event;
	}


	public List<DisplayScreen> getFeedbackScreens() {
		return feedbackScreens;
	}


	public void initializeErrorFlagScreen(DisplayScreen template) {
		errorFlagScreen=template;
		errorFlagScreen.setDelayBeforeDisplay((int)base_feedback_delay);
		errorFlagScreen.adjustDelayBeforeDisplay(error_flag_delay);
		errorFlagScreen.setDurationOfDisplay((int)DEFAULT_SCREEN_DURATION);
		errorFlagScreen.setName("error flag");
		VariableText usersAnswerFirstDigit=new VariableText("",ProcessingApplication.UI_FONT_COLOR,-ProcessingApplication.UI_FONT_SIZE/2-ProcessingApplication.UI_FONT_SIZE/4,0,ProcessingApplication.UI_FONT_SIZE);
		VariableText usersAnswerSecondDigit=new VariableText("",ProcessingApplication.UI_FONT_COLOR,0,0,ProcessingApplication.UI_FONT_SIZE);
		VariableText usersAnswerThirdDigit=new VariableText("",ProcessingApplication.UI_FONT_COLOR,ProcessingApplication.UI_FONT_SIZE/2+ProcessingApplication.UI_FONT_SIZE/4,0,ProcessingApplication.UI_FONT_SIZE);
		errorFlagScreen.addVariableText(usersAnswerFirstDigit);
		errorFlagScreen.addVariableText(usersAnswerSecondDigit);
		errorFlagScreen.addVariableText(usersAnswerThirdDigit);
		errorFlagScreen.setBackgroundColor(ProcessingApplication.UI_BACKGROUND_COLOR);
		errorFlagScreen.setTransparent(false);

	}

	public void initializeCorrectAnswerScreen(DisplayScreen template) {
		template.translate(0, -template.getHeight());
		correctAnswerScreen=template;
		correctAnswerScreen.setTransparent(true);
		correctAnswerScreen.setDelayBeforeDisplay((int)base_feedback_delay);
		correctAnswerScreen.setDurationOfDisplay((int)DEFAULT_SCREEN_DURATION);
		correctAnswerScreen.setName("correct answer");
		VariableText answerFirstDigit=new VariableText("",CORRECT_VERIFICATION_COLOR,-ProcessingApplication.UI_FONT_SIZE/2-ProcessingApplication.UI_FONT_SIZE/4,0,ProcessingApplication.UI_FONT_SIZE);
		VariableText answerSecondDigit=new VariableText("",CORRECT_VERIFICATION_COLOR,0,0,ProcessingApplication.UI_FONT_SIZE);
		VariableText answerThirdDigit=new VariableText("",CORRECT_VERIFICATION_COLOR,ProcessingApplication.UI_FONT_SIZE/2+ProcessingApplication.UI_FONT_SIZE/4,0,ProcessingApplication.UI_FONT_SIZE);

		correctAnswerScreen.addVariableText(answerFirstDigit);
		correctAnswerScreen.addVariableText(answerSecondDigit);
		correctAnswerScreen.addVariableText(answerThirdDigit);

		correctAnswerScreen.adjustDelayBeforeDisplay(correct_answer_delay);
	}

	public void initializeAttributeIsolationScreen(DisplayScreen template) {
		attributeIsolationScreen=template;
		attributeIsolationScreen.setTransparent(true);
		attributeIsolationScreen.setDelayBeforeDisplay((int)base_feedback_delay);
		attributeIsolationScreen.setDurationOfDisplay((int)DEFAULT_SCREEN_DURATION);
		attributeIsolationScreen.setName("attributeIsolation");
		attributeIsolationScreen.adjustDelayBeforeDisplay(attributeIsolationDelay);
	}

	public void initializeVerificationScreen(DisplayScreen template) {
		verificationScreen=template;
		verificationScreen.setTransparent(true);
		verificationScreen.setDelayBeforeDisplay((int)base_feedback_delay);
		verificationScreen.setDurationOfDisplay((int)DEFAULT_SCREEN_DURATION);
		verificationScreen.setName("verification");
		verificationScreen.addVariableText(verification_text);	
		verificationScreen.adjustImageOffset(0,(int)(DEFAULT_CORRECT_VERIFICATION_IMAGE.height/4));
	}



	public void initializeResubmitScreen(DisplayScreen template){
		allowResubmitScreen=template;
		allowResubmitScreen.setTransparent(false);
		allowResubmitScreen.setDelayBeforeDisplay((int)base_feedback_delay);
		allowResubmitScreen.setBackgroundColor(ProcessingApplication.UI_BACKGROUND_COLOR);
		allowResubmitScreen.setDurationOfDisplay(DEFAULT_SCREEN_DURATION);
		allowResubmitScreen.addVariableText(allowResubmitText);
		allowResubmitScreen.setName("allow resubmit");

	}

	//int times=0;
	@Override
	public void update(Observable arg0, Object arg1) {

		if(arg0 instanceof DisplayScreen){

			DisplayScreen done = (DisplayScreen)arg0;
			numScreensCompleted++;
			done.deleteObserver(this); //de register or else we keep receiving updates.

			//System.out.println("called update; "+numScreensCompleted+" num screens total "+feedbackScreens.size());
			if(numScreensCompleted==feedbackScreens.size()){
				//feedback done; tell processing app.
				numScreensCompleted=PRE_FEEDBACK;
				if(!allowing_resubmission){

					ProcessingApplication.getInstance().feedbackDone();
				}
				//else, we just don't let processing know. 
			}
		}
	}






	public void resetFeedbackScreens(){

		for(DisplayScreen screen : feedbackScreens){
			screen.reset();
		}


	}

	public void provideFeedback(MathProblem problem) {
		resetPerFeedbackVariables();
		updateFeedbackScreensAccordingToProblemData(problem);
		startFeedbackScreens(problem);

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

	public boolean feedbackInProcess() {
		return numScreensCompleted!=PRE_FEEDBACK&&numScreensCompleted<feedbackScreens.size();
	}

	public void updateIdentificationColor(int[] color) {
		id_color[0]=color[0];
		id_color[1]=color[1];
		id_color[2]=color[2];
	}

	public int[] getIdColor(){
		return id_color;
	}

	public double distanceFrom(Feedback feedback) {
		double result=0;
		for(int i=0;i<staticFields.length;i++){
			double other_field=feedback.staticFields[i];
			double this_field=staticFields[i];

			if(count(this_field,other_field)){
				result+=Math.pow(this_field-other_field, 2);
			}
		}
		return Math.sqrt(result);
	}


	private boolean count(double this_field, double other_field){
		return !(this_field==DISCOUNT)&& !(other_field==DISCOUNT);
	}



	private String parametersAsString="";
	public String getParametersAsString(){
		if(parametersAsString.length()==0){
			if(staticFieldsHaveBeenInitialized()){
				createRepresentationAsString();
			}
		}
		return parametersAsString;
	}




	private boolean staticFieldsHaveBeenInitialized() {

		return staticFields[0]!=UNINITIALIZED;
	}




	private void createRepresentationAsString() {



		StringBuilder s=new StringBuilder();
		for(FeedbackGeneType g : FeedbackGeneType.values()){
			s.append(g.nameOfGene());
			s.append(": ");
			double myAllelle=staticFields[g.ordinal()];
			s.append((myAllelle==DISCOUNT ? "Doesn't matter." : myAllelle));
			s.append("|");
		}

		parametersAsString=s.toString();

	}




	public String getName() {
	
		return name;
	}







}

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
	private static final int[] ATTRIBUTE_HIGHLIGHT_COLOR = new int[]{255,0,0};
	static PImage DEFAULT_CORRECT_VERIFICATION_IMAGE=null;
	static PImage DEFAULT_INCORRECT_VERIFICATION_IMAGE=null;


	/* for elaboration */
	private double p_elaborate=1.0;
	private int elaboration_delay;


	private double p_attribute_isolation=1.0;
	private int attributeIsolationDuration;
	private int attributeIsolationDelay;
	

	

	IChromosome individual;
	/*parameters that are interpreted from the individual (chromosome)*/
	private double feedback_delay;





	/*variables particular to different feedback events*/
	/*general*/
	private int[] text_color=new int[]{0,0,0};

	/*verification*/
	private int verificationType;
	private int verificationModality;
	private boolean includeVerificationText;
	private boolean includeVerificationImage;

	private VariableText verification_text=new VariableText("",text_color,0,0,processing.UI_FONT_SIZE);
	private String correctVerificationMessage=DEFAULT_CORRECT_VERIFICATION;
	private String incorrectVerificationMessage=DEFAULT_INCORRECT_VERIFICATION;

	private PImage correctVerificationImage;
	private PImage incorrectVerificationImage;
	private PImage PLACEHOLDER_IMAGE; 
	
	
	private int verification_duration=300; //ms


	DisplayScreen verificationScreen;
	DisplayScreen attributeIsolationScreen;

	
	List<DisplayScreen> feedbackScreens;
	int numScreensCompleted=PRE_FEEDBACK;
	
	

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
	}

	private void updateElaborationParameters() {
		p_elaborate=(Double)individual.getGene(GenePosition.P_ELABORATE.ordinal()).getAllele();
		elaboration_delay=(Integer)individual.getGene(GenePosition.DELAY_UNTIL_ELABORATE.ordinal()).getAllele();
		p_attribute_isolation=(Double)individual.getGene(GenePosition.P_ATTRIBUTE_ISOLATION.ordinal()).getAllele();
		attributeIsolationDuration=(Integer)individual.getGene(GenePosition.ATTRIBUTE_ISOLATION_DURATION.ordinal()).getAllele();	
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
		text_color[0]=(Integer)individual.getGene(GenePosition.TEXT_COLOR_R.ordinal()).getAllele();
		text_color[1]=(Integer)individual.getGene(GenePosition.TEXT_COLOR_G.ordinal()).getAllele();
		text_color[2]=(Integer)individual.getGene(GenePosition.TEXT_COLOR_B.ordinal()).getAllele();
	}

	private void updateFeedbackDelay() {
		feedback_delay=(Double)individual.getGene(GenePosition.FEEDBACK_DELAY.ordinal()).getAllele();
	}



	/* methods for providing the feedback, assuming the variables were set */
	public void provideFeedback(MathProblem problem) {
		numScreensCompleted=0;
		updateFeedbackScreensAccordingToProblemData(problem);
		startFeedbackScreens();
	}



	private void updateFeedbackScreensAccordingToProblemData(MathProblem problem) {
		updateVerificationScreen(problem);
		updateElaborationScreens(problem);

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
		verification_text.update((data.correct? correctVerificationMessage : incorrectVerificationMessage));
		verificationScreen.setImage(data.correct? correctVerificationImage : incorrectVerificationImage);

	}

	private void startFeedbackScreens(){
		if(eventOccurs(p_verify)) activateScreen(verificationScreen);
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
		initializeAttributeIsolationScreen();
		

	}

	private void initializeAttributeIsolationScreen() {
		attributeIsolationScreen=setupEmptyFeedbackScreenAndAddToListOfFeedbackScreens(processing.mathProblemUI.getProblemScreenTransform());
		attributeIsolationScreen.setDelayBeforeDisplay((int)elaboration_delay+attributeIsolationDelay);
		attributeIsolationScreen.setDurationOfDisplay((int)attributeIsolationDuration);
		attributeIsolationScreen.setName("attributeIsolation");
	}

	private void initializeVerificationScreen() {
		verificationScreen=setupEmptyFeedbackScreenAndAddToListOfFeedbackScreens(processing.makeEmptyScreenSizedToApplication());
		verificationScreen.setDelayBeforeDisplay((int)feedback_delay);
		verificationScreen.setDurationOfDisplay(verification_duration);
		verificationScreen.setName("verification");
		verificationScreen.addVariableText(verification_text);	
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
				processing.feedbackDone();
			}

		}

	}
	
	
	public boolean feedbackInProcess(){
		return numScreensCompleted!=PRE_FEEDBACK&&numScreensCompleted<feedbackScreens.size();
	}





}

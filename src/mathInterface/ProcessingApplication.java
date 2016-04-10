package mathInterface;
import java.util.Iterator;
import java.util.Observable;
import java.util.Observer;




import org.jgap.Chromosome;
import org.jgap.Configuration;
import org.jgap.InvalidConfigurationException;

import processing.core.*;

import org.jgap.IChromosome;
import org.jgap.impl.DefaultConfiguration;
public class ProcessingApplication extends PApplet implements Observer {
	private static ProcessingApplication processingAppInstance;
	public static final int APPLICATION_WIDTH=800;
	public static final int APPLICATION_HEIGHT=600;

	//GA params
	public static final int NUM_GENERATIONS=4;

	public static final int NUM_INDIVIDUALS_TO_SHOW_USER=6;

	//math problem params
	public static final int MATH_PROBLEMS_PER_SET=2;
	public static final int MAX_ARGUMENT_VALUE = 3;
	public static final int MAX_DIGITS_IN_ANSWER = 2;


	public static ChromosomeToFeedbackManifester feedbackManifester;
	JGAPAdapter jgapAdaptor;
	UI mathProblemUI;
	MathProblemSetHandler mathProblemSetHandler;
	ChildPerformanceMonitor childPerformanceMonitor;



	Iterator<IChromosome> currentPopulation;
	private static final int DELAY_BEFORE_START_TIME = 120;
	private static final int RECORDING_APTITUDE = 0;
	public static final int RUNNING_INDIVIDUAL=1;
	public static final int UPDATING_POPULATION=2;
	private static final int FINISHED_ALL_POPULATIONS = 3;
	private static final int DELAY_BEFORE_START = 4;
	private static final int SIGNALING_NEXT_INDIVIDUAL=5;

	public static final int UPDATE_POPULATION_DELAY = 1000;
	public static final int[] UI_BACKGROUND_COLOR=new int[]{0,0,0};
	public static final int[] UI_FONT_COLOR=new int[]{255,255,255};
	public static final int UI_FONT_SIZE = 60;

	private int state;

	private int generationNumber;


	private DisplayScreen allDoneScreen;

	private Button[] aptitude_buttons;
	private int delay_before_start_timer;
	public static final int HIGH=2;
	public static final int LOW=0;
	public static final int MED=1;
	public static final int NUM_APTITUDE_LEVELS=3;


	private static final float NUM_GA_SHAKEUP_STRATEGIES = 3;
	private GAShakeupStrategy adjustWeights;
	private GAShakeupStrategy mutants;
	private GAShakeupStrategy deviateFromTemplates;



	private static int curr_child_aptitude;
	public static int getCurrentChildAptitude(){
		return curr_child_aptitude;
	}


	public void settings(){
		size(APPLICATION_WIDTH,APPLICATION_HEIGHT);
	}



	public void setup(){
		processingAppInstance=this;
		feedbackManifester=ChromosomeToFeedbackManifester.getInstance();
		jgapAdaptor=new JGAPAdapter();
		mathProblemUI=UI.getInstance();
		mathProblemSetHandler=MathProblemSetHandler.getInstance();
		childPerformanceMonitor=ChildPerformanceMonitor.getInstance();

		allDoneScreen=makeEmptyScreenSizedToApplication();
		allDoneScreen.addVariableText(new VariableText("All finished.",UI_FONT_COLOR,0,0,20));

		//initialize shakeup strategies
		adjustWeights=new AdjustWeights(jgapAdaptor);
		mutants=new SeedPopulationWithTailEndMutants(jgapAdaptor);
		deviateFromTemplates=new RewardDeviationFromTemplates(jgapAdaptor);


		initializeRecordAptitudeButtons();



	}


	private void initializeRecordAptitudeButtons() {
		aptitude_buttons=new Button[3];
		for(int i=0;i<aptitude_buttons.length;i++){
			Button b=new Button(-100+(100)*i, 0, 80, 40, i);
			b.setMessage(intToAptitude(i), UI_FONT_SIZE);
			b.setColor(new float[]{120,120,120});
			aptitude_buttons[i]=b;
		}
	}



	private String intToAptitude(int i) {
		// TODO Auto-generated method stub
		switch(i){
		case LOW: 
			return "LOW";
		case MED:
			return "MED";
		case HIGH:
			return "HIGH";
		}

		return "MEDIUM";
	}


	public static ProcessingApplication getInstance(){
		return processingAppInstance;
	}




	private void updateRecordAptitudeUI(){
		pushMatrix();
		background(UI_BACKGROUND_COLOR);
		translate(width/2, height/2);
		textAlign(CENTER);
		textSize(30);
		fill(UI_FONT_COLOR);
		text("Please select this student's aptitude level.",0,-100);
		for(int i=0;i<aptitude_buttons.length;i++){
			Button b=aptitude_buttons[i];
			b.update(this);
		}
		popMatrix();
	}

	private void fill(int[] color) {
		fill(color[0], color[1], color[2]);

	}


	public void background(int[] color) {
		background(color[0], color[1], color[2]);

	}

	private void handleTransitionScreen(){
		delay_before_start_timer--;
		if(delay_before_start_timer==0){
			state=RUNNING_INDIVIDUAL;
			delay_before_start_timer=DELAY_BEFORE_START_TIME;
		}
		textSize(20);
		background(UI_BACKGROUND_COLOR);
		fill(UI_FONT_COLOR);
		textAlign(CENTER);
	}

	public void draw() {
		switch(state){
		case RECORDING_APTITUDE:
			updateRecordAptitudeUI();
			return;
		case DELAY_BEFORE_START:
			handleTransitionScreen();
			text("Thanks!", width/2, height/2);
			text("Configuring first set of feedbacks... one moment.", width/2, height/2+50);
			
			if(wait_one_frame_to_configure==0){
				configureApplicationForNextGeneration();
				configureApplicationForNextIndividual(); //for first individual.
				wait_one_frame_to_configure=-1;
			}
			
			if(wait_one_frame_to_configure>0) {
				wait_one_frame_to_configure--;
			}
			
			return;
		case SIGNALING_NEXT_INDIVIDUAL:
			handleTransitionScreen();
			text("Set completed.", width/2, height/2);
			text("Configuring next problem set... one moment.", width/2, height/2+50);
			return;
		case UPDATING_POPULATION:
			handleTransitionScreen();
			text("All sets completed.", width/2, height/2);
			text("Configuring next generation of feedbacks... please wait.", width/2, height/2+50);
			text(delay_before_start_timer, width/2, height/2+100);
			return;
		}


		mathProblemUI.update();




		//for testing
		displayCurrentFeedbackInfo();

	}


	private void displayCurrentFeedbackInfo(){
		fill(255);
		rect(width/2-190, height-80, 380, 80);
		int[] col=feedbackManifester.currFeedbackColor();
		fill(col[0],col[1],col[2]);
		textSize(20);
		textAlign(CENTER);
		text(feedbackManifester.currFeedback(), width/2, height-40);
	}






	public void mousePressed(){

		if(state==RECORDING_APTITUDE){
			Button pressed=identifyPressedAptitudeButton(mouseX-APPLICATION_WIDTH/2, mouseY-APPLICATION_HEIGHT/2);
			if(pressed!=null){
				handlePressedAptitudeButton(pressed);
			}




		}
		else {
			/* if we clicked in the submit answer button, then delegate control to the feedback message */
			mathProblemUI.mousePressed(mouseX-APPLICATION_WIDTH/2, mouseY-APPLICATION_HEIGHT/2);
		}
	}


	Button identifyPressedAptitudeButton(int mouseX, int mouseY) {
		for(Button button : aptitude_buttons){
			if (!button.isPressed() && button.wasPressed(mouseX, mouseY)) return button; 
		}
		return null;
	}

	int wait_one_frame_to_configure=1;
	void handlePressedAptitudeButton(Button button){
		button.flashToIndicatePressed(this);
		recordButtonIDAsAptitude(button.id);

		state=DELAY_BEFORE_START;
		delay_before_start_timer=(int)(DELAY_BEFORE_START_TIME/2);
	
	    
	}





	private void recordButtonIDAsAptitude(int id) {
		// TODO Auto-generated method stub
		curr_child_aptitude=id;	
	}


	public void keyPressed(){
		if (KeyInterpreter.isInt(key)) {
			mathProblemSetHandler.addDigitToAnswer(KeyInterpreter.toInt(key));
		}
		if (KeyInterpreter.isDelete(key)) {
			mathProblemSetHandler.removeDigitFromAnswer();
		}
		updateUserAnswerDigits();


	}


	private void updateUserAnswerDigits(){
		int[] userAnswerDigits=mathProblemSetHandler.currentAnswerDigits();

		int i=0;
		for(;i<userAnswerDigits.length;i++){
			userAnswerDigitsTextObjects[i].update(userAnswerDigits[i]);
		}
		for(;i<userAnswerDigitsTextObjects.length;i++){
			userAnswerDigitsTextObjects[i].update("");
		}

	}




	private void storeReferenceToCurrentIndividuals(){

		currentPopulation=jgapAdaptor.iterator();
	}


	//you should call this when you are done with the current indiviaul
	private void configureApplicationForNextIndividual(){

		//get a fresh new set of problems
		mathProblemSetHandler.initializeNewProblemSet();
		mathProblemSetHandler.createRandomProblems();
		//update feedback variables
		feedbackManifester.setCurrentFeedbackChromosome(currentPopulation.next());
		feedbackManifester.initializeFeedbackScreens();
		//notify the child performance monitor that we are running a new individual
		//of the current generation.
		childPerformanceMonitor.prepareForNextIndividual();
		//setup first problem
		setupNextProblemAndUpdateUI();	
	}



	public DisplayScreen makeEmptyScreenSizedToApplication() {
		DisplayScreen screen=new DisplayScreen(0,0, APPLICATION_WIDTH,APPLICATION_HEIGHT);
		screen.setBackgroundColor(UI_BACKGROUND_COLOR);
		return screen;
	}


	int numAttempts;
	boolean isFirstAttemptAtThisProblem(){
		return numAttempts==1;
	}
	public void handleSubmittedAnswer() {
		//prevent user re submitting after feedback has started.
		numAttempts++;
		if(feedbackManifester.acceptingResponse()){
			if(isFirstAttemptAtThisProblem()){
				mathProblemSetHandler.updateUserScore();
			}
			feedbackManifester.provideFeedback(mathProblemSetHandler.currentProblem());
		}
	}


	private VariableText[] userAnswerDigitsTextObjects=new VariableText[3];
	private void setupNextProblemAndUpdateUI(){
		numAttempts=0;
		VariableText usersAnswerFirstDigit=new VariableText("x",UI_FONT_COLOR,-UI_FONT_SIZE/2-UI_FONT_SIZE/4,0,UI_FONT_SIZE);
		VariableText usersAnswerSecondDigit=new VariableText("x",UI_FONT_COLOR,0,0,UI_FONT_SIZE);
		VariableText usersAnswerThirdDigit=new VariableText("x",UI_FONT_COLOR,UI_FONT_SIZE/2+UI_FONT_SIZE/4,0,UI_FONT_SIZE);
		userAnswerDigitsTextObjects[0]=usersAnswerFirstDigit;
		userAnswerDigitsTextObjects[1]=usersAnswerSecondDigit;
		userAnswerDigitsTextObjects[2]=usersAnswerThirdDigit;
		mathProblemSetHandler.prepareNextProblem();
		mathProblemUI.clearAnswerScreen();


		for(int i=0;i<userAnswerDigitsTextObjects.length;i++){
			mathProblemUI.getAnswerScreen().addVariableText(userAnswerDigitsTextObjects[i]);
		}

		mathProblemUI.updateProblemScreen(mathProblemSetHandler.currentProblem());




	}

	private void resetFeedbackScreens(){
		feedbackManifester.resetFeedbackScreens();
	}




	public void feedbackDone(){

		if(!mathProblemSetHandler.currentProblemSetFinished()){
			setupNextProblemAndUpdateUI();
			resetFeedbackScreens(); 
		}
		else {//current problem set finished.
			if(currentPopulation.hasNext()){

				FeedbackChromosomeFactory.recordUserScoreAsFitnessTermForCurrentFeedback(feedbackManifester.getCurrentGenotype(), mathProblemSetHandler.getResultsForProblemSet());
				//tell the child performance monitor to record the summary score for the current individual.
				childPerformanceMonitor.recordSummaryScoreForCurrentIndividual(mathProblemSetHandler.getResultsForProblemSet());

				configureApplicationForNextIndividual();
				state=SIGNALING_NEXT_INDIVIDUAL;
			}
			else {
				childPerformanceMonitor.recordSummaryDataForCurrentPopulation();
				generationNumber++;
				if(generationNumber<NUM_GENERATIONS){
					if(generationNumber==NUM_GENERATIONS/2){
						shakeUpGeneticAlgorithmOnTheBasisOfIntergenerationalChildPerformance();
					}
					configureApplicationForNextGeneration();
					state=UPDATING_POPULATION;
					configureApplicationForNextIndividual();
				}
				else {
					mathProblemUI.activateOverlayScreen(allDoneScreen);
					state=FINISHED_ALL_POPULATIONS;
				}
			}
		}
	}

	private boolean shouldShakeUpGeneticAlgorithm(IntergenerationalPerformanceTrend currentTrend){
		return currentTrend==IntergenerationalPerformanceTrend.WORSENED || currentTrend==IntergenerationalPerformanceTrend.STABLE;
	}

	private void shakeUpGeneticAlgorithmOnTheBasisOfIntergenerationalChildPerformance() {
		IntergenerationalPerformanceTrend currentTrend=childPerformanceMonitor.getCurrentIntergenerationalPerformanceTrend();
		if(shouldShakeUpGeneticAlgorithm(currentTrend)){
			GAShakeupStrategy strategy=pickRandomStrategy(); //only policy I have so far
			if(strategy.hasUndoneChanges()){
				strategy.undo();
			}
			strategy.shakeUp();

		}

	}


	private GAShakeupStrategy pickRandomStrategy() {
		int rand=(int)random(0, NUM_GA_SHAKEUP_STRATEGIES);
		
		switch(rand){
		case 0:
			return adjustWeights;
		case 1:
			return mutants;
		}
		
		return deviateFromTemplates;

	}


	private void configureApplicationForNextGeneration(){
		jgapAdaptor.evolveNewIndividuals(); 
		storeReferenceToCurrentIndividuals();

		childPerformanceMonitor.prepareForNextGeneration();
	}





	@Override
	public void update(Observable arg0, Object arg1) {
		if(arg0 instanceof DisplayScreen){
			DisplayScreen s = (DisplayScreen)arg0;
			if(s.getName().equals("populationScreen")){
				mathProblemUI.removeOverlayScreen();
				state=RUNNING_INDIVIDUAL;
			}
		}

	}


	public static double constrain(double d, double lower, double upper) {

		return Math.min(Math.max(lower,d), upper);
	}

}








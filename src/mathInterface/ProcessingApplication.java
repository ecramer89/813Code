package mathInterface;
import java.util.Iterator;
import java.util.Observable;
import java.util.Observer;

import org.jgap.Chromosome;
import org.jgap.InvalidConfigurationException;

import processing.core.*;

import org.jgap.IChromosome;
public class ProcessingApplication extends PApplet implements Observer {
	private static ProcessingApplication processingAppInstance;
	public static final int APPLICATION_WIDTH=800;
	public static final int APPLICATION_HEIGHT=600;
	public static final int NUM_GENERATIONS=5;
	public static final int POPULATION_SIZE=2;

	public static ChromosomeToFeedbackManifester feedbackManifester;
	JGAPAdapter jgapAdaptor;
	UI mathProblemUI;
	MathProblemHandler mathProblemHandler;

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
	private VariableText populationUpdateTimer=new VariableText(0, UI_FONT_COLOR,0,0,UI_FONT_SIZE);
	private DisplayScreen populationUpdateScreen;
	private int generationNumber;


	private DisplayScreen allDoneScreen;

	private Button[] aptitude_buttons;
	private int delay_before_start_timer;
	public static final int HIGH=2;
	public static final int LOW=0;
	public static final int MED=1;
	public static final int NUM_APTITUDE_LEVELS=3;

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
		jgapAdaptor=JGAPAdapter.getInstance();
		mathProblemUI=UI.getInstance();
		mathProblemHandler=MathProblemHandler.getInstance();


		populationUpdateScreen=makeEmptyScreenSizedToApplication();
		populationUpdateScreen.setDurationOfDisplay(UPDATE_POPULATION_DELAY);
		populationUpdateScreen.addVariableText(new VariableText("Evolving next generation... please wait.",UI_FONT_COLOR,0,0,20));
		populationUpdateScreen.newLine();
		populationUpdateScreen.newLine();
		populationUpdateScreen.addVariableText(populationUpdateTimer);
		populationUpdateScreen.setName("populationScreen");
		populationUpdateScreen.addObserver(this);


		allDoneScreen=makeEmptyScreenSizedToApplication();
		allDoneScreen.addVariableText(new VariableText("All finished.",UI_FONT_COLOR,0,0,20));

		initializeRecordAptitudeButtons();


		configureGenotype();		




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
			return;
		case SIGNALING_NEXT_INDIVIDUAL:
			handleTransitionScreen();
			text("Set completed.", width/2, height/2);
			text("Configuring next problem set... one moment.", width/2, height/2+50);
			return;
		}

		mathProblemUI.update();
		if(state==UPDATING_POPULATION){
			updateDisplayablePopulationTimer();
		}



		//for testing
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

	void handlePressedAptitudeButton(Button button){
		button.flashToIndicatePressed(this);
		recordButtonIDAsAptitude(button.id);

		state=DELAY_BEFORE_START;
		delay_before_start_timer=DELAY_BEFORE_START_TIME;

		//evolve the first population given the child's level of aptitude
		//and each of the initial populations distances from the 
		//templates, times the weighting those templates have for the child's level of aptitude
		//(i.e., if they're closer to the templates that have negative weightings...
		//for this child's aptitude
		//then that will impact fitness negatively
		jgapAdaptor.updatePopulation(JGAPAdapter.ONLY_RANK_BY_TEMPLATES);
		storeReferenceToCurrentPopulation();
		configureApplicationForNextIndividual(); //for first individual.



	}





	private void recordButtonIDAsAptitude(int id) {
		// TODO Auto-generated method stub
		curr_child_aptitude=id;	
	}


	public void keyPressed(){
		if (KeyInterpreter.isInt(key)) {
			mathProblemHandler.addDigitToAnswer(KeyInterpreter.toInt(key));
		}
		if (KeyInterpreter.isDelete(key)) {
			mathProblemHandler.removeDigitFromAnswer();
		}



		updateUserAnswerDigits();


	}


	private void updateUserAnswerDigits(){
		int[] userAnswerDigits=mathProblemHandler.currentAnswerDigits();

		int i=0;
		for(;i<userAnswerDigits.length;i++){
			userAnswerDigitsTextObjects[i].update(userAnswerDigits[i]);
		}
		for(;i<userAnswerDigitsTextObjects.length;i++){
			userAnswerDigitsTextObjects[i].update("");
		}

	}



	private void updateDisplayablePopulationTimer() {
		populationUpdateTimer.update(populationUpdateTimer.toInt()+1);

	}

	private void configureGenotype(){
		try {
			jgapAdaptor.createInitialGenotype();
		} catch (InvalidConfigurationException e) {
			e.printStackTrace();
		}
	}



	private void storeReferenceToCurrentPopulation(){

		currentPopulation=jgapAdaptor.iterator();
	}


	//you should call this when you are done with the current indiviaul
	private void configureApplicationForNextIndividual(){
		//get a fresh new set of problems
		mathProblemHandler.initializeNewProblemSet();
		mathProblemHandler.createRandomProblems();
		//update feedback variables
		feedbackManifester.setCurrentFeedbackChromosome(currentPopulation.next());
		feedbackManifester.initializeFeedbackScreens();
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
				mathProblemHandler.updateUserScore();
			}
			feedbackManifester.provideFeedback(mathProblemHandler.currentProblem());
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
		mathProblemHandler.prepareNextProblem();
		mathProblemUI.clearAnswerScreen();


		for(int i=0;i<userAnswerDigitsTextObjects.length;i++){
			mathProblemUI.getAnswerScreen().addVariableText(userAnswerDigitsTextObjects[i]);
		}

		mathProblemUI.updateProblemScreen(mathProblemHandler.currentProblem());




	}

	private void resetFeedbackScreens(){
		feedbackManifester.resetFeedbackScreens();
	}




	public void feedbackDone(){

		if(!mathProblemHandler.currentProblemSetFinished()){
			setupNextProblemAndUpdateUI();
			resetFeedbackScreens(); 
		}
		else {//current problem set finished.
			if(currentPopulation.hasNext()){

				ChromosomeFactory.recordUserScoreAsFitnessTermForCurrentFeedback(feedbackManifester.getCurrentGenotype(), mathProblemHandler.getResultsForProblemSet());
				configureApplicationForNextIndividual();
				state=SIGNALING_NEXT_INDIVIDUAL;
			}
			else {
				generationNumber++;
				if(generationNumber<NUM_GENERATIONS){
					jgapAdaptor.updatePopulation(JGAPAdapter.INCOPORATE_USER_SCORE_INTO_RANKING); 
					storeReferenceToCurrentPopulation();
					state=UPDATING_POPULATION;
					populationUpdateTimer.update(0);
					mathProblemUI.activateOverlayScreen(populationUpdateScreen);
					configureApplicationForNextIndividual();
				}
				else {
					mathProblemUI.activateOverlayScreen(allDoneScreen);
					state=FINISHED_ALL_POPULATIONS;
				}
			}
		}
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

}








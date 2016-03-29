package mathInterface;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import processing.core.*;

public class UI  {
	/* user interface of the main mathematics application. */
	static UI instance;
	static ProcessingApplication processing;


	// display screen information
	DisplayScreen backgroundScreen;
	DisplayScreen problemScreen;
	DisplayScreen answerScreen;
	List<DisplayScreen> feedbackScreens = new LinkedList<DisplayScreen>();


	// display constants
	static final int TEXT_SIZE = 60;



	static final int PROBLEM_SCREEN_Y_OFFSET = -100;
	static final int PROBLEM_SCREEN_WIDTH=(int)(processing.APPLICATION_WIDTH/6);
	static final int PROBLEM_SCREEN_HEIGHT=TEXT_SIZE;
	static final int PROBLEM_SCREEN_X_OFFSET = -(int)(PROBLEM_SCREEN_WIDTH/2);


	static final int ANSWER_SCREEN_WIDTH=(int)(processing.APPLICATION_WIDTH/6);
	static final int ANSWER_SCREEN_HEIGHT=PROBLEM_SCREEN_HEIGHT;
	static final int ANSWER_SCREEN_X_OFFSET= (int)(PROBLEM_SCREEN_WIDTH/2+ANSWER_SCREEN_WIDTH/2);
	static final int ANSWER_SCREEN_Y_OFFSET=PROBLEM_SCREEN_Y_OFFSET;


	// button information
	private static final int SUBMIT_ID = 0;
	private static final int BUTTON_HEIGHT = 80;
	private static final int BUTTON_WIDTH = 100;
	private static final String SUBMIT_MESSAGE = "Done!";


	private List<Button> buttons = new LinkedList<Button>();
	private DisplayScreen overlay;



	private UI() {
		processing = ProcessingApplication.getInstance();
		initializeBackgroundScreen();
		initializeProblemScreen();
		initializeAnswerScreen();
		initializeButtons();
	}



	private void initializeProblemScreen() {
		problemScreen=new DisplayScreen(PROBLEM_SCREEN_X_OFFSET,PROBLEM_SCREEN_Y_OFFSET,PROBLEM_SCREEN_WIDTH,PROBLEM_SCREEN_HEIGHT);
		problemScreen.setBackgroundColor(processing.UI_BACKGROUND_COLOR);

		problemScreen.setName("problem");
		problemScreen.activate();
	}



	private void initializeAnswerScreen(){
		answerScreen=new DisplayScreen(ANSWER_SCREEN_X_OFFSET, ANSWER_SCREEN_Y_OFFSET, ANSWER_SCREEN_WIDTH, ANSWER_SCREEN_HEIGHT);
		answerScreen.setBackgroundColor(processing.UI_BACKGROUND_COLOR);
		answerScreen.setName("answer");
		answerScreen.activate();
	}



	private void initializeBackgroundScreen() {
		backgroundScreen=processing.makeEmptyScreenSizedToApplication();
		backgroundScreen.setName("background");
		backgroundScreen.activate();
	}

	void initializeButtons() {
		Button submit = new Button(processing.APPLICATION_WIDTH / 2
				- BUTTON_WIDTH, processing.APPLICATION_HEIGHT / 2
				- BUTTON_HEIGHT, BUTTON_WIDTH, BUTTON_HEIGHT, SUBMIT_ID);
		submit.setMessage(SUBMIT_MESSAGE,
				BUTTON_HEIGHT / SUBMIT_MESSAGE.length());
		buttons.add(submit);

	}

	public static UI getInstance() {
		if (instance == null)
			instance = new UI();
		return instance;
	}




	public void update(){
		pushTransform();
		if(overlay==null) runMainMathProblemUI();
		else runOverlay();
		popTransform();
	}

	private void runMainMathProblemUI(){
		updateScreens();
		updateButtons();
	}

	private void runOverlay(){
		overlay.update();
	}


	void updateScreens() {
		backgroundScreen.update();
		problemScreen.update();
		answerScreen.update();
		for(DisplayScreen screen : feedbackScreens){
			screen.update();
		}
	}


	void pushTransform() {
		processing.pushMatrix();
		processing.pushStyle();
		
		processing.translate(processing.APPLICATION_WIDTH / 2,
				processing.APPLICATION_HEIGHT / 2);

	}

	void popTransform() {
		processing.popMatrix();
		processing.popStyle();

	}


	private void updateButtons() {
		for (Button button : buttons) {
			updateButton(button);
		}
	}

	private void updateButton(Button button) {
		button.update(processing);
	}

	/* intercepts clicks */
	public void mousePressed(int mouseX, int mouseY) {
		Button pressed=identifyPressedButton(mouseX, mouseY);
		if(pressed!=null){
			handlePressedButton(pressed);
		}
	}

	Button identifyPressedButton(int mouseX, int mouseY) {
		for(Button button : buttons){
			if (!button.isPressed() && button.wasPressed(mouseX, mouseY)) return button; 
		}
		return null;
	}

	void handlePressedButton(Button button){
		button.flashToIndicatePressed(processing);
		int button_id = button.id;
		switch (button_id) {
		case SUBMIT_ID:
			processing.handleSubmittedAnswer();
			return;
		}
	}


	/* refactor (for speed): store a single ref. to the currentProblem object and just update it */
	public void updateProblemScreen(MathProblem problem) {
		problemScreen.clearText();
		problemScreen.addVariableText(new VariableText(problem.arg1, processing.UI_FONT_COLOR,-processing.UI_FONT_SIZE*2,0,processing.UI_FONT_SIZE));
		problemScreen.addVariableText(new VariableText(" ", processing.UI_FONT_COLOR,-processing.UI_FONT_SIZE*2+processing.UI_FONT_SIZE/2,0,processing.UI_FONT_SIZE));
		problemScreen.addVariableText(new VariableText(problem.op, processing.UI_FONT_COLOR,-processing.UI_FONT_SIZE*2+processing.UI_FONT_SIZE,0,processing.UI_FONT_SIZE));
		problemScreen.addVariableText(new VariableText(" ", processing.UI_FONT_COLOR,-processing.UI_FONT_SIZE*2+processing.UI_FONT_SIZE/2*3,0,processing.UI_FONT_SIZE));
		problemScreen.addVariableText(new VariableText(problem.arg2, processing.UI_FONT_COLOR,-processing.UI_FONT_SIZE*2+processing.UI_FONT_SIZE*2,0,processing.UI_FONT_SIZE));
		problemScreen.addVariableText(new VariableText(" ", processing.UI_FONT_COLOR,-processing.UI_FONT_SIZE*2+processing.UI_FONT_SIZE/2*5,0,processing.UI_FONT_SIZE));
		problemScreen.addVariableText(new VariableText(problem.EQUALS, processing.UI_FONT_COLOR,-processing.UI_FONT_SIZE*2+processing.UI_FONT_SIZE*3,0,processing.UI_FONT_SIZE));
		

	}

	/* refactor here too; we should need to clear the text so much as just replace the string inside of a global answer object*/
	public void clearAnswerScreen() {
		answerScreen.clearText();

	}



	public void setFeedbackScreens(List<DisplayScreen> feedbackScreens) {
		this.feedbackScreens=feedbackScreens;

	}


	//displays the specified screen for a given amount of time before switching back
	//to the default application view.
	public void activateOverlayScreen(DisplayScreen overlay){
		overlay.activate();
		this.overlay=overlay;	
		System.out.println(this.overlay.getName());
	}

	public void removeOverlayScreen(){
		overlay=null;
	}


	public DisplayScreen getAnswerScreen() {
		// TODO Auto-generated method stub
		return answerScreen;
	}


	public DisplayScreen getProblemScreenTransform(){
		return problemScreen.getTransform();
	}

	public DisplayScreen getAnswerScreenTransform(){
		return problemScreen.getTransform();
	}
















}

package mathInterfaceBaseCode;
import java.util.Iterator;

import org.jgap.Chromosome;
import org.jgap.InvalidConfigurationException;

import processing.core.*;

import org.jgap.IChromosome;
public class IECMathInterfaceProcessingApplication extends PApplet {
	private static IECMathInterfaceProcessingApplication processingAppInstance;
	public static final int APPLICATION_WIDTH=800;
	public static final int APPLICATION_HEIGHT=600;
	public static final int NUM_GENERATIONS=5;
	public static final int POPULATION_SIZE=6;



	public static final int NUM_RATINGS=6;
	public static final int RATING_FIELD_HEIGHT=(int)(.15*APPLICATION_HEIGHT);
	public static final int RATING_FIELD_CENTER_X=APPLICATION_WIDTH/2;
	public static final int RATING_FIELD_CENTER_Y=APPLICATION_HEIGHT-RATING_FIELD_HEIGHT/2;
	public static final int RATING_BUTTON_WIDTH=(int)(.8*(APPLICATION_WIDTH/NUM_RATINGS));
	public static final int RATING_BUTTON_HEIGHT=(int)(.8*RATING_FIELD_HEIGHT);
	public static final int RATING_BUTTON_OFFSET=(APPLICATION_WIDTH/NUM_RATINGS)-RATING_BUTTON_WIDTH;



	public static final float RATING_FIELD_COLOR=120;
	public static final float RATING_BUTTON_COLOR=80;
	public static final float RATING_LABEL_COLOR=255;
	public static final int RATING_LABEL_SIZE=RATING_BUTTON_HEIGHT;
	IECRatingButton[] buttons=new IECRatingButton[NUM_RATINGS];

	IECChromosomeToUIManifester iecUI;
	IECJGAPAdapter JGAPAdapter;

	Iterator<IChromosome> currentPopulation;


	public static final int RUNNING_INDIVIDUAL=0;
	public static final int UPDATING_POPULATION=1;
	public static final int DONE=3;
	public static final int UPDATE_POPULATION_DELAY = 60;


	private int state;
	private int populationUpdateTimer;
	private int generationNumber;

	public void settings(){
		size(APPLICATION_WIDTH,APPLICATION_HEIGHT);
	}



	public void setup(){
		processingAppInstance=this;
		iecUI=IECChromosomeToUIManifester.getInstance();
		JGAPAdapter=IECJGAPAdapter.getInstance();
		try {
			JGAPAdapter.createInitialGenotype();
		} catch (InvalidConfigurationException e) {
			e.printStackTrace();
		}
		currentPopulation=JGAPAdapter.iterator();

		iecUI.updateIndividual(currentPopulation.next());
		
		
		
		
		
		
		initializeRatingButtons();
	}

	public static PApplet getInstance(){
		return processingAppInstance;
	}





	private void initializeRatingButtons(){
		IECRatingButton.setButtonColor(RATING_BUTTON_COLOR);
		IECRatingButton.setHeight(RATING_BUTTON_HEIGHT);
		IECRatingButton.setLabelColor(RATING_LABEL_COLOR);
		IECRatingButton.setLabelSize(RATING_LABEL_SIZE);
		IECRatingButton.setWidth(RATING_BUTTON_WIDTH);
		IECRatingButton.setProcessingUI(this);

		for(int i=0;i<buttons.length;i++){
			IECRatingButton button=new IECRatingButton(RATING_BUTTON_OFFSET+(RATING_BUTTON_OFFSET+RATING_BUTTON_WIDTH)*i,RATING_FIELD_CENTER_Y,i);
			buttons[i]=button;
		}
	}



	public void draw() {
		switch(state){
		case RUNNING_INDIVIDUAL:
			iecUI.draw(); 
			renderRatingField();
			break;
		case UPDATING_POPULATION:
			timeUpdatingPopulation();
			showUpdatingPopulationScreen();
			break;

		case DONE:
			iecUI.draw();
			renderDoneField();
			break;
		}
	}


	private void renderDoneField() {
		fill(RATING_FIELD_COLOR);
		pushMatrix();
		translate(RATING_FIELD_CENTER_X,RATING_FIELD_CENTER_Y);
		rect(-APPLICATION_WIDTH/2,-RATING_FIELD_HEIGHT/2,APPLICATION_WIDTH,RATING_FIELD_HEIGHT);
		pushStyle();
		textAlign(CENTER);
		textSize(20);
		fill(0);
		text("Fittest individual of last generation.",0,0);
		popStyle();
		popMatrix();

	}



	private void showUpdatingPopulationScreen() {
		background(0);
		pushMatrix();
		pushStyle();
		translate(width/2, height/2);
		textSize(20);
		fill(255);
		textAlign(CENTER);
		text("Evolving next generation... please wait.",0,0);
		text(populationUpdateTimer,0,height/6);
		popMatrix();
		popStyle();	
	}



	private void timeUpdatingPopulation() {
		if(populationUpdateTimer==UPDATE_POPULATION_DELAY){
			state=RUNNING_INDIVIDUAL;
		}
		else populationUpdateTimer++;

	}



	private void renderRatingField() {
		fill(RATING_FIELD_COLOR);
		pushMatrix();
		translate(RATING_FIELD_CENTER_X,RATING_FIELD_CENTER_Y);
		rect(-APPLICATION_WIDTH/2,-RATING_FIELD_HEIGHT/2,APPLICATION_WIDTH,RATING_FIELD_HEIGHT);
		popMatrix();

		for(int i=0;i<buttons.length;i++){
			IECRatingButton button=buttons[i];
			button.render();
		}

	}

	public void mousePressed(){

		if(clickedInRatingField(mouseX, mouseY)){
			for(int i=0;i<buttons.length;i++){
				IECRatingButton button=buttons[i];
				if(button.wasClicked(mouseX,mouseY)){
					iecUI.receiveRating(button.getRating());
					if(currentPopulation.hasNext()){
						iecUI.updateIndividual(currentPopulation.next());
					}
					else {
						//update the population
						if(generationNumber<NUM_GENERATIONS){
							JGAPAdapter.updatePopulation();
							currentPopulation=JGAPAdapter.iterator();
							iecUI.updateIndividual(currentPopulation.next());
							state=UPDATING_POPULATION;
							populationUpdateTimer=0;
							generationNumber++;
						}
						else {
							state=DONE;
							iecUI.updateIndividual(JGAPAdapter.getFittest());
						}
					}

				}

			}
		}
		else {
			iecUI.mousePressed(mouseX,mouseY);
		}
	}


	boolean clickedInRatingField(int x, int y){
		return abs(y-RATING_FIELD_CENTER_Y)<RATING_FIELD_HEIGHT/2;
	}


	public void keyPressed(){

		iecUI.keyPressed(key);


	}





}

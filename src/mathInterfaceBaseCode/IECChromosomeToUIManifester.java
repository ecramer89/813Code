package mathInterfaceBaseCode;

import org.jgap.Chromosome;
import org.jgap.Configuration;
import org.jgap.Gene;
import org.jgap.IChromosome;
import org.jgap.InvalidConfigurationException;
import org.jgap.impl.DoubleGene;
import org.jgap.impl.IntegerGene;



/*
 * class that is responsible for translating an individual of the population 
 * of chromosomes into a user interface.
 * 
 * It renders all of the widgets and it handles all user inputs.
 * 
 * 
 */
public class IECChromosomeToUIManifester {
	static IECMathInterfaceProcessingApplication processingUI;
	static IECChromosomeToUIManifester theInstance;
	public static final int USER_RATING_GENE_POS = 0;
	public static final int BACKGROUND_COLOR_R_GENE_POS=1;
	public static final int BACKGROUND_COLOR_G_GENE_POS=2;
	public static final int BACKGROUND_COLOR_B_GENE_POS=3;
	
	public static final int TEXT_COLOR_R_GENE_POS=4;
	public static final int TEXT_COLOR_G_GENE_POS=5;
	public static final int TEXT_COLOR_B_GENE_POS=6;
	
	public static final int TEXT_SIZE_GENE_POS=7;
	public static final int TEXT_X_GENE_POS=8;
	public static final int TEXT_Y_GENE_POS=9;

	public static final int NUM_GENES = 10;
	
	
	IChromosome individual;
	
	/*variables that are interpreted from the individual (chromosome)*/
	private int[] bgColor;
	private int[] fontColor;
	private int fontSize;
	private int[] fontPosition;
	
	
	private IECChromosomeToUIManifester(){
		processingUI=(IECMathInterfaceProcessingApplication) IECMathInterfaceProcessingApplication.getInstance();
	}
	
	public static IECChromosomeToUIManifester getInstance(){
		if(theInstance==null)
			theInstance=new IECChromosomeToUIManifester();
		return theInstance;
	}
	
	public void updateIndividual(IChromosome iChromosome){
		this.individual=iChromosome;
		updateInterfaceVariables();
	}
	
	
	
	public void draw(){
		processingUI.background(bgColor[0],bgColor[1],bgColor[2]);
		processingUI.textSize(fontSize);
		processingUI.pushMatrix();
		processingUI.translate(fontPosition[0],fontPosition[1]);
		processingUI.pushStyle();
		processingUI.textAlign(processingUI.CENTER);
		processingUI.fill(fontColor[0],fontColor[1],fontColor[2]);
		processingUI.text("Hello World!",0,0);
		processingUI.popStyle();
		processingUI.popMatrix();
	}
	
	private void updateInterfaceVariables(){
		updateBackgroundColor();
		updateFontColor();
		updateFontSize();
		updateFontPosition();
	}
	
	
	private void updateFontPosition() {
		fontPosition=new int[]{
				(Integer)individual.getGene(TEXT_X_GENE_POS).getAllele(),
				(Integer)individual.getGene(TEXT_Y_GENE_POS).getAllele(),
		};
		
	}

	private void updateFontSize() {
		fontSize=(Integer)individual.getGene(TEXT_SIZE_GENE_POS).getAllele();
	}

	private void updateFontColor() {
		fontColor=new int[]{
				(Integer)individual.getGene(TEXT_COLOR_R_GENE_POS).getAllele(),
				(Integer)individual.getGene(TEXT_COLOR_G_GENE_POS).getAllele(),
				(Integer)individual.getGene(TEXT_COLOR_B_GENE_POS).getAllele()
		};
		
	}

	private void updateBackgroundColor() {
		bgColor=new int[]{
				(Integer)individual.getGene(BACKGROUND_COLOR_R_GENE_POS).getAllele(),
				(Integer)individual.getGene(BACKGROUND_COLOR_G_GENE_POS).getAllele(),
				(Integer)individual.getGene(BACKGROUND_COLOR_B_GENE_POS).getAllele()
		};
		
	}


	public void mousePressed(int mouseX, int mouseY) {

	}

	public void keyPressed(char key) {

	}

	public void receiveRating(int rating) {

		individual.getGene(USER_RATING_GENE_POS).setAllele(rating);

	}

	public static Gene[] getGenes(Configuration conf)
			throws InvalidConfigurationException {
		Gene background_r = new IntegerGene(conf, 0, 255); // red channel
		Gene background_g = new IntegerGene(conf, 0, 255); // red channel
		Gene background_b = new IntegerGene(conf, 0, 255); // red channel

		Gene text_x = new IntegerGene(conf, 0,
				IECMathInterfaceProcessingApplication.APPLICATION_WIDTH);
		Gene text_y = new IntegerGene(conf, 0,
				IECMathInterfaceProcessingApplication.APPLICATION_HEIGHT);

		Gene text_r = new IntegerGene(conf, 0, 255); // red channel
		Gene text_g = new IntegerGene(conf, 0, 255); // red channel
		Gene text_b = new IntegerGene(conf, 0, 255); // red channel

		Gene text_size = new IntegerGene(conf, 20, 200);

		Gene userRating = new IntegerGene(conf, 0,
				IECMathInterfaceProcessingApplication.NUM_RATINGS);

		Gene[] genes = new Gene[NUM_GENES];
		genes[USER_RATING_GENE_POS] = userRating;

		genes[BACKGROUND_COLOR_R_GENE_POS] = background_r;
		genes[BACKGROUND_COLOR_G_GENE_POS] = background_g;
		genes[BACKGROUND_COLOR_B_GENE_POS] = background_b;

		genes[TEXT_COLOR_R_GENE_POS] = text_r;
		genes[TEXT_COLOR_G_GENE_POS] = text_g;
		genes[TEXT_COLOR_B_GENE_POS] = text_b;

		genes[TEXT_SIZE_GENE_POS] = text_size;
		genes[TEXT_X_GENE_POS] = text_x;
		genes[TEXT_Y_GENE_POS] = text_y;

		return genes;
	}
	

}

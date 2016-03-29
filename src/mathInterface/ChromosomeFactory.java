package mathInterface;

import org.jgap.Configuration;
import org.jgap.Gene;
import org.jgap.IChromosome;
import org.jgap.InvalidConfigurationException;
import org.jgap.impl.DoubleGene;
import org.jgap.impl.IntegerGene;

public class ChromosomeFactory {



	//in milliseconds
	private static final int MAX_DELAY = 400;

	/*Prepares a set of sample genes for use by the configuration code
	 * just needs to specify the variable types and limits on each gene in the chromosome */
	public static Gene[] getSampleGenes(Configuration conf)
			throws InvalidConfigurationException {

		Gene textRGene= new IntegerGene(conf, 0, 255);
		Gene textGGene= new IntegerGene(conf, 0, 255);
		Gene textBGene= new IntegerGene(conf, 0, 255);

		Gene delayGene = new DoubleGene(conf, 0, MAX_DELAY);

		Gene userScoreGene = new DoubleGene(conf, 0, 100.0);

		Gene verificationTypeGene = new IntegerGene(conf,0,3);
		Gene verificationModalityGene=new IntegerGene(conf,0,5);


		Gene pElaborateGene=new DoubleGene(conf, .99,1);
		Gene delayUntilElaborateGene=new IntegerGene(conf, 0,MAX_DELAY);
		//given elaboration occurred.
		Gene pAttributeIsolationGene=new DoubleGene(conf, .99,1);
		Gene attributeIsolationDurationGene=new IntegerGene(conf, 100,MAX_DELAY*2);


		Gene[] genes = new Gene[GenePosition.values().length];

		genes[GenePosition.USER_SCORE.ordinal()] = userScoreGene;


		genes[GenePosition.FEEDBACK_DELAY.ordinal()] = delayGene;
		genes[GenePosition.TEXT_COLOR_R.ordinal()]=textRGene;
		genes[GenePosition.TEXT_COLOR_G.ordinal()]=textGGene;
		genes[GenePosition.TEXT_COLOR_B.ordinal()]=textBGene;


		genes[GenePosition.VERIFICATION_MODALITY.ordinal()]=verificationModalityGene;
		genes[GenePosition.VERIFICATION_TYPE.ordinal()]=verificationTypeGene;		


		genes[GenePosition.P_ELABORATE.ordinal()]=pElaborateGene;
		genes[GenePosition.DELAY_UNTIL_ELABORATE.ordinal()]=delayUntilElaborateGene;
		genes[GenePosition.P_ATTRIBUTE_ISOLATION.ordinal()]=pAttributeIsolationGene;
		genes[GenePosition.ATTRIBUTE_ISOLATION_DURATION.ordinal()]=attributeIsolationDurationGene;



		return genes;
	}





}

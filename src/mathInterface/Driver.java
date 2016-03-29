package mathInterface;


import java.util.List;

import org.jgap.Chromosome;
import org.jgap.Configuration;
import org.jgap.FitnessFunction;
import org.jgap.Gene;
import org.jgap.Genotype;
import org.jgap.IChromosome;
import org.jgap.InvalidConfigurationException;
import org.jgap.Population;
import org.jgap.impl.DefaultConfiguration;
import org.jgap.impl.DoubleGene;
import org.jgap.impl.IntegerGene;

import processing.core.PApplet;
import processing.core.*;


/*main class. Responsible for starting the processing application. */

public class Driver {

  static final String PROCESSING_APPLICATION="mathInterface.ProcessingApplication";


	public static void main(String[] args) throws InvalidConfigurationException {
      
      //start processing sketch
     PApplet.main(new String[] { PROCESSING_APPLICATION });
	}
	
	
	

}

package mathInterface;

import java.util.Iterator;
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


/* class that interfaces between the main application and the JGap library methods*/


public class JGAPAdapter implements Iterable<IChromosome> {

	 static JGAPAdapter theInstance;
	 Genotype genotype;
	 
	 private JGAPAdapter(){}
	 
	 public static JGAPAdapter getInstance(){
		 if(theInstance==null) theInstance=new JGAPAdapter();
		 return theInstance;
	 }
	 
	 
	 public void createInitialGenotype() throws InvalidConfigurationException{
		 Configuration conf=new DefaultConfiguration();
	      FitnessFunction myfunction=new IECFitnessFunction();
	      conf.setFitnessFunction(myfunction);
	      
	      //create a sample chromosome to indicate
	      //to the configuration object how we want the chromosome to be setup.
	     
	      Gene[] genes=ChromosomeFactory.getSampleGenes(conf);
	      IChromosome sampleChromosome=new Chromosome(conf, genes);
	      conf.setSampleChromosome(sampleChromosome);
	      
	      conf.setPopulationSize(ProcessingApplication.POPULATION_SIZE);
	      
	      
	      //create initial population (genotype)
	      genotype=Genotype.randomInitialGenotype(conf);
	 }
	 
	

	@Override
	/* returns an iterator over the current population */
	public Iterator<IChromosome> iterator() {
	
		return genotype.getPopulation().getChromosomes().iterator();
	}

	public void updatePopulation() {
		genotype.evolve();
		
	}

	public IChromosome getFittest() {
	
		return genotype.getFittestChromosome();
	}

	 

	
	 
	 
}

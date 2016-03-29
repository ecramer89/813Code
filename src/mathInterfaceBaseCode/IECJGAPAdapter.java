package mathInterfaceBaseCode;

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

public class IECJGAPAdapter implements Iterable<IChromosome> {

	 static IECJGAPAdapter theInstance;
	 Genotype genotype;
	 
	 private IECJGAPAdapter(){}
	 
	 public static IECJGAPAdapter getInstance(){
		 if(theInstance==null) theInstance=new IECJGAPAdapter();
		 return theInstance;
	 }
	 
	 
	 public void createInitialGenotype() throws InvalidConfigurationException{
		 Configuration conf=new DefaultConfiguration();
	      FitnessFunction myfunction=new IECFitnessFunction();
	      conf.setFitnessFunction(myfunction);
	      
	      //create a sample chromosome to indicate
	      //to the configuration object how we want the chromosome to be setup.
	     
	      Gene[] genes=IECChromosomeToUIManifester.getGenes(conf);
	      IChromosome sampleChromosome=new Chromosome(conf, genes);
	      conf.setSampleChromosome(sampleChromosome);
	      
	      conf.setPopulationSize(IECMathInterfaceProcessingApplication.POPULATION_SIZE);
	      
	      
	      //create initial population (genotype)
	      genotype=Genotype.randomInitialGenotype(conf);
	 }
	 
	

	@Override
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

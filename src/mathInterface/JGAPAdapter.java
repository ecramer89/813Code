package mathInterface;

import java.util.Iterator;
import java.util.List;

import org.jgap.Chromosome;
import org.jgap.Configuration;
import org.jgap.FitnessFunction;
import org.jgap.Gene;
import org.jgap.GeneticOperator;
import org.jgap.Genotype;
import org.jgap.IChromosome;
import org.jgap.InvalidConfigurationException;
import org.jgap.Population;
import org.jgap.impl.DefaultConfiguration;
import org.jgap.impl.DoubleGene;
import org.jgap.impl.IntegerGene;
import org.jgap.impl.MutationOperator;


/* class that interfaces between the main application and the JGap library methods*/


public class JGAPAdapter implements Iterable<IChromosome> {

	 public static final boolean ONLY_RANK_BY_TEMPLATES = false;
	 public static final boolean INCOPORATE_USER_SCORE_INTO_RANKING = true;
	 
	 

	 Genotype genotype;
	 IECFitnessFunction fitnessFunction;
	 
	 public JGAPAdapter(){
		 fitnessFunction=new IECFitnessFunction();
	 }
	 

	 public  IECFitnessFunction getFitnessFunction(){
		 return fitnessFunction;
	 }
	 
	 
	 public void createInitialGenotype(int populationSize) throws InvalidConfigurationException{
		 Configuration conf=new DefaultConfiguration();
	      //fitnessFunction=new IECFitnessFunction();
	      conf.setFitnessFunction(fitnessFunction);
	     System.out.println("Message from JGap adater");
	     System.out.println("Genetic operators in the default configuration: ");
	     for(Object op : conf.getGeneticOperators()){
	    	 System.out.println(op.getClass().toString());
	    	 if(op instanceof MutationOperator){
	    		 MutationOperator mut = (MutationOperator)op;
	    		 System.out.println("Mutation rate: "+mut.getMutationRate());
	    	 }
	     }
	 
	      
	      //create a sample chromosome to indicate
	      //to the configuration object how we want the chromosome to be setup.
	     
	      Gene[] genes=FeedbackChromosomeFactory.getSampleGenes(conf);
	      IChromosome sampleChromosome=new Chromosome(conf, genes);
	      conf.setSampleChromosome(sampleChromosome);
	      
	      conf.setPopulationSize(populationSize);
	      
	      
	      //create initial population (genotype)
	      genotype=Genotype.randomInitialGenotype(conf);
	 }
	 
	 
	 

	@Override
	/* returns an iterator over the current population */
	public Iterator<IChromosome> iterator() {
	
		return genotype.getPopulation().getChromosomes().iterator();
	}

	public void updatePopulation(boolean incorporateUserRating) {
		fitnessFunction.setIncorporateUserRating(incorporateUserRating);
		genotype.evolve();
		
	}

	public IChromosome getFittest() {
	
		return genotype.getFittestChromosome();
	}

	 

	
	 
	 
}

package mathInterface;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.jgap.Chromosome;
import org.jgap.Configuration;
import org.jgap.FitnessFunction;
import org.jgap.Gene;
import org.jgap.GeneticOperator;
import org.jgap.Genotype;
import org.jgap.IChromosome;
import org.jgap.InvalidConfigurationException;
import org.jgap.NaturalSelector;
import org.jgap.Population;
import org.jgap.impl.BestChromosomesSelector;
import org.jgap.impl.CrossoverOperator;
import org.jgap.impl.DefaultConfiguration;
import org.jgap.impl.DoubleGene;
import org.jgap.impl.IntegerGene;
import org.jgap.impl.MutationOperator;


/* class that interfaces between the main application and the JGap library methods*/


public class JGAPAdapter implements Iterable<IChromosome> {
	public static final int DEFAULT_MUTATION_RATE=6;
	public static final boolean ONLY_RANK_BY_TEMPLATES = false;
	public static final boolean INCOPORATE_USER_SCORE_INTO_RANKING = true;
	public static final int POPULATION_SIZE = 80;


	public static final double DEFAULT_PROPORTION_SELECT_FROM_FITTEST=.8;

	private Configuration conf;
	private Genotype genotype;
	private IECFitnessFunction fitnessFunction;
	private MutationOperator mutationOperator;

	UserChromosomeSelector selector;
	private Set<IChromosome> selectedForPresenationToUser=new HashSet<IChromosome>();;

	public JGAPAdapter(){
		selector=new UserChromosomeSelector(DEFAULT_PROPORTION_SELECT_FROM_FITTEST);
		conf=new DefaultConfiguration();
		fitnessFunction=new IECFitnessFunction();
		try {
			initializeConfiguration();
			configureGenotype();

		} catch (InvalidConfigurationException e) {

			e.printStackTrace();
		}


	}




	private void configureGenotype() throws InvalidConfigurationException {

		genotype=Genotype.randomInitialGenotype(conf);

	}

	public UserChromosomeSelector getUserChromosomeSelector(){
		return selector;
	}


	private void initializeConfiguration() throws InvalidConfigurationException {
		mutationOperator = (MutationOperator)conf.getGeneticOperators().get(1);
		mutationOperator.setMutationRate(DEFAULT_MUTATION_RATE);

		conf.setFitnessFunction(fitnessFunction);
		conf.setPreservFittestIndividual(true);
		Gene[] genes=FeedbackChromosomeFactory.getSampleGenes(conf);
		IChromosome sampleChromosome=new Chromosome(conf, genes);
		conf.setSampleChromosome(sampleChromosome);
		conf.setPopulationSize(POPULATION_SIZE);

	}


	public void accelerateMutationRate(double scale){
		int newRate=(int)(mutationOperator.getMutationRate()*scale);
		mutationOperator.setMutationRate(newRate);
	}

	public void deaccelerateMutationRate(double scale){
		int newRate=(int)(mutationOperator.getMutationRate()/scale);
		mutationOperator.setMutationRate(newRate);
	}



	public  IECFitnessFunction getFitnessFunction(){
		return fitnessFunction;
	}


	@Override
	/* returns an iterator over the (smaller) set of individuals chosen for
	 * presentation to the user. */
	public Iterator<IChromosome> iterator() {

		return selectedForPresenationToUser.iterator();
	}

	public void evolveNewIndividuals() {
		setUserRatingOfUnusedIndividualsToDiscount();
		genotype.evolve();
		selectIndividualsForPresentationToUser();
	}

	private void selectIndividualsForPresentationToUser() {
		selectedForPresenationToUser=new HashSet<IChromosome>();
		selector.selectChromosomesToDisplayToUser(ProcessingApplication.NUM_INDIVIDUALS_TO_SHOW_USER, genotype.getPopulation().getChromosomes(), selectedForPresenationToUser);

		List<IChromosome> l=genotype.getPopulation().getChromosomes();

	}



	private void setUserRatingOfUnusedIndividualsToDiscount() {
		for(IChromosome ch : this){
			if(!selectedForPresenationToUser.contains(ch)){
				ch.getGene(GenePosition.USER_SCORE.ordinal()).setAllele(new Double(Feedback.DISCOUNT));
			}
		}

	}


	public IChromosome getFittest() {

		return genotype.getFittestChromosome();
	}


	public Configuration getConfiguration() {

		return conf;
	}






}

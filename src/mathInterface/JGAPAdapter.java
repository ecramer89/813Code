package mathInterface;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
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
	public static final int POPULATION_SIZE = ProcessingApplication.POPULATION_SIZE;


	public static final double DEFAULT_PROPORTION_SELECT_FROM_FITTEST=.8;

	private Configuration conf;
	private Genotype genotype;
	private IECFitnessFunction fitnessFunction;
	private MutationOperator mutationOperator;

	private UserChromosomeSelector selector;
	private Map<IChromosome,Double> selectedForPresenationToUser=new HashMap<IChromosome, Double>();
	private static JGAPAdapter theInstance;

	private JGAPAdapter(){
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

	public static JGAPAdapter getInstance(){
		if(theInstance==null) theInstance=new JGAPAdapter();
		return theInstance;
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

		return selectedForPresenationToUser.keySet().iterator();
	}




	public void replaceActivePopulation(Population newPopulation){
		Population active=genotype.getPopulation();


		if(ProcessingApplication.PRINT_DEBUG_MESSAGES){
			System.out.println("message from adaptor: ");
			System.out.println("replacing active population: ");
		}

		active.clear();
		for(int i=0;i<newPopulation.size();i++){
			active.addChromosome(newPopulation.getChromosome(i));
		}



		if(ProcessingApplication.PRINT_DEBUG_MESSAGES){
			System.out.println("new population: ");
			for(int i=0;i<active.size();i++){
				System.out.println(active.getChromosome(i).toString());
			}
		}

	}

	public void evolveNewIndividuals() {

		genotype.evolve();
		selectIndividualsForPresentationToUser();
	}

	private void selectIndividualsForPresentationToUser() {
		selectedForPresenationToUser=new HashMap<IChromosome,Double>();
		selector.selectChromosomesToDisplayToUser(ProcessingApplication.NUM_INDIVIDUALS_TO_SHOW_USER_PER_GENERATION, genotype.getPopulation().getChromosomes(), selectedForPresenationToUser);


		if(ProcessingApplication.PRINT_DEBUG_MESSAGES){
			System.out.println("number of chromo selected "+selectedForPresenationToUser.size());
		}
	}


	public double getUserScoreFor(IChromosome a_subject){
		return selectedForPresenationToUser.get(a_subject);
	}


	public void setUserScoreFor(IChromosome a_subject, double userScore){


		selectedForPresenationToUser.put(a_subject, userScore);


	}

	public boolean wasSelectedForPresentationToUser(IChromosome a_subject){
		return selectedForPresenationToUser.containsKey(a_subject);
	}


	public Configuration getConfiguration() {

		return conf;
	}

	public Population getCurrentPopulation() {
		// TODO Auto-generated method stub
		return genotype.getPopulation();
	}

	public void printPopulation() {
		Population active=genotype.getPopulation();

		System.out.println("message from adaptor: ");
		System.out.println("curr population: ");
		for(int i=0;i<active.size();i++){
			System.out.println(active.getChromosome(i).toString());
		}

	}






}

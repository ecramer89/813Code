package mathInterface;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jgap.Configuration;
import org.jgap.IChromosome;
import org.jgap.InvalidConfigurationException;
import org.jgap.NaturalSelector;
import org.jgap.Population;


public class UserChromosomeSelector  {

	double prop_from_head;
	double prop_from_tail;


	public UserChromosomeSelector(double prop_from_head){
		adjustProportionsOfFittestToLeastFit(prop_from_head);

	}

	public void adjustProportionsOfFittestToLeastFit(double adjust_most_fit){
		if(ProcessingApplication.PRINT_DEBUG_MESSAGES){
			System.out.println("message from selector: ");
			System.out.println("adjusting proportions");
			System.out.println("adjustment: "+adjust_most_fit);
		}
		prop_from_head+=adjust_most_fit;
		prop_from_head=ProcessingApplication.constrain(prop_from_head, 0, 1);
		prop_from_tail=1-prop_from_head;
		if(ProcessingApplication.PRINT_DEBUG_MESSAGES){
			System.out.println("from head: "+prop_from_head);
			System.out.println("from tail: "+prop_from_tail);
		}

	}


	public class ComparableChromosome implements Comparable{


		IChromosome chromosome;


		public ComparableChromosome(IChromosome ic) {
			chromosome=ic;
		}


		@Override
		public int compareTo(Object o) {

			ComparableChromosome other=(ComparableChromosome)o;
			Double my_fitness=chromosome.getFitnessValueDirectly();
			Double other_fitness=other.chromosome.getFitnessValueDirectly();

			return my_fitness.compareTo(other_fitness);
		}



	}


	public void selectChromosomesToDisplayToUser(int a_howManyToSelect,
			List<IChromosome> from_pop, Map<IChromosome,Double> to_set){


		List<ComparableChromosome> c=new LinkedList<ComparableChromosome>();

		for(IChromosome ic : from_pop){
			//ic.getFitnessValue();
			c.add(new ComparableChromosome(ic));
		}



		int from_tail=(int)(a_howManyToSelect*prop_from_tail);

		if(from_tail==0){
			from_tail++;

		}

		int from_head=a_howManyToSelect-from_tail;

		if(ProcessingApplication.PRINT_DEBUG_MESSAGES){
			System.out.println("message from selector: ");
			System.out.println("should select: "+a_howManyToSelect);
			System.out.println("from head: "+from_head);
			System.out.println("from tail: "+from_tail);
		}



		int idx=0;
		for(int i=0;i<from_head;i++){
			IChromosome to_Add=c.get(idx).chromosome;

			if(ProcessingApplication.PRINT_DEBUG_MESSAGES){
				System.out.println("idx: "+idx);
				System.out.println("adding: "+to_Add);
			}
			to_set.put(to_Add,(double) 0);
			idx++;

		}

		idx=from_pop.size()-1;
		for(int j=0; j<from_tail;j++){
			IChromosome to_Add=c.get(idx).chromosome;
			if(ProcessingApplication.PRINT_DEBUG_MESSAGES){
				System.out.println("idx: "+idx);
				System.out.println("adding: "+to_Add);
			}
			to_set.put(to_Add,(double) 0);
			idx--;
		}

	}






}

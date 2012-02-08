/*
 * Copyright 2001-2003 Neil Rotstan Copyright (C) 2004 Derek James and Philip Tucker
 * 
 * This file is part of JGAP.
 * 
 * JGAP is free software; you can redistribute it and/or modify it under the terms of the GNU
 * Lesser Public License as published by the Free Software Foundation; either version 2.1 of the
 * License, or (at your option) any later version.
 * 
 * JGAP is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser Public License along with JGAP; if not,
 * write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA
 * 02111-1307 USA
 * 
 * Modified on Feb 3, 2003 by Philip Tucker
 */
package org.jgap;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.jgap.event.GeneticEvent;

import com.anji.neat.NeatCrossoverReproductionOperator;

/**
 * Genotypes are fixed-length populations of chromosomes. As an instance of a
 * <code>Genotype</code> is evolved, all of its <code>Chromosome</code> objects are also
 * evolved. A <code>Genotype</code> may be constructed normally, whereby an array of
 * <code>Chromosome</code> objects must be provided, or the static
 * <code>randomInitialGenotype()</code> method can be used to generate a <code>Genotype</code>
 * with a randomized <code>Chromosome</code> population. Changes made by Tucker and James for
 * <a href="http://anji.sourceforge.net/">ANJI </a>:
 * <ul>
 * <li>added species</li>
 * <li>modified order of operations in <code>evolve()</code></li>
 * <li>added <code>addChromosome*()</code> methods</li>
 * </ul>
 */
public class Genotype implements Serializable {

	/** Generated ID  */
	private static final long serialVersionUID = -1656185539021476474L;

	/**
	 * Convenience method that returns a newly constructed Genotype instance configured according to
	 * the given Configuration instance. The population of Chromosomes will created according to the
	 * setup of the sample Chromosome in the Configuration object, but the gene values (alleles)
	 * will be set to random legal values.
	 * <p>
	 * Note that the given Configuration instance must be in a valid state at the time this method
	 * is invoked, or an InvalidConfigurationException will be thrown.
	 * 
	 * @param a_activeConfiguration
	 * @return A newly constructed Genotype instance.
	 * @throws InvalidConfigurationException if the given Configuration instance not in a valid
	 * state.
	 */
	public static Genotype randomInitialGenotype( Configuration a_activeConfiguration )
	throws InvalidConfigurationException {
		if ( a_activeConfiguration == null ) {
			throw new IllegalArgumentException( "The Configuration instance may not be null." );
		}

		a_activeConfiguration.lockSettings();

		// Create an array of chromosomes equal to the desired size in the
		// active Configuration and then populate that array with Chromosome
		// instances constructed according to the setup in the sample
		// Chromosome, but with random gene values (alleles). The Chromosome
		// class' randomInitialChromosome() method will take care of that for
		// us.
		// ------------------------------------------------------------------
		int populationSize = a_activeConfiguration.getPopulationSize();
		List<Chromosome> chroms = new ArrayList<Chromosome>( populationSize );

		for ( int i = 0; i < populationSize; i++ ) {
			ChromosomeMaterial material = ChromosomeMaterial.randomInitialChromosomeMaterial( a_activeConfiguration );
			chroms.add( new Chromosome( material, a_activeConfiguration.nextChromosomeId() ) );
		}

		return new Genotype( a_activeConfiguration, chroms );
	}

	/**
	 * The current active Configuration instance.
	 */
	protected Configuration m_activeConfiguration;

	/**
	 * Chromosomes that makeup thie Genotype's population.
	 */
	protected List<Chromosome> m_chromosomes = new ArrayList<Chromosome>();

	/**
	 * Species that makeup this Genotype's population.
	 */
	protected List<Specie> m_species = new ArrayList<Specie>();

	/**
	 * This constructor is used for random initial Genotypes. Note that the Configuration object
	 * must be in a valid state when this method is invoked, or a InvalidconfigurationException will
	 * be thrown.
	 * 
	 * @param a_activeConfiguration The current active Configuration object.
	 * @param a_initialChromosomes <code>List</code> contains Chromosome objects: The Chromosome
	 * population to be managed by this Genotype instance.
	 * @throws IllegalArgumentException if either the given Configuration object or the array of
	 * Chromosomes is null, or if any of the Genes in the array of Chromosomes is null.
	 * @throws InvalidConfigurationException if the given Configuration object is in an invalid
	 * state.
	 */
	public Genotype( Configuration a_activeConfiguration, List<Chromosome> a_initialChromosomes )
	throws InvalidConfigurationException {
		// Sanity checks: Make sure neither the Configuration, the array
		// of Chromosomes, nor any of the Genes inside the array are null.
		// ---------------------------------------------------------------
		if ( a_activeConfiguration == null )
			throw new IllegalArgumentException( "The Configuration instance may not be null." );

		if ( a_initialChromosomes == null )
			throw new IllegalArgumentException( "The array of Chromosomes may not be null." );

		for ( int i = 0; i < a_initialChromosomes.size(); i++ ) {
			if ( a_initialChromosomes.get( i ) == null )
				throw new IllegalArgumentException( "The Chromosome instance at index " + i
						+ " of the array of " + "Chromosomes is null. No instance in this array may be null." );
		}

		// Lock the settings of the Configuration object so that the cannot
		// be altered.
		// ----------------------------------------------------------------
		a_activeConfiguration.lockSettings();
		m_activeConfiguration = a_activeConfiguration;

		addChromosomes( a_initialChromosomes );
	}

	/**
	 * add chromosome to population and to appropriate specie
	 * 
	 * @param chrom
	 */
	protected void addChromosome( Chromosome chrom ) {
		m_chromosomes.add( chrom );
		speciate(chrom);
	}

	/**
	 * @param cMat chromosome material from which to construct new chromosome object
	 * @see Genotype#addChromosome(Chromosome)
	 */
	protected void addChromosomeFromMaterial( ChromosomeMaterial cMat ) {
		Chromosome chrom = new Chromosome( cMat, m_activeConfiguration.nextChromosomeId() );
		addChromosome( chrom );
	}

	/**
	 * @param chromosomes <code>Collection</code> contains Chromosome objects
	 * @see Genotype#addChromosome(Chromosome)
	 */
	protected void addChromosomes( Collection<Chromosome> chromosomes ) {
		for (Chromosome c : chromosomes) {
			addChromosome( c );
		}
	}

	/**
	 * @param chromosomeMaterial <code>Collection</code> contains ChromosomeMaterial objects
	 * @see Genotype#addChromosomeFromMaterial(ChromosomeMaterial)
	 */
	protected void addChromosomesFromMaterial( Collection<ChromosomeMaterial> chromosomeMaterial ) {
		for (ChromosomeMaterial cMat : chromosomeMaterial) {
			addChromosomeFromMaterial( cMat );
		}
	}

	/**
	 * adjust chromosome list to fit population size; first, clone population (starting at beginning
	 * of list) until we reach or exceed pop. size or trim excess (from end of list)
	 * 
	 * @param chroms <code>List</code> contains <code>Chromosome</code> objects
	 * @param targetSize
	 */
	private void adjustChromosomeList( List<ChromosomeMaterial> chroms, int targetSize ) {
		List<ChromosomeMaterial> more = new ArrayList<ChromosomeMaterial>( chroms );
		
		while ( chroms.size() < targetSize ) {
			Collections.shuffle(more);
			chroms.addAll(more);
		}
		while ( chroms.size() > targetSize ) {
			// remove from end of list
			chroms.remove( chroms.size() - 1 );
		}
	}

	/**
	 * Compares this Genotype against the specified object. The result is true if the argument is an
	 * instance of the Genotype class, has exactly the same number of chromosomes as the given
	 * Genotype, and, for each Chromosome in this Genotype, there is an equal chromosome in the
	 * given Genotype. The chromosomes do not need to appear in the same order within the
	 * populations.
	 * 
	 * @param other The object to compare against.
	 * @return true if the objects are the same, false otherwise.
	 */
	public boolean equals( Object other ) {
		try {
			// First, if the other Genotype is null, then they're not equal.
			// -------------------------------------------------------------
			if ( other == null ) {
				return false;
			}

			Genotype otherGenotype = (Genotype) other;

			// First, make sure the other Genotype has the same number of
			// chromosomes as this one.
			// ----------------------------------------------------------
			if ( m_chromosomes.size() != otherGenotype.m_chromosomes.size() ) {
				return false;
			}

			// Next, prepare to compare the chromosomes of the other Genotype
			// against the chromosomes of this Genotype. To make this a lot
			// simpler, we first sort the chromosomes in both this Genotype
			// and the one we're comparing against. This won't affect the
			// genetic algorithm (it doesn't care about the order), but makes
			// it much easier to perform the comparison here.
			// --------------------------------------------------------------
			Collections.sort( m_chromosomes );
			Collections.sort( otherGenotype.m_chromosomes );

			Iterator iter = m_chromosomes.iterator();
			Iterator otherIter = otherGenotype.m_chromosomes.iterator();
			while ( iter.hasNext() && otherIter.hasNext() ) {
				Chromosome chrom = (Chromosome) iter.next();
				Chromosome otherChrom = (Chromosome) otherIter.next();
				if ( !( chrom.equals( otherChrom ) ) ) {
					return false;
				}
			}

			return true;
		}
		catch ( ClassCastException e ) {
			return false;
		}
	}
	
	
	/**
	 * Performs one generation cycle, evaluating fitness, selecting survivors, repopulting with
	 * offspring, and mutating new population. This is a modified version of original JGAP method
	 * which changes order of operations and splits <code>GeneticOperator</code> into
	 * <code>ReproductionOperator</code> and <code>MutationOperator</code>. New order of
	 * operations:
	 * <ol>
	 * <li>assign <b>fitness </b> to all members of population with
	 * <code>BulkFitnessFunction</code> or <code>FitnessFunction</code></li>
	 * <li><b>select </b> survivors and remove casualties from population</li>
	 * <li>re-fill population with offspring via <b>reproduction </b> operators</li>
	 * <li><b>mutate </b> offspring (note, survivors are passed on un-mutated)</li>
	 * </ol>
	 * Genetic event <code>GeneticEvent.GENOTYPE_EVALUATED_EVENT</code> is fired between steps 2
	 * and 3. Genetic event <code>GeneticEvent.GENOTYPE_EVOLVED_EVENT</code> is fired after step
	 * 4.
	 */
	public synchronized void evolve() {
		try {
			m_activeConfiguration.lockSettings();

			// If a bulk fitness function has been provided, then convert the
			// working pool to an array and pass it to the bulk fitness
			// function so that it can evaluate and assign fitness values to
			// each of the Chromosomes.
			// --------------------------------------------------------------
			BulkFitnessFunction bulkFunction = m_activeConfiguration.getBulkFitnessFunction();
			if ( bulkFunction != null )
				bulkFunction.evaluate( m_chromosomes );
			else {
				// Refactored such that Chromosome does not need a reference to Configuration. Left his
				// in for backward compatibility, but it makes more sense to use BulkFitnessFunction
				// now.
				FitnessFunction function = m_activeConfiguration.getFitnessFunction();
				Iterator it = m_chromosomes.iterator();
				while ( it.hasNext() ) {
					Chromosome c = (Chromosome) it.next();
					c.setFitnessValue( function.getFitnessValue( c ) );
				}
			}
			
			// Fire an event to indicate we've evaluated all chromosomes.
			// -------------------------------------------------------
			m_activeConfiguration.getEventManager().fireGeneticEvent(
					new GeneticEvent( GeneticEvent.GENOTYPE_EVALUATED_EVENT, this ) );

			// Select chromosomes to survive.
			// ------------------------------------------------------------
			NaturalSelector selector = m_activeConfiguration.getNaturalSelector();
			selector.add( m_activeConfiguration, m_chromosomes );
			m_chromosomes = selector.select( m_activeConfiguration );
			selector.empty();

			// Repopulate the population of species and chromosomes with those selected
			// by the natural selector, and cull species down to contain only remaining
			// chromosomes.
			Iterator<Specie> speciesIter = m_species.iterator();
			while ( speciesIter.hasNext() ) {
				Specie s = (Specie) speciesIter.next();
				s.cull( m_chromosomes );
				if ( s.isEmpty() )
					speciesIter.remove();
			}

			// Fire an event to indicate we're starting genetic operators. Among
			// other things this allows for RAM conservation.
			// -------------------------------------------------------
			m_activeConfiguration.getEventManager().fireGeneticEvent(
					new GeneticEvent( GeneticEvent.GENOTYPE_START_GENETIC_OPERATORS_EVENT, this ) );

			// Execute Reproduction Operators.
			// -------------------------------------
			List<ChromosomeMaterial> offspring = new ArrayList<ChromosomeMaterial>();
			for (ReproductionOperator operator : m_activeConfiguration.getReproductionOperators()) {
				operator.reproduce( m_activeConfiguration, m_species, offspring );
			}

			// Execute Mutation Operators.
			// -------------------------------------
			for (MutationOperator operator : m_activeConfiguration.getMutationOperators()) {
				operator.mutate( m_activeConfiguration, offspring );
			}

			// in case we're off due to rounding errors
			Collections.shuffle( offspring, m_activeConfiguration.getRandomGenerator() );
			adjustChromosomeList( offspring, m_activeConfiguration.getPopulationSize() - m_chromosomes.size() );

			// add offspring
			// ------------------------------
			addChromosomesFromMaterial( offspring );

			// Fire an event to indicate we're starting genetic operators. Among
			// other things this allows for RAM conservation.
			// -------------------------------------------------------
			m_activeConfiguration.getEventManager().fireGeneticEvent(
					new GeneticEvent( GeneticEvent.GENOTYPE_FINISH_GENETIC_OPERATORS_EVENT, this ) );

			// Fire an event to indicate we've performed an evolution.
			// -------------------------------------------------------
			m_activeConfiguration.getEventManager().fireGeneticEvent(
					new GeneticEvent( GeneticEvent.GENOTYPE_EVOLVED_EVENT, this ) );
		}
		catch ( InvalidConfigurationException e ) {
			throw new RuntimeException( "bad config", e );
		}
	}
	
	/**
	 * @return List contains Chromosome objects, the population of Chromosomes.
	 */
	public synchronized List<Chromosome> getChromosomes() {
		return m_chromosomes;
	}

	/**
	 * Retrieves the Chromosome in the population with the highest fitness value.
	 * 
	 * @return The Chromosome with the highest fitness value, or null if there are no chromosomes in
	 * this Genotype.
	 */
	public synchronized Chromosome getFittestChromosome() {
		if ( getChromosomes().isEmpty() )
			return null;

		// Set the highest fitness value to that of the first chromosome.
		// Then loop over the rest of the chromosomes and see if any has
		// a higher fitness value.
		// --------------------------------------------------------------
		Chromosome fittestChromosome = null;
		for (Chromosome chrom : getChromosomes()) {
			if (fittestChromosome == null)
				fittestChromosome = chrom;
			// Added to record fittest individual during novelty search
			if (chrom.getMiscValue() > fittestChromosome.getMiscValue())
				fittestChromosome = chrom;
			//		// Removed temporarily while novelty is the selection criteria
			//		if (chrom.getFitnessValue() > fittestChromosome.getFitnessValue())
			//			fittestChromosome = chrom;
		}
		return fittestChromosome;
	}

	/** Retrieves the Chromosome in the population with the lowest speciated fitness value.  Typically applied during
	 *  steady-state evolution.
	 * 
	 * @return The Chromosome with the lowest speciated fitness value, or null if there are no chromosomes in
	 * this Genotype.
	 */
	private Chromosome getPoorestChromosome() {
		if ( getChromosomes().isEmpty() )
			return null;

		Collections.sort(m_chromosomes, new ChromosomeFitnessComparator(true, false));
		return m_chromosomes.get(0);
	}

	/**
	 * @return List contains Specie objects
	 */
	public synchronized List<Specie> getSpecies() {
		return m_species;
	}

	public void replaceLeastFitIndividual() {
		try {
			// Fire an event to indicate we're starting genetic operators. Among
			// other things this allows for RAM conservation.
			// -------------------------------------------------------
			m_activeConfiguration.getEventManager().fireGeneticEvent(
					new GeneticEvent( GeneticEvent.GENOTYPE_START_GENETIC_OPERATORS_EVENT, this ) );						

			for (int i=0; i<m_activeConfiguration.getPopulationSize(); i++) {
				m_activeConfiguration.getBulkFitnessFunction().evaluate(m_chromosomes);

				Collections.sort(m_chromosomes, new ChromosomeFitnessComparator(true, true));
				m_chromosomes.remove(0);

				// Repopulate the population of species and chromosomes with those selected
				// by the natural selector, and cull species down to contain only remaining
				// chromosomes.
				Iterator<Specie> speciesIter = m_species.iterator();
				while ( speciesIter.hasNext() ) {
					Specie s = (Specie) speciesIter.next();
					s.cull( m_chromosomes );
					if ( s.isEmpty() )
						speciesIter.remove();
				}

				// Execute Reproduction Operators.
				// -------------------------------------
				List<ChromosomeMaterial> offspring = new ArrayList<ChromosomeMaterial>();
				for (ReproductionOperator operator : m_activeConfiguration.getReproductionOperators()) {
					if (operator instanceof NeatCrossoverReproductionOperator)
						operator.reproduce( m_activeConfiguration, m_species, offspring );
				}

				// Execute Mutation Operators.
				// -------------------------------------
				for (MutationOperator operator : m_activeConfiguration.getMutationOperators()) {
					operator.mutate( m_activeConfiguration, offspring );
				}

				// add offspring
				// ------------------------------
				addChromosomesFromMaterial( offspring );

				if (m_chromosomes.size() > m_activeConfiguration.getPopulationSize())
					System.out.println("Populations size is now " + m_chromosomes.size());
			}
			
			// Fire an event to indicate we've evaluated all chromosomes.
			// -------------------------------------------------------
			m_activeConfiguration.getEventManager().fireGeneticEvent(
					new GeneticEvent( GeneticEvent.GENOTYPE_EVALUATED_EVENT, this ) );

			// Fire an event to indicate we're starting genetic operators. Among
			// other things this allows for RAM conservation.
			// -------------------------------------------------------
			m_activeConfiguration.getEventManager().fireGeneticEvent(
					new GeneticEvent( GeneticEvent.GENOTYPE_FINISH_GENETIC_OPERATORS_EVENT, this ) );

			// Fire an event to indicate we've performed an evolution.
			// -------------------------------------------------------
			m_activeConfiguration.getEventManager().fireGeneticEvent(
					new GeneticEvent( GeneticEvent.GENOTYPE_EVOLVED_EVENT, this ) );
		}
		catch ( InvalidConfigurationException e ) {
			throw new RuntimeException( "bad config", e );
		}
	}

	private void reSpeciatePopulation() {
		m_species.clear();
		for(Chromosome c : getChromosomes()) {
			c.m_specie = null;
			speciate(c);
		}
	}

	public synchronized void speciate(Chromosome chrom) {
		boolean added = false;
		for (Specie specie : m_species) {
			if ( specie.match( chrom ) ) {
				specie.add( chrom );
				added = true;
				break;
			}
		}
		if ( !added ) {
			Specie specie = new Specie( m_activeConfiguration.getSpeciationParms(), chrom );
			m_species.add( specie );
		}		
	}

	/**
	 * @return <code>String</code> representation of this <code>Genotype</code> instance.
	 */
	public String toString() {
		StringBuffer buffer = new StringBuffer();

		for (Chromosome chrom : m_chromosomes) {
			buffer.append( chrom.toString() );
			buffer.append( " [" );
			buffer.append( chrom.getFitnessValue() );
			buffer.append( ']' );
			buffer.append( '\n' );
		}

		return buffer.toString();
	}

}

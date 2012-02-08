/*
 * Copyright (C) 2004 Derek James and Philip Tucker
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
 * Created on Feb 3, 2003 by Philip Tucker
 */
package org.jgap;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Species are reproductively isolated segments of a population. They are used to ensure
 * diversity in the population. This can protect innovation, and also serve to maintain a
 * broader search space, avoiding being trapped in local optima.
 * 
 * @author Philip Tucker
 */
public class Specie {

	/**
	 * XML chromosome tag
	 */
	public final static String CHROMOSOME_TAG = "chromosome";
	
	/**
	 * XML count tag
	 */
	public final static String COUNT_TAG = "count";
	
	/**
	 * XML fitness tag
	 */
	public final static String FITNESS_TAG = "fitness";
	
	/**
	 * XML ID tag
	 */
	public final static String ID_TAG = "id";
	
	/**
	 * XML base tag
	 */
	public final static String SPECIE_TAG = "specie";
	
	/**
	 * chromosomes active in current population; these logically should be a <code>Set</code>,
	 * but we use a <code>List</code> to make random selection easier, specifically in
	 * <code>ReproductionOperator</code>
	 */
	private List<Chromosome> chromosomes = new ArrayList<Chromosome>();
	
	private Chromosome fittest = null;
	
	private Chromosome representative = null;
	
	private SpeciationParms speciationParms = null;
		
	/**
	 * Create new specie from representative. Representative is first member of specie, and all
	 * other members of specie are determined by compatibility with representative. Even if
	 * representative dies from population, a reference is kept here to determine specie membership.
	 * @param aSpeciationParms
	 * @param aRepresentative
	 */
	public Specie( SpeciationParms aSpeciationParms, Chromosome aRepresentative ) {
		representative = aRepresentative;
		aRepresentative.setSpecie( this );
		chromosomes.add( aRepresentative );
		speciationParms = aSpeciationParms;
	}
	
	/**
	 * @param aChromosome
	 * @return true if chromosome is added, false if chromosome already is a member of this specie
	 */
	public boolean add( Chromosome aChromosome ) {
		if ( !match( aChromosome ) )
			throw new IllegalArgumentException( "chromosome does not match specie: " + aChromosome );
		if ( chromosomes.contains( aChromosome ) )
			return false;
		aChromosome.setSpecie( this );
		fittest = null;
		return chromosomes.add( aChromosome );
	}
	
	/**
	 * remove all chromosomes from this specie except <code>keepers</code>
	 * 
	 * @param keepers <code>Collection</code> contains chromosome objects
	 */
	public void cull( Collection<Chromosome> keepers ) {
		fittest = null;
		chromosomes.retainAll( keepers );
	}
	
	/**
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals( Object o ) {
		Specie other = (Specie) o;
		return representative.equals( other.representative );
	}
	
	/**
	 * @param aChromosome
	 * @return double adjusted fitness for aChromosome relative to this specie
	 * @throws IllegalArgumentException if chromosome is not a member if this specie
	 */
	public double getChromosomeFitnessValue( Chromosome aChromosome ) {
		if ( aChromosome.getFitnessValue() < 0 )
			throw new IllegalArgumentException( "chromosome's fitness has not been set: "
					+ aChromosome.toString() );
		if ( chromosomes.contains( aChromosome ) == false )
			throw new IllegalArgumentException( "chromosome not a member of this specie: "
					+ aChromosome.toString() );
	
		return ( (double) aChromosome.getFitnessValue() ) / chromosomes.size();
	}
	
	/**
	 * @return all chromosomes in specie
	 */
	public List<Chromosome> getChromosomes() {
		return Collections.unmodifiableList( chromosomes );
	}
	
	/**
	 * @return total adjusted fitness (i.e. adjusted for specie size) of all chromosomes in specie
	 */
	public double getFitnessValue() {
		long totalRawFitness = 0;
		for (Chromosome aChromosome : chromosomes) {
			if ( aChromosome.getFitnessValue() < 0 )
				throw new IllegalStateException( "chromosome's fitness has not been set: "
						+ aChromosome.toString() );
			totalRawFitness += aChromosome.getFitnessValue();
		}
	
		return (double) totalRawFitness / chromosomes.size();
	}
	
	/**
	 * @return Chromosome fittest in this specie
	 */
	public synchronized Chromosome getFittest() {
		for (Chromosome ch : chromosomes) {
			if ( fittest == null ) fittest = ch;
			if ( ch.getFitnessValue() > fittest.getFitnessValue() )
				fittest = ch;
		}
		return fittest;
	}
	
	/**
	 * @return representative chromosome
	 */
	protected Chromosome getRepresentative() {
		return representative;
	}
	
	/**
	 * @return unique ID; this is chromosome ID of representative
	 */
	public Long getRepresentativeId() {
		return representative.getId();
	}
	
	/**
	 * @see java.lang.Object#hashCode()
	 */
	public int hashCode() {
		return representative.hashCode();
	}
	
	/**
	 * @return true iff specie contains no active chromosomes in population
	 */
	public boolean isEmpty() {
		return chromosomes.isEmpty();
	}
	
	/**
	 * @param aChromosome
	 * @return boolean true iff compatibility difference between
	 * <code>aChromosome</code? and representative
	 * is less than speciation threshold
	 */
	public boolean match( Chromosome aChromosome ) {
		return ( representative.distance( aChromosome, speciationParms ) < speciationParms.getSpeciationThreshold() );
	}
	
	/**
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		StringBuffer result = new StringBuffer( "Specie " );
		result.append( getRepresentativeId() );
		return result.toString();
	}
	
	/**
	 * @return String XML representation of object according to <a
	 * href="http://nevt.sourceforge.net/">NEVT </a>.
	 */
	public String toXml() {
		StringBuffer result = new StringBuffer();
		result.append( "<" ).append( SPECIE_TAG ).append( " " ).append( ID_TAG ).append( "=\"" );
		result.append( getRepresentativeId() ).append( "\" " ).append( COUNT_TAG ).append( "=\"" );
		result.append( getChromosomes().size() ).append( "\">\n" );
		for (Chromosome chromToStore : getChromosomes()) {
			result.append( "<" ).append( CHROMOSOME_TAG ).append( " " ).append( ID_TAG ).append( "=\"" );
			result.append( chromToStore.getId() ).append( "\" " ).append( FITNESS_TAG ).append( "=\"" );
			result.append( chromToStore.getFitnessValue() ).append( "\" />\n" );
		}
		result.append( "</" ).append( SPECIE_TAG ).append( ">\n" );
		return result.toString();
	}

}

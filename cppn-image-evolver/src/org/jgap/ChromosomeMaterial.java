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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * This is the guts of the original Chromosome object, pulled out so the genes can be modified
 * by genetic operators before creating the Chromosome object. Also enables us to handle special
 * cases, like sample chromosome, where you don't need a Configuration or fitness value. Also,
 * made methods not synchronized, since only Genotype.evolve() should be modifying this object.
 */
public class ChromosomeMaterial implements Comparable<ChromosomeMaterial>, Serializable {

	private static final long serialVersionUID = -3787302764560986521L;
	
	private static long getMaxInnovationId( Collection<Allele> someAlleles ) {
		long result = -1;
		for (Allele allele : someAlleles) {
			if ( allele.getInnovationId().longValue() > result )
				result = allele.getInnovationId().longValue();
		}
		return result;
	}
	
	/**
	 * Convenience method that returns a new Chromosome instance with its genes values (alleles)
	 * randomized. Note that, if possible, this method will acquire a Chromosome instance from the
	 * active ChromosomePool (if any) and then randomize its gene values before returning it. If a
	 * Chromosome cannot be acquired from the pool, then a new instance will be constructed and its
	 * gene values randomized before returning it.
	 * 
	 * @param a_activeConfiguration The current active configuration.
	 * @return new <code>ChromosomeMaterial</code>
	 * 
	 * @throws InvalidConfigurationException if the given Configuration instance is invalid.
	 * @throws IllegalArgumentException if the given Configuration instance is null.
	 */
	public static ChromosomeMaterial randomInitialChromosomeMaterial(
			Configuration a_activeConfiguration ) throws InvalidConfigurationException {
		// Sanity check: make sure the given configuration isn't null.
		// -----------------------------------------------------------
		if ( a_activeConfiguration == null ) {
			throw new IllegalArgumentException( "Configuration instance must not be null" );
		}
	
		// Lock the configuration settings so that they can't be changed
		// from now on.
		// -------------------------------------------------------------
		a_activeConfiguration.lockSettings();
	
		// If we got this far, then we weren't able to get a Chromosome from
		// the pool, so we have to construct a new instance and build it from
		// scratch.
		// ------------------------------------------------------------------
		ChromosomeMaterial newMaterial = a_activeConfiguration.getSampleChromosomeMaterial().clone(
				null );
	
		for (Allele newAllele : newMaterial.getAlleles()) {
	
			// Set the gene's value (allele) to a random value.
			// ------------------------------------------------
			newAllele.setToRandomValue( a_activeConfiguration.getRandomGenerator() );
		}
	
		return newMaterial;
	}
	
	private SortedSet<Allele> m_alleles = null;
	
	private Long primaryParentId = null;
	
	private Long secondaryParentId = null;
	
	/**
	 * for hibernate
	 */
	ChromosomeMaterial() {
		this( new TreeSet<Allele>(), null, null );
	}
	
	/**
	 * Create chromosome with no parents. Used for startup sample chromosome material.
	 * 
	 * @param a_initialAlleles
	 * @see ChromosomeMaterial#ChromosomeMaterial(Collection, Long, Long)
	 */
	public ChromosomeMaterial( Collection<Allele> a_initialAlleles ) {
		this( a_initialAlleles, null, null );
	}
	
	/**
	 * Create chromosome with one parents. Used for cloning.
	 * 
	 * @param a_initialGenes
	 * @param aPrimaryParentId
	 * @see ChromosomeMaterial#ChromosomeMaterial(Collection, Long, Long)
	 */
	public ChromosomeMaterial( Collection<Allele> a_initialGenes, Long aPrimaryParentId ) {
		this( a_initialGenes, aPrimaryParentId, null );
	}
	
	/**
	 * Create chromosome with two parents. Used for crossover.
	 * 
	 * @param a_initialAlleles
	 * @param aPrimaryParentId
	 * @param aSecondaryParentId
	 */
	public ChromosomeMaterial( Collection<Allele> a_initialAlleles, Long aPrimaryParentId,
			Long aSecondaryParentId ) {
		// Sanity checks: make sure the genes array isn't null and
		// that none of the genes contained within it are null.
		// -------------------------------------------------------
		if ( a_initialAlleles == null )
			throw new IllegalArgumentException( "The given List of alleles cannot be null." );
	
		setPrimaryParentId( aPrimaryParentId );
		setSecondaryParentId( aSecondaryParentId );
	
		// sanity check
		for (Allele allele : a_initialAlleles) {
			if ( allele == null )
				throw new IllegalArgumentException( "The given List of alleles cannot contain nulls." );
		}
		m_alleles = new TreeSet<Allele>( a_initialAlleles );
	}
	
	/**
	 * Returns a copy of this ChromosomeMaterial. The returned instance can evolve independently of
	 * this instance.
	 * 
	 * @param parentId represents ID of chromosome that was cloned. If this is initial chromosome
	 * material without a parent, or is the clone of material only (e.g., before the it has become a
	 * Chromosome), parentId == null.
	 * @return copy of this object
	 */
	public ChromosomeMaterial clone( Long parentId ) {
		// First we make a copy of each of the Genes. We explicity use the Gene
		// at each respective gene location (locus) to create the new Gene that
		// is to occupy that same locus in the new Chromosome.
		// -------------------------------------------------------------------
		List<Allele> copyOfAlleles = new ArrayList<Allele>( m_alleles.size() );
	
		for (Allele orig : m_alleles) {
			copyOfAlleles.add( orig.cloneAllele() );
		}
	
		// Now construct a new Chromosome with the copies of the genes and return it.
		// ---------------------------------------------------------------
		Long cloneParentId = ( parentId == null ) ? getPrimaryParentId() : parentId;
		return new ChromosomeMaterial( copyOfAlleles, cloneParentId );
	}
	
	/**
	 * Compares the given Chromosome to this Chromosome. This chromosome is considered to be "less
	 * than" the given chromosome if it has a fewer number of genes or if any of its gene values
	 * (alleles) are less than their corresponding gene values in the other chromosome.
	 * 
	 * @param other The Chromosome against which to compare this chromosome.
	 * @return a negative number if this chromosome is "less than" the given chromosome, zero if
	 * they are equal to each other, and a positive number if this chromosome is "greater than" the
	 * given chromosome.
	 * @see Comparable#compareTo(java.lang.Object)
	 */
	public int compareTo( ChromosomeMaterial other ) {
		// First, if the other ChromosomeMaterial is null, then this chromosome
		// is automatically the "greater" Chromosome.
		// ---------------------------------------------------------------
		if ( other == null )
			return 1;
	
		SortedSet<Allele> otherAlleles = other.m_alleles;
	
		// If the other Chromosome doesn't have the same number of genes,
		// then whichever has more is the "greater" Chromosome.
		// --------------------------------------------------------------
		if ( otherAlleles.size() != m_alleles.size() )
			return m_alleles.size() - otherAlleles.size();
	
		// Next, compare the gene values (alleles) for differences. If
		// one of the genes is not equal, then we return the result of its
		// comparison.
		// ---------------------------------------------------------------
		Iterator iter = m_alleles.iterator();
		Iterator otherIter = otherAlleles.iterator();
		while ( iter.hasNext() && otherIter.hasNext() ) {
			Allele allele = (Allele) iter.next();
			Allele otherAllele = (Allele) otherIter.next();
			Class srcClass = allele.getClass();
			Class targetClass = otherAllele.getClass();
			if ( srcClass != targetClass )
				return srcClass.getName().compareTo( targetClass.getName() );
	
			int comparison = allele.compareTo( otherAllele );
	
			if ( comparison != 0 )
				return comparison;
		}
	
		// Everything is equal. Return zero.
		// ---------------------------------
		return 0;
	}
	
	/**
	 * Calculates compatibility distance between this and <code>target</code> according to <a
	 * href="http://nn.cs.utexas.edu/downloads/papers/stanley.ec02.pdf">NEAT </a> speciation
	 * methodology. Made it generic enough that the genes do not have to be nodes and connections.
	 * 
	 * @param mMaterial
	 * @param speciationParms
	 * @return distance between this object and <code>target</code>
	 * @see Allele#distance(Allele)
	 */
	public double distance( ChromosomeMaterial mMaterial, SpeciationParms speciationParms ) {
		// get genes I have target does not
		List<Allele> myUnmatchedAlleles = new ArrayList<Allele>( m_alleles );
		myUnmatchedAlleles.removeAll( mMaterial.getAlleles() );
	
		// get genes target has I do not
		List<Allele> targetUnmatchedAlleles = new ArrayList<Allele>( mMaterial.getAlleles() );
		targetUnmatchedAlleles.removeAll( m_alleles );
	
		// extract excess genes
		long targetMax = getMaxInnovationId( mMaterial.getAlleles() );
		long thisMax = getMaxInnovationId( m_alleles );
		List<Allele> excessAlleles = ( targetMax > thisMax ) ? extractExcessAlleles( targetUnmatchedAlleles,
				thisMax ) : extractExcessAlleles( myUnmatchedAlleles, targetMax );
	
		// all other extras are disjoint
		List<Allele> disjointAlleles = new ArrayList<Allele>( myUnmatchedAlleles );
		disjointAlleles.addAll( targetUnmatchedAlleles );
	
		// get common connection genes
		List<Allele> myCommonAlleles = new ArrayList<Allele>( this.getAlleles() );
		myCommonAlleles.retainAll( mMaterial.getAlleles() );
		List<Allele> targetCommonAlleles = new ArrayList<Allele>( mMaterial.getAlleles() );
		targetCommonAlleles.retainAll( this.getAlleles() );
	
		// sanity test
		if ( myCommonAlleles.size() != targetCommonAlleles.size() )
			throw new IllegalStateException( "sizes of my common genes and target common genes differ" );
	
		// calculate distance for common genes
		double avgCommonDiff = 0;
		int numComparableCommonAlleles = 0;
		if ( myCommonAlleles.size() > 0 ) {
			double totalCommonDiff = 0.0;
			Iterator myIter = myCommonAlleles.iterator();
			Iterator targetIter = targetCommonAlleles.iterator();
			while ( myIter.hasNext() && targetIter.hasNext() && totalCommonDiff < Double.MAX_VALUE ) {
				Allele myAllele = (Allele) myIter.next();
				Allele targetAllele = (Allele) targetIter.next();
				if ( myAllele.getInnovationId().equals( targetAllele.getInnovationId() ) == false )
					throw new IllegalStateException( "corresponding genes do not have same innovation ids" );
				try {
					double aDistance = myAllele.distance( targetAllele );
					if ( totalCommonDiff + aDistance > Double.MAX_VALUE )
						totalCommonDiff = Double.MAX_VALUE;
					else
						totalCommonDiff += aDistance;
					++numComparableCommonAlleles;
				}
				catch ( UnsupportedOperationException e ) {
					// do nothing
				}
			}
			avgCommonDiff = totalCommonDiff / numComparableCommonAlleles;
		}
	
		// formula from "Evolving Neural Networks Through Augmenting Topologies",
		// Stanley/Miikkulainen
		long maxChromSize = Math.max( this.getAlleles().size(), mMaterial.getAlleles().size() );
		double result = 0.0d;
		if ( maxChromSize > 0 )
			// should never be 0
			result = ( ( speciationParms.getSpecieCompatExcessCoeff() * excessAlleles.size() ) / maxChromSize )
					+ ( ( speciationParms.getSpecieCompatDisjointCoeff() * disjointAlleles.size() ) / maxChromSize )
					+ ( speciationParms.getSpecieCompatCommonCoeff() * avgCommonDiff );
		return result;
	}
	
	/**
	 * Compares this Chromosome against the specified object. The result is true if and the argument
	 * is an instance of the Chromosome class and has a set of genes equal to this one.
	 * 
	 * @param other The object to compare against.
	 * @return true if the objects are the same, false otherwise.
	 */
	public boolean equals( ChromosomeMaterial other ) {
		return compareTo( other ) == 0;
	}
	
	/**
	 * returns excess alleles, defined as those alleles whose innovation ID is greater than
	 * <code>threshold</code>; also, removes excess alleles from <code>alleles</code>
	 * 
	 * @param alleles <code>List</code> contains <code>Allele</code> objects
	 * @param threshold
	 * @return excess alleles, <code>List</code> contains <code>Allele</code> objects
	 */
	private List<Allele> extractExcessAlleles( Collection<Allele> alleles, long threshold ) {
		List<Allele> result = new ArrayList<Allele>();
		for (Allele allele : alleles) {
			if ( allele.getInnovationId().longValue() > threshold ) {
				result.add( allele );
			}
		}
		for (Allele allele : result) {
			alleles.remove(allele);
		}
		return result;
	}
	
	/**
	 * Retrieves the set of genes. This method exists primarily for the benefit of GeneticOperators
	 * that require the ability to manipulate Chromosomes at a low level.
	 * 
	 * @return an array of the Genes contained within this Chromosome.
	 */
	public SortedSet<Allele> getAlleles() {
		return m_alleles;
	}
	
	/**
	 * @return primary parent ID; dominant parent if chromosome spawned by crossover
	 */
	public Long getPrimaryParentId() {
		return primaryParentId;
	}
	
	/**
	 * @return primary parent ID; recessive parent if chromosome spawned by crossover
	 */
	public Long getSecondaryParentId() {
		return secondaryParentId;
	}
	
	/**
	 * for hibernate
	 * @param aAlleles
	 */
	void setAlleles( SortedSet<Allele> aAlleles ) {
		m_alleles = aAlleles;
	}
	
	/**
	 * for hibernate
	 * @param id ID of dominant parent
	 */
	void setPrimaryParentId( Long id ) {
		if ( primaryParentId != null )
			throw new IllegalStateException( "can not set primary parent ID twice" );
		primaryParentId = id;
	}
	
	/**
	 * @param id ID of recessive parent
	 */
	public void setSecondaryParentId( Long id ) {
		if ( secondaryParentId != null )
			throw new IllegalStateException( "can not set secondary parent ID twice" );
		secondaryParentId = id;
	}

/**
 * Returns a string representation of this Chromosome, useful for some display purposes.
 * 
 * @return A string representation of this Chromosome.
 */
public String toString() {
	StringBuffer representation = new StringBuffer();
	representation.append( "[ " );

	// Append the representations of each of the gene Alleles.
	// -------------------------------------------------------
	for (Allele allele : m_alleles) {
		representation.append( allele.toString() ).append( ", ");
	}
	representation.replace(representation.length()-2, representation.length(), " ]");

	return representation.toString();
}

}

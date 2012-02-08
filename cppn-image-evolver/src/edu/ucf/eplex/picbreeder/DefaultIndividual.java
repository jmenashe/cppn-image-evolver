/*
 * Unlicensed intellectual property of the University of Central Florida for
 * internal usage only. You may not distribute this code to anyone. You may
 * not use this code (as source or compiled) or information obtained from
 * this code without permission.
 *
 * Picbreeder Project
 * Evolutionary Complexity Research Group
 * School of Electrical Engineering and Computer Science
 * 2006-2007
 */

package edu.ucf.eplex.picbreeder;

import java.awt.image.ImageObserver;

import org.jgap.Chromosome;

public class DefaultIndividual implements Individual {
	Chromosome genome = null;
	Phenotype phenotype = null;
	boolean rendered = false;
	boolean selected = false;
	
	public DefaultIndividual() {
		this(null);
	}
	
	public DefaultIndividual(Chromosome g) {
		genome = g;
		phenotype = new ImagePhenotype();
	}

	public void conserveMemory() {
		phenotype = null;
		rendered = false;
	}
	
	public int countPhenotypes() {
		return 1;
	}

	public void deselect() {
		selected = false;
	}

	public Phenotype getDominantPhenotype() {
		return getPhenotype(0);  // Hard-coded because the ANJI chromosome dosn't have genome.getDominantPhenotype()
	}

	public Chromosome getGenome() {
		return genome;
	}

	public Phenotype getPhenotype(int index) {
		if(phenotype == null)
			phenotype = new ImagePhenotype();
		return phenotype;
	}

	public int getQuality() {
		return 0;
	}
	
	public boolean hasGenome() {
		return genome != null;
	}
	
	public boolean isRendered() {
		return rendered;
	}
	
	public boolean isSelected() {
		return selected;
	}
	
	public void notifyCompleted() {
		
	}
	
	// blah to get rid of compiler crap
	public void notifyUpdated() {
		
	}
	

	public void select() {
		selected = true;
	}
	
	public void setGenome(Chromosome g) {
		genome = g;
	}
	
	public void setObserver(ImageObserver im) {
	}
	
	public void setQuality(int quality) {
	}
	
	public void setRendered(boolean value) {
		rendered = value;
	}
}

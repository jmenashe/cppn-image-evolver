/**
 * Copyright (C) 2010 Brian Woolley
 * 
 * This file is part of the octopusArm simulator.
 * 
 * The octopusArm simulator is free software; you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software Foundation; either
 * version 2 of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with this program; if
 * not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA
 * 02111-1307 USA
 * 
 * created by Brian Woolley on Jul 7, 2010
 */
package edu.ucf.eplex.imageEvolver;

import com.anji.util.Configurable;
import com.anji.util.Properties;


/**
 * @author Brian Woolley on Jul 7, 2010
 *
 */
public class FuzzyHammingDist implements Configurable {

	private static final String ALPHA_MOULATION_KEY = "imageEvolver.fitness.alpha";

	private static final double DEFAULT_ALPHA_MODULATION = 10;

	private static final int DEFAULT_MAX_FITNESS = 10000;

	private static final String MAX_FITNESS_KEY = "imageEvolver.fitness.max";
	
	private double alpha = DEFAULT_ALPHA_MODULATION;
	private int maxFitness = DEFAULT_MAX_FITNESS;
	/**
	 * Compares the difference between two arrays of EQUAL length.  A result of zero (0) indicates that the 
	 * two sets are the same and a result of one (1) indicates that the two sets are maximally different.
	 * @param subject the source set to be evaluated against the target set
	 * @param targetImage the query set, or target pattern, being compared against
	 * @return a decimal value in the range [0...1] where values closer to zero are more similar
	 */
	/**
	 * Calculates the average value acorss the Fuzzy Difference Set.  The alpha parameter adjusts the
	 * sensitivity of the comparison. i.e. for values of alpha < 1 (but > zero) report lower error 
	 * between dissimilar feature sets, while values of alpha that are are > 1 emphasize the error 
	 * between dissimilar feature sets.
	 */
	public int evaluate(double[] source, double[] target) {
		if (source.length != target.length) {
			System.err.println("Size of source [" + source.length + "] does not match size of target  [" + target.length + "]");
			return 1;
		}
		double[] weight = new double[source.length];
		for (int i=0; i<source.length; i++) 
			weight[i] = 1.0;
		return evaluate(source, target, weight);
	}

	public int evaluate(double[] source, double[] target, double[] weight) {
		if (source.length != target.length) {
			System.err.println("Size of source [" + source.length + "] does not match size of target  [" + target.length + "]");
			return -1;
		}
		if (source.length != weight.length) {
			System.err.println("Size of source and target [" +source.length+ "] does not match size of weight [" +weight.length+ "]");
			return -1;
		}

		// Calculate the degree of difference between each corresponding element in the two sets
		// For implementation purposes, this code implements the maximization function
		//		d(a,b) = e^ -alpha(a-b)^2
		// rather than the original minimization function d(a,b) = 1 - e^ -alpha(a-b)^2
		double err, diff, fitness = 0.0, features = 0;
		for (int i=0; i<source.length; i++) {
//			err = Math.pow(source[i] - target[i], 2);
			err = Math.abs(source[i] - target[i]);
			err = -alpha * err;
			diff = Math.pow(Math.E, err);
			fitness += diff * weight[i];
			features += weight[i];
		}
		
		// Calculate the average degree of difference across the difference fuzzy set (Ralescu, 2003)
		fitness = (double) fitness/features;
		
		// Keep the raw fitness value in the range [0, 1] 
		fitness = Math.max(0.0, fitness);
		fitness = Math.min(1.0, fitness);
		fitness = Math.pow(fitness, 2);
		
		// Scale fitness by the maxFitness
		return Math.round( (float) (maxFitness * fitness));
	}
	/**
	 * The highest fitness score that can be reported by the <code>evaluate()</code> method
	 * @return the highest value that can be reported by the <code>evaluate()</code> method
	 */
	public int getMaxFitness() {
		return maxFitness;
	}
	/**
	 * 
	 */
	public void init(Properties props) throws Exception {
		maxFitness = props.getIntProperty( MAX_FITNESS_KEY, DEFAULT_MAX_FITNESS );
		alpha = props.getDoubleProperty( ALPHA_MOULATION_KEY, DEFAULT_ALPHA_MODULATION );
	}

}

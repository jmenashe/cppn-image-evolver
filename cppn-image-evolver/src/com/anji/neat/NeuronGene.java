/*
 * Copyright (C) 2004 Derek James and Philip Tucker
 * 
 * This file is part of ANJI (Another NEAT Java Implementation).
 * 
 * ANJI is free software; you can redistribute it and/or modify it under the terms of the GNU
 * General Public License as published by the Free Software Foundation; either version 2 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See
 * the GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with this program; if
 * not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA
 * 02111-1307 USA
 * 
 * created by Philip Tucker on Feb 16, 2003
 */
package com.anji.neat;

import org.jgap.Gene;

import com.anji.activationFunction.ActivationFunction;

/**
 * Gene corresponding to NEAT node gene according to <a
 * href="http://nn.cs.utexas.edu/downloads/papers/stanley.ec02.pdf"> Evolving Neural Networks
 * through Augmenting Topologies </a>
 * 
 * @author Philip Tucker
 */
public class NeuronGene extends Gene {
	
	private static final long serialVersionUID = -8685648052755061350L;
	private ActivationFunction activation;
	private String label;
	
	private NeuronType type = NeuronType.HIDDEN;
	
	/**
	 * Construct new NeuronGene with given type and ID. Protected since this should only be
	 * constructed via factory methods in <code>NeuronGene</code> and
	 * <code>NeatChromosomeUtility</code>, and persistence objects.
	 * 
	 * @param newType
	 * @param newInnovationId
	 * @param anActivationFunc
	 */
	public NeuronGene( NeuronType newType, Long newInnovationId, ActivationFunction anActivationFunc ) {
		this(newType, newInnovationId, anActivationFunc, "");
	}
	
	public NeuronGene( NeuronType newType, Long newInnovationId, ActivationFunction anActivationFunc, String aLabel ) {
		super( newInnovationId );
		type = newType;
		activation = anActivationFunc;
		label = aLabel;
	}
	
	/**
	 * @return gets activation function type
	 */
	public ActivationFunction getActivation() {
		return activation;
	}
	
	public String getLabel() {
		return label;
	}
	
	/**
	 * @return type of neuron
	 * @see NeuronType
	 */
	NeuronType getType() {
		return type;
	}
	
	/**
	 * @param aType
	 * @return true iff neuron is of type <code>aType</code>
	 */
	boolean isType( NeuronType aType ) {
		return type.equals( aType );
	}
	
	/**
	 * @see Object#toString()
	 */
	public String toString() {
		return getInnovationId().toString();
	}
}

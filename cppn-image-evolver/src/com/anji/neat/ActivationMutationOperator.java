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
 * created by Brian Woolley on Jul 26, 2010
 */
package com.anji.neat;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jgap.Allele;
import org.jgap.ChromosomeMaterial;
import org.jgap.Configuration;
import org.jgap.MutationOperator;

import com.anji.activationFunction.ActivationFunction;
import com.anji.integration.AnjiRequiredException;
import com.anji.util.Configurable;
import com.anji.util.Properties;

/**
 * @author Brian Woolley on Jul 26, 2010
 *
 */
public class ActivationMutationOperator extends MutationOperator implements Configurable {

	/**
	 * properties key, prune network mutation rate
	 */
	private static final String ACTIVATION_MUTATE_RATE_KEY = "activation.mutation.rate";

	/**
	 * default mutation rate
	 */
	public final static float DEFAULT_ACTIVATION_MUTATE_RATE = 0.025f;

	/**
	 * @see ActivationMutationOperator#ActivationMutationOperator(float)
	 */
	public ActivationMutationOperator() {
		this( DEFAULT_ACTIVATION_MUTATE_RATE );
	}

	/**
	 * @see MutationOperator#MutationOperator(float)
	 */
	public ActivationMutationOperator( float newMutationRate ) {
		super( newMutationRate );
	}

	public boolean changeNeuronActivationFunction( NeatConfiguration config, Map<Long, NeuronAllele> neurons,
			Map<Long, ConnectionAllele> connections, NeuronAllele oldNeuronAllele, Set<Allele> allelesToAdd, Set<Allele> allelesToRemove ) {
		
		// Create a new new neuronAllele to replace the existing one
		NeuronAllele newNeuronAllele = config.newNeuronAllele( config.nextInnovationId() );

		// check for duplicate innovation IDs
		if ( neurons.containsKey( newNeuronAllele.getInnovationId() ) == false ) {
			neurons.put( newNeuronAllele.getInnovationId(), newNeuronAllele );
//			allelesToRemove.add( oldNeuronAllele );
			allelesToAdd.add( newNeuronAllele );

			Set<ConnectionAllele> incomingConnections = new HashSet<ConnectionAllele>();
			Set<ConnectionAllele> outgoingConnections = new HashSet<ConnectionAllele>();
			for (ConnectionAllele c : connections.values()) {
				// Build the set of incoming connections
				if (c.getDestNeuronId() == oldNeuronAllele.getInnovationId()) {
					incomingConnections.add(c);
				}
				// Build the set of outgoing connections
				if (c.getSrcNeuronId() == oldNeuronAllele.getInnovationId()) {
					outgoingConnections.add(c);
				}
			}
			
			// For all incoming connections: create a new ConnectionAllele(from oldNeuronID to newNeuronID )
			ConnectionAllele newConnectionAllele;
			for (ConnectionAllele oldConnectionAllele : incomingConnections) {
				newConnectionAllele = config.newConnectionAllele( oldConnectionAllele.getSrcNeuronId(), newNeuronAllele.getInnovationId() );
				newConnectionAllele.setWeight(oldConnectionAllele.getWeight());
//				allelesToRemove.add( oldConnectionAllele) ;
				allelesToAdd.add( newConnectionAllele );
			}		
			
			// For all outgoing connection: create a new ConnectionAllele(from newNeuronID to oldNeuronID )
			for (ConnectionAllele oldConnectionAllele : outgoingConnections) {
				newConnectionAllele = config.newConnectionAllele( newNeuronAllele.getInnovationId(), oldConnectionAllele.getDestNeuronId() );
				newConnectionAllele.setWeight(oldConnectionAllele.getWeight());
//				allelesToRemove.add( oldConnectionAllele) ;
				allelesToAdd.add( newConnectionAllele );	
			}
			
			System.out.println("neuron " +oldNeuronAllele.getInnovationId()+ ": " +oldNeuronAllele.getActivationType().toString()+
							   " --> " +newNeuronAllele.getInnovationId()+ ": " +newNeuronAllele.getActivationType().toString() );
			return true;
		}

		return false;
	}

	/**
	 * @see com.anji.util.Configurable#init(com.anji.util.Properties)
	 */
	public void init( Properties props ) throws Exception {
		setMutationRate( props.getFloatProperty( ACTIVATION_MUTATE_RATE_KEY, DEFAULT_ACTIVATION_MUTATE_RATE ) );
	}
	
	/**
	 * Get the set of hidden nodes and then change the activation function for a number of those depending on mutation rate.
	 * 
	 * @param config
	 * @param target chromosome material to mutate
	 * @param genesToAdd <code>Set</code> contains <code>Gene</code> objects
	 * @param genesToRemove <code>Set</code> contains <code>Gene</code> objects
	 * @see org.jgap.MutationOperator#mutate(org.jgap.Configuration, org.jgap.ChromosomeMaterial,
	 * java.util.Set, java.util.Set)
	 */
	protected void mutate( Configuration jgapConfig, ChromosomeMaterial target, Set allelesToAdd, Set allelesToRemove ) {
		if ( ( jgapConfig instanceof NeatConfiguration ) == false )
			throw new AnjiRequiredException( "com.anji.neat.NeatConfiguration" );
		NeatConfiguration config = (NeatConfiguration) jgapConfig;

		Map<Long, NeuronAllele> neurons = NeatChromosomeUtility.getNeuronMap( target.getAlleles() );
		Map<Long, ConnectionAllele> connections = NeatChromosomeUtility.getConnectionMap( target.getAlleles() );

		// neuron can be mutated on any connection
		List<NeuronAllele> neuronList = NeatChromosomeUtility.getNeuronList(target.getAlleles(), NeuronType.HIDDEN);
		Collections.shuffle( neuronList, config.getRandomGenerator() );

		int numActivationMutations = numMutations( config.getRandomGenerator(), neuronList.size() );
		int count = 0;
		for (NeuronAllele oldNeuronAllele : neuronList) {
			if ( ++count > numActivationMutations ) break;
			changeNeuronActivationFunction( config, neurons, connections, oldNeuronAllele, allelesToAdd, allelesToRemove );
		}
	}

}

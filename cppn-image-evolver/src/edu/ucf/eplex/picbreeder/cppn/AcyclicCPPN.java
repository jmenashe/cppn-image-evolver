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

package edu.ucf.eplex.picbreeder.cppn;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import org.jgap.Chromosome;

/**
 * An acyclic CPPN can be constructed and activated in linear time
 * with respect to edges.  This is considerably faster than a
 * "normal" (recursive) CPPN.
 * <p>
 * During construction, the connections are sorted according to
 * a topological sort.  If the topological sort succeeds, the
 * network is instantiated.  Otherwise, the constructor will
 * throw an exception, indicating that the genome has recursive
 * links.
 * 
 * @author Nick Beato
 *
 */
final class AcyclicCPPN extends AbstractNetwork {
	
	AcyclicCPPN(Chromosome genome, NetworkListener observer) throws CreationFailedException {
		super(genome, observer);
		topologicalSort();
	}
	
	public void activate() {
		// reset the input nodes to inactive
		for(Neuron n : neurons.values())
			((DefaultNeuron) n).setActive(false);
		
		// run activation
		// use the active flag to determine whether the
		// activation function already fired
		for(Connection c : connections) {
			Neuron src = c.getSource();
			
			if(!src.isActive()) {
				((DefaultNeuron)src).setActive(true);
				src.activate();
			}
			
			c.transmit();
		}
		
		// because only source nodes were activated,
		// the output neurons are not ready.
		// activate the output layer
		for(Neuron n : neurons.values())
			if(!n.isActive()) {
				((DefaultNeuron)n).setActive(true);
				n.activate();
			}
	}
	
	public void clearNetwork() {
		for(Neuron n : neurons.values())
			n.clearInput();
	}

	/**
	 * Sorts the connections in a topological order so that activation
	 * can simply iterate over the edges, dropping the activation time
	 * to linear in the number of edges.
	 * <p>
	 * This algorithm runs in O(N^2) where N is the number of neurons.
	 */
	private void topologicalSort() throws CreationFailedException {
		// this algorithm will reference nodes as indexes into parallel arrays
		
		// stores the incoming degree to a node
		int [] inDegrees = new int[neurons.size()];
		
		// stores where we can get to from a neuron
		// java note: to create an array of a generic type, you have to drop
		// the generic syntax. type-checking still occurs everywhere else
		List<List<Connection>> neighbors = new ArrayList<List<Connection>>();
		for(int i = 0; i < neurons.size(); i++)
			neighbors.add(i, new ArrayList<Connection>());
		
		// maps a neuron to it's index in the arrays
		Map <Neuron, Integer> map = new HashMap<Neuron, Integer>();
		
		// create the said mapping
		int i = 0;
		for(Neuron n : neurons.values())
			map.put(n, i++);

		// calculate the incoming degree to each neuron
		// and also build the neighborhood
		Arrays.fill(inDegrees, 0);
		for(Connection c : connections) {
			int dest = map.get(c.getDestination());
			int src = map.get(c.getSource());
			
			// self loop
			if(dest == src)
				throw new CreationFailedException();
			
			neighbors.get(src).add(c);
			inDegrees[dest]++;
		}
		
		// use a queue to process the the nuerons with 0
		// incoming degree
		Queue<Integer> q = new LinkedList<Integer>();
		
		// find the input layer (initial nodes)
		for(i = 0; i < neurons.size(); i++)
			if(inDegrees[i] == 0)
				q.offer(i);
		
		// the output (topologically sorted edges)
		Connection []sorted = new Connection[connections.size()];

		i = 0;
		// found indicates how many nodes were reached.
		// this will equal the number of neurons on success
		int found = q.size();
		while(q.size() > 0) {
			// add all edges coming from the next neuron
			// in order
			for(Connection c : neighbors.get(q.poll())) {
				sorted[i++] = c;
				int dest = map.get(c.getDestination());
				
				// decrement the incoming degree of the target
				if(--inDegrees[dest] == 0) {
					found++;
					q.offer(dest);
				}
			}
		}

		// it's not a DAG, return the number of neurons
		if(found != neurons.size())
			throw new CreationFailedException();
		
		connections.clear();
		for (int idx = 0; idx < sorted.length; idx++) {
			connections.add(idx, sorted[idx]);
		}
	}
}

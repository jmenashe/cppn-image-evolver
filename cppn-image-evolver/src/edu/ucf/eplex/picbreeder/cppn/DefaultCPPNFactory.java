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

import java.util.*;

import org.jgap.Chromosome;

import com.anji.integration.Activator;
import com.anji.neat.ConnectionAllele;
import com.anji.neat.NeuronAllele;

/**
 * Creates all objects in the CPPN package. This is used so that
 * the implmentation may change without rewriting any code in the package.
 * 
 */
public class DefaultCPPNFactory implements CPPNFactory {
	private String connectionInfo(ConnectionAllele c) {
		return "id=\"" +c.getInnovationId()+ "\" src-id=\"" +c.getSrcNeuronId()+ "\" dest-id=\"" +c.getDestNeuronId()+ "\" weight=" +c.getWeight();
	} 

	public Connection createConnection(Neuron from, Neuron to, ConnectionAllele link) {
//		System.out.println("Adding Connection: " + connectionInfo(link) + " [ i.e. " +from.toString()+ "-->" +to.toString()+ " ]");
		return new DefaultConnection(from, to, link);
	}
	
	public InputLayer createInputLayer(Collection<Neuron> neurons) {		
		return new DefaultInputLayer(neurons);
	}
	
	/**
	 * This function is not yet implemented and should not be called.  Eventually, we may
	 * add the ability to create a default, simple network.
	 * 
	 * @return Network New network.
	 */
	public Activator createNetwork() {		
		return null;
	} 
	
	public Network createNetwork(Chromosome genome) {
		return createNetwork(genome, null);
	} 

	public Network createNetwork(Chromosome genome, NetworkListener observer) {
		try {
//			System.out.println("Trying to use an AcyclicCPPN network");
			return new AcyclicCPPN(genome, observer);
		}
		catch(CreationFailedException e) {
//			System.out.println("Caught CreationFailedException:  Using a DefaultCPPN network");
			return new DefaultNetwork(genome, observer);
		}
	} 

	public Neuron createNeuron(NeuronAllele node, NetworkListener observer) {	
//		System.out.println("Adding Neuron: " + nodeInfo(node));
		return new DefaultNeuron(node, observer);
	}
	
	public OutputLayer createOutputLayer(Collection<Neuron> neurons, int index) {
		switch(index) {
			case 0: return new GreyOutputLayer(neurons);
			case 1:	return new HSBOutputLayer(neurons);
			default: return new DefaultOutputLayer(neurons);
		}
	}
	
	private String nodeInfo(NeuronAllele n) {
		return "id=\"" +n.getInnovationId()+ "\" type=\"" +n.getType().toString()+ "\" activation=\"" + n.getActivationType().toString()+ "\"";
	}
}


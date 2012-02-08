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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jgap.Chromosome;

import com.anji.neat.ConnectionAllele;
import com.anji.neat.NeuronAllele;
import com.anji.neat.NeuronType;

abstract class AbstractNetwork implements Network {
	/**
	 * List of neurons for this network. Used in the activation functions.
	 *
	 */
	protected final List<Connection> connections;
	
	/**
	 * Represents the genome that created this network.
	 */
	private final Chromosome genome;
	
	/**
	 * Represents the input layer for this network.
	 *
	 */
	private final InputLayer inputLayer;
	
	/**
	 * List of neurons for this network. Used in the activation functions.
	 *
	 */
	protected final Map<Long, Neuron> neurons;
	
	/**
	 * Represents the output layer of the network.
	 *
	 */
	private final OutputLayer[] outputLayers;
	
	protected AbstractNetwork(Chromosome genome, NetworkListener listner) {
		this.genome = genome;
		
		neurons = new HashMap<Long, Neuron>();
		connections = new ArrayList<Connection>();

		for(NeuronAllele node : genome.getNodes()) {
			Neuron n = CPPNFactoryInstance.get().createNeuron(node, listner);
			neurons.put(node.getInnovationId(), n);
		}
		
		Neuron source, destination;
		for(ConnectionAllele link: genome.getConnections()) {
			source = neurons.get(link.getSrcNeuronId());
			destination = neurons.get(link.getDestNeuronId());
			connections.add(CPPNFactoryInstance.get().createConnection(source, destination, link));
		}
		
		inputLayer = CPPNFactoryInstance.get().createInputLayer(neurons.values());
		
		outputLayers = new OutputLayer[2];
		outputLayers[0] = CPPNFactoryInstance.get().createOutputLayer(neurons.values(), 0);
		outputLayers[1] = CPPNFactoryInstance.get().createOutputLayer(neurons.values(), 1);
	}
	
	protected abstract void activate();
	
	protected abstract void clearNetwork();
	
	public double []evaluate(double x, double y) {
		clearNetwork();
		inputLayer.writeInput(x, y);
		activate();
		return outputLayers[1].readOutput();
	}
	
	public void evaluateAt(double x, double y) {
		clearNetwork();
		inputLayer.writeInput(x, y);
		activate();
	}
	
	public final Chromosome getGenome() {
		return genome;
	}
	
	public void notifyNetworkListners() {
		for (Neuron n : neurons.values())
			n.notifyNetworkListeners();
	}
	public double []readOutput(int i) {
		return outputLayers[i].readOutput();
	}
}

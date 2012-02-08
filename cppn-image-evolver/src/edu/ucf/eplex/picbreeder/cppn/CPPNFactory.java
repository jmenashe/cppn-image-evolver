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

import com.anji.neat.ConnectionAllele;
import com.anji.neat.NeuronAllele;

/**
 * The CPPNFactory is responsible for creating all cppn objects. This
 * hides the CPPN implementation from the GUI.
 * 
 * @author Adam Campbell
 * @author Nick Beato
 */
public interface CPPNFactory {
	/**
	 * Creates a new connection.
	 * 
	 * @param source The source neuron.
	 * @param destination The destination neuron.
	 * @param link Contains the weight for this connection.
	 * @return New connection.
	 */
	public Connection createConnection(Neuron source, Neuron destination, ConnectionAllele link);

	/**
	 * Creates new input layer with the given collection of neurons.
	 * 
	 * @param neurons List of neurons from which the input neuron layer will be created.
	 * @return New input layer.
	 */
	public InputLayer createInputLayer(Collection <Neuron> neurons);

	/**
	 * Creates a default network with the given genome.
	 * 
	 * @param genome Genome from which the network should be created.
	 * @return New network.
	 */
	public Network createNetwork(Chromosome genome);

	public Network createNetwork(Chromosome genome, NetworkListener observer);

	/**
	 * Creates a default neuron out of the given node.
	 * 
	 * @param node Node that the neuron is created out of.
	 * @return New neuron.
	 */
	public Neuron createNeuron(NeuronAllele node, NetworkListener observer);

	/**
	 * <p>Creates new output layer with the given collection of neurons.</p>
	 * 
	 * 
	 * @param neurons List of neurons from which the output neuron layer will be created.
	 * @param index The index of the layer to create
	 * @return New output layer.
	 */
	public OutputLayer createOutputLayer(Collection <Neuron> neurons, int index);
 }

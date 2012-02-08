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

import java.util.Collection;
import java.util.HashSet;

import com.anji.activationFunction.ActivationFunctionStrategy;
import com.anji.neat.NeuronAllele;

/**
 * 
 * @author Adam Campbell
 */
final class DefaultNeuron implements Neuron {
	/**
	 * Shows whether or not the neuron is active.  If a neuron is active,
	 * then its  output value can be read by the connections using it as
	 * their source.
	 *
	 */
	private boolean active;
	
	/**
	 * The node bias.
	 */
	private double bias;
	
	/**
	 * Represents the activation function for this neuron.
	 *
	 */
	final private ActivationFunctionStrategy function;
	
	/**
	 * Represents the input values obtained from its incoming connections.
	 *
	 */
	private double input;
	
	/**
	 * Represents the node that this neuron was created from.
	 *
	 */
	final private NeuronAllele node;
	
	private Collection<NetworkListener> observers = new HashSet<NetworkListener>();
	
	/**
	 * Represents the value output from this neuron.
	 *
	 */
	private double output;
	
	/**
	 * Used to keep values between activations.
	 *
	 */
	private double resetValue;
	
	/**
	 * Constructs a neuron class given its corresponding evolutionary neuron.
	 * This neuron obtains its activation function from the evolutionary neuron.
	 *
	 * @param neuron The evolutionary neuron from which this neuron obtains its
	 * 	activation function.
	 */
	public DefaultNeuron(NeuronAllele n, NetworkListener observer){
		node = n;
		function = n.getActivationType();
		bias = resetValue = 0.0;
		active = false;
		if (observer != null) observers.add(observer);
	}
	
	public void activate(){
		if(active){
			output = function.valueAt(input);
			input = resetValue;
		}
	}
	
	public void addInput(double x){
		input += x;
	}
	
	public void clearInput(){
		input = resetValue = bias;
		active = false;
	}
	
	public NeuronAllele getNode(){
		return node;
	}
	
	public double getOutput(){
		return output;
	}
	
	public boolean isActive(){
		return active;
	}

	public void notifyNetworkListeners() {
		for (NetworkListener o : observers)
			o.update(node, output);
	}
	
	public void setActive(boolean b){
		active = b;
	}
	
	public void setInput(double x){
		input = resetValue = x;
		//Set this neuron to be active because its input value was explicitly set.
		//A neuron's input is explicitly set only if it is an input neuron, and input neurons
		//are always active.
		active = true;
	}
	
	public String toString() {
		return node.getInnovationId().toString();
	}
}



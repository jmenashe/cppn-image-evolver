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

import com.anji.neat.NeuronType;

import edu.ucf.eplex.picbreeder.ParameterTableInstance;

/**
 * 
 * @author Adam Campbell
 */
final class DefaultInputLayer implements InputLayer {
	/**
	 * Returns the distance of the given (x,y) pair to the origin.
	 *
	 * @param x Value in the x direction.
	 * @param y Value in the y direction.
	 * @return Distance of the x,y pair to the origin.
	 */
	private static double distance(double x, double y){
		return Math.sqrt(x*x + y*y);
	}
	private static DefaultNeuron find(Collection<Neuron> neurons, long id){
		for(Neuron neuron: neurons){
			if(neuron.getNode().getType().equals(NeuronType.INPUT) && neuron.getNode().getInnovationId() == id) {
				return (DefaultNeuron)neuron;
			}
		}
		return null;
	}
	/**
	 * Returns an input neuron with a specific label.  If no neuron in the
	 * collection matches the criteria, then null is returned.
	 *
	 * @param neurons Collection of neurons to be searched.
	 * @param label Label of the sought after neuron.
	 * @return Input neuron with given label.  If none is found, then null is returned.
	 */
	private static DefaultNeuron find(Collection<Neuron> neurons, String label){
		for(Neuron neuron: neurons){
			if(neuron.getNode().getType().equals(NeuronType.INPUT) && neuron.getNode().getLabel().equals(label)) {
				return (DefaultNeuron)neuron;
			}
		}
		return null;
	}
	private final double BIAS;
	
	/**
	 * Represents the neuron containing the bias value.
	 *
	 */
	private DefaultNeuron biasInput;
	private final double DISTANCE_SCALE;
	/**
	 * Represents the neuron containing the distance from origin input value.
	 *
	 */
	private DefaultNeuron distInput;

	private final double X_SCALE;
	
	/**
	 * Represents the neuron containing the x input value.
	 *
	 */
	private DefaultNeuron xInput;
	
	private final double Y_SCALE;
	
	/**
	 * Represents the neuron containing the y input value.
	 *
	 */
	private DefaultNeuron yInput;
	
	/**
	 * Constructs an input layer out of the list of neurons given.
	 * The inputs are x, y, and distance from the origin.
	 *
	 * @param neurons List of neurons from which this input layer
	 * 	will obtain the correct input nodes.
	 */
	public DefaultInputLayer(Collection <Neuron> neurons){
		// statics cause problems in applet :(
		BIAS = ParameterTableInstance.get().getDouble("activation", "bias");
		X_SCALE = ParameterTableInstance.get().getDouble("activation", "x scale");
		Y_SCALE = ParameterTableInstance.get().getDouble("activation", "y scale");
		DISTANCE_SCALE = ParameterTableInstance.get().getDouble("activation", "distance scale");
		
		xInput = find(neurons, "x");
		yInput = find(neurons, "y");
		distInput = find(neurons, "d");
		biasInput = find(neurons, "bias");
		
		if (xInput == null) xInput = find(neurons, 2);
		if (yInput == null) yInput = find(neurons, 3);
		if (distInput == null) distInput = find(neurons, 1);
		if (biasInput == null) biasInput = find(neurons, 0);
	}
	
	public void writeInput(double x, double y){
		x *= X_SCALE;
		y *= Y_SCALE;
		
		xInput.setInput(x);
		yInput.setInput(y);
		distInput.setInput(distance(x, y) * DISTANCE_SCALE);
		biasInput.setInput(BIAS);
	}
	
}



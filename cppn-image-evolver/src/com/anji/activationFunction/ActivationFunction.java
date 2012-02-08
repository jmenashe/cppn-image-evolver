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
 * created by Brian Woolley on Jul 6, 2010
 */
package com.anji.activationFunction;

import java.util.ArrayList;
import java.util.List;

/**
 * Enumerated type allowing functional access to the available flavors of input, output, and hidden neurons.
 * @author Brian Woolley on Jul 6, 2010
 *
 */
public enum ActivationFunction implements ActivationFunctionStrategy {
	ABS_INVERSE ( "inverseAbs", new InverseAbs() ),
	CLAMPED_LINEAR_SIGNED ( "signed-clamped-linear", new ClampedLinearSigned() ),
	CLAMPED_LINEAR_UNSIGNED( "clamped-linear", new ClampedLinearUnsigned() ),
	COSINE ( "cos(x)", new Cosine() ),
	COSINE_SIGNED ( "signed.cosine", new Cosine() ),
COSINE_UNSIGNED ( "unsigned.cosine", new CosineUnsigned() ),

	EV_SAIL_SIGMOID ("evSailSigmoid", new EvSailSigmoid() ),
	
	GAUSSIAN ( "gaussian(x)", createBipolarFunction(new Gaussian()) ),
	GAUSSIAN_SIGNED ( "signed.gaussian", createBipolarFunction(new Gaussian()) ),
	GAUSSIAN_UNSIGNED ( "unsigned.gaussian", new Gaussian() ),

	IDENTITY ( "identity(x)", new Identity()),
	//	
	LINEAR ( "linear", new Identity()),

	SIGMOID ( "sigmoid", createBipolarFunction(new Sigmoid()) ),
	SIGMOID_X ( "sigmoid(x)", createBipolarFunction(new Sigmoid()) ),
	SIGMOID_SIGNED ( "signed.sigmoid", createBipolarFunction(new Sigmoid()) ),

	SIGMOID_UNSIGNED ( "unsigned.sigmoid", new Sigmoid()),
	SINE ( "sin(x)", new Sine() ),
	SINE_SIGNED ( "signed.sinusoid", new Sine() ),
	SINE_UNSIGNED ( "unsigned.sinusoid", new SineUnsigned() ),
	STEP_SIGNED ( "step.signed", new StepSigned() ),
	STEP_UNSIGNED ( "step", new StepUnsigned() ),
	TAN_H ( "tanh", new Tanh() ),
	TAN_H_CUBIC ( "tanh-cubic", new TanhCubic() );
	
	private static List<ActivationFunction> cppnActivations = null;
	private static ActivationFunctionStrategy createBipolarFunction(ActivationFunctionStrategy function) {
		return new UnipolarToBipolar(function);
	}
	
	public static ActivationFunction get(String aFuncName) {
		if (aFuncName.equalsIgnoreCase(IDENTITY.toString()))			return IDENTITY;
		if (aFuncName.equalsIgnoreCase(LINEAR.toString()))				return LINEAR;

		if (aFuncName.equalsIgnoreCase(SIGMOID.toString()))				return SIGMOID;
		if (aFuncName.equalsIgnoreCase(SIGMOID_X.toString()))			return SIGMOID_X;
		if (aFuncName.equalsIgnoreCase(SIGMOID_SIGNED.toString())) 		return SIGMOID_SIGNED;
		if (aFuncName.equalsIgnoreCase(SIGMOID_UNSIGNED.toString()))	return SIGMOID_UNSIGNED;
		
		if (aFuncName.equalsIgnoreCase(GAUSSIAN.toString()))			return GAUSSIAN;
		if (aFuncName.equalsIgnoreCase(GAUSSIAN_SIGNED.toString()))		return GAUSSIAN_SIGNED;
		if (aFuncName.equalsIgnoreCase(GAUSSIAN_UNSIGNED.toString()))	return GAUSSIAN_UNSIGNED;

		if (aFuncName.equalsIgnoreCase(SINE.toString())) 				return SINE;
		if (aFuncName.equalsIgnoreCase(SINE_SIGNED.toString())) 		return SINE_SIGNED;
		if (aFuncName.equalsIgnoreCase(SINE_UNSIGNED.toString())) 		return SINE_UNSIGNED;
		
		if (aFuncName.equalsIgnoreCase(COSINE.toString()))				return COSINE;
		if (aFuncName.equalsIgnoreCase(COSINE_SIGNED.toString()))		return COSINE_SIGNED;
		if (aFuncName.equalsIgnoreCase(COSINE_UNSIGNED.toString()))		return COSINE_UNSIGNED;
		
//		if (aFuncName.equalsIgnoreCase(SINE_UNSIGNED.toString())) 			return SINE_UNSIGNED;
//		if (aFuncName.equalsIgnoreCase(COSINE_UNSIGNED.toString())) 		return COSINE_UNSIGNED;
//		if (aFuncName.equalsIgnoreCase(TAN_H.toString())) 					return TAN_H;
//		if (aFuncName.equalsIgnoreCase(TAN_H_CUBIC.toString())) 			return TAN_H_CUBIC;
//		if (aFuncName.equalsIgnoreCase(ABS_INVERSE.toString())) 			return ABS_INVERSE;
//		if (aFuncName.equalsIgnoreCase(STEP_UNSIGNED.toString())) 			return STEP_UNSIGNED;
//		if (aFuncName.equalsIgnoreCase(STEP_SIGNED.toString())) 			return STEP_SIGNED;
//		if (aFuncName.equalsIgnoreCase(CLAMPED_LINEAR_UNSIGNED.toString()))	return CLAMPED_LINEAR_UNSIGNED;
//		if (aFuncName.equalsIgnoreCase(CLAMPED_LINEAR_SIGNED.toString()))	return CLAMPED_LINEAR_SIGNED;
//		if (aFuncName.equalsIgnoreCase(EV_SAIL_SIGMOID.toString()))			return EV_SAIL_SIGMOID;
		throw new IllegalArgumentException(aFuncName + " is not a registered activation type.");
	}
	
	public static ActivationFunction getRandomActivationType() {
		if (cppnActivations == null) {
			System.out.println("initializing CPPN function array");
			cppnActivations = new ArrayList<ActivationFunction>();
			cppnActivations.add(ActivationFunction.GAUSSIAN_SIGNED);
			cppnActivations.add(ActivationFunction.LINEAR);
			cppnActivations.add(ActivationFunction.SINE_SIGNED);
			//cppnActivations.add(ActivationFunction.COSINE);
			cppnActivations.add(ActivationFunction.SIGMOID_SIGNED);		
		}
		return cppnActivations.get( (int) (Math.random() * cppnActivations.size()));
	}
		
	private final ActivationFunctionStrategy function;

	private final String name;

	ActivationFunction(String name, ActivationFunctionStrategy function) {
		this.name = name;
		this.function = function;		
	}

	/**
	 * @see com.anji.activationFunction.ActivationFunctionStrategy#cost()
	 * @return number corresponding to cost of activation in resources
	 */
	public long cost() {
		return function.cost();
	}

	/**
	 * @see com.anji.activationFunction.ActivationFunctionStrategy#getMaxValue()
	 * @return ceiling value for this function
	 */
	public double getMaxValue() {
		return function.getMaxValue();
	}

	/**
	 * @see com.anji.activationFunction.ActivationFunctionStrategy#getMinValue()
	 * @return floor value for this function
	 */
	public double getMinValue() {
		return function.getMinValue();
	}

	/**
	 * @see Object#toString()
	 */
	public String toString() {
		return name;
	}
	/**
	 * Apply activation function to input.
	 * @see com.anji.activationFunction.ActivationFunctionStrategy#valueAt(double)
	 * @param input
	 * @return double result of applying activation function to <code>input</code>
	 */
	public double valueAt( double input ) {
		return function.valueAt(input);
	}

}

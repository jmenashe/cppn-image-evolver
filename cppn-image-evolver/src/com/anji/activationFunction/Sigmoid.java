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
 * Created on Feb 26, 2004 by Philip Tucker
 */
package com.anji.activationFunction;


/**
 * Modified classic sigmoid. Copied from <a href="http://www.jooneworld.com/">JOONE</a>
 * <code>SigmoidLayer</code>.
 * 
 * @author Philip Tucker
 */
public class Sigmoid implements ActivationFunctionStrategy {

	/**
	 * This class should only be instantiated by the ActivationFunction enumeration.
	 * @see com.anji.activationFunction.ActivationFunction
	 */
	protected Sigmoid() {}

	/**
	 * @see com.anji.activationFunction.ActivationFunctionStrategy#cost()
	 */
	public long cost() {
		return 497;
	}

	/**
	 * @see com.anji.activationFunction.ActivationFunctionStrategy#getMaxValue()
	 */
	public double getMaxValue() {
		return 1;
	}
	
	/**
	 * @see com.anji.activationFunction.ActivationFunctionStrategy#getMinValue()
	 */
	public double getMinValue() {
		return 0;
	}

	/**
	 * Modified classic sigmoid.
	 * 
	 * @see com.anji.activationFunction.ActivationFunctionStrategy#valueAt(double)
	 */
	public double valueAt( double input ) {
		return 1.0/(1.0+Math.exp(-input));
	}
}

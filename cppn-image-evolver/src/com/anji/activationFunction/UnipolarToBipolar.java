/**
 * Copyright (C) 2011 Brian Woolley and Kenneth Stanley
 * 
 * This file is part of the octopusArm simulator.
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
 * Created on 11 February 2011 by Brian Woolley
 */
package com.anji.activationFunction;

/**
 * This is a utility class to convert a unipolar function
 * to a bipolar. It does not gaurantee that the input
 * function is unipolar, that is the programmer's responsibility.
 * 
 * @author Nick
 */

public final class UnipolarToBipolar implements ActivationFunctionStrategy {
	private final ActivationFunctionStrategy function;
	
	/**
	 * Constructs a bipolar functions who's output is dependant
	 * on the given unipolar function.
	 * 
	 * @param function The unipolar function
	 */
	public UnipolarToBipolar(ActivationFunctionStrategy function) {
		this.function = function;
	}
	
	public long cost() {
		// TODO Auto-generated method stub
		return 0;
	}

	public double getMaxValue() {
		// TODO Auto-generated method stub
		return 0;
	}

	public double getMinValue() {
		// TODO Auto-generated method stub
		return 0;
	}

	public double valueAt(double x) {
		return function.valueAt(x) * 2.0 - 1.0;
	}
}

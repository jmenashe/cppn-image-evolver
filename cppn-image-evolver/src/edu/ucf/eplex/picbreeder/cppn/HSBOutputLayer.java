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

/**
 * An output layer that interprets HSB (hue, saturation, brightness)
 * by looking for 3 output neurons (with the same names) and converting
 * their values to rgb.
 * 
 * @author Nick
 */

final class HSBOutputLayer implements OutputLayer {
	private static double clamp(double d) {
		return Math.min(1.0, Math.max(0.0, d));
		//return Math.min(1.0, Math.abs(d));
	}
	private Neuron hue, saturation, brightness;
	
	private final double []output = new double[3];
	
	HSBOutputLayer(Collection<Neuron> neurons) {
		for(Neuron n : neurons)
			if(n.getNode().getType().equals(NeuronType.OUTPUT)) {
				if(n.getNode().getLabel().equals("hue")) hue = n;
				else if(n.getNode().getLabel().equals("saturation")) saturation = n;
				else if(n.getNode().getLabel().equals("brightness")) brightness = n;
			}
	}

	public double []readOutput() {
		set(hue.getOutput(), clamp(saturation.getOutput()), clamp(Math.abs(brightness.getOutput())));
		return output;
	}
	
	// reimplementing java.awt.Color.HSBtoRGB
	// to stay in floating point
	// you can use that function to verify this one is correct (i did!)
	private void set(double h, double s, double v) {
		h = (h * 6.0) % 6.0;
		
		double r = 0.0, g = 0.0, b = 0.0;
		
		if(h < 0.0) h += 6.0;
		int hi = (int) h;
		double f = h - hi;
		
		double vs = v * s;
		double vsf = vs * f;
		
		double p = v - vs;
		double q = v - vsf;
		double t = v - vs + vsf;
		
		switch(hi) {
			case 0: r = v; g = t; b = p; break;
			case 1: r = q; g = v; b = p; break;
			case 2: r = p; g = v; b = t; break;
			case 3: r = p; g = q; b = v; break;
			case 4: r = t; g = p; b = v; break;
			case 5: r = v; g = p; b = q; break;
		}
		
		output[0] = r;
		output[1] = g;
		output[2] = b;
	}

}

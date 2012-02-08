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
 * created by Brian Woolley on Aug 20, 2010
 */
package edu.ucf.eplex.imageEvolver.gui;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JFrame;

import org.jgap.Chromosome;
import org.jgap.Configuration;

import com.anji.neat.NeuronAllele;
import com.anji.persistence.Persistence;
import com.anji.util.ArgumentParser;
import com.anji.util.DummyConfiguration;
import com.anji.util.Properties;

import edu.ucf.eplex.picbreeder.DefaultIndividual;
import edu.ucf.eplex.picbreeder.DefaultParameters;
import edu.ucf.eplex.picbreeder.Individual;
import edu.ucf.eplex.picbreeder.ParameterTableInstance;
import edu.ucf.eplex.picbreeder.cppn.CPPNFactoryInstance;
import edu.ucf.eplex.picbreeder.cppn.Network;
import edu.ucf.eplex.picbreeder.cppn.NetworkListener;

/**
 * @author Brian Woolley on Aug 20, 2010
 *
 */
public class NetworkViewer extends JFrame implements NetworkListener {
	
	private class ImageBuilder {
		private int[] imageArray = new int[height * width];
		
		private Image getImage() {
	        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
	        image.setRGB(0,	0, width, height, imageArray, 0, width);
	        return image;
		}

		private void writeOutput(double value) {
			int ink = (int) Math.round( 255 * Math.abs(value) );
			ink = Math.max(Math.min(ink, 255), 0);

			if (value >= 0)
				imageArray[(y*width)+x] = (int) (0xFF << 24) | ((ink & 0xFF) << 16) | ((ink & 0xFF) << 8) | (ink & 0xFF);
			else
				imageArray[(y*width)+x] = (int) (0xFF << 24) | ((ink & 0xFF) << 16);
		}
	}
	private static final long serialVersionUID = 7845307708329517238L;
	
	private static boolean verbose = false;
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		if(args.length == 0) {
			System.out.println("Usage: java exp.imageEvolver.Start [options]");
			System.out.println("Options:");
			System.out.println("    -t subjectGenomeFile");
			System.out.println("    -p propertiesFile");
			System.out.println("    -v verbose");
			return;
		}

		ArgumentParser parser = new ArgumentParser(args);
		ParameterTableInstance.set(new DefaultParameters());
		
		String subjectGenomeFile = parser.findArgument("-t");
		String propFile = parser.findArgument("-p");
		if (parser.hasOption("-v")) verbose = true;
		
		Properties props = new Properties();
		try {
			props.loadFromResource( propFile );
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(-1);
		}

		// load chomosomes from XML
		Persistence db = (Persistence) props.newObjectProperty( Persistence.PERSISTENCE_CLASS_KEY );
		Configuration config = new DummyConfiguration();

		Chromosome chrom = db.loadChromosome( subjectGenomeFile, config );
		if ( chrom == null ) {
			throw new IllegalArgumentException( "no chromosome found: " + subjectGenomeFile );
		}

		new NetworkViewer(new DefaultIndividual(chrom));
	}
	private int height = ParameterTableInstance.get().getInteger("display", "height");
	private Map<Long, ImageBuilder> nodePanels = new HashMap<Long, ImageBuilder>();
	
	private int width = ParameterTableInstance.get().getInteger("display", "width");
	
	private int x = 0, y = 0;

	public NetworkViewer(Individual individual) {
		// The lifecycle of this object:
		if (verbose) System.out.print("Creating a network observed by this...");
		Network net = CPPNFactoryInstance.get().createNetwork(individual.getGenome(), this);
		if (verbose) System.out.println("done.");

		if (verbose) System.out.println("Querying the network for each (x, y) position in the image...");
		double fx, fy;
		
		for(y = 0; y < height; y++) {
			fy = individual.getPhenotype(0).computeInputY(y);
			
			for(x = 0; x < width; x++) {
				fx = individual.getPhenotype(0).computeInputX(x);
				net.evaluateAt(fx, fy);
				net.notifyNetworkListners();
			}
		}
		if (verbose) System.out.println("done.");
		
		if (verbose) System.out.println("Launching separate NodeViewer windows for each node in the network...");
		for (Long id : nodePanels.keySet()) {
			if (verbose) System.out.println("--Node ID: " +id);
			new NodeViewer(id.toString(), nodePanels.get(id).getImage());
		}
		if (verbose) System.out.println("done.");
	}
	/**
	 * @see edu.ucf.eplex.picbreeder.cppn.NetworkListener#update(com.anji.neat.NeuronAllele, double)
	 */
	public void update(NeuronAllele node, double value) {
		if (!nodePanels.containsKey(node.getInnovationId())) {
			nodePanels.put(node.getInnovationId(), new ImageBuilder());
			if (verbose) System.out.println("Created ImageBuilder for nodeID=" +node.getInnovationId());
		}
		nodePanels.get(node.getInnovationId()).writeOutput(value);
	}

}

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
 * created by Brian Woolley on Jul 9, 2010
 */
package edu.ucf.eplex.imageEvolver.util;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import org.jgap.Chromosome;
import org.jgap.Configuration;

import com.anji.Copyright;
import com.anji.persistence.Persistence;
import com.anji.util.DummyConfiguration;
import com.anji.util.Properties;

import edu.ucf.eplex.imageEvolver.ImageEvolver;
import edu.ucf.eplex.imageEvolver.gui.EvolutionViewer;

/**
 * @author Brian Woolley on Jul 9, 2010
 *
 */
public class ImageCompare {

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {

		System.out.println( Copyright.STRING );

		if ( args.length < 2 ) {
			System.err.println( "usage: <cmd> <properties-file> <chromosome-subject-ID>" );
			System.exit( -1 );
		}

		// load fitness function from properties
		Properties props = new Properties();
		props.loadFromResource( args[ 0 ] );

		ImageEvolver fitnessFunc = new ImageEvolver();
		fitnessFunc.init(props);

		// load chomosomes from XML
		Persistence db = (Persistence) props.newObjectProperty( Persistence.PERSISTENCE_CLASS_KEY );
		Configuration config = new DummyConfiguration();
		ArrayList<Chromosome> chroms = new ArrayList<Chromosome>();

		for (int i=1; i<args.length; i++) {
			Chromosome chrom = db.loadChromosome( args[ i ], config );
			if ( chrom == null ) throw new IllegalArgumentException( "no chromosome found: " + args[ i ] );
			chroms.add( chrom );
		}

		// evaluate
//		fitnessFunc.evaluate( chroms );

		for (Chromosome c : chroms) {
			long start = System.currentTimeMillis();
			System.out.print("rendering targetImage" + c.getId() + "...");
			List<Image> targets = fitnessFunc.getTargetImages();			
			Image subject = fitnessFunc.loadGreyscaleImage(c);
			System.out.print("writing...");
			new EvolutionViewer("Subject " +c.getId()+ " vs. Image " +c.getId(), subject, targets);	
//			System.out.println( c.getId() + "\t" + fitnessFunc.getAvgPixelErr( c ) );
			ImageIO.write((RenderedImage) subject, "png", new File("./imageTargets/target" +c.getId()+"_"+(chroms.indexOf(c)+1)+".png"));
			long stop = System.currentTimeMillis();
			System.out.println("done in " +((stop-start)/1000)+" secs.");
		}
	}
}

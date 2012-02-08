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
 * created by Brian Woolley on Jul 7, 2010
 */
package edu.ucf.eplex.imageEvolver;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.media.jai.JAI;
import javax.media.jai.KernelJAI;
import javax.media.jai.PlanarImage;
import javax.media.jai.RenderedImageAdapter;

import org.jgap.BulkFitnessFunction;
import org.jgap.Chromosome;
import org.jgap.Configuration;

import com.anji.Copyright;
import com.anji.neat.Evolver;
import com.anji.persistence.Persistence;
import com.anji.util.ArgumentParser;
import com.anji.util.Configurable;
import com.anji.util.DummyConfiguration;
import com.anji.util.Properties;

import edu.ucf.eplex.imageEvolver.gui.EvolutionViewer;
import edu.ucf.eplex.picbreeder.DefaultIndividual;
import edu.ucf.eplex.picbreeder.DefaultParameters;
import edu.ucf.eplex.picbreeder.ImagePhenotype;
import edu.ucf.eplex.picbreeder.ParameterTableInstance;
import edu.ucf.eplex.picbreeder.renderers.RenderingAlgorithm;
import edu.ucf.eplex.picbreeder.renderers.algorithms.DefaultRenderer;

/**
 * @author Brian Woolley on Jul 7, 2010
 *
 */
public class ImageEvolver implements BulkFitnessFunction, Configurable {

	private static final boolean DEFAULT_EVALUATE_GRADIENT = true;
	
	private static final boolean DEFAULT_EVALUATE_GRAYSCALE = true;
	
	private static final double DEFAULT_GRADIENT_SENSITIVITY = 16;

	private static final double DEFAULT_GRAYSCALE_SENSITIVITY = 256;
	
	private static final String DEFAULT_TARGET_IMAGE = "94";

	private static final String EVALUATE_GRADIENT_KEY = "imageEvolver.graident.evaluate";

	private static final String EVALUATE_GRAYSCALE_KEY = "imageEvolver.grayscale.evaluate";
	
	private static final String GRADIENT_SENSITIVITY_KEY = "imageEvolver.gradient.threshold";
	
	private static final String GRAYSCALE_SENSITIVITY_KEY = "imageEvolver.grayscale.threshold";

	private static final long serialVersionUID = -3836536035901867803L;
	
	private static final String TARGET_IMAGE_KEY = "imageEvolver.target";
		
	static int width = 128;

	static int height = 128;
	
	private static boolean visable = false;

	private static BufferedImage getImageFromArray(int[] pixels, int width, int height) {
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        image.setRGB(0,	0, width, height, pixels, 0, width);
        return image;
    }
	
	public static BufferedImage loadGreyscaleImage(Chromosome ch) {
		return getImageFromArray(set24bitGreyscale(loadImageArray(ch)), width, height);
	}
	
	private static int[] loadImageArray(Chromosome ch) {
		DefaultIndividual target = new DefaultIndividual(ch);
		
		RenderingAlgorithm alg = new DefaultRenderer();
		alg.render(target);

		int[] image = ((ImagePhenotype) target.getDominantPhenotype()).getPixelArray();

		return image;
	}
	
	public static void main( String[] args ) {
		if(args.length == 0) {
			System.out.println("Usage: java edu.ucf.eplex.imageEvolver.ImageEvolver [options]");
			System.out.println("Options:");
			System.out.println("    -p propertiesFile");
			System.out.println("    -v setVisable (optional)");
			System.out.println("    -s setSaveImage (optional)");
			System.exit(-1);
		}

		System.out.println( Copyright.STRING );
		ArgumentParser parser = new ArgumentParser(args);
		
		if (parser.hasOption("-v")) visable = true;		
		String propertyFile = parser.findArgument("-p");
		
		Properties props;
		try {
			props = new Properties( propertyFile );
			Evolver evolver = new Evolver();
			evolver.init( props );
			evolver.run();			
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static int[] set24bitGreyscale(int[] image) {
		int[] out = new int[image.length];
		for (int i = 0; i < image.length; i++) {
//			if (image[i] >= 0)
				out[i] = (int) (0xFF << 24) | ((image[i] & 0xFF) << 16) | ((image[i] & 0xFF) << 8) | (image[i] & 0xFF);
//			else
//				out[i] = (int) (0xFF << 24) | ((Math.abs(image[i]) & 0xFF) << 16);
		}
		return out;
	}
	
	Chromosome champ = null;

	private Collection<EvolutionObserver> champObservers = new ArrayList<EvolutionObserver>();
	private boolean evaluateGradient = DEFAULT_EVALUATE_GRADIENT;
	
    private boolean evaluateGrayscale = DEFAULT_EVALUATE_GRAYSCALE;

	private final FuzzyHammingDist ff = new FuzzyHammingDist();
	
	private double graidentThreshold = DEFAULT_GRADIENT_SENSITIVITY;
	private double grayValue = DEFAULT_GRAYSCALE_SENSITIVITY;

	private int[] targetFeatureSet_Pixels;
	private int[] targetFeatureSet_Gradient;

	private double[] normalizedTargetFeatureSet_Pixels;
	private double[] normalizedTargetFeatureSet_Gradient;
	
	private String targetImageFile = DEFAULT_TARGET_IMAGE;

	private int[] computeFeatureSet_Gradient(int[] imageFeatures) {
		return computeGradientFeatures(getImageFromArray(set24bitGreyscale(imageFeatures), width, height));
	}
	
	private int[] computeGradientFeatures(BufferedImage anImage) {
		return set8bitGrayscale(computeGradientImage(anImage).getRGB(0, 0, anImage.getWidth(), anImage.getHeight(), null, 0, anImage.getWidth()));
	}

	private BufferedImage computeGradientImage(BufferedImage anImage) {
		PlanarImage img = new RenderedImageAdapter(anImage);
		
		// Create the kernel filter
	     float data_h[] = new float[] { 1.0F,   0.0F,   -1.0F,
	    		 						1.414F, 0.0F,   -1.414F,
	    		 						1.0F,   0.0F,   -1.0F};
	     float data_v[] = new float[] {-1.0F,  -1.414F, -1.0F,
                 						0.0F,   0.0F,    0.0F,
                 						1.0F,   1.414F,  1.0F};

	     KernelJAI kern_h = new KernelJAI(3,3,data_h);
	     KernelJAI kern_v = new KernelJAI(3,3,data_v);
	     
	     // Create the Gradient operation.
	     PlanarImage gradient = (PlanarImage)JAI.create("gradientmagnitude", img, kern_h, kern_v);
	     
	     return gradient.getAsBufferedImage();
	}

	public void evaluate(Chromosome subject) {
		int [] subjectPixelSet;
		double [] normalizedSubjectPixelSet;
		double [] normalizedSubjectGradientSet;

		int fitness, count;
		fitness = 0;
		count = 0;
		subjectPixelSet = loadImageArray(subject);
		normalizedSubjectPixelSet = normalize(subjectPixelSet, grayValue);
		
		if (evaluateGrayscale) {
			fitness += ff.evaluate(normalizedSubjectPixelSet, normalizedTargetFeatureSet_Pixels);
			count++;
		}
		if (evaluateGradient) {
			normalizedSubjectGradientSet = normalize(computeFeatureSet_Gradient(subjectPixelSet), graidentThreshold);
			fitness += ff.evaluate(normalizedSubjectGradientSet, normalizedTargetFeatureSet_Gradient);
			count++;
		}
		subject.setFitnessValue(fitness/count);
	}
	/**
	 * @see org.jgap.BulkFitnessFunction#evaluate(java.util.List)
	 */
	public void evaluate(List<Chromosome> subjects) {
		for (Chromosome c : subjects) {
			evaluate(c);
			if (champ == null) champ = c;
			if (c.getFitnessValue() > champ.getFitnessValue()) {
				champ = c;
			}
		}
		notifyEvolutionObservers();
	}

	public BufferedImage getChampImage() {
		if (champ != null) return loadGreyscaleImage(champ);
		else return null;
	}
	
	/**
	 * @see org.jgap.BulkFitnessFunction#getMaxFitnessValue()
	 */
	public int getMaxFitnessValue() {
		return ff.getMaxFitness();
	}
	
	public BufferedImage getTargetImage() {
		return getImageFromArray(set24bitGreyscale(targetFeatureSet_Pixels), width, height);
	}
	/**
	 * @see com.anji.util.Configurable#init(com.anji.util.Properties)
	 */
	public void init(Properties props) throws Exception {
		ParameterTableInstance.set(new DefaultParameters());
		height = ParameterTableInstance.get().getInteger("display", "height");
		width = ParameterTableInstance.get().getInteger("display", "width");

		// Load target chromosome from XML
		targetImageFile = props.getProperty( TARGET_IMAGE_KEY, DEFAULT_TARGET_IMAGE );	
		Persistence db = (Persistence) props.newObjectProperty( Persistence.PERSISTENCE_CLASS_KEY );
		Configuration config = new DummyConfiguration();
		Chromosome targetChromosome = db.loadTargetChromosome( targetImageFile, config );
		if ( targetChromosome == null ) throw new IllegalArgumentException( "no chromosome found: " + targetImageFile );

		grayValue = props.getDoubleProperty( GRAYSCALE_SENSITIVITY_KEY, DEFAULT_GRAYSCALE_SENSITIVITY );
		graidentThreshold = props.getDoubleProperty( GRADIENT_SENSITIVITY_KEY, DEFAULT_GRADIENT_SENSITIVITY );
		
		evaluateGrayscale = props.getBooleanProperty( EVALUATE_GRAYSCALE_KEY, DEFAULT_EVALUATE_GRAYSCALE );
		evaluateGradient = props.getBooleanProperty( EVALUATE_GRADIENT_KEY, DEFAULT_EVALUATE_GRADIENT );

		ff.init(props);

		System.out.print("Generating target image feature set...");
		
		System.out.print("pixels...");
		targetFeatureSet_Pixels = loadImageArray(targetChromosome);
		
		System.out.print("gradients...");
		targetFeatureSet_Gradient = computeFeatureSet_Gradient(targetFeatureSet_Pixels);

		grayValue = 0;
		for (int i : targetFeatureSet_Pixels) {
			grayValue = Math.max(i, grayValue);
		}
		int min = Integer.MAX_VALUE;
		int max = Integer.MIN_VALUE;
		for (int i : targetFeatureSet_Gradient) {
			min = Math.min(i, min);
			max = Math.max(i, max);
		}
		graidentThreshold = max - min;
		
		System.out.print("normalizing(" +grayValue+ ", " +graidentThreshold+ ")...");
		normalizedTargetFeatureSet_Pixels = normalize(targetFeatureSet_Pixels, grayValue);
		normalizedTargetFeatureSet_Gradient = normalize(targetFeatureSet_Gradient, graidentThreshold);
		
		System.out.println("done.");
				
		if (visable) new EvolutionViewer(this);
	}
	
	/**
	 * Scale values down to fractions in the range [-1, 1]. The sign is preserved
	 * @param input
	 * @return
	 */
	private double[] normalize(int[] input, double threshold) {
		double[] output = new double[input.length];
		if (threshold > 0.0) {
			for (int i=0; i<input.length; i++) {
				output[i] = (double) input[i]/threshold;
			}
		}
		return output;
	}

	public void notifyEvolutionObservers() {
		for (EvolutionObserver o : champObservers)
			o.updateChampImage(getChampImage());
	}
	public void registerChampObserver(EvolutionObserver o) {
		if (!champObservers.contains(o))
			champObservers.add(o);
	}

	public void removeChampObserver(EvolutionObserver o) {
		while (champObservers.contains(o)) {
			champObservers.remove(o);
		}
	}

	private int[] set8bitGrayscale(int[] image) {
		int[] out = new int[image.length];
		for (int i=0; i<image.length; i++) {
			out[i] = image[i] & 0xFF;
		}
		return out;
	}
}

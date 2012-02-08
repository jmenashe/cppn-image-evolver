/**
 * ----------------------------------------------------------------------------| Created on Apr
 * 12, 2003
 */
package com.anji.util;

import java.util.List;
import java.util.Random;

import org.jgap.BulkFitnessFunction;
import org.jgap.Chromosome;

/**
 * @author Philip Tucker
 */
public class DummyBulkFitnessFunction implements BulkFitnessFunction {

private Random rand = null;

/**
 * ctor
 */
public DummyBulkFitnessFunction() {
	rand = new Random();
}

/**
 * ctor
 * @param newRand
 */
public DummyBulkFitnessFunction( Random newRand ) {
	rand = newRand;
}

public void evaluate( Chromosome a_subject ) {
	a_subject.setFitnessValue( rand.nextInt( 100 ) );
}

/**
 * @see org.jgap.BulkFitnessFunction#evaluate(java.util.List)
 */
public void evaluate( List<Chromosome> aSubjects ) {
	for (Chromosome c : aSubjects) {
		evaluate( c );
	}
}

/**
 * @see org.jgap.BulkFitnessFunction#getMaxFitnessValue()
 */
public int getMaxFitnessValue() {
	return 100;
}

}

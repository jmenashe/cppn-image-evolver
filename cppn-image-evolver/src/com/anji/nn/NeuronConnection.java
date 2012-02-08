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
package com.anji.nn;

import java.util.Collection;

/**
 * Connection between neurons.
 * @author Philip Tucker
 */
public class NeuronConnection implements Connection {

static final String DEST_XML_TAG = "tgt-id";

static final String RECURRENT_XML_TAG = "recurrent";

static final String SRC_XML_TAG = "src-id";

static final String WEIGHT_XML_TAG = "weight";

/**
 * Utility method to convert collection of connections, presumably an entire ANN, to XML. XML
 * representation is consistent with <a href="http://nevt.sourceforge.net/">NEVT </a>.
 * @param allNeurons
 * @param result
 */
public static void appendToXml( Collection<Neuron> allNeurons, StringBuffer result ) {
	for (Neuron neuron : allNeurons) {
		for (Connection conn : neuron.getIncomingConns()) {
			if ( conn instanceof NeuronConnection ) {
				NeuronConnection nConn = (NeuronConnection) conn;
				long srcId = nConn.getIncomingNode().getId();
				result.append( "<" ).append( XML_TAG ).append( " " );
				result.append( SRC_XML_TAG ).append( "=\"" ).append( srcId ).append( "\" " );
				result.append( DEST_XML_TAG ).append( "=\"" ).append( neuron.getId() ).append( "\" " );
				result.append( WEIGHT_XML_TAG ).append( "=\"" ).append( nConn.getWeight() ).append(
						"\" " );
				result.append( RECURRENT_XML_TAG ).append( "=\"" ).append( nConn.isRecurrent() )
						.append( "\" />\n" );
			}
		}
	}
}

private long id = hashCode();

private Neuron incomingNode = null;

private double weight = 0.0d;

/**
 * @param anIncoming
 * @see NeuronConnection#NeuronConnection(Neuron, double)
 */
public NeuronConnection( Neuron anIncoming ) {
	this( anIncoming, 0.0d );
}

/**
 * Create connection with input neuron <code>anIncoming</code> and weight <code>aWeight</code>.
 * @param anIncoming
 * @param aWeight
 */
public NeuronConnection( Neuron anIncoming, double aWeight ) {
	super();
	incomingNode = anIncoming;
	weight = aWeight;
}

/**
 * @see com.anji.nn.Connection#cost()
 */
public long cost() {
	return 57;
}

/**
 * for tracking back to chromosome innovation id
 * @return long ID
 */
protected long getId() {
	return id;
}

/**
 * @return Neuron input
 */
protected Neuron getIncomingNode() {
	return incomingNode;
}

/**
 * @return double connection weight
 */
protected double getWeight() {
	return weight;
}

/**
 * @return false
 */
public boolean isRecurrent() {
	return false;
}

/**
 * @return double
 */
public double read() {
	return weight * incomingNode.getValue();
}

/**
 * @param l new innovation ID
 */
public void setId( long l ) {
	id = l;
}

/**
 * @param f new weight
 */
public void setWeight( double f ) {
	weight = f;
}

/**
 * @see Object#toString()
 */
public String toString() {
	return "id: " + id + ", src: " + incomingNode.getId();
}

/**
 * @return String XML representation of object
 */
public String toXml() {
	StringBuffer result = new StringBuffer();
	result.append( "<" ).append( XML_TAG ).append( " id=\"" ).append( id );
	result.append( "\" " ).append( SRC_XML_TAG ).append( "=\"" ).append( incomingNode.getId() );
	result.append( "\" " ).append( WEIGHT_XML_TAG ).append( "=\"" ).append( getWeight() );
	result.append( "\" " ).append( RECURRENT_XML_TAG ).append( "=\"" ).append( isRecurrent() )
			.append( " />" );
	return result.toString();
}

}

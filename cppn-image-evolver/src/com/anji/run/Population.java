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
 * Created on Apr 4, 2004 by Philip Tucker
 */
package com.anji.run;

import java.util.List;

/**
 * @author Philip Tucker
 */
public class Population {
	
	private Domain domain;
	private String name;
	private Representation representation;
	private List<Run> runs;
	
	/**
	 * @param aName
	 * @param aDomain
	 * @param aRepresentation
	 */
	public Population( String aName, Domain aDomain, Representation aRepresentation ) {
		name = aName;
		domain = aDomain;
		representation = aRepresentation;
	}
	
	/**
	 * Add new run to population.
	 * @param aRun
	 */
	public void addRun( Run aRun ) {
		runs.add( aRun );
	}
	
	/**
	 * @return domain
	 */
	public Domain getDomain() {
		return domain;
	}
	
	/**
	 * @return unique run ID
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * @return representation
	 */
	public Representation getRepresentation() {
		return representation;
	}
	
	/**
	 * @return <code>List</code> contains <code>Run</code> objects ordered by the time at which
	 * they were executed
	 */
	public List<Run> getRuns() {
		return runs;
	}
	
	/**
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return name;
	}
}

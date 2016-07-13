/**
 * Part of the LS3 Similarity-based process model search package.
 * 
 * Licensed under the GNU General Public License v3.
 *
 * Copyright 2012 by Andreas Schoknecht <andreas_schoknecht@web.de>
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * @author Andreas Schoknecht
 */

package de.andreasschoknecht.LS3;

import java.util.ArrayList;

/**
 * A QueryResult contains a query document and a corresponding list of documents which resemble the results of executing the query.
 */
public class QueryResult {
	
	/** The query document and the the list of documents which are the result of executing the query. */
	private LS3Document query;
	private ArrayList<LS3Document> results;
	
	QueryResult(LS3Document query) {
		setQuery(query);
		results = new ArrayList<LS3Document>();
	}
	
	LS3Document getQuery() {
		return query;
	}
	
	void setQuery(LS3Document query) {
		this.query = query;
	}

	public ArrayList<LS3Document> getResults() {
		return results;
	}

	void addResult(LS3Document result) {
		results.add(result);
	}

}

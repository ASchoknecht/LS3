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
 * QueryAllResult is used for calculating the results of either a query or for querying a document collection at once. The resulst are
 * stored in a list of QueryResult objects. The result list contains only one element when calculating the results for one query model.
 * When calculating the querying results for a whole collection, the result list contains as many elements as there are document in 
 * the collection.
 */
public class QueryAllResult {
	
	/** The results of a query as a list of QueryResult objects. */
	private ArrayList<QueryResult> results;
	
	QueryAllResult() {
		results = new ArrayList<QueryResult>();
	}

	/**
	 * Adds the result of a query to the result list.
	 *
	 * @param result The result object of a query which should be added to the final result list.
	 */
	void addResult(QueryResult result) {
		results.add(result);
	}
	
	/**
	 * Gets the result list, which can contain the result of one or multiple queries.
	 *
	 * @return the results of one or multiple queries.
	 */
	public ArrayList<QueryResult> getResults() {
		return results;
	}

}

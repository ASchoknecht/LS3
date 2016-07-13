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
import java.util.LinkedHashSet;

/**
 * A TDMatrix represents a Term-Document Matrix of a document collection containing matrices with absolute term frequencies as well as
 * log-entropy weighted term frequencies.
 */
public class TDMatrix {

	/** The row and column numbers of a Term-Document Matrix. */
	private int rowNumber, columnNumber;

	// TODO: Ich muss eine Funktion schreiben, um das termArray einer TDMatrix neu zu setzen, wenn ich die Matrix anpasse. Bei Hinzufuegen und Loeschen einer Zeile.
	/** The array of distinct terms from a collection. */
	private String[] termArray;

	/** The arrays gf and df contain the total number of a term in a document collection (gf) and respectively
	 *  the document frequencies (df), i.e, how many documents contain a certain term. */
	private double[] gf;
	private double[] df;

	/** The Term-Document Matrix matrix containing absolute term frequencies and a weighted Term-Document Matrix containing the
	 * weighted frequencies. */
	private double[][] matrix, weightedMatrix;

	/**
	 * Instantiates a new Term-Document Matrix.
	 *
	 * @param amountTerms The amount of terms, i.e. the amount of rows
	 * @param documentNumber The document number, i.e. the amount of columns
	 */
	TDMatrix(int amountTerms, int documentNumber) {
		setRowNumber(amountTerms);
		setColumnNumber(documentNumber);
		matrix = new double[rowNumber][columnNumber];
	}

	/**
	 * Fill a Term-Document Matrix with absolute term frequencies.
	 *
	 * @param termCollection The term collection for determining the number of rows of the Term-Document Matrix
	 * @param ls3Documents The LS3Documents for determining the number of columns of the Term-Document Matrix
	 */
	void fillMatrix(LinkedHashSet<String> termCollection, ArrayList<LS3Document> ls3Documents) {
		termArray = termCollection.toArray(new String[termCollection.size()]);

		for (int column = 0; column < columnNumber; column++){
			LS3Document currentDoc = ls3Documents.get(column);
			for (int row = 0; row < rowNumber; row++){
				String tmp = termArray[row];
				double count = currentDoc.getTermCollection().count(tmp);
				matrix[row][column] = count;
			}
		}

	}

	/**
	 * Fill weighted Term-Document Matrix based on a Term-Document Matrix with absolute term frequencies.
	 * The weighting is based on the log-entropy weighting scheme.
	 */
	void fillWeightedMatrix() {
		weightedMatrix = new double[rowNumber][columnNumber];
		calcTermFrequencies(matrix);
		for (int row = 0; row < rowNumber; row++) {
			for(int column = 0; column < columnNumber; column++){
				if (matrix[row][column] != 0){
					double pij = matrix[row][column]/gf[row];
					weightedMatrix[row][column] = calcLogBase2(matrix[row][column] + 1) * 
							(1+(df[row]*(pij * calcLogBase2(pij))/calcLogBase2(columnNumber)));
				}	
			}
		}
	}

	/**
	 * This method calculates to frequencies related to a Term-Document Matrix. The first calculation counts the total frequency
	 * of a term in all documents. The second calculation counts the documents containing a certain term.
	 * 
	 * (1) Calculation of the total frequency of a term occurring in all documents of a collection. This global frequency calculation
	 * is stored in array gf.
	 * 
	 * (2) Calculation of the amount of documents which contain a term t_i based on a Term-Document Matrix. This document
	 * frequency calculation is stored in the array df.
	 * 
	 * The calculation of frequencies is proceeded for all terms in a Term-Document Matrix.
	 * 
	 * @param m The Term-Document Matrix on which the term frequency calculation is based
	 */
	private void calcTermFrequencies(double[][] m){
		gf = new double[rowNumber];
		df = new double[rowNumber];
		for (int row = 0; row < rowNumber; row++){
			for (int column = 0; column < columnNumber; column++) {
				gf[row]+= m[row][column];
				if (m[row][column] > 0) {
					df[row]+= 1;
				}
			}

		}
	}

	/**
	 * Calculation of the logarithm of a number with base 2 (natural logarithm).
	 *
	 * @param number The number of which the logarithm shall be calculated
	 * @return The logarithm of number with base 2
	 */
	private static double calcLogBase2(double number){				
		return Math.log(number)/Math.log(2);
	}


	void fillRow(double[] row, int rowNumber) {
		for (int i = 0; i < columnNumber; i++) {
			matrix[rowNumber][i] = row[i];
		}
	}

	/**
	 * Deletes a column from the Term-Document Matrix.
	 * 
	 * @param column The column index to be removed.
	 */
	 void deleteColumn(int column) {
		double[][] newArray = new double[matrix.length][matrix[0].length-1];
		int counterColumn = 0;
		for (int i = 0; i < matrix.length; i++) {
			for (int j = 0; j < matrix[0].length; j++) {

				if (j != column) {
					newArray[i][counterColumn] = matrix[i][j];
					counterColumn = counterColumn + 1;
				}
			}
			counterColumn = 0;
		}

		setMatrix(newArray);
		setColumnNumber(columnNumber - 1);
	}

	/**
	 * Deletes a row from the Term-Document Matrix.
	 * 
	 * @param row The row index to be removed.
	 */
	void deleteRow(int row) {
		double[][] newArray = new double[matrix.length-1][matrix[0].length];
		int counterRow = 0;
		for (int i = 0; i < matrix.length; i++) {
			for (int j = 0; j < matrix[i].length; j++) {
				if (i != row)
					newArray[counterRow][j] = matrix[i][j];
			}		

			if (i != row)
				counterRow = counterRow + 1;
		}

		setMatrix(newArray);
		setRowNumber(rowNumber - 1);
	}



	// TODO: Testen, ob das auch wirklich korrekt eine Zeile der Matrix hinzufuegt.
	void addRow(double[] row) {
		setRowNumber(getRowNumber() + 1);

		double[][] temp = new double[rowNumber][columnNumber];

		for (int i = 0; i < rowNumber-1; i++) {
			for (int j = 0; j < columnNumber; j++) {
				temp[i][j] = matrix[i][j];
			}
		}

		for (int i = 0; i < columnNumber; i++) {
			temp[rowNumber-1][i] = row[i];
		}

		matrix = temp;
	}
	
	// TODO: Testen, ob das auch wirklich korrekt eine Spalte der Matrix hinzufuegt.
	void addColumn(double[] column) {
		setColumnNumber(getColumnNumber() + 1);

		double[][] temp = new double[rowNumber][columnNumber];

		for (int i = 0; i < rowNumber; i++) {
			for (int j = 0; j < columnNumber - 1 ; j++) {
				temp[i][j] = matrix[i][j];
			}
		}

		for (int i = 0; i < rowNumber; i++) {
			temp[i][columnNumber-1] = column[i];
		}

		matrix = temp;
	}

	public int getColumnNumber() {
		return columnNumber;
	}

	public void setColumnNumber(int columnNumber) {
		this.columnNumber = columnNumber;
	}

	public int getRowNumber() {
		return rowNumber;
	}

	public void setRowNumber(int rowNumber) {
		this.rowNumber = rowNumber;
	}

	public double[][] getMatrix() {
		return matrix;
	}

	public void setMatrix(double[][] matrix) {
		this.matrix = matrix;
	}

	public String[] getTermArray() {
		return termArray;
	}

	public void setTermArray(String[] termArray) {
		this.termArray = termArray;
	}

	public double[][] getWeightedMatrix() {
		return weightedMatrix;
	}

	public double[] getGf() {
		return gf;
	}

	public double[] getDf() {
		return df;
	}

}

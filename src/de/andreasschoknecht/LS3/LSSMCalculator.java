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

import java.math.BigDecimal;
import java.math.RoundingMode;

import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;
import org.apache.commons.math3.linear.SingularValueDecomposition;

/**
 * <p>
 * The LSSMCalculator calculates the Latent Semantic Analysis-based Similarity Measure (LSSM). The LSSM expresses the similarity between
 * two process models based on a document vector representation of these models. To calculate the similarity value Latent Semantic Analysis
 * is used.
 * </p>
 * <p>
 * The intended use of the LSSMCalculator is to provide a (weighted) Term-Document Matrix wTDM and calculate the 
 * singular value decomposition of wTDM with the method calculateSVD(double[][] tdMatrix). Afterwards the dimensionality of the
 * resulting matrices should be reduced to the desired dimensionality k through method reduceDimensionality(int k). A reasonable 
 * upper bound for the dimensionality k is the rank of the SVD, which can be obtained by the function getRank(). Following this
 * preprocessing the function calculateLSSMMatrix() determines the similarity values between all combinations of models.
 * </p>
 */
public class LSSMCalculator {

	/** The weighted Term-Document Matrix representation for SVD.
	 *  Rank is the rank of the SVD. */
	private RealMatrix weightedTDMatrix;
	private int rank;
	
	/** The matrices resulting from SVD. */
	private RealMatrix U;
	private RealMatrix S;
	private RealMatrix Vt;

	/** The reduced k-dimensional SVD matrices. */
	private RealMatrix Uk;
	private RealMatrix Sk;
	private RealMatrix Vtk;
		
	/** The final LSSM matrix which contains the similarity values between all pairs of documents, i.e. models. */
	private RealMatrix lssmMatrix;
	
	/**
	 * Calculation of the singular value decomposition and initialization of corresponding variables.
	 *
	 * @param tdMatrix The weighted Term-Document Matrix for decomposition
	 */
	void calculateSVD(double[][] tdMatrix) {
		weightedTDMatrix = new Array2DRowRealMatrix(tdMatrix);
		
		// execute actual SVD
		SingularValueDecomposition svd = new SingularValueDecomposition(weightedTDMatrix);
		U = svd.getU();
		S = svd.getS();
		Vt = svd.getVT();
		
		// set rank of SVD
		rank = svd.getRank();
	}
	
	/**
	 * Reduce the dimensionality of the SVD matrices to k dimensions.
	 *
	 * @param k The value k of remaining dimensions
	 */
	void reduceDimensionality(int k) {
		Uk = U.getSubMatrix(0, weightedTDMatrix.getRowDimension() - 1, 0, k-1);
		Sk = S.getSubMatrix(0, k-1, 0, k-1);
		Vtk = Vt.getSubMatrix(0, k-1, 0, weightedTDMatrix.getColumnDimension() - 1);
	}
	
	/**
	 * Calculate the LSSM matrix containing the final similarity values between the documents, i.e., the models.
	 * For calculating the similarity values the Vtk matrix is scaled with the singular value matrix. Afterwards, the cosine similarity
	 * transformed on the interval [0,1] is calculated, which represents the similarity value of two documents.
	 */
	void calculateLSSMMatrix() {
		// scale Vtk with singular value matrix Sk
		RealMatrix scaledVtk = Sk.multiply(Vtk);
		
		int docsNumber = scaledVtk.getColumnDimension();
		double[][] tmpArray = new double[docsNumber][docsNumber];
		
		for (int i = 0; i < docsNumber; i++){
			RealVector documentVector1 = scaledVtk.getColumnVector(i);
			for (int j = i; j < docsNumber; j++){
				double lssmValue = (documentVector1.cosine(scaledVtk.getColumnVector(j)) + 1) / 2;
				tmpArray[i][j] = round(lssmValue, 2);
				tmpArray[j][i] = round(lssmValue, 2);
			}		
		}
		
		lssmMatrix = new Array2DRowRealMatrix(tmpArray);
	}
	
	double round(double value, int places) {
	    if (places < 0) throw new IllegalArgumentException();

	    BigDecimal bd = new BigDecimal(value);
	    bd = bd.setScale(places, RoundingMode.HALF_UP);
	    return bd.doubleValue();
	}
	
	
	int getRank() {
		return rank;
	}

	RealMatrix getU() {
		return U;
	}

	RealMatrix getVt() {
		return Vt;
	}

	RealMatrix getS() {
		return S;
	}

	RealMatrix getUk() {
		return Uk;
	}

	RealMatrix getSk() {
		return Sk;
	}

	RealMatrix getVtk() {
		return Vtk;
	}

	RealMatrix getLSSMMatrix() {
		return lssmMatrix;
	}
	
}

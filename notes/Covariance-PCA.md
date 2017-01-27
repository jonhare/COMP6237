*COMP6237 Data Mining*

# Covariance, EVD, PCA & SVD

## Summary
Understanding the shape of data in a feature space is important to effectively using it. In addition, by understanding the distribution of really highly dimensional data, it is possible to determine the most important modes of variation of that data, and thus represent the data in a space with many fewer dimensions. This is the goal of Principle Component Analysis (PCA).

Eigendecomposition (EVD) and Singular Value Decomposition (SVD) are important mathematical tools for performing PCA. In addition SVD has numerous applications throughout data-mining, and is perhaps the most important mathematical tool that you'll come across.

## Key points

### Variance and covariance
* Mathematicians talk about variance and covariance in terms of **random variables** and **expected values**.
	* For our purpose, a random variable can be thought of as the set of values from a *single dimension* of some or all the data in a feature space.
	* The expected value of such a variable is just its mean value.
* Variance (*σ*<sup>2</sup>(*x*)) of a set of n data points, *x* = [*x*<sub>1</sub>,*x*<sub>2</sub>,...,*x*<sub>*n*</sub>], is the average squared difference from the mean (*μ*): <br/>
	<img style="vertical-align:text-top;" src="http://latex.codecogs.com/svg.latex?\small \sigma^2(x)=\frac{1}{n} \sum\limits_{i=1}^n (x_i-\mu)^2"/>
	
	* Variance measures how *spread-out* the data is from the mean
* Covariance measures how two variables (*x* and *y*) change together:
	<img style="vertical-align:text-top" src="http://latex.codecogs.com/svg.latex?\small \sigma(x,y)=\frac{1}{n} \sum\limits_{i=1}^n (x_i-\mu_x)(y_i-\mu_y)"/>
	* Variance is the covariance when the two variables are the same: *σ*(*x*,*x*) = *σ*<sup>2</sup>(*x*)
	* A covariance of 0 means that the variables are **uncorrelated**
		* Covariance is in fact related to correlation: <br/>
		<img style="vertical-align:text-top" src="http://latex.codecogs.com/svg.latex?\small \rho(x,y)=\frac{\sigma(x,y)}{\sqrt{\sigma^2(x)}\sqrt{\sigma^2(y)}}"/>
	* Also note that *σ*(*x*,*y*) = *σ*(*y*,*x*)
* The covariance matrix, **Σ**, encodes how all possible pairs of dimensions in a *n*-dimensional dataset (i.e. points in a feature space), **X**, vary together: <br/>
	<img style="vertical-align:text-top" src="http://latex.codecogs.com/svg.latex?\small \mathbf{\Sigma}=\begin{bmatrix} \sigma(X_1,X_1) & \sigma(X_1,X_2) & \dots & \sigma(X_1,X_n) \\ \sigma(X_2,X_1) & \sigma(X_2,X_2) & \dots & \sigma(X_2,X_n) \\ \vdots & \vdots & \ddots & \vdots \\ \sigma(X_n,X_1) & \sigma(X_n,X_2) & \dots & \sigma(X_n,X_n)\end{bmatrix}"/> <br/>
where the vector *X*<sub>*i*</sub> is formed from the *i*-th element of all the vectors in the feature space. 
	* The covariance matrix is a **symmetric matrix**

### Mean centred data
* **Mean centring** a set of vectors is the process of subtracting the mean (computed from all [or a significant sample] of the vectors) from each vector.
* If you have a set of *n* mean-centred vectors, you can form them into a matrix, **Z**, where each *row* corresponds to one of your vectors. The covariance matrix is then directly proportional to the transpose of **Z** multiplied by **Z**:
**Σ**∝**Z**<sup>T</sup>**Z**

### Principle axes of variation
* A basis is a set of linearly independent vectors that forms a "coordinate system".
	* As the vectors are linearly independent, they are **orthogonal**.
	* For a given dimensionality, there are an infinite number of possible basis.
* In the two-dimensional case, the covariance matrix (or indeed any other 2⨉2 symmetric matrix) can be seen to define an ellipse with major and minor axes (the actual reason for this is related to a mathematical concept called "Quadratic forms", which is even applicable in higher dimensions). 
	* The major axis is along the dimension of which the underlying data is most spread.
	* The minor axis is **perpendicular** to the major axis.
* With more dimensions a similar pattern emerges:
	* The (first) principle axis is along the dimension of which the underlying data is most spread.
 	* The second principle axis is in the direction in which the data is most spread orthogonal to the principal axis.
	* The third principle axis is in the direction in which the data is most spread orthogonal to the principal axis and the second principal axis.
	* And so on...
* The set of principal axes is a **basis**.

### The Eigendecomposition of the covariance matrix
* An **eigenvector** of a square matrix **A** is a non-zero vector *v* that, when the matrix is multiplied by *v*, yields a constant multiple of *v* commonly denoted as *λ*:
<strong>A</strong>*v* = *λv*
	* *λ* is called the **eigenvalue** of **A** corresponding to the vector *v*.
* If **A** is *N*⨉*N*, then there are at most *N* unique eigenvalue-vector pairs. 
* If **A** is symmetric, then the set of all eigenvectors of **A** is a basis and the eigenvectors are **orthogonal**.
* If the matrix **A** is a covariance matrix, then it turns out that **the eigenvectors are the principal components!**
	* The vector with the largest eigenvalue is the principal axis, the vector with the second largest eigenvalue is the second principal axis, and so on.
	* **Eigenvalues turn out to be proportional to the variance along an axis!**
* Formally, the **Eigendecomposition** (EVD) factorises a diagonalisable square matrix **A** such that:
**A** = **QΛQ**<sup>-1</sup>
where **Q** is the square (*N*⨉*N*) matrix whose *i*-th column is the eigenvector *q<sub>i</sub>* of **A** and **Λ** is the diagonal matrix whose diagonal elements are the corresponding eigenvalues (i.e., **Λ**<sub>*ii*</sub> = *λ<sub>i</sub>*).
	* **The Eigendecomposition is thus a way of finding the principal axes**
	* If **A** is a real symmetric matrix (such as a covariance matrix) then **Q** is an orthogonal matrix and **Q**<sup>-1</sup> = **Q**<sup>T</sup>
	* The Eigendecomposition can be solved analytically for very small matrices (i.e. *N*≤4). For larger matrices it is solved using iterative numerical methods (see below).
	* It is common practice to arrange the columns of **Q** and corresponding eigenvalues in **Λ**, such that the eigenvalues decrease (i.e. *λ<sub>i</sub>* > *λ<sub>i+1</sub>*).

### Dimensionality reduction with Principle Component Analysis
* A linear transform (**W**) maps vectors *z<sub>i</sub>* (rows of **Z**) from one space to another:
**T** = **ZW**
where **T** is the transformed space (vectors *t<sub>i</sub>* from the rows of **T** correspond to the original vectors *z<sub>i</sub>* in the transformed space). 
	* **T** can have fewer dimensions than **Z**.
* PCA is mathematically defined as an **orthogonal linear transformation** (meaning it rotates and scales) that transforms the data to a new coordinate system such that the greatest variance by any projection of the data comes to lie on the first coordinate (called the first principal component), the second greatest variance on the second coordinate, and so on.
	* PCA thus projects data in an original space to a new space defined by the basis of principal axes. The transform matrix is just the eigenvector matrix **Q**:
	**W** = **Q**
	* Because the new (principle) axes are sorted by variance, we can choose to ignore any axes with small variance, thus providing a way of **reducing the dimensionality** of the data.
		* Keeping only the first *L* principal components (i.e. columns of **Q**, assuming the eigenvectors are sorted by decreasing eigenvalue) gives a truncated transformation:
		**T**<sub>*L*</sub> = **ZQ**<sub>*L*</sub>
		where the matrix **T**<sub>*L*</sub> now has *n* rows but only *L* columns.
	* Given a low-dimensional vector formed from PCA, it is possible to reconstruct the original vector: *t<sub>L</sub>* = *z* <strong>Q</strong><sub>*L*</sub> ⇒ *z* = *t<sub>L</sub>*<strong>Q</strong><sub>*L*</sub><sup>-1</sup> = *t<sub>L</sub>*<strong>Q</strong><sub>*L*</sub><sup>T</sup>
		* Then add the mean vector to get back into the original space before mean centring.
	* Summary of the steps for PCA:
		<div class="highlight highlight-source-shell">
			<ol>
				<li>Mean-centre the data vectors</li>
				<li>Form the vectors into a matrix **Z**, such that each row corresponds to a vector</li>
				<li>Perform the Eigendecomposition of the matrix **Z**<sup>T</sup>**Z**, to recover the eigenvector matrix **Q** and diagonal eigenvalue matrix **Λ**: **Z**<sup>T</sup>**Z** = **QΛQ**<sup>-1</sup></li>
				<li>Sort the columns of **Q** and corresponding diagonal values of **Λ** so that the eigenvalues are decreasing.</li>
				<li>Select the *L* largest eigenvectors of **Q** (the first *L* columns) to create the transform matrix **Q**<sub>*L*</sub>.</li>
				<li>Project the original vectors into a lower dimensional space, **T**<sub>*L*</sub>: **T**<sub>*L*</sub> = **ZQ**<sub>*L*</sub></li>
		</ol></div>

### Singular Value Decomposition (SVD)
* Recall the rank, *ρ*, of a matrix, **M** is the number of linearly independent rows or columns: *ρ* = rank(**M**)
* For a real *m*⨉*n* matrix, **M**, the SVD is defined as: **M** = **UΣV**<sup>T</sup>
	* Note that to keep with standard notation we're re-using the **Σ** symbol - other than being square, it's a very different matrix to the covariance matrix in SVD
	* **Σ** is a real diagonal matrix
		* The diagonal values are the "Singular Values" of **M**
		* **Σ** has dimensions *ρ*⨉*ρ*
		* The singular values are normally arranged in monotonically decreasing order: **Σ**<sub>*i,i*</sub> ≥ **Σ**<sub>*i+1,i+1*</sub>
	* The matrices **U** and **V** are orthogonal: **U**<sup>T</sup>**U** = **UU**<sup>T</sup> = **I** and **V**<sup>T</sup>**V** = **VV**<sup>T</sup> = **I**, where **I** is the identity matrix (note that the size of **I** is different for the identities involving **U** and **V** respectively).
		* **U** has dimensions *m*⨉*ρ*
			* The *m* dimensional vectors that form the columns of **U** are called the *left singular vectors*
			* The left singular vectors are the eigenvectors of **MM**<sup>T</sup>
		* **V**<sup>T</sup> has dimensions *ρ*⨉*n* (equally **V** has dimensions *n*⨉*ρ*)
			* The *n* dimensional vectors that form the columns of **V** (rows of **V**<sup>T</sup>) are called the *right singular vectors*
			* The right singular vectors are the eigenvectors of **M**<sup>T</sup>**M**
	* The singular values are the square roots of the corresponding eigenvalues of *both* **MM**<sup>T</sup> and **M**<sup>T</sup>**M**
* Relationship of SVD to PCA
	* If **Z** is a matrix of mean-centred feature vectors, then the right singular vectors of **Z** are the eigenvectors of **Z**<sup>T</sup>**Z** and thus are the principal components of **Z**
		* This means you can actually perform PCA without explicitly computing the covariance matrix
			* Better numerical stability
			* Potentially much faster
* Truncated SVD
	* Given the relationship between PCA and SVD, it's easy to see that computing a truncated SVD that only considers the top-*r* singular values and respective left and right singular vectors is useful for dimensionality reduction
	* Low rank matrix approximation is also possible and it can be proved (the Eckart-Young theorem) that keeping only the top-*r* S.V.s and reconstructing a matrix **Ḿ**=**U**<sub>*r*</sub>**Σ**<sub>*r*</sub>**V**<sub>*r*</sub><sup>T</sup> gives the *best* possible rank-*r* approximation of the original matrix, **M**, in the sense that the Frobenius norm ||**M**-**Ḿ**||<sub>F</sub> is minimised.
		* Frobenius Norm of **A** is just the square root of the sum of all the elements of **A** squared; it's basically a generalisation of the Euclidean norm of a vector to a matrix.
* Other uses of SVD
	* Pseudoinverse
		* The Moore-Penrose pseudoinverse is a generalisation of the standard matrix inverse.
			* Can be computed using SVD: **A**<sup>†</sup> = **VΣ**<sup>-1</sup>**U**<sup>T</sup>
		* Key application is in finding least squares ("best-fit") solutions to systems of linear equations
			* Solution of <strong>A</strong>*x*=*b* for *x* where ||<strong>A</strong>*x*-*b*||<sub>2</sub> is minimised is *x*=**A**<sup>†</sup>*b*
			* Example: Least squares line fitting using the pseudo-inverse:
				* assume we have a set of (*x*, *y*) points [(*x*<sub>1</sub>, *y*<sub>1</sub>), ..., (*x<sub>i</sub>*, *y<sub>i</sub>*)] for which we want to fit a straight line of best fit of the form *y* = *mx* + *c*. We can write this in the form <strong>A</strong>*x* = *b* as follows: <br/>
				<img style="vertical-align:text-top" src="http://latex.codecogs.com/svg.latex?\small \begin{bmatrix}x_1 & 1 \\ x_2 & 1 \\ \vdots & \vdots \\ x_i & 1\end{bmatrix}\begin{bmatrix} m \\ c \end{bmatrix} = \begin{bmatrix}y_1\\y_2\\\vdots\\y_3\end{bmatrix}"/><br/>
				and solve by taking the pseudo-inverse:<br/>
				<img style="vertical-align:text-top" src="http://latex.codecogs.com/svg.latex?\small \begin{bmatrix} m \\ c \end{bmatrix} = \begin{bmatrix}x_1 & 1 \\ x_2 & 1 \\ \vdots & \vdots \\ x_i & 1\end{bmatrix}^\dagger\begin{bmatrix}y_1\\y_2\\\vdots\\y_3\end{bmatrix}"/> <br/><br/>
	* Solving homogeneous equations of the form <strong>A</strong>*x*=0
		* Solution for the non-zero *x* case is any right singular vector with a corresponding singular value of 0.
			* Requires a modification to the SVD, called the "full SVD" to be computed, where the left and right singular vectors for the null-space of the matrix is computed (i.e. singular vectors that have corresponding singular values equal to zero).
	* Data mining
		* Model based CF, Latent factor models, Information retrieval, ... *and many more*

### Computing SVD and EVD
* All general EVD algorithms are iterative
* Simplest classical approach to computing eigenvectors is the "Power Iteration"
	* This computes the eigenvector with the biggest eigenvalue
	* To compute more than one eigenvector, compute the biggest, transform (rotate) the input data and truncate it so that the effect of that eigenvector is removed, then repeat...
* More efficient variants like the "Arnoldi Iteration" and the "Lanczos algorithm" exploit the idea of Krylov subspaces to compute approximate eigenvectors and use the "Gram-Schmidt" process to orthonormalise them
* These standard approaches are implemented in packages like LAPACK and ARPACK
	* Allow efficient computation of biggest *r* eigenvalue-vector pairs (and computation of truncated SVD)
	* Variants also allow the smallest *s* eigenvector-value pairs to be computed (without the need to compute the biggest)
		* Useful for "spectral clustering" - we'll come back to this when we talk about stream mining and social event detection
	* Can be applied to relatively large sparse input matrices
		* **But not really massive matrices** - more on this in a later lecture when we talk about the Netflix challenge

## Further Reading
* Wikipedia has good coverage of all the key ideas:
	* http://en.wikipedia.org/wiki/Variance
	* http://en.wikipedia.org/wiki/Covariance
	* http://en.wikipedia.org/wiki/Covariance_matrix
	* http://en.wikipedia.org/wiki/Eigenvalue,_eigenvector_and_eigenspace
	* http://en.wikipedia.org/wiki/Eigendecomposition_of_a_matrix
	* http://en.wikipedia.org/wiki/Eigenface 
	* https://en.wikipedia.org/wiki/Singular_value_decomposition
	* https://en.wikipedia.org/wiki/Power_iteration
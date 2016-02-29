*COMP6237 Data Mining*

#Lecture 12 - Modelling Prices

##Summary
Predicting the price of an item based using the prices of similar items is something data mining is often used for. This is clearly a regression problem, although the underlying function could potentially be extremely complex. You've probably seen the K Nearest Neighbours algorithm used for classification, but in this lecture you'll see how we can use it for regression. We'll look at a number of extensions of KNN and then look at how some of the computational problems associated with KNN-like methods can be mitigated.

##Key points

### KNN for classification and regression

#### Classification:

#### Regression:

#### Choosing K:

### Weighted KNN

### Dealing with Heterogeneous Variables

### KNN Problems

* KNN is computationally expensive if there are:
	- Lots of training examples
	- Many dimensions
* However, on the flip side,
	- More examples *generally* mean better accuracy
	- More dimensions *generally* give more descriptive power 
		+ unless dimensions are highly correlated or irrelevant...
* Approximate NN approaches and dimensionality reduction can help make KNN tractable with lots of high-dimensional data

#### Fast Approximate Nearest Neighbours

#### Dimensionality reduction revisited

* There are a number of possible techniques for speeding-up nearest-neighbour search in high dimensional spaces:
	- Tree structures: **K-D Trees**
		+ A k-d tree is a binary tree structure
		+ Each node splits a specific dimension of the space in two
		+ The leaf nodes store a number of points corresponding to the points that have made it down the tree to that point
		+ Fast nearest-neighbour search can be achieved by walking down the tree until a leaf is hit.
			- Brute force search can be used to select the neighbour from the points in the leaf; unfortunately, this isn’t guaranteed to actually be the closest, so you have to back-track up the tree looking in down the other paths to different leaf nodes until you can be assured that you’ve checked all the leaves that can possibly contain the neighbour (this is still just a small subset of all the leaves in the tree)
	- Hashing
		+ Some recently introduced techniques allow feature vectors to be hashed with special hashing schemes that allow vectors that are spatially similar (i.e. in the sense of Euclidean or other distance/similarity measures) to have similar hash codes!
		+ These are called Locality Sensitive Hashing Functions 
		+ 
	- Sketching

##Further Reading

* Chapter 8 of "Programming Collective Intelligence" gives a good overview of regression KNN, weighting, cross-validation and optimisation
* The original paper of LSH using p-stable distributions is quite accessible:
	* Locality-Sensitive Hashing Scheme Based on p-Stable Distributions. Datar et al. In Proceedings of the twentieth annual symposium on Computational geometry. 2004. http://people.csail.mit.edu/mirrokni/pstable.ps
* Wikipedia is a good starting point to reading about general ideas related to approximate NN and dimensionality reduction:
* https://en.wikipedia.org/wiki/Random_projection
* https://en.wikipedia.org/wiki/Locality-sensitive_hashing
* https://en.wikipedia.org/wiki/Stable_distribution
* https://en.wikipedia.org/wiki/Dimensionality_reduction


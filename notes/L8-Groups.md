*COMP6237 Data Mining*

#Lecture 8 - Discovering Groups

##Summary
Being able to meaningfully cluster data into groups using **Clustering** or **Cluster Analysis** is a key part of the process of exploratory and descriptive data mining. Clustering techniques are all a form of unsupervised machine learning. Numerous techniques for clustering exist; in this lecture we'll look at two of the most common and useful: __Hierarchical Clustering__ and __K-Means Clustering__. We'll also look briefly at a more advanced, but computationally intensive algorithm called __Mean-shift Clustering__ that both produces clusters and finds the _modes_ of the data. 

Being able to group data into clusters is a good basis for understanding that data, however, in many cases these clusters can be difficult to interpret. An alternative approach is to attempt to produce 2D visualisations (images) that highlight the key relationships in the data by projecting the data from a high dimensionality to two dimensions. We'll look at three key algorithms: **Principal Component Analysis (PCA)**, **Multidimensional Scaling (MDS)** and **Self Organising Maps (SOMs)**

##Key points
* Clustering is an unsupervised machine learning technique, that learns to group data without prior knowledge of what the groups should look like. 
	* Hierarchical Clustering attempts to iteratively break data into a hierarchy of clusters
		* Hierarchical Agglomerative Clustering builds a binary tree of clusters from the leaf nodes upwards towards the root
			* Known as a bottom-up approach

			* Requires three things:
				* a set of items to cluster
				* a distance measure to measure how close items are to each other
					* e.g. an Lp distance, a similarity measure converted to a distance (i.e. 1-Pearson or 1-cosine)
					* Doesn't necessarily have to be a distance computed over a vector; some forms of agglomerative clustering allow only need a matrix of distances or similarities computed between all items as input (see below)
				* a **linkage criterion** which measures dissimilarity of clusters as a function of the pairwise distances of items in the clusters

			* Two categories of linkage criterion: 
				* Centroid-based linkage functions that measure similarity between clusters based on the distance between their centroids
					* Requires that each item is represented by a numeric feature vector that can be interpreted as a position in space
				* Distance-based linkage functions that measure distances between clusters as a function of 

			* Given some items of data (represented by feature vectors that can meaningfully represent a point in space) and a suitable distance measure clusters are formed by grouping together the most similar items and/or previously formed clusters.
				* Once a cluster is formed its position is computed as the average of features of the items (or clusters) within it.

			* In general, complexity is *O(n<sup>3</sup>)*, which can be a problem for large data sets, however there are some *O(n<sup>2</sup>)* variants for the single-linkage and complete-linkage cases
		* Divisive clustering algorithms, which start with all the data in the root node and recursively split do exist, however they are not widely used in practice.
			* Once major reason is that in general complexity is *O(2<sup>n</sup>)*, which is worse than the agglomerative methods.

	* The K-Means algorithm is a simple, but powerful, approach to clustering that attempts to group data in a feature space into K groups or clusters represented by centroids (i.e. the mean point of the class in feature-space). 
			* The K-value must be set a priori (beforehand)
			* To begin, K initial cluster centres are chosen (typically randomly or from a sample of the existing data points)
			* Then the following process is performed iteratively until the centroids don't move between iterations (or the maximum number of iterations is reached):
				* Each point is assigned to its closest centroid
				* The centroid is recomputed as the mean of all the points assigned to it. If the centroid has no points assigned it is randomly re-initialised to a new point.
			* The final clusters are created by assigning all points to their nearest centroid.
		* K-Means always converges, but not necessarily to the most optimal solution



##Further Reading

*COMP6237 Data Mining*

# Discovering Groups

## Summary
Being able to meaningfully cluster data into groups using **Clustering** or **Cluster Analysis** is a key part of the process of exploratory and descriptive data mining. Clustering techniques are all a form of unsupervised machine learning. Numerous techniques for clustering exist; in this lecture we'll look at two of the most common and useful: __Hierarchical Clustering__ and __K-Means Clustering__. We'll also look briefly at a more advanced, but computationally intensive algorithm called __Mean Shift Clustering__ that both produces clusters and finds the _modes_ of the data. 

Being able to group data into clusters is a good basis for understanding that data, however, in many cases these clusters can be difficult to interpret. An alternative approach is to attempt to produce 2D visualisations (images) that highlight the key relationships in the data by projecting the data from a high dimensionality to two dimensions. We'll look at three key algorithms: **Principal Component Analysis (PCA)**, **Multidimensional Scaling (MDS)** and **Self Organising Maps (SOMs)**

## Key points

### Clustering

* Clustering is an unsupervised machine learning technique, that learns to group data without prior knowledge of what the groups should look like. 

#### Hierarchical Clustering
* __Hierarchical Clustering__ attempts to iteratively break data into a hierarchy of clusters
* __Hierarchical Agglomerative Clustering__ builds a binary tree of clusters from the leaf nodes upwards towards the root
	* Known as a bottom-up approach

	* Requires three things:
		* a set of items to cluster
		* a distance measure to measure how close items are to each other
			* e.g. an Lp distance, a similarity measure converted to a distance (i.e. 1-Pearson or 1-cosine)
			* Doesn't necessarily have to be a distance computed over a vector; some forms of agglomerative clustering allow only need a matrix of distances or similarities computed between all items as input (see below)
		* a **linkage criterion** which measures dissimilarity of clusters as a function of the pairwise distances of items in the clusters

	* Basic approach:
	<div class="highlight highlight-source-shell">
		<ul>
			<li>Initially every item is in a cluster of its own</li>
			<li>While there is more than one single cluster:
				<ul><li>The closest pair of clusters according to the linkage criterion are merged into a bigger cluster</li></ul>
			</li>
		</ul>
	</div>
	
		* By recording the merges at each step a binary tree structure linking the clusters can be formed
			- Often a useful way of utilising this is by drawing a diagram known as a dendrogram that shows the structure of the tree
	* Two categories of linkage criterion: 
		* Centroid-based linkage functions that measure similarity between clusters based on the distance between their centroids
			- Requires that each item is represented by a numeric feature vector that can be interpreted as a position in space
			- Examples:
				+ __Weighted Centroid Clustering__ _(WPGMC - Weighted Pair Group Method with Centroids; often also known as the "median" method)_
					* When two clusters _s_ and _t_ are combined into a new cluster _u_, the average of centroids _s_ and _t_ give the new centroid _u_
				+ __Unweighted Centroid Clustering__ _(UPGMC - Unweighted Pair Group Method with Centroids)_
					*  When two clusters _s_ and _t_ are combined into a new cluster _u_, the average of the positions of all the items within _s_ and _t_ give the new centroid _u_
		* Distance-based linkage functions that measure distances between clusters as a function of the distances between items within those clusters.
			- Clustering can be performed purely as a function of a __distance matrix__ in which each element __D__<sub>_i,j_</sub> represents the distance, _d_(_i_,_j_), between items _i_ and _j_
			- Commonly used linkage criteria between two sets (clusters) of items _A_ and _B_ include:
				+ __Minimum__ or __single-linkage clustering__:  min{_d_(_a_,_b_) : _a_ ∈ _A_, _b_ ∈ _B_} 
					* Drawback: tends to produce long, thin, clusters where the items at each end are far apart
				+ __Maximum__ or __complete-linkage clustering__: max{_d_(_a_,_b_) : _a_ ∈ _A_, _b_ ∈ _B_} 
					* Avoids problems of single-linkage clustering; tends to find compact clusters of approximately equal diameter
				+ __Mean__ or __average linkage clustering__ (UPGMA - Unweighted Pairwise Group Method with Arithmetic Mean): <br/>  <img style="vertical-align:text-top" src="http://latex.codecogs.com/svg.latex?\small \frac{1}{|A| |B|} \sum_{a \in A }\sum_{ b \in B} d(a,b)"/>

	* In general, complexity is *O(n<sup>3</sup>)*, which can be a problem for large data sets, however there are some *O(n<sup>2</sup>)* variants for the single-linkage and complete-linkage cases
* __Divisive clustering__ algorithms ("top-down" approaches), which start with all the data in the root node and recursively split do exist
	* Not widely used in practice.
		* One major reason is that in general complexity is *O(2<sup>n</sup>)*, which is worse than the agglomerative methods.

#### K-Means Clustering
* The __K-Means algorithm__ (also known as _Lloyds algorithm_) is a simple, but powerful, approach to clustering that attempts to group data in a feature space into K groups or clusters represented by centroids (i.e. the mean point of the class in feature-space). 
	- Algorithm:
	<div class="highlight highlight-source-shell">
		<ul>
		<li>The K-value must be chosen _a-priori_ (beforehand)</li>
		<li>To begin, _K_ initial cluster centres are chosen (typically randomly or from a sample of the existing data points, although note that better initialisation procedures exist - e.g. the KMeans++ algorithm)</li>
		<li>Then the following process is performed iteratively until the centroids don't move between iterations (or the maximum number of iterations is reached):
			<ul>
			<li>Each point is assigned to its closest centroid</li>
			<li>The centroid is recomputed as the mean of all the points assigned to it. If the centroid has no points assigned it is randomly re-initialised to a new point.</li>
			</ul></li>
		<li>The final clusters are created by assigning all points to their nearest centroid.</li>
	</div>
	* K-Means always converges, but not necessarily to the most optimal solution

#### Mean Shift Clustering
* __Mean Shift__ is a standard algorithm to efficiently find the modes of a __Probability Density Function (PDF)__ from a set of samples of that PDF (i.e. the featurevectors representing a set of items).
	* The only variable of the mean shift algorithm is the __kernel__ and the __kernel bandwidth__ of a __kernel density estimator__.
	* Clustering is an application of the mean shift procedure
		- Automatically chooses the number of clusters!
* The PDF of a continuous random variable is a function that describes the relative likelihood for this random variable to take on a given value
	* The PDF is non-negative everywhere and sums to 1
* In the context of a feature space, the PDF is a function that tells you how likely it is that a featurevector is _drawn_ from a specific location in a feature space.
	- A feature vector drawn from part of the space where there are lots of similar items would have a higher probability density than if the drawn feature vector were from a part of the space with very few similar items
		- or in other words, dense parts of the space with more items have a higher probability density
	* Generally speaking, for arbitrary features describing a set of items, the PDF cannot be described empirically
		- Must be estimated using some other method
			+ Simple, but crude, way to do this would be to quantise the feature space into bins in order to build a histogram
				* Each bin would contain the count of the number of items with feature vectors falling into that bin divided by the number of total items
				* Major disadvantage of this approach is that it isn't *continuous* and only gives a discrete approximation of the PDF
			+ Better way to do this is to use a __Kernel Density Estimator__ (also known as a __"Parzen Window"__)
				* Letting (__x__<sub>1</sub>, __x__<sub>2</sub>, ..., __x__<sub>n</sub>) represent the set of samples (e.g. feature vectors) in a _d_-dimensional space _R<sup>d</sup>_ from an unknown density _f_, then: <br/>
				<img style="vertical-align:text-top" src="http://latex.codecogs.com/svg.latex?\small f(\mathbf{x})=\frac{1}{nh^d}\sum\limits_{i=1}^n K(\frac{\mathbf{x}-\mathbf{x}_i}{h})"/> <br/>
				where _K_(•) is the __kernel__ (a non-negative function that integrates to one and has mean zero), and _h_ > 0 is a smoothing parameter called the __kernel bandwidth__.
					- Common choice for the kernel is a multivariate Gaussian with zero mean and unit s.d.
						+ For radially symmetric kernels, it suffices to define the profile of the kernel _k_(__x__) satisfying _K_(__x__) = _c<sub>k,d</sub>k_(||__x__||<sup>2</sup>)
				+ Intuitively one wants to choose _h_ as small as the data will allow
					- there is always a trade-off between the bias of the estimator and its variance however
* The __Mean Shift procedure__ attempts to find the modes of the density function - that is the points where the gradient is 0: ∇f(__x__)=0
	- Assuming a radially symmetric kernel, then the gradient is:
	<img style="vertical-align:text-top" src="http://latex.codecogs.com/svg.latex?\small \nabla f(\mathbf{x})  =  \frac{2c_{k,d}}{nh^{d+2}} \sum\limits_{i=1}^{n} (\mathbf{x} - \mathbf{x}_i) g\left( \left\Vert \frac{\mathbf{x} - \mathbf{x}_i}{h}\right\Vert^2 \right)"/>
	<img style="vertical-align:text-top" src="http://latex.codecogs.com/svg.latex?\small =  \frac{2c_{k,d}}{nh^{d+2}} \left[\sum\limits_{i=1}^{n} g\left( \left\Vert \frac{\mathbf{x} - \mathbf{x}_i}{h}\right\Vert^2 \right)\right]\left[\frac{\sum_{i=1}^n \mathbf{x}_ig\left( \left\Vert \frac{\mathbf{x} - \mathbf{x}_i}{h}\right\Vert^2 \right)}{\sum_{i=1}^{n}g\left( \left\Vert \frac{\mathbf{x} - \mathbf{x}_i}{h}\right\Vert^2 \right)}-\mathbf{x}\right]"/> <br/>
	where _g_(_s_)=-_k_'(_s_). 
		* The first term in the above is proportional to the density estimate at __x__ computed with a kernel _G_(__x__)=_c_<sub>_g,d_</sub>g(||__x__||<sup>2</sup>), and the second term <br/>
		<img style="vertical-align:text-top" src="http://latex.codecogs.com/svg.latex?\small \mathbf{m}_h(\mathbf{x})=\frac{\sum_{i=1}^n \mathbf{x}_ig\left( \left\Vert \frac{\mathbf{x} - \mathbf{x}_i}{h}\right\Vert^2 \right)}{\sum_{i=1}^{n}g\left( \left\Vert \frac{\mathbf{x} - \mathbf{x}_i}{h}\right\Vert^2 \right)}-\mathbf{x}"/> <br/>
		is the __mean shift__.
			* The mean shift vector always points toward the direction of the maximum increase in the density. 
	* The mean shift procedure, obtained by successive
		* computation of the mean shift vector __m__<sub>_h_</sub>(__x__<sub>_t_</sub>),
		* translation of the window __x__<sub>_t_+1</sub> = __x__<sub>_t_</sub> + __m__<sub>_h_</sub>(__x__<sub>_t_</sub>)
	
	is guaranteed to converge to a point where the gradient of density function is zero
* __Mean Shift Clustering__ works as follows:	
	<div class="highlight highlight-source-shell">
		<ul>
			<li>for each feature vector:
			<ul>
				<li>apply the mean shift procedure until convergence and store the resultant mode</li>
			</ul></li>
			<li>the set of featurevectors that converge to the same mode define the _basin of attraction_ of that mode; all features that converged to the same mode belong to the same cluster</li>
		</ul>
	</div>

### Visualising Data in Two Dimensions
* Sometimes rather than clustering data explicitly, we just want a way to visualise (through an image or diagram) which items are similar to each other and which are highly dissimilar
	- Basically we want to map high dimensional data into a lower dimensional space in a meaningful way
	- Lots of techniques allow us to do this:

#### Principal Component Analysis (PCA)
- __Principal component analysis__ allows us to project hight dimensional data into a lower dimensional space
+ Could use PCA to create a scatter plot where the x and y axis are the first and second principal components
	* No control over distance measure
	* Just because axes are the oriented along greatest variances, doesn't mean similar items will appear close to each other

#### Self Organising Maps 
* A __self-organizing map__ (__SOM__) or a _Kohonen Map_ is a type of __artificial neural network__ (__ANN__) that is trained using unsupervised learning to produce a low-dimensional (typically two-dimensional), discretised representation of the input space of the training samples, called a __map__. 
* Self-organizing maps are different from other artificial neural networks
	- they apply competitive learning as opposed to error-correction learning (such as backpropagation with gradient descent),
	- they use a neighbourhood function to preserve the topological properties of the input space.
* However, it's best not to think of SOMs in terms of neural networks!
	* Consider a SOM as an _n_ by _m_ grid of _units_ where each unit has a weight vector with dimensionality equal to the dimensionality of the input vectors
		- This is known as the _map_
		- Units that are spatially close together are considered to be neighbours
		- The _location_ of a unit in the grid can be considered to be a coordinate in 2D space
			+ The SOM maps high dimensional vectors to a 2D coordinate given by the unit which has a weight vector which is most similar to the input vector (typically in terms of Euclidean distance); this unit is called the _best matching unit_
* The are two parts to using a SOM
	*  The training process in which the weights of the units are learned
	*  The projection process in which an vector is assigned to the __best matching unit__ (__BMU__)
		-  The coordinate of this unit is the projection of the input vector onto the 2D plane
* Training a SOM:
	- Prerequisite definitions - Let:
		<div class="highlight highlight-source-shell">
			<ul>
				<li>_s_ define the current iteration</li>
				<li>_λ_ define the maximum number of iterations</li>
				<li>_t_ define the index of the target vector in the input dataset __D__</li>
				<li>__D__(_t_) defines the target input vector</li>
				<li>_v_ define an the index of a unit in the map</li>
				<li>__W__<sub>_v_</sub> define the weight vector of unit _v_</li>
				<li>_u_ is the BMU in the map</li>
				<li>Θ(_u_,_v_,_s_) defines the neighbourhood weighting function
					<ul>
						<li>This produces a weight for the update of a neighbouring node based on its distance from the BMU</li>
						<li>Common to use a Gaussian function</li>
					</ul>
				</li>
				<li>α(_s_) defines the learning rate
					<ul>
						<li>Typically this is a function that falls off as iterations increase</li>
						<li>for example: _r_<sub>_initial_</sub>exp(-_s_/_λ_), where _r_<sub>_initial_</sub> is the initial learning rate (usually a small value between 0.1 and 0.001).</li>
					</ul>
				</li>
			</ul>
		</div>
	- Algorithm:
	<div class="highlight highlight-source-shell">
		<ol>
			<li>Randomly assign weights to each unit</li>
			<li>Traverse each input vector in the input data set
				<ol>
					<li>Find the BMU by computing the Euclidean distance of the input vector to each unit and picking the unit with the smallest distance
					<li>Update the units in the neighbourhood of the BMU (including the BMU itself) by pulling them closer to the input vector: 
						__W__<sub>_v_</sub>(_s_ + 1) = __W__<sub>_v_</sub>(_s_) + Θ(_u_, _v_, _s_) α(_s_)(__D__(_t_) - __W__<sub>_v_</sub>(_s_))</li>
				</ol>
			</li>
			<li>Increase _s_ and repeat from step 2 while _s_ < _λ_</li>
		</ol>
	</div>
* The SOM idea generalises in a number of ways:
	- The map can be modified to have more (or fewer dimensions)
	- The map needn't be a regular grid; any lattice structure upon which a neighbourhood function can be defined will work
		+ Hexagonal lattices are fairly popular

#### Multidimensional Scaling (MDS)
- __Multidimensional Scaling (MDS)__ is an alternate approach to embedding high-dimensional data in a lower dimensional (typically 2D) space.
+ Two main categories:
	- __Metric MDS__: tries to optimise layout of points so that Euclidean distances in the lower dimensional space match original distances 
	- __Non-metric MDS__: attempts to directly maintain the ordering or rank between items in the 2D projection compared to the ordering of the original distances
+ Only requires distances between items as input
	+ Unlike PCA and SOM there is no explicit mapping from points in the higher dimensional space to points in the lower dimensional space
+ Irrespective of the category of the MDS algorithm, the key idea is to minimise a stress function
	* The stress function describes how well the interpoint dissimilarities in the low-dimensional space preserve those in the original space
	* Depending on the choice of stress function, the embedding can be non-linear
	* A popular stress function for non-linear metric scaling is the __Sammon Mapping__: <br/>
		<img style="vertical-align:text-top" src="http://latex.codecogs.com/svg.latex?\small S(\mathbf z_1,\mathbf z_2,...,\mathbf z_n) = \sum\limits_{i \ne j}\frac{(\delta_{ij} - \Vert \mathbf z_i - \mathbf z_j \Vert)^2}{\delta_{ij}}"/> <br/>
		where __z__<sub>_i_</sub> is the lower-dimensional vector for the _i_-th item and _δ_<sub>_ij_</sub> is the original distance between items _i_ and _j_
	* Other popular stress functions include the
		- _Least-squares scaling_ or _Kruskal-Shepard scaling_
		- _Shepard-Kruskal non-metric scaling_
	* Some stress functions can be solved using Eigendecomposition, however many must be solved using _gradient descent_ based optimisation
		- e.g. for Sammon Mapping:
			- Each point __z__<sub>_j_</sub> can be iteratively updated by: <img style="vertical-align:text-top" src="http://latex.codecogs.com/svg.latex?\small \mathbf z_j(k+1) = \mathbf z_j(k) - \gamma_k\nabla_{\mathbf z_j}S(\mathbf z_1(k), \mathbf z_2(k), ..., \mathbf z_n(k))"/> <br/>
			where ɣ is a scalar _learning rate_ (Sammon's original paper refers to this as the "magic factor"!) and the derivative of the stress function w.r.t __z__<sub>_j_</sub> is: <br/>
			<img style="vertical-align:text-top" src="http://latex.codecogs.com/svg.latex?\small \nabla_{\mathbf z_j}S(\mathbf z_1(k), \mathbf z_2(k), ..., \mathbf z_n(k)) = 2\sum\limits_{i \ne j}\left(\frac{\Vert \mathbf z_i(k) - \mathbf z_j(k)\Vert - \delta_{ij}}{\delta_{ij}}\right)\left(\frac{\mathbf z_j(k) - \mathbf z_i(k)}{\Vert \mathbf z_i(k) - \mathbf z_j(k) \Vert}\right)"/>
			
#### Other techniques
* Examples of other and more modern approaches to non-linear dimensionality reduction include
	- ISOMAP
	- Locally Linear Embedding (LLE)
	- Principal curves

## Further Reading
* Chapter 3 of "Programming Collective Intelligence" gives a good overview of the some of the basic techniques.
* Relevant sections of Chapter 14 of [The Elements of Statistical Learning](http://statweb.stanford.edu/~tibs/ElemStatLearn/printings/ESLII_print10.pdf) provide a good academic introduction
* Wikipedia has reasonable commentary (and good links to the original research) on a number of the topics: 
	- https://en.wikipedia.org/wiki/Hierarchical_clustering
	- https://en.wikipedia.org/wiki/K-means_clustering
	- https://en.wikipedia.org/wiki/Mean_shift
	- https://en.wikipedia.org/wiki/Multidimensional_scaling
	- https://en.wikipedia.org/wiki/Self-organizing_map
* [k-means++: the advantages of careful seeding](http://ilpubs.stanford.edu:8090/778/1/2006-13.pdf). Arthur and Vassilvitskii. Proceedings of the eighteenth annual ACM-SIAM symposium on Discrete algorithms. Society for Industrial and Applied Mathematics Philadelphia, PA, USA. pp. 1027–1035. 2007.
* [Mean shift: A robust approach toward feature space analysis](http://www.caip.rutgers.edu/riul/research/papers/pdf/mnshft.pdf). Comaniciu and Meer. IEEE Trans. Pattern Anal. Machine Intell., 24:603–619, 2002.
* Good descriptions of dimensionality reduction techniques and clustering:
	* Learning from Data: Concepts, Theory, and Methods (2nd ed.). Cherkassky and Mulier. John Wiley & Sons, Inc., New York, NY, USA.
* [A Nonlinear Mapping for Data Structure Analysis](http://ieeexplore.ieee.org/stamp/stamp.jsp?tp=&arnumber=1671271). Sammon. in Computers, IEEE Transactions on , vol.C-18, no.5, pp.401-409, May 1969



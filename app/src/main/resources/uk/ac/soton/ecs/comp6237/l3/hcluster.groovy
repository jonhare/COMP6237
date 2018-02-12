import uk.ac.soton.ecs.comp6237.l3.ItemTermData

//load the data
moduledata = new ItemTermData("moduledata.txt")

//class to represent a node in the tree
class BiCluster {
	BiCluster left;
	BiCluster right;
	def vec;
	double distance;
	String label;

	String toString(n=0) {
        String s = ""
        for (i in 0..<n) {
            s += ' '
        }
            
        if (left) {
            s += '-\n'
            s += left.toString(n+1)
            s +=right.toString(n+1)
        } else {
            s += label + "\n"
        }
        
        return s
    }
}

//linkage function
linkage = {distance, clust1, clust2 ->
	return distance(clust1.vec, clust2.vec)
}

//function to compute cluster centroids (if necessary - i.e. centroid based methods)
centroid = {clust1, clust2 ->
	def n = clust1.vec.length
	float[] vec = new float[n]
	for (int i=0; i<n; i++)
		vec[i] = (clust1.vec[i] + clust2.vec[i]) / 2
	return vec
}

//distance function
distance = {h1, h2 ->
	double N = h1.length;
	double SH1=0, SH2=0;

	for (int i=0; i<N; i++) {
		SH1 += h1[i];
		SH2 += h2[i];
	}
	
	SH1 /= N;
	SH2 /= N;

	double d = 0;
	double SH1S = 0;
	double SH2S = 0;

	for (int i=0; i<N; i++) {
		double h1prime = h1[i] - SH1;
		double h2prime = h2[i] - SH2;

		d += (h1prime * h2prime);
		SH1S += (h1prime * h1prime);
		SH2S += (h2prime * h2prime);
	}
	
	if (d==0) return 1;

	return 1 - d / Math.sqrt(SH1S * SH2S);
}

hcluster = {data, dFcn=distance, lFcn=linkage, cFcn=centroid ->
	def clusters = []
	data.itemNames.eachWithIndex {name, idx ->
		clusters << new BiCluster(vec: data.counts[idx], label: name)
	}
	
	while (clusters.size() > 1) {
		def best
		double minDist = 9999999999;
		//find the closest pair
		for (int i=0; i<clusters.size(); i++) {
			for (int j=i+1; j<clusters.size(); j++) {
				double dist = lFcn(dFcn, clusters[i], clusters[j])
				if (dist < minDist) {
					minDist = dist;
					best = [i, j]
				}
			}
		}
		
		//create a new cluster
		clusters << new BiCluster(
			vec: cFcn(clusters[best[0]], clusters[best[1]]),
			distance: minDist, 
			left: clusters[best[0]],
			right: clusters[best[1]])
		clusters.remove(best[1])
		clusters.remove(best[0])
	}
	
	return clusters[0]
}


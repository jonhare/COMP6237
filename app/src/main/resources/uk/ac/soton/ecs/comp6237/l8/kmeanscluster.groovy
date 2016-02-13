import uk.ac.soton.ecs.comp6237.l8.ItemTermData

//load the data
moduledata = new ItemTermData("moduledata.txt")

//pearson distance function
pearsonDistance = {h1, h2 ->
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

euclidean = {h1, h2 ->
	double d = 0;

	for (int i=0; i<h1.length; i++) {
		double diff = (h1[i] - h2[i]);
		d += (diff * diff);
	}
	return Math.sqrt(d);
}

kmeans = {data, k=10, distance=euclidean, maxiter=300 -> 
	def rows = data.counts
	
	if (k > rows.length) k = rows.length
	if (rows.length == 0) return [dataClusters:[], clusterFeatures:[]];
		
	// Determine the minimum and maximum values for each point
	def ranges = (0..<rows[0].size()).collect{i-> [rows.min{it[i]}[i], rows.max{it[i]}[i]] }

	// Create k randomly placed centroids
	def clusters = new float[rows.length][rows[0].length]
	(0..<k).each{ j -> (0..<rows[0].length).each{ i -> clusters[j][i] = Math.random()*(ranges[i][1]-ranges[i][0])+ranges[i][0] } }

	def lastmatches = null
	def bestmatches
	for (t in 0..<maxiter) {
		//println "Iteration ${t}"
		bestmatches = (0..<k).collect{[]}
	
		// Find which centroid is the closest for each row
		for (j in 0..<rows.length) {
			def row=rows[j]
			def bestmatch = 0
			def bestdist = 99999999
			for (i in 0..<k) {
					def d = distance(clusters[i], row)
					if (d < bestdist) { 
					bestmatch = i
					bestdist = d
				}
			}
			bestmatches[bestmatch] << j
		}

		// If the results are the same as last time, this is complete
		if (bestmatches==lastmatches) break
		lastmatches=bestmatches

		// Move the centroids to the average of their members
		for (i in 0..<k) {
			def avgs=[0.0]*rows[0].size()
			if (bestmatches[i].size()>0) {
				for (rowid in bestmatches[i]) {
					for (m in 0..<rows[rowid].size()) {
						avgs[m] += rows[rowid][m]
					}
				}
				for (j in 0..<avgs.size()) {
					avgs[j] /= bestmatches[i].size()
				}
				clusters[i]=avgs
			}
		}
	}
	
	def featClust = []
	for (c in clusters) {
		def feats = []
		
		for (i in 0..<c.size()) {
			if (c[i] > 0)
				feats << [dist:c[i], feature:data.terms[i]]
		}
		feats = feats.sort{-it.dist}
		featClust<<feats
	}
	
	def msgClust = []
	for (bm in bestmatches) {
		def o = []
		for (m in bm) {
			o << data.itemNames[m]
		}
		msgClust << o
	}
	return [dataClusters:msgClust, clusterFeatures:featClust]
}

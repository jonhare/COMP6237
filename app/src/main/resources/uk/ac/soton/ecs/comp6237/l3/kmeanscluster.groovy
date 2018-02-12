import uk.ac.soton.ecs.comp6237.l3.ItemTermData

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

	//initialise the centroids from data
	def rnd = (0..<rows.length).collect{it}
	Collections.shuffle(rnd)
	def clusters = new float[k][]
	for (i in 0..<k) 
		clusters[i] = rows[rnd[i]].clone()

	def lastmatches = null
	def bestmatches
	for (t in 0..<maxiter) {
		//println "Iteration ${t}"
		bestmatches = (0..<k).collect{[]}
	
		// Find which centroid is the closest for each row
		for (j in 0..<rows.length) {
			def row=rows[j]
			def bestmatch = 0
			def bestdist = distance(clusters[0], row)
			for (i in 1..<k) {
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
			} else {
				//reinit
				clusters[i] = rows[Math.Random() * rows.length].clone()
			}
		}
	}
	
	def results = [].withDefault{ [:] }
	for (i in 0..<k) {
		def c = clusters[i]
		def feats = []
		
		for (j in 0..<c.size()) {
			if (c[j] > 0)
				feats << [dist:c[j], feature:data.terms[j]]
		}
		feats = feats.sort{-it.dist}
		results[i].centroid = c
		results[i].topFeatures = feats.subList(0,10)

		results[i].items = []
		for (m in bestmatches[i]) {
			results[i].items << data.itemNames[m]
		}
	}
	
	return results
	//return msgClust
}

prettyPrint = {data ->
	String s = ""
	data.each {item ->
		s+= "\n"
		item.each {
			s+=it.toString() + "\n"
		}
		s+="\n"
	}
	return s
}

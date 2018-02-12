import static Math.sqrt

import uk.ac.soton.ecs.comp6237.l2.MovieData
data = MovieData.SMALLDATA

//Compute a Euclidean-distance based similarity
simEuclidean = {prefs, key1, key2 ->
	if (!prefs[key1] || !prefs[key2]) return 0
	
	//find items in common
	def si = prefs[key1].keySet().intersect(prefs[key2].keySet())
	
	if (si.size() == 0) return 0
	
	def sumsq = si.sum{ (prefs[key1][it] - prefs[key2][it])**2 }
	
	return 1 / (1 + sqrt(sumsq))   
}

//Compute a Pearson Correlation similarity
simPearson = {prefs, key1, key2 ->
	if (!prefs[key1] || !prefs[key2]) return 0
	//find items in common
	def si = prefs[key1].keySet().intersect(prefs[key2].keySet())
	
	def n = si.size()
	if (n == 0) return 0
	
	def sum1 = si.sum{ prefs[key1][it] }
	def sum2 = si.sum{ prefs[key2][it] }
	
	def sumSq1 = si.sum{ prefs[key1][it]**2 }
	def sumSq2 = si.sum{ prefs[key2][it]**2 }
	
	def sumProd = si.sum{ prefs[key1][it]*prefs[key2][it] }
	
	def num = sumProd - (sum1*sum2/n)
	def den = sqrt((sumSq1 - sum1*sum1/n)*(sumSq2 - sum2*sum2/n))
	
	if (den == 0) return 0
	
	return num/den
}

//Compute the similarity of a user to all other users and get the top ranked results
rank = {prefs, key, limit=10, sim=simPearson ->
	scores = prefs.findResults{
		it.key == key ? null : [sim(prefs, key, it.key), it.key] 
	}
	scores.sort{ -it[0] }
	return scores.take(limit)
}


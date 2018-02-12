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

//Compute the similarity of a user to all other users
//and get the top ranked results
rank = {prefs, key, limit=10, sim=simPearson ->
	scores = prefs.findResults{
		it.key == key ? null : [sim(prefs, key, it.key), it.key] 
	}
	scores.sort{ -it[0] }
	return scores.take(limit)
}

//Compute user-based recommendations, returning a ranked list of results
getRecommendations = {prefs, person, limit=10, similarity=simPearson ->
	def simUsers = rank(prefs, person, limit, similarity)
	
	def totals = [:]
	def simSums = [:]
	
	simUsers.each{ othersim, other ->
		//irrespectively of the limit we'll ignore highly dissimilar users
		if (othersim<0) return
		prefs[other].each { movie, rating ->
			if (prefs[person][movie]) return
			
			if (!totals[movie]) {
				totals[movie] = 0
				simSums[movie] = 0
			}
			
			totals[movie] += rating * othersim
			simSums[movie] += Math.abs(othersim)
		}
	}
	
	def ranking = totals.findResults{k, v ->
		[v/simSums[k],k]
	}
	ranking.sort{-it[0]}
	return ranking
}

//transpose the sparse ratings matrix so that rank
//can be used to compare items to items
transpose = {prefs ->
	def newprefs = [:]
	prefs.each {user, movieRatings ->
		movieRatings.each { movie, rating ->
			if (!newprefs[movie]) newprefs[movie] = [:]
			newprefs[movie][user] = rating
		}
	}
	return newprefs
}

//compute a datastructure holding the top-N similar items to each item
calculateSimilarItems = {prefs, limit=10, sim=simEuclidean ->
	result = [:]
	
	tPrefs = transpose(prefs)
	
	tPrefs.keySet().each{ item ->
		result[item] = rank(tPrefs, item, limit, sim)
	}
	
	return result
}

//precompute the item similarities for the data
itemsim = calculateSimilarItems(data)

//perform item-based CF to get recommendations
getRecommendedItems = {prefs, itemMatch, user ->
	def totals = [:].withDefault {0}
	def simSums = [:].withDefault {0}
	
	prefs[user].each { item, rating ->
		itemMatch[item].each { similarity, item2 ->
			//skip items already rated
			if (prefs[user][item2]) return

			totals[item2] += similarity * rating
			simSums[item2] += similarity
		}
	}
	
	def ranking = totals.findResults{k, v ->
		[v/simSums[k],k]
	}
	ranking.sort{-it[0]}
	return ranking
}

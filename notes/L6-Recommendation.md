*COMP6237 Data Mining*

# Lecture 6 - Making Recommendations

## Summary
Recommender systems have increasingly become a common part of everyday life. Common examples include the recommendations Amazon makes for you about products you might like to buy, and the movies Netflix recommends you might enjoy watching. 

This lecture summarises the different types of recommender systems and looks in detail at a form of recommendation called **Collaborative Filtering** in which the past behaviour of users is captured in order to make predictions about what other items the users may like (or dislike). In particular we're going to look at *neighbourhood-based* Collaborative Filtering approaches.

## Key points

* Key Terminology
	- Typically based around the idea of people (**Users**), *buying/using/wanting* some **items**.
		- items could be anything (even other people in the case of dating websites!)

* Recommender systems fall into a number of different categories:
	- **Content-based** approaches use characteristics of items in order to recommend other items with similar properties.
		- Doesn't rely on the users; only information about items is used to make predictions
	- Systems based on **collaborative filtering** make use of users' past behaviour in order to recommend items. 
		- Doesn't explicitly rely on the attributes of the items, only on user behaviour
	- *Hybrid* recommendation systems fall in between and combine user information with content attributes of the items.

* Types of Collaborative Filtering
	- **Neighbourhood-based** (or **Memory-based**)
	- **Model-based**
	- **Hybrid**

* Collaborative filtering (CF) recommender systems 101
	- Key idea: **"Similar users like similar items"**
		- Personal preferences are correlated
			- If Jack loves A and B, and Jill loves A, B, and C, then Jack is more likely to love C
		- If we can find out which users are similar to each other, we might be able to predict whether a user will like (or dislike) an item they have not *rated* before on the basis of the tastes of users that are similar.
	- CF systems:
		- Discover patterns in observed preference behavior (e.g. purchase history, item ratings, click counts) across community of users
		- Predict new preferences based on those patterns

* Measuring similarity
	- feature spaces
	- user-item matrices
	- sparsity
	- distance measures

* User based CF
	* Ranking users
		- Typically want the *top-N* users most similar to a target user. 
			- Compute the similarity between the target user and other users and choose the *N* users with the highest similarity.
			- Rather than considering all users, might want to only consider those that rated a specific item...

	* Recommending items for a user
		- Predict the rating, *r<sub>u,i</sub>*, of an item *i* by user *u* as an aggregation of the ratings of item *i* by users similar to *u*: <img style="vertical-align:middle" src="http://latex.codecogs.com/svg.latex?r_{u,i} = \mathrm{aggr}_{\hat u \in U}(r_{\hat u, i})"/>, where *U* is the set *N* of top users most similar to *u* that rated item *i*.
		- Possible aggregation functions:
			- <img style="vertical-align:text-top" src="http://latex.codecogs.com/svg.latex?r_{u,i} = \frac{1}{N} \sum\limits_{\hat u \in U} r_{\hat u, i}"/>
			- <img style="vertical-align:text-top" src="http://latex.codecogs.com/svg.latex?r_{u,i} = \frac{\sum\limits_{\hat u \in U} \mathrm{sim}(u, \hat u)r_{\hat u, i}}{\sum\limits_{\hat u \in U}|\mathrm{sim}(u, \hat u)|}"/>
			- <img style="vertical-align:text-top" src="http://latex.codecogs.com/svg.latex?r_{u,i} = \bar{r}_u + \frac{\sum\limits_{\hat u \in U} \mathrm{sim}(u, \hat u)(r_{\hat u, i} - \bar{r}_{\hat{u}})}{\sum\limits_{\hat u \in U}|\mathrm{sim}(u, \hat u)|}"/>

* Item-based versus user-based

* Problems with CF
	- the cold start problem

## Further Reading

* Chapter 1 of "Programming Collective Intelligence" gives a good overview of the basic techniques.
* Wikipedia has a good overview of Recommender Systems and Collaborative Filtering:
	- https://en.wikipedia.org/wiki/Recommender_system
	- https://en.wikipedia.org/wiki/Collaborative_filtering
* [Recommender systems](http://comp6237.ecs.soton.ac.uk/reading/summary_recommender_systems.pdf), Melville and Sindwhani, Encyclopaedia of Machine Learning, 2010
* [Amazon.com recommendations: item-to-item collaborative filtering](http://comp6237.ecs.soton.ac.uk/reading/amazon_recommender_system_2003.pdf), Linden, Smith, and York, 2003  (overview of the basic components of Amazon's recommender system)
* [Methods and Metrics for Cold-Start Recommendations](http://citeseer.ist.psu.edu/schein02methods.html), Schein, Popescul, Ungar and Pennock, SIGIR 2002.
* [Recommender systems: from algorithms to user experience](http://comp6237.ecs.soton.ac.uk/reading/recommendations_from_algorithms_to_user_experience_2012.pdf), Konstain and Riedl, 2012 (emphasizes that the user experience is important, not just predictive accuracy)
* [Audioscrobbler: Real-time Data Harvesting and Musical Collaborative Filtering](http://comp6237.ecs.soton.ac.uk/reading/audioscrobbler.pdf), Jones, ECS Project Report, 2003 (The original description of the Audioscrobbler CF system developed as part of an ECS third year project)


*COMP6237 Data Mining*

#Lecture 6 - Making Recommendations

##Summary
Recommender systems have increasingly become a common part of everyday life. Common examples include the recommendations Amazon makes for you about products you might like to buy, and the movies Netflix recommends you might enjoy watching. This lecture summarises the different types of recommender systems and looks in detail at a form of recommendation called **Collaborative Filtering** in which the past behaviour of users is captured in order to make predictions about what other items the users may like (or dislike). In particular we're going to look at "neighbourhood-based Collaborative Filtering approaches.

##Key points

* Key Terminology
	- **Users**: 
	- **Items**:

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

* Collaborative filtering recommender systems 101
	- Key idea:

Measuring similarity
	- feature spaces
	- user-item matrices
	- sparsity
	- distance measures
* User based CF
* item-based versus user-based

* Problems with CF
	- the cold start problem

##Further Reading

* Chapter 1 of "Programming Collective Intelligence" gives a good overview of the basic techniques.
* Wikipedia has a good overview of Recommender Systems and Collaborative Filtering:
	- https://en.wikipedia.org/wiki/Recommender_system
	- https://en.wikipedia.org/wiki/Collaborative_filtering
* [Recommender systems](http://comp6237.ecs.soton.ac.uk/reading/summary_recommender_systems.pdf), Melville and Sindwhani, Encyclopaedia of Machine Learning, 2010
* [Amazon.com recommendations: item-to-item collaborative filtering](http://comp6237.ecs.soton.ac.uk/reading/amazon_recommender_system_2003.pdf), Linden, Smith, and York, 2003  (overview of the basic components of Amazon's recommender system)
* [Recommender systems: from algorithms to user experience](http://comp6237.ecs.soton.ac.uk/reading/recommendations_from_algorithms_to_user_experience_2012.pdf), Konstain and Riedl, 2012 (emphasizes that the user experience is important, not just predictive accuracy)

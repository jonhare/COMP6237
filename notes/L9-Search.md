*COMP6237 Data Mining*

# Lecture 9 - Searching and Ranking

## Summary

## Key points

### Text-search basics
- Most text-search systems (and textual document classification systems) represent the text in a form called a bag of words.
	- A bag is an unordered data structure like a set, but which unlike a set allows elements to be inserted multiple times.
	- In a bag of words, the order of the words in the document is irrelevant.
	- To create a bag of words from a text document, there are a two key processes:
		- Breaking the document into its constituent words (tokenisation); 
		- Processing the words to reduce variability in the vocabulary
			- Often the words are processed using techniques like stemming (which removes variations in words like the letters _s_ and _ing_ at the end of some words).
			- Certain words are also removed (stop word removal) – words like "a", "the", "at", "which", etc., which don't have semantic meaning.
	- There are a number of computational models for text search systems, but we're interested in one called the vector-space model.
		- In the vector-space model, text documents are represented by vectors
		- The vectors from text documents contain counts of the number of times each word in the lexicon (the set of all possible words) occurs in the document.
			- Essentially the vectors are just histograms of word counts.
			- The vector for any given document is highly sparse – a document is only likely to contain a small proportion of all possible words! 
	- Searching using the vector space model is simple:
		- A query can be turned into a vector form, and all the documents in the system can be ranked by their similarity to the query.
		- Cosine similarity (i.e. the angle between the vectors) is often used, as it is less affected by the vector magnitude (the query vector probably only contains a few words, so has a much lower magnitude (e.g. L1 or L2 norm) compared to the document vectors).
		- Many of the documents will have a similarity of 0 as they don't share any terms with the query.
	- Often, the cosine similarity function is modified to weight the elements of vectors being compared.
		- The intuition is that words that appear a lot in all documents should have less weight.
			- A commonly used weighting scheme is term frequency-inverse document frequency (tf-idf)
	- In practice, actual vectors are never created (it would just be too inefficient), and the bag of words is indexed directly in a structure called an inverted index.
		- An inverted index is a map of words to postings lists.
			- A postings list contains postings.
				- A posting is a pair containing a document identifier and word count.
				- Postings are only created if the word count is bigger than 1. 
		- Using an inverted index, you can quickly find out which documents a word occurs in, and how many times that word occurs in each of those documents.
		- This allows for really efficient computation of the cosine similarity, as you only need to perform calculations for the words that actually appear in the query, and the documents containing those words.

### Indexing
* number of passes
* io considerations
* compression

### Augmented Indexes and advanced queries

### Ranking Models

### Other issues in search

## Further Reading

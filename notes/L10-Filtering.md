*COMP6237 Data Mining*

# Lecture 10 - Document Filtering

## Summary

p(d|c)=\prod_{f \in d}p(f|c)
p(c|d) \propto p(c)\prod_{f \in d}p(f|c)

\log(p(c|d)) \propto \log(p(c)) + \sum\limits_{f \in d}\log(p(f|c))

\text{Posterior probability} \propto \text{Likelihood} \times \text{Prior probability}

## Key points

## Further Reading

* A Statistical Approach to the Spam Problem. Robinson. 2003 http://www.linuxjournal.com/article/6467
    



P(A\mid B) = \frac{P(B \mid  A)\, P(A)}{P(B)}

P(B) = {\sum_j P(B\mid A_j) P(A_j)}

P(A_i\mid B) = \frac{P(B\mid A_i)\,P(A_i)}{\sum\limits_j P(B\mid A_j)\,P(A_j)}


X^2_{2k} \sim -2\sum_{i=1}^k \ln(p(c=C|f_i))

p=C^{-1}(-2\sum_{i=1}^k \ln(p(c=C|f_i)), 2k)=C^{-1}(-2\ln(\prod_{i=1}^k p(c=C|f_i)), 2k)
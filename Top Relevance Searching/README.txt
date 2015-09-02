README



*****************************************************************
Part a
*****************************************************************




*****************************************************************
Part b
*****************************************************************

Makefile
BingTest.java
xmlReader.java
xmlWriter.java


Used library: commons-codec-1.7.jar



*****************************************************************
Part c
*****************************************************************

How to run my program:
1. Go to project folder, type "make"
2. Type "make run Hx+8rg78o3Ly6+v9TGq1IX7U9olvALGFZBq19QLUkio 0.9 gates  (gates for example)



*****************************************************************
Part d
*****************************************************************

Internal design:

In this program, when it starts, it will use default account key to do searching using BING search API for user-assigned query. After getting the result, it will write result into an xml file. Then, the program will read from that file and do an information parsing, as a result we wil get information tuple [URL,title,summary] for each of top 10 results. Then it will ask user if each one is relevant or not and record user feed back by adding an indicator into each tuple (1 for relevant, 0 for non-relevant). If resulting precision is lower than target precision, then we will do a Rocchio algorithm based query expansion which will be explained in next part, after we get the new expanded query, we used that query to do the search again. This search--get relevance feedback--expand query procedure will keep going until current precision reaches target precision.



*****************************************************************
Part e
*****************************************************************

Description of query modification method:

The core in our query modification method is Rocchio algorithm, one important piece of information that needs to be calculated is tf-idf, which is the product of two statistics, term frequency and inverse document frequency.

Instead of using raw frequency of a term in a document, which is the number of times that term t occurs in document d, in this program we used augmented frequency: 
tf(t,d) = 0.5 + (0.5*f(t,d))/max{f(w,d):w belong to d} where f(t,d) is the raw frequency of a term in a document. By using this representation, it prevents a bias towards longer documents. This method of calculating term frequency returns most optimal expansion words. For calculating inverse document frequency, we used the same method to calculate idf as mentioned in the class, which is dividing the total number of documents by the number of documents containing the term, and then taking the logarithm of that quotient. We can thus calculate weight using formular weight = tf * idf. Note here we only calculate in relevant document set, therefore we neglect the negative effect from non-relevant documents which has minimum effect on expansion decision. Then we keep a general hashtable which holds all weight values for all terms from relevant documents, if one term appears in several relevant documents, its general weight will be the sum of its each individual weight from each relevant document. At the end we just pick 
out two terms from "general weight" table with top two weights and append them to the end of query to make a new query.



*****************************************************************
Part f
*****************************************************************

Bing account key: Hx+8rg78o3Ly6+v9TGq1IX7U9olvALGFZBq19QLUkio



*****************************************************************
Part g
*****************************************************************

Instead of directly using raw frequency of a term in a document, which is the number of times that term t occurs in document d, in this program we also tried to use 
two other potential alternatives to calculate term frequency.

f(t,d) is the raw frequency of a term in a document

1. logarithmically scaled frequency: tf(t,d) = 1 + log f(t,d), or zero if f(t, d) is zero.    
2. augmented frequency: tf(t,d) = 0.5 + (0.5*f(t,d))/max{f(w,d):w belong to d}

After testing and comparison, we found out that augmented frequency results in best expansion words.

We still keep the rest two methods in code, if you are interested to try the performance, you can simply uncomment and comment out corresponding parts.

Source reference: http://en.wikipedia.org/wiki/Tf-idf











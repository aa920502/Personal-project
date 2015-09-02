README
	

==========================================================================================

b).A list of all the files that you are submitting

	AprioriAssoRuleExtractor.java
	INTEGRATED-DATASET.csv
	Makefile
	README.txt
	example-run.txt
	output.txt

==========================================================================================

c).A detailed description explaining: (a) which NYC Open Data data set(s) you used to generate the INTEGRATED-DATASET file; (b) what (high-level) procedure you used to map the original NYC Open Data data set(s) into your INTEGRATED-DATASET file; (c) what makes your choice of INTEGRATED-DATASET file interesting (in other words, justify your choice of NYC Open Data data set(s)). The explanation should be detailed enough to allow us to recreate your INTEGRATED-DATASET file exactly from scratch from the NYC Open Data site.

a) We are using 311 service requests specifically for year 2014. 
(1/1/2014 - 12/31/2014)

b) On NYC Dataset website, there is only one very large CSV file which contains all 311 service requests from 2010 to present. For this project analysis purpose, we need to shrink down the size of file significantly. First, on the website, when we view the data file, we add filters to reserve requests which only happen during year 2014. Next, we removed many irrelevant and unnecessary attributes for each row, such attributes are "closed date", "Descriptor", "Incident zip", "Incident Address", "Street Name", etc. At the end, we have only six columns left in the data file to be analyzed. They are: Created Date, Agency, Agency Name, Complain Type, Location Type, Borough. We pick these columns because they seem to be the most representative and valuable columns in the file. When we export the file, we first sort it by date so that it is more organizable.
After that, we wrote a simple program which does following, we removed "Agency Name" column while extracting the information to create a text file which contains abbreviation for each agency name. In this way, the file size is further shrank because agency names often tend to be long. Also to make analysis easier, we modified created date attribute to "January", "February"... based upon different time intervals. 
The last step is to decrease number of "transactions" in the file. In this step, to be fair, we randomly pick 20000 "transactions" from each month in the file. This fields total of 240000 "transactions" on the file. 

c)This dataset is pretty interesting because this dataset is one of the largest dataset on the website which contains various kinds of information related to people's daily life in New York City. By analyzing this rich dataset, we can get many interesting relationship between many entities such as NYC government departments, resident complaints, resident location and event occurrance time. However, after we briefly skim through the dataset, we think this dataset is fairly diverse because there are just too many different types of complaints and government departments, therefore, in deciding min_sup and min_conf, we will make min_sup fairly low, typically around 5% (0.05), this setting will enable us to observe some useful rules.

==========================================================================================

d).A clear description of how to run your program (note that your project must compile/run under Linux in your CS account)

The program handles two ways of execution:

First way:
1. Type "make" in project folder to compile
2. Type "make run <min_sup> <min_conf>" to run the program. (min_sup & min_conf should be double. Ex: 5% min_sup & 20% min_conf means "make run 0.05 0.2")

----------------------------------------

Second way:
1. Type "make" in project folder to compile
2. Type "make run”, this will prompt you to input the name of file to be analyzed.(INTEGRATED-DATASET in this case). After that you will be prompted to input both min_sup and min_conf as system input parameters.

Both ways work well and produce same results.

PS: I think the second way is a better matching for project description requirement.


==========================================================================================

e).A clear description of the internal design of your project; in particular, if you decided to implement variation(s) of the original a-priori algorithm (see above), you must explain precisely what variation(s) you have implemented and why.

We implemented the same Apriori algorithm which is described in section 2.1 in the paper:
The first pass of the algorithm simply counts item occurrences to determine the large 1-itemsets. Then as long as L_k-1 is not empty, we extract candidates from L_k-1, and we put candidates that have support greater than min_sup into L_k, also we did the prune step before that. At the end of loop, we save this L_k and use it as L_k-1 for next iteration.


Our internal design can be divided into four major functions:
1. CalculateL_1
2. ExtractLargeItemsets
3. ExtractRules
4. WriteResults

After carefully observing the .csv file, we found out that each column in the file contains a particular type of items. For example, "Created Date" will only contain "month" type items, while "Borough" will only contain "borough" type items. Therefore, in making large item sets, we will not put two items with same column into same basket. Therefore, in addition to attribute value, for each item we also record its column number. (Since we use ArrayList to store the values, col Num = 0, 1, 2 ...) This also enable us to do following:
in each large itemset, each item is sorted with regard to its column number. The rationale to do this is because we know each column contains different type of objects, so we can use such schema in the "join" step to fulfill the requirement that "last item in p and q are different".

1. In order to calculate L1, we iterate through the file, collect each distinct item and its occurrence, then we pick items that have higher support than min_sup, add to L1 List.

2. In order to extract all large item-sets, we look at L_k-1 large item set every time and check to see if it's empty or not. If it is empty, it means that we cannot extract any large item-set, so we finish. Else, we will get candidate C_k from L_k-1, as introduced in the class as well as in the paper, we will pick two item-sets in L_k-1 list, then we compare first (k-2) items in L_list[i] and L_list[j], if first (k-2) items are equal, we check the last item, if they are different, we add the item in the order of column number, however, if they are same, we are not able to generate the candidate item-set. After we get the candidate sets, we compute the support of each candidate, we traverse all rows in the file, if the row contains candidate, we increase support value by 1 for that candidate. Then we normalize the value by dividing each candidate's support by total number of rows. We add only large sets that have sup greater than min_sup.

3. After we get all the large item-sets, now we can extract rules. We start from the large item-set which contains two items because L_1 is useless in extracting rules since a rule contains at least two items. The way we set up rules here is same as the way professor introduced in the class. We will have more than one item on the left hand side and only one item on the right hand side. To get confidence for the rule, we use formula: Confidence = Support(LHS U RHS)/Support(LHS). Support(LHS U RHS) is simply the support for current item in the large set, while Support(LHS) can be obtained from L_k-1 large item set. Finally we add only rules which have higher confidence than min_conf.

4. The print out function simply requires some formatting, since we have all our information well stored, it is not a hard task.

==========================================================================================

f).The command line specification of an interesting sample run (i.e., a min_sup, min_conf combination that produces interesting results). Briefly explain why the results are interesting.

After our observation, this 311 request documentation is really sparse in the way that there are too many different types of complaints and government department. Also we have truncated the file from original 900MB to about 1.2MB, there are more than 1800000 records for the year 2014, and we now randomly pick 20000 records from each month, so a 2.2% support means that we have 5280 rows that support the rule.

Therefore, in order to retrieve more interesting result, we have to adjust the min_sup and min_conf value to a relatively small number. (2% - 3.5% / 30% - 40%)

To get interesting results, run following: make run 0.022 0.35

These results are interesting because it directly reflects some life pattern among people living in New York City, things happening around them and corresponding government reaction. This can help government to better monitor the people's daily life and allocate proper resource at proper time. 

Following are some interesting rules we got from example run, you can get the same result by using example run.

[January,HEATING] => [RESIDENTIAL BUILDING] appears in the high-confidence association rules, and it reveals that when the complaints raised about heating and with the residential building, it tends to happen in January, this makes sense because in January in New York, it is really cold and there will always be issues and complaints regarding with heating problems in residential buildings. 

[Illegal Parking,Street/Sidewalk] => [NYPD], when complaints are about illegal parking and problems related with street and sidewalk, most likely they are handled by NYPD, which is New York City Police Department, this is also a reasonable and useful discovery.

[November,HEAT/HOT WATER] => [RESIDENTIAL BUILDING], this rule also tells us some useful information. In November, when there are complaints about hot water usage, most likely such complaints will happen in place like residential buildings. New York is a large city with billions of people living in it, so it makes sense that in Winter when there are hot water complaints, those complaints are most likely coming from people who live in residential buildings.

==========================================================================================

g).Any additional information that you consider significant
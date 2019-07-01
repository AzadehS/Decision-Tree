I used Java to program decision tree. There is a zip folder containing datasets, "Tree.java", "Tree.class" and a readme.txt files. I used the below command in terminal port of MacBook.  

java Tree 8 4 data_sets1/training_set.csv data_sets1/validation_set.csv data_sets1/test_set.csv no

It gets 6 variables for L, K, directory addresses of training, validation and test sets. The last variable (yes/no) is determine whether you want to print the tree or not. If yes it will print the tree for both heuristics (Information Gain and Variance Impurity). 

The below is an example of L and K and the reported accuracies for two heuristic methods and accuracies for the post-pruned decision trees constructed using the two heuristics. 

for L= 8 and K= 4

Tree Accuracy by Information Gain: 0.7585
Tree Accuracy by Variance Impurity: 0.752
Tree Accuracy of Information Gain Pruned Tree: 0.765
Tree Accuracy of Variance Impurity Pruned Tree: 0.7575

java Tree 8 4 data_sets1/training_set.csv data_sets1/validation_set.csv data_sets1/test_set.csv no

java Tree 8 4 data_sets2/training_set.csv data_sets2/validation_set.csv data_sets1/test_set.csv no

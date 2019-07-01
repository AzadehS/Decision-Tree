/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author azadehsamadian , axs168931, 2021329467, Spring_2018
 */



import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;




public class Tree {
    
        public int posNum = 0;
        public int negNum = 0;
        
	public Tree right;
        public Tree left;
        
        public String ID; 
        public String rightState;
        public String leftState;
        
	public int nodeStatus = -1;
        
	public Tree(Tree P) {
	}
        
        
        public static double entropy(int positive, int negative)
	{
		if(positive == 0 || negative == 0)
			return 0.0;
		
		double negativeR = (double)negative / (double)(positive + negative);
                double positiveR = (double)positive / (double)(positive + negative);
		double positiveLog2 = - Math.log(positiveR)/Math.log(2);
		double negativeLog2 = - Math.log(negativeR)/Math.log(2);
		return positiveR * positiveLog2 + negativeR * negativeLog2;
	}
        
	public static double varianceImpurity(int positive, int negative)
	{
		if(positive == 0 || negative == 0)
			return 0.0;
		return  (double)positive * (double)negative / (double)(positive + negative) / (double)(positive + negative);
	}
        
        
        
	public static double calculateAccuracy(Tree T, ArrayList<ArrayList<String>> list)
	{
		int Correct = 0, Wrong = 0;
		for(int i = 1; i < list.size(); i++)
			if(nodeCondition(T, list.get(0), list.get(i)))
				Correct++;
			else
				Wrong++;
		return (double)Correct / (double)(Correct + Wrong);
	}
	
        public static double computeGain(ArrayList<ArrayList<String>> S, String ID, int mode)
	{
                int featureID = featureID(S.get(0), ID);	
                int classID = featureID(S.get(0), "Class");
		
		int P = 0, N = 0;
                int P_P = 0, P_N = 0;
                int N_P = 0, N_N = 0;
                
		double negativeR = 0;
                double positiveR = 0;
		for(int i = 1; i < S.size(); i++)
			if(S.get(i).get(classID).compareTo("1") == 0)
			{
				P++;
				if(S.get(i).get(featureID).compareTo("1") == 0)
					P_P++;
				else
					P_N++;
			}
			else
			{
				N++;
				if(S.get(i).get(featureID).compareTo("1") == 0)
					N_P++;
				else
					N_N++;				
			}
		positiveR = (double)(P_P + N_P) / (double)(N + P);
                negativeR = (double)(N_N + P_N) / (double)(N + P);
		
		if(mode == 2)
			return varianceImpurity(P, N) - positiveR * varianceImpurity(P_P, N_P) - negativeR * varianceImpurity(P_N, N_N);
		return entropy(P, N) - positiveR * entropy(P_P, N_P) - negativeR * entropy(P_N, N_N);
	}
     
        public static int getNodeCount(Tree T)
	{
		if(T.nodeStatus >= 0)
			return 0;
		return getNodeCount(T.left) + getNodeCount(T.right) + 1;
	}
        
        public static boolean nodeCondition(Tree T, ArrayList<String> nameID, ArrayList<String> state)
	{
		boolean recentState = false;
		Tree recent = T;
		while(recent.nodeStatus < 0)
		{
			String ID = recent.ID;
			int recentClassID = featureID(nameID, ID);
			if(state.get(recentClassID).compareTo("0") == 0)
				recent = recent.right;
			else
				recent = recent.left;
		}
		int recentClassID = featureID(nameID, "Class");
		if(recent.nodeStatus == Integer.valueOf(state.get(recentClassID)))
			recentState = true;
		return recentState;
	}
        
	public static Tree postPruning(Tree T, ArrayList<ArrayList<String>> V, int K, int L)
	{
		Tree Best = duplicateTree(T);
		double BestAccuracy = calculateAccuracy(Best, V);
		for(int i = 0; i < L; i++)
		{
			Random R = new Random();
			int M = R.nextInt(K);
			for(int j = 0; j < M; j++)
			{
				Tree X2 = duplicateTree(T);
				int N = getNodeCount(T);
				int P = R.nextInt(N);
				int z = 0;
				ArrayList<Tree> newTree = new ArrayList<Tree>();
				Tree subTree = X2;
				newTree.add(subTree);
				while(z < P && newTree.size() > 0)
				{
					if(subTree != null && subTree.left != null)
					{
						if(subTree.left.nodeStatus < 0)
							newTree.add(newTree.size(),subTree.left);
					}
					if(subTree != null && subTree.right != null)
					{
						if(subTree.right.nodeStatus < 0)
							newTree.add(newTree.size(),subTree.right);
					}
					subTree = newTree.get(0);
					newTree.remove(0);
					z++;
				}

				if(subTree.posNum > subTree.negNum)
					subTree.nodeStatus=(1);
				else
                                subTree.nodeStatus=(0);
				
				subTree.right=null;
                                subTree.left=null;
				//Print(Temp,0);
				double recentAccuracy = calculateAccuracy(X2, V);
				if(recentAccuracy > BestAccuracy)
				{
					Best = duplicateTree(X2);
					BestAccuracy = recentAccuracy;
				}
			}
		}
		return Best;
	}
        
        public static Tree treeGenerator(ArrayList<ArrayList<String>> table, int mode)
	{
		Tree T = new Tree(null);
		int classID = featureID(table.get(0), "Class");
		int P = 0, N = 0;
		for(int i = 1; i < table.size(); i++)
			if(table.get(i).get(classID).compareTo("1") == 0)
				P++;
			else
				N++;
		if(table.size() - 1 == P)
		{
			T.nodeStatus=(1);
			return T;
		}
		if(table.size() - 1 == N)
		{
			T.nodeStatus=(0);
			return T;
		}
		if(table.get(0).size() < 2)
			if(P > N)
			{
				T.nodeStatus=(1);
				return T;
			}
			else
			{
				T.nodeStatus=(0);
				return T;
			}
		double max_gain = 0;
		String max_gain_set = "";
		for(int i = 0; i < table.get(0).size() - 1; i++)
		{
			double recentGain = computeGain(table, table.get(0).get(i), mode);
			if(recentGain > max_gain)
			{
				max_gain = recentGain;
				max_gain_set = table.get(0).get(i);
			}
		}
		T.ID=max_gain_set;
		T.left=treeGenerator(parseSet(table, max_gain_set, "1"), mode);
		T.leftState="1";
		T.right=treeGenerator(parseSet(table, max_gain_set, "0"), mode);
		T.rightState="0";
		T.posNum=P;
		T.negNum=N;
		return T;
	}
        
	public static ArrayList<ArrayList<String>> parseSet(ArrayList<ArrayList<String>> S, String ID, String state)
	{
		ArrayList<ArrayList<String>> OutPut = new ArrayList<ArrayList<String>>();

		int featureID = featureID(S.get(0), ID);
		OutPut.add(new ArrayList<String>());
		for(int i = 0; i < S.get(0).size(); i++)
			if(i != featureID)
				OutPut.get(0).add(S.get(0).get(i));
		for(int j = 0; j < S.size(); j++)
			if(S.get(j).get(featureID).compareTo(state) == 0)
			{
				OutPut.add(new ArrayList<String>());
				for(int i = 0; i < S.get(0).size(); i++)
					if(i != featureID)
						OutPut.get(OutPut.size() - 1).add(S.get(j).get(i));
			}
		return OutPut;
	}
        
	public static ArrayList<ArrayList<String>> fileRead(String csvname)
	{
	    try {
	    	ArrayList<ArrayList<String>> table = new ArrayList<ArrayList<String>>();
			BufferedReader buffer = new BufferedReader(new FileReader(csvname));
	        String Line = buffer.readLine();
	        while (Line != null) {
	        	table.add(new ArrayList<String>(Arrays.asList(Line.split(","))));
	        	Line = buffer.readLine();
	        }
	        buffer.close();
	        return table;
	    }
	    catch (FileNotFoundException e) {e.printStackTrace();return null;}
			
	    catch (IOException e) {e.printStackTrace();return null;}
		
	    catch (Exception e) {e.printStackTrace();return null;}
		
	}
	public static Tree duplicateTree(Tree S)
	{
		Tree T = new Tree(null);
		T.negNum=(S.negNum);
		T.posNum=(S.posNum);
		T.ID=(S.ID);
		T.leftState=(S.leftState);
		T.nodeStatus=(S.nodeStatus);
		T.rightState=(S.rightState);
		if(S.nodeStatus >= 0)
			return T;
		T.left=(duplicateTree(S.left));
		T.right=(duplicateTree(S.right));
		return T;
	}
	public static int featureID(ArrayList<String> dataList , String ID)
	{
		for(int i = 0; i < dataList.size(); i++)
			if(dataList.get(i).compareTo(ID) == 0)
				return i;
		return -1;
	}
        
	public static void Print(Tree T, int Depth)
	{
		if(T.nodeStatus >= 0)
		{
			System.out.println(T.nodeStatus);
			return;
		}
		for(int i = 0; i < Depth; i++)
			System.out.print("| ");
		System.out.print(T.ID + " = " + T.leftState + " : ");
		if(T.left.nodeStatus < 0)
			System.out.println(" ");
		Print(T.left, Depth + 1);
		for(int i = 0; i < Depth; i++)
			System.out.print("| ");
		System.out.print(T.ID + " = " + T.rightState + " : ");
		if(T.right.nodeStatus < 0)
			System.out.println(" ");
		Print(T.right, Depth + 1);
		
	}
        
        public static void main(String[] args) {
            
				
		int L = Integer.parseInt(args[0]);	
	        int K = Integer.parseInt(args[1]);
		ArrayList<ArrayList<String>> TrainSet = fileRead(args[2]);
		ArrayList<ArrayList<String>> ValidationSet = fileRead(args[3]);
		ArrayList<ArrayList<String>> TestSet = fileRead(args[4]);
          
		Tree ID3, ID3_variance, ID3_postPruned, ID3VI_postPruned;
		ID3 = treeGenerator(TrainSet, 1);
		ID3_variance =  treeGenerator(TrainSet, 2);
		ID3_postPruned = postPruning(ID3, ValidationSet, K, L);
                ID3VI_postPruned = postPruning(ID3_variance, ValidationSet, K, L);
                
                System.out.println(); 
		System.out.println("for L= " + L + " and K= "+ K);
                System.out.println(); 

		System.out.println("Tree Accuracy by Information Gain: " + calculateAccuracy(ID3, TestSet));
		System.out.println("Tree Accuracy by Variance Impurity: " + calculateAccuracy(ID3_variance, TestSet));
		System.out.println("Tree Accuracy of Information Gain Pruned Tree: " + calculateAccuracy(ID3_postPruned, TestSet));
		System.out.println("Tree Accuracy of Variance Impurity Pruned Tree: " + calculateAccuracy(ID3VI_postPruned, TestSet));
                System.out.println();
                System.out.println("________________________________________________");

		if(args[5].compareToIgnoreCase("yes") == 0)
		{
                        System.out.println("========Information Gain========");
			Print(ID3, 0);
                        System.out.println("========Variance Impurity========");
			Print(ID3_variance, 0);
		}
	}
        
}

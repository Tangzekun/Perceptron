import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import java.util.*;
import java.io.*;
import java.text.*;
import java.math.*;



public class Perceptron 
{
	static int maxIter = 100;
	static double learningRate = 0.1;
	static int num_Instance = 0;
	static int test_Num_Instance =0;
	static int theta = 0;
	
	
	public static void main(String[] args) throws IOException 
	{

		String trainingFile = args[0];
		String testingFile = args[1];
		String modelFile = args[2];
		
		String [] attributeName = null;
		int rowLength =0;
//		int testRowLength = 0;
		ArrayList<int[]> fileContent = new ArrayList <int[]> ();
		ArrayList<int[]> testFileContent = new ArrayList <int[]> ();
		BufferedReader trainReader = null;
		BufferedReader testReader = null;
		
        try
        {
            String line = "";
            String nameLine = "";
            String testNameLine ="";
            //trainReader = new BufferedReader(new FileReader("train.csv"));
            trainReader = new BufferedReader(new FileReader(trainingFile)); 
            testReader = new BufferedReader(new FileReader(testingFile)); 
            
            
            nameLine = trainReader.readLine();
    		attributeName = nameLine.split(",");
    		rowLength = attributeName.length;
            while ((line = trainReader.readLine()) != null) 
            {	            	
            	String [] rowContentStr ;          	
            	rowContentStr = line.split(",");  
            	int[] rowContent = new int[rowContentStr.length];
            	for(int i = 0;i < rowContentStr.length;i++)
            	{
            		rowContent[i] = Integer.parseInt(rowContentStr[i]);
            	}
            	fileContent.add(rowContent);
            	num_Instance = fileContent.size();
            }
            
            
            testNameLine = testReader.readLine();
//            testAttributeName = testNameLine.split(",");
//            testRowLength = testAttributeName.length;
            while ((line = testReader.readLine()) != null) 
            {
            	String [] rowContentStr ;          	
            	rowContentStr = line.split(",");  
            	int[] testRowContent = new int[rowContentStr.length];
            	for(int i = 0;i < rowContentStr.length;i++)
            	{
            		testRowContent[i] = Integer.parseInt(rowContentStr[i]);
            	}
            	testFileContent.add(testRowContent);
            	test_Num_Instance = testFileContent.size();
            }
//            System.out.println("test_Num_Instance : " + test_Num_Instance);
        } 
        catch (Exception e) 
        {
            e.printStackTrace();
        } 
        finally
        {
            try 
            {
                trainReader.close();
                testReader.close();
            } 
            catch (IOException e) 
            {
                e.printStackTrace();
            }
        }
        
        int[] outputs = new int [num_Instance];
        for (int t =0; t < num_Instance; t++)
        {
        	outputs[t] = fileContent.get(t)[rowLength-1];
 //       	System.out.println (outputs[t]);
        }
        
        int [] testOutputs = new int[test_Num_Instance];
        for (int t =0; t < test_Num_Instance; t++)
        {
        	testOutputs[t] = testFileContent.get(t)[rowLength - 1];
 //       	System.out.println (outputs[t]);
        }
        
        
        
        double [] weights = new double [rowLength];
		double localError, globalError;
		int output;
		int iteration = 0; 
		
		for (int j =0; j< rowLength; j++)
		{
			//String name = attributeName[j];
			weights[j] = randomNumber(0,1);
		}
		
		iteration = 0;
		do {
			iteration++;
			globalError = 0;
			//loop through all instances (complete one epoch)
			for (int p = 0; p < num_Instance; p++) 
			{
				// calculate predicted class
				int [] rowStuff = fileContent.get(p);
				output = calculateOutput(theta,weights, rowStuff, rowLength);
				// difference between predicted and actual class values
				localError = outputs[p] - output;
				//update weights and bias
				for (int z=0; z<rowLength; z++)
				{	
					//weights [z] += learningRate * localError;
					weights [z] += learningRate * localError * rowStuff[z];
				}
				weights [rowLength - 1] += learningRate * localError;
				globalError += (localError*localError);
			}

			/* Root Mean Squared Error */
			//System.out.println("Iteration "+iteration+" : RMSE = "+Math.sqrt(globalError/num_Instance));
		} while (globalError != 0 && iteration<= maxIter);
        
//		System.out.println("\n=======\nDecision boundary equation:");
//		System.out.println("Bias is :" + weights[rowLength-1]);
		DecimalFormat df = new DecimalFormat("#.####");      
		weights[rowLength-1] = Double.valueOf(df.format(weights[rowLength-1]));
		
		writeFile(modelFile, "", weights[rowLength-1]);
		for (int y= 0; y < rowLength-1; y++)
		{
//			System.out.println(attributeName[y] +"'s weight is: "+ weights[y]);
			weights[y] = Double.valueOf(df.format(weights[y]));
			writeFile(modelFile,attributeName[y] + " ", weights[y]);
		}
		
		calculateAccuracy (theta, weights, testFileContent, rowLength);
	}
	
	
	public static double randomNumber(int min , int max) 
	{
		DecimalFormat df = new DecimalFormat("#.####");
		double d = min + Math.random() * (max - min);
		String s = df.format(d);
		double x = Double.parseDouble(s);
		return x;
	}
	
	static int calculateOutput(int theta, double weights[], int [] rowStuff, int rowLength)
	{	
		double sum = 0;
		for (int i = 0; i < rowLength-1; i++)
		{	
			sum += rowStuff[i] * weights[i];
		}
		sum += weights[weights.length-1];
			return (sum >= theta) ? 1 : 0;	
	}
	

	static void calculateAccuracy (int theta, double weights[], ArrayList<int[]> testFileContent, int rowLength )
	{	
//		double [] testWeights = new double [rowLength];
		int accuracyCounter =0;
//		for (int j =0; j< rowLength; j++)
//		{
//			//String name = attributeName[j];
//			weights[j] = randomNumber(0,1);
//		}
		for (int t=0; t< test_Num_Instance; t++)
		{
			int [] rowStuff = testFileContent.get(t);
			
			if(testFileContent.get(t)[rowLength-1] == calculateOutput (theta, weights, rowStuff, rowLength))
			{
				accuracyCounter ++;
			}
		}
		
//		System.out.format("Accuracy is %d%n", (accuracyCounter/1000)*100);
//		return ("Accuracy is " +(accuracyCounter/1000)*100);
			
	}
	
	
	public static void writeFile(String model, String attribute, double corresponding) throws IOException 
	{
		File fout = new File(model);
		if(!fout.exists())
		{
			fout.createNewFile();
		}
		
		FileWriter fileWritter = new FileWriter(fout.getName(),true);
        BufferedWriter bufferWritter = new BufferedWriter(fileWritter);
//        for (int i = 0; i < length; i++) 
//		{
        bufferWritter.write(attribute+" "+ corresponding);
        bufferWritter.newLine();
        bufferWritter.close();
	}
	
		
}

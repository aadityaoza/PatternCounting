package com.cs.sjsu.edu;

import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

public class Packet_Contents_Main {


	public static void main(String[] args) 
	{
		try
		{
				String fname = args[0];            // Training data
				String fnameTest = args[1];        // Scoring data
				
				// Train  Packet counting scheme on normal data
				TestDataGenerator packetReader = new TestDataGenerator();
				
				// Use 5000 packets from training data to create model
				packetReader.loadPackets(fname,-1);
				
				//System.out.println("The total number of bytes of payloads in the training file = "+packetReader.countOfBytes);
				//System.out.println("The number of Packets extracted    = "+packetReader.listOfExtractedSequencesFromAllPayloads.size());
				
				// Map incoming n-grams to numbers .List of all unique n grams extracted 
				HashMap mapByteToNumbers = new HashMap<>();
			
			   

				
				//Read payload size and nGram size from properties file
				FileReader reader = new FileReader("packetCount.properties");
				Properties properties = new Properties();
				properties.load(reader);
				int nGramSize=Integer.parseInt(properties.getProperty("nGramsize"));
				
				//Construct a list of all unique n grams 
				for(int i=0 ;i<packetReader.listOfExtractedSequencesFromAllPayloads.size();i++)
				{
					//Extract a payload 
					ArrayList<Integer> payload = new ArrayList<Integer>();
					payload=packetReader.listOfExtractedSequencesFromAllPayloads.get(i);
					
					for(int j=0;j<payload.size()-(nGramSize-1);j++) // Run loop to length - (nGramSize -1 ) to prevent ArrayIndexOutOfBounds Exception
					{
						//Extract the nGram into a List
						List nGram = payload.subList(j, j+nGramSize);
						
						if (mapByteToNumbers.containsKey(nGram))
						{
							// Do nothing
						}
						else
						{
							//Put the new nGram into the list
							mapByteToNumbers.put(nGram, 0);
						}
					}
					
				}
				
				System.out.println("Number of n grams "+mapByteToNumbers.size());
				//System.out.println("The total number of unique"+ nGramSize+ " grams extracted = "+mapByteToNumbers.size());
				
				
				// Test Packet counting scheme on other data
				TestDataGenerator testDataGenerator = new TestDataGenerator();
				
				// Use 5000 packets to test against the model
				testDataGenerator.loadPackets(fnameTest,-1);
				DecimalFormat df = new DecimalFormat("#.######");
				
				// Remove .pcap from names of training and testing files
				fname = fname.replaceAll(".pcap", "");
				fnameTest = fnameTest.replaceAll(".pcap", "");
				
				// Output file to write results
				//BufferedWriter out = new BufferedWriter(new FileWriter(fname+"_"+fnameTest+".csv"));
				//out.write("Serial no.,Size,Score,Normalized_Score\n");
				
				BufferedWriter out = new BufferedWriter(new FileWriter(fname+"_"+fnameTest+".dat"));
				
				double begTime = System.currentTimeMillis();
				
				for(int i=0;i<testDataGenerator.listOfExtractedSequencesFromAllPayloads.size();i++)
				{
					//Extract a payload 
					ArrayList<Integer> payload = new ArrayList<Integer>();
					payload=testDataGenerator.listOfExtractedSequencesFromAllPayloads.get(i);
					double scoreOfPayload = 0 ;
					
					for(int j=0;j<payload.size()-(nGramSize-1);j++) // Run loop to length -(nGramSize -1) to prevent ArrayIndexOutOfBounds Exception
					{
						//Extract the nGram into a List
						List nGram = payload.subList(j, j+nGramSize);
						
						if (mapByteToNumbers.containsKey(nGram))
						{
							scoreOfPayload+=1;
						}
						else
						{
							// Do nothing
						}
					}
					
					
					//System.out.println("The score of payload "+i+" = "+scoreOfPayload+" length = "+payload.size()+" and normalized score = "+df.format((double)scoreOfPayload/payload.size()));
					
					// Write results to a csv file 
					//out.write(i+","+payload.size()+","+scoreOfPayload+","+df.format((double)scoreOfPayload/payload.size())+"\n");
					out.write(i+"\t"+df.format((double)scoreOfPayload/payload.size())+"\n");
					
				}
				
				double endTime = System.currentTimeMillis();
				double inputSize = (double)testDataGenerator.listOfExtractedSequencesFromAllPayloads.size();
				System.out.println("Total time for testing all packets = "+df.format(endTime-begTime));
				out.close();
				
				// Write performance related details to time.txt file
				BufferedWriter outTime = new BufferedWriter(new FileWriter(nGramSize+"_time.txt",true));
				outTime.write(testDataGenerator.listOfExtractedSequencesFromAllPayloads.size()+" "+(endTime-begTime)+"\n");
				outTime.close();
		}
		
		catch(Exception e)
		{
			e.printStackTrace();
			//System.err.println("Error from Packet_Contents_Main");
		}
				
				
	}

}

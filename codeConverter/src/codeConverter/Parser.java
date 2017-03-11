package codeConverter;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import errorHandler.ErrorMessage;
import errorHandler.ErrorType;

public class Parser {
		
	/*	Read data from input file. Divide the data into lines of code
	 * or opCodes, skipping the empty lines*/
	public static HashMap <Integer, String> readFromFile(String path)
	{
		HashMap <Integer, String> linesOfCode = new HashMap<Integer, String>();
		int index = 0; 
		try 
		{
			File inputFile = new File(path);
			if (inputFile.exists())
			{
				Scanner sc = new Scanner(inputFile);
			
				while ( sc.hasNextLine())
				{				
					String nextLine = "";				
					// Skip the empty lines and comment lines
					while (nextLine.isEmpty() && sc.hasNextLine())
					{
						nextLine = eraseComments(sc.nextLine());
						index++;
					}
					if (!nextLine.isEmpty())
					linesOfCode.put(index, nextLine);
				}
				sc.close();
			}
			else 
			{
				ErrorMessage err = new ErrorMessage(ErrorType.inputFileNotFoundError);
				err.print();
			}
		} 
		catch (FileNotFoundException e) 
		{
			System.err.println("A FileNotFoundException was caught!");
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return linesOfCode;
	}
	
	
	/* Comment is starting with ';' symbol
 	Erase everything after ';' symbol in every line */
	private static String eraseComments(String inputLine)
	{
		inputLine = inputLine.trim();
		
		int index = inputLine.indexOf(';');
		if (index >= 0) {
			inputLine = inputLine.substring(0,index);
			inputLine = inputLine.trim();
		}
		return inputLine;
	}

		
	public static void main(String[] args)
	{
		HashMap <Integer, String> linesOfCode = new HashMap <Integer, String>(); 
		String path = "tests\\dependent.txt";
		
		linesOfCode =readFromFile(path);
		
		for (Map.Entry<Integer, String> e : linesOfCode.entrySet())
        {
            System.out.println(e.getKey() + " - " + e.getValue());
        }
			
	}

}

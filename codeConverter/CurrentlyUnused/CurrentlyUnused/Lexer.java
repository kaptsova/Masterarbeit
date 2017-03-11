package CurrentlyUnused;

import java.util.ArrayList;

import asmLine.AsmLine;
import asmLine.OpCode;

public class Lexer {
	// Split single assembler code line into Lexsems
	// E.g. : ADD A B C => 'ADD' + 'A' + 'B' + 'C'
	private static boolean splitLineToLexsems(String inputLine)
	{
		// Every assembler code line consists of 3 Operands and 1 Operator
		int numberOfOperands = 3;
		int numberOfOperators = 1;
		int index = -1;
		char delimiter = ' ';
		
		String processedLine = inputLine;
		String lexem[] = new String[numberOfOperands + numberOfOperators];
		
		// Lexem is a substring between 2 delimiters
		// Handle all lexems except the last lexem in the line
		for (int i = 0; i <(numberOfOperands + numberOfOperators - 1); i++)
		{
			processedLine = processedLine.trim();
			// Seek next delimiter in string
			if ((index = processedLine.indexOf(delimiter)) < 0)
				return false;
			lexem[i] = processedLine.substring(0, index);
			processedLine = processedLine.substring(index);
		}
		// Handle last lexem in a string - remaining part of the string
		lexem[numberOfOperands + numberOfOperators - 1] = processedLine.trim();
		// If last lexem contains delimiters - wrong format
		if (lexem[numberOfOperands + numberOfOperators - 1].contains(" "))
			return false;
		
		System.out.println(lexem[0] + "  " +lexem[1] + " " + lexem[2]+ " " + lexem[3]);
		// Assembler code line is syntax-correct
		return true;				
	}
	

	
	public static String splitSingleWord(String inputLine, String processedLine)
	{
		int index = -1;
		char delimiter = ' ';
		
		processedLine = inputLine.trim();
		String singleWord = "";
		index = processedLine.indexOf(delimiter);
		
		// If there are more than one words in the line
		if (index > 0)
		{
			// Split line into 2 parts : before and after delimiter
			singleWord = processedLine.substring(0, index);
			processedLine = inputLine.substring(index);
			// Trim remaining part of the line
			processedLine = processedLine.trim();
			System.out.println(processedLine);
			
			return singleWord;
		}
		// If there is only 1 word in the line
		if ((index < 0) && !processedLine.isEmpty())
		{
			return processedLine;
		}
		return singleWord;
		
	}
	
	public static void main(String[] args)
	{
		String myString = "ADD A B C";
		
		splitLineToLexsems(myString);
		String processedString = "";
		String myWord = splitSingleWord(myString, processedString);
		
		System.out.println(myWord + " " + processedString);
		
		String[] myStringArray = myString.split("\\s");
		
		String mnemonic = myStringArray[0];
		String op1 = myStringArray[1];
		System.out.println(myStringArray.length + " : " + mnemonic + " - " + op1); 
	}
	
	private static boolean checkMnemonics(ArrayList<AsmLine> programCodeList, ArrayList<OpCode> opCodeList)
	{
		boolean status = false;
		String operator = "";
		for (int i = 0; i < programCodeList.size(); i++)
		{
			status = false;
			AsmLine AsmLine = programCodeList.get(i);
			operator = AsmLine.getmOperator();
			
			if (operator.equalsIgnoreCase("dq"))
				status = true;
			else
			{
				status = isRegularCommand(opCodeList, status, operator);
			}
			
			if (status == false)
			{
				return status;
			}
		}
		return status;
	}


	private static boolean isRegularCommand(ArrayList<OpCode> opCodeList, boolean status, String operator) {
		for (int j = 0; j < opCodeList.size(); j++)
		{
			OpCode opCode = opCodeList.get(j);
			String mnemonic = opCode.getMnemonic();
			if (operator.equalsIgnoreCase(mnemonic))
			{	
				status = true;
			}
		}
		return status;
	}
	
}

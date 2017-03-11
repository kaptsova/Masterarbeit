package asmLine;

import java.util.ArrayList;

import commonTypes.CommandType;
import operand.OperandType;

public class DeclarationLine extends AsmLine{


	private String variableName = mOperandIn1;
	public String getVariableName() {
		return variableName;
	}

	private String variableAddressString = "";
	public String getVariableAddressString() {
		return variableAddressString;
	}

	private int variableAddress = 0;
	
	public int getVariableAddress() {
		return variableAddress;
	}

	private String nullString = "00000000";
	
	private ArrayList<Integer> accessList = new ArrayList<Integer>();
	private ArrayList<Integer> readAccessList = new ArrayList<Integer>();
	private ArrayList<Integer> writeAccessList = new ArrayList<Integer>();
	
	//TODO change name of "index" variable to be more reasonable :)
	private int index = -1;
	
	public static int getNumberOfVariables() {
		return numberOfVariables;
	}
	public static int getNumberOfInitLines() {
		return numberOfInitLines;
	}

	private static int numberOfVariables = 0;
	private static int numberOfInitLines = 2;
	
	
	public DeclarationLine(){
		super();
		initializeAsmLine();
		numberOfVariables++;
	}
	public DeclarationLine(String initString, int initIndex) {
		super(initString, initIndex);
		initializeAsmLine();
			
		setVariableAddress();
		numberOfVariables++;
	}
	
	private void setVariableAddress() {
		variableAddress = numberOfVariables;
		String address = Integer.toString(variableAddress, 2);
		int addressLength = address.length();
		StringBuilder sb = new StringBuilder();
		
		sb = sb.append(nullString, 0, 8 - addressLength);
		sb = sb.append(address);
		
		variableAddressString = sb.toString();
	}
	
	public boolean initializeAsmLine() {
		boolean status = true;
		// Define command type and types of all operands
		//TODO: add check functions to ensure that operands
		cmdType = CommandType.dqCommand;
		if (isVariable(mOperandIn1))
			opIn1Type = OperandType.variableOperand;
		else 
			return false;
		
		if (isDelimiter(mOperandIn2))
			opIn2Type = OperandType.delimiterOperand;
		else 
			return false;
		if (isHexadecimal(mOperandOut))
			opOutType = OperandType.hexadecimalOperand;
		else 
			return false;		
		return status;
	}
	
	public void fillLineList(ArrayList<ExecutableLine> executableCode){

		for (ExecutableLine ex : executableCode){
			if (variableName.equalsIgnoreCase(ex.mOperandIn1)){
				readAccessList.add(ex.getIndex());
			}
			if (variableName.equalsIgnoreCase(ex.mOperandIn2)){
				readAccessList.add(ex.getIndex());
			}
			if (variableName.equalsIgnoreCase(ex.mOperandOut)){
				writeAccessList.add(ex.getIndex());
			}
		}		
	}

	
	public void addReadIndex(int lineIndex){
		readAccessList.add(lineIndex);
	}
	
	public void addWriteIndex(int lineIndex){
		writeAccessList.add(lineIndex);
	}
	
	public int getNextLineIndex(){
		return accessList.get(index);
	}
	
	public void printWriteAccessList(){
		System.out.println("write-");
		for (Integer i : writeAccessList){
			System.out.println(i + " ");
		}
	}
	
	public void printReadAccessList(){
		System.out.println("read-");
		for (Integer i : readAccessList){
			System.out.println(i + " ");
		}
	}
	
	
	public String toString(){
		String info = String.format("%s = %s address %d = %s\n", mOperandIn1, mOperandOut, variableAddress, variableAddressString);
		return info;
	}


}

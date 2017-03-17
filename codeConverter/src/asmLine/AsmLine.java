package asmLine;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import commonTypes.CommandType;
import commonTypes.PrecisionType;
import operand.OperandType;


public class AsmLine {
	
	protected String mOperator;
	protected String mOperandIn1;
	protected String mOperandIn2;
	protected String mOperandOut;
	
	public CommandType getCmdType() {
		return cmdType;
	}

	public OperandType getOpIn1Type() {
		return opIn1Type;
	}

	public OperandType getOpIn2Type() {
		return opIn2Type;
	}

	public OperandType getOpOutType() {
		return opOutType;
	}

	public PrecisionType getPrecisionType() {
		return precisionType;
	}

	protected CommandType cmdType = CommandType.illegalCommand;
	protected OperandType opIn1Type = OperandType.illegalOperand;
	protected OperandType opIn2Type = OperandType.illegalOperand;
	protected OperandType opOutType = OperandType.illegalOperand;
	
	PrecisionType precisionType = PrecisionType.illegalPrecision;
	
	private int index = -1;
	
	public String getmOperator() {
		return mOperator;
	}

	public String getmOperandIn1() {
		return mOperandIn1;
	}

	public String getmOperandIn2() {
		return mOperandIn2;
	}

	public String getmOperandOut() {
		return mOperandOut;
	}

	public int getIndex() {
		return index;
	}
	
	public AsmLine(){}
	public AsmLine(String initString, int initIndex)
	{
		getOperatorAndOperands(initString, initIndex);
	}
	
	// Simple fabric to generate asmLines
	public static AsmLine createAsmLine(String initString, int initIndex, PrecisionType precType)
	{
		AsmLine asmLine = new AsmLine();
		String dqMnemonic = "dq";
		asmLine.getOperatorAndOperands(initString, initIndex);
		
		if (asmLine.mOperator.equalsIgnoreCase(dqMnemonic))
		{
			asmLine = new DeclarationLine(initString, initIndex);
			asmLine.cmdType = CommandType.dqCommand;
		}
		else 
		{
			asmLine = new ExecutableLine(initString, initIndex);
		}
		asmLine.precisionType = precType;
		
		return asmLine;
	}

	private boolean getOperatorAndOperands(String initString, int initIndex) {
		String[] initArray = initString.split("[\\s\\xA0]+", 4);
		if (initArray.length == 4)
		{
			mOperator = initArray[0].toLowerCase();
			mOperandIn1 = initArray[1].toLowerCase();
			mOperandIn2 = initArray[2].toLowerCase();
			//TODO add check to ensure mOperandOut doesn't contain spaces
			if (initArray[3].contains(" ") == false)
				mOperandOut = initArray[3].trim();
			else 
			{
				System.out.println("Wrong number of operands");
				return false;
			}
			index = initIndex;
			return true;
		}
		else
		{
			System.out.println("Wrong number of operands");
			return false;
		}
	}
	
	protected boolean checkMnemonic(String mnemonic)
	{
		if (mOperator.equalsIgnoreCase(mnemonic))
			return true;
		else 
			return false;
	}
		
	protected boolean isVariable(String operand)
	{
		Pattern p = Pattern.compile("^[A-Za-z0-9_]+");
		Matcher m = p.matcher(operand);
		
		return m.matches();
	}
	
	protected boolean isPortIndex(String operand){
		Pattern p = Pattern.compile("[0-9]+");
		Matcher m = p.matcher(operand);
		
		return m.matches();
	}
	
	protected boolean isHexadecimal(String operand)
	{
		boolean status = false;
		if (precisionType == PrecisionType.doublePrecision)
			status = isDoubleHexadecimal(operand);
		else if (precisionType == PrecisionType.singlePrecision)
			status = isSingleHexadecimal(operand);
		else 
		{
			;//TODO handle error 			
		}
			
		return status;
	}
	
	protected boolean isDoubleHexadecimal(String operand)
	{
		Pattern p = Pattern.compile("0x([a-fA-F0-9]{16})");
		Matcher m = p.matcher(operand);
		
		return m.matches();
	}
	
	private boolean isSingleHexadecimal(String operand)
	{
		Pattern p = Pattern.compile("0x([a-fA-F0-9]{8})");
		Matcher m = p.matcher(operand);
		
		return m.matches();
	}
	
	protected boolean isDelimiter(String operand)
	{
		if (operand.equalsIgnoreCase("?"))
			return true;
		else
			return false;	
	}
	
	public String toString(){
		String asmLineString = index + "\t" + mOperator + " " + mOperandIn1 + " " + mOperandIn2 + " " + mOperandOut + "\n";
		return asmLineString;
	}
	
	public static void main(String[] args)
	{
		AsmLine myLine = new AsmLine("ADD A f B", 3);
		System.out.println(myLine.mOperator + ": " + myLine.mOperandIn1 + " -" + myLine.mOperandIn2 + " -" + myLine.mOperandOut);
		System.out.println(myLine.isDoubleHexadecimal("0x400800000000000D") + "  " + myLine.isDoubleHexadecimal("0x400800000000000D") + "  " + myLine.isDoubleHexadecimal("0x400800000000000D") );
	}

	public int compareTo(ExecutableLine o) {
		if (this.index > o.getIndex()){
			return +1;
		} else if (this.index < o.getIndex()){
			return -1;
		}
		return 0;
	}

}

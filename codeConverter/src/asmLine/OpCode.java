package asmLine;

import commonTypes.CommandType;
import operand.OperandType;

//TODO: implement Iterable <OpCode> interface 
public class OpCode {
	// Private members
	private String mnemonic;
	private String wbCommand;
	private String exCommand;
	
	private CommandType cmdType = CommandType.illegalCommand;
	private OperandType opIn1Type = OperandType.illegalOperand;
	private OperandType opIn2Type = OperandType.illegalOperand;
	private OperandType opOutType = OperandType.illegalOperand;
	
	public CommandType getCmdType() {
		return cmdType;
	}
	private int aluDelay = 0;
	public String getMnemonic() {
		return mnemonic;
	}
	public String getWbCommand() {
		return wbCommand;
	}
	public String getExCommand() {
		return exCommand;
	}
	public int getAluDelay() {
		return aluDelay;
	}
	public int getPipeDelay() {
		return pipeDelay;
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
	private int pipeDelay = 0;
	private char opIn1TypeChar = 'd';
	private char opIn2TypeChar = 'd';
	private char opOutTypeChar = 'd';
	
	public OpCode(String initString)
	{		
		/* Attention : regex splitting the words including all
		standard whitespace-characters and Unicode non-breaking
		space xA0*/
		String[] initArray = initString.split("[\\s\\xA0]+", 8);
		mnemonic = initArray[0];
		wbCommand = initArray[1];
		exCommand = initArray[2];
		/* For all integer members of opCode class - convert from char */
		aluDelay = Integer.parseInt(initArray[3]);
		pipeDelay = Integer.parseInt(initArray[4]);
		/* For all char members of opCode class - convert from string */
		opIn1TypeChar = initArray[5].charAt(0);
		opIn2TypeChar = initArray[6].charAt(0);
		opOutTypeChar = initArray[7].charAt(0);	
		initializeOpCode();
	}

	
	private void initializeOpCode()
	{
		opIn1Type = defineOperandType(opIn1TypeChar);
		opIn2Type = defineOperandType(opIn2TypeChar);
		opOutType = defineOperandType(opOutTypeChar);
		
		cmdType = defineCommandType();
	}
	
	private CommandType defineCommandType() {
		// TODO handle all command types
		
		if (opIn1Type.equals(OperandType.portIndexOperand) && opIn2Type.equals(OperandType.delimiterOperand) && opOutType.equals(OperandType.variableOperand))
			return CommandType.inputCommand;
		else if (opIn1Type.equals(OperandType.variableOperand) && opIn2Type.equals(OperandType.delimiterOperand) && opOutType.equals(OperandType.portIndexOperand))
			return CommandType.outputCommand;
		else if (opIn1Type.equals(OperandType.variableOperand) && opIn2Type.equals(OperandType.delimiterOperand) && opOutType.equals(OperandType.variableOperand))
			return CommandType.twoOperandCommand;
		else if (opIn1Type.equals(OperandType.variableOperand) && opIn2Type.equals(OperandType.variableOperand) && opOutType.equals(OperandType.variableOperand))
			return CommandType.threeOperandCommand;
		else if (opIn1Type.equals(OperandType.delimiterOperand) && opIn2Type.equals(OperandType.delimiterOperand) && opOutType.equals(OperandType.delimiterOperand))
			return CommandType.wbNoneCommand;
		else 
			return CommandType.illegalCommand;
	}
	
	private OperandType defineOperandType(char opTypeChar){
		switch (opTypeChar)
		{
		case 'c':
			return OperandType.portIndexOperand;
		case 'd':
			return OperandType.delimiterOperand;
		case 'v':
			return OperandType.variableOperand;
		default:
			return OperandType.illegalOperand;
		}
	}
	
	public String getShortInfo(){
		String opCodeString = mnemonic + " " + wbCommand + " " + exCommand + " " + aluDelay + " " + pipeDelay + "\n";
		return opCodeString;
	}
	
	public String getTypeInfo(){
		String opCodeTypeInfo = "\t" + cmdType + " " + opIn1Type + " " + opIn2Type + " " + opOutType;	
		return opCodeTypeInfo;
	}
	
	public String toString(){
		String info = getShortInfo() + getTypeInfo() + "\n----------------------------------------\n";
		return info;
	}
	public static void main(String[] args)
	{
		OpCode myOpCode = new OpCode("in 00000000    00000000    0           3             c   d   v");
		System.out.println(myOpCode.mnemonic + " " + myOpCode.wbCommand + " " + myOpCode.exCommand + " " + myOpCode.aluDelay + " " + myOpCode.pipeDelay + " " + myOpCode.opIn1TypeChar +  " " + myOpCode.opIn2TypeChar + " " + myOpCode.opOutTypeChar);
		myOpCode.initializeOpCode();
		System.out.println(myOpCode.toString() + myOpCode.getTypeInfo());
	}

}

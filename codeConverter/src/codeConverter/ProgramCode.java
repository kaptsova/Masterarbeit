package codeConverter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import asmLine.AsmLine;
import asmLine.DeclarationLine;
import asmLine.ExecutableLine;
import asmLine.OpCode;
import commonTypes.CommandType;
import commonTypes.PrecisionType;
import fileSystem.FileSystem;
import memorySystem.DataMemoryFile;
import memorySystem.ProgramMemoryFile;
import operand.Operand;
import optimiser.OptimizationCriterion;

public class ProgramCode {
	
	private int setupTime = 3;
	public int getSetupTime(){
		return setupTime;
	}

	private ArrayList<AsmLine> programCode = new ArrayList<AsmLine>();
	private ArrayList<DeclarationLine> declarationCode = new ArrayList<DeclarationLine>();
	
	public HashMap<String, Operand> operands = new HashMap<String, Operand>();
	
	private boolean toOptimise= true;
	
	private OptimizationCriterion optimizationCriterion = OptimizationCriterion.undefined;
	
	public boolean isToOptimise(){
		return toOptimise == true;
	}
	
	private void setToOptimise(){
		Scanner sc = new Scanner(System.in);
		System.out.println("Choose if you want to optimise the program code(Y/n)");
		String optimisationMode = sc.nextLine().toLowerCase();
		if (optimisationMode.startsWith("y")){
			toOptimise = true;
			setOptimizationCriterion();
		}
		else {
			toOptimise = false;
		}	
		sc.close();
	}
	
	private void setOptimizationCriterion() {
		Scanner sc = new Scanner(System.in);
		printOptimisationCriterien();
		String optCriterionString = sc.nextLine();
		int optimizationCriterionIndex = Integer.parseInt(optCriterionString);
		
		switch (optimizationCriterionIndex){
		case 1: 
			optimizationCriterion = OptimizationCriterion.maxDelay;
			break;
		case 2:
			optimizationCriterion = OptimizationCriterion.minDelay;
			break;
		case 3:
			optimizationCriterion = OptimizationCriterion.minLevel;
			break;
		case 4:
			optimizationCriterion = OptimizationCriterion.timeDistanceRatio;
			break;
		default:
			optimizationCriterion = OptimizationCriterion.undefined;
			break;
		}
		
		
	}

	public OptimizationCriterion getOptimizationCriterion() {
		return optimizationCriterion;
	}

	private void printOptimisationCriterien() {
		System.out.println("Choose the optimization criterion");
		System.out.println("\t - 1: Max Delay");
		System.out.println("\t - 2: Min Delay");
		System.out.println("\t - 3: Min Level");
		System.out.println("\t - 4: TimeDistanceRatio");
	}

	public ArrayList<DeclarationLine> getDeclarationCode() {
		return declarationCode;
	}

	private ArrayList<ExecutableLine> executableCode = new ArrayList<ExecutableLine>();
	public ArrayList<ExecutableLine> getExecutableCode() {
		return executableCode;
	}
	private ArrayList<OpCode> opCodeList = new ArrayList<OpCode>();
	
	DataMemoryFile datamem = new DataMemoryFile();
	ProgramMemoryFile progmem = new ProgramMemoryFile();
	

	
	public ProgramCode() {
		// TODO Auto-generated constructor stub
	}
	
	void setAdditionalParameters(){
		setToOptimise();
	}
	
	public void readProgramCode(final String programCodePath, PrecisionType prType) {

		// Read the program code from file and fill the 
		HashMap <Integer, String> programCodeStrings = new HashMap <Integer, String>();
		programCodeStrings = Parser.readFromFile(programCodePath);
		
		
		for (Map.Entry<Integer, String> e : programCodeStrings.entrySet())
        {
	    	AsmLine instance = AsmLine.createAsmLine(e.getValue(), e.getKey(), prType);
	    	if (instance.getCmdType().equals(CommandType.dqCommand)){
	    		DeclarationLine dq = (DeclarationLine)instance;
	    		if (dq.isAlreadyDefined(declarationCode)){
	    			// TODO handle error
	    			System.out.println("already found");
	    		} else {
		    		declarationCode.add((DeclarationLine) instance);
		    		addOperandToMap(instance);
	    		}
	    	}
	    	else {
	    		executableCode.add((ExecutableLine) instance);
	    	}
    		
	    	programCode.add(instance);	  
	    }

	}
	

	private void addOperandToMap(AsmLine instance) {
		DeclarationLine dq = (DeclarationLine) instance;
		String name = dq.getVariableName();
		String addressString = dq.getVariableAddressString(); 
		int addressIndex = dq.getVariableAddress();
		Operand operand = new Operand(addressIndex, addressString, name);
		operands.put(name, operand);
	}
	
	public void readOperationCodes(final String opCodesPath) 
	{
		HashMap <Integer, String> opCodeStrings = new HashMap <Integer, String>();
		opCodeStrings = Parser.readFromFile(opCodesPath);
		
		for (Map.Entry<Integer, String> e : opCodeStrings.entrySet())
        {
	    	OpCode instance = new OpCode(e.getValue());
	    	opCodeList.add(instance);
	    	//System.out.println(instance.toString());
	    
	    }
	}
	
	public void handleProgramCode(FileSystem fileSystem, PrecisionType precType){		
		boolean executableCodeOk = checkExecutableCode();
		if (!executableCodeOk)
			return;
		
		
		datamem.initializeMemoryFile(this, precType);
		datamem.fillMemoryFile(fileSystem.getDataMemoryFilePath());
		
		progmem.initializeMemoryFile(this, precType);
		progmem.fillMemoryFile(fileSystem.getProgMemoryFilePath());
	}
	
	private boolean checkExecutableCode(){
		for (ExecutableLine ex: executableCode){
			boolean exLineOk = ex.checkAssemblerLine(this, opCodeList, declarationCode);
			if (exLineOk != true)
				return false;
			//System.out.println(ex.getStatusInfo());
		}
		return true;
	}

		
	public void printDeclarations(){		
		System.out.println("All the declarations");
		
		for (DeclarationLine dq : declarationCode){
			System.out.print("Dq " + "\t" + dq.toString());
		}
	}
	
	public void printExecutable(){
		System.out.println("All executable code");
		for (ExecutableLine exec : executableCode){
			System.out.print("Ex " + "\t" + exec.toString());
		}
	}
	
	public void printOpCodes(){
		System.out.println("All operation codes");
		for (OpCode opCode : opCodeList){
			System.out.print("opC\t" + opCode.toString());
		}
	}
	
	public String toString(){
		StringBuilder str = new StringBuilder();
		for (AsmLine asmLine : programCode){
			str.append(asmLine.toString());
		}
		return str.toString();
	}
	
	public void printInfo(){
		System.out.println(toString());
	}
	
	
	
	public void clearExecutable(){
		executableCode.clear();
	}
	
	public void printAllInfo(){
		printDeclarations();
		printExecutable();
		printOpCodes();
	}
	


	public static void main(String [] args){		
		ProgramCode pcode = new ProgramCode();
		pcode.readProgramCode("tests\\dependent.txt", PrecisionType.doublePrecision);
		pcode.readOperationCodes("tests\\opcodes_dbl.txt");
		pcode.toString();
		
		
		pcode.printDeclarations();
		pcode.printExecutable();
		pcode.printOpCodes();
				
	}
	

	
}

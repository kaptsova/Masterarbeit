package memorySystem;

import java.util.ArrayList;


import asmLine.ExecutableLine;
import asmLine.ExecutableLine.Operator;
import asmLine.ProgramCode;
import commonTypes.PrecisionType;
import optimiser.OptimisationCriterion;
import optimiser.Optimiser;

// TODO correct visibility levels

public class ProgramMemoryFile extends MemoryFile{

	ArrayList<String> programMemoryList = new ArrayList<String>();	
	Optimiser optimiser = null;
	private OptimisationCriterion optCriterion = OptimisationCriterion.minDelay;
	
	public ProgramMemoryFile() {
		// TODO Auto-generated constructor stub
	}
		
	@Override
	public void setPrecisionType(PrecisionType precType) {
		// currently only 1 precision type is available
		int numberOfDigits = 9;
		precisionTypeString = String.format("%08X", numberOfDigits);
	}

	@Override
	public void fillMemoryList(ProgramCode pcode, PrecisionType prType) {
		addWbNones(pcode);
		
		if (pcode.isToOptimise()){
			fillMemoryList_optimised(pcode, prType);
		} 
		else {
			fillMemoryList_notOptimised(pcode, prType);
		}	
	}

	private void addWbNones(ProgramCode pcode) {
		// Add noOps
		for (int i = 0; i < pcode.getSetupTime(); i++){
			doNothing();
		}
	}
	
	
	private void fillMemoryList_optimised(ProgramCode pcode, PrecisionType prType){
		optimiser = Optimiser.createOptimiser(optCriterion);
		linesList.addAll(optimiser.optimiseProgramCode(pcode));
	}
	
	private void doNothing() {
		linesList.add("003800000");		
	}
		
	private void fillMemoryList_notOptimised(ProgramCode pcode, PrecisionType prType){
		
		for (ExecutableLine ex: pcode.getExecutableCode()){
			Operator operator = ex.getOperator();
			// Calculate
			String calcLine = operator.getCalculationOperation();
			linesList.add(calcLine);
			// Add nops
			for (int i = 0; i < operator.getAluDelay() - 1; i++){
				doNothing();
			}
			String wbLine = operator.getWriteBackOperation();
			linesList.add(wbLine);
			
			// Add nops
			for (int i = 0; i < operator.getPipeDelay(); i++){
				doNothing();
			}
		}	
	}
	

}

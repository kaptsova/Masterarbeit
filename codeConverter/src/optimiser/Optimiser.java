package optimiser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import asmLine.ExecutableLine;
import asmLine.ExecutableLine.Operator;
import asmLine.ProgramCode;
import asmLine.ResultForwardingItem;
import commonTypes.CommandType;
import commonTypes.ExecutionStatus;

public abstract class Optimiser {
	
	ArrayList<ExecutableLine> readyNodes = new ArrayList<ExecutableLine>();
	
	ArrayList<ExecutableLine> toBeExecutedNodes = new ArrayList<ExecutableLine>();
	ArrayList<String> linesList = new ArrayList<String>();
	
	HashMap<Integer, ExecutableLine> writeBackMap = new HashMap <Integer,ExecutableLine>();
	HashMap<Integer, ExecutableLine> endOfOpMap = new HashMap <Integer, ExecutableLine>();
	HashMap <Integer, ExecutableLine> checkList = new HashMap<Integer, ExecutableLine>();
	
	ArrayList<ExecutableLine> executableCode = null;
	ArrayList<ResultForwardingItem> resultForwardingList = new ArrayList<ResultForwardingItem>();
	
	int numberOfCommandsExecuted = 0;
	int numberOfCommandsToExecute = 0;

	public Optimiser() {
		// TODO Auto-generated constructor stub
	}
	
	
	private HashMap<Integer, ExecutableLine> findFirstNodesToBeChecked(){
		for (int i = 0; i < executableCode.size(); i++){
			ExecutableLine ex = executableCode.get(i);
			int levelInGraph = ex.getLevelOfGraph();
			int firstLevel = 1;
			int nodeIndex = ex.getNodeIndex();
			if (levelInGraph == firstLevel){
				checkList.put(nodeIndex, ex);
			}
		}
		return checkList;
	}
	
	//TODO return a list of ready-to start executable commands
	private ArrayList<ExecutableLine> findReadyNodes(ArrayList<ExecutableLine> executableCode){
		ArrayList<ExecutableLine> readyCommands = new ArrayList<ExecutableLine>();
		
		
		for (Integer i : checkList.keySet()){
			ExecutableLine ex = checkList.get(i);
			ex.setExecStatus(executableCode);
			if (ex.getExecStatus() == ExecutionStatus.permittedStatus){
				readyCommands.add(ex);
			}
		}
		return readyCommands;		
	}
	
	public void constructGraph(ArrayList<ExecutableLine> executableCode){
		constructConnectionsBetweenNodes(executableCode);
		calculateLevelsInGraph(executableCode);
	}
	
	public void constructConnectionsBetweenNodes(ArrayList<ExecutableLine> executableCode){
		for (ExecutableLine ex : executableCode){			
			if (ex.getCmdType() != CommandType.outputCommand){
				ex.setFollowers(executableCode);
			}
		}	
		for (ExecutableLine ex : executableCode){			
			if (ex.getCmdType() != CommandType.outputCommand){
				ex.setReadNodesToBeExecuted(executableCode);
			}
		}	
		
			
	}
	
	public void calculateLevelsInGraph(ArrayList<ExecutableLine> executableCode){
		for (ExecutableLine ex : executableCode){
			ex.setLevelInGraph(executableCode);
		}
	}
	
	public ArrayList<String> optimiseProgramCode(ProgramCode pcode){
		
		executableCode = pcode.getExecutableCode();
		numberOfCommandsToExecute = executableCode.size();

		int takt = 0;
		
		constructGraph(pcode.getExecutableCode());
		
		recognizeBypass();
		System.out.println("RF LIST" +resultForwardingList);
		findFirstNodesToBeChecked();
			
		while (numberOfCommandsExecuted < numberOfCommandsToExecute){
			readyNodes = findReadyNodes(executableCode);

			boolean taktOccupiedWithWb = isTaktOccupiedWithWb(takt);
			boolean resultWrittenBack = isResultWrittenBack(takt);
			boolean executionInAluStarted = false;
			
			if (taktOccupiedWithWb){
				startWriteBack(takt);
			}
			else {
				executionInAluStarted = chooseAndStartNode(takt);
			}
			
			if (!executionInAluStarted)
				doNothing();
			
			
			if (resultWrittenBack){
				endExecution(takt);
				numberOfCommandsExecuted++;
				System.out.println(numberOfCommandsExecuted + " of " + numberOfCommandsToExecute);
			}
			
						
			takt++;
			
			//-------------------------------------------------------
			// For test-purposes only
			// TODO: remove
			
			if (takt > 100)
				break;
		}
		
		return linesList;
	}


	private void doNothing() {
		linesList.add("003800000");		
	}
	
	private boolean chooseAndStartNode(int takt){
		ExecutableLine ex = chooseNode();
		boolean success = startExecutionInAlu(ex, takt);
		return success;
	}

	private boolean startExecutionInAlu(ExecutableLine ex, int takt) {	
		if (ex != null) {
			Operator operator = ex.getOperator();
			boolean taktOccupiedWithWb = isTaktOccupiedWithWb(takt + operator.getAluDelay());	
			
			
			if (!taktOccupiedWithWb){
				ex.start();
				
				linesList.add(operator.getCalculationOperation());
				checkList.remove(ex.getNodeIndex());

				if ((ex.getCmdType() == CommandType.outputCommand) || (ex.getCmdType() == CommandType.inputCommand)) {
					endOfOpMap.put(takt + operator.getPipeDelay(), ex);
				}
				else {
					writeBackMap.put(takt + operator.getAluDelay(), ex);
				}
				System.out.println("started " + ex);
				return true;
			}
			else {
				return false;
			}
		}
		else {
			return false;
		}
	}
	
	private void startWriteBack(int takt){
		ExecutableLine ex = writeBackMap.get(takt);
		Operator operator = ex.getOperator();
		
		if (ex.canBeForwarded()){
			boolean rf = tryResultForwarding(ex, takt-1);
			if (rf)
				return;
		} else {
			addDescendantsToCheckList(ex);	
		}

		ex.startWriteBack();
					
		linesList.add(operator.getWriteBackOperation());	
		endOfOpMap.put(takt + operator.getPipeDelay() - 1, ex);
	}


	private boolean tryResultForwarding(ExecutableLine ex, int takt) {
		boolean success = true;
		
		ExecutionStatus status = ex.getExecStatus();
		ex.startResultForwarding();
		
		ExecutableLine nextEx = ex.descendants.get(0);
		int nodeIndex = nextEx.getNodeIndex();
		checkList.put(nodeIndex, nextEx);
		nextEx.setExecStatus(executableCode);
		if (nextEx.getExecStatus() == ExecutionStatus.permittedStatus){
			System.out.println("heheu");
			startExecutionInAlu(nextEx, takt);
			ex.endExecution();
			numberOfCommandsExecuted++;
			return success;
		}
		else {
			success = false;
			ex.setExecStatus(status);
			return false;
		}
		
	}
	
	private void endExecution(int takt) {
		ExecutableLine ex = endOfOpMap.get(takt);
		addDescendantsToCheckList(ex);	
		
		ex.endExecution();
		

	}


	private void addDescendantsToCheckList(ExecutableLine ex) {
		int lowerBound = ex.getNodeIndex();		
		int upperBound = ex.getUpperBound(executableCode);
		
		ArrayList<Integer> readAccessList = ex.getOperandOut().getReadAccessList();
		for (int i = lowerBound; i <= upperBound; i++){
			if (readAccessList.contains(i)){
				ExecutableLine follower = executableCode.get(i-1);
				if (!checkList.containsKey(i)){
					checkList.put(i, follower);
				}
			}
		}		
	}
	
	private boolean isResultWrittenBack(int takt){
		if (endOfOpMap.containsKey(takt))			
			return true;
		else 
			return false;
	}

	private boolean isTaktOccupiedWithWb(int takt) {
		if (writeBackMap.containsKey(takt))
			return true;
		else 
			return false;
		
	}
	
	public static Optimiser createOptimiser(OptimisationCriterion initOptCriterion) {
		OptimisationCriterion optCriterion = initOptCriterion;	
		Optimiser opt = null;
		switch (optCriterion){
			case maxDelay :	
				opt = new Optimiser_maxDelay();
				break;
			case minDelay:
				opt = new Optimiser_minDelay();
				break;
			case minLevel:
				opt = new Optimiser_minLevel();
				break;
			case timeDistanceRatio:
				opt = new Optimiser_TDR();
				break;
			default:
				opt = new Optimiser_maxDelay();
				break;
		}
		return opt;
	}
	
	public abstract ExecutableLine chooseNode();
	
	public void recognizeBypass(){
		for (ExecutableLine ancestor : executableCode){
			ResultForwardingItem rf = null;
			if (ancestor.canBeForwarded()){
				ExecutableLine descendant = ancestor.descendants.get(0);
				while (descendant.canAcceptForwarding()){
					if (rf == null){
						rf = new ResultForwardingItem(ancestor, descendant);	
					} else {
						rf.addExecutableLine(descendant);
					}
					ancestor = descendant;
					if (ancestor.canBeForwarded()){
						ancestor.setResultForwarding(true);	
						
						// TODO change it during optimization not here
						ancestor.setRfCalculationOperation();
						
						descendant = ancestor.descendants.get(0);
					} else {
						break;
					}
					
				}
				if (rf != null){
					resultForwardingList.add(rf);
				}
			}
		}
	}

}

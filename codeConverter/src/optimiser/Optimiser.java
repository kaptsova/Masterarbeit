package optimiser;

import java.util.ArrayList;
import java.util.HashMap;

import asmLine.ExecutableLine;
import asmLine.ExecutableLine.Operator;
import asmLine.IOPort;
import asmLine.ProgramCode;
import asmLine.ResultForwardingItem;
import commonTypes.CommandType;
import commonTypes.ExecutionStatus;

public abstract class Optimiser {
	
	ArrayList<ExecutableLine> readyNodes = new ArrayList<ExecutableLine>();
	
	ArrayList<ExecutableLine> toBeExecutedNodes = new ArrayList<ExecutableLine>();
	ArrayList<String> linesList = new ArrayList<String>();
	// TODO implement multiple IO-Ports both in compiler and in ViSARD
	IOPort port = new IOPort();
	
	HashMap<Integer, ExecutableLine> startOpMap = new HashMap<Integer, ExecutableLine>();
	HashMap<Integer, ExecutableLine> writeBackMap = new HashMap <Integer,ExecutableLine>();
	HashMap<Integer, ExecutableLine> endOfOpMap = new HashMap <Integer, ExecutableLine>();
	HashMap <Integer, ExecutableLine> checkMap = new HashMap<Integer, ExecutableLine>();
	
	ArrayList<ExecutableLine> executableCode = null;
	
	HashMap<ExecutableLine, ResultForwardingItem> resultForwardingMap = new HashMap<ExecutableLine, ResultForwardingItem>();
	
	int numberOfCommandsExecuted = 0;
	int numberOfCommandsToExecute = 0;

	public Optimiser() {
		// TODO Auto-generated constructor stub
	}
	
	public void initIOPort(ArrayList<ExecutableLine> executableCode){
		for (ExecutableLine ex: executableCode){
			if (ex.getCmdType() == CommandType.inputCommand) {
				port.queue.add(ex);
			}
			if (ex.getCmdType() == CommandType.outputCommand) {
				port.queue.add(ex);
			}
		}
	}
	
	
	private HashMap<Integer, ExecutableLine> findFirstNodesToBeChecked(){
		for (int i = 0; i < executableCode.size(); i++){
			ExecutableLine ex = executableCode.get(i);
			int levelInGraph = ex.getLevelOfGraph();
			int firstLevel = 1;
			int nodeIndex = ex.getNodeIndex();
			if (levelInGraph == firstLevel){
				checkMap.put(nodeIndex, ex);
			}
		}
		return checkMap;
	}
	
	//TODO return a list of ready-to start executable commands
	private ArrayList<ExecutableLine> findReadyNodes(ArrayList<ExecutableLine> executableCode){
		ArrayList<ExecutableLine> readyCommands = new ArrayList<ExecutableLine>();
		
		
		for (Integer i : checkMap.keySet()){
			ExecutableLine ex = checkMap.get(i);
			ex.setExecStatus(executableCode, port);
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
		
		for (ExecutableLine ex : executableCode){
			ex.getOperandsInfo();
		}
		
		initIOPort(executableCode);
		
		recognizeBypass();

		findFirstNodesToBeChecked();
			
		while (numberOfCommandsExecuted < numberOfCommandsToExecute){
			System.out.println("takt: " + takt);
			readyNodes = findReadyNodes(executableCode);

			boolean taktOccupiedWithWb = isTaktOccupiedWithWb(takt);
			boolean resultWrittenBack = isResultWrittenBack(takt);
			boolean executionInAluStarted = false;
			boolean writeBackStarted = false;
			
			if (taktOccupiedWithWb){
				writeBackStarted = startWriteBack(takt);
				System.out.println(" - wb started = " + writeBackStarted);
			}
			else {
				executionInAluStarted = chooseAndStartNode(takt);
			}
			
			if (!writeBackStarted && !executionInAluStarted && !taktOccupiedWithWb)
				doNothing();
			
			
			if (resultWrittenBack){
				endExecution(takt);
				numberOfCommandsExecuted++;
				System.out.println(numberOfCommandsExecuted + " of " + numberOfCommandsToExecute);
			}
			
			System.out.println(" - rf" + resultForwardingMap.toString() + "\n - ready " + readyNodes);
			
			takt++;
			
			//-------------------------------------------------------
			// For test-purposes only
			// TODO: remove			
			if (takt > 300)
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
				checkMap.remove(ex.getNodeIndex());

				if ((ex.getCmdType() == CommandType.outputCommand) || (ex.getCmdType() == CommandType.inputCommand)) {
					endOfOpMap.put(takt + operator.getPipeDelay(), ex);
				}
				else {
					int writeBackTakt = takt + operator.getAluDelay();
					writeBackMap.put(writeBackTakt, ex);
					System.out.println(" - will end at " + writeBackTakt);
				}
				startOpMap.put(takt, ex);
				System.out.println(" - started " + ex.getNodeIndex());
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
	
	private boolean startWriteBack(int takt){
		ExecutableLine ex = writeBackMap.get(takt);
		Operator operator = ex.getOperator();
		
		System.out.println(" - try to wb " + ex);
		
		if (resultForwardingMap.containsKey(ex)){
			System.out.println(" -     contains key for rf" + ex);
			int size = resultForwardingMap.get(ex).getExLines().size(); 
			if (size > 1) {
				System.out.println(" -     size is ok");
				boolean rf = tryResultForwarding(ex, takt-1);
				if (rf) {
					System.out.println(" -     result forwarding started");
					return true;
				}
			}
			else {
				resultForwardingMap.remove(ex);
			}
		} 
		//else {
			System.out.println(" - writeback" + ex);
			addDescendantsToCheckList(ex);	
		//}

		ex.startWriteBack();
					
		linesList.add(operator.getWriteBackOperation());	
		endOfOpMap.put(takt + operator.getPipeDelay() - 1, ex);
		return true;
	}

	// TODO try to implement delay up to 3 takts, if takt is occupied
	// TODO test if result forwarding reg is occupied: add arraylists for each rf reg
	private boolean tryResultForwarding(ExecutableLine ex, int takt) {
		boolean success = true;
		
		ExecutionStatus status = ex.getExecStatus();
		ex.startResultForwarding();
		
		ExecutableLine nextEx = ex.descendants.get(0);
		int nodeIndex = nextEx.getNodeIndex();
		checkMap.put(nodeIndex, nextEx);
		nextEx.setExecStatus(executableCode, port);
		
		if ((nextEx.getExecStatus() == ExecutionStatus.permittedStatus) && (!isTaktOccupied(takt))){
			startExecutionRf(ex, nextEx, takt);
			numberOfCommandsExecuted++;
			updateRfMap(ex, nextEx);
			System.out.println(" - bypass " + ex + " to " + nextEx );
			return success;
		}
		else {
			System.out.println(" - failed -" + ex + nextEx + "taktOccupied" + !isTaktOccupied(takt) + "\nstatus" + nextEx.getExecStatus());
			
			updateRfMap(ex, nextEx);
			success = false;
			ex.setExecStatus(status);
			nextEx.setExecStatus(executableCode, port);
			return false;
		}
		
	}

	
	private void updateRfMap(ExecutableLine ex, ExecutableLine nextEx) {
		ResultForwardingItem rf = resultForwardingMap.get(ex);
		rf = rf.deleteFirst();
		resultForwardingMap.remove(ex);
		resultForwardingMap.put(nextEx, rf);
	}


	private void startExecutionRf(ExecutableLine ex, ExecutableLine nextEx, int takt) {
		nextEx.setRfCalculationOperation(ex);
		startExecutionInAlu(nextEx, takt);
		ex.endExecution();
	}
	
	private void endExecution(int takt) {
		ExecutableLine ex = endOfOpMap.get(takt);
		addDescendantsToCheckList(ex);
		
		if ((ex.getCmdType() == CommandType.outputCommand) || (ex.getCmdType() == CommandType.inputCommand)) {
			port.queue.remove();
		}
		System.out.println(" - ended " + ex);
		ex.endExecution();
		

	}


	private void addDescendantsToCheckList(ExecutableLine ex) {
		int lowerBound = ex.getNodeIndex();		
		int upperBound = ex.getUpperBound(executableCode);
		
		ArrayList<Integer> readAccessList = ex.getOperandOut().getReadAccessList();
		for (int i = lowerBound; i <= upperBound; i++){
			if (readAccessList.contains(i)){
				ExecutableLine follower = executableCode.get(i-1);
				if (!checkMap.containsKey(i)){
					checkMap.put(i, follower);
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
	
	private boolean isTaktOccupiedWithOperation(int takt) {
		if (startOpMap.containsKey(takt))
			return true;
		else 
			return false;		
	}
	
	private boolean isTaktOccupied(int takt){
		return (isTaktOccupiedWithWb(takt)&& isTaktOccupiedWithOperation(takt));
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
		for (ExecutableLine ex : executableCode){
			ResultForwardingItem rf = null;
			ExecutableLine ancestor = ex;
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
						
						descendant = ancestor.descendants.get(0);
					} else {
						break;
					}
					
				}
				if (rf != null){
					resultForwardingMap.putIfAbsent(ex, rf);
				}
			}
		}
	}

}

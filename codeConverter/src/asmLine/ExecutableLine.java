package asmLine;

import java.util.ArrayList;
import java.util.HashMap;

import asmLine.ExecutableLine.Operator;
import commonTypes.CommandType;
import commonTypes.ExecutionStatus;
import errorHandler.ErrorMessage;
import errorHandler.ErrorType;
import operand.Operand;
import operand.OperandType;

public class ExecutableLine extends AsmLine{
	
	ExecutionStatus execStatus = ExecutionStatus.illegalStatus;
	private boolean asmLineOk = false;
	
	private Operand operandIn1;
	private Operand operandIn2;
	private Operand operandOut;
	
	public int leftAncestorIndex = 0;
	public int rightAncestorIndex = 0;
	
	
	public ExecutableLine leftAncestor = null;
	public ExecutableLine rightAncestor = null;
	
	public ArrayList<Integer> readNodesToBeExecuted = new ArrayList<Integer>();
	
	public ArrayList<ExecutableLine> descendants = new ArrayList<ExecutableLine>();
	
	private String [] ariphmeticOperators = {"add", "sub", "addsub", "mul", "div"};
	
	private boolean isResultForwarding = false;
	
	public void setResultForwarding(boolean isResultForwarding) {
		this.isResultForwarding = isResultForwarding;
	}
	
	private boolean isLeftOperandRf = false;
	private boolean isRightOperandRf = false;

	private int leftOperandRf_delay = 0;	
	private int rightOperandRf_delay = 0;

	
	private int levelInGraph = 0;
	private int nodeIndex = 0;
	public int getNodeIndex() {
		return nodeIndex;
	}

	private static int numberOfNodes = 0;
	
	public int getLevelOfGraph() {
		return levelInGraph;
	}

	private Operator operator;
	public Operator getOperator(){
		return operator;
	}
	
	private static String filePath;
	
	public static void setFilePath(String initFilePath) {
		filePath = initFilePath;
	}	
	public ExecutableLine(){
		super();
	}

	public ExecutableLine(String initString, int initIndex) {
		super(initString, initIndex);
		
		// nodeIndex == 0 is abstraction for input node
		nodeIndex = numberOfNodes + 1;
		numberOfNodes++;
		
		execStatus = ExecutionStatus.notPermittedStatus;
		
		operandIn1 = new Operand(mOperandIn1);
		operandIn2 = new Operand(mOperandIn2);
		setOperandOut(new Operand(mOperandOut));
		operator   = new Operator(mOperator);
		

	}
	
	public void setCmdType(){
		if (operator.mnemonic.equals("in")) {
			cmdType = CommandType.inputCommand;
		}
		else if (operator.mnemonic.equals("out")){
			cmdType = CommandType.outputCommand;
		}
		else {
			cmdType = CommandType.threeOperandCommand;
		}
	}
	
	public void setExecStatus(ExecutionStatus status){
		if (execStatus == ExecutionStatus.illegalStatus){
			execStatus = ExecutionStatus.notPermittedStatus;
		}		
		execStatus = status;
	}
	
	public ExecutionStatus getExecStatus(){
		return execStatus;		
	}
	
	public boolean checkAssemblerLine(ProgramCode pcode, ArrayList<OpCode> opCodeList, ArrayList<DeclarationLine> declarations)
	{
		// Status variables
		boolean mnemonicOk = false;
		boolean operandTypesOk = false;
		boolean operandVariablesOk = false;

		// Check if mnemonic is declared among available		
		OpCode foundOpCode = null;
		for (OpCode opCode :  opCodeList)
		{
			String mnemonic = opCode.getMnemonic();
			mnemonicOk = checkMnemonic(mnemonic);
			// If mnemonic is found in the list -> save it
			if (mnemonicOk)
			{
				foundOpCode = opCode;
				setOperator(opCode);
				setCmdType();
				break;
			}
		}
		// If mnemonic is wrong -> assembler line is wrong
		if (mnemonicOk == false)
			return false;
		else {
			operandTypesOk = checkOperandTypes(foundOpCode);
		}
		
		// If (at least) operand type is wrong -> assembler line is wrong
		if ( operandTypesOk == false)
			return false;
		else
			//operandVariablesOk = checkOperandVariables(declarations);
			operandVariablesOk = checkOperandValues(pcode.operands);
		

		asmLineOk = mnemonicOk && operandTypesOk && operandVariablesOk;
		if (asmLineOk)
			setOperator(foundOpCode);
		
		return asmLineOk;
	}

	private void setOperator(OpCode opCode) {
		operator.wbCommand = opCode.getWbCommand();
		operator.exCommand = opCode.getExCommand();
		operator.aluDelay = opCode.getAluDelay();
		operator.pipeDelay = opCode.getPipeDelay();
		operator.setOperations();
		
	}
	
	/*Check if actual operand types coincide with the declared
	 * @param opCode contains information about declared types of operands for given mnemonic
	 */
	private boolean checkOperandTypes(OpCode opCode)
	{
		boolean operandTypesOk = true;
		// Define actual operand types	
		defineOperandType(opCode.getOpIn1Type(), operandIn1);
		defineOperandType(opCode.getOpIn2Type(), operandIn2);
		defineOperandType(opCode.getOpOutType(), getOperandOut());
				
		if (!typesFound())
			operandTypesOk = false;
					
		return operandTypesOk;
	}
		
	private boolean defineOperandType(OperandType opCodeOperandType, Operand operand){
		
		boolean status = false;
		// check if declared operand type coincide with actual 
		switch (opCodeOperandType)
		{
		case delimiterOperand:
			status = isDelimiter(operand.getName());		
			break;
		case variableOperand:
			status = isVariable(operand.getName());	
			break;
		case hexadecimalOperand:
			status = isHexadecimal(operand.getName());
			break;
		case portIndexOperand:
			status = isPortIndex(operand.getName());
			break;
		default:
			status = false;
		}
		if (status == true){
			operand.setOpType(opCodeOperandType);
			return true;
		}
		else{
			operand.setOpType(OperandType.illegalOperand);
			//TODO handle errors somewhere file paths are known
			ErrorMessage errMsg = new ErrorMessage(ErrorType.wrongOperandTypeError, getIndex(), filePath);
			errMsg.printToFile();
			return false;
		}						
	}
	
	// TODO handle error in errorMessage.java class
		private boolean typesFound(){
			if (operandIn1.getOpType().equals(OperandType.illegalOperand)){
				ErrorMessage errMsg = new ErrorMessage(ErrorType.wrongOpIn1TypeError, getIndex(), filePath);
				errMsg.printToFile();
				return false;
			}
			if (operandIn2.getOpType().equals(OperandType.illegalOperand)){
				ErrorMessage errMsg = new ErrorMessage(ErrorType.wrongOpIn2TypeError, getIndex(), filePath);
				errMsg.printToFile();
				return false;
			}
			if (getOperandOut().getOpType().equals(OperandType.illegalOperand)){
				ErrorMessage errMsg = new ErrorMessage(ErrorType.wrongOpOutTypeError, getIndex(), filePath);
				errMsg.printToFile();
				return false;
			}
			
			return true;
		}
	
	
	
	private boolean checkOperandValues(HashMap<String, Operand> operands){
		boolean operandValuesOk = true;
		boolean read = true;
		boolean write = false;
		
		boolean opIn1Found = true;
		boolean opIn2Found = true;
		boolean opOutFound = true;
		
		operandIn1 = findOperatorValue(operandIn1, operands, read);
		operandIn2 = findOperatorValue(operandIn2, operands, read);
		setOperandOut(findOperatorValue(getOperandOut(), operands, write));
		
		if (operandIn1 == null){
			ErrorMessage errMsg = new ErrorMessage(ErrorType.opIn1NotFoundError, getIndex(), filePath);
			errMsg.printToFile();
			opIn1Found = false;
		}
		if (operandIn2 == null){
			ErrorMessage errMsg = new ErrorMessage(ErrorType.opIn2NotFoundError, getIndex(), filePath);
			errMsg.printToFile();
			opIn2Found = false;
		}
		if (getOperandOut() == null){
			ErrorMessage errMsg = new ErrorMessage(ErrorType.opOutNotFoundError, getIndex(), filePath);
			errMsg.printToFile();
			opOutFound = false;
		}
			
		
		operandValuesOk = opIn1Found && opIn2Found && opOutFound;
		
		return operandValuesOk;		
	}

	private Operand findOperatorValue(Operand operand, HashMap<String, Operand> operands, boolean mode) {
		
		String operandName = operand.getName().toLowerCase();
		boolean read = true;
		boolean write = false;
		
		Operand tempOperand = new Operand();
		if ((tempOperand = operands.get(operandName)) == null) {
			if (operand.getOpType() == OperandType.variableOperand){
				return null;
			}
			else 
				return operand;
		}	
		else {
			if (mode == read)
				tempOperand.addReadIndex(nodeIndex);
			else if (mode == write)
				tempOperand.addWriteIndex(nodeIndex);
			return tempOperand;
		}
	}
	
	
	public String getStatusInfo(){
		return mOperator + " found = " + asmLineOk + " " + operandIn1.getOpType() + " " + operandIn2.getOpType()  + " " + operandIn2.getOpType() ; 
	}
	
	public String getOperandsInfo(){
		StringBuilder str = new StringBuilder();
		System.out.println("--------------------------------");
		System.out.println(operator.mnemonic + " index: " + nodeIndex + " level: " + levelInGraph);
		
		operandIn1.printInfo();
		operandIn2.printInfo();
		getOperandOut().printInfo();
		
		return str.toString();
	}
	
	
	public void setFollowers(ArrayList<ExecutableLine> executableCode){
		int lowerBound = nodeIndex;		
		int upperBound = getUpperBound(executableCode);
		
		ArrayList<Integer> readAccessList = getOperandOut().getReadAccessList();
		for (int i = lowerBound; i <= upperBound; i++){
			if (readAccessList.contains(i)){
				ExecutableLine ex = executableCode.get(i-1);
				if (ex.mOperandIn1.equalsIgnoreCase(this.mOperandOut)){
					ex.leftAncestorIndex = nodeIndex;
				}
				if (ex.mOperandIn2.equalsIgnoreCase(this.mOperandOut)){
					ex.rightAncestorIndex = nodeIndex;
				}
				descendants.add(ex);
			}
		}	
		System.out.println("Descendants of"+ this.toString() + ":" + descendants.toString());
	}
	
	
	public int getUpperBound(ArrayList<ExecutableLine> executableCode) {
		ArrayList<Integer> writeAccessList = getOperandOut().getWriteAccessList();	
		

		int upperBound = 0;
		if (writeAccessList.size() > 1){
			upperBound = getUpperBound_multipleValues();
		}
		else {
			upperBound = executableCode.size();
		}
		return upperBound;
	}

	private int getUpperBound_multipleValues() {
 		ArrayList<Integer> writeAccessList = getOperandOut().getWriteAccessList();	
		
		int startIndex = writeAccessList.indexOf(nodeIndex);
		int endIndex = startIndex;
		
		int nextNodeIndex = 0;
		
		
		while (endIndex < writeAccessList.size())
		{
			nextNodeIndex = writeAccessList.get(endIndex);
			if (nextNodeIndex != nodeIndex)
				break;
			endIndex++;
		}
		return nextNodeIndex;
	}
	
	public void setLevelInGraph(ArrayList<ExecutableLine> executableCode) {
		int levelOfLeftAncestor = 0;
		int levelOfRightAncestor = 0;
		
		if (leftAncestorIndex > 0){
			leftAncestor = executableCode.get(leftAncestorIndex-1);
			levelOfLeftAncestor = leftAncestor.levelInGraph;
		}

		if (rightAncestorIndex > 0){
			rightAncestor = executableCode.get(rightAncestorIndex-1);
			levelOfRightAncestor = rightAncestor.levelInGraph;
		}
		
		levelInGraph = max(levelOfLeftAncestor, levelOfRightAncestor) + 1;
	}


	private int max(int levelOfLeftAncestor, int levelOfRightAncestor) {
		if (levelOfLeftAncestor > levelOfRightAncestor)
			return levelOfLeftAncestor;
		else 
			return levelOfRightAncestor;
	}
	
	public void setReadNodesToBeExecuted(ArrayList<ExecutableLine> executableCode){
		int lowerBound = getLowerBound(executableCode);
		int upperBound = nodeIndex;
		
		ArrayList<Integer> readAccessList = getOperandOut().getReadAccessList();
		for (int i = lowerBound; i <= upperBound; i++){
			if (readAccessList.contains(i)){
				readNodesToBeExecuted.add(i);
			}
		}
	}
	
	
	private int getLowerBound(ArrayList<ExecutableLine> executableCode) {
		ArrayList<Integer> writeAccessList = getOperandOut().getWriteAccessList();	
		
		int lowerBound = 0;
		if (writeAccessList.size() > 1){
			lowerBound = getLowerBound_multipleValues();
		}
		else {
			lowerBound = 0;
		}
		return lowerBound;
	}


	private int getLowerBound_multipleValues() {
		ArrayList<Integer> writeAccessList = getOperandOut().getWriteAccessList();	
		
		int endIndex = writeAccessList.indexOf(nodeIndex);
		int startIndex = endIndex;
		
		int previousNodeIndex = 0;
		
		
		while (startIndex >= 0)
		{
			previousNodeIndex = writeAccessList.get(startIndex);
			if (previousNodeIndex != nodeIndex)
				break;
			startIndex--;
		}
		return previousNodeIndex;
	}
	public boolean readNodesExecuted = false;
	
	public void setReadNodesExecuted(ArrayList<ExecutableLine> executableCode){
		for (int i : readNodesToBeExecuted){
			ExecutableLine ex = executableCode.get(i - 1);
			if (ex.getExecStatus() != ExecutionStatus.wbFinishedStatus){
				readNodesExecuted =  false;
				return;
			}
		}
		readNodesExecuted =  true;
	}
	
	public boolean getReadNodesExecuted(ArrayList<ExecutableLine> executableCode){
		if (readNodesExecuted == false)
			setReadNodesExecuted(executableCode);
		
		return readNodesExecuted;
	}
	
	public boolean ancestorsExecuted = false;
	
	public void setAncestorsExecuted(ArrayList<ExecutableLine> executableCode){
		boolean leftAncestorExecuted = false;
		boolean rightAncestorExecuted = false;
		
		if (leftAncestorIndex == 0){
			leftAncestorExecuted = true;
		}
		else {
			ExecutionStatus status = executableCode.get(leftAncestorIndex - 1).getExecStatus();
			if ( status == ExecutionStatus.wbFinishedStatus || status == ExecutionStatus.resultForwardedStatus){
				leftAncestorExecuted = true;
			}
		}

		if (rightAncestorIndex == 0){
			rightAncestorExecuted = true;			
		}
		else {
			ExecutionStatus status = executableCode.get(rightAncestorIndex - 1).getExecStatus();
			if (status == ExecutionStatus.wbFinishedStatus || status == ExecutionStatus.resultForwardedStatus){
				rightAncestorExecuted = true;
			}
		}
		ancestorsExecuted = leftAncestorExecuted && rightAncestorExecuted;
	}
	
	public boolean getAncestorsExecuted(ArrayList<ExecutableLine> executableCode){
		if (ancestorsExecuted == false)
			setAncestorsExecuted(executableCode);
		
		return ancestorsExecuted;
	}
	
	public boolean alreadyStarted(){
		if ((execStatus == ExecutionStatus.startedStatus) || (execStatus == ExecutionStatus.aluFinishedStatus) || (execStatus == ExecutionStatus.wbFinishedStatus))
			return true;
		else 
			return false;
	}
	
	public void setExecStatus(ArrayList<ExecutableLine> executableCode){
		//TODO: Add other conditions
		if ((getReadNodesExecuted(executableCode)) && (getAncestorsExecuted(executableCode)) && (!alreadyStarted()))
			this.execStatus = ExecutionStatus.permittedStatus;
	}
	
	public boolean isMultiplication(){
		String mnemonic = this.operator.mnemonic;
		return (mnemonic.equals("mul"));
	}
	
	public boolean isAddition(){
		String mnemonic = this.operator.mnemonic;
		return (mnemonic.equals("add"));
	}
	
	public boolean isAripheticOperation(){
		String mnemonic = operator.getMnemonic();
		for (int i = 0; i < ariphmeticOperators.length; i++){
			if (mnemonic.equals(ariphmeticOperators[i]))
				return true;
		}
		return false;
	}
	
	public boolean hasOneDescendant(){
		if (descendants.size() == 1){
			return true;
		}
		else 
			return false;
	}
	// TODO: other mnemonics can be executed
	public boolean canBeForwarded(){
		return (isAripheticOperation() &&  hasOneDescendant() && !isResultForwarding);
	}

	public boolean canAcceptForwarding(){
		return (isAripheticOperation());
	}
	
	public boolean swapOperands(){
		boolean swapable = isAddition() || isMultiplication();
		if (swapable){
			Operand temp = operandIn1;
			operandIn1   = operandIn2;
			operandIn2   = temp;
		}
		return swapable;		
	}
	
	public void setRfCalculationOperation(ExecutableLine ex){
		if (ex.equals(this.leftAncestor)) {
			leftOperandRf_delay = 1;
			isLeftOperandRf = true;
		} 
		// TODO implement in ViSARD core
		else if (ex.equals(this.rightAncestor)) {
			rightOperandRf_delay = 1;
			isRightOperandRf = true;
		}
		operator.setCalculationOperation();
	}
	


	public boolean isResultForwarding() {
		return isResultForwarding;
	}



	public class Operator {
		private String mnemonic = "";
		
		public String getMnemonic() {
			return mnemonic;
		}

		protected String wbCommand = "";
		protected String exCommand = "";
		protected int aluDelay = 0;
		protected int pipeDelay = 0;
		protected String calculationOperation;
		protected String writeBackOperation;
		protected boolean calculationExecuted = false;
		protected boolean writeBackExecuted = false;
		protected final String wbNone = "00111";
		
		public int getAluDelay() {
			return aluDelay;
		}

		public int getPipeDelay() {
			return pipeDelay;
		} 
		
		public int getDelay() {
			return aluDelay + pipeDelay;
		}
		
		public Operator(){
			
		}
		
		public Operator(String operator){
			mnemonic = operator;
		}
		
		public String binaryToHex(String binaryString) {
			int decimalString = Integer.parseInt(binaryString,2);
			String hexString = Integer.toString(decimalString,16);
			
			return addNulls(hexString);
		}

		private String addNulls(String hexString) {
			String nullString = "00000000".substring(hexString.length());			
			return nullString + hexString;
		}
		
		public void setRfDelay(int leftRf, int rightRf){
			leftOperandRf_delay = leftRf;
			rightOperandRf_delay = rightRf;
		}
		
		public void setCalculationOperation(){
			
			String wbAddr = "00000000";
			String wbCom  = wbCommand.substring(3);
			String addr2  = operandIn2.getAddressString();
			String addr1  = operandIn1.getAddressString();
			String exCom  = exCommand.substring(5);
			String Rf 	  = getRf_hex();
			
			String binaryOperation = wbAddr + wbCom + addr2 + addr1 + exCom; 
			calculationOperation = binaryToHex(binaryOperation) + Rf;
		}

		private String getRf_hex() {
			int Rf_dec = Integer.parseInt(getRfString(),2);
			String Rf_hex  = Integer.toString(Rf_dec, 16);
			return Rf_hex;
		}

		private String getRfString() {
			String leftRf = "00";
			String rightRf= "00";
			if (isResultForwarding)
			{
				if (isLeftOperandRf){
					leftRf = Integer.toBinaryString(leftOperandRf_delay);
				}
				if (isRightOperandRf){
					rightRf = Integer.toBinaryString(leftOperandRf_delay);
				}
			}
			return leftRf + rightRf;
		}
				
		public void setWriteBackOperation(){
			
			String wbAddr = getOperandOut().getAddressString();
			String wbCom  = wbNone;
			String addr2  = "00000000";
			String addr1  = "00000000";
			String exCom  = exCommand.substring(5);
			
			String Rf	  = "0000";
			
			String binaryOperation = wbAddr + wbCom + addr2 + addr1 + exCom;
			writeBackOperation =  binaryToHex(binaryOperation) + "0";
		}
		
		public void setOperations(){
			setCalculationOperation();
			setWriteBackOperation();
		}
		
		public String getCalculationOperation(){
			return calculationOperation;
		}
		
		public String getWriteBackOperation(){
			return writeBackOperation;
		}
		
		public boolean isCalculationExecuted() {
			return calculationExecuted == true;
		}
		
		public boolean isWriteBackExecuted() {
			return writeBackExecuted == true;
		}
	}


	public void start() {
		execStatus = ExecutionStatus.startedStatus;		
	}
	public void startWriteBack() {
		execStatus = ExecutionStatus.aluFinishedStatus;		
	}
	public void endExecution() {
		execStatus = ExecutionStatus.wbFinishedStatus;		
	}
	public void startResultForwarding() {
		execStatus = ExecutionStatus.resultForwardedStatus;
	}
	
	public Operand getOperandOut() {
		return operandOut;
	}
	public void setOperandOut(Operand operandOut) {
		this.operandOut = operandOut;
	}	
}

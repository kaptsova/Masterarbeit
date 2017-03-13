package asmLine;

import java.util.ArrayList;

public class ResultForwardingItem extends ExecutableLine{
	
	private ArrayList<ExecutableLine> exLines = new ArrayList<ExecutableLine>();

	
	RfOperator operator = null;
	
	public static int maxLength = 5;
		
	public ResultForwardingItem() {
		// TODO Auto-generated constructor stub
	}
	
	
	public ResultForwardingItem(ExecutableLine ex1, ExecutableLine ex2){
		exLines.add(ex1);
		exLines.add(ex2);
		
		operator = new RfOperator(ex1, ex2);
	}
	
	
	public void addExecutableLine(ExecutableLine ex){
		exLines.add(ex);
		Operator op = ex.getOperator();
		operator.operators.add(op);
	}
	
	public ResultForwardingItem splitIntoTwoRfItems(int i){
		ResultForwardingItem item1 = this;
		return item1;		
	}

	
	public String toString(){
		return exLines.toString();
	}
	
	public class RfOperator extends Operator {
		
		ArrayList<Operator> operators = new ArrayList<Operator>();

		public void setCalculationOperation(){
			Operator myOperator = operators.get(0);
			myOperator.setCalculationOperation();
		}
		
		public void setLastCalculationOperation(){
			
		}
		
		
		public void setWriteBackOperation(){
			ExecutableLine ex = exLines.get(0);
			ex.getOperator().setWriteBackOperation();
		}
		
		
		public RfOperator(ExecutableLine ex1, ExecutableLine ex2){
			ex1.super();
			
			Operator operator1 = exLines.get(0).getOperator();
			Operator operator2 = exLines.get(1).getOperator();
			
			operators.add(operator1);
			operators.add(operator2);
			
			//operator.aluDelay = operator1.getAluDelay() + operator2.getAluDelay();
			//operator.pipeDelay = operator2.getPipeDelay();
			
		}
		
		public RfOperator(ExecutableLine executableLine, String operator) {
			executableLine.super(operator);
			// TODO Auto-generated constructor stub
		}

		
	}

}

package asmLine;

import java.util.ArrayList;

public class ResultForwardingItem extends ExecutableLine{
	
	private ArrayList<ExecutableLine> exLines = new ArrayList<ExecutableLine>();
	
	private ExecutableLine ex1 = null;
	private ExecutableLine ex2 = null;
	
	RfOperator operator = null;
	
	static int maxLength = 5;
		
	public ResultForwardingItem() {
		// TODO Auto-generated constructor stub
	}
	
	
	public ResultForwardingItem(ExecutableLine initFirstAsmLine, ExecutableLine initSecondAsmLine){
		ex1 = initFirstAsmLine;
		ex2 = initSecondAsmLine;
		
		operator = new RfOperator(ex1, ex2);
	}
	
	public ResultForwardingItem(ArrayList<ExecutableLine> ex){
		ex.add(ex1);
		ex.add(ex2);
		
		operator = new RfOperator(ex);
	}
	
	public ResultForwardingItem splitIntoTwoRfItems(int i){
		ResultForwardingItem item1 = this;
		return item1;		
	}

	
	public String toString(){
		return ex1 + " " + ex2;
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
			ex2.getOperator().setWriteBackOperation();
		}
		
		
		public RfOperator(ExecutableLine ex1, ExecutableLine ex2){
			ex1.super();
			
			Operator operator1 = ex1.getOperator();
			Operator operator2 = ex2.getOperator();
			
			//operator.aluDelay = operator1.getAluDelay() + operator2.getAluDelay();
			//operator.pipeDelay = operator2.getPipeDelay();
			
		}
		
		public RfOperator(ExecutableLine executableLine, String operator) {
			executableLine.super(operator);
			// TODO Auto-generated constructor stub
		}

		public RfOperator(ArrayList<ExecutableLine> ex) {
			ex1.super();
		}
		
	}

}

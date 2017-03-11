package optimiser;

import asmLine.ExecutableLine;

public class Optimiser_TDR extends Optimiser_levelDependent {

	public Optimiser_TDR() {
		// TODO Auto-generated constructor stub
	}
	
	public ExecutableLine chooseNode() {
		System.out.println("Optimiser Max Delay");
		ExecutableLine resultNode = null;
		if (!readyNodes.isEmpty()){
			resultNode = readyNodes.get(0);
			for (ExecutableLine node : readyNodes) {
				int resultDelay = resultNode.getOperator().getDelay();
				int delay = node.getOperator().getDelay();
				if (resultDelay > delay){
					resultNode = node;
				}
			}
		}
		return resultNode;
	}
	

}

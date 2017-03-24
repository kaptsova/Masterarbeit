package optimiser;

import asmLine.ExecutableLine;

public class Optimiser_minDelay extends Optimizer{

	public Optimiser_minDelay() {
		// TODO Auto-generated constructor stub
	}

	//@override
	public ExecutableLine chooseNode() {
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

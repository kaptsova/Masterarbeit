package optimiser;

import asmLine.ExecutableLine;

public class Optimiser_minLevel extends Optimiser_levelDependent{

	public Optimiser_minLevel() {
		// TODO Auto-generated constructor stub
	}
	
	public ExecutableLine chooseNode() {
		ExecutableLine resultNode = null;
		if (!readyNodes.isEmpty()){
			resultNode = readyNodes.get(0);
			for (ExecutableLine node : readyNodes) {
				int resultDelay = resultNode.getLevelOfGraph();
				int delay = node.getLevelOfGraph();
				if (resultDelay > delay){
					resultNode = node;
				}
			}
		}
		return resultNode;
	}
	

}

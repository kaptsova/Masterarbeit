package optimiser;

import java.util.ArrayList;

import asmLine.ExecutableLine;

public abstract class Optimiser_levelDependent extends Optimiser {

	public void constructGraph(ArrayList<ExecutableLine> executableCode){
		constructConnectionsBetweenNodes(executableCode);
		calculateLevelsInGraph(executableCode);
	}
	
	public void calculateLevelsInGraph(ArrayList<ExecutableLine> executableCode){
		for (ExecutableLine ex : executableCode){
			ex.setLevelInGraph(executableCode);
		}
	}
	
	public Optimiser_levelDependent() {
		// TODO Auto-generated constructor stub
	}

}

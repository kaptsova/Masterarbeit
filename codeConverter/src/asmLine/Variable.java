package asmLine;

import java.util.ArrayList;

public class Variable {
	
	private String variableName = "";
	private ArrayList<Integer> lineIndexList = new ArrayList<Integer>();
	//TODO change name of "index" variable to be more reasonable :)
	private int index = -1;
	
	public Variable() {
		// TODO Auto-generated constructor stub
	}
	
	public void fillLineIndexList(ArrayList<ExecutableLine> executableCode){
		System.out.println(variableName +  ": Fill line index list");
	}
	
	public void addLineIndex(int lineIndex){
		lineIndexList.add(lineIndex);
	}
	
	public int getNextLineIndex(){
		return lineIndexList.get(index);
	}
}

package memorySystem;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import asmLine.DqAsmLine;
import commonTypes.PrecisionType;

public class DataMemory {
	
	String precisionType = "";
	String numberOfVariables = "";
	ArrayList<String> dataMemoryList = new ArrayList<String>();
	

	public DataMemory() {
		// TODO Auto-generated constructor stub
	}
	
	public void setPrecisionType(PrecisionType precType){
		int precision = 0;
		switch (precType)
		{
		case singlePrecision:
			precision = 8;
			break;
		case doublePrecision:
			precision = 16;
			break;
		default:
			break;
		}
		precisionType = String.format("%08X", precision);		
	}
	
	public void setNumberOfVariables(){
		numberOfVariables = String.format("%08X", DqAsmLine.getNumberOfVariables());
	}
	
	public void fillMemoryList(ArrayList<DqAsmLine> declarationCode, PrecisionType precType){
		
		setPrecisionType(precType);
		setNumberOfVariables();
		
		for (DqAsmLine dq: declarationCode){
			dataMemoryList.add(dq.getVariableValue());
		}
	}
	
	public void fillMemoryFile(String filePath){
		
		File file= new File (filePath);
		FileWriter fw = null;
		BufferedWriter bw = null;
		
		try {
			if (file.exists()){
				//if file exists append to file
				fw = new FileWriter(file,false);
				
			}
			else {
				// if file doesn't exist - create a file wih path specified
				file.createNewFile();
				fw = new FileWriter(file);
			}
			bw = new BufferedWriter(fw);
			bw.write(precisionType);
			bw.newLine();
			bw.write(numberOfVariables);
			bw.newLine();
			for (String line: dataMemoryList){
				bw.write(line);
				bw.newLine();
			}
			//fw.flush();
		} 
		catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally {
            try {
                bw.close();
                fw.close();
            } 
            catch (IOException e) {
                e.printStackTrace();
            }
        }		
	}
	
	
	public String toString(){
		StringBuilder str = new StringBuilder();
		str.append(precisionType + "\n");
		str.append(numberOfVariables + "\n")
		for (String line: dataMemoryList){
			str.append(line + "\n");
		}
		return str.toString();		
	}
	
	public void printInfo(){
		System.out.println(toString());
	}
	
	public static void main(String [] args){
		
	}

}

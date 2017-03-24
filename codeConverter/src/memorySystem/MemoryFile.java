package memorySystem;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import codeConverter.ProgramCode;
import commonTypes.PrecisionType;
import errorHandler.ErrorMessage;
import errorHandler.ErrorType;

public abstract class MemoryFile {

	public ArrayList<String> linesList = new ArrayList<String>();
	protected int numberOfLines = 0;
	protected String numberOfLinesString = "";
	
	protected boolean isFirstFile = true;
	
	protected String precisionTypeString = "";
	
	public MemoryFile() {
		// TODO Auto-generated constructor stub
	}
	
	public abstract void setPrecisionType(PrecisionType precType);
	public abstract void fillMemoryList(ProgramCode pcode, PrecisionType precType);
	
	public void initializeMemoryFile(ProgramCode pcode, PrecisionType precType){
		// 	
		setPrecisionType(precType);
		//
		fillMemoryList(pcode, precType);
		setNumberOfLinesString();
	}
	
	public void setNumberOfLinesString(){
		numberOfLines =linesList.size();
		numberOfLinesString = String.format("%08X", numberOfLines);	
	}
	
	public void fillMemoryFile(String filePath){
		File file= new File (filePath);
		FileWriter fw = null;
		BufferedWriter bw = null;
		
		try {
			if (file.exists()){
				//if file exists - rewrite file
				fw = new FileWriter(file,false);				
			}
			else {
				// if file doesn't exist - create a file with path specified
				file.createNewFile();
				fw = new FileWriter(file);
			}
			bw = new BufferedWriter(fw);
			bw.write(precisionTypeString);
			bw.newLine();
			bw.write(numberOfLinesString);
			bw.newLine();
			for (String line: linesList){
				bw.write(line);
				bw.newLine();
			}
			//fw.flush();
		} 
		catch (IOException e) {
			// TODO Auto-generated catch block
			ErrorMessage errMsg = new ErrorMessage(ErrorType.outputFileNotFoundError, filePath);
			errMsg.printToFile();
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

}

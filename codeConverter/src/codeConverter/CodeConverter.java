package codeConverter;

import java.util.ArrayList;

import asmLine.ExecutableLine;
import asmLine.ProgramCode;
import commonTypes.ApplicationType;
import commonTypes.PrecisionType;
import fileSystem.FileSystem;

public class CodeConverter {
	
	public boolean errorOccured = false;
	private ApplicationType appType = ApplicationType.consoleApplication;
	//TODO Implement method to get precision type
	protected PrecisionType prType = PrecisionType.doublePrecision;
	
	
	public static void main(String[] args)
	{
		CodeConverter codeConverter = new CodeConverter();
		codeConverter.start();
	}
	
	public void start(){
		// initialize file system
		FileSystem fileSystem = FileSystem.getInstance(appType);
		fileSystem.initializeFileSystem();
		fileSystem.printInfo();
		
		executeProgramCode(fileSystem);
		
	}

	private void executeProgramCode(FileSystem fileSystem) {
		int numberOfFiles = fileSystem.getNumberOfFiles();
		ArrayList<String> filePaths = fileSystem.getInputFilePathList();
		
		ProgramCode pcode = new ProgramCode();
		pcode.readOperationCodes(fileSystem.getOpCodeFilePath());
		
		
		
		for (int i = 0; i < numberOfFiles; i++){
			
			String programCodePath = filePaths.get(i);	
			ExecutableLine.setFilePath(programCodePath);
			
			pcode.readProgramCode(programCodePath, prType);
			
			pcode.printInfo();
			
			pcode.handleProgramCode(fileSystem, prType);
						
			System.out.println("______________________________");
		}
	}
	
}

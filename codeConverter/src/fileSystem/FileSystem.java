package fileSystem;

import java.util.ArrayList;

import commonTypes.ApplicationType;
import commonTypes.PrecisionType;
import errorHandler.ErrorMessage;

public abstract class FileSystem {

	private static FileSystem uniqueFileSystem;
	
	// fields 
	// 	- pathes to files
	protected ArrayList<String> inputFilePathList = new ArrayList<String>();
	public static FileSystem getUniqueFileSystem() {
		return uniqueFileSystem;
	}
	public  ArrayList<String> getInputFilePathList() {
		return inputFilePathList;
	}
	public String getDataMemoryFilePath() {
		return dataMemoryFilePath;
	}
	public String getProgMemoryFilePath() {
		return progMemoryFilePath;
	}	
	public String getErrorFilePath() {
		return errorFilePath;
	}
	public String getOpCodeFilePath() {
		return opCodeFilePath;
	}
	public String getInfoFilePath() {
		return infoFilePath;
	}
	public static String getFirstFileName() {
		return firstFileName;
	}
	public int getNumberOfFiles() {
		return numberOfFiles;
	}


	protected String dataMemoryFilePath = "";
	protected String progMemoryFilePath = "";
	protected String errorFilePath = "";
	protected String opCodeFilePath = "";
	protected String infoFilePath = "";
	
	
	protected static String firstFileName = "";
	protected static String commonFileName = "";
	// 	- number f input files to be converted
	protected int numberOfFiles = 0;
	
	String opCodeSglFilePath = "opcodes_sgl";
	String opCodeDblFilePath = "opcodes_dbl";
	
	// abstract methods
	protected abstract int setNumberOfFiles();
	protected abstract ArrayList<String> setInputFilePathList();
	
	// create singleton filesystem
	protected FileSystem(){;}
		
	public static FileSystem getInstance(ApplicationType appType){
		if (uniqueFileSystem == null)
		{
			switch (appType)
			{
			case consoleApplication:
				uniqueFileSystem = new FileSystem_Console();
				break;
			case guiApplication:
				uniqueFileSystem = new FileSystem_Gui();
				break;
			case fileBasedApplication:
				uniqueFileSystem = new FileSystem_Gui();
				break;
			default:
				uniqueFileSystem = new FileSystem_Console();
				break;
			}
		}
		return uniqueFileSystem;
	}
	
	public void initializeFileSystem(){
		setInputFilePathList();
		createOutputFiles(commonFileName);
		setOpCodeFilePath(PrecisionType.doublePrecision);
		ErrorMessage.setErrorFilePath(getErrorFilePath());
	}
	
	private void createOutputFiles(String fileName) 
	{
		String dataMemoryFileName = fileName.concat("-data");
		String progMemoryFileName = fileName.concat("-prog");
		String infoFileName = fileName.concat("-info");
		String errorFileName = fileName.concat("-error");
		
		dataMemoryFilePath = FilePath.getRelativePath(dataMemoryFileName);
		progMemoryFilePath = FilePath.getRelativePath(progMemoryFileName);	
		infoFilePath = FilePath.getRelativePath(infoFileName);	
		errorFilePath = FilePath.getRelativePath(errorFileName);	
	}
	
	public String toString(){				
		StringBuilder str = new StringBuilder();
		for (int i = 0; i < inputFilePathList.size(); i++)
		{
			/*System.out.println("File " + i + ": " + inputFilePathList.get(i));*/
			str.append("File " + i + ": " + inputFilePathList.get(i)+ "\n");
		}
		
		str.append("Data memory file path: " + dataMemoryFilePath + "\n");
		str.append("Program memory file path: " + progMemoryFilePath + "\n");
		str.append("Common info file path: " + infoFilePath + "\n");
		str.append("Error file path: " + errorFilePath + "\n");
		str.append("OpCode file path: " + opCodeFilePath + "\n");
		//System.out.println(str.toString());

		return str.toString();		
	}
	
	public void printInfo(){
		System.out.println(toString());
	}
	
	private String setOpCodeFilePath(PrecisionType prType){
		
		switch (prType)
		{
		case singlePrecision:
			opCodeFilePath = opCodeSglFilePath;
			break;
		case doublePrecision:
			opCodeFilePath = opCodeDblFilePath;
			break;
		default:
			break;
		}
		opCodeFilePath = FilePath.getRelativePath(opCodeFilePath);
		return "";
	}
	
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		FileSystem fs = FileSystem.getInstance(ApplicationType.consoleApplication);
		fs.initializeFileSystem();
		fs.toString();
				
	}

}

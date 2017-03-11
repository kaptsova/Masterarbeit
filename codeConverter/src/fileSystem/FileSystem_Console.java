package fileSystem;

import java.util.ArrayList;
import java.util.Scanner;

import codeConverter.Device;

public class FileSystem_Console extends FileSystem{

	public FileSystem_Console() {
		super();
		System.out.println("In file system - console");
		// TODO Auto-generated constructor stub
	}

	
	protected int setNumberOfFiles(){
		Scanner sc = new Scanner(System.in);
		int count = 0;
		
		System.out.println("Enter the number of files: ");
		if (sc.hasNextInt()){
			count = sc.nextInt();
		}
		else {	
			System.out.println("Warning: need to enter an integer");
			count = setNumberOfFiles();
		}		
		return count;
	}
	
	@Override
	protected ArrayList<String> setInputFilePathList() {
		// TODO Auto-generated method stub
		numberOfFiles = setNumberOfFiles();
		boolean isFirst = true;
		
		for (int i = 0; i < numberOfFiles; i++)
		{	
			Device.print(i + ": ");
			
			FilePath fp = new FilePath();
			String filePath = fp.getFilePath(isFirst);
			if (!filePath.isEmpty())
			{
				inputFilePathList.add(filePath);
			}
			isFirst = false;
		}	
		firstFileName = FilePath.getFirstFileName();
		return inputFilePathList;		
	}
	
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}

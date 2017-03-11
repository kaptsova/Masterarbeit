package fileSystem;

import java.util.Scanner;

public class FilePath {
	String filePath = "";
	private static String firstFileName = "";
	private static String directoryPath = "tests\\";
		
	public static String getFirstFileName() {
		return firstFileName;
	}

	protected String getFilePath(boolean isFirst){
		Scanner sc = new Scanner(System.in);
		System.out.println("Enter the name of an ASM-file you want to work with (without .txt): ");
		String fileName = sc.nextLine();
		filePath = getRelativePath(fileName);
		if (isFirst)
			firstFileName = fileName;

		return filePath;
	}
	

	static String getRelativePath(String fileName){		
		// Add .txt
		String filePath = fileName.concat(".txt");	
		String relativePath = directoryPath.concat(filePath);
		return relativePath;
	}
	

	public static void main(String[] args) {
		System.out.println("FilePath class begins\n");
	
	}

}

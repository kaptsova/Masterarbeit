package errorHandler;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/*
 * @author Natalia Kaptsova
 * @version 1.0
 */
public class ErrorMessage {
	private static int errorCount = 0;
	private static boolean isFirstErrorMessage = true;
	
	private static String errorFilePath;
	public static void setErrorFilePath(String filePath){
		errorFilePath = filePath;
	}

	private ErrorType errorType = ErrorType.illegalError;
	private String errorMessage = "";

	private int errorId = -1;

	public int getErrorId() {
		return errorId;
	}

	private String errorDescription = null;

	public String getErrorDescription() {
		return errorDescription;
	}

	private int fileLineId = -1;

	private String filePath = null;

	public ErrorMessage() {
	}

	public ErrorMessage(ErrorType initErrorType) {
		errorType = initErrorType;
		errorCount++;
		errorId = errorCount;
	}

	public ErrorMessage(ErrorType initErrorType, int initFileLineId) {
		this(initErrorType);
		fileLineId = initFileLineId;
	}
	public ErrorMessage(ErrorType initErrorType, String initFilePath) {
		this(initErrorType);
		filePath = initFilePath;
	}

	public ErrorMessage(ErrorType initErrorType, int initFileLineId, String initFilePath) {
		this(initErrorType, initFileLineId);
		filePath = initFilePath;
	}

	public ErrorMessage(ErrorType initErrorType, int initFileLineId, String initFilePath, String initErrorString) {
		this(initErrorType, initFileLineId, initFilePath);
		errorDescription = initErrorString;
	}

	/*
	 * Print information about this error message
	 */
	public void print() {
		setErrorMessage();
		System.out.println(errorMessage);
	}

	/*
	 * Set up all information about this error message into a string
	 * @return errorMessage string containing information about error
	 */
	public String setErrorMessage() {
		errorMessage = String.format("Error %d of %d (%5s) occured\r\n", errorId, errorCount, errorType);
	
		
		if (filePath != null) {
			String filePathInfo = String.format(":\tline %d of file %s\r\n", fileLineId, filePath);
			errorMessage = errorMessage.concat(filePathInfo);
		}

		if (errorDescription != null) {
			String errorLongInfo = String.format("\tDetails: %s\r\n", errorDescription);
			errorMessage = errorMessage.concat(errorLongInfo);
		}
		return errorMessage;
	}

	/*
	 * Print information about error to file (path specified)
	 * 
	 * @param filePath - path to file, containing error log information
	 */
	public void printToFile() {
		// create new file
		File file = new File(errorFilePath);
		// create new writer
		FileWriter fw = null;
		BufferedWriter bw = null;
		
		print();

		try {
			// if file exists
			if (file.exists()) {
				// for first message - rewrite old error log file
				// for every other messages - append to old file
				boolean appendToFile = !isFirstErrorMessage;
				fw = new FileWriter(file, appendToFile);
			} else {
				// if file doesn't exist - create a file with path specified
				file.createNewFile();
				fw = new FileWriter(file);
			}
			bw = new BufferedWriter(fw);
			setErrorMessage();
			bw.write(errorMessage);
			// Mark that the first error message is written to file
			isFirstErrorMessage = false;
		} catch (IOException e) {
			ErrorMessage errMsg = new ErrorMessage(ErrorType.outputFileNotFoundError);
			errMsg.print();
			e.printStackTrace();
		} finally {
			try {
				bw.close();
				fw.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	/*
	 * For tests
	 */
	public static void main(String[] args) {
		System.out.println("Error message log:");
		ErrorMessage err1 = new ErrorMessage(ErrorType.inputError, 23, "/nata.txt", "It's error");
		err1.print();
		err1.printToFile();

		ErrorMessage err2 = new ErrorMessage(ErrorType.outputError, 57, "/slava.txt");
		err2.print();
		err2.printToFile();

		ErrorMessage err3 = new ErrorMessage(ErrorType.wrongMnemonicError, 59, "/slava.txt");
		err3.print();
		err3.printToFile();
	}
}

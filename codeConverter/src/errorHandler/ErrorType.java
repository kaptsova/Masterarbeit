package errorHandler;

public enum ErrorType {
	illegalError,
	inputError,
	outputError,
	
	inputFileNotFoundError,
	outputFileNotFoundError,
	
	wrongMnemonicError, 
	wrongOperandTypeError,
	
	wrongOpIn1TypeError,
	wrongOpIn2TypeError, 
	wrongOpOutTypeError,
	
	opIn1NotFoundError,
	opIn2NotFoundError,
	opOutNotFoundError,
	
	//wbNones
	delayError,
	wrongPrecisionError,
	
	multiplyDeclaredcVariableError, 	
	undeclaredVariableError,	
	
}

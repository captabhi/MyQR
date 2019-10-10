package com.example.myqr;

@SuppressWarnings("serial")
public class FileSizeException extends Exception
{
	String cause;
	public FileSizeException(String s) {
		cause=s;
	}
	public FileSizeException() {
		cause="1";
	}
	
	@Override
	public String toString()
	{
		return cause;
	}
}
package com.example.myqr;
import java.util.Arrays;


/**
 * @author shashank
 * @class FileSplitter
 * 
 * @params 
 * <<File>> or <<Byte[]>> 
 * 	- Can take a File path and read that file  
 * 	- Or Can take raw byte data already readed from somewhere else
 *	  and split it.			
 */

public class FileSplitter {
	//Current Set limit for data in a QR code
	public final static int QR_SIZE = 1100;
	public final static int BUF_SIZE = 1024*100; // 300 KiloBytes
	
	public  byte data[][];
	private int packets;
	//private FileOutputStream fos=null;
	
	/** @return the packets count */
	public int getPackets() {
		return packets;
	}

	/** @param packets - sets the number of packets in advance */
	@Deprecated
	public void setPackets(int packets) {
		this.packets = packets;
	}

	/** @return the splitted data */
	public byte[][] getSplitted() {
		return data;
	}

	public byte[] getIthSpliited(int i) { return data[i]; }

	private void initialiseData(long length)
	{
		packets= (int) Math.ceil(length/QR_SIZE);
		data = new byte[packets][QR_SIZE];
	}

	public FileSplitter(byte rawdata[]) throws FileSizeException
	{
		if(rawdata.length>BUF_SIZE)
			throw new FileSizeException("File Size Greater than " + BUF_SIZE/1024 + "Kb");

		/** IMPLEMENT METHOD **/
		
		initialiseData(rawdata.length);
		
		for(int i=0;i<packets;i++)
		{
		data[i]=Arrays.copyOfRange(rawdata, 
								   i*QR_SIZE, 
								   Math.min(i*QR_SIZE+QR_SIZE,rawdata.length)
								   );
		}
		
	}
}

/*
	@SuppressWarnings("unused")
	public static void main(String[] args) {

		File fin=new File("C:\\Users\\admin\\Desktop\\qr project\\man.jpg");
		try {
			FileSplitter obj=new FileSplitter(fin);

		} catch (FileSizeException e) {
			e.printStackTrace();
		}
	}*/

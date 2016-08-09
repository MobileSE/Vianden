package org.vianden.model;

public class Publisher 
{
	public static final int ACM = 1;
	public static final int IEEE = 2;
	public static final int SPRINGER = 3;
	public static final int ELSEVIER = 4;
	public static final int WILEY = 5;
	public static final int USENIX = 6;
	public static final int INTERNET_SOCIETY = 7;
	public static final int IET = 8;
	public static final int CAMBRIDGE_UNIVERSITY_PRESS = 9;
	public static final int IOS_PRESS = 10;						//Journal of Computer Security (JCS)
	public static final int WORLD_SCIENTIFIC = 11;
	public static final int EMERALD = 12;						//Information Management & Computer Security (IMCS)
	public static final int IGI_GLOBAL = 13;					//International Journal of Information Security and Privacy (IJISP)
	//...more
	
	
	public static boolean isSingleColumn(int publisher){
		boolean flag = false;
		
		//judging single or double column
		switch(publisher){
		case Publisher.ACM:
		case Publisher.IEEE:
		case Publisher.ELSEVIER: 
		case Publisher.WILEY:
		case Publisher.USENIX: flag = false;
			break;
		case Publisher.SPRINGER: flag = true;
			break;
		}
		
		return flag;
	}
	
	public static String getPublisherName(int publisher){
		String pubName = "";
		
		switch(publisher){
		case Publisher.ACM: pubName = "ACM";
			break;
		case Publisher.IEEE: pubName = "IEEE";
			break;
		case Publisher.ELSEVIER: pubName = "ELSEVIER";
			break;
		case Publisher.WILEY: pubName = "WILEY";
			break;
		case Publisher.USENIX: pubName = "USENIX";
			break;
		case Publisher.SPRINGER: pubName = "SPRINGER";
			break;
		}
		
		return pubName;
	}
}

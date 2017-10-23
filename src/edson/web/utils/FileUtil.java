package edson.web.utils;

import java.util.UUID;

public class FileUtil {

	//产生多级目录和随机名
	public static String mdMoreDirs(String filename){
		int code=filename.hashCode();
		
		int dir1=code&0xf;
		int dir2=(code&0xf0)>>4;
		
		return "/"+dir1+"/"+dir2;	
	}
	
	public static String getUUID(){
		return UUID.randomUUID().toString();
	}
}

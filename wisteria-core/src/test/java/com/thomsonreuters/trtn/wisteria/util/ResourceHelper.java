package com.thomsonreuters.trtn.wisteria.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class ResourceHelper {
	public static final String CLASSPATH_PREFIX = "classpath:";
	
	public static InputStream load(String resource) throws IOException{
		if(resource == null){
			throw new NullPointerException();
		}
		
		resource = resource.trim();
		
		if(resource.toLowerCase().startsWith(CLASSPATH_PREFIX)){
			resource = resource.substring(CLASSPATH_PREFIX.length());
			return ResourceHelper.class.getResourceAsStream(resource);
		}else{
			return new FileInputStream(new File(resource));
		}
	}
}

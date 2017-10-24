package com.thomsonreuters.trtn.wisteria.core.session;

import java.util.Random;

public class SimpleSessionIdGenerator implements SessionIdGenerator{
	private Random random = new Random();
	
	@Override
	public String generate() {
		return random.nextInt(Integer.MAX_VALUE)+"";
	}

}

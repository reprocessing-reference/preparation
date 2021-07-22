package com.csgroup.jpadatasource;

import java.util.concurrent.atomic.AtomicBoolean;

import org.springframework.stereotype.Component;

@Component
public class StorageStatus {
	
	private AtomicBoolean hasChanges = new AtomicBoolean(true);
	
	public void jobDone()
	{
		hasChanges.set(false);
	}
	
	public Boolean hasChanges() {
		return hasChanges.get();
	}
	
	public void modified() {
		hasChanges.set(true);
	}

}

package com.csgroup.auxip.model.repository;

import java.util.concurrent.atomic.AtomicBoolean;

import org.springframework.stereotype.Component;

@Component
public class StorageStatus {
	
	private AtomicBoolean hasChanges = new AtomicBoolean(true);
	private AtomicBoolean archiveDone = new AtomicBoolean(false);
	private AtomicBoolean metricsDone = new AtomicBoolean(false);
	
	public void archiveDone()
	{
		archiveDone.set(true);
		if ( archiveDone.get() && metricsDone.get())
		{
			hasChanges.set(false);
		}
	}
	
	public void metricsDone()
	{
		metricsDone.set(true);
		if ( archiveDone.get() && metricsDone.get())
		{
			hasChanges.set(false);
		}
	}
	
	public Boolean hasChanges() {
		return hasChanges.get();
	}
	
	public void modified() {
		hasChanges.set(true);
		archiveDone.set(false);
		metricsDone.set(false);
	}

}

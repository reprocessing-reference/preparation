package com.csgroup.auxip.model.jpa;


import java.time.LocalDateTime;
import javax.persistence.Embeddable;
/**
 * Downloaded volume over a period/duration
 */
@Embeddable
public class DurationVolume {
	// starting point of the period where to count a downloaded volumes
	private LocalDateTime periodStart;
	// the period/duration is now configurable and read from the service properties
	// @Transient
	// private Duration duration = Globals.DOWNLOAD_DURATION;
	// cumulative volume in the current duration ( period ) in Bytes as for size of auxiliary data files
	private long volume;


	public LocalDateTime getPeriodStart() {
		return periodStart;
	}

	public void setPeriodStart(LocalDateTime periodStart) {
		this.periodStart = periodStart;
	}

	public long getVolume() {
		return volume;
	}

	public void setVolume(long volume) {
		this.volume = volume;
	}

	public void addVolume(long volume)
	{
		this.volume += volume ;
	}



}

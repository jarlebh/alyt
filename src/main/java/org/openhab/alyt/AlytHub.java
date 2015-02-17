package org.openhab.alyt;

import java.util.List;

public interface AlytHub {

	public abstract String getName();
	public abstract String getIPAddr();
	public abstract String getID();
	public abstract void initialize() throws AlytError;

	public abstract List<AlytDevice> getDevices() throws AlytError;

	public abstract void capabilityCommand(AlytDevice device, String command) throws AlytError;

	public abstract List<AlytEvent> getNewEvents(int interval) throws AlytError;
	
	public abstract void setPassword(String pwd) throws AlytError;
}
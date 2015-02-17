package org.openhab.alyt;

import static org.openhab.alyt.AlytConstants.TAG_DESCRIPTION;
import static org.openhab.alyt.AlytConstants.TAG_DEV_TYPE;
import static org.openhab.alyt.AlytConstants.TAG_ID;
import static org.openhab.alyt.AlytConstants.TAG_PROT_TYPE;
import static org.openhab.alyt.AlytConstants.TAG_REACHABLE;

import org.json.JSONObject;
public class AlytDevice {
	private final String alytID;
	private final AlytDeviceType deviceType;
	private final int protocolType;
	private String deviceName;
	private boolean reachable;
	private boolean updated = false;
	
	public AlytHub getHub() {
		return hub;
	}
	protected AlytHub hub;
	public AlytDevice(AlytHub hub, JSONObject deviceJSON) {
		this.hub = hub;
		this.alytID = Integer.toString(deviceJSON.getInt(TAG_ID));
		this.deviceType = AlytDeviceType.findDeviceType(deviceJSON.getInt(TAG_DEV_TYPE));
		this.deviceName = deviceJSON.getString(TAG_DESCRIPTION);
		this.protocolType = deviceJSON.getInt(TAG_PROT_TYPE);
		this.reachable = deviceJSON.getBoolean(TAG_REACHABLE);
	}
	public String getAlytID() {
		return alytID;
	}
	public AlytDeviceType getDeviceType() {
		return deviceType;
	}
	public int getProtocolType() {
		return protocolType;
	}
	public String getDeviceName() {
		return deviceName;
	}
	public boolean isReachable() {
		return reachable;
	}
	public boolean isUpdated() {
		return updated;
	}
	public void setUpdated(boolean updated) {
		this.updated = updated;
	}
	@Override
	public String toString() {
		return "AlytDevice [alytUUID=" + alytID + ", deviceType="
				+ deviceType + ", protocolType=" + protocolType
				+ ", deviceName=" + deviceName + ", reachable=" + reachable
				+ ", hub=" + hub + "]";
	}
	
}

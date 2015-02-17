package org.openhab.alyt;
import static org.openhab.alyt.AlytConstants.*;
import org.json.JSONArray;
import org.json.JSONObject;

public class AlytSwitch extends AlytDevice {
	public static final int ACTION_ID_ACTIVATE = 4;
	public static final int ACTION_ID_DEACTIVATE = 5;

	private JSONObject turnOnCommand;
	private JSONObject turnOffCommand;
	
	private boolean state = false;
	private int watt = 0;
	private int energy = 0;
	
	public AlytSwitch(AlytHub hub, JSONObject deviceJSON) throws AlytError {
		super(hub, deviceJSON);
		JSONObject capabilitiesJSON = deviceJSON.getJSONObject(TAG_CAPABILITY_LIST);
		JSONArray outputtag = capabilitiesJSON.getJSONArray(TAG_OUTPUT);
		for (int i = 0; i < outputtag.length(); i++) {
			JSONObject outputCapability = outputtag.getJSONObject(i);
			if (outputCapability.getInt(TAG_ACTION_ID) == ACTION_ID_ACTIVATE) {
				turnOnCommand = outputCapability;
			} else if (outputCapability.getInt(TAG_ACTION_ID) == ACTION_ID_DEACTIVATE) {
				turnOffCommand = outputCapability;
			} 
		}
		if (turnOnCommand == null || turnOffCommand == null) {
			throw new AlytError("Cannot create switch without on and off command");
		}
		JSONObject deviceData = deviceJSON.getJSONObject(TAG_DATA);
		this.state = deviceData.getInt(TAG_STATE) == 1;
	}

	public void turnOn() throws AlytError{
		hub.capabilityCommand(this, turnOnCommand.toString());
		this.setUpdated(true);
	}
	public void turnOff() throws AlytError{
		hub.capabilityCommand(this, turnOffCommand.toString());
		this.setUpdated(true);
	}
	public boolean isState() {
		return state;
	}

	public void setState(boolean state) {
		this.state = state;
		this.setUpdated(true);
	}

	public int getWatt() {
		return watt;
	}

	public void setWatt(int watt) {
		this.watt = watt;
		this.setUpdated(true);
	}

	public int getEnergy() {
		return energy;
	}

	public void setEnergy(int energy) {
		this.energy = energy;
		this.setUpdated(true);
	}
	
}

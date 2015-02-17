package org.openhab.alyt;

import static org.openhab.alyt.AlytConstants.ALYT_DATE_FORMATTER;
import static org.openhab.alyt.AlytConstants.TAG_DESCRIPTION;
import static org.openhab.alyt.AlytConstants.TAG_ID;
import static org.openhab.alyt.AlytConstants.TAG_ID_DISPOSITIVO;
import static org.openhab.alyt.AlytConstants.TAG_ID_PADRE;
import static org.openhab.alyt.AlytConstants.TAG_RESULT;
import static org.openhab.alyt.AlytConstants.TAG_TIME;
import static org.openhab.alyt.AlytConstants.TAG_TIPO_DISPOSITIVO;
import static org.openhab.alyt.AlytConstants.TAG_TIPO_EVENTO;

import java.text.ParseException;
import java.util.Date;

import org.json.JSONObject;
public class AlytEvent {
	
	private AlytEventType eventType;
	private int eventId;
	private AlytDeviceType eventOrigin;
	private Date timestamp;
	private String result;
	private int eventFather;
	private String eventDescription;
	private int eventTarget;

	public AlytEvent(JSONObject deviceJSON) {
		this.eventType = AlytEventType.findEventType(deviceJSON.getInt(TAG_TIPO_EVENTO));
		this.eventId  = deviceJSON.getInt(TAG_ID);
        this.eventOrigin = AlytDeviceType.findDeviceType(deviceJSON.getInt(TAG_TIPO_DISPOSITIVO));
        try {
			this.timestamp = ALYT_DATE_FORMATTER.parse(deviceJSON.getString(TAG_TIME));
		} catch (ParseException e) {
			throw new RuntimeException("Failed to parse date");
		}
        this.result = deviceJSON.getString(TAG_RESULT);
        this.eventFather = deviceJSON.getInt(TAG_ID_PADRE);
        this.eventDescription = deviceJSON.getString(TAG_DESCRIPTION);
        this.eventTarget =  deviceJSON.getInt(TAG_ID_DISPOSITIVO);
	}

	public AlytEventType getEventType() {
		return eventType;
	}

	@Override
	public String toString() {
		return "AlytEvent [eventType=" + eventType + ", eventId=" + eventId
				+ ", timestamp=" + timestamp + ", eventDescription="
				+ eventDescription + ", eventTarget=" + eventTarget + "]";
	}

	public int getEventId() {
		return eventId;
	}

	public AlytDeviceType getEventOrigin() {
		return eventOrigin;
	}

	public Date getTimestamp() {
		return timestamp;
	}

	public String getResult() {
		return result;
	}

	public int getEventFather() {
		return eventFather;
	}

	public String getEventDescription() {
		return eventDescription;
	}

	public int getEventTarget() {
		return eventTarget;
	}

}

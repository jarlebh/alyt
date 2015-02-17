package org.openhab.alyt;

public enum AlytEventType {
	NULL_EVENT (0),
	ON_EVENT (1),
	OFF_EVENT (2),
	ACTIVATION (3),
	DEACTIVATION (4),
	VALUE_SETTING (5),
	MOVE_EVENT (6),
	SMS_SENT (7),
	CALL_EXECUTION (8),
	LOGIN_LOCAL (9),
	LOGOUT_LOCAL (10),
	LOGIN_SERVER (11),
	LOGOUT_SERVER (12),
	LOGIN_ERROR (13),
	LOGOUT_ERROR (14),
	ALLARME (15),
	RULE_ACTIVATION (16),
	ADDING_DEVICE (17),
	REMOVING_DEVICE (18),
	START_LIVE (19),
	STOP_LIVE (20),
	STREAMING (21),
	CHANGE_PROG (22),
	DEBUG (100);
	
	private final int id;
	private AlytEventType(int id) {
		this.id = id;
	}
	public static AlytEventType findEventType(int id) {
		AlytEventType type = null;
		for (AlytEventType p : AlytEventType.values()) {
		   if (p.id == id) {
			   type = p;
		   }
		}
		if (type == null) {
			throw new RuntimeException("Could not find AlytEventType:"+id);
		}
		return type;
	}
	public String toString() {
		return this.name()+"("+this.id+")";
	}
}

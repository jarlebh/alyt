package org.openhab.alyt;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Test;

public class AlytHubTest {
	
	public void discover() throws Exception {
		new AlytHubDiscovery().discover();
	}
	@Test
	public void testHUB() throws Exception {
		List<AlytHub> hubs = new AlytHubDiscovery().discover();
		assertEquals("Did not find ALYT", 1, hubs.size());
		
		AlytHub hub = hubs.get(0);
		
		hub.setPassword("Indgu966");
		hub.initialize();
		System.out.println(hub);
		List<AlytEvent> events = hub.getNewEvents(30);
		List<AlytDevice> devices = hub.getDevices();
		assertEquals("Wrong number of devices", 1, devices.size());
		System.out.println(devices.get(0));
		events = hub.getNewEvents(30);
		System.out.println(events);
	}

}

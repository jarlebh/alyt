# ALYT Java API
Alyt Java Library for communication with an Alyt HUB.

##Basic USAGE

    List<AlytHub> hubs = new AlytHubDiscovery().discover();
    AlytHub hub = hubs.get(0);
    hub.setPassword("YOUR HUB PASSWORD");
    hub.initialize();
    List<AlytEvent> events = hub.getNewEvents(30);
    List<AlytDevice> devices = hub.getDevices();

Fore more information about ALYT goto www.alyt.com

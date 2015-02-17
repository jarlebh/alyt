package org.openhab.alyt;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.SocketTimeoutException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AlytHubDiscovery {
	private static final int ALYT_DISCOVER_PORT = 55005;

	static final String ALYT_DISCOVER_STRING = "ALYT_DISCOVERY";

	private final static Logger logger = LoggerFactory
			.getLogger(AlytHubDiscovery.class);
	private boolean discoveryRunning = false;

	public List<AlytHub> discover() throws Exception {
		discoveryRunning = true;
		final DatagramSocket bc = new DatagramSocket(ALYT_DISCOVER_PORT);
		List<AlytHub> result = null;
		try {
			bc.setBroadcast(true);
			bc.setSoTimeout(10000);

			Thread thread = new Thread("Sendbroadcast") {
				public void run() {
					sendDiscoveryMessage(bc, ALYT_DISCOVER_STRING);
					try {
						sleep(5000);
					} catch (Exception e) {
					}
					discoveryRunning = false;
					logger.trace("Done sending broadcast discovery messages.");
				}
			};
			thread.start();
			// thread.join();
			result = receiveDiscoveryMessage(bc);
		} finally {
			// Close the port!
			try {
				if (bc != null)
					bc.close();
			} catch (Exception e) {
				logger.debug(e.toString());
			}
		}
		return result;
	}

	private List<AlytHub> receiveDiscoveryMessage(DatagramSocket bcReceipt) {
		String alytHubIP = null;
		String alytHubName = null;
		List<AlytHub> alytHubs = new ArrayList<AlytHub>();
		try {

			while (discoveryRunning) {
				// Wait for a response
				byte[] recvBuf = new byte[1500];
				DatagramPacket receivePacket = new DatagramPacket(recvBuf,
						recvBuf.length);
				bcReceipt.receive(receivePacket);

				// We have a response
				byte[] data = receivePacket.getData();
				String message = new String(data).trim();
				logger.trace("Broadcast response from {} : {} '{}'",
						receivePacket.getAddress(), message.length(), message);

				// Check if the message is correct
				if (message.startsWith(ALYT_DISCOVER_STRING)
						&& !message.equals(ALYT_DISCOVER_STRING)) {
					alytHubIP = receivePacket.getAddress().getHostAddress();
					alytHubName = new String(data, 14, 16).trim().replaceAll("[:_;.,]", "");
					int[] ip = getIPAddr(data, 30, 4);
					short port = getShortValue(data, 34, 2);
					// macAddr = new String(data,37,17);

					logger.debug("ALYT found on network");
					logger.debug("Found at  : {}", alytHubIP);
					logger.debug("Name      : {}", alytHubName);
					logger.trace("IP   : {}", ip);
					logger.trace("Port   : {}", port);
					alytHubs.add(new AlytHubImpl(alytHubName, alytHubIP, port,
							null));
				}
			}
		} catch (SocketTimeoutException e) {
			logger.trace("No further response");
		} catch (IOException e) {
			logger.debug("IO error during MAX! Cube discovery: {}",
					e.getMessage());
		}
		return alytHubs;
	}

	private short getShortValue(byte[] data, int offset, int length) {
		short shortval = 0;
		ByteBuffer bb = ByteBuffer.wrap(data, offset, length);
		bb.order(ByteOrder.LITTLE_ENDIAN);
		while (bb.hasRemaining()) {
			shortval = bb.getShort();
			/* Do something with v... */
		}
		return shortval;
	}

	private int[] getIPAddr(byte[] data, int offset, int length) {
		int[] shortval = new int[length];
		ByteBuffer bb = ByteBuffer.wrap(data, offset, length);
//		bb.order(ByteOrder.LITTLE_ENDIAN);
		int index = 0;
		while (bb.hasRemaining()) {
			shortval[index] = bb.get()& 0xFF;
			index++;
			/* Do something with v... */
		}
		return shortval;
	}

	private void sendDiscoveryMessage(DatagramSocket bcSend,
			String discoverString) {
		// Find the MaxCube using UDP broadcast
		try {
			byte[] sendData = discoverString.getBytes();

			// Broadcast the message over all the network interfaces
			Enumeration<NetworkInterface> interfaces = NetworkInterface
					.getNetworkInterfaces();
			while (interfaces.hasMoreElements()) {
				NetworkInterface networkInterface = (NetworkInterface) interfaces
						.nextElement();
				if (networkInterface.isLoopback() || !networkInterface.isUp()
						|| networkInterface.isPointToPoint()) {
					continue;
				}
				InetAddress broadcast255 = InetAddress
						.getByName("255.255.255.255");
				List<InetAddress> broadcasts = new ArrayList<InetAddress>(2);
				for (InterfaceAddress interfaceAddress : networkInterface
						.getInterfaceAddresses()) {
					broadcasts.add(interfaceAddress.getBroadcast());
				}
				if (!broadcasts.contains(broadcast255)) {
					broadcasts.add(broadcast255);
				}
				for (InetAddress bc : broadcasts) {
					// Send the broadcast package!
					if (bc != null) {
						try {
							DatagramPacket sendPacket = new DatagramPacket(
									sendData, sendData.length, bc,
									ALYT_DISCOVER_PORT);
							bcSend.send(sendPacket);
							logger.trace(
									"Request packet sent to: {} Interface: {}",
									bc.getHostAddress(),
									networkInterface.getDisplayName());
						} catch (IOException e) {
							logger.debug(
									"IO error during MAX! Cube discovery: {}",
									e.getMessage());
						} catch (Exception e) {
							logger.info(e.getMessage(), e);
						}

					}
				}
			}
			logger.trace("Done looping over all network interfaces. Now waiting for a reply!");

		} catch (IOException e) {
			logger.debug("IO error during MAX! Cube discovery: {}",
					e.getMessage());
		}
	}
}

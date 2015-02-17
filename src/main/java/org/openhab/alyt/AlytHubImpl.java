package org.openhab.alyt;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.mashape.unirest.request.GetRequest;
import com.mashape.unirest.request.HttpRequest;
import com.mashape.unirest.request.HttpRequestWithBody;

public class AlytHubImpl implements AlytHub {
	private String localIP = null;
	private int port = 0;
	private String password = null;
	private String public_code = null;
	private String name = null;
	private String id = null;
	private boolean isLocal = true;
	private String fwVersion = null;
	private long lastEventId = 0;
	private URI url;
	
	private final static Logger logger = LoggerFactory
			.getLogger(AlytHubImpl.class);
	
	public AlytHubImpl(String name, String public_code) {
		this.name = name;
		this.public_code = public_code;
		this.isLocal = false;
	}
	public AlytHubImpl(String name, String localIP, int port, String password) {
		this.name = name;
		this.port = port;
		this.password = password;
		this.localIP = localIP;
		this.isLocal = true;
	}
	public void setPassword(String pwd) throws AlytError {
		this.password = pwd;
		this.isLocal = true;
	}
	/* (non-Javadoc)
	 * @see org.openhab.alyt.AlytHub#initialize()
	 */
	public void initialize() throws AlytError {
		try {
			if (isLocal) {
				this.url = new URI("http",null,localIP,port,"/alyt/",null,null);
			} else {
				this.url = new URI("http","alyt.com","alyt",null);
			}
			HttpResponse<JsonNode> jsonResp = createPostRequest("login")
						 .field("password", this.password)
						 .asJson();
			
			JSONObject cmdInfo = getCommandInfoObjectFromResponse(jsonResp);
		
//			JSONObject object = jsonResp.getBody().getObject();
			
//			this.sessionId = object.getString("SESSION_ID");		
			this.fwVersion = cmdInfo.getString("FW_VERSION");
			this.id = cmdInfo.getString("ALYT_ID");
			
			if (!AlytConstants.VERIFIED_VERSIONS.contains(this.fwVersion)) {
				logger.warn("Untested version of ALYT "+this.fwVersion);
			}
		} catch (UnirestException e) {
			throw new AlytError(e.getMessage());
		} catch (URISyntaxException e) {
			throw new AlytError(e.getMessage());
		}
		
	}
	@Override
	public String toString() {
		return "AlytHubImpl [id="+id+" localIP=" + localIP + ", port=" + port
				+ ", password=" + password + ", public_code=" + public_code
				+ ", name=" + name + ", isLocal=" + isLocal + ", fwVersion="
				+ fwVersion + "]";
	}
	private JSONObject getJsonFromResponse(HttpResponse<JsonNode> jsonResp) throws AlytError {
		JSONObject jsonResponse = null;
		if (jsonResp.getStatus() == 200) {
			JsonNode json = jsonResp.getBody();
			if (json.isArray()) {
				throw new AlytError("Alyt returned array not json object");
			} else {
				JSONObject jsonObj = json.getObject();
				logger.trace("PRETTY_JSON:"+jsonObj.toString(2));
				jsonResponse = jsonObj.getJSONObject("TAG_RISP_CMD");
				String result = jsonResponse.getString("RESULT");
				logger.trace("RESULT "+result);
				if (!result.equals("OK")) {
					handleErrorCode(jsonResponse);
				}
			}
		} else {
			handleErrorCode(jsonResp);
		}
		return jsonResponse;
	}
	
	private JSONObject getCommandInfoObjectFromResponse(HttpResponse<JsonNode> jsonResp) throws AlytError {
		JSONObject jsonResponse = getJsonFromResponse(jsonResp);
		return jsonResponse.getJSONObject("CMD_INFO");
	}
	private JSONArray getCommandInfoArrayFromResponse(HttpResponse<JsonNode> jsonResp) throws AlytError {
		JSONObject jsonResponse = getJsonFromResponse(jsonResp);
		return jsonResponse.getJSONArray("CMD_INFO");
	}
	private List<AlytDevice> handleDeviceList(JSONObject object) throws AlytError {
		List<AlytDevice> result = new ArrayList<AlytDevice>();
		String listType = object.getString("LIST_TYPE");
		JSONArray devices = object.getJSONArray("LIST");
		for (int i=0; i < devices.length(); i++) {
			JSONObject deviceObj = devices.getJSONObject(i);
//			String deviceClass = deviceObj.getString("CLASS");
			JSONObject deviceJSON = deviceObj.getJSONObject("JSONOBJ");
			if (listType.equals("switch")) {
				result.add(createAlytSwitch(deviceJSON));
			}
		}
		return result;
	}
	private List<AlytEvent> handleEventList(JSONObject object, long lastEventId) throws AlytError {
		List<AlytEvent> result = new ArrayList<AlytEvent>();
		String listType = object.getString("LIST_TYPE");
		JSONArray devices = object.getJSONArray("LIST");
		long maxEventId = 0;
		for (int i=0; i < devices.length(); i++) {
			if (listType.equals("event")) {
				AlytEvent event = createAlytEvent(devices.getJSONObject(i));
				if (event.getEventId() > lastEventId) {
					result.add(event);
				}
				if (event.getEventId() > maxEventId) {
					maxEventId = event.getEventId();
				}
			}
		}
		this.lastEventId = maxEventId;
		return result;
	} 
	AlytDevice createAlytSwitch(JSONObject deviceJSON) throws AlytError {
		return new AlytSwitch(this, deviceJSON);
	}
	AlytEvent createAlytEvent(JSONObject deviceJSON) throws AlytError {
		return new AlytEvent( deviceJSON);
	}
	private HttpRequestWithBody createPostRequest(String path) {
		URI relativeURL = this.url.resolve(path);
		return Unirest.post(relativeURL.toString())
				 .header("accept", "application/json");
				 
	}
	private GetRequest createGetRequest(String path) {
		URI relativeURL = this.url.resolve(path);
		return Unirest.get(relativeURL.toString())
				 .header("accept", "application/json");
				 
	}
	private void handleErrorCode(HttpResponse<JsonNode> jsonResp) throws AlytError {
		throw new AlytError(jsonResp.getStatus(), jsonResp.getBody().getObject());
	}
	private void handleErrorCode(JSONObject jsonResp) throws AlytError {
		throw new AlytError(jsonResp);
	}
	/* (non-Javadoc)
	 * @see org.openhab.alyt.AlytHub#getDevices()
	 */
	public List<AlytDevice> getDevices() throws AlytError {
		List<AlytDevice> result = new ArrayList<AlytDevice>();
		try {
			HttpResponse<JsonNode> jsonresp = createGetRequest("get_list").
					queryString("category", "device").queryString("filter","all").asJson();
			JSONArray cmdInfo = getCommandInfoArrayFromResponse(jsonresp);
			for(int i = 0; i < cmdInfo.length(); i++) {
				List<AlytDevice> devices = handleDeviceList(cmdInfo.getJSONObject(i));
				result.addAll(devices);
			}
		} catch (UnirestException e) {
			throw new AlytError(e);
		}
		
		
		return result;
	}
	public void capabilityCommand(AlytDevice device, String command) throws AlytError {
		
		try {
			HttpResponse<JsonNode> jsonresp = createPostRequest("capability_cmd").
					field("prot_type", device.getProtocolType()).
					field("id",device.getAlytID()).
					field("capability",command).asJson();
			getJsonFromResponse(jsonresp);
			
		} catch (UnirestException e) {
			throw new AlytError(e);
		}
	}
	/* (non-Javadoc)
	 * @see org.openhab.alyt.AlytHub#getDevices()
	 */
	public List<AlytEvent> getNewEvents(int interval) throws AlytError {
		List<AlytEvent> result = new ArrayList<AlytEvent>();
		try {
			
			HttpRequest jsonresp = createGetRequest("get_list").
					queryString("category", "event")
					.queryString("filter","last_n")
					.queryString("event_number",interval*2);
			HttpResponse<JsonNode> res = jsonresp.asJson();
			JSONArray cmdInfo = getCommandInfoArrayFromResponse(res);
			for(int i = 0; i < cmdInfo.length(); i++) {
				List<AlytEvent> events = handleEventList(cmdInfo.getJSONObject(i), lastEventId);
				result.addAll(events);
			}
		} catch (UnirestException e) {
			throw new AlytError(e);
		}
		
		
		return result;
	}
	public String getName() {
		return this.name;
	}
	public String getIPAddr() {
		return this.localIP;
	}
	public String getID() {
		return this.id;
	}
	
}

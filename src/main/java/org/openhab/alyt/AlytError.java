package org.openhab.alyt;

import org.json.JSONObject;

import com.mashape.unirest.http.exceptions.UnirestException;

public class AlytError extends Exception {
	/**
	 * SeralizeID
	 */
	private static final long serialVersionUID = 2052056452727437775L;
	private int httpErrorCode;
	private String alytErrorCode;
	
	public AlytError(int httpCode, JSONObject errorBody) {
		super(errorBody.has("ERROR_MESSAGE") ? errorBody.getString("ERROR_MESSAGE") : "NO ERROR FOUND");
		this.httpErrorCode = httpCode;
		this.alytErrorCode = errorBody.getString("ERROR_CODE");
	}
	public AlytError(JSONObject errorBody) {
		super(errorBody.has("ERROR_MESSAGE") ? errorBody.getString("ERROR_MESSAGE") : errorBody.getString("ERROR_MSG"));
		this.httpErrorCode = 0;
		this.alytErrorCode = errorBody.has("ERROR_MESSAGE") ? errorBody.getString("ERROR_MESSAGE") : null;
	}
	public AlytError(String errorMsg) {
		super(errorMsg);
		this.httpErrorCode = 0;
		this.alytErrorCode = null;
	}
	public AlytError(UnirestException e) {
		super(e.getMessage(),e);
		this.httpErrorCode = 0;
		this.alytErrorCode = null;
	}
	public int getHttpErrorCode() {
		return httpErrorCode;
	}
	public String getAlytErrorCode() {
		return alytErrorCode;
	}
}

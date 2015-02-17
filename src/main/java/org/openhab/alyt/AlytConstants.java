package org.openhab.alyt;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.List;

public class AlytConstants {
	public static final String TAG_TIPO_EVENTO = "TAG_TIPO_EVENTO";
	public static final String TAG_ID="TAG_ID";
	public static final String TAG_DEV_TYPE="TAG_DEV_TYPE";
	public static final String TAG_PROT_TYPE="TAG_PROT_TYPE";
	public static final String TAG_REACHABLE="TAG_REACHABLE";
	public static final String TAG_TIPO_DISPOSITIVO="TAG_TIPO_DISPOSITIVO";
	public static final String TAG_TIME="TAG_TIME";
	public static final String TAG_RESULT="TAG_RESULT";
	public static final String TAG_ID_PADRE="TAG_ID_PADRE";
	public static final String TAG_DESCRIPTION="TAG_DESCRIPTION";
	public static final String TAG_ID_DISPOSITIVO="TAG_ID_DISPOSITIVO";
	public static final String TAG_ACTION_ID = "TAG_ACTION_ID";
	public static final String TAG_OUTPUT = "TAG_OUTPUT";
	public static final String TAG_CAPABILITY_LIST = "TAG_CAPABILITY_LIST";
	public static final String TAG_DATA = "TAG_DATA";
	public static final String TAG_ENERGY ="TAG_ENERGY";
	public static final String TAG_WATT ="TAG_WATT";
	public static final String TAG_STATE ="TAG_STATE";
	public static final List<String> VERIFIED_VERSIONS = Arrays.asList(new String[]{"1.3.3"});
	
	public static final SimpleDateFormat ALYT_DATE_FORMATTER = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
}

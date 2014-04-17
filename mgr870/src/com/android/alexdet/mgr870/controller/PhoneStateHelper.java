package com.android.alexdet.mgr870.controller;

import java.util.List;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.telephony.CellIdentityLte;
import android.telephony.CellInfo;
import android.telephony.CellInfoCdma;
import android.telephony.CellInfoGsm;
import android.telephony.CellInfoLte;
import android.telephony.CellInfoWcdma;
import android.telephony.CellLocation;
import android.telephony.NeighboringCellInfo;
import android.telephony.PhoneStateListener;
import android.telephony.ServiceState;
import android.telephony.SignalStrength;
import android.telephony.TelephonyManager;
import android.telephony.cdma.CdmaCellLocation;
import android.telephony.gsm.GsmCellLocation;
import android.util.Log;

import com.android.alexdet.mgr870.utils.Config;

public class PhoneStateHelper extends PhoneStateListener implements
		LocationListener {

	private static final String TAG = "PhoneStateHelper - ";

	private static final boolean DBG = false;

	private TelephonyManager mTelMgr = null;
	private LocationManager mLocMgr = null;

	private String mMcc = "0";
	private String mMnc = "0";
	private String mLac = "0";
	private String mCellId = "0";
	private String mPsc = "0";
	private String mGsm_SignalStrength = "0";
	private String mGsm_bitErrorRate = "0";
	private String mCdma_Dbm = "0";
	private String mCdma_Ecio = "0";
	private String mEvdo_Dbm = "0";
	private String mEvdo_Ecio = "0";
	private String mEvdo_Snr = "0";
	private String mLTE_RSSI = "0";
	private String mLTE_RSRP = "0";
	private String mLTE_RSRQ = "0";
	private String mLTE_RSSNR = "0";
	private String mLTE_CQI = "0";

	private int mServiceStage = 0;
	private int mDataState = 0;
	private int mCallState = 0;

	private boolean isGsm = false;
	private boolean isRoaming = false;

	final static String EOL = System.getProperty("line.separator");

	public PhoneStateHelper(Context context) {
		mTelMgr = (TelephonyManager) context
				.getSystemService(Context.TELEPHONY_SERVICE);

		mLocMgr = (LocationManager) context
				.getSystemService(Context.LOCATION_SERVICE);

		setLocation();
		startListening();
	}

	/**
	 * Request location from GPS and Network provider
	 * 
	 * @see LocationManager#GPS_PROVIDER
	 * @see LocationManager#NETWORK_PROVIDER
	 */
	private void setLocation() {
		if (DBG)
			Log.d(Config.TAG, TAG + "setLocation called");

		mLocMgr.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 1,
				this);
		mLocMgr.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 5000,
				1, this);
	}

	/**
	 * Start listeners
	 * 
	 * @see PhoneStateListener#LISTEN_SIGNAL_STRENGTHS
	 * @see PhoneStateListener#LISTEN_CELL_LOCATION
	 * @see PhoneStateListener#LISTEN_CALL_STATE
	 * @see PhoneStateListener#LISTEN_SERVICE_STATE
	 * @see PhoneStateListener#LISTEN_DATA_CONNECTION_STATE
	 */
	private void startListening() {
		if (DBG)
			Log.d(Config.TAG, TAG + "startListening called");
		mTelMgr.listen(this, PhoneStateListener.LISTEN_SIGNAL_STRENGTHS);
		// TODO set listeners
		// mTelMgr.listen(this, PhoneStateListener.LISTEN_CELL_LOCATION);
		// mTelMgr.listen(this, PhoneStateListener.LISTEN_CALL_STATE);
		// mTelMgr.listen(this, PhoneStateListener.LISTEN_SERVICE_STATE);
		// mTelMgr.listen(this,
		// PhoneStateListener.LISTEN_DATA_CONNECTION_STATE);
	}

	/**
	 * Pause listeners
	 */
	public void pauseListening() {
		if (DBG)
			Log.d(Config.TAG, TAG + "pauseListening called");
		mTelMgr.listen(this, PhoneStateListener.LISTEN_NONE);
	}

	/**
	 * Set network info related to the connected cell: MCC, MNC, LAC, CellId
	 */
	public void setNetworkInfo() {
		if (DBG)
			Log.d(Config.TAG, TAG + "setNetworkInfo called");
		String temp = mTelMgr.getNetworkOperator();
		if ((temp != null) && (temp.length() >= 5)) {
			mMcc = temp.substring(0, 3);
			mMnc = temp.substring(3);
		}
		CellLocation oCell = mTelMgr.getCellLocation();
		if (oCell instanceof GsmCellLocation) {
			mLac = String.valueOf(((GsmCellLocation) oCell).getLac());
			mCellId = String.valueOf(((GsmCellLocation) oCell).getCid());
			mPsc = String.valueOf(((GsmCellLocation) oCell).getPsc());
		}
		if (oCell instanceof CdmaCellLocation) {
			String t = null;
			// (CdmaCellLocation) oCell

			t = "Base station id : "
					+ ((CdmaCellLocation) oCell).getBaseStationId()
					+ "base station latitude "
					+ ((CdmaCellLocation) oCell).getBaseStationLatitude()
					+ " base station longitude"
					+ ((CdmaCellLocation) oCell).getBaseStationLongitude()
					+ " network id" + ((CdmaCellLocation) oCell).getNetworkId()
					+ " system id " + ((CdmaCellLocation) oCell).getSystemId();

			Log.d(Config.TAG, TAG + t);
		}

	}

	/**
	 * Get current cell info
	 * 
	 * @return cell information format as {@link String}
	 */
	public String getCellInfo() {
		if (DBG)
			Log.d(Config.TAG, TAG + "getCellInfo called");
		String ret = null;
		List<CellInfo> listCellInfo = mTelMgr.getAllCellInfo();
		if (listCellInfo != null)
			for (CellInfo a_Info : listCellInfo) {
				if (CellInfoCdma.class.isInstance(a_Info))
					ret = ret + "\n Cell info cdma: " + a_Info.toString();
				else if (CellInfoGsm.class.isInstance(a_Info))
					ret = ret + "\n Cell info gsm: " + a_Info.toString();
				else if (CellInfoLte.class.isInstance(a_Info)) {
					ret = ret + "\n Cell info lte: " + a_Info.toString();
					CellInfoLte cellInfoLte = (CellInfoLte) a_Info;
					CellIdentityLte cellIdentity = cellInfoLte
							.getCellIdentity();
					Log.d(Config.TAG, TAG + " LTE - " + cellIdentity.getCi()
							+ cellIdentity.getTac() + cellIdentity.getPci());

				} else if (CellInfoWcdma.class.isInstance(a_Info))
					ret = ret + "\n Cell info : wcdma" + a_Info.toString();
			}
		return ret;
	}

	/**
	 * Get current neighboring cell info
	 * 
	 * @return neighboring cell information format as {@link String}
	 */
	public String getNeighboringCellInfo() {
		if (DBG)
			Log.d(Config.TAG, TAG + "getNeighboringCellInfo called");
		String ret = null;
		List<NeighboringCellInfo> listNeighboringCellInfo = mTelMgr
				.getNeighboringCellInfo();
		if (listNeighboringCellInfo != null)
			for (NeighboringCellInfo a_Info : listNeighboringCellInfo)
				ret = ret + "\nCell neighboring info : " + a_Info.getRssi()
						+ " - " + a_Info.getCid();
		return ret;
	}

	/**
	 * Callback invoked when cell info changed
	 */
	@Override
	public void onCellInfoChanged(List<CellInfo> cellInfo) {
		super.onCellInfoChanged(cellInfo);
		if (DBG)
			Log.d(Config.TAG, TAG + "onCellInfoChanged called");
		// TODO see can be done
	}

	/**
	 * Callback invoked when cell location change
	 */
	@Override
	public void onCellLocationChanged(CellLocation location) {
		super.onCellLocationChanged(location);
		if (DBG)
			Log.d(Config.TAG, TAG + "onCellLocationChanged called");
		// TODO see what can be done
	}

	/**
	 * Callback invoked when device service state changes.
	 * 
	 * @see ServiceState#STATE_EMERGENCY_ONLY
	 * @see ServiceState#STATE_IN_SERVICE
	 * @see ServiceState#STATE_OUT_OF_SERVICE
	 * @see ServiceState#STATE_POWER_OFF
	 */
	@Override
	public void onServiceStateChanged(ServiceState serviceState) {
		if (DBG)
			Log.d(Config.TAG, TAG + "onServiceStateChanged called");
		if (mServiceStage != serviceState.getState()) {
			mServiceStage = serviceState.getState();
			isRoaming = serviceState.getRoaming();
		}
	}

	/**
	 * Callback invoked when device call state changes.
	 * 
	 * @see TelephonyManager#CALL_STATE_IDLE
	 * @see TelephonyManager#CALL_STATE_RINGING
	 * @see TelephonyManager#CALL_STATE_OFFHOOK
	 */
	@Override
	public void onCallStateChanged(int state, String incomingNumber) {
		if (DBG)
			Log.d(Config.TAG, TAG + "onCallStateChanged called");
		if (mCallState != state) {
			mCallState = state;
			Log.d("PP30Lite", "Call State: " + getCallState() + "Phonenumber: "
					+ ((incomingNumber == null) ? "N/A" : incomingNumber));
		}
		super.onCallStateChanged(state, incomingNumber);

	}

	/**
	 * Callback invoked when connection state changes.
	 * 
	 * @see TelephonyManager#DATA_DISCONNECTED
	 * @see TelephonyManager#DATA_CONNECTING
	 * @see TelephonyManager#DATA_CONNECTED
	 * @see TelephonyManager#DATA_SUSPENDED
	 */
	@Override
	public void onDataConnectionStateChanged(int state) {
		if (DBG)
			Log.d(Config.TAG, TAG + "onDataConnectionState called");
		mDataState = state;
	}

	/**
	 * Callback invoked when signal strength changed
	 */
	@Override
	public void onSignalStrengthsChanged(SignalStrength signalStrength) {
		super.onSignalStrengthsChanged(signalStrength);

		String temp = signalStrength.toString();
		String[] parts = temp.split(" ");

		mGsm_SignalStrength = String
				.valueOf((Integer.parseInt(parts[1]) * 2 - 113));
		mGsm_bitErrorRate = parts[2];
		mCdma_Dbm = parts[3];
		mCdma_Ecio = parts[4];
		mEvdo_Dbm = parts[5];
		mEvdo_Ecio = parts[6];
		mEvdo_Snr = parts[7];
		mLTE_RSSI = String.valueOf(Integer.parseInt(parts[8]) * 2 - 113);
		mLTE_RSRP = parts[9];
		mLTE_RSRQ = parts[10];
		mLTE_RSSNR = parts[11];
		mLTE_CQI = parts[12];

		if (parts[14].equals("gsm|lte"))
			isGsm = true;

		// Set the network info
		setNetworkInfo();

		Log.d(Config.TAG, TAG + "signal: " + signalStrength.toString());

		// Update the UI
		MainActivity.updateView();
	}

	public int getAsuLevel() {
		int asuLevel;
		if (isGsm) {
			if ((mLTE_RSSI.equals("-1") && (mLTE_RSRP.equals("-1"))
					&& (mLTE_RSRQ.equals("-1")) && (mLTE_CQI.equals("-1")))) {
				asuLevel = getGsmAsuLevel();
			} else {
				asuLevel = getLteAsuLevel();
			}
		} else {
			int cdmaAsuLevel = getCdmaAsuLevel();
			int evdoAsuLevel = getEvdoAsuLevel();
			if (evdoAsuLevel == 0) {
				/* We don't know evdo use, cdma */
				asuLevel = cdmaAsuLevel;
			} else if (cdmaAsuLevel == 0) {
				/* We don't know cdma use, evdo */
				asuLevel = evdoAsuLevel;
			} else {
				/* We know both, use the lowest level */
				asuLevel = cdmaAsuLevel < evdoAsuLevel ? cdmaAsuLevel
						: evdoAsuLevel;
			}
		}
		if (DBG)
			Log.d(Config.TAG, TAG + "getAsuLevel=" + asuLevel);
		return asuLevel;
	}

	public int getGsmAsuLevel() {
		// ASU ranges from 0 to 31 - TS 27.007 Sec 8.5
		// asu = 0 (-113dB or less) is very weak
		// signal, its better to show 0 bars to the user in such cases.
		// asu = 99 is a special case, where the signal strength is unknown.
		int level = Integer.parseInt(mGsm_SignalStrength);
		if (DBG)
			Log.d(Config.TAG, TAG + "getGsmAsuLevel=" + level);
		return level;
	}

	public int getLteAsuLevel() {
		int lteAsuLevel = 99;
		int lteDbm = Integer.parseInt(mLTE_RSSI);
		if (lteDbm <= -140)
			lteAsuLevel = 0;
		else if (lteDbm >= -43)
			lteAsuLevel = 97;
		else
			lteAsuLevel = lteDbm + 140;
		if (DBG)
			Log.d(Config.TAG, TAG + "Lte Asu level: " + lteAsuLevel);
		return lteAsuLevel;
	}

	public int getCdmaAsuLevel() {
		final int cdmaDbm = Integer.parseInt(mCdma_Dbm);
		final int cdmaEcio = Integer.parseInt(mCdma_Ecio);
		int cdmaAsuLevel;
		int ecioAsuLevel;

		if (cdmaDbm >= -75)
			cdmaAsuLevel = 16;
		else if (cdmaDbm >= -82)
			cdmaAsuLevel = 8;
		else if (cdmaDbm >= -90)
			cdmaAsuLevel = 4;
		else if (cdmaDbm >= -95)
			cdmaAsuLevel = 2;
		else if (cdmaDbm >= -100)
			cdmaAsuLevel = 1;
		else
			cdmaAsuLevel = 99;

		// Ec/Io are in dB*10
		if (cdmaEcio >= -90)
			ecioAsuLevel = 16;
		else if (cdmaEcio >= -100)
			ecioAsuLevel = 8;
		else if (cdmaEcio >= -115)
			ecioAsuLevel = 4;
		if (cdmaEcio >= -130)
			ecioAsuLevel = 2;
		else if (cdmaEcio >= -150)
			ecioAsuLevel = 1;
		else
			ecioAsuLevel = 99;

		int level = (cdmaAsuLevel < ecioAsuLevel) ? cdmaAsuLevel : ecioAsuLevel;
		if (DBG)
			Log.d(Config.TAG, TAG + "getCdmaAsuLevel=" + level);
		return level;
	}

	public int getEvdoAsuLevel() {
		int evdoDbm = Integer.parseInt(mEvdo_Dbm);
		int evdoSnr = Integer.parseInt(mEvdo_Snr);
		int levelEvdoDbm;
		int levelEvdoSnr;

		if (evdoDbm >= -65)
			levelEvdoDbm = 16;
		else if (evdoDbm >= -75)
			levelEvdoDbm = 8;
		else if (evdoDbm >= -85)
			levelEvdoDbm = 4;
		else if (evdoDbm >= -95)
			levelEvdoDbm = 2;
		else if (evdoDbm >= -105)
			levelEvdoDbm = 1;
		else
			levelEvdoDbm = 99;

		if (evdoSnr >= 7)
			levelEvdoSnr = 16;
		else if (evdoSnr >= 6)
			levelEvdoSnr = 8;
		else if (evdoSnr >= 5)
			levelEvdoSnr = 4;
		else if (evdoSnr >= 3)
			levelEvdoSnr = 2;
		else if (evdoSnr >= 1)
			levelEvdoSnr = 1;
		else
			levelEvdoSnr = 99;

		int level = (levelEvdoDbm < levelEvdoSnr) ? levelEvdoDbm : levelEvdoSnr;
		if (DBG)
			Log.d(Config.TAG, TAG + "getEvdoAsuLevel=" + level);
		return level;
	}

	/**
	 * Get Location
	 * 
	 * @return current location
	 */
	public String getLocation() {
		if (DBG)
			Log.d(Config.TAG, TAG + "getLocation called");
		Location l = null;
		if (mLocMgr.getLastKnownLocation(LocationManager.GPS_PROVIDER) != null) {
			l = new Location(
					mLocMgr.getLastKnownLocation(LocationManager.GPS_PROVIDER));
		} else if (mLocMgr
				.getLastKnownLocation(LocationManager.NETWORK_PROVIDER) != null) {
			l = new Location(
					mLocMgr.getLastKnownLocation(LocationManager.NETWORK_PROVIDER));
		}
		return printLocation(l);
	}

	/**
	 * Print location
	 * 
	 * @param location
	 *            location to print
	 * @return location formated as {@link String}
	 */
	private String printLocation(Location location) {
		String loc = "Location unknown" + EOL;
		if (location != null) {
			loc = "Latitude: " + location.getLatitude() + EOL + "Longitude: "
					+ location.getLongitude() + EOL + "Altitude: "
					+ location.getAltitude() + EOL;
		}
		return new String(loc);
	}

	/**
	 * Get the service state
	 * 
	 * @see ServiceState#STATE_EMERGENCY_ONLY
	 * @see ServiceState#STATE_IN_SERVICE
	 * @see ServiceState#STATE_OUT_OF_SERVICE
	 * @see ServiceState#STATE_POWER_OFF
	 * @return service state
	 */
	public String getServiceState() {
		if (DBG)
			Log.d(Config.TAG, TAG + "getServiceState called");
		switch (mServiceStage) {
		case ServiceState.STATE_EMERGENCY_ONLY:
			return "Emergency Only";
		case ServiceState.STATE_IN_SERVICE:
			return "In Service";
		case ServiceState.STATE_POWER_OFF:
			return "Power Off";
		case ServiceState.STATE_OUT_OF_SERVICE:
			return "Out Of Service";
		default:
			return "Unknown";
		}
	}

	/**
	 * Get if the device is roaming
	 * 
	 * @return {@code true} if roaming, {@code false} otherwise
	 */
	public boolean getRoaming() {
		if (DBG)
			Log.d(Config.TAG, TAG + "getRoaming called");
		if (isRoaming == true)
			return true;
		else
			return false;
	}

	/**
	 * Get call state
	 * 
	 * @see TelephonyManager#CALL_STATE_IDLE
	 * @see TelephonyManager#CALL_STATE_OFFHOOK
	 * @see TelephonyManager#CALL_STATE_RINGING
	 * @return call state
	 */
	public String getCallState() {
		if (DBG)
			Log.d(Config.TAG, TAG + "getCallState called");
		switch (mCallState) {
		case TelephonyManager.CALL_STATE_OFFHOOK:
			return "Off Hook";
		case TelephonyManager.CALL_STATE_RINGING:
			return "Ringing";
		case TelephonyManager.CALL_STATE_IDLE:
			return "Idle";
		default:
			return "Unknown";
		}
	}

	/**
	 * Get data stage
	 * 
	 * @see TelephonyManager#DATA_CONNECTED
	 * @see TelephonyManager#DATA_CONNECTING
	 * @see TelephonyManager#DATA_DISCONNECTED
	 * @see TelephonyManager#DATA_SUSPENDED
	 * @return data state
	 */
	public String getDataState() {
		if (DBG)
			Log.d(Config.TAG, TAG + "getDataState called");
		switch (mDataState) {
		case TelephonyManager.DATA_DISCONNECTED:
			return "Disconnected";
		case TelephonyManager.DATA_CONNECTING:
			return "Connecting";
		case TelephonyManager.DATA_CONNECTED:
			return "Connected";
		case TelephonyManager.DATA_SUSPENDED:
			return "Suspended";
		default:
			return "Unknown";
		}
	}

	/**
	 * Get phone number
	 * 
	 * @return phone number
	 */
	public String getPhoneNumber() {
		if (DBG)
			Log.d(Config.TAG, TAG + "getPhoneNumber called");
		String pn = mTelMgr.getLine1Number();
		return (pn == null ? "UNKNOWN" : pn);
	}

	/**
	 * Get softwate version
	 * 
	 * @return software version
	 */
	public String getSoftwareVer() {
		if (DBG)
			Log.d(Config.TAG, TAG + "getSoftwareVer called");
		String swv = mTelMgr.getDeviceSoftwareVersion();
		return (swv == null ? "UNKNOWN" : swv);
	}

	/**
	 * Get subscriber ID
	 * 
	 * @return subscriber ID
	 */
	public String getSubscriberID() {
		if (DBG)
			Log.d(Config.TAG, TAG + "getSubscriberID called");
		String sid = mTelMgr.getSubscriberId();
		return (sid == null ? "UNKNOWN" : sid);
	}

	/**
	 * Get device ID
	 * 
	 * @return device ID
	 */
	public String getDeviceID() {
		if (DBG)
			Log.d(Config.TAG, TAG + "getDeviceID called");
		String did = mTelMgr.getDeviceId();
		return (did == null ? "UNKNOWN" : did);
	}

	/**
	 * get operator name
	 * 
	 * @return operator name
	 */
	public String getSimOpName() {
		if (DBG)
			Log.d(Config.TAG, TAG + "getSimOpName called");
		String sim = mTelMgr.getSimOperatorName();
		if (sim.equals("") == true) {
			// Try network operator name
			sim = mTelMgr.getNetworkOperatorName();
		}
		if (sim != null) {
			// This is a quick KLUDGE for AT&T
			// would not need this after we implement the RESTful interface or
			// encode/decode http parameters but time is short
			sim = sim.replaceAll("&", "");
		}

		return (sim == null ? "UNKNOWN" : sim);
	}

	/**
	 * Get network type
	 * 
	 * @see TelephonyManager#NETWORK_TYPE_1xRTT
	 * @see TelephonyManager#NETWORK_TYPE_CDMA
	 * @see TelephonyManager#NETWORK_TYPE_EDGE
	 * @see TelephonyManager#NETWORK_TYPE_EHRPD
	 * @see TelephonyManager#NETWORK_TYPE_EVDO_0
	 * @see TelephonyManager#NETWORK_TYPE_EVDO_A
	 * @see TelephonyManager#NETWORK_TYPE_EVDO_B
	 * @see TelephonyManager#NETWORK_TYPE_GPRS
	 * @see TelephonyManager#NETWORK_TYPE_HSDPA
	 * @see TelephonyManager#NETWORK_TYPE_HSPA
	 * @see TelephonyManager#NETWORK_TYPE_HSPAP
	 * @see TelephonyManager#NETWORK_TYPE_HSUPA
	 * @see TelephonyManager#NETWORK_TYPE_IDEN
	 * @see TelephonyManager#NETWORK_TYPE_LTE
	 * @see TelephonyManager#NETWORK_TYPE_UMTS
	 * @see TelephonyManager#NETWORK_TYPE_UNKNOWN
	 * @return network type
	 */
	public String getNetworkType() {
		if (DBG)
			Log.d(Config.TAG, TAG + "getNetworkType called");

		switch (mTelMgr.getNetworkType()) {
		case TelephonyManager.NETWORK_TYPE_UNKNOWN:
			return "UNKNOWN";
		case TelephonyManager.NETWORK_TYPE_GPRS:
			return "GPRS";
		case TelephonyManager.NETWORK_TYPE_EDGE:
			return "EDGE";
		case TelephonyManager.NETWORK_TYPE_UMTS:
			return "UMTS";
		case TelephonyManager.NETWORK_TYPE_HSDPA:
			return "HSDPA";
		case TelephonyManager.NETWORK_TYPE_HSUPA:
			return "HSUPA";
		case TelephonyManager.NETWORK_TYPE_HSPA:
			return "HSPA";
		case TelephonyManager.NETWORK_TYPE_HSPAP:
			return "HSPAP";
		case TelephonyManager.NETWORK_TYPE_EHRPD:
			return "EHRPD";
		case TelephonyManager.NETWORK_TYPE_IDEN:
			return "IDEN";
		case TelephonyManager.NETWORK_TYPE_CDMA:
			return "CDMA";
		case TelephonyManager.NETWORK_TYPE_EVDO_0:
			return "EVDO_0";
		case TelephonyManager.NETWORK_TYPE_EVDO_A:
			return "EVDO_A";
		case TelephonyManager.NETWORK_TYPE_1xRTT:
			return "1xRTT";
		case TelephonyManager.NETWORK_TYPE_LTE:
			return "LTE";
		}
		return "UNKNOWN";
	}

	/**
	 * Get the phone type
	 * 
	 * @see TelephonyManager#PHONE_TYPE_CDMA
	 * @see TelephonyManager#PHONE_TYPE_GSM
	 * @see TelephonyManager#PHONE_TYPE_NONE
	 * @see TelephonyManager#PHONE_TYPE_SIP
	 * @return phone type
	 */
	public String getPhoneType() {
		if (DBG)
			Log.d(Config.TAG, TAG + "getPhoneType called");
		switch (mTelMgr.getPhoneType()) {
		case TelephonyManager.PHONE_TYPE_CDMA:
			return "CDMA";
		case TelephonyManager.PHONE_TYPE_GSM:
			return "GSM";
		case TelephonyManager.PHONE_TYPE_SIP:
			return "SIP";
		case TelephonyManager.PHONE_TYPE_NONE:
			return "NONE";
		}
		return "UNKNOWN";
	}

	/**
	 * @return the Mcc
	 */
	public String getMcc() {

		if (DBG)
			Log.d(Config.TAG, TAG + "getMcc called");
		return mMcc;
	}

	/**
	 * @return the Mnc
	 */
	public String getMnc() {
		if (DBG)
			Log.d(Config.TAG, TAG + "getMnc called");
		return mMnc;
	}

	/**
	 * @return the Lac
	 */
	public String getLac() {
		if (DBG)
			Log.d(Config.TAG, TAG + "getLAC called");
		return mLac;
	}

	/**
	 * @return the CellId
	 */
	public String getCellId() {
		if (DBG)
			Log.d(Config.TAG, TAG + "getCellId called");
		return mCellId;
	}

	/**
	 * @return the Gsm_SignalStrength
	 */
	public String getGsm_SignalStrength() {
		if (DBG)
			Log.d(Config.TAG, TAG + "getGSM_SignalStrength called");
		return mGsm_SignalStrength;
	}

	/**
	 * @return the Gsm_bitErrorRate
	 */
	public String getGsm_bitErrorRate() {
		if (DBG)
			Log.d(Config.TAG, TAG + "getGSM_bitErrorRate called");
		return mGsm_bitErrorRate;
	}

	/**
	 * @return the Cdma_Dbm
	 */
	public String getCdma_Dbm() {
		if (DBG)
			Log.d(Config.TAG, TAG + "getCdma_Dbm called");
		return mCdma_Dbm;
	}

	/**
	 * @return the Cdma_Ecio
	 */
	public String getCdma_Ecio() {
		if (DBG)
			Log.d(Config.TAG, TAG + "getCdma_Ecio called");
		return mCdma_Ecio;
	}

	/**
	 * @return the Evdo_Dbm
	 */
	public String getEvdo_Dbm() {
		if (DBG)
			Log.d(Config.TAG, TAG + "getEvdo_Dbm called");
		return mEvdo_Dbm;
	}

	/**
	 * @return the Evdo_Ecio
	 */
	public String getEvdo_Ecio() {
		if (DBG)
			Log.d(Config.TAG, TAG + "getEvdo_Ecio called");
		return mEvdo_Ecio;
	}

	/**
	 * @return the Evdo_Snr
	 */
	public String getEvdo_Snr() {
		if (DBG)
			Log.d(Config.TAG, TAG + "getEvdo_Snr called");
		return mEvdo_Snr;
	}

	/**
	 * @return the LTE_SignalStrenth
	 */
	public String getLTE_RSSI() {
		if (DBG)
			Log.d(Config.TAG, TAG + "getLTE_SignalStrenth called");
		return mLTE_RSSI;
	}

	/**
	 * @return the LTE_RSRP
	 */
	public String getLTE_RSRP() {
		if (DBG)
			Log.d(Config.TAG, TAG + "getLTE_RSRP called");
		return mLTE_RSRP;
	}

	/**
	 * @return the LTE_RSRQ
	 */
	public String getLTE_RSRQ() {
		if (DBG)
			Log.d(Config.TAG, TAG + "getLTE_RSRP called");
		return mLTE_RSRQ;
	}

	/**
	 * @return the LTE_RSSNR
	 */
	public String getLTE_RSSNR() {
		if (DBG)
			Log.d(Config.TAG, TAG + "getLTE_RSSNR called");
		return mLTE_RSSNR;
	}

	/**
	 * @return the LTE_CQI
	 */
	public String getLTE_CQI() {
		if (DBG)
			Log.d(Config.TAG, TAG + "getLTE_CQI called");
		return mLTE_CQI;
	}

	/**
	 * @return the isGsm
	 */
	public boolean isGsm() {
		return isGsm;
	}

	/**
	 * @return the isRoaming
	 */
	public boolean isRoaming() {
		return isRoaming;
	}

	public String getPsc() {
		return mPsc;
	}

	// Location Listener
	@Override
	public void onLocationChanged(Location location) {
		// TODO nothing
	}

	// Location Listener
	@Override
	public void onProviderDisabled(String provider) {
		// TODO nothing
	}

	// Location Listener
	@Override
	public void onProviderEnabled(String provider) {
		// TODO nothing
	}

	// Location Listener
	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO nothing
	}
};
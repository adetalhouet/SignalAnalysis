package com.android.alexdet.mgr870.controller;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.alexdet.mgr870.R;

@SuppressWarnings("deprecation")
public class MainActivity extends FragmentActivity {

	private static PhoneStateHelper mTel;

	private static View mView;

	private static Context mContext;

	private static ImageView mRSSIimageView;
	private static ImageView mRSRPimageView;
	private static ImageView mRSRQimageView;
	private static ImageView mSINRimageView;

	private static TextView mRSSItextView;
	private static TextView mRSRPtextView;
	private static TextView mRSRQtextView;
	private static TextView mSINRtextView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.info);

		mContext = this;
		mView = getWindow().getDecorView().findViewById(android.R.id.content);
		mTel = new PhoneStateHelper(mContext);

		mRSSIimageView = (ImageView) findViewById(R.id.strengthImageView);
		mRSRPimageView = (ImageView) findViewById(R.id.powerImageView);
		mRSRQimageView = (ImageView) findViewById(R.id.qualityImageView);
		mSINRimageView = (ImageView) findViewById(R.id.noiseImageView);

		mRSSItextView = (TextView) findViewById(R.id.strengthTextEdit);
		mRSRPtextView = (TextView) findViewById(R.id.powerTextView);
		mRSRQtextView = (TextView) findViewById(R.id.qualityTextView);
		mSINRtextView = (TextView) findViewById(R.id.noiseTextView);

		// ViewTreeObserver vto = mRSRPimageView.getViewTreeObserver();
		// vto.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
		// public boolean onPreDraw() {
		// Log.d(Config.TAG, "MainActivity" + " height : "
		// + mRSRPimageView.getMeasuredHeight() + " width : "
		// + mRSRPimageView.getMeasuredWidth());
		//
		// findViewById(R.id.powerFrameLayout).getMeasuredHeight();
		// return true;
		// }
		// });
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	public static void updateView() {

		TextView temp = null;

		// MCC
		temp = (TextView) mView.findViewById(R.id.mccTextView);
		temp.setText(mContext.getString(R.string.mcc) + ": " + mTel.getMcc());

		// MNC
		temp = (TextView) mView.findViewById(R.id.mncTextView);
		temp.setText(mContext.getString(R.string.mnc) + ": " + mTel.getMnc());

		// CellId
		temp = (TextView) mView.findViewById(R.id.cellIdTextView);
		temp.setText(mContext.getString(R.string.cellId) + ": "
				+ mTel.getCellId());

		// Frequency band
		temp = (TextView) mView.findViewById(R.id.frequenceBandTextView);
		temp.setText(mContext.getString(R.string.freqBand) + ": N/A");

		// Bandwidth
		temp = (TextView) mView.findViewById(R.id.bandwidthTextView);
		temp.setText(mContext.getString(R.string.bandWidth) + ": N/A");

		// PhyCellId
		temp = (TextView) mView.findViewById(R.id.physicalCellIdTextView);
		temp.setText(mContext.getString(R.string.phyCellId) + ": N/A");

		// TAC
		temp = (TextView) mView.findViewById(R.id.tacTextView);
		temp.setText(mContext.getString(R.string.tac) + ": " + mTel.getLac());

		// Technology
		temp = (TextView) mView.findViewById(R.id.technologyTextView);
		temp.setText(mContext.getString(R.string.technology) + ": "
				+ mTel.getNetworkType());

		String tech = mTel.getNetworkType();
		if (tech == "LTE") {

			computeRSRP();
			computeRSRQ();
			computeRSSI(true);
			computeSINR();
		} else {
			computeRSSI(false);
			computeRSRP();
			computeRSRQ();
			computeSINR();
		}

		// Log.d(Config.TAG, "MainActivity - " + mTel.getLTE_RSRQ());
	}

	private static void computeRSSI(boolean t) {

		int strength = -1;
		if (t)
			strength = Integer.parseInt(mTel.getLTE_RSSI());
		else
			strength = Integer.parseInt(mTel.getGsm_SignalStrength());
		Drawable bitmap = null;

		if (strength > -20)
			bitmap = mContext.getResources().getDrawable(
					R.drawable.strength_full);
		else if (strength <= -20 && strength > -30)
			bitmap = mContext.getResources().getDrawable(
					R.drawable.strength_20_to_30);
		else if (strength <= -30 && strength > -40)
			bitmap = mContext.getResources().getDrawable(
					R.drawable.strength_30_to_40);
		else if (strength <= -40 && strength > -50)
			bitmap = mContext.getResources().getDrawable(
					R.drawable.strength_40_to_50);
		else if (strength <= -50 && strength > -60)
			bitmap = mContext.getResources().getDrawable(
					R.drawable.strength_50_to_60);
		else if (strength <= -60 && strength > -70)
			bitmap = mContext.getResources().getDrawable(
					R.drawable.strength_60_to_70);
		else if (strength <= -70 && strength > -80)
			bitmap = mContext.getResources().getDrawable(
					R.drawable.strength_70_to_80);
		else if (strength <= -80 && strength > -90)
			bitmap = mContext.getResources().getDrawable(
					R.drawable.strength_80_to_90);
		else if (strength <= -90 && strength > -100)
			bitmap = mContext.getResources().getDrawable(
					R.drawable.strength_90_to_100);
		else if (strength <= -100)
			bitmap = mContext.getResources().getDrawable(
					R.drawable.strength_100_and_less);

		if (strength == 85) {
			bitmap = mContext.getResources().getDrawable(
					R.drawable.strength_null);
			mRSSItextView.setText("N/A");
		} else
			mRSSItextView.setText(strength + "\ndBm");

		mRSSIimageView.setBackgroundDrawable(bitmap);
	}

	private static void computeRSRP() {
		int power = -1;
		power = Integer.parseInt(mTel.getLTE_RSRP());

		Drawable bitmap = null;

		if (power > -40)
			bitmap = mContext.getResources().getDrawable(R.drawable.power_full);
		else if (power <= -40 && power > -50)
			bitmap = mContext.getResources().getDrawable(
					R.drawable.power_40_to_50);
		else if (power <= -50 && power > -60)
			bitmap = mContext.getResources().getDrawable(
					R.drawable.power_50_to_60);
		else if (power <= -60 && power > -70)
			bitmap = mContext.getResources().getDrawable(
					R.drawable.power_60_to_70);
		else if (power <= -70 && power > -80)
			bitmap = mContext.getResources().getDrawable(
					R.drawable.power_70_to_80);
		else if (power <= -80 && power > -90)
			bitmap = mContext.getResources().getDrawable(
					R.drawable.power_80_to_90);
		else if (power <= -90 && power > -100)
			bitmap = mContext.getResources().getDrawable(
					R.drawable.power_90_to_100);
		else if (power <= -100 && power > -110)
			bitmap = mContext.getResources().getDrawable(
					R.drawable.power_100_to_110);
		else if (power <= -110 && power > -120)
			bitmap = mContext.getResources().getDrawable(
					R.drawable.power_110_to_120);
		else if (power <= -120)
			bitmap = mContext.getResources().getDrawable(
					R.drawable.power_120_and_less);

		if (power == 2147483647) {
			bitmap = mContext.getResources().getDrawable(R.drawable.power_null);
			mRSRPtextView.setText("N/A");
		} else
			mRSRPtextView.setText(power + "\ndBm");

		mRSRPimageView.setBackgroundDrawable(bitmap);

	}

	private static void computeRSRQ() {
		int quality = Integer.parseInt(mTel.getLTE_RSRQ());
		quality *= -1;

		Drawable bitmap = null;

		if (quality > -3)
			bitmap = mContext.getResources().getDrawable(
					R.drawable.quality_full);
		else if (quality >= -6 && quality < -3)
			bitmap = mContext.getResources().getDrawable(
					R.drawable.quality_3_to_6);
		else if (quality >= -9 && quality < -6)
			bitmap = mContext.getResources().getDrawable(
					R.drawable.quality_6_to_9);
		else if (quality >= -12 && quality < -9)
			bitmap = mContext.getResources().getDrawable(
					R.drawable.quality_9_to_12);
		else if (quality >= -15 && quality < -12)
			bitmap = mContext.getResources().getDrawable(
					R.drawable.quality_12_to_15);
		else if (quality >= -18 && quality < -15)
			bitmap = mContext.getResources().getDrawable(
					R.drawable.quality_15_to_18);
		else if (quality >= -21 && quality < -18)
			bitmap = mContext.getResources().getDrawable(
					R.drawable.quality_18_to_21);
		else if (quality >= -24 && quality < -21)
			bitmap = mContext.getResources().getDrawable(
					R.drawable.quality_21_to_24);
		else if (quality >= -27 && quality < -24)
			bitmap = mContext.getResources().getDrawable(
					R.drawable.quality_24_to_27);
		else if (quality >= -30 && quality < -27)
			bitmap = mContext.getResources().getDrawable(
					R.drawable.quality_27_to_30);
		else
			bitmap = mContext.getResources().getDrawable(
					R.drawable.quality_30_and_less);

		if (quality == 2147483647 || quality == -2147483647) {
			bitmap = mContext.getResources().getDrawable(
					R.drawable.quality_null);
			mRSRQtextView.setText("N/A");
		} else {
			mRSRQtextView.setText(quality + "\ndB");
		}
		mRSRQimageView.setBackgroundDrawable(bitmap);

	}

	private static void computeSINR() {
		int noise = Integer.parseInt(mTel.getLTE_RSSNR()) / 10;

		Drawable bitmap = null;

		if (noise >= 25)
			bitmap = mContext.getResources().getDrawable(R.drawable.noise_full);
		else if (noise >= 22.5 && noise < 25)
			bitmap = mContext.getResources().getDrawable(
					R.drawable.noise_225_to_25);
		else if (noise >= 20 && noise < 22.5)
			bitmap = mContext.getResources().getDrawable(
					R.drawable.noise_20_to_225);
		else if (noise >= 17.5 && noise < 20)
			bitmap = mContext.getResources().getDrawable(
					R.drawable.noise_175_to_20);
		else if (noise >= 15 && noise < 17.5)
			bitmap = mContext.getResources().getDrawable(
					R.drawable.noise_15_to_175);
		else if (noise >= 12.5 && noise < 15)
			bitmap = mContext.getResources().getDrawable(
					R.drawable.noise_125_to_15);
		else if (noise >= 10 && noise < 12.5)
			bitmap = mContext.getResources().getDrawable(
					R.drawable.noise_10_to_125);
		else if (noise >= 7.5 && noise < 10)
			bitmap = mContext.getResources().getDrawable(
					R.drawable.noise_75_to_10);
		else if (noise >= 5 && noise < 7.5)
			bitmap = mContext.getResources().getDrawable(
					R.drawable.noise_5_to_75);
		else if (noise >= 2.5 && noise < 5)
			bitmap = mContext.getResources().getDrawable(
					R.drawable.noise_25_to_5);
		else
			bitmap = mContext.getResources().getDrawable(
					R.drawable.noise_0_to_25);

		if (noise == 214748364) {
			bitmap = mContext.getResources().getDrawable(R.drawable.noise_null);
			mSINRtextView.setText("N/A");
		} else
			mSINRtextView.setText(noise + "\ndB");
		mSINRimageView.setBackgroundDrawable(bitmap);

	}
}

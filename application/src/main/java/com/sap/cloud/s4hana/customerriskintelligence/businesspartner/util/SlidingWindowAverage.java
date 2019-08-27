package com.sap.cloud.s4hana.customerriskintelligence.businesspartner.util;

import java.util.Date;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;

import com.sap.cloud.sdk.cloudplatform.logging.CloudLoggerFactory;

import lombok.AllArgsConstructor;

/**
 * Exponential Moving Average (EMA) Algorithm for calculating Sliding Window
 * Average (SWA).
 * 
 * EMA = ( P * alpha ) + ( Previous EMA * (1 - alpha)) , Where, P = Current or
 * Latest Value alpha = 2 / (1 + N) (smoothing factor) N = Time Period (days)
 * EMA = Exponential Moving Average
 */

@AllArgsConstructor
public class SlidingWindowAverage {

	private static final Logger logger = CloudLoggerFactory.getLogger(SlidingWindowAverage.class);
	private float p;
	private float pr_ema;
	private Date lastUpdated;
	private Date currentdate;

	public float getExponentialMovingAverage() {
		try {
			long n = daysBetween(lastUpdated, currentdate);
			float alpha = 2 / (1 + (float) n);

			return ((p * alpha) + (pr_ema * (1 - alpha)));
		} catch (Exception e) {
			logger.error("Exception occured while calculating ExponentialMovingAverage :", e);
			throw e;
		}
	}

	private long daysBetween(Date lastUpdated, Date currentdate) {
		try {
			long dateDiffInMiliseconds = currentdate.getTime() - lastUpdated.getTime();

			return TimeUnit.DAYS.convert(dateDiffInMiliseconds, TimeUnit.MILLISECONDS);
		} catch (Exception e) {
			logger.error("Exception occured while calculating TimePeriod :", e);
			throw e;
		}
	}
}

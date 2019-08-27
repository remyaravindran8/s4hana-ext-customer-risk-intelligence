package com.sap.cloud.s4hana.customerriskintelligence.businesspartner.service;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.Map;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sap.cloud.s4hana.customerriskintelligence.businesspartner.config.ReadCustomFields;
import com.sap.cloud.s4hana.customerriskintelligence.businesspartner.exception.SapException;
import com.sap.cloud.s4hana.customerriskintelligence.businesspartner.exception.ServiceException;
import com.sap.cloud.s4hana.customerriskintelligence.businesspartner.model.SocialModel;
import com.sap.cloud.s4hana.customerriskintelligence.businesspartner.util.SlidingWindowAverage;
import com.sap.cloud.sdk.cloudplatform.logging.CloudLoggerFactory;
import com.sap.cloud.sdk.odatav2.connectivity.ODataException;
import com.sap.cloud.sdk.s4hana.datamodel.odata.namespaces.businesspartner.BusinessPartner;
import com.sap.cloud.sdk.s4hana.datamodel.odata.services.DefaultBusinessPartnerService;

@Service
public class BPService {

	private static final Logger logger = CloudLoggerFactory.getLogger(BPService.class);

	@Autowired
	private ReadCustomFields readCustomFields;

	@Autowired
	private DefaultBusinessPartnerService businessPartnerService;

	@Autowired
	private SocialModel creditScore;

	// YY1_TwitterAccount_bus type [Precision=10, Scale=2]
	private BigDecimal setDecimalPrecison(Float num) {

		BigDecimal bd = new BigDecimal(num);
		bd = bd.setScale(2, BigDecimal.ROUND_HALF_UP);

		return bd;
	}

	/**
	 * 
	 * @param Business Partner Id
	 * @exception ODataException
	 */
	public SocialModel getSocialAccountDetails(String bp) {

		try {
			final BusinessPartner businessPartner = businessPartnerService.getBusinessPartnerByKey(bp).execute();
			Map<String, Object> customfields = businessPartner.getCustomFields();

			if (customfields.get(readCustomFields.getTwitterAccount()).toString() != null) {

				creditScore.setTwitterDetails((customfields.get(readCustomFields.getTwitterAccount()).toString()));
				creditScore.setBpId(bp);
				creditScore.setCreditScore(
						(Float.valueOf(customfields.get(readCustomFields.getTwitterScore()).toString())));
				creditScore.setEMAScore(0.0f);
				creditScore.setCurrentDate(LocalDate.now().toString());// used for today's Date

				if (customfields.get(readCustomFields.getLastUpdated()) != null) {

					Calendar cal = (Calendar) (customfields.get(readCustomFields.getLastUpdated()));
					creditScore.setPreviousDate(new SimpleDateFormat("yyyy-MM-dd").format(cal.getTime()));

				} else {
					creditScore.setPreviousDate(LocalDate.now().toString());
				}
			}
			return creditScore;
		} catch (Exception e) {
			logger.error("Exception occured while Fetching Business Partner Details :", e);
			throw new ServiceException("Failed to Fetch Business Partner Details... Check Logs  :" + e);
		}

	}

	/**
	 * 
	 * @param Sentiment Current, Old Credit Score Value
	 * @return SocialModel with newly Evaluated Exponential Moving Averaged Credit Score Value
	 * @exception NumberFormat Exception , Parse Exception
	 */
	public SocialModel evaluateSocialScore(SocialModel oldcreditScore) {

		try {
			SlidingWindowAverage slidingWindowAverage = new SlidingWindowAverage(oldcreditScore.getCreditScore(),
					creditScore.getCreditScore(), oldcreditScore.getFormattedPreviousDate(),
					oldcreditScore.getFormattedCurrentDate());

			creditScore.setEMAScore(slidingWindowAverage.getExponentialMovingAverage());
			creditScore.setCurrentDate(oldcreditScore.getCurrentDate());
			creditScore.setPreviousDate(oldcreditScore.getPreviousDate());
			creditScore.setCreditScore(oldcreditScore.getCreditScore());

			return creditScore;
		} catch (Exception e) {
			logger.error("Exception occured while calculating Social Score :", e);
			throw new SapException("Failed to calculate Social Score... Check Logs  :" + e);
		}
	}

	/**
	 * Update Business partner details into s/4hana
	 * @exception ODataException, NumberFormatException, ParseException
	 */
	public void putSocialScoreDetails(SocialModel socialModel) {

		try {
			BusinessPartner businessPartner = businessPartnerService.getBusinessPartnerByKey(creditScore.getBpId())
					.execute();

			businessPartner.setCustomField(readCustomFields.getLastUpdated(), creditScore.getFormattedCurrentDate());
			businessPartner.setCustomField(readCustomFields.getTwitterAccount(), creditScore.getTwitterDetails());
			businessPartner.setCustomField(readCustomFields.getTwitterScore(),
					setDecimalPrecison(creditScore.getEMAScore()));

			logger.info("putSocialScoreDetails :" + creditScore.toString());

			businessPartnerService.updateBusinessPartner(businessPartner).execute();

		} catch (Exception e) {
			logger.error("Exception occured while Saving Business Partner Details", e);
			throw new ServiceException("Failed to Save Business Partner Details... Check Logs  : " + e);
		}
	}

}

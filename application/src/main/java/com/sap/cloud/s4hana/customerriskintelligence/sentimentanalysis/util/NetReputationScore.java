package com.sap.cloud.s4hana.customerriskintelligence.sentimentanalysis.util;

import java.math.BigDecimal;
import java.util.function.Supplier;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sap.cloud.s4hana.customerriskintelligence.sentimentanalysis.config.ReadNRSThresholds;
import com.sap.cloud.s4hana.customerriskintelligence.sentimentanalysis.model.Prediction;
import com.sap.cloud.s4hana.customerriskintelligence.sentimentanalysis.model.SentimentMLModel;
import com.sap.cloud.sdk.cloudplatform.logging.CloudLoggerFactory;

@Service
public class NetReputationScore {

	private static final Logger logger = CloudLoggerFactory.getLogger(NetReputationScore.class);

	/*
	 * NET (Net Reputation Score) alternative SIM Net Reputation Score = [% positive
	 * mentions] – [% negative mentions] skip 35%<>65% as Neutral sentiments -100 to
	 * 100 Range
	 */
	@Autowired
	private ReadNRSThresholds nrsThresholds;

	public BigDecimal nRSalgorithm(SentimentMLModel data) {
		try {

			final Supplier<Stream<Prediction.Result>> predictionResultSupplier = () -> data.getPredictions().stream()
					.flatMap(prediction -> prediction.getResults().stream());

			long positiveResults = predictionResultSupplier.get().filter(r -> "positive".equalsIgnoreCase(r.getLabel()))
					.filter(r -> r.getScore() > nrsThresholds.getPositive()).count();

			long negativeResults = predictionResultSupplier.get().filter(r -> "negative".equalsIgnoreCase(r.getLabel()))
					.filter(r -> r.getScore() > nrsThresholds.getNegative()).count();

			final float absoluteScore = ((float) (positiveResults - negativeResults)
					/ predictionResultSupplier.get().count()) * 100;

			return new BigDecimal(absoluteScore).setScale(2, BigDecimal.ROUND_HALF_UP);
		} catch (Exception e) {
			logger.error("Exception occured while calculating  NetReputationScore :", e);
			throw e;
		}
	}
}

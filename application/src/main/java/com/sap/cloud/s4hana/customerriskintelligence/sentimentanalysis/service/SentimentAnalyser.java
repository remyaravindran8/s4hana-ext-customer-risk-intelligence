package com.sap.cloud.s4hana.customerriskintelligence.sentimentanalysis.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sap.cloud.s4hana.customerriskintelligence.businesspartner.exception.ServiceException;
import com.sap.cloud.s4hana.customerriskintelligence.sentimentanalysis.config.LeonardoConfig;
import com.sap.cloud.s4hana.customerriskintelligence.sentimentanalysis.model.SentimentMLModel;
import com.sap.cloud.s4hana.customerriskintelligence.sentimentanalysis.util.NetReputationScore;
import com.sap.cloud.sdk.cloudplatform.logging.CloudLoggerFactory;

import twitter4j.JSONObject;
import twitter4j.TwitterException;

@Service
public class SentimentAnalyser {

	private static final Logger logger = CloudLoggerFactory.getLogger(SentimentAnalyser.class);

	@Autowired
	private LeonardoConfig leonardoConfig;
	@Autowired
	private NetReputationScore netReputationScore;

	private HttpClient httpclient;

	private SentimentAnalyser() {
		this.httpclient = HttpClients.createDefault();
	}

	private String fetchBearerToken() throws Exception {

		// Authenticate and Fetch Bearer Token
		List<NameValuePair> params1 = new ArrayList<NameValuePair>(3);
		params1.add(new BasicNameValuePair("grant_type", leonardoConfig.getGrant_type()));
		params1.add(new BasicNameValuePair("client_id", leonardoConfig.getClient_id()));
		params1.add(new BasicNameValuePair("client_secret", leonardoConfig.getClient_secret()));

		final HttpPost postfetch = new HttpPost(leonardoConfig.getOauthTokenUrl());
		postfetch.addHeader("Content-Type", "application/x-www-form-urlencoded");
		postfetch.addHeader("cache-control", "no-cache");
		postfetch.setEntity(new UrlEncodedFormEntity(params1, "UTF-8"));

		HttpResponse response = httpclient.execute(postfetch);
		HttpEntity entity = response.getEntity();

		// Get Bearer Token

		String token = null;
		if (entity != null) {
			String retSrc = EntityUtils.toString(entity);
			// parsing JSON
			token = new JSONObject(retSrc).getString("access_token");
			token = "Bearer " + token; // append Bearer
			EntityUtils.consume(entity);
		} else {
			throw new ServiceException("Failed to Fetch Bearer Token");
		}

		return token;
	}

	public BigDecimal getAnalysedSentiments(List<String> texts) {

		try {

			if (texts.isEmpty()) {
				throw new TwitterException("Tweet list is Empty");
			}

			List<NameValuePair> params = new ArrayList<NameValuePair>(texts.size());
			for (String message : texts) {
				params.add(new BasicNameValuePair("texts", message));
			}

			final HttpPost post = new HttpPost(leonardoConfig.getRetainedTextClassifierServiceUrl());
			post.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));
			post.addHeader("Authorization", fetchBearerToken());
			post.setHeader(HttpHeaders.ACCEPT, ContentType.APPLICATION_JSON.getMimeType());

			// Execute and get the response.

			HttpResponse responseSentiment = httpclient.execute(post);

			// handle failure mostly 5XX from server

			if (responseSentiment.getStatusLine().getStatusCode() >= 200
					&& responseSentiment.getStatusLine().getStatusCode() < 300) {
				String responseml = EntityUtils.toString(responseSentiment.getEntity());
				logger.info("Sentiment Results :" + responseml);

				return netReputationScore
						.nRSalgorithm(new ObjectMapper().readValue(responseml, SentimentMLModel.class));
			} else {
				logger.info("Response from ML Service is not in<2XX> :",
						responseSentiment.getStatusLine().getReasonPhrase());
				throw new ServiceException("Response from ML Service is not in<2XX> ");
			}
		} catch (Exception e) {

			logger.error("Exception occured while getting AnalysedSentiments from Ml model :", e);
			throw new ServiceException("Failed to get  AnalysedSentiments from Ml model... Check Logs  :" + e);
		}
	}

}

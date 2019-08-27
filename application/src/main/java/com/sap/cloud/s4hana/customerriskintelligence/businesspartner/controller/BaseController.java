package com.sap.cloud.s4hana.customerriskintelligence.businesspartner.controller;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.sap.cloud.s4hana.customerriskintelligence.businesspartner.exception.ServiceException;
import com.sap.cloud.s4hana.customerriskintelligence.businesspartner.model.SocialModel;
import com.sap.cloud.s4hana.customerriskintelligence.businesspartner.service.BPService;
import com.sap.cloud.s4hana.customerriskintelligence.sentimentanalysis.service.SentimentAnalyser;
import com.sap.cloud.s4hana.customerriskintelligence.twitter.service.TwitterImplemetation;

@RestController
@RequestMapping("/base")
public class BaseController {

	@Autowired
	private SentimentAnalyser sentimentAnalyser;

	@Autowired
	private BPService bpservice;

	@Autowired
	private TwitterImplemetation twitterImplemetation;

	/**
	 * Search and fetch tweets for the Social account found.
	 * 
	 * @throws  ServiceException if there is no twitter account or credentials are
	 *                             wrong.
	 * @returns List of tweets in the specified time period.
	 */
	@PostMapping("/tweets")
	public List<String> getTweets(@RequestBody SocialModel socialModel) {

		return twitterImplemetation.searchtweets(socialModel);
	}

	/**
	 * Search and get sentiment score for the tweets
	 * 
	 * @throws  ServiceException if there service is unavailable
	 * @returns Sentiment score
	 */
	@PostMapping("/sentiment")
	public BigDecimal getSentiments(@RequestBody List<String> tweets) {

		return sentimentAnalyser.getAnalysedSentiments(tweets);
	}

	/**
	 * Search and fetch the details of Business partner Social account Details
	 * 
	 * @throws  ServiceException if there is no service location info available in
	 *                           given service.
	 * @returns SocialModel
	 */
	@GetMapping("/socialaccounts")
	public SocialModel getSocialAccounts(@RequestParam String businessPartnerId) {

		return bpservice.getSocialAccountDetails(businessPartnerId);
	}

	/**
	 * Update Business partner Social account Details and calculated score
	 * 
	 * @throws ServiceException, if there is no service location info available
	 */
	@PostMapping("/creditscore")
	public void addCreditScore(@RequestBody SocialModel socialModel) {

		bpservice.putSocialScoreDetails(socialModel);
	}

	/**
	 * Evaluates Social Credit score based on old and current score.
	 * 
	 * @throws SapException
	 */
	@PostMapping("/evaluatescore")
	public SocialModel evaluatescore(@RequestBody SocialModel socialModel) {

		return bpservice.evaluateSocialScore(socialModel);
	}

}

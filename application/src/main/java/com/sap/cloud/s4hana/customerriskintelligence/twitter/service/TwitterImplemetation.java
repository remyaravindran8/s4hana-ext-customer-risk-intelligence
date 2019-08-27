package com.sap.cloud.s4hana.customerriskintelligence.twitter.service;

import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sap.cloud.s4hana.customerriskintelligence.businesspartner.exception.ServiceException;
import com.sap.cloud.s4hana.customerriskintelligence.businesspartner.model.SocialModel;
import com.sap.cloud.s4hana.customerriskintelligence.twitter.config.TwitterConfig;
import com.sap.cloud.sdk.cloudplatform.logging.CloudLoggerFactory;

import twitter4j.Query;
import twitter4j.QueryResult;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;

@Service
public class TwitterImplemetation {

	private static final Logger logger = CloudLoggerFactory.getLogger(TwitterImplemetation.class);
	private static final String LANG = "en";
	private static final int COUNT = 100;

	@Autowired
	private TwitterConfig configtwitter;

	public Twitter getTwitterinstance() {
		
		ConfigurationBuilder twitterConfiguration = new ConfigurationBuilder()
				.setDebugEnabled(true).setOAuthConsumerKey(configtwitter.getConsumerKey())
				.setOAuthConsumerSecret(configtwitter.getConsumerSecret())
				.setOAuthAccessToken(configtwitter.getAccessToken())
				.setOAuthAccessTokenSecret(configtwitter.getAccessTokenSecret());

		TwitterFactory twitterFactory = new TwitterFactory(twitterConfiguration.build());

		return twitterFactory.getInstance();

	}

	public List<String> searchtweets(SocialModel creditScore) {
		try {
			Twitter twitter = getTwitterinstance();

			if (!creditScore.getTwitterDetails().isEmpty()) {

				Query query = new Query(creditScore.getTwitterDetails());
				query.setSince(creditScore.getPreviousDate());
				query.lang(LANG);
				query.setCount(COUNT);

				QueryResult result = twitter.search(query);

				List<Status> statuses = result.getTweets();
				return statuses.stream().map(item -> item.getText()).collect(Collectors.toList());

			} else {
				throw new TwitterException(
						"Twitter Details not found for Business Partner :" + creditScore.getTwitterDetails());
			}
		} catch (Exception e) {
			logger.error("Exception occured while fetching Tweets", e);
			throw new ServiceException("Failed to fetch Tweets... Check Logs  :" + e);
		}
	}
}

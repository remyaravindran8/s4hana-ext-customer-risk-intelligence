package com.sap.cloud.s4hana.customerriskintelligence.businesspartner.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.Setter;

@Component
@PropertySource("classpath:application.properties")
@ConfigurationProperties("customfield")
@Getter
@Setter
public class ReadCustomFields {

	private String lastUpdated;
	private String twitterAccount;
	private String twitterScore;

}

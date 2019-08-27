package com.sap.cloud.s4hana.customerriskintelligence.sentimentanalysis.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.Setter;

@Component
@PropertySource("classpath:application.properties")
@ConfigurationProperties(prefix = "leo")

@Getter
@Setter
public class LeonardoConfig {

	private String grant_type;
	private String client_id;
	private String client_secret;
	private String oauthTokenUrl;
	private String retainedTextClassifierServiceUrl;
}

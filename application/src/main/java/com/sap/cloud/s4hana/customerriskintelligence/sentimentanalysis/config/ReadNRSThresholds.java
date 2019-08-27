package com.sap.cloud.s4hana.customerriskintelligence.sentimentanalysis.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import lombok.Setter;

@Component
@PropertySource("classpath:application.properties")
@ConfigurationProperties("nrsthreshold")

@Setter
public class ReadNRSThresholds {

	private float positive;
	private float negative;
	
	// convert from percentage to float
	public float getPositive() {
		return positive/100;
	}
	
	// convert from percentage to float
	public float getNegative() {
		return negative/100;
	}
	
}

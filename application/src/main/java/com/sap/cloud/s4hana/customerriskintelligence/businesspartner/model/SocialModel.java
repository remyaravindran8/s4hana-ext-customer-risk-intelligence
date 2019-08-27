package com.sap.cloud.s4hana.customerriskintelligence.businesspartner.model;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;

@Data
@Component
@JsonIgnoreProperties(ignoreUnknown = true)
public class SocialModel implements Serializable {

	private static final long serialVersionUID = 145246262L;

	private String bpId;
	private String twitterDetails;
	private Float creditScore;
	private Float EMAScore;
	private String previousDate;
	private String currentDate;

	private Date getDate(String date) throws ParseException {

		if (date.isEmpty() || date == null) {
			return new SimpleDateFormat("yyyy-MM-dd").parse(LocalDate.now().toString());
		} else {
			return new SimpleDateFormat("yyyy-MM-dd").parse(date);
		}
	}
	
	@JsonIgnore
	public Date getFormattedPreviousDate() throws ParseException {

		return getDate(this.previousDate);
	}
	
	@JsonIgnore
	public Date getFormattedCurrentDate() throws ParseException {

		return getDate(this.currentDate);
	}

}

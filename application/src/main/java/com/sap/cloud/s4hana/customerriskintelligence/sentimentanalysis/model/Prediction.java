package com.sap.cloud.s4hana.customerriskintelligence.sentimentanalysis.model;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)

public class Prediction implements Serializable {

	private static final long serialVersionUID = 788479254127321423L;
	private List<Result> results = null;

	@Data
	public static class Result {
		private String label;
		private Float score;
	}
}
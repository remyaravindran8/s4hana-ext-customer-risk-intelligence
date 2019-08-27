package com.sap.cloud.s4hana.customerriskintelligence.sentimentanalysis.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class SentimentMLModel implements Serializable {

	private static final long serialVersionUID = 7801501667601932738L;
	private String id;
	private List<Prediction> predictions = new ArrayList<Prediction>();
	private String processedTime;
	private String status;
}

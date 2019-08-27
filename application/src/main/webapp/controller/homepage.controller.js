jQuery.sap.require("com.sap.riskIntelligence.util.rester");
sap.ui.define([ "sap/ui/core/mvc/Controller","sap/m/MessageBox",
	"sap/m/MessageToast","sap/ui/model/ValidateException" ], function(Controller,MessageBox, MessageToast, ValidateException) {
	"use strict";

	return Controller.extend("com.sap.riskIntelligence.controller.homepage", {
		onInit : function() {
			
			this.model = new sap.ui.model.json.JSONModel();
			this.getView().setModel(this.model,"tweetsModel");
			this.carousel = this.getView().byId("carouseltable");
			this.carousel.removePage(this.getView().byId("tweetsdata1"));
			this.carousel.removePage(this.getView().byId("tweetsdata2"));
			this.carousel.removePage(this.getView().byId("tweetsdata3"));
			this.carousel.removePage(this.getView().byId("tweetsdata4"));
			this.carousel.removePage(this.getView().byId("tweetsdata5"));
			this.carousel.removePage(this.getView().byId("tweetsdata6"));
			this.carousel.removePage(this.getView().byId("tweetsdata7"));
			this.carousel.removePage(this.getView().byId("tweetsdata8"));
			this.carousel.removePage(this.getView().byId("tweetsdata9"));
			sap.ui.getCore().getMessageManager().registerObject(this.oView.byId("bpInput"), true);
			
		},
		
		_readSocialAccount : function(bpValue) {
			jQuery.proxy(sap.ui.demo.wt.util.rester.read({ 
				 "url" : "/base/socialaccounts?businessPartnerId=" + bpValue, 
				 "callbackSuccess" : function(data) { 
					 if(data != "empty"){
						 var processedData = this.processData(data);
						 this._readTweets(processedData); 
						 
					 }else{
						 MessageBox.alert("No business partner found with entered ID.");
					 }
				 }.bind(this), 
				 "callbackError" :function(error) {
					 MessageToast.show("No business partner found.");
				 }.bind(this) }), this);
			
			
		},
		
		processData: function(data){
			this.obj = data;
			this.model.setProperty("/previoussentiscore", this.obj["creditScore"]); 
			this.model.setProperty("/previousdate", this.obj["previousDate"]);
			this.getView().setModel(this.model,"tweetsModel");
			
			return JSON.stringify(this.obj);
			
		},
		
		_readTweets : function(_data) {
			jQuery.proxy(sap.ui.demo.wt.util.rester.fetchTweets({
				"data": _data,
				"url" : "/base/tweets",
				"callbackSuccess" : function(data) {
					if(data.length>0){
						this.displayTweets(data);
						this._fetchSentimentScore(data);
					}else{
						MessageToast.show("Tweets data not found");
					}
				}.bind(this),
				"callbackError" : function(error) {
					MessageToast.show("Social account is not valid");
				}.bind(this)
			}), this);
		},
		
		displayTweets: function(data){
			this.carousel = this.getView().byId("carouseltable");
			var j;
			var count = 0;
			if(data.length>10){
				//when more than 10 tweets are fetched
			for(j=0; j<100, j<data.length ; j+=10){
				var i;
				var jsonObj = [];
				for(i=j;i<j+10;i++){
					 var item = {};
					 item["index"]=i+1;
				     item ["tdata"] = data[i];
				     jsonObj.push(item);
				};
				
				this.model.setProperty("/tweetsdata"+count, jsonObj); 
				count++;
				this.carousel.insertPage(this.getView().byId("tweetsdata"+count),count);
				
			};
			}else{
				//when less than 10 tweets are available
				var k;
				var jsonObj = [];
				for(k=0; k<data.length;k++){
					var item = {};
					 item["index"]=k+1;
				     item ["tdata"] = data[k];
				     jsonObj.push(item);
				};
				this.model.setProperty("/tweetsdata1", jsonObj); 
				}
			
		},
		
		_fetchSentimentScore:function(data){
			var that = this;
			jQuery.proxy(sap.ui.demo.wt.util.rester.fetchTweets({
				"data":JSON.stringify(data),
				"url" : "/base/sentiment",
				"callbackSuccess" : function(data) {
					this.obj.creditScore = data;
					this.evaluateScoreEMA();
					this.getView().byId("update_button_id").setEnabled(true);
				}.bind(this),
				"callbackError" : function(error) {
					MessageToast.show("Sentiment score not found");
				}.bind(this)
			}), this);
		},
		
		evaluateScoreEMA:function(){
			var paramValue = JSON.stringify(this.obj);
			jQuery.proxy(sap.ui.demo.wt.util.rester.update({ 
			"data": paramValue,
			"url": "/base/evaluatescore",
			"callbackSuccess" : function(data) {
				this.processEMAScore(data);
				this.getView().byId("update_button_id").setEnabled(true);
			}.bind(this),
			"callbackError" : function(error) {
				MessageToast.show("Sentiment score not found");
			}.bind(this)
		}), this);
		},
		
		processEMAScore: function(data){
			this.model.setProperty("/EMAScore",  data["emascore"]); 
			this.getView().setModel(this.model,"tweetsModel");
			this.obj.EMAScore = data["emascore"];
		},
		
		_updatebp: function(data){
			
			var _postData = JSON.stringify(this.obj);
			jQuery.proxy(sap.ui.demo.wt.util.rester.update({
				"data": _postData,
				"url" : "/base/creditscore",
				"callbackSuccess" : function(data) {
					MessageToast.show("Sentiment score is successfully updated for BP");
				}.bind(this),
				"callbackError" : function(error) {
					MessageToast.show("Failed to update sentiment score to BP");
				}.bind(this)
			}), this);
		},
		
		onNext : function () {
			// collect input data
			var that = this;
			var oView = this.getView();
			var aInputs = [
				oView.byId("bpInput"),
			];
			var bValidationError = false;

			jQuery.each(aInputs, function (i, oInput) {
				bValidationError = that.validateValue(oInput.getValue()) || bValidationError;
				if (!bValidationError) {
					// validation success
					that.model.setProperty("/bpvalue", oInput.getValue()); 
					// that.getView().setModel(that.model, "tweetsModel");
					that._readSocialAccount(oInput.getValue());
				} else {
					MessageBox.alert("Please enter a valid business partner ID");
				}
			});
		},
		
		validateValue: function (oValue) {
			// The following Regex is NOT a completely correct one and only used
			// for demonstration purposes.
			
			var bValidationError = false;
			var rexMail = /^[0-9]+$/;
			if (!oValue.match(rexMail)) {
				bValidationError = true;
			}
			return bValidationError;
		}

	});

});

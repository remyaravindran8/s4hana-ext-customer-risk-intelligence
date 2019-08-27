/*global QUnit*/

sap.ui.define([
	"riskIntelligence/risk_intelligence/controller/homepage.controller"
], function (oController) {
	"use strict";

	QUnit.module("homepage Controller");

	QUnit.test("I should test the homepage controller", function (assert) {
		var oAppController = new oController();
		oAppController.onInit();
		assert.ok(oAppController);
	});

});
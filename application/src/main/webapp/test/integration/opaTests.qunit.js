/* global QUnit */
QUnit.config.autostart = false;

sap.ui.getCore().attachInit(function () {
	"use strict";

	sap.ui.require([
		"riskIntelligence/risk_intelligence/test/integration/AllJourneys"
	], function () {
		QUnit.start();
	});
});
/* global QUnit */
QUnit.config.autostart = false;

sap.ui.getCore().attachInit(function () {
	"use strict";

	sap.ui.require([
		"riskIntelligence/risk_intelligence/test/unit/AllTests"
	], function () {
		QUnit.start();
	});
});
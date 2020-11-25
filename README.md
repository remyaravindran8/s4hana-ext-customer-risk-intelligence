# SAP S/4HANA Cloud Extensions - Customer Risk Intelligence
This repository contains sample code for the [Customer Risk Intelligence tutorial](http://tiny.cc/s4-cust-risk-intelligence).

_This code is only part of the tutorial, so please follow the tutorial before attempting to use this code._

# Description
The [Customer Risk Intelligence tutorial](http://tiny.cc/s4-cust-risk-intelligence) showcases an extension to an SAP S/4HANA Cloud system.In this scenario, the SAP Leonardo Machine Learning functional service is retrained with a set of social media data from Twitter to perform sentiment analysis on social media. For a specific Business Partner, the scenario uses in-app extension to store social account details - in this example, from Twitter. These details are then used to perform social media sentiment analysis using SAP Leonardo Machine Learning Foundation services as side-by-side extension. The results obtained are stored in SAP S/4HANA Cloud as social score for that business partner.

As a custom use case, scope item ["Sell from Stock - BD9"](https://rapid.sap.com/bp/#/browse/scopeitems/BD9) implements an in-app custom business logic to perform a precheck against the business partner's social score. This precheck serves as a custom credit check before a sales order is created. If the score is too low, a warning message is displayed.

## SAP Extensibility Explorer
This tutorial is one of multiple tutorials that make up the SAP Extensibility Explorer for SAP S/4HANA Cloud. SAP Extensibility Explorer is a central place where anyone involved in the extensibility process can gain insight into various types of extensibility options. At the heart of [SAP Extensibility Explorer](https://sap.com/extends4), there is a rich repository of sample scenarios which show, in a hands-on way, how to realize an extensibility requirement leveraging different extensibility patterns.

# Prerequisites
This sample usages ["ml text classifier"](https://github.com/SAP-samples/leonardo-ml-training) based on
SAP Leonardo Machine Learning Functional services as side-by-side extension, which requires training on set of data before it can be used in here.

# Requirements
- An account in SAP Cloud Platform with a subaccount in the Cloud Foundry environment, SAP Leonardo Machine Learning Foundation Service,
Destination and Authorization services enabled.
- [Java SE 8 Development Kit (JDK)](https://www.oracle.com/technetwork/java/javase/downloads/index.html) and [Apache Maven](http://maven.apache.org/download.cgi) to build the application.
- [Cloud Foundry Command Line Interface (CF CLI)](https://docs.cloudfoundry.org/cf-cli/install-go-cli.html) tool in case you want to deploy the application to Cloud Foundry.
- An SAP S/4HANA Cloud tenant. This is a commercial paid product.

---------------------
# Download and Installation
This repository is a part of the sample [Customer Risk Intelligence](https://help.sap.com/viewer/aa1fb90ce2884ab890425aeeb6004eed/latest/en-US). In step ['Build and Deploy the application'](https://help.sap.com/viewer/aa1fb90ce2884ab890425aeeb6004eed/latest/en-US/2ff3ec61924642769016932e250836e7.html) in the tutorial the instructions for use can be found.

[Please download the zip file by clicking here](https://github.com/SAP-samples/s4hana-ext-customer-risk-intelligence/archive/master.zip) so that the code can be used in the tutorial.
# Known Issues
This sample scenario is based on Machine Learning and Hence, the accuracy of the resultant depends upon the Data Set Used. 

# How to obtain support
If you have any issues with this sample, please open a report using [GitHub issues](https://github.com/SAP-samples/s4hana-ext-customer-risk-intelligence/issues).

# License
Copyright © 2020 SAP SE or an SAP affiliate company. All rights reserved. This file is licensed under the Apache Software License, version 2.0 except as noted otherwise in the [LICENSE file](LICENSES/Apache-2.0.txt).

# Super Health API

## Description
This is the back-end set up for the Super Health, Inc. website where the user can view, add, update
and patients to the database as well as associated encounters for each patient. The endpoints for 
these actions exist to be connected to the super-health-ui to view and manipulate the date from 
the front-end. 

Each patient has a number of fields, each with validation when a user is updating or saving new 
patient information. If valid fields are not entered, then the server will throw an appropriate 
error and not save, update, or delete the patient should it not meet the requirements. The error 
messages that will show up will inform the user of what is wrong with the data they entered. 

Like the patient, the encounters also have validation for adding and updating the data, and will
throw appropriate errors with messages to inform the user of the issue. 

## Pre-requisites
You can operate this back end platform from Intellij, and must install necessary dependencies before
attempting to run the server. If you are only viewing the backend, you can view the endpoints on the
postman link below (postman must be installed), or run the front end using npm to view and 
manipulate the date from the UI. 
#### JDK

You must have a JDK installed on your machine.

#### Postgres

This server requires that you have Postgres installed and running on the default Postgres port of
5432. It requires that you have a database created on the server with the name of `postgres`

- Your username should be `postgres`
- Your password should be `root`

### Start the Server

Right-click AppRunner, and select "Run 'AppRunner.main()'"

## PostMan Collection Link
[![Run in Postman](https://run.pstmn.io/button.svg)](https://god.gw.postman.com/run-collection/26507437-76a0eb98-1c89-4217-a71c-3575d053f6f5?action=collection%2Ffork&source=rip_markdown&collection-url=entityId%3D26507437-76a0eb98-1c89-4217-a71c-3575d053f6f5%26entityType%3Dcollection%26workspaceId%3D79fc21a9-bce6-4924-a55b-251d99c35738)

### Connections

By default, this service starts up on port 8085 and accepts cross-origin requests from `*`.

## Testing

Right-click the testing file you wish to run. There are four options:
    1. EncounterApiTest
    2. EncounterServiceImplTest
    3. PatientApiTEst
    4. PatientServiceImplTest
select "Run 'NameOfFile'" and the tests will begin.

## Viewing the Front End
Clone the super-health-ui repository to your local machine, and follow its read me instructions
to view the application on your local host. 

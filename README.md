# Actions on Google: Updates API sample using Java and Cloud Functions for Firebase

This sample shows an app that gives tips about developing apps for the Google Assistant using Actions on Google.

## Setup Instructions

### Webhook
The sample includes entry points for both AWS Lambda and Google App Engine.

#### Build for AWS
    1. Delete ActionsServlet
    1. Remove the following line from build.gradle:
       1. `apply from: 'build-gcp.gradle'`
    1. Build the AWS Lambda compatible zip file using the buildAWSZip gradle task: `gradle buildAWSZip`
    1. Deploy the zip file found at `build/distributions/notifications-java.zip` as an AWS Lambda function by following instructions at https://aws.amazon.com/lambda/

#### Build for Google Cloud Platform
    1. Delete ActionsAWSHandler.java
    1. Remove the following line from build.gradle:
       1. `apply from: 'build-aws.gradle'`
    1. Download the [SDK for App Engine](https://cloud.google.com/appengine/docs/flexible/java/download)
    1. Follow the steps for [Setting up a GCP project](https://cloud.google.com/appengine/docs/flexible/java/using-gradle#setting_up_and_validating_your_project_name_short)
    1. Deploy to [App Engine using Gradle](https://cloud.google.com/appengine/docs/flexible/java/using-gradle) by running the following command: `gradle appengineDeploy`

### Setup

#### Create a project
1. Use the [Actions on Google Console](https://console.actions.google.com) to add a new project with a name of your choosing and click *Create Project*.
1. Click *Skip*, located on the top right.
1. On the left navigation menu under *BUILD*, click on *Actions*. Click on *Add Your First Action* and choose your app's language(s).
1. Select *Custom intent*, click *BUILD*. This will open a Dialogflow console. Click *CREATE*.
1. Click on the gear icon to see the project settings.
1. Select *Export and Import*.
1. Select *Restore from zip*. Follow the directions to restore from the `agent.zip` file in this repo.

#### Enable the Actions API
1. Visit the [Google Cloud console](https://console.cloud.google.com/) for the project used in the [Actions console](https://console.actions.google.com).
1. Navigate to the [API Library](https://console.cloud.google.com/apis/library).
1. Search for and enable the Google Actions API.
1. Navigate to the Credentials page in the API manager.
1. Click Create credentials > Service Account Key.
1. Click the Select box under Service Account and click New Service Account.
1. Give the Service Account the name (i.e. "service-account") and the role of Project Owner.
1. Select the JSON key type.
1. Click Create.
1. Place the newly downloaded file in the 'src/main/resources/' directory calling the file `service-account.json`.

#### Setup Cloud Firestore for Firebase
1. Go to the [Firebase console](https://console.firebase.google.com) and select the project that you have created on the Actions on Google console.
1. Click the gear icon, then select *Project settings* > *SERVICE ACCOUNTS*.
1. Generate a new private key and save it in the `src/main/resources/` directory calling the file `firebase-service-account.json`.
1. On the left navigation menu under *DEVELOP*, click on *Database*.
1. Under *Cloud Firestore Beta*, click *Create database*.
1. Select *Start in test mode*, click *Enable*.

#### Deploy
1. Deploy the fulfillment webhook as previously described in the *Webhook* section.
1. Go back to the Dialogflow console and select *Fulfillment* from the left navigation menu. Enable *Webhook*, set the value of *URL* to the webhook from the previous section, then click *Save*.

#### Configure updates and push notifications
1. Go to the [Actions on Google console](https://console.actions.google.com).
1. Follow the *Console Setup* instructions in the [Daily Updates](https://developers.google.com/actions/assistant/updates/daily) and the [Push Notifications](https://developers.google.com/actions/assistant/updates/notifications) documentation to enable daily updates and push notifications.

### Test on the Actions on Google simulator
1. Type `Talk to my test app` in the simulator, or say `OK Google, talk to my test app` to any Actions on Google enabled device signed into your developer account.
1. To test daily updates, choose a category. After the tip, the app will show a suggestion chip to subscribe for daily updates. Once a user is subscribed, they will receive update notifications daily for the time they specified.
1. To test push notifications, choose to hear the most recent tip. After the tip, the app will show
a suggestion chip to subscribe for push notifications. To send a push notification to all subscribed users, type "send notification" at any point during the conversation.

For more detailed information on deployment, see the [documentation](https://developers.google.com/actions/dialogflow/deploy-fulfillment).

## References and How to report bugs
* Actions on Google documentation: [https://developers.google.com/actions/](https://developers.google.com/actions/).
* If you find any issues, please open a bug here on GitHub.
* Questions are answered on [StackOverflow](https://stackoverflow.com/questions/tagged/actions-on-google).

## How to make contributions?
Please read and follow the steps in the [CONTRIBUTING.md](CONTRIBUTING.md).

## License
See [LICENSE](LICENSE).

## Terms
Your use of this sample is subject to, and by using or downloading the sample files you agree to comply with, the [Google APIs Terms of Service](https://developers.google.com/terms/).

## Google+
Actions on Google Developers Community on Google+ [https://g.co/actionsdev](https://g.co/actionsdev).

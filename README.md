# Actions on Google: Daily Updates & Push Notifications Sample

This sample demonstrates Actions on Google user engagement features for use on Google Assistant including push notifications and daily updates -- using the [Java client library](https://github.com/actions-on-google/actions-on-google-java) and deployed on [App Engine](https://cloud.google.com/appengine/docs/standard/java/quickstart).

### Setup Instructions
### Prerequisites
1. Download & install the [Google Cloud SDK](https://cloud.google.com/sdk/docs/)
1. [Gradle with App Engine Plugin](https://cloud.google.com/appengine/docs/flexible/java/using-gradle)
   + Run `gcloud auth application-default login` with your Google account
   + Install and update the App Engine component,`gcloud components install app-engine-java`
   + Update other components, `gcloud components update`

### Configuration
#### Actions Console
1. From the [Actions on Google Console](https://console.actions.google.com/), New project (this will become your *Project ID*) > **Create project** > under **More options** > **Conversational**.
1. From the top menu under **Develop** > **Actions** > **Add your first action** > **BUILD** (this will bring you to the Dialogflow console) > Select language and time zone > **CREATE**.
1. In Dialogflow, go to **Settings** ⚙ > **Export and Import** > **Restore from zip**.
   + Follow the directions to restore from the `agent.zip` file in this repo.
1. In the `src/main/resources/config.properties` file of the project, update the value of the `project_id` field with the project ID of your newly created project.

#### Cloud Platform Console
1. From the Dialogflow console under **Settings** ⚙ > [Google Cloud](https://console.cloud.google.com/) link
1. In the Cloud console, go to **Menu ☰** > **APIs & Services** > **Library** > select **Actions API** > **Enable**
4. Under **Menu ☰** > **APIs & Services** > **Credentials** > **Create Credentials** > **Service Account Key**.
5. From the dropdown, select **New Service Account**
    + name:  `service-account`
    + role:  **Project/Owner**
    + key type: **JSON** > **Create**
    + Your private JSON file will be downloaded to your local machine
1. Rename private key file to `service-account.json` and store in the `src/main/resources/` directory.

#### Firestore Database Configuration
1. From the [Firebase console](https://console.firebase.google.com), find and select your Actions on Google Project ID
1. In the left navigation menu under **Develop** section > **Database** > **Create database** button > Select **Start in test mode** > **Enable**

#### Configure Daily Updates and Notifications
1. From the [Actions on Google console](https://console.actions.google.com) > under the top menu **Develop** > **Actions** (left nav):
    + Select the `tell_tip` intent > under **User engagement** > **Enable** `Would you like to offer daily updates to users?` > add a title `Advice Alert` > **Save**
    + Select the `tell_most_recent_tip` intent > under **User engagement** > **Enable** `Would you like to send push notifications? If yes, user permission will be needed` > add a title `Latest Info Alert` > **Save**

#### App Engine Deployment & Webhook Configuration
When a new project is created using the Actions Console, it also creates a Google Cloud project in the background.
1. Configure the gcloud CLI and set your Google Cloud project to the name of your Actions on Google Project ID, which you can find from the [Actions on Google console](https://console.actions.google.com/) under Settings ⚙
   + `gcloud init`
1. Deploy to [App Engine using Gradle](https://cloud.google.com/appengine/docs/flexible/java/using-gradle):
   + `gradle appengineDeploy` OR
   +  From within IntelliJ, open the Gradle tray and run the appEngineDeploy task.

#### Dialogflow Console
1. Return to the [Dialogflow Console](https://console.dialogflow.com), from the left navigation menu under **Fulfillment** > **Enable Webhook**, set the value of **URL** to `https://${YOUR_PROJECT_ID}.appspot.com` > **Save**.
1. From the left navigation menu, click **Integrations** > **Integration Settings** under Google Assistant > Enable **Auto-preview changes** >  **Test** to open the Actions on Google simulator then say or type `Talk to my test app`.

### Running this Sample
+ In the [Dialogflow console](https://console.dialogflow.com), from the left navigation menu > **Integrations** > **Integration Settings** under Google Assistant > Enable **Auto-preview changes** >  **Test** to open the Actions on Google simulator.
+ Type `Talk to my test app` in the simulator, or say `OK Google, talk to my test app` to Google Assistant on a mobile device associated with your Action's account.
+ To test daily updates, choose a category. After the tip, the app will show a suggestion chip to subscribe for daily updates. Once a user is subscribed, they will receive update notifications daily for the time they specified.
+ To test push notifications, choose to hear the most recent tip. After the tip, the app will show
   a suggestion chip to subscribe for push notifications. To send a push notification to all subscribed users, type "send notification" at any point during the conversation.

### References & Issues
+ Questions? Go to [StackOverflow](https://stackoverflow.com/questions/tagged/actions-on-google), [Assistant Developer Community on Reddit](https://www.reddit.com/r/GoogleAssistantDev/) or [Support](https://developers.google.com/actions/support/).
+ For bugs, please report an issue on Github.
+ Actions on Google [Documentation](https://developers.google.com/actions/extending-the-assistant)
+ [Webhook Boilerplate Template](https://github.com/actions-on-google/dialogflow-webhook-boilerplate-java) for Actions on Google.
+ More info about [Gradle & the App Engine Plugin](https://cloud.google.com/appengine/docs/flexible/java/using-gradle).
+ More info about deploying [Java apps with App Engine](https://cloud.google.com/appengine/docs/standard/java/quickstart).

### Make Contributions
Please read and follow the steps in the [CONTRIBUTING.md](CONTRIBUTING.md).

### License
See [LICENSE](LICENSE).

### Terms
Your use of this sample is subject to, and by using or downloading the sample files you agree to comply with, the [Google APIs Terms of Service](https://developers.google.com/terms/).

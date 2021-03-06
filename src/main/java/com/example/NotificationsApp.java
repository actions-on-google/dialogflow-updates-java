/*
 * Copyright 2018 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example;

import com.example.service.notification.NotificationService;
import com.example.service.tip.TipService;
import com.example.service.tip.data.Tip;
import com.example.service.tip.data.User;
import com.google.actions.api.ActionRequest;
import com.google.actions.api.ActionResponse;
import com.google.actions.api.DialogflowApp;
import com.google.actions.api.Capability;
import com.google.actions.api.ConstantsKt;
import com.google.actions.api.ForIntent;
import com.google.actions.api.response.ResponseBuilder;
import com.google.actions.api.response.helperintent.RegisterUpdate;
import com.google.actions.api.response.helperintent.UpdatePermission;
import com.google.api.services.actions_fulfillment.v2.model.Argument;
import com.google.api.services.actions_fulfillment.v2.model.BasicCard;
import com.google.api.services.actions_fulfillment.v2.model.Button;
import com.google.api.services.actions_fulfillment.v2.model.OpenUrlAction;
import com.google.common.collect.Lists;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutionException;

import static com.google.common.base.Preconditions.checkNotNull;

public class NotificationsApp extends DialogflowApp {

  private static final String TIPS_FILE_NAME = "tips.json";
  private static final String BUNDLE_NAME = "resources";
  private final TipService tipService;
  private final NotificationService notificationService;
  private final ResourceBundle prompts;

  public NotificationsApp() throws IOException, ExecutionException, InterruptedException {
    this(new TipService(), new NotificationService(), ResourceBundle.getBundle(BUNDLE_NAME));
  }

  protected NotificationsApp(
      TipService tipService, NotificationService notificationService, ResourceBundle resourceBundle)
      throws IOException, ExecutionException, InterruptedException {
    super();
    this.tipService = checkNotNull(tipService, "tipService cannot be null.");
    this.notificationService =
        checkNotNull(notificationService, "notificationService cannot be null.");
    this.prompts = checkNotNull(resourceBundle, "resourceBundle cannot be null.");
    tipService.loadTipsFromFile(TIPS_FILE_NAME);
  }

  @ForIntent("Default Welcome Intent")
  public ActionResponse welcome(ActionRequest request)
      throws ExecutionException, InterruptedException {
    ResponseBuilder responseBuilder = getResponseBuilder(request);

    if (!request.hasCapability(Capability.SCREEN_OUTPUT.getValue())) {
      // User engagement features aren't currently supported on speaker-only devices
      // See docs: https://developers.google.com/actions/assistant/updates/overview
      responseBuilder.add(prompts.getString("welcomeSpeakerOnly"));
      responseBuilder.endConversation();
    } else if (!request.getUser().getUserVerificationStatus().equals("VERIFIED")) {
      // User engagement features aren't currently for non-verified users
      // See docs: https://developers.google.com/actions/assistant/guest-users
      responseBuilder.add(prompts.getString("welcomeVerifiedOnly"));
      responseBuilder.endConversation();
    } else {
      responseBuilder.add(prompts.getString("welcome"));

      // Get a list of all data categories from the database
      List<String> uniqueCategories = tipService.getCategories();
      uniqueCategories.add("most recent");
      uniqueCategories = Lists.reverse(uniqueCategories);
      responseBuilder.addSuggestions(uniqueCategories.toArray(new String[0]));
    }
    return responseBuilder.build();
  }

  @ForIntent("tell_tip")
  public ActionResponse tellTip(ActionRequest request)
      throws ExecutionException, InterruptedException {
    // Retrieve a list of tips for the user selected category
    String category = (String) request.getParameter("category");
    Tip tip = tipService.getRandomTip(category);
    // Send data to the user
    ResponseBuilder responseBuilder = getResponseBuilder(request);
    responseBuilder.add(tip.getTip());
    responseBuilder
        .add(new BasicCard()
            .setFormattedText(tip.getTip())
            .setButtons(Arrays.asList(new Button()
                            .setTitle(prompts.getString("buttonTitle"))
                            .setOpenUrlAction(new OpenUrlAction().setUrl(tip.getUrl())))))
        .addSuggestions(new String[] {prompts.getString("dailyUpdatesSuggestion")});

    return responseBuilder.build();
  }

  @ForIntent("tell_most_recent_tip")
  public ActionResponse tellMostRecentTip(ActionRequest request)
      throws ExecutionException, InterruptedException {
    // Retrieve the most recently added data from the database
    Tip tip = tipService.getMostRecentTip();
    // Send data to the user
    ResponseBuilder responseBuilder = getResponseBuilder(request);
    responseBuilder.add(tip.getTip());
    // Display data in a card for devices with screen
    responseBuilder
        .add(new BasicCard()
              .setFormattedText(tip.getTip())
              .setButtons(Arrays.asList(new Button()
                              .setTitle(prompts.getString("buttonTitle"))
                              .setOpenUrlAction(new OpenUrlAction().setUrl(tip.getUrl())))))
          .addSuggestions(new String[] {prompts.getString("notificationsSuggestion")});
    return responseBuilder.build();
  }

  @ForIntent("setup_notification")
  public ActionResponse setupNotification(ActionRequest request) {
    // Ask for the user's permission to send push notifications
    ResponseBuilder responseBuilder = getResponseBuilder(request);
    responseBuilder.add(" ").add(new UpdatePermission().setIntent("tell_most_recent_tip"));

    return responseBuilder.build();
  }

  @ForIntent("complete_notification_setup")
  public ActionResponse completeNotificationSetup(ActionRequest request) {
    // Verify the user has subscribed for push notifications
    ResponseBuilder responseBuilder = getResponseBuilder(request);
    if (request.isPermissionGranted()) {
      Argument userId = request.getArgument(ConstantsKt.ARG_UPDATES_USER_ID);
      if (userId != null) {
        // Store the user's ID in the database
        notificationService.subscribeUserToIntent(userId.getTextValue(), "tell_most_recent_tip");
      }
      responseBuilder.add(prompts.getString("notificationSetupSuccess"));
    } else {
      responseBuilder.add(prompts.getString("notificationSetupFail"));
    }
    responseBuilder.endConversation();
    return responseBuilder.build();
  }

  @ForIntent("setup_daily_update")
  public ActionResponse setupDailyUpdates(ActionRequest request) {
    // Ask for the user's permission to send daily updates
    String category = (String) request.getParameter("category");
    ResponseBuilder responseBuilder = getResponseBuilder(request);
    responseBuilder.add(" ");
    responseBuilder.add(
        new RegisterUpdate()
            .setIntent("tell_tip")
            .setFrequency("DAILY")
            .setArguments(
                Arrays.asList(new Argument().setName("category").setTextValue(category))));

    return responseBuilder.build();
  }

  @ForIntent("complete_daily_updates_setup")
  public ActionResponse completeDailyUpdatesSetup(ActionRequest request) {
    // Verify the user has subscribed for daily updates
    ResponseBuilder responseBuilder = getResponseBuilder(request);
    if (request.isUpdateRegistered()) {
      responseBuilder.add(prompts.getString("dailyUpdateSetupSuccess"));
    } else {
      responseBuilder.add(prompts.getString("dailyUpdateSetupFail"));
    }
    responseBuilder.endConversation();

    return responseBuilder.build();
  }

  @ForIntent("send_notification")
  public ActionResponse sendNotification(ActionRequest request)
      throws ExecutionException, InterruptedException {
    // Retrieve a list of users that have subscribed for push notifications
    List<User> users = notificationService.getSubscribedUsersForIntent("tell_most_recent_tip");
    // Send a push notification to every user
    ResponseBuilder responseBuilder = getResponseBuilder(request);
    users.forEach(
        user -> {
          String title = prompts.getString("notificationTitle");
          try {
            notificationService.sendNotification(title, user.getId(), user.getIntent());
            responseBuilder.add(prompts.getString("notificationSendSuccess"));
          } catch (IOException e) {
            e.printStackTrace();
            responseBuilder.add(prompts.getString("notificationSendFail"));
          }
        });

    return responseBuilder.build();
  }

  @ForIntent("restore_tips")
  public ActionResponse restoreTips(ActionRequest request)
      throws ExecutionException, InterruptedException, FileNotFoundException {
    tipService.loadTipsFromFile(TIPS_FILE_NAME);
    ResponseBuilder responseBuilder = getResponseBuilder(request);
    responseBuilder.add(prompts.getString("restoreTips"));

    return responseBuilder.build();
  }
}

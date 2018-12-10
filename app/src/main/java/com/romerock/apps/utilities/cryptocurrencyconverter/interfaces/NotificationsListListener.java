package com.romerock.apps.utilities.cryptocurrencyconverter.interfaces;

import com.romerock.apps.utilities.cryptocurrencyconverter.model.NotificationModel;

import java.util.List;

/**
 * Created by Ebricko on 09/04/2018.
 */
public interface NotificationsListListener {
    void getNotificationList(List<NotificationModel> notificationModelList);
    void removeNotification(String key);
}

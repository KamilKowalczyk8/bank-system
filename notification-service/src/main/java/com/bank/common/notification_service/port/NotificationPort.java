package com.bank.common.notification_service.port;

public interface NotificationPort {
    void sendNotification(String message);
    String getChannelType();
}

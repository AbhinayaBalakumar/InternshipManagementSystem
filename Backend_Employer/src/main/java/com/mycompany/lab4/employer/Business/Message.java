package com.mycompany.lab4.employer.Business;

import io.kubemq.sdk.event.Event;
import io.kubemq.sdk.event.Channel;
import io.kubemq.sdk.tools.Converter;
import javax.net.ssl.SSLException;
import java.io.IOException;

/**
 * Messaging - Publishes application status updates to KubeMQ.
 * The employer publishes to "application_status_channel" whenever
 * an application status is changed (e.g., Submitted → Shortlisted / Rejected).
 */
public class Message {

    public static void sendmessage(String message) throws IOException {
        String channelName  = "application_status_channel";
        String clientID     = "employer-status-publisher";
        String kubeMQAddress = System.getenv("kubeMQAddress");

        Channel channel = new Channel(channelName, clientID, false, kubeMQAddress);
        channel.setStore(true);

        Event event = new Event();
        event.setBody(Converter.ToByteArray(message));
        event.setEventId("event-Store-" + System.currentTimeMillis());

        try {
            channel.SendEvent(event);
        } catch (SSLException e) {
            System.out.printf("SSLException: %s%n", e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.out.printf("Exception sending KubeMQ message: %s%n", e.getMessage());
            e.printStackTrace();
        }
    }
}
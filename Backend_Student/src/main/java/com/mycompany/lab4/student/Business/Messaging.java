package com.mycompany.lab4.student.Business;

import io.grpc.stub.StreamObserver;
import io.kubemq.sdk.basic.ServerAddressNotSuppliedException;
import io.kubemq.sdk.event.EventReceive;
import io.kubemq.sdk.event.Subscriber;
import io.kubemq.sdk.subscription.SubscribeRequest;
import io.kubemq.sdk.subscription.SubscribeType;
import io.kubemq.sdk.subscription.EventsStoreType;
import io.kubemq.sdk.tools.Converter;

import com.mycompany.lab4.student.Persistence.Application_CRUD;

import javax.net.ssl.SSLException;

/**
 * Messaging - Subscribes to KubeMQ and receives application status updates
 * published by the Employer microservice.
 *
 * When the employer changes an application status, this subscriber receives
 * the message and updates the Student database so the student can see
 * their updated application status.
 *
 * Message format: STATUS_UPDATE:$applicationId:$studentId:$newStatus
 */
public class Messaging {

    private static final String CHANNEL_NAME = "application_status_channel";
    private static final String CLIENT_ID    = "student-status-subscriber";

    public void run() {
        String kubeMQAddress = System.getenv("kubeMQAddress");

        SubscribeRequest subscribeRequest = new SubscribeRequest();
        subscribeRequest.setChannel(CHANNEL_NAME);
        subscribeRequest.setClientID(CLIENT_ID);
        subscribeRequest.setSubscribeType(SubscribeType.EventsStore);
        subscribeRequest.setEventsStoreType(EventsStoreType.StartNewOnly);

        StreamObserver<EventReceive> streamObserver = new StreamObserver<EventReceive>() {

            @Override
            public void onNext(EventReceive event) {
                try {
                    String message = Converter.FromByteArray(event.getBody()).toString();
                    System.out.println("Student received KubeMQ message: " + message);

                    // Expected format: STATUS_UPDATE:$applicationId:$studentId:$newStatus
                    if (message.startsWith("STATUS_UPDATE:")) {
                        String[] parts = message.split(":");
                        if (parts.length == 4) {
                            int    applicationId = Integer.parseInt(parts[1]);
                            String newStatus     = parts[3];

                            boolean updated = Application_CRUD.updateStatus(applicationId, newStatus);
                            if (updated) {
                                System.out.println("Student DB updated - applicationId: "
                                        + applicationId + " -> " + newStatus);
                            } else {
                                System.out.println("Failed to update Student DB for applicationId: "
                                        + applicationId);
                            }
                        }
                    }
                } catch (Exception e) {
                    System.out.println("Error processing KubeMQ message: " + e.getMessage());
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(Throwable t) {
                System.out.println("KubeMQ stream error: " + t.getMessage());
            }

            @Override
            public void onCompleted() {
                System.out.println("KubeMQ stream completed.");
            }
        };

        try {
            Subscriber subscriber = new Subscriber(kubeMQAddress);
            subscriber.SubscribeToEvents(subscribeRequest, streamObserver);
            System.out.println("Student microservice subscribed to: " + CHANNEL_NAME);
        } catch (SSLException e) {
            System.out.println("SSLException subscribing to KubeMQ: " + e.getMessage());
        } catch (ServerAddressNotSuppliedException e) {
            System.out.println("KubeMQ address not supplied: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Error subscribing to KubeMQ: " + e.getMessage());
        }
    }
}
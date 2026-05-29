package com.mycompany.lab4.student.Business;

import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;

/**
 * MyAppServletContextListener - Starts the KubeMQ subscriber when the
 * Student microservice web application starts up.
 */
@WebListener
public class MyAppServletContextListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        System.out.println("Student microservice starting - initializing KubeMQ subscriber...");
        // Run subscriber in a background thread so it doesn't block startup
        Thread t = new Thread(() -> {
            Messaging messaging = new Messaging();
            messaging.run();
        });
        t.setDaemon(true);
        t.start();
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        System.out.println("Student microservice stopping.");
    }
}
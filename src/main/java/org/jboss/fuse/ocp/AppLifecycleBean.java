package org.jboss.fuse.ocp;

import org.jboss.logging.Logger;

import javax.enterprise.event.Observes;

import io.quarkus.runtime.StartupEvent;

public class AppLifecycleBean {
    private static final Logger LOGGER = Logger.getLogger("ListenerBean");

    void onStart(@Observes StartupEvent ev) {
        LOGGER.info("Sample app has started!");
    }
}

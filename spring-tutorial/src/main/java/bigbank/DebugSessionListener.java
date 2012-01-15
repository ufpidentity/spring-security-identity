package bigbank;

import java.io.Serializable;

import javax.servlet.http.HttpSessionAttributeListener;
import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import org.apache.log4j.Logger;

public class DebugSessionListener implements HttpSessionAttributeListener, HttpSessionListener, Serializable {
    private static Logger logger = Logger.getLogger(DebugSessionListener.class);
    private static final long serialVersionUID = -6204250344313534030L;

    public void attributeAdded(HttpSessionBindingEvent se) {
        logger.debug("added attribute " + se.getName() + " with value " + se.getValue().toString() + " to session " + se.getSession().getId());
    }
    public void attributeRemoved(HttpSessionBindingEvent se) {
        logger.debug("removed attribute " + se.getName() + " with value " + se.getValue().toString());
    }
    public void attributeReplaced(HttpSessionBindingEvent se) {
        logger.debug("replaced attribute " + se.getName() + " with value " + se.getValue().toString());
    }

    public void sessionCreated(HttpSessionEvent se) {
        logger.debug("session created " + se.getSession().getId());
    }
    public void sessionDestroyed(HttpSessionEvent se) {
        logger.debug("session destroyed " + se.getSession().getId());
    }
}
    
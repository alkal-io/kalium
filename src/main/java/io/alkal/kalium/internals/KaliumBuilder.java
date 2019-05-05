package io.alkal.kalium.internals;

import io.alkal.kalium.Kalium;
import io.alkal.kalium.exceptions.KaliumBuilderException;
import io.alkal.kalium.interfaces.KaliumQueueAdapter;

import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Ziv Salzman
 * Created on 20-Jan-2019
 */

public class KaliumBuilder {

    private static final Logger logger = Logger.getLogger(KaliumBuilder.class.getName());

    private KaliumQueueAdapter queueAdapter = null;

    public KaliumBuilder setQueueAdapter(KaliumQueueAdapter queueAdapter) {
        if (queueAdapter != null) {
            this.queueAdapter = queueAdapter;
            logger.info("KaliumQueueAdapter is set to be of type: " + queueAdapter.getClass().getName());
        }
        return this;
    }


    public Kalium build() throws KaliumBuilderException {
        if (queueAdapter == null) {
            String errorMessage = "No KaliumQueueAdapter is set! No Kalium instance can be built. call setQueueAdapter" +
                    " and pass a proper adapter prior to calling build.";
            logger.severe(errorMessage);
            throw new KaliumBuilderException(errorMessage);
        }
        KaliumImpl kalium = new KaliumImpl();
        queueAdapter.setQueueListener(kalium);
        kalium.setQueueAdapter(queueAdapter);

        return kalium;
    }
}

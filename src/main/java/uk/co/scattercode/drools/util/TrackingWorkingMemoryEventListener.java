package uk.co.scattercode.drools.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.beanutils.BeanUtils;
import org.kie.api.event.rule.DefaultRuleRuntimeEventListener;
import org.kie.api.event.rule.ObjectDeletedEvent;
import org.kie.api.event.rule.ObjectInsertedEvent;
import org.kie.api.event.rule.ObjectUpdatedEvent;
import org.kie.api.event.rule.RuleRuntimeEvent;
import org.kie.api.runtime.rule.FactHandle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Records the insertions, retractions and updates in a session's working
 * memory, so that a client can query everything that happened to the facts
 * for a request. Drools now calls this a rule runtime event listener, and a
 * retraction a deletion.
 * <p>
 * You should probably avoid using this outside testing, as the contained lists
 * of events will grow and grow.
 * </p>
 *
 * @author Stephen Masters
 */
public class TrackingWorkingMemoryEventListener extends DefaultRuleRuntimeEventListener {

    private static Logger log = LoggerFactory.getLogger(TrackingWorkingMemoryEventListener.class);

    private List<RuleRuntimeEvent> allEvents = new ArrayList<RuleRuntimeEvent>();
    private List<ObjectInsertedEvent> insertions = new ArrayList<ObjectInsertedEvent>();
    private List<ObjectDeletedEvent> retractions = new ArrayList<ObjectDeletedEvent>();
    private List<ObjectUpdatedEvent> updates = new ArrayList<ObjectUpdatedEvent>();

    private List<Map<String, Object>> factChanges = new ArrayList<Map<String, Object>>();

    private FactHandle handleFilter;
    private Class<?> classFilter;

    /**
     * Void constructor sets the listener to record all working memory events
     * with no filtering.
     */
    public TrackingWorkingMemoryEventListener() {
        this.handleFilter = null;
    }

    /**
     * Constructor which sets up an event filter. The listener will only record
     * events when the event {@link FactHandle} matches the constructor argument.
     *
     * @param handle
     *            The {@link FactHandle} to filter on.
     */
    public TrackingWorkingMemoryEventListener(FactHandle handle) {
        this.handleFilter = handle;
    }

    /**
     * Constructor which sets up an event filter. The listener will only record
     * events for facts of exactly this class.
     *
     * @param classFilter
     *            The class to filter on.
     */
    public TrackingWorkingMemoryEventListener(Class<?> classFilter) {
        this.handleFilter = null;
        this.classFilter = classFilter;
    }

    @Override
    public void objectInserted(final ObjectInsertedEvent event) {
        if ((handleFilter == null && classFilter == null)
                || event.getFactHandle() == handleFilter
                || event.getObject().getClass().equals(classFilter)) {
            insertions.add(event);
            allEvents.add(event);
            log.debug("Insertion: " + DroolsUtil.objectDetails(event.getObject()));
        }
    }

    @Override
    public void objectDeleted(final ObjectDeletedEvent event) {
        if ((handleFilter == null && classFilter == null)
                || event.getFactHandle() == handleFilter
                || event.getOldObject().getClass().equals(classFilter)) {
            retractions.add(event);
            allEvents.add(event);
            log.debug("Retraction: " + DroolsUtil.objectDetails(event.getOldObject()));
        }
    }

    @Override
    public void objectUpdated(final ObjectUpdatedEvent event) {
        if ((handleFilter == null && classFilter == null)
                || event.getFactHandle() == handleFilter
                || event.getObject().getClass().equals(classFilter)) {
            updates.add(event);
            allEvents.add(event);

            Object fact = event.getObject();
            try {
                factChanges.add(new HashMap<String, Object>(BeanUtils.describe(fact)));
            } catch (Exception e) {
                log.error("Unable to get object details for tracking: " + DroolsUtil.objectDetails(fact), e);
            }
            log.debug("Update: " + DroolsUtil.objectDetails(event.getObject()));
        }
    }

    public List<RuleRuntimeEvent> getAllEvents() {
        return allEvents;
    }

    public List<ObjectInsertedEvent> getInsertions() {
        return insertions;
    }

    public List<ObjectDeletedEvent> getRetractions() {
        return retractions;
    }

    public List<ObjectUpdatedEvent> getUpdates() {
        return updates;
    }

    public List<Map<String, Object>> getFactChanges() {
        return factChanges;
    }

    public String getPrintableSummary() {
        return "TrackingWorkingMemoryEventListener: " + "insertions=["
                + insertions.size() + "], " + "retractions=["
                + retractions.size() + "], " + "updates=["
                + updates.size() + "]";
    }

    public String getPrintableDetail() {
        StringBuilder report = new StringBuilder(getPrintableSummary());

        for (ObjectInsertedEvent event : insertions) {
            report.append("\n" + DroolsUtil.objectDetails(event.getObject()));
        }
        for (ObjectDeletedEvent event : retractions) {
            report.append("\n" + DroolsUtil.objectDetails(event.getOldObject()));
        }
        for (ObjectUpdatedEvent event : updates) {
            report.append("\n" + DroolsUtil.objectDetails(event.getObject()));
        }

        return report.toString();
    }
}

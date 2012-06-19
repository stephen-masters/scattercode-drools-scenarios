package uk.co.scattercode.drools.util;

import java.util.ArrayList;
import java.util.List;

import org.drools.event.rule.ObjectInsertedEvent;
import org.drools.event.rule.ObjectRetractedEvent;
import org.drools.event.rule.ObjectUpdatedEvent;
import org.drools.event.rule.DefaultWorkingMemoryEventListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * When validation rules fire, they should insert a TradeValidationAnnotation
 * into the working memory. This class listens for these events, and adds them
 * to a list so that a client can query all the alerts raised for a request.
 * <p>
 * You should probably avoid using this outside testing, as the contained lists
 * of events will grow and grow.
 * </p>
 * 
 * @author Stephen Masters
 */
public class TrackingWorkingMemoryEventListener extends
		DefaultWorkingMemoryEventListener {

	private static Logger log = LoggerFactory.getLogger(TrackingWorkingMemoryEventListener.class);
	
	private List<ObjectInsertedEvent> insertions = new ArrayList<ObjectInsertedEvent>();
	private List<ObjectRetractedEvent> retractions = new ArrayList<ObjectRetractedEvent>();
	private List<ObjectUpdatedEvent> updates = new ArrayList<ObjectUpdatedEvent>();

	@Override
	public void objectInserted(final ObjectInsertedEvent event) {
		insertions.add(event);
		log.info("Insertion: " + DroolsUtil.objectDetails(event.getObject()));
	}

	@Override
	public void objectRetracted(final ObjectRetractedEvent event) {
		retractions.add(event);
		log.info("Retraction: " + DroolsUtil.objectDetails(event.getOldObject()));
	}

	@Override
	public void objectUpdated(final ObjectUpdatedEvent event) {
		updates.add(event);
		log.info("Update: " + DroolsUtil.objectDetails(event.getObject()));
	}

	public void reset() {
		insertions.clear();
		retractions.clear();
		updates.clear();
	}

	public List<ObjectInsertedEvent> getInsertions() {
		return insertions;
	}
	
	public List<ObjectRetractedEvent> getRetractions() {
		return retractions;
	}

	public List<ObjectUpdatedEvent> getUpdates() {
		return updates;
	}
	
	public String getPrintableSummary() {
		return "TrackingWorkingMemoryEventListener: " +
				"insertions=[" + insertions.size() + "], " +
				"retractions=[" + retractions.size() + "], " +
				"updates=[" + updates.size() + "]";
	}
	
	public String getPrintableDetail() {
		StringBuilder report = new StringBuilder("TrackingWorkingMemoryEventListener: " +
				"insertions=[" + insertions.size() + "], " +
				"retractions=[" + retractions.size() + "], " +
				"updates=[" + updates.size() + "]");
		
		for (ObjectInsertedEvent event : insertions) {
			report.append("\n" + event.getFactHandle().toString());
		}
		for (ObjectRetractedEvent event : retractions) {
			report.append("\n" + event.getFactHandle().toString());
		}
		for (ObjectUpdatedEvent event : updates) {
			report.append("\n" + event.getFactHandle().toString());
		}
		
		return report.toString();
	}
	


}

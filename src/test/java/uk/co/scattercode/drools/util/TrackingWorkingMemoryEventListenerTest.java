package uk.co.scattercode.drools.util;

import static org.junit.Assert.*;

import org.drools.event.rule.ObjectInsertedEvent;
import org.drools.runtime.KnowledgeRuntime;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import uk.co.scattercode.drools.util.TrackingWorkingMemoryEventListener;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

/**
 * A very simple test to confirm that the
 * {@link TrackingWorkingMemoryEventListener} is responding to insertions and
 * retractions.
 * 
 * @author Stephen Masters
 */
public class TrackingWorkingMemoryEventListenerTest {

	@Mock private ObjectInsertedEvent objectInsertedEvent;
	@Mock private KnowledgeRuntime knowledgeRuntime;
	
	@Before
	public void setUp() {
		initMocks(this);
		
		when(this.objectInsertedEvent.getKnowledgeRuntime()).thenReturn(this.knowledgeRuntime);
		when(this.objectInsertedEvent.getObject()).thenReturn(new String("Mock object."));
	}
	
	@Test
	public void test() {
		TrackingWorkingMemoryEventListener listener = new TrackingWorkingMemoryEventListener();
		
		int insertionCountBeforeInsertion = listener.getInsertions().size();
		int retractionCountBeforeInsertion = listener.getRetractions().size();
		int updateCountBeforeInsertion = listener.getUpdates().size();
		
		listener.objectInserted(objectInsertedEvent);
		
		int insertionCountAfterInsertion = listener.getInsertions().size();
		int retractionCountAfterInsertion = listener.getRetractions().size();
		int updateCountAfterInsertion = listener.getUpdates().size();
		
		assertEquals(insertionCountBeforeInsertion + 1, insertionCountAfterInsertion);
		assertEquals(retractionCountBeforeInsertion, retractionCountAfterInsertion);
		assertEquals(updateCountBeforeInsertion, updateCountAfterInsertion);
		
	}

}

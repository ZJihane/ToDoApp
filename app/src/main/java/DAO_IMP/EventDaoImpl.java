package DAO_IMP;
import Model.Event ;
import DAO.EventDAO ;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EventDaoImpl implements EventDAO {
    private Map<String, Event> events;

    public EventDaoImpl() {
        this.events = new HashMap<>();
    }

    @Override
    public void addEvent(Event event) {
        events.put(event.getEventId(), event);
    }

    @Override
    public void updateEvent(Event event) {
        events.put(event.getEventId(), event);
    }

    @Override
    public void deleteEvent(String eventId) {
        events.remove(eventId);
    }

    @Override
    public void getAllEvents(DataCallback<List<Event>> callback) {
        List<Event> eventList = new ArrayList<>(events.values());
        callback.onSuccess(eventList);
    }
}


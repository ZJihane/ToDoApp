package DAO;
import Model.Event ;
import java.util.List;

public interface EventDAO {
    void addEvent(Event event);
    void updateEvent(Event event);
    void deleteEvent(String eventId);
    void getAllEvents(DataCallback<List<Event>> callback);

    interface DataCallback<T> {
        void onSuccess(T data);
        void onError(String errorMessage);
    }
}

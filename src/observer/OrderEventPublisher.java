package observer;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class OrderEventPublisher {
    private final List<OrderEventListener> listeners = new CopyOnWriteArrayList<>();

    public void addListener(OrderEventListener listener) {
        listeners.add(listener);
    }

    public void removeListener(OrderEventListener listener) {
        listeners.remove(listener);
    }

    public void notifyListeners(OrderEvent event) {
        for (OrderEventListener listener : listeners) {
            listener.onOrderStatusChanged(event);
        }
    }
}

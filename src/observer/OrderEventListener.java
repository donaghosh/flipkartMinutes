package observer;

public interface OrderEventListener {
    void onOrderStatusChanged(OrderEvent event);
}

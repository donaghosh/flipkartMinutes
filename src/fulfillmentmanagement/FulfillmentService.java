package fulfillmentmanagement;

import entity.DeliveryPartner;
import entity.Order;
import enums.OrderStatus;
import enums.PartnerStatus;
import exception.InvalidRequestException;
import exception.NotFoundException;
import observer.OrderEvent;
import observer.OrderEventPublisher;
import repository.IDeliveryPartnerRepository;
import repository.IOrderRepository;
import strategy.DefaultAssignmentStrategy;
import strategy.OrderAssignmentStrategy;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.stream.Collectors;

public class FulfillmentService implements IFulfillmentService {
    private final IOrderRepository orderRepository;
    private final IDeliveryPartnerRepository partnerRepository;
    private final Queue<String> orderQueue;
    private final OrderEventPublisher eventPublisher;
    private OrderAssignmentStrategy assignmentStrategy;

    public FulfillmentService(IOrderRepository orderRepository, IDeliveryPartnerRepository partnerRepository, OrderEventPublisher eventPublisher) {
        this.orderRepository = orderRepository;
        this.partnerRepository = partnerRepository;
        this.orderQueue = new LinkedList<>();
        this.eventPublisher = eventPublisher;
        this.assignmentStrategy = new DefaultAssignmentStrategy();
    }

    @Override
    public void setAssignmentStrategy(OrderAssignmentStrategy strategy) {
        this.assignmentStrategy = strategy;
    }

    @Override
    public synchronized void autoAssignOrder(String orderId) {
        if(orderId == null) {
            throw new InvalidRequestException("OrderId must not be null");
        }
        Order order = orderRepository.findById(orderId);
        if (order == null || order.getStatus() == OrderStatus.CANCELLED) {
            throw new InvalidRequestException("This Order cannot be assigned");
        }

        List<DeliveryPartner> availablePartners = partnerRepository.findAll().stream()
                .filter(p -> p.getStatus() == PartnerStatus.AVAILABLE)
                .collect(Collectors.toList());

        DeliveryPartner assignedPartner = assignmentStrategy.assignPartner(availablePartners, order);

        if (assignedPartner != null) {
            OrderStatus previousStatus = order.getStatus();
            order.assignPartner(assignedPartner.getPartnerId());
            assignedPartner.assignOrder(orderId);
            orderRepository.save(order);
            partnerRepository.save(assignedPartner);
            eventPublisher.notifyListeners(new OrderEvent(order, previousStatus, OrderStatus.ASSIGNED));
        } else {
            orderQueue.add(orderId);
        }
    }

    @Override
    public synchronized void handleOrderCancellation(String orderId) {
        if(orderId == null) {
            throw new InvalidRequestException("OrderId must not be null");
        }
        orderQueue.remove(orderId);

        Order order = orderRepository.findById(orderId);
        if (order == null) {
            throw new InvalidRequestException("Order does not exist");
        }

        String partnerId = order.getAssignedPartnerId();
        if (partnerId != null) {
            DeliveryPartner partner = partnerRepository.findById(partnerId);
            if (partner != null && order.getOrderId().equals(partner.getCurrentOrderId())) {
                partner.freeUp();
                partnerRepository.save(partner);
                assignNextFromQueue();
            }
        }
    }

    @Override
    public synchronized void pickupOrder(String partnerId, String orderId) {
        DeliveryPartner partner = partnerRepository.findById(partnerId);
        if (partner == null) {
            throw new NotFoundException("Partner not found: " + partnerId);
        }

        Order order = orderRepository.findById(orderId);
        if (order == null) {
            throw new NotFoundException("Order not found: " + orderId);
        }

        if (order.getStatus() == OrderStatus.CANCELLED) {
            throw new IllegalStateException("Order has been cancelled");
        }

        if (!partnerId.equals(order.getAssignedPartnerId())) {
            throw new IllegalStateException("Order is not assigned to this partner");
        }

        OrderStatus previousStatus = order.getStatus();
        order.setStatus(OrderStatus.PICKED_UP);
        orderRepository.save(order);
        eventPublisher.notifyListeners(new OrderEvent(order, previousStatus, OrderStatus.PICKED_UP));
    }

    @Override
    public synchronized void completeOrder(String partnerId, String orderId) {
        DeliveryPartner partner = partnerRepository.findById(partnerId);
        if (partner == null) {
            throw new NotFoundException("Partner not found: " + partnerId);
        }

        Order order = orderRepository.findById(orderId);
        if (order == null) {
            throw new NotFoundException("Order not found: " + orderId);
        }

        OrderStatus previousStatus = order.getStatus();
        order.setStatus(OrderStatus.COMPLETED);
        partner.completeOrder();

        orderRepository.save(order);
        partnerRepository.save(partner);

        eventPublisher.notifyListeners(new OrderEvent(order, previousStatus, OrderStatus.COMPLETED));

        assignNextFromQueue();
    }

    private void assignNextFromQueue() {
        while (!orderQueue.isEmpty()) {
            String nextOrderId = orderQueue.poll();
            Order nextOrder = orderRepository.findById(nextOrderId);

            if (nextOrder == null || nextOrder.getStatus() == OrderStatus.CANCELLED) {
                continue;
            }

            List<DeliveryPartner> availablePartners = partnerRepository.findAll().stream()
                    .filter(p -> p.getStatus() == PartnerStatus.AVAILABLE)
                    .collect(Collectors.toList());

            DeliveryPartner assignedPartner = assignmentStrategy.assignPartner(availablePartners, nextOrder);

            if (assignedPartner != null) {
                OrderStatus previousStatus = nextOrder.getStatus();
                nextOrder.assignPartner(assignedPartner.getPartnerId());
                assignedPartner.assignOrder(nextOrderId);
                orderRepository.save(nextOrder);
                partnerRepository.save(assignedPartner);
                eventPublisher.notifyListeners(new OrderEvent(nextOrder, previousStatus, OrderStatus.ASSIGNED));
            } else {
                orderQueue.offer(nextOrderId);
                break;
            }
        }
    }

    @Override
    public DeliveryPartner showDeliveryPartnerStatus(String partnerId) {
        DeliveryPartner partner = partnerRepository.findById(partnerId);
        if (partner == null) {
            throw new NotFoundException("Partner not found: " + partnerId);
        }
        return partner;
    }
}

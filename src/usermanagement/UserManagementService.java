package usermanagement;

import entity.Customer;
import entity.DeliveryPartner;
import entity.Order;
import enums.OrderStatus;
import exception.NotFoundException;
import exception.InvalidRequestException;
import factory.EntityFactory;
import repository.ICustomerRepository;
import repository.IDeliveryPartnerRepository;
import repository.IOrderRepository;
import strategy.RankingStrategy;

import java.util.List;

public class UserManagementService implements IUserManagementService {
    private final ICustomerRepository customerRepository;
    private final IDeliveryPartnerRepository partnerRepository;
    private final IOrderRepository orderRepository;
    private RankingStrategy rankingStrategy;

    public UserManagementService(ICustomerRepository customerRepository, IDeliveryPartnerRepository partnerRepository, IOrderRepository orderRepository) {
        this.customerRepository = customerRepository;
        this.partnerRepository = partnerRepository;
        this.orderRepository = orderRepository;
    }

    @Override
    public void setRankingStrategy(RankingStrategy rankingStrategy) {
        this.rankingStrategy = rankingStrategy;
    }

    public void ratePartner(String partnerId, double rating, String customerId, String orderId){
        Order order = orderRepository.findById(orderId);
        if(!order.getCustomerId().equals(customerId)){
            throw new NotFoundException("Customer not found: " + customerId);
        }
        if(!order.getAssignedPartnerId().equals(partnerId)){
            throw new NotFoundException("Delivery partner not found: " + partnerId);
        }
        if(!order.getStatus().equals(OrderStatus.COMPLETED)){
            throw new InvalidRequestException("You cannot rate delivery partner before order is completed: " + orderId);
        }
        DeliveryPartner dp = getPartner(partnerId);
        dp.setRating(rating);
        partnerRepository.save(dp);
    }

    @Override
    public void onboardCustomer(String customerId, String name) {
        Customer customer = EntityFactory.createCustomer(customerId, name);
        customerRepository.save(customer);
    }

    @Override
    public void onboardDeliveryPartner(String partnerId, String name) {
        DeliveryPartner partner = EntityFactory.createDeliveryPartner(partnerId, name);
        partnerRepository.save(partner);
    }

    @Override
    public Customer getCustomer(String customerId) {
        Customer customer = customerRepository.findById(customerId);
        if (customer == null) {
            throw new NotFoundException("Customer not found: " + customerId);
        }
        return customer;
    }

    @Override
    public DeliveryPartner getPartner(String partnerId) {
        DeliveryPartner partner = partnerRepository.findById(partnerId);
        if (partner == null) {
            throw new NotFoundException("Partner not found: " + partnerId);
        }
        return partner;
    }

    @Override
    public List<String> getTopDeliveryPartners() {
        List<DeliveryPartner> deliveryPartners = partnerRepository.findAll();
        return rankingStrategy.rankDeliveryPartners(deliveryPartners);
    }
}

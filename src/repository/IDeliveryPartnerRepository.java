package repository;

import entity.DeliveryPartner;

import java.util.List;

public interface IDeliveryPartnerRepository extends IRepository<DeliveryPartner> {
    List<DeliveryPartner> findAvailable();
}

package sk.streetofcode.productordermanagement.service;

import org.springframework.stereotype.Service;
import sk.streetofcode.productordermanagement.domain.Order;
import sk.streetofcode.productordermanagement.repository.OrderRepo;
import sk.streetofcode.productordermanagement.request.AddProductRequest;

@Service
public class OrderService {
    private final OrderRepo repo;

    public OrderService(OrderRepo repo) {
        this.repo = repo;
    }

    public Order findById(Integer id) {
        return repo.findById(id);
    }

    public Order create (){
        return repo.save();
    }

//    public Order pay (Integer id){
//        repo.payOrder(id);
//        return findById(id);
//    }

    public String pay (Integer id) {
        return repo.payOrder(id);
    }

    public Order addProductToOrder(Integer orderId, AddProductRequest request){
        return repo.addProductToOrder(
                orderId,
                request.getProductId(),
                request.getAmount()
        );
    }

    public void delete (Integer id) {
        repo.delete(id);
    }
}

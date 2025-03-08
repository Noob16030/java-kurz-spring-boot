package ordersManagementSystem.service;

import ordersManagementSystem.domain.Product;
import ordersManagementSystem.repository.ProductRepo;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductService {
    private final ProductRepo repo;

    public ProductService(ProductRepo repo) {
        this.repo = repo;
    }

    public List<Product> findAll(){
        return repo.findAll();
    }
}

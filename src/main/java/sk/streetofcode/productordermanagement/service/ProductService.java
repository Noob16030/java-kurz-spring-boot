package sk.streetofcode.productordermanagement.service;

import sk.streetofcode.productordermanagement.domain.Amount;
import sk.streetofcode.productordermanagement.domain.Product;
import sk.streetofcode.productordermanagement.repository.ProductRepo;
import org.springframework.stereotype.Service;
import sk.streetofcode.productordermanagement.request.CreateProductRequest;
import sk.streetofcode.productordermanagement.request.ProductAmountRequest;

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

    public Product findById(Integer id) {
        return repo.findById(id);
    }

    public Product create(CreateProductRequest request){
        return repo.save(
                request.getName(),
                request.getDescription(),
                request.getAmount(),
                request.getPrice()
        );
    }

    public Product update(Integer id, CreateProductRequest request){
        repo.update(
                id,
                request.getName(),
                request.getDescription()
        );
        return findById(id);
    }

    public void delete(Integer id) {
        repo.delete(id);
    }
}

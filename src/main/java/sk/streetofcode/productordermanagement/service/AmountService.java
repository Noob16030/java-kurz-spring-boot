package sk.streetofcode.productordermanagement.service;

import org.springframework.stereotype.Service;
import sk.streetofcode.productordermanagement.domain.Amount;
import sk.streetofcode.productordermanagement.repository.AmountRepo;
import sk.streetofcode.productordermanagement.request.ProductAmountRequest;

@Service
public class AmountService {
    private final AmountRepo repo;

    public AmountService(AmountRepo repo) {
        this.repo = repo;
    }

    public Amount findAmountById(Integer id) {
        return repo.findAmountById(id);
    }

    public Amount updateAmount(Integer id, ProductAmountRequest request){
        repo.updateAmount(
                id,
                request.getAmount()
        );
        return findAmountById(id);
    }
}

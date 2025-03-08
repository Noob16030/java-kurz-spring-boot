package sk.streetofcode.productordermanagement.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import sk.streetofcode.productordermanagement.domain.Amount;
import sk.streetofcode.productordermanagement.domain.Product;
import sk.streetofcode.productordermanagement.request.CreateProductRequest;
import sk.streetofcode.productordermanagement.request.ProductAmountRequest;
import sk.streetofcode.productordermanagement.service.AmountService;
import sk.streetofcode.productordermanagement.service.ProductService;
import org.springframework.http.ResponseEntity;

import java.util.List;

@RestController
@RequestMapping("/product")
public class ProductController {
    private final ProductService productService;
    private final AmountService amountService;

    public ProductController(ProductService service, AmountService amountService) {
        this.productService = service;
        this.amountService = amountService;
    }

    @GetMapping
    public ResponseEntity<List<Product>> getProducts(){
        return ResponseEntity.ok(productService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Product> getProduct (@PathVariable Integer id){
        return ResponseEntity.ok(productService.findById(id));
    }

    @GetMapping("/{id}/amount")
    public ResponseEntity<Amount> getProductAmount (@PathVariable Integer id){
        return ResponseEntity.ok(amountService.findAmountById(id));
    }

    @PostMapping("/{id}/amount")
    public ResponseEntity<Amount> setProductAmount (@PathVariable Integer id, @RequestBody ProductAmountRequest request){
        return ResponseEntity.ok(amountService.updateAmount(id, request));
    }

    @PostMapping
    public ResponseEntity<Product> createProduct(@RequestBody CreateProductRequest request){
        return ResponseEntity.status(HttpStatus.CREATED).body(productService.create(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Product> updateProduct(@PathVariable Integer id, @RequestBody CreateProductRequest request) {
        return ResponseEntity.ok(productService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        productService.delete(id);
        return ResponseEntity.ok().build();
    }
}

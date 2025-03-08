package sk.streetofcode.productordermanagement.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sk.streetofcode.productordermanagement.domain.Order;
import sk.streetofcode.productordermanagement.request.AddProductRequest;
import sk.streetofcode.productordermanagement.service.OrderService;

@RestController
@RequestMapping("/order")
public class OrderController {
    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<Order> getOrder (@PathVariable Integer id) {
        return ResponseEntity.ok(orderService.findById(id));
    }

    @PostMapping
    public ResponseEntity<Order> createOrder(){
        return ResponseEntity.status(HttpStatus.CREATED).body(orderService.create());
    }

    @PostMapping("/{id}/pay")
    public ResponseEntity<String> payOrder(@PathVariable Integer id){
        return ResponseEntity.ok(orderService.pay(id));
    }

    @PostMapping("/{id}/add")
    public ResponseEntity<Order> addProductToOrder(@PathVariable Integer id, @RequestBody AddProductRequest request){
        return ResponseEntity.ok(orderService.addProductToOrder(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id){
        orderService.delete(id);
        return ResponseEntity.ok().build();
    }
}

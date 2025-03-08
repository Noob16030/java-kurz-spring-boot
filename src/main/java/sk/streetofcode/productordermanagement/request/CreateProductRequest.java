package sk.streetofcode.productordermanagement.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class CreateProductRequest {
    private String name;
    private String description;
    private Integer amount;
    private Double price;

}

package sk.streetofcode.productordermanagement.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class Order {
    private Long id;
    private List<Amount> shoppingList;
    private Boolean paid;
}

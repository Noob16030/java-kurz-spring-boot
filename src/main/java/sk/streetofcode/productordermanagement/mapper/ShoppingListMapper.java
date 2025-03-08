package sk.streetofcode.productordermanagement.mapper;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import sk.streetofcode.productordermanagement.domain.Amount;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class ShoppingListMapper implements RowMapper<Amount> {
    @Override
    public Amount mapRow(ResultSet rs, int rowNum) throws SQLException {
        Amount shoppingList = new Amount();
        shoppingList.setProductId(rs.getInt("productId"));
        shoppingList.setAmount(rs.getInt("amount"));
        return shoppingList;
    }

}

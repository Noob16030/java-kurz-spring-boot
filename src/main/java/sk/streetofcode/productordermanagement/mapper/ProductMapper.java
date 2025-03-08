package sk.streetofcode.productordermanagement.mapper;

import sk.streetofcode.productordermanagement.domain.Product;
import org.springframework.stereotype.Component;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;


@Component
public class ProductMapper implements RowMapper<Product> {

    @Override
    public Product mapRow(ResultSet rs, int rowNum) throws SQLException {
        Product product = new Product();
        product.setId(rs.getLong("id"));
        product.setName(rs.getString("name"));
        product.setDescription(rs.getString("description"));
        product.setAmount(rs.getLong("amount"));
        product.setPrice(rs.getDouble("price"));
        return product;
    }
}

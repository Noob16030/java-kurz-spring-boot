package sk.streetofcode.productordermanagement.mapper;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import sk.streetofcode.productordermanagement.domain.Amount;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class AmountMapper implements RowMapper<Amount> {
    @Override
    public Amount mapRow(ResultSet rs, int rowNum) throws SQLException {
        Amount amount = new Amount();
        amount.setProductId(rs.getInt("id"));
        amount.setAmount(rs.getInt("amount"));
        return amount;
    }
}

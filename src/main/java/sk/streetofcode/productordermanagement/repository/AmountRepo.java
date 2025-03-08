package sk.streetofcode.productordermanagement.repository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import sk.streetofcode.productordermanagement.domain.Amount;
import sk.streetofcode.productordermanagement.exception.InternalErrorException;
import sk.streetofcode.productordermanagement.exception.ProductNotFoundException;
import sk.streetofcode.productordermanagement.mapper.AmountMapper;

@Repository
public class AmountRepo {
    private static final Logger logger = LoggerFactory.getLogger(AmountRepo.class);

    private static final String GET_BY_ID = "SELECT * FROM product WHERE id = ?";
    private static final String UPDATE_AMOUNT = "UPDATE product SET amount = amount + ? WHERE id = ?";

    private final JdbcTemplate jdbcTemplate;
    private final AmountMapper amountMapper;

    public AmountRepo(JdbcTemplate jdbcTemplate, AmountMapper amountMapper) {
        this.jdbcTemplate = jdbcTemplate;
        this.amountMapper = amountMapper;
    }

    public Amount findAmountById(Integer id) {
        logger.debug("Finding product amount with id: {}", id);
        try {
            return jdbcTemplate.queryForObject(
                    GET_BY_ID,
                    amountMapper,
                    id
            );
        } catch (EmptyResultDataAccessException e) {
            logger.error("Product with id {} not found.", id);
            throw new ProductNotFoundException("Product with id " + id + " not found.");
        }
    }

    public void updateAmount (Integer id, Integer amount){
        logger.debug("Updating amount of product with id: {}", id);

        findAmountById(id);

        try {
            jdbcTemplate.update(
                    UPDATE_AMOUNT,
                    amount,
                    id
            );
        } catch (DataAccessException e) {
            logger.error("Error while updating product amount with id: {}", id, e);
            throw new InternalErrorException("Error while updating product amount.");
        }
    }
}

package sk.streetofcode.productordermanagement.repository;

import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import sk.streetofcode.productordermanagement.domain.Amount;
import sk.streetofcode.productordermanagement.domain.Product;
import sk.streetofcode.productordermanagement.exception.InternalErrorException;
import sk.streetofcode.productordermanagement.exception.ProductNotFoundException;
import sk.streetofcode.productordermanagement.mapper.AmountMapper;
import sk.streetofcode.productordermanagement.mapper.ProductMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;

@Repository
public class ProductRepo {
    private static final Logger logger = LoggerFactory.getLogger(ProductRepo.class);

    private static final String GET_ALL = "SELECT * FROM product";
    private static final String GET_BY_ID = "SELECT * FROM product WHERE id = ?";
    private static final String INSERT = "INSERT INTO product (name, description, amount, price) VALUES (?, ?, ?, ?)";
    private static final String UPDATE = "UPDATE product SET name = ?, description = ? WHERE id = ?";
    private static final String DELETE = "DELETE FROM product WHERE id = ?";

    private final JdbcTemplate jdbcTemplate;
    private final ProductMapper productMapper;

    public ProductRepo(JdbcTemplate jdbcTemplate, ProductMapper productMapper) {
        this.jdbcTemplate = jdbcTemplate;
        this.productMapper = productMapper;
    }

    public List<Product> findAll(){
        logger.debug("Finding all products.");
        return jdbcTemplate.query(
                GET_ALL,
                productMapper
        );
    }

    public Product findById(Integer id) {
        logger.debug("Finding product with id: {}", id);
        try {
            return jdbcTemplate.queryForObject(
                    GET_BY_ID,
                    productMapper,
                    id
            );
        } catch (EmptyResultDataAccessException e) {
            logger.error("Product with id {} not found.", id);
            throw new ProductNotFoundException("Product with id " + id + " not found.");
        }
    }


    public Product save (String name, String description, Integer amount, Double price){
        logger.debug("Saving new product: {}", name);
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(con -> {
            PreparedStatement ps = con.prepareStatement(
                    INSERT,
                    Statement.RETURN_GENERATED_KEYS
            );
            ps.setString(1, name);
            ps.setString(2, description);
            ps.setInt(3, amount);
            ps.setDouble(4, price);
            return ps;
        }, keyHolder);

        if (keyHolder.getKeys() == null){
            logger.error("KeyHolder was null while creating a new product.");
            throw new InternalErrorException("Error while creating a new product.");
        }

        Number key = (Number) keyHolder.getKeys().get("ID");
        return findById(key.intValue());
    }

    public void update (Integer id, String name, String description){
        logger.debug("Updating product with id: {}", id);

        findById(id);

        try {
            jdbcTemplate.update(
                    UPDATE,
                    name,
                    description,
                    id
            );
        } catch (DataAccessException e) {
            logger.error("Error while updating product with id: {}", id, e);
            throw new InternalErrorException("Error while updating product.");
        }
    }


    public void delete(Integer id) {
        logger.debug("Deleting Product with id: {}", id);

        findById(id);
        jdbcTemplate.update(DELETE, id);
    }

}

package ordersManagementSystem.repository;

import ordersManagementSystem.domain.Product;
import ordersManagementSystem.mapper.ProductMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class ProductRepo {
    private static final Logger logger = LoggerFactory.getLogger(ProductRepo.class);

    private static final String GET_ALL = "SELECT * FROM product";


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
}

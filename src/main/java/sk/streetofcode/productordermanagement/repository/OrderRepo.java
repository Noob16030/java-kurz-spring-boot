package sk.streetofcode.productordermanagement.repository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import sk.streetofcode.productordermanagement.domain.Amount;
import sk.streetofcode.productordermanagement.domain.Order;
import sk.streetofcode.productordermanagement.domain.Product;
import sk.streetofcode.productordermanagement.exception.BadRequestException;
import sk.streetofcode.productordermanagement.exception.InternalErrorException;
import sk.streetofcode.productordermanagement.exception.ProductNotFoundException;
import sk.streetofcode.productordermanagement.mapper.AmountMapper;
import sk.streetofcode.productordermanagement.mapper.OrderMapper;
import sk.streetofcode.productordermanagement.mapper.ProductMapper;
import sk.streetofcode.productordermanagement.mapper.ShoppingListMapper;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;

@Repository
public class OrderRepo {
    private static final Logger logger = LoggerFactory.getLogger(OrderRepo.class);

    private static final String GET_BY_ID = "SELECT * FROM order WHERE id = ?";
    private static final String GET_SHOPPING_LIST_BY_ORDER_ID = "SELECT * FROM shoppinglistitem WHERE orderId = ?";
    private static final String GET_SHOPPING_LIST_PRODUCT_BY_ORDER_ID_PRODUCT_ID = "SELECT * FROM shoppinglistitem WHERE orderId = ? AND productId = ?";
    private static final String INSERT = "INSERT INTO order(paid) VALUES (false)";
    private static final String PAY = "UPDATE order SET paid = true WHERE id = ?";
    private static final String UPDATE_PRODUCT_IN_LIST = "UPDATE shoppinglistitem SET amount = amount + ? WHERE orderId = ? AND productId = ?";
    private static final String ADD_PRODUCT_TO_ORDER = "INSERT INTO shoppinglistitem (orderId, productId, amount) VALUES (?, ?, ?)";
    private static final String GET_AMOUNT = "SELECT * FROM product WHERE id = ?";
    private static final String UPDATE_PRODUCT_AMOUNT = "UPDATE product SET amount = amount - ? WHERE id = ?";
    private static final String UPDATE_PRODUCT_AMOUNT_PLUS = "UPDATE product SET amount = amount + ? WHERE id = ?";
    private static final String DELETE_ORDER = "DELETE FROM order WHERE id = ?";
    private static final String DELETE_SHOPPING_LIST = "DELETE FROM order WHERE orderId = ?";

    private final JdbcTemplate jdbcTemplate;
    private final OrderMapper orderMapper;
    private final ShoppingListMapper shoppingListMapper;
    private final AmountMapper amountMapper;
    private final ProductMapper productMapper;

    public OrderRepo(JdbcTemplate jdbcTemplate, OrderMapper orderMapper, ShoppingListMapper shoppingListMapper, AmountMapper amountMapper, ProductMapper productMapper) {
        this.jdbcTemplate = jdbcTemplate;
        this.orderMapper = orderMapper;
        this.shoppingListMapper = shoppingListMapper;
        this.amountMapper = amountMapper;
        this.productMapper = productMapper;
    }

    public Integer getAmount(Integer id) {
        try {
            Amount amount = jdbcTemplate.queryForObject(
                    GET_AMOUNT,
                    amountMapper,
                    id
            );
            return amount.getAmount();
        } catch (EmptyResultDataAccessException e) {
            logger.error("Product with id {} not found.", id);
            throw new ProductNotFoundException("Product with id " + id + " not found.");
        }
    }

    public Order findById(Integer id) {
        logger.debug("Finding order with id: {}", id);
        try {
                    Order order = jdbcTemplate.queryForObject(
                    GET_BY_ID,
                    orderMapper,
                    id
            );

            List<Amount> shoppingList = jdbcTemplate.query(
                    GET_SHOPPING_LIST_BY_ORDER_ID,
                    shoppingListMapper,
                    id
            );

            order.setShoppingList(shoppingList);

            return order;

        } catch (EmptyResultDataAccessException e) {
            logger.error("Order with id {} not found.", id);
            throw new ProductNotFoundException("Order with id " + id + " not found.");
        }
    }

    public Order save () {
        logger.debug("Making new order.");
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(con -> {
            PreparedStatement ps = con.prepareStatement(
                    INSERT,
                    Statement.RETURN_GENERATED_KEYS
            );
            return ps;
        }, keyHolder);

        if (keyHolder.getKeys() == null){
            logger.error("KeyHolder was null while creating a new order.");
            throw new InternalErrorException("Error while creating a new order.");
        }

        Number key = (Number) keyHolder.getKeys().get("ID");
        return findById(key.intValue());
    }

    public String payOrder(Integer id){
        logger.debug("Paying foe order number: {}", id);

        Order order = findById(id);

        if (!order.getPaid()) {
            try {
                jdbcTemplate.update(
                        PAY,
                        id
                );

                List<Amount> productAmount = jdbcTemplate.query(
                        GET_SHOPPING_LIST_BY_ORDER_ID,
                        shoppingListMapper,
                        id
                );

                double cena = 0.0;
                if (!productAmount.isEmpty()) {
                    for (Amount amount : productAmount) {

                        Product product = jdbcTemplate.queryForObject(
                                GET_AMOUNT,
                                productMapper,
                                amount.getProductId()
                        );

                        cena = cena + (product.getPrice() * amount.getAmount());
                    }
                    return String.format("%.1f", cena);
                }
            } catch (DataAccessException e) {
                logger.error("Error while paying for order with id: {}", id, e);
                throw new InternalErrorException("Error while paying for order.");
            }
        } else {
            logger.debug("Order with id: {} is already payed.", id);
            throw new BadRequestException("Order is already payed.");
        }
        return null;
    }

    public Order addProductToOrder (Integer id, Integer productId, Integer amount) {
        logger.debug("Adding product: {} to order: {}", productId, id);

        Order order = findById(id);
        Integer amountOfProduct = getAmount(productId);

        if (order.getPaid()) {
            logger.debug("Order with id: {} is already payed.", id);
            throw new BadRequestException("Order is already payed.");
        } else if (amountOfProduct < amount) {
            logger.debug("Product amount with id: {} do not have enough pieces.", productId);
            throw new BadRequestException("You need more products.");
        } else {
            try {

                List<Amount> productAmount = jdbcTemplate.query(
                        GET_SHOPPING_LIST_PRODUCT_BY_ORDER_ID_PRODUCT_ID,
                        shoppingListMapper,
                        id,
                        productId
                );

                if (!productAmount.isEmpty()){
                    jdbcTemplate.update(
                            UPDATE_PRODUCT_IN_LIST,
                            amount,
                            id,
                            productId
                    );

                    logger.debug("Updating amount of product with id: {}", productId);

                    jdbcTemplate.update(
                            UPDATE_PRODUCT_AMOUNT,
                            amount,
                            productId
                    );

                } else {
                    jdbcTemplate.update(
                            ADD_PRODUCT_TO_ORDER,
                            id,
                            productId,
                            amount
                    );

                    logger.debug("Updating amount of product with id: {}", productId);

                    jdbcTemplate.update(
                            UPDATE_PRODUCT_AMOUNT,
                            amount,
                            productId
                    );

                }

            } catch (DataAccessException e) {
                logger.error("Error while adding product with id: {} to order: {}", productId, id, e);
                throw new InternalErrorException("Error while adding product to order.");
            }
        }
        return findById(id);
    }

    public void delete(Integer id) {
        logger.debug("Deleting order with id: {}",id);

        findById(id);
        jdbcTemplate.update(DELETE_ORDER, id);

        List<Amount> productAmount = jdbcTemplate.query(
                GET_SHOPPING_LIST_BY_ORDER_ID,
                shoppingListMapper,
                id
        );

        if (!productAmount.isEmpty()){
            jdbcTemplate.update(
                    DELETE_SHOPPING_LIST,
                    id
                    );
            for (Amount amount : productAmount) {
                logger.debug("Updating amount of product with id: {}", amount.getProductId());

                jdbcTemplate.update(
                        UPDATE_PRODUCT_AMOUNT_PLUS,
                        amount.getAmount(),
                        amount.getProductId()
                );
            }

        }

    }


}

package com.example.springbootmall.service.impl;

import com.example.springbootmall.dao.OrderDao;
import com.example.springbootmall.dao.ProductDao;
import com.example.springbootmall.dao.UserDao;
import com.example.springbootmall.dto.BuyItem;
import com.example.springbootmall.dto.CreateOrderRequest;
import com.example.springbootmall.model.Order;
import com.example.springbootmall.model.OrderItem;
import com.example.springbootmall.model.Product;
import com.example.springbootmall.model.User;
import com.example.springbootmall.service.OrderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Component
public class OrderServiceImpl implements OrderService {

    private final static Logger log = LoggerFactory.getLogger(OrderServiceImpl.class);

    @Autowired
    private OrderDao orderDao;

    @Autowired
    private ProductDao productDao;

    @Autowired
    private UserDao userDao;

    @Override
    public Order getOrderById(Integer orderId) {
        Order order = orderDao.getOrderById(orderId);

        List<OrderItem> orderItemList = orderDao.getOrderItemsByOrderId(orderId);

        order.setOrderItemList(orderItemList);

        return order;
    }

    @Transactional
    @Override
    public Integer createOrder(Integer userId, CreateOrderRequest createOrderRequest) {
        // check if user exists
        User user = userDao.getUserById(userId);

        if (Objects.isNull(user)) {
            log.warn("user id {} doesn't exist.", userId);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }

        int totalAmount = 0;
        List<OrderItem> orderItemList = new ArrayList<>();

        for (BuyItem buyItem : createOrderRequest.getBuyItemList()) {
            int productId = buyItem.getProductId();
            int quantity = buyItem.getQuantity();

            Product product = productDao.getProductById(productId);

            // check if the product exists and if the stock is sufficient
            if (Objects.isNull(product)) {
                log.warn("product id {} doesn't exist.", productId);
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
            } else if (product.getStock() < quantity) {
                log.warn("The product {} is not available in sufficient quantity to be purchased. " +
                        "Remaining stock: {}, want to buy quantity: {}", productId, product.getStock(), quantity);
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
            }

            // deduct product inventory
            productDao.updateStock(productId, product.getStock() - quantity);

            // calculate total amount
            int amount = product.getPrice() * buyItem.getQuantity();
            totalAmount += amount;

            // convert BuyItem to OrderItem
            OrderItem orderItem = new OrderItem();
            orderItem.setProductId(buyItem.getProductId());
            orderItem.setQuantity(buyItem.getQuantity());
            orderItem.setAmount(amount);

            orderItemList.add(orderItem);
        }



        // create order
        Integer orderId = orderDao.createOrder(userId, totalAmount);

        orderDao.createOrderItems(orderId, orderItemList);

        return orderId;
    }
}

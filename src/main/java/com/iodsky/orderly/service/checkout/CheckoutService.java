package com.iodsky.orderly.service.checkout;

import com.iodsky.orderly.enums.OrderStatus;
import com.iodsky.orderly.exceptions.EmptyCartException;
import com.iodsky.orderly.model.Cart;
import com.iodsky.orderly.model.Order;
import com.iodsky.orderly.model.OrderItem;
import com.iodsky.orderly.model.Product;
import com.iodsky.orderly.model.User;
import com.iodsky.orderly.service.cart.CartService;
import com.iodsky.orderly.service.order.OrderService;
import com.iodsky.orderly.service.product.ProductService;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CheckoutService implements ICheckoutService {

    private final OrderService orderService;
    private final CartService cartService;
    private final ProductService productService;

    @Override
    @Transactional
    public Order placeOrder(User user) {
        Cart cart = cartService.getCartByUser(user);

        if (cart.getItems().isEmpty()) {
            throw new EmptyCartException(cart.getId());
        }

        Order order = Order.builder()
                .user(user)
                .dateOrdered(LocalDateTime.now())
                .totalAmount(cart.getTotalAmount())
                .orderStatus(OrderStatus.PROCESSING)
                .build();

        cart.getItems().forEach(item -> {

            Product product = productService.decreaseStock(item.getProduct().getId(), item.getQuantity());

            OrderItem orderItem = OrderItem.builder()
                    .order(order)
                    .price(item.getUnitPrice())
                    .product(product)
                    .quantity(item.getQuantity())
                    .build();
            order.getItems().add(orderItem);
        });

        cart.getItems().clear();
        cartService.saveCart(cart);

        return orderService.saveOrder(order);
    }

}

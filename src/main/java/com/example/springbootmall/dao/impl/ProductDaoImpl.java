package com.example.springbootmall.dao.impl;

import com.example.springbootmall.constant.ProductCategory;
import com.example.springbootmall.dao.ProductDao;
import com.example.springbootmall.dto.ProductQueryParams;
import com.example.springbootmall.dto.ProductRequest;
import com.example.springbootmall.model.Product;
import com.example.springbootmall.rowmapper.ProductRowMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.*;

@Component
public class ProductDaoImpl implements ProductDao {

    @Autowired
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Override
    public Integer countProduct(ProductQueryParams productQueryParams) {
        StringBuilder sqlSb = new StringBuilder();
        sqlSb.append("SELECT count(*) FROM product WHERE 1=1");

        Map<String, Object> map = new HashMap<>();

        ProductCategory category = productQueryParams.getCategory();
        String productName = productQueryParams.getProductName();

        // Filtering
        addFilteringSql(sqlSb, map, category, productName);

        return namedParameterJdbcTemplate.queryForObject(sqlSb.toString(), map, Integer.class);
    }

    @Override
    public List<Product> getProducts(ProductQueryParams productQueryParams) {
        StringBuilder sqlSb = new StringBuilder();
        sqlSb.append("SELECT product_id, product_name, category, image_url, ")
                .append("price, stock, description, created_date, last_modified_date ")
                .append("FROM product WHERE 1=1");

        Map<String, Object> map = new HashMap<>();

        ProductCategory category = productQueryParams.getCategory();
        String productName = productQueryParams.getProductName();
        String orderBy = productQueryParams.getOrderBy();
        String sort = productQueryParams.getSort();
        Integer limit = productQueryParams.getLimit();
        Integer offset = productQueryParams.getOffset();

        // Filtering
        addFilteringSql(sqlSb, map, category, productName);

        // Sorting, "ORDER BY" must use "string append" to implement
        sqlSb.append(" ORDER BY ").append(orderBy).append(" ").append(sort);

        // Pagination
        sqlSb.append(" LIMIT :limit OFFSET :offset");
        map.put("limit", limit);
        map.put("offset", offset);

        return namedParameterJdbcTemplate.query(sqlSb.toString(), map, new ProductRowMapper());
    }

    @Override
    public Product getProductById(Integer productId) {
        String sql = "SELECT product_id, product_name, category, image_url, price, stock, description, created_date, last_modified_date " +
                "FROM product WHERE product_id = :productId";

        Map<String, Object> map = new HashMap<>();
        map.put("productId", productId);

        List<Product> productList = namedParameterJdbcTemplate.query(sql, map, new ProductRowMapper());

        Product product;
        if (CollectionUtils.isEmpty(productList)) {
            product = null;
        } else {
            product = productList.get(0);
        }

        return product;
    }

    @Override
    public Integer createProduct(ProductRequest productRequest) {
        String sql = "INSERT INTO product(product_name, category, image_url, " +
                "price, stock, description, created_date, last_modified_date) " +
                "VALUES (:productName, :category, :imageUrl, :price, :stock, " +
                ":description, :createdDate, :lastModifiedDate)";

        Map<String, Object> map = new HashMap<>();
        putProductRequestDataToMap(productRequest, map);

        Date now = new Date();
        map.put("createdDate", now);
        map.put("lastModifiedDate", now);

        KeyHolder keyHolder = new GeneratedKeyHolder();

        namedParameterJdbcTemplate.update(sql, new MapSqlParameterSource(map), keyHolder);

        return Objects.requireNonNull(keyHolder.getKey()).intValue();
    }

    @Override
    public void updateProduct(Integer productId, ProductRequest productRequest) {
        String sql = "UPDATE product SET product_name = :productName, " +
                "category = :category, image_url = :imageUrl, price = :price, " +
                "stock = :stock, description = :description, last_modified_date = :lastModifiedDate " +
                "WHERE product_id = :productId";

        Map<String, Object> map = new HashMap<>();
        map.put("productId", productId);

        putProductRequestDataToMap(productRequest, map);

        map.put("lastModifiedDate", new Date());

        namedParameterJdbcTemplate.update(sql, map);
    }

    @Override
    public void deleteProductById(Integer productId) {
        String sql = "DELETE FROM product WHERE product_id = :productId";

        Map<String, Object> map = new HashMap<>();
        map.put("productId", productId);

        namedParameterJdbcTemplate.update(sql, map);
    }

    private void putProductRequestDataToMap(ProductRequest productRequest, Map<String, Object> map) {
        map.put("productName", productRequest.getProductName());
        map.put("category", productRequest.getCategory().toString());
        map.put("imageUrl", productRequest.getImageUrl());
        map.put("price", productRequest.getPrice());
        map.put("stock", productRequest.getStock());
        map.put("description", productRequest.getDescription());
    }

    private void addFilteringSql(StringBuilder sqlSb, Map<String, Object> map, ProductCategory category, String productName) {
        // Filtering
        if (Objects.nonNull(category)) {
            sqlSb.append(" AND category = :category");
            map.put("category", category.name());
        }
        if (StringUtils.hasText(productName)) {
            sqlSb.append(" AND product_name LIKE :productName");
            map.put("productName", "%" + productName + "%");
        }
    }
}

package com.scene.mesh.model.product;

import lombok.Data;

/**
 * 产品模型
 */
@Data
public class Product {
    private String id;
    private String name;
    private String description;
    private ProductImage image;
    private String category;
    private ProductSetting settings;

    @Data
    public static class ProductImage {
        private String fileName;
        private String filePath;
        private long fileSize;
        private String fileType;
    }
}

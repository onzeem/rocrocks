package com.example.demo;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "categories")
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String description;

    // Display metadata — used to render the colored dot on category
    // rows/bars/pie slices. Lives here now instead of being duplicated
    // in a separate frontend lookup table, so adding a category only
    // means editing one place instead of two that have to stay in sync.
    private String icon;  // e.g. "🏠"
    private String color; // one of the site's theme colors: grape, coral, mint, yellow, pink, sky, gold, teal

    public Category() {}

    public Category(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public Category(String name, String description, String icon, String color) {
        this.name = name;
        this.description = description;
        this.icon = icon;
        this.color = color;
    }

    public String getName() {
        return name;
    }

    public Long getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public String getIcon() {
        return icon;
    }

    public String getColor() {
        return color;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public void setColor(String color) {
        this.color = color;
    }
}
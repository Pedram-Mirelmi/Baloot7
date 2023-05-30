package ie.baloot7.model;

import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "categories")
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long categoryId;

    @Column(nullable = false)
    private String categoryName;

    @ManyToMany(mappedBy = "categorySet", fetch = FetchType.LAZY)
    private Set<Commodity> commoditySet = new HashSet<>();

    public void setCommoditySet(Set<Commodity> commoditySet) {
        this.commoditySet = commoditySet;
    }


    public Category() {
    }

    public Category(String categoryName) {
        this.categoryName = categoryName;
    }


    public long getCategoryId() {
        return categoryId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public Set<Commodity> getCommoditySet() {
        return commoditySet;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Category category = (Category) o;
        return categoryId == category.categoryId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(categoryId);
    }
}

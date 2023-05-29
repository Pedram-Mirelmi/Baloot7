package ie.baloot6.model;


import com.google.gson.annotations.SerializedName;
import jakarta.persistence.*;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "providers")
public class Provider {

    @Id
    private long providerId;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private Date registryDate;

    @OneToMany(fetch = FetchType.LAZY, cascade=CascadeType.ALL)
    @JoinColumn(name = "providerId")
    private Set<Commodity> commoditySet = new HashSet<>();;

    public Provider(String name, Date registryDate) {
        this.name = name;
        this.registryDate = registryDate;
    }

    public Provider(Provider provider) {
        this(provider.name, provider.registryDate);
        this.providerId = provider.providerId;
    }

    public Provider() {
    }

    public long getProviderId() {
        return providerId;
    }

    public void setProviderId(long id) {
        this.providerId = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public java.util.Date getRegistryDate() {
        return registryDate;
    }

    public void setRegistryDate(Date registryDate) {
        this.registryDate = registryDate;
    }

    @Override
    public boolean equals (Object o){
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Provider provider = (Provider) o;
        return providerId == provider.providerId && name.equals(provider.name) && registryDate.equals(provider.registryDate);
    }

}
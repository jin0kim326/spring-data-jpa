package study.datajpa.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Persistable;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Item extends BaseEntity implements Persistable<String> {
    @Id
    private String id;

    public Item(String id) {
        this.id = id;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public boolean isNew() {
        return getCreatedDate() == null;
    }
}

package study.datajpa.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Getter;

@Entity
@Getter

public class Member {
    @Id @GeneratedValue
    private Long id;
    private String name;

    // JPA는 프록시 기술을 사용할때 private으로 선언되어있으면 제한이생길수있음, 최대 protected로 보호
    protected Member() {
    }

    public Member(String name) {
        this.name = name;
    }

    public void changeUserName(String userName){
        this.name = userName;
    }
}

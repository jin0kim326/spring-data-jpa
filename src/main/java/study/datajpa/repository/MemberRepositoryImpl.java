package study.datajpa.repository;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import study.datajpa.entity.Member;

import java.util.List;

/**
 * 🔥 MemberRepository라는 이름 + Impl 을 맞춰줘야함!! (인터페이스는 상관없음, Custom이 아니여도 문제없음)
 */
@RequiredArgsConstructor
public class MemberRepositoryImpl implements MemberRepositoryCustom{
    private final EntityManager em;

    @Override
    public List<Member> findMemberCustom() {
        return em.createQuery("select m from Member m" )
                .getResultList();
    }
}

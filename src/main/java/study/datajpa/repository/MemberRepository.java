package study.datajpa.repository;

import jakarta.persistence.LockModeType;
import jakarta.persistence.QueryHint;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import study.datajpa.dto.MemberDto;
import study.datajpa.entity.Member;

import java.util.List;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {
    /**
     * 1.쿼리 메소드
     * => 이름이 길어진다.
     */
    List<Member> findByUsernameAndAgeGreaterThan(String username, int age);

    /**
     * 2.네임드 쿼리
     * => 엔티티에 정의된 네임쿼리를 실행 (파라미터 오타시 애플리케이션 로딩 시점에서 에러발생한다는 장점)
     * => 그러나 왔다갔다 해야함..
     */
    @Query(name = "Member.findByUsername2")
    List<Member> findByUsername(@Param("username") String username);

    /**
     * 3.레포지토리에서 바로 쿼리를 정의
     * => 실무에서 사용함, 가장 추천하는 방법
     */
    @Query("select m from Member m where m.username = :username and m.age=:age")
    List<Member> findUser(@Param("username") String username, @Param("age") int age);

    @Query("select m.username from Member m")
    List<String> findUsernameList();

    @Query("select new study.datajpa.dto.MemberDto(m.id, m.username, t.name) from Member m join m.team t")
    List<MemberDto> findMemberDto();

    /**
     * JPA반환타입 3가지
     * - 컬렉션
     * - 클래스 단건
     * - 옵셔널
     */
    List<Member> findListByUsername(String username);
    Member findMemberByUsername(String username);
    Optional<Member> findOptionalByUsername(String username);

    Page<Member> findJoinByAge(int age, Pageable pageable);
    Slice<Member> findSliceByAge(int age, Pageable pageable);

    @Modifying(clearAutomatically = true)
    @Query("update Member m set m.age = m.age+3 where m.age >= :age")
    int bulkAgePlus(@Param("age") int age);

    @Query("select m from Member m left join fetch m.team t")
    List<Member> findMembersFetchJoin();

    @Override
    @EntityGraph(attributePaths = {"team"})
    List<Member> findAll();

    @EntityGraph(attributePaths = {"team"})
    @Query("select m from Member m")
    List<Member> findMemberEntityGraph();

    @EntityGraph(attributePaths = {"team"})
//    @EntityGraph("Member.all") //named
    List<Member> findEntityGraphByUsername(@Param("username") String username);

    @QueryHints(value = @QueryHint(name = "org.hibernate.readOnly", value = "true"))
    Member findReadOnlyByUsername(String username);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    List<Member> findLockByUsername(String username);
}

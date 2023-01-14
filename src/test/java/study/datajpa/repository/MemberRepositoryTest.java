package study.datajpa.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import study.datajpa.dto.MemberDto;
import study.datajpa.entity.Member;
import study.datajpa.entity.Team;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
@Rollback(value = false)
class MemberRepositoryTest {
    @Autowired MemberRepository memberRepository;
    @Autowired TeamRepository teamRepository;
    @PersistenceContext
    EntityManager em;

    @Test
    public void testMember() throws Exception {
        // given
        Member member = new Member("kimjinyoungA");
        Member savedMember = memberRepository.save(member);
        // when
        Member findMember = memberRepository.findById(savedMember.getId()).get();
        // then
        assertThat(findMember.getId()).isEqualTo(member.getId());
        assertThat(findMember.getUsername()).isEqualTo(member.getUsername());
        assertThat(findMember).isEqualTo(member); // findMember == member (JPA에서는 같은 영속성에서는 동일한 객체를 반환)
    }

    @Test
    public void basicCRUD() throws Exception {
        // given
        Member member1 = new Member("kimjinyoung");
        Member member2 = new Member("parkkildong");
        memberRepository.save(member1);
        memberRepository.save(member2);

        // 저장 및 단순조회검증
        Member findMember1 = memberRepository.findById(member1.getId()).get();
        Member findMember2 = memberRepository.findById(member2.getId()).get();

        assertThat(findMember1).isEqualTo(member1);
        assertThat(findMember2).isEqualTo(member2);

        //리스트 조회 검증
        List<Member> all = memberRepository.findAll();
        assertThat(all.size()).isEqualTo(2);

        long count = memberRepository.count();
        assertThat(count).isEqualTo(2);

        //삭제검증
        memberRepository.delete(member1);
        memberRepository.delete(member2);

        long deletedCount = memberRepository.count();
        assertThat(deletedCount).isEqualTo(0);
    }

    @Test
    public void findByUsernameAndAgeGreaterThan() throws Exception {
        // given
        Member m1 = new Member("aa", 10);
        Member m2 = new Member("aa", 20);
        memberRepository.save(m1);
        memberRepository.save(m2);

        List<Member> result =
                memberRepository.findByUsernameAndAgeGreaterThan("aa", 15);

        assertThat(result.get(0).getUsername()).isEqualTo("aa");
        assertThat(result.get(0).getAge()).isEqualTo(20);
        assertThat(result.size()).isEqualTo(1);
    }

    @Test
    public void namedQueryWithDataJpa() throws Exception {
        Member m1 = new Member("aa", 10);
        Member m2 = new Member("bb", 20);
        memberRepository.save(m1);
        memberRepository.save(m2);

        List<Member> result = memberRepository.findByUsername("aa");
        Member findMember = result.get(0);

        assertThat(findMember).isEqualTo(m1);
    }

    @Test
    public void queryAnotationWithDataJpa() throws Exception {
        Member m1 = new Member("aa", 10);
        Member m2 = new Member("bb", 20);
        memberRepository.save(m1);
        memberRepository.save(m2);

        List<Member> result = memberRepository.findUser("aa",10);
        Member findMember = result.get(0);
        assertThat(findMember).isEqualTo(m1);
    }

    @Test
    public void findUsernameList() throws Exception {
        Member m1 = new Member("aa", 10);
        Member m2 = new Member("bb", 20);
        memberRepository.save(m1);
        memberRepository.save(m2);

        List<String> usernameList = memberRepository.findUsernameList();
        for (String s : usernameList) {
            System.out.println(s);
        }
    }

    @Test
    public void findDto() throws Exception {
        Team teamA = new Team("FC Bar");
        Team teamB = new Team("FC Real");
        teamRepository.save(teamA);
        teamRepository.save(teamB);

        Member m1 = new Member("aa", 10, teamA);
        Member m2 = new Member("bb", 20, teamB);

        memberRepository.save(m1);
        memberRepository.save(m2);

        List<MemberDto> usernameList = memberRepository.findMemberDto();
        for (MemberDto memberDto : usernameList) {
            System.out.println(memberDto);
        }
    }

    @Test
    public void returnCollection() throws Exception {
        Member m1 = new Member("aa", 10);
        Member m2 = new Member("bb", 20);
        Member m3 = new Member("aa", 30);
        memberRepository.save(m1);
        memberRepository.save(m2);
        memberRepository.save(m3);

        List<Member> aa = memberRepository.findListByUsername("aa");
        for (Member member : aa) {
            System.out.println(member);
        }

        // 컬렉션 조회결과가 없는경우 빈 Empty컬렉션 반환
        List<Member> result = memberRepository.findListByUsername("asdfghj");
        System.out.println("결과 >> " + result.size());


        // 클래스(단건)반환의 경우 조회결과가 없으면 null 반환
        Member member = memberRepository.findMemberByUsername("asdfghj");
        System.out.println("member::: " + member );

        // 단건반환인데 조회결과가 1건보다 많을경우 exception반 (IncorrectResultSizeDataAccessException)
//        Member member2 = memberRepository.findMemberByUsername("aa");
//        System.out.println(member2);

        // best : 디비를 조회하려는데 데이터가 있는지, 없는지 모른다 ? => optional 사용
        Optional<Member> aa1 = memberRepository.findOptionalByUsername("bb");
        System.out.println(aa1);
    }

    @Test
    public void paging() throws Exception {
        // given
        memberRepository.save(new Member("member1", 10));
        memberRepository.save(new Member("member2", 10));
        memberRepository.save(new Member("member3", 10));
        memberRepository.save(new Member("member4", 10));
        memberRepository.save(new Member("member5", 10));

        int age =10;
        // ** 🔥 페이지의 인덱스는 0부터 시작
        PageRequest pageRequest = PageRequest.of(0, 3, Sort.by(Sort.Direction.DESC, "username"));

        // when
        Page<Member> page = memberRepository.findJoinByAge(age, pageRequest);

        // 페이징 결과( Page<Member> )를 바로 API결과로 반환하면 안됨 : API결과에 엔티티를 노출시키지않기!
        Page<MemberDto> toMap
                = page.map(m -> new MemberDto(m.getId(), m.getUsername(), null));

        // then
        List<Member> content = page.getContent();
        long totalElements = page.getTotalElements();

        for (Member member : content) {
            System.out.println(member);
        }
        System.out.println(totalElements);

        assertThat(content.size()).isEqualTo(3);    //현재 페이지 데이터 수
        assertThat(page.getTotalElements()).isEqualTo(5); // 총 데이터로우수
        assertThat(page.getNumber()).isEqualTo(0);  // 현재 페이지
        assertThat(page.getTotalPages()).isEqualTo(2); // 총 페이지
        assertThat(page.isFirst()).isTrue();
        assertThat(page.hasNext()).isTrue();
    }

    @Test
    public void slice() throws Exception {
        // given
        memberRepository.save(new Member("member1", 10));
        memberRepository.save(new Member("member2", 10));
        memberRepository.save(new Member("member3", 10));
        memberRepository.save(new Member("member4", 10));
        memberRepository.save(new Member("member5", 10));

        int age =10;
        PageRequest pageRequest = PageRequest.of(0, 3, Sort.by(Sort.Direction.DESC, "username"));

        // when
        Slice<Member> page = memberRepository.findSliceByAge(age, pageRequest);

        // then
        List<Member> content = page.getContent();
//        long totalElements = page.getTotalElements(); 슬라이스는 total카운트를 날리지않음


        assertThat(content.size()).isEqualTo(3);    //현재 페이지 데이터 수
//        assertThat(page.getTotalElements()).isEqualTo(5); // 총 데이터로우수
        assertThat(page.getNumber()).isEqualTo(0);  // 현재 페이지
//        assertThat(page.getTotalPages()).isEqualTo(2); // 총 페이지
        assertThat(page.isFirst()).isTrue();
        assertThat(page.hasNext()).isTrue();
    }

    @Test
    public void bulkUpdateWithdDataJpa() throws Exception {
        // given
        memberRepository.save(new Member("member1", 10));
        memberRepository.save(new Member("member2", 19));
        memberRepository.save(new Member("member3", 20));
        memberRepository.save(new Member("member4", 23));
        memberRepository.save(new Member("member5", 35));

        // when
        int result = memberRepository.bulkAgePlus(20);

        /**
         * 🔥🔥🔥🔥🔥🔥🔥🔥
         * 벌크연산 주의점 : JPA (영속성 컨텍스트를 무시하고 디비에 처리하기때문에,
         * 다시 조회하면 업데이트 반영분이아니라 영속성컨텍스트에 남아있는 결과로 조회함
         * => 해결법 : 벌크연산 후 [영속성 컨텍스트를 날려버린다]
         *    - em.clear 혹은 @Modifying(clearAutomatically = true) 옵션
         */
        List<Member> members = memberRepository.findByUsername("member5");
        Member findMember = members.get(0);
        System.out.println(findMember);

        // then
        assertThat(result).isEqualTo(3);
    }

    @Test
    public void entityGraph() throws Exception {
        Team teamA = new Team("FC Bar");
        Team teamB = new Team("FC Real");
        teamRepository.save(teamA);
        teamRepository.save(teamB);

        Member m1 = new Member("member1", 10, teamA);
        Member m2 = new Member("member2", 20, teamB);
        memberRepository.save(m1);
        memberRepository.save(m2);

        em.flush();
        em.clear();

        //when
        // N+1
        List<Member> members = memberRepository.findAll();
        for (Member member : members) {
            System.out.println("member = " + member.getUsername());
            System.out.println("member.teamClass = " + member.getTeam().getClass());
            System.out.println("member.team = " + member.getTeam().getName());
        }

        // 패치조인
//        List<Member> fetchMembers = memberRepository.findMembersFetchJoin();
//        for (Member fetchMember : fetchMembers) {
//            System.out.println("fetchMember = " + fetchMember.getUsername());
//            System.out.println("f etchMember.teamClass = " + fetchMember.getTeam().getClass());
//            System.out.println("fetchMember.team = " + fetchMember.getTeam().getName());
//        }

        //
    }

    @Test
    public void queryHint() throws Exception {
        // given
        Member member = new Member("member1", 10);
        memberRepository.save(member);

        em.flush();
        em.clear();

        // when 1
//        Member findMember = memberRepository.findById(member.getId()).get();
//        findMember.setUsername("member2");
//        em.flush();
        /**
         * 변경감지로 데이터를 업데이트하기 위해서는 원본(변경되기전)을 가지고 있어야 한다.
         * 100% 조회용이면 낭비
         *
         */

        //when 2
        // @QueryHint를 사용하면 업데이트 쿼리가 안나가도록 설정가능
        Member findMember = memberRepository.findReadOnlyByUsername("member1");
        findMember.setUsername("member2");
        em.flush();
    }
    
    @Test
    public void lock() throws Exception {
        // given
        Member member = new Member("member1", 10);
        memberRepository.save(member);
        em.flush();
        em.clear();

        List<Member> member1 = memberRepository.findLockByUsername("member1");
    }

    @Test
    public void callCustom() throws Exception {
        List<Member> memberCustom = memberRepository.findMemberCustom();
    }
}
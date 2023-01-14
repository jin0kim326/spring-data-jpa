package study.datajpa.controller;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import study.datajpa.dto.MemberDto;
import study.datajpa.entity.Member;
import study.datajpa.repository.MemberRepository;

@RestController
@RequiredArgsConstructor
public class MemberController {
    private final MemberRepository memberRepository;

    @GetMapping("/members/{id}")
    public String findMember(@PathVariable("id") Long id) {
        Member member = memberRepository.findById(id).get();
        return member.getUsername();
    }

    /**
     * 도메인 클래스 컨버터가 중간에 동작해서 회원엔티티 객체를 반환,
     * 도메인 클래스 컨버터도 리파지토리를 사용해서 엔티티를 찾음(쿼리나감)
     *
     * 단순 조회용으로만 사용할것 (트랜잭션이 없는 범위에서 엔티티조회 -> 엔티티를변경해도 디비에 반영안됨)
     */
    @GetMapping("/members2/{id}")
    public String findMember2(@PathVariable("id") Member member) {
        return member.getUsername();
    }

    /**
     * ex) http://localhost:8078/members?page=0&size=3&sort=id,desc
     *
     *
     */
    @GetMapping("members")
    public Page<MemberDto> list(@PageableDefault(size = 5) Pageable pageable) {
        Page<Member> page = memberRepository.findAll(pageable);
        Page<MemberDto> map = page.map(member -> new MemberDto(member.getId(), member.getUsername(), null));
        return map;
    }

//    @PostConstruct
    public void init() {
        for ( int i=0; i<100; i++) {
            memberRepository.save(new Member("member"+i, i));
        }


    }
}

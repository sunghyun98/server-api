package gong.server_api.repository;

import gong.server_api.domain.entity.Member;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.*;

@Slf4j
@Repository
public class MemberRepository {

    private static Map<Long, Member> store = new HashMap<>();
    private static long sequence = 0L;

    public Member save(Member member){
        member.setId((++sequence));
        log.info("save: member={}",member);
        store.put(member.getId(),member);
        return member;
    }

    public Optional<Member> findByLoginId(String email){
    /*    List<Member> all = findAll();
        for (Member m : all) {
            if (m.getEmail().equals(email)){
                return Optional.of(m);
            }
        }
        return Optional.empty();
*/
        return findAll().stream()
                .filter(m->m.getEmail().equals(email))
                .findFirst();
    }
    public Member findById(Long id){
        return store.get(id);
    }
    public List<Member> findAll(){
        return new ArrayList<>(store.values());
    }

    public void clearStore(){
        store.clear();
    }
}

package com.viora.repository;

// src/main/java/com/viora/repository/UserRepository.java

import com.viora.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> { // <엔티티, PK타입>

    /**
     * Spring Data JPA의 Query Method 기능
     * 메서드 이름만 규칙에 맞게 지으면, Spring이 알아서 JPQL 쿼리를 생성하고 실행해줍니다.
     * * [규칙]
     * findBy + {필드 이름}
     * existsBy + {필드 이름}
     */

    // email을 기준으로 사용자를 찾는 메서드
    // 결과가 없을 수도 있으므로, NullPointerException(NPE) 방지를 위해 Optional<T>로 감싸서 반환합니다.
    Optional<User> findByEmail(String email);

    // email을 가진 사용자가 이미 존재하는지 확인하는 메서드 (true/false 반환)
    boolean existsByEmail(String email);

    Optional<User> findByNickname(String nickname);
    Optional<User> findByEmailAndNickname(String email, String nickname);
}
//package com.viora.config;
//
//import com.viora.entity.Review;
//import com.viora.entity.User;
//import com.viora.repository.ReviewRepository;
//import com.viora.repository.UserRepository;
//import lombok.RequiredArgsConstructor;
//import org.springframework.boot.CommandLineRunner;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.stereotype.Component;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.util.Arrays;
//import java.util.List;
//
//@Component
//@RequiredArgsConstructor
//public class DataInitializer implements CommandLineRunner {
//
//    private final UserRepository userRepository;
//    private final ReviewRepository reviewRepository;
//    private final PasswordEncoder passwordEncoder;
//
//    @Override
//    @Transactional
//    public void run(String... args) throws Exception {
//        // 1. 테스트용 사용자 생성 (이미 있으면 건너뜀)
//        if (userRepository.findByEmail("test@viora.com").isEmpty()) {
//            User user = User.builder()
//                    .email("test@viora.com")
//                    .passwordHash(passwordEncoder.encode("password")) // 비밀번호: password
//                    .nickname("비오라테스트")
//                    .provider(com.viora.entity.Provider.VIORA)
//                    .build();
//            userRepository.save(user);
//        }
//
//        // 사용자를 가져옴 (리뷰 작성자로 사용)
//        User testUser = userRepository.findByEmail("test@viora.com").orElseThrow();
//
//        // 2. 리뷰 데이터가 하나도 없을 때만 샘플 데이터 추가
//        if (reviewRepository.count() == 0) {
//            List<Review> sampleReviews = Arrays.asList(
//                    // --- 기존 데이터 (7개) ---
//                    Review.builder()
//                            .user(testUser)
//                            .category("맛집")
//                            .contentName("스시 오마카세")
//                            .location("서울 강남구 역삼동")
//                            .text("신선한 재료 본연의 맛을 잘 살렸습니다. 셰프님의 접객도 훌륭해서 기념일에 가기 딱 좋아요. 예약은 필수입니다!")
//                            .rating(5)
//                            .imageUrl("https://placehold.co/600x400?text=Sushi+Omakase") // ❗️ placehold.co로 변경
//                            .build(),
//                    Review.builder()
//                            .user(testUser)
//                            .category("카페")
//                            .contentName("어니언 성수")
//                            .location("서울 성동구 성수동")
//                            .text("힙한 분위기의 베이커리 카페. 팡도르가 정말 맛있지만 주말엔 사람이 너무 많아서 자리 잡기가 힘들어요.")
//                            .rating(4)
//                            .imageUrl("https://placehold.co/600x400?text=Cafe+Onion")
//                            .build(),
//                    Review.builder()
//                            .user(testUser)
//                            .category("숙소")
//                            .contentName("시그니엘 서울")
//                            .location("서울 송파구 신천동")
//                            .text("뷰가 모든 것을 용서하는 곳. 서울의 야경을 한눈에 담으며 즐기는 룸서비스는 최고였습니다. 비싸지만 가치 있어요.")
//                            .rating(5)
//                            .imageUrl("https://placehold.co/600x400?text=Signiel+Seoul")
//                            .build(),
//                    Review.builder()
//                            .user(testUser)
//                            .category("여행지")
//                            .contentName("제주 협재 해수욕장")
//                            .location("제주 제주시 한림읍")
//                            .text("에메랄드빛 바다가 정말 아름답습니다. 수심이 얕아서 아이들과 놀기도 좋고, 일몰 때 풍경이 예술이에요.")
//                            .rating(5)
//                            .imageUrl("https://placehold.co/600x400?text=Jeju+Beach")
//                            .build(),
//                    Review.builder()
//                            .user(testUser)
//                            .category("영화")
//                            .contentName("인터스텔라")
//                            .location(null)
//                            .text("SF 영화의 걸작. 압도적인 영상미와 한스 짐머의 음악, 그리고 가족애를 다룬 스토리까지 완벽합니다. 아이맥스로 못 본 게 한이네요.")
//                            .rating(5)
//                            .imageUrl("https://placehold.co/600x400?text=Interstellar")
//                            .build(),
//                    Review.builder()
//                            .user(testUser)
//                            .category("도서")
//                            .contentName("미움받을 용기")
//                            .location(null)
//                            .text("인간관계 때문에 힘들 때 읽고 많은 위로를 받았습니다. 아들러 심리학을 대화 형식으로 쉽게 풀어내서 술술 읽혀요.")
//                            .rating(4)
//                            .imageUrl("https://placehold.co/600x400?text=Book+Cover")
//                            .build(),
//                    Review.builder()
//                            .user(testUser)
//                            .category("기타")
//                            .contentName("소니 노이즈캔슬링 헤드폰")
//                            .location(null)
//                            .text("출퇴근길의 구세주입니다. 노캔 성능 확실하고 착용감도 편안해요. 배터리도 오래 갑니다.")
//                            .rating(4)
//                            .imageUrl("https://placehold.co/600x400?text=Headphones")
//                            .build(),
//
//                    // --- 🆕 추가된 데이터 (7개) ---
//                    Review.builder()
//                            .user(testUser)
//                            .category("맛집")
//                            .contentName("청담동 한우 코스")
//                            .location("서울 강남구 청담동")
//                            .text("입에서 살살 녹는 한우 오마카세. 가격대는 좀 있지만 특별한 날 분위기 내기에는 최고입니다. 와인 페어링도 좋았어요.")
//                            .rating(5)
//                            .imageUrl("https://placehold.co/600x400?text=Hanwoo+Beef")
//                            .build(),
//                    Review.builder()
//                            .user(testUser)
//                            .category("카페")
//                            .contentName("블루보틀 삼청")
//                            .location("서울 종로구 북촌로")
//                            .text("한옥 기와 뷰가 보이는 멋진 카페. 커피 맛은 명불허전이고, 2층 창가 자리에 앉으면 북악산이 보여서 힐링됩니다.")
//                            .rating(5)
//                            .imageUrl("https://placehold.co/600x400?text=Blue+Bottle")
//                            .build(),
//                    Review.builder()
//                            .user(testUser)
//                            .category("숙소")
//                            .contentName("파라다이스 시티")
//                            .location("인천 중구 영종해안남로")
//                            .text("호캉스의 끝판왕. 수영장, 테마파크, 미술관까지 호텔 안에서 모든 걸 해결할 수 있어요. 아이들과 함께 가기에도 좋습니다.")
//                            .rating(5)
//                            .imageUrl("https://placehold.co/600x400?text=Paradise+City")
//                            .build(),
//                    Review.builder()
//                            .user(testUser)
//                            .category("여행지")
//                            .contentName("경주 불국사")
//                            .location("경북 경주시 진현동")
//                            .text("가을 단풍이 정말 아름다운 곳. 다보탑과 석가탑을 실제로 보니 웅장함이 느껴졌습니다. 수학여행의 추억이 새록새록 하네요.")
//                            .rating(5)
//                            .imageUrl("https://placehold.co/600x400?text=Bulguksa")
//                            .build(),
//                    Review.builder()
//                            .user(testUser)
//                            .category("영화")
//                            .contentName("탑건: 매버릭")
//                            .location(null)
//                            .text("극장에서 안 봤으면 후회할 뻔했습니다. 전투기 액션 씬의 쾌감이 엄청납니다. 톰 크루즈 형님은 늙지도 않네요.")
//                            .rating(5)
//                            .imageUrl("https://placehold.co/600x400?text=Top+Gun")
//                            .build(),
//                    Review.builder()
//                            .user(testUser)
//                            .category("도서")
//                            .contentName("불편한 편의점")
//                            .location(null)
//                            .text("마음이 따뜻해지는 힐링 소설입니다. 각자의 사연을 가진 인물들이 편의점이라는 공간에서 서로 위로받는 이야기가 감동적이에요.")
//                            .rating(4)
//                            .imageUrl("https://placehold.co/600x400?text=Book+Novel")
//                            .build(),
//                    Review.builder()
//                            .user(testUser)
//                            .category("기타")
//                            .contentName("닌텐도 스위치")
//                            .location(null)
//                            .text("젤다의 전설 하려고 샀는데 후회 없습니다. 휴대 모드로 침대에서 뒹굴거리며 게임하는 게 최고네요. 시간 가는 줄 모릅니다.")
//                            .rating(5)
//                            .imageUrl("https://placehold.co/600x400?text=Nintendo+Switch")
//                            .build()
//            );
//
//            reviewRepository.saveAll(sampleReviews);
//            System.out.println("✅ 샘플 리뷰 데이터 14건이 추가되었습니다!");
//        }
//    }
//}
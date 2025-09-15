package io.goorm.team02.core.reviews.domain;

import io.goorm.team02.core.orders.domain.Order;
import io.goorm.team02.core.stores.domain.Store;
import io.goorm.team02.core.users.domain.User;
import io.goorm.team02.core.TestEnv;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("Review 도메인 테스트")
class ReviewTest extends TestEnv {

    private Review review;
    private Order order;
    private User user;
    private Store store;

    @BeforeEach
    void setUp() {
        review = new Review();
        order = new Order();
        user = new User();
        store = new Store();
        
        review.setOrder(order);
        review.setUser(user);
        review.setStore(store);
        review.setRating(5);
        review.setContent("맛있어요!");
    }

    @Test
    @DisplayName("validate - 정상적인 리뷰 검증")
    void validate() {
        // when & then
        review.validate(); // 예외가 발생하지 않아야 함
    }

    @Test
    @DisplayName("validate - 사용자 정보가 없을 때 예외 발생")
    void validateWithNullUser() {
        // given
        review.setUser(null);

        // when & then
        assertThatThrownBy(() -> review.validate())
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("사용자 정보가 필요합니다");
    }

    @Test
    @DisplayName("validate - 가게 정보가 없을 때 예외 발생")
    void validateWithNullStore() {
        // given
        review.setStore(null);

        // when & then
        assertThatThrownBy(() -> review.validate())
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("가게 정보가 필요합니다");
    }

    @Test
    @DisplayName("validate - 주문 정보가 없을 때 예외 발생")
    void validateWithNullOrder() {
        // given
        review.setOrder(null);

        // when & then
        assertThatThrownBy(() -> review.validate())
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("주문 정보가 필요합니다");
    }

    @Test
    @DisplayName("validate - 평점이 범위를 벗어날 때 예외 발생")
    void validateWithInvalidRating() {
        // given
        review.setRating(6);

        // when & then
        assertThatThrownBy(() -> review.validate())
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("평점은 1~5 사이여야 합니다");
    }

    @Test
    @DisplayName("addOwnerReply - 사장님 답글 추가")
    void addOwnerReply() {
        // given
        String reply = "감사합니다!";

        // when
        review.addOwnerReply(reply);

        // then
        assertThat(review.getOwnerReply()).isEqualTo(reply);
        assertThat(review.getOwnerRepliedAt()).isNotNull();
    }

    @Test
    @DisplayName("addOwnerReply - 빈 답글일 때 예외 발생")
    void addOwnerReplyWithEmptyReply() {
        // when & then
        assertThatThrownBy(() -> review.addOwnerReply(""))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("답글 내용이 필요합니다");
    }

    @Test
    @DisplayName("report - 리뷰 신고")
    void report() {
        // when
        review.report();

        // then
        assertThat(review.getIsReported()).isTrue();
    }

    @Test
    @DisplayName("update - 리뷰 수정")
    void update() {
        // given
        Integer newRating = 4;
        String newContent = "수정된 리뷰";

        // when
        review.update(newRating, newContent);

        // then
        assertThat(review.getRating()).isEqualTo(newRating);
        assertThat(review.getContent()).isEqualTo(newContent);
    }

    @Test
    @DisplayName("update - 잘못된 평점으로 수정 시 예외 발생")
    void updateWithInvalidRating() {
        // when & then
        assertThatThrownBy(() -> review.update(0, "내용"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("평점은 1~5 사이여야 합니다");
    }

    @Test
    @DisplayName("create - 리뷰 생성")
    void create() {
        // when
        Review createdReview = Review.create(order, user, store, 5, "맛있어요!");

        // then
        assertThat(createdReview.getOrder()).isEqualTo(order);
        assertThat(createdReview.getUser()).isEqualTo(user);
        assertThat(createdReview.getStore()).isEqualTo(store);
        assertThat(createdReview.getRating()).isEqualTo(5);
        assertThat(createdReview.getContent()).isEqualTo("맛있어요!");
        assertThat(createdReview.getIsReported()).isFalse();
    }

    @Test
    @DisplayName("create - 잘못된 평점으로 생성 시 예외 발생")
    void createWithInvalidRating() {
        // when & then
        assertThatThrownBy(() -> Review.create(order, user, store, 6, "내용"))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("평점은 1~5 사이여야 합니다");
    }
}

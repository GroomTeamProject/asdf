package io.goorm.team02.review.event;

import io.goorm.team02.kafka.model.BaseEvent;
import io.goorm.team02.review.entity.Review;
import lombok.Getter;

@Getter
public class ReviewCreatedEvent extends BaseEvent {

}

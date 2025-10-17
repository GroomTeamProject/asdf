package io.goorm.team02.core.deliveries.controller.dto;

import jakarta.validation.constraints.NotNull;

public record AcceptRequest(@NotNull Long riderId) {}
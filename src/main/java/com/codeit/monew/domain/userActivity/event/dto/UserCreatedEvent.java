package com.codeit.monew.domain.userActivity.event.dto;

import com.codeit.monew.domain.user.entity.User;

public record UserCreatedEvent(User user) {
}

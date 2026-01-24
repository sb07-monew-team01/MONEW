package com.codeit.monew.domain.userActivity.event;

import com.codeit.monew.domain.user.entity.User;

public record UserCreatedEvent(User user) {
}

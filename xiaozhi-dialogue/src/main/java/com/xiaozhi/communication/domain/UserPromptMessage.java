package com.xiaozhi.communication.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public final class UserPromptMessage extends Message {
    public UserPromptMessage() {
        super("user_prompt");
    }

    private String text;
}

package me.jaesung.simplepg.common.event;

import lombok.Getter;

@Getter
public class CredentialUpdateEvent {

    private String clientId;

    public CredentialUpdateEvent(String clientId) {
        this.clientId = clientId;
    }
}

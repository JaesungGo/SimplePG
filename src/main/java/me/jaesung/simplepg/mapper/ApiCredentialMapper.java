package me.jaesung.simplepg.mapper;

import me.jaesung.simplepg.domain.vo.api.ApiCredential;

public interface ApiCredentialMapper {
    ApiCredential findByClientId(String clientId);
}

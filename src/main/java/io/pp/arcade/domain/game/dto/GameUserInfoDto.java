package io.pp.arcade.domain.game.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class GameUserInfoDto {
    private String intraId;
    private String userImageUri;
}

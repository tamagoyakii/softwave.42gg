package io.pp.arcade.domain.user.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserRivalRecordDto {
    Integer curUserWin;
    Integer targetUserWin;
}

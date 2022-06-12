package io.pp.arcade.domain.user.dto;

import io.pp.arcade.domain.user.User;
import io.pp.arcade.global.util.RacketType;
import lombok.Builder;
import lombok.Getter;

import javax.persistence.Column;
import javax.validation.constraints.NotNull;
import java.util.Objects;

@Getter
@Builder
public class UserDto {
    private Integer id;
    private String intraId;
    private String imageUri;
    private RacketType racketType;
    private String statusMessage;
    private Integer ppp;

    public static UserDto from(User user) {
        UserDto userDto;
        if (user == null) {
            userDto = null;
        } else {
            userDto = UserDto.builder()
                    .id(user.getId())
                    .intraId(user.getIntraId())
                    .imageUri(user.getImageUri())
                    .racketType(user.getRacketType())
                    .statusMessage(user.getStatusMessage())
                    .ppp(user.getPpp())
                    .build();
        }
        return userDto;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserDto userDto = (UserDto) o;
        return Objects.equals(id, userDto.id) && Objects.equals(intraId, userDto.intraId) && Objects.equals(imageUri, userDto.imageUri) && racketType == userDto.racketType && Objects.equals(statusMessage, userDto.statusMessage) && Objects.equals(ppp, userDto.ppp);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, intraId, imageUri, racketType, statusMessage, ppp);
    }
}

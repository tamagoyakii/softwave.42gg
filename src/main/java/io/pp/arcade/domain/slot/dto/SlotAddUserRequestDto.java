package io.pp.arcade.domain.slot.dto;

import io.pp.arcade.global.type.Mode;
import lombok.Getter;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

@Getter
public class SlotAddUserRequestDto {
    @NotNull
    @Positive
    private Integer slotId;
    private Mode mode;

    @Override
    public String toString() {
        return "SlotAddUserRequestDto{" +
                "slotId=" + slotId +
                "mode=" + mode.getCode() +
                '}';
    }
}

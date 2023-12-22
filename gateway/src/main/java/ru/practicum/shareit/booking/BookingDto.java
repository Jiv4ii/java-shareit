package ru.practicum.shareit.booking;

import lombok.*;

import javax.validation.constraints.Future;
import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BookingDto {

	@Positive
	private int itemId;

	@NotNull(message = "Отсутствует начальная точка отсчета.")
	@FutureOrPresent
	private LocalDateTime start;

	@NotNull(message = "Отсутствует конечная точка отсчета.")
	@Future
	private LocalDateTime end;
}
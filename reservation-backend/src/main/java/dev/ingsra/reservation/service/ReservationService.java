package dev.ingsra.reservation.service;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import dev.ingsra.reservation.exception.BusinessRuleViolationException;
import dev.ingsra.reservation.model.entity.Reservation;
import dev.ingsra.reservation.model.entity.ReservationStatus;
import dev.ingsra.reservation.repository.ReservationRepository;

@Service
@Transactional(readOnly = true)
public class ReservationService {

    private final ReservationRepository reservationRepository;

    public ReservationService(ReservationRepository reservationRepository) {
        this.reservationRepository = reservationRepository;
    }

    public List<Reservation> findAllReservations() {
        return reservationRepository.findAll();
    }

    @Transactional
    public Reservation createReservation(Reservation reservation) {
        if (reservationRepository.existsByDateAndTime(
                reservation.getDate(), reservation.getTime())) {
            throw new BusinessRuleViolationException(
                    "Ya existe una reserva activa para la fecha y hora seleccionadas.");
        }
        reservation.setStatus(ReservationStatus.ACTIVE);
        return reservationRepository.save(reservation);
    }

    @Transactional
    public Reservation cancelReservation(Long id) {
        Reservation reservation = reservationRepository
                .findById(id)
                .orElseThrow(() -> new BusinessRuleViolationException(
                        "No se encontró la reserva con id: " + id, HttpStatus.NOT_FOUND));
        if (reservation.getStatus() == ReservationStatus.CANCELLED) {
            throw new BusinessRuleViolationException("La reserva ya está cancelada.");
        }
        reservation.setStatus(ReservationStatus.CANCELLED);
        return reservationRepository.save(reservation);
    }
}

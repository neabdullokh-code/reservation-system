package school.sorokin.reservation_system;

import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.concurrent.atomic.AtomicLong;

@Service 
public class ReservationService {

    private final Map<Long, Reservation> reservationMap;
    private final AtomicLong idCounter;

    public ReservationService() {
        reservationMap = new HashMap<>();
        idCounter = new AtomicLong();
    }

    public Reservation getReservationById(Long id) {
        if (!reservationMap.containsKey(id)) {
            throw new NoSuchElementException("Reservation with id " + id + " not found");
        }
        return reservationMap.get(id);
    }

    public List<Reservation> findAllReservations() {
        return reservationMap.values().stream().toList();
    }

    public Reservation createReservation(Reservation reservationToCreate) {
        if (reservationToCreate.id() != null) {
            throw new IllegalArgumentException("Id should be null");
        }
        if (!(reservationToCreate.status() == null)){
            throw new IllegalArgumentException("Status should be null");
        }
            var newReservation = new Reservation(
                    idCounter.incrementAndGet(),
                    reservationToCreate.userId(),
                    reservationToCreate.roomId(),
                    reservationToCreate.startDate(),
                    reservationToCreate.endDate(),
                    ReservationStatus.PENDING
            );
        reservationMap.put(newReservation.id(), newReservation);
        return newReservation;
    }

    public Reservation updateReservation(Long id, Reservation reservationToUpdate) {
        if (!reservationMap.containsKey(id)) {
            throw new NoSuchElementException("Reservation with id " + id + " not found");
        }
        var reservation = reservationMap.get(id);
        if (reservationToUpdate.status() != ReservationStatus.PENDING) {
            throw new IllegalArgumentException("Status should be PENDING");
        }
        var updatedReservation = new Reservation(
                reservation.id(),
                reservationToUpdate.userId(),
                reservationToUpdate.roomId(),
                reservationToUpdate.startDate(),
                reservationToUpdate.endDate(),
                ReservationStatus.PENDING
        );
        reservationMap.put(reservation.id(), updatedReservation);
        return updatedReservation;
    }

    public void deleteReservation(Long id) {
        if (!reservationMap.containsKey(id)) {
            throw new NoSuchElementException("Reservation with id " + id + " not found");
        }
        reservationMap.remove(id);
    }

    public Reservation approveReservation(Long id) {
        if (!reservationMap.containsKey(id)) {
            throw new NoSuchElementException("Reservation with id " + id + " not found");
        }
        var reservation = reservationMap.get(id);
        if (reservation.status() != ReservationStatus.PENDING) {
            throw new IllegalArgumentException("Cannot approve status should be PENDING");
        }
        boolean isConflict = isReservationConflict(reservation);
        if (isConflict) {
            throw new IllegalArgumentException("Cannot approve reservation because its conflict");
        }
        var approvedReservation = new Reservation(
                reservation.id(),
                reservation.userId(),
                reservation.roomId(),
                reservation.startDate(),
                reservation.endDate(),
                ReservationStatus.APPROVED
        );
        reservationMap.put(approvedReservation.id(), approvedReservation);
        return approvedReservation;
    }

    private boolean isReservationConflict(Reservation reservation) {
        for (Reservation existingReservation : reservationMap.values()) {
            if (reservation.id().equals(existingReservation.id())) {
                continue;
            }
            if (!(reservation.roomId().equals(existingReservation.roomId()))) {
                continue;
            }
            if (reservation.startDate().isBefore(existingReservation.endDate()) && existingReservation.startDate().isBefore(reservation.endDate())) {
                return true;
            }
        }
        return false;
    }
}
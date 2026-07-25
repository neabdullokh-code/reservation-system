package school.sorokin.reservation_system;

import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.concurrent.atomic.AtomicLong;

@Service 
public class ReservationService {

    private final Map<Long, Reservation> reservationMap;
    private AtomicLong idCounter;

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
        return null;
    }

    public void deleteReservation(Long id) {
        if (!reservationMap.containsKey(id)) {
            throw new NoSuchElementException("Reservation with id " + id + " not found");
        }
        reservationMap.remove(id);
    }
}
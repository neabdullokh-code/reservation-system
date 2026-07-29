package school.sorokin.reservation_system;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service 
public class ReservationService {
    private final ReservationRepository repository;

    public ReservationService(ReservationRepository repository) {
        this.repository = repository;
    }

    public Reservation getReservationById(Long id) {
        ReservationEntity reservationEntity = repository.findById(id).orElseThrow(()->new EntityNotFoundException("Reservation with id " + id + " not found"));

        return toDomainReservation(reservationEntity);
    }

    public List<Reservation> findAllReservations() {

        List<ReservationEntity> allEntities = repository.findAll();

        List<Reservation> reservationsList = allEntities.stream().
                map(it -> toDomainReservation(it)).toList();
        return reservationsList;
    }

    public Reservation createReservation(Reservation reservationToCreate) {
        if (reservationToCreate.id() != null) {
            throw new IllegalArgumentException("Id should be null");
        }
        if (!(reservationToCreate.status() == null)){
            throw new IllegalArgumentException("Status should be null");
        }
        var entityToSave = new ReservationEntity(
                null,
                reservationToCreate.userId(),
                reservationToCreate.roomId(),
                reservationToCreate.startDate(),
                reservationToCreate.endDate(),
                ReservationStatus.PENDING
            );
        var savedEntity = repository.save(entityToSave);
        return toDomainReservation(savedEntity);
    }

    public Reservation updateReservation(Long id, Reservation reservationToUpdate) {
        var reservationEntity = repository.findById(id).orElseThrow(()->new EntityNotFoundException("Reservation with id " + id + " not found"));
        if (reservationEntity.getStatus() != ReservationStatus.PENDING) {
            throw new IllegalArgumentException("Status should be PENDING");
        }
        var reservationToSave = new ReservationEntity(
                reservationEntity.getId(),
                reservationToUpdate.userId(),
                reservationToUpdate.roomId(),
                reservationToUpdate.startDate(),
                reservationToUpdate.endDate(),
                ReservationStatus.PENDING
        );
        var updatedEntity = repository.save(reservationToSave);
        return toDomainReservation(updatedEntity);
    }

    public void deleteReservation(Long id) {
        if (!repository.existsById(id)) {
            throw new EntityNotFoundException("Reservation with id " + id + " not found");
        }
        repository.deleteById(id);
    }

    public Reservation approveReservation(Long id) {
        var reservationEntity = repository.findById(id).orElseThrow(()->new EntityNotFoundException("Reservation with id " + id + " not found"));

        if (reservationEntity.getStatus() != ReservationStatus.PENDING) {
            throw new IllegalArgumentException("Cannot approve status should be PENDING");
        }
        boolean isConflict = isReservationConflict(reservationEntity);
        if (isConflict) {
            throw new IllegalArgumentException("Cannot approve reservation because its conflict");
        }
        reservationEntity.setStatus(ReservationStatus.APPROVED);
        repository.save(reservationEntity);
        return toDomainReservation(reservationEntity);
    }

    private boolean isReservationConflict(ReservationEntity reservation) {
        var allReservations =repository.findAll();
        for (ReservationEntity existingReservation : allReservations) {
            if (reservation.getId().equals(existingReservation.getId())) {
                continue;
            }
            if (!(reservation.getRoomId().equals(existingReservation.getRoomId()))) {
                continue;
            }
            if (reservation.getStartDate().isBefore(existingReservation.getEndDate()) && existingReservation.getStartDate().isBefore(reservation.getEndDate())) {
                return true;
            }
        }
        return false;
    }
    private Reservation toDomainReservation(ReservationEntity reservationEntity) {
        return new Reservation(
                reservationEntity.getId(),
                reservationEntity.getUserId(),
                reservationEntity.getRoomId(),
                reservationEntity.getStartDate(),
                reservationEntity.getEndDate(),
                reservationEntity.getStatus()
        );
    }
}
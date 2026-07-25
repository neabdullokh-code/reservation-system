package school.sorokin.reservation_system;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/reservation")
public class ReservationController {
    private static final Logger log = LoggerFactory.getLogger(ReservationController.class);

    private final ReservationService reservationService;

    @Autowired
    public ReservationController(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<Reservation> getReservationById(@PathVariable Long id) {
        log.info("Getting reservation with id {}", id);
        try{
            return ResponseEntity.status(HttpStatus.OK)
                    .body(reservationService.getReservationById(id));
        }catch (NoSuchElementException e){
            log.info("Reservation with id {} not found", id);
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping
    public ResponseEntity<List<Reservation>> getAllReservations() {
        log.info("Getting all reservations");
        return ResponseEntity.ok(reservationService.findAllReservations());
    }

    @PostMapping
    public ResponseEntity<Reservation> createReservation(@RequestBody Reservation reservationToCreate) {
        log.info("Creating reservation {}", reservationToCreate);
        return ResponseEntity.status(HttpStatus.CREATED)  //можно просто 201
                .header("testheader","123")
                .body(reservationService.createReservation(reservationToCreate));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Reservation> updateReservation(
            @PathVariable Long id,
            @RequestBody Reservation reservationToUpdate) {
        log.info("Updating reservation {}", reservationToUpdate);
        var updated = reservationService.updateReservation(id,reservationToUpdate);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReservation(@PathVariable Long id) {
        log.info("Deleting reservation {}", id);
        try{reservationService.deleteReservation(id);
            return ResponseEntity.noContent().build();
        }catch (NoSuchElementException e){
            return ResponseEntity.notFound().build();
        }
    }

}

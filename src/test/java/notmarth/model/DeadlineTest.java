package notmarth.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

/** Tests deadline date parsing, date matching, storage values, and display text. */
class DeadlineTest {
    @Test
    void dateOnlyDeadline_preservesDateOnlyFormatting() {
        Deadline deadline = new Deadline("submit report", "2019-10-15");

        assertEquals(LocalDateTime.of(2019, 10, 15, 0, 0), deadline.getBy());
        assertEquals("2019-10-15", deadline.getByForStorage());
        assertEquals("[D][ ] submit report (by: Oct 15 2019)", deadline.toString());
    }

    @Test
    void timedDeadline_matchesOnlyItsCalendarDate() {
        Deadline deadline = new Deadline("return book", "2/12/2019 1800");

        assertTrue(deadline.isDueOn(LocalDate.of(2019, 12, 2)));
        assertFalse(deadline.isDueOn(LocalDate.of(2019, 12, 3)));
        assertEquals("2019-12-02T18:00", deadline.getByForStorage());
        assertEquals("[D][ ] return book (by: Dec 02 2019 6:00 PM)", deadline.toString());
    }

    @Test
    void localDateTimeDeadline_acceptsTypedValue() {
        Deadline deadline = new Deadline("meeting", LocalDateTime.of(2019, 10, 15, 14, 30));

        assertEquals(LocalDateTime.of(2019, 10, 15, 14, 30), deadline.getBy());
        assertEquals("2019-10-15T14:30", deadline.getByForStorage());
    }
}

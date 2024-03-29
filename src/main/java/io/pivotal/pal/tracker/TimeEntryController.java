package io.pivotal.pal.tracker;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.DistributionSummary;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/time-entries")
public class TimeEntryController {

    private TimeEntryRepository timeEntryRepository;
    private final DistributionSummary timeEntrySummary;
    private final Counter actionCounter;


    public TimeEntryController(TimeEntryRepository timeEntryRepository, MeterRegistry meterRegistry) {
        this.timeEntryRepository = timeEntryRepository;
        timeEntrySummary = meterRegistry.summary("timeEntry.summary");
        actionCounter = meterRegistry.counter("timeEntry.actionCounter");
    }

    @PostMapping()
    public ResponseEntity<TimeEntry> create(@RequestBody TimeEntry timeEntryToCreate) {
        TimeEntry res = timeEntryRepository.create(timeEntryToCreate);
        actionCounter.increment();
        timeEntrySummary.record(timeEntryRepository.list().size());

        return new ResponseEntity<>(res, HttpStatus.CREATED);

    }

    @GetMapping("/{id}")
    public ResponseEntity<TimeEntry> read(@PathVariable Long id) {
        TimeEntry res = timeEntryRepository.find(id);

        if (res!=null){
            actionCounter.increment();
            return new ResponseEntity<>(res, HttpStatus.OK);

        } else {
        return new ResponseEntity<>(res,HttpStatus.NOT_FOUND);
        }

    }

    @GetMapping
    public ResponseEntity<List<TimeEntry>> list() {
        List<TimeEntry> res = timeEntryRepository.list();
        actionCounter.increment();

        return new ResponseEntity<>(res, HttpStatus.OK);

    }

    @PutMapping("/{id}")
    public ResponseEntity<TimeEntry> update(@PathVariable Long id, @RequestBody TimeEntry timeEntry) {
        TimeEntry res = timeEntryRepository.update(id, timeEntry);
        if (res!=null){
            actionCounter.increment();
            return new ResponseEntity<>(res, HttpStatus.OK);

        }else {
            return new ResponseEntity<>(res, HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity delete(@PathVariable Long id) {
        timeEntryRepository.delete(id);
        actionCounter.increment();
        timeEntrySummary.record(timeEntryRepository.list().size());
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}

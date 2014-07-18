package wfx8.model;

import java.time.LocalTime;
import java.time.ZonedDateTime;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

public final class WorkingDay {

    private ObjectProperty<ZonedDateTime> startTime;
    private ObjectProperty<LocalTime>     endTime;

    public WorkingDay(ZonedDateTime startTime, LocalTime endTime) {
        this.startTime = new SimpleObjectProperty<ZonedDateTime>(startTime);
        this.endTime = new SimpleObjectProperty<LocalTime>(endTime);
    }

    public ZonedDateTime getStartTime() {
        return startTime.get();
    }

    public void setStartTime(ZonedDateTime startTime) {
        this.startTime.set(startTime);
    }

    public ObjectProperty<ZonedDateTime> startTimeProperty() {
        return this.startTime;
    }

    public LocalTime getEndTime() {
        return endTime.get();
    }

    public void setEndTime(LocalTime endTime) {
        this.endTime.set(endTime);
    }

    public ObjectProperty<LocalTime> endTimeProperty() {
        return this.endTime;
    }

}

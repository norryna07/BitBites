package main.java.services;

import main.java.models.DailyScheduleModel;
import main.java.repositories.DailySchedulesRepository;

public class DailyScheduleService extends BitBitesService<DailyScheduleModel>{

    public DailyScheduleService() {
        repository = DailySchedulesRepository.getInstance();
    }


}

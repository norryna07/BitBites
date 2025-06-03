package com.bitbites.bitbites2.backend.services;

import com.bitbites.bitbites2.backend.models.DailyScheduleModel;
import com.bitbites.bitbites2.backend.repositories.DailySchedulesRepository;

public class DailyScheduleService extends BitBitesService<DailyScheduleModel>{

    public DailyScheduleService() {
        repository = DailySchedulesRepository.getInstance();
    }


}

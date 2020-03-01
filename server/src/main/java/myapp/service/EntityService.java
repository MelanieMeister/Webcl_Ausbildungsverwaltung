package myapp.service;

import java.util.List;

import org.opendolphin.core.server.DTO;

public interface EntityService {
    DTO loadDetails(long id);

    List<DTO> loadAllMasters();

    void update(List<DTO> modified, List<Long> created, List<Long> deleted);
}

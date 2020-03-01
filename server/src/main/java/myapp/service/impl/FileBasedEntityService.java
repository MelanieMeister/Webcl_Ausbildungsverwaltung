package myapp.service.impl;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.opendolphin.core.server.DTO;

import myapp.presentationmodel.PMDescription;
import myapp.service.EntityService;
import myapp.util.AttributeDescription;
import myapp.util.DTOMixin;

public class FileBasedEntityService implements EntityService , DTOMixin {

    private static final String DELIMITER = ";";

    private final PMDescription detailPMDescription;
    private final PMDescription masterPMDescription;
    private final String        dataFileName;

    public FileBasedEntityService(PMDescription masterPMDescription, PMDescription detailPMDescription) {
        this.masterPMDescription = masterPMDescription;
        this.detailPMDescription = detailPMDescription;
        this.dataFileName = "/data/"+detailPMDescription.entityName() +".csv";
    }

    @Override
    public DTO loadDetails(long id) {
        List<String> propertyNames = getPropertyNamesFromFile();

        try (Stream<String> streamOfLines = getStreamOfLines()) {
            return streamOfLines.skip(1)
                                .map(line -> line.split(DELIMITER, propertyNames.size()))
                                .filter(strings -> Long.valueOf(strings[0]) == id)
                                .map(strings -> createDTO(detailPMDescription, strings, id, propertyNames))
                                .findAny()
                                .orElseThrow(IllegalArgumentException::new);
        }
    }

    @Override
    public List<DTO> loadAllMasters() {
        return loadAll(masterPMDescription);
    }

    @Override
    public void update(List<DTO> modified, List<Long> created, List<Long> deleted) {
        if(modified.isEmpty() && created.isEmpty() && deleted.isEmpty()){
            return;
        }
        List<DTO> dtosToSave   = new ArrayList<>();
        List<DTO> dtosFromFile = loadAll(detailPMDescription);

        for(DTO persistedDTO : dtosFromFile){
            long id = entityId(persistedDTO);
            if(deleted.contains(id)){
                continue;
            }
            DTO modifiedDTO = getDTO(id, modified);
            if(modifiedDTO != null){
                persistedDTO = merge(modifiedDTO, persistedDTO);

            }
            dtosToSave.add(persistedDTO);
        }

        for(Long id : created){
            DTO createdDTO = getDTO(id, modified);
            DTO emptyDTO = createDTO(detailPMDescription, id);
            dtosToSave.add(merge(createdDTO, emptyDTO));
        }

        try {
            List<String> headline = Collections.singletonList(Arrays.stream(detailPMDescription.getAttributeDescriptions())
                                                                    .map(AttributeDescription::name)
                                                                    .collect(Collectors.joining(";")));
            List<String> dataLines = dtosToSave.stream()
                                         .map(dto -> dto.getSlots().stream()
                                                        .map(slot -> slot.getValue() == null ? "" : String.valueOf(slot.getValue())
                                                                                                          .replaceAll("\\n", "<br>"))
                                                        .collect(Collectors.joining(DELIMITER)))
                                         .collect(Collectors.toList());

            Path path = getTempFile().toPath();
            Files.write(path,
                        headline,
                        StandardOpenOption.TRUNCATE_EXISTING);
            Files.write(path,
                        dataLines,
                        StandardOpenOption.APPEND);
        } catch (IOException e) {
            throw new IllegalStateException("save failed");
        }
    }


    private List<DTO> loadAll(PMDescription pmDescription) {
        List<String> propertyNames = getPropertyNamesFromFile();

        try (Stream<String> streamOfLines = getStreamOfLines()) {
            return streamOfLines.skip(1)
                                .map(s -> s.split(DELIMITER, propertyNames.size()))
                                .map(strings -> createDTO(pmDescription, strings, Long.parseLong(strings[0]), propertyNames))
                                .collect(Collectors.toList());
        }
    }

    private DTO createDTO(PMDescription pmDescription, String[] data, long id, List<String> propertyNames) {
        return new DTO(Arrays.stream(pmDescription.getAttributeDescriptions())
                     .map(att -> createSlot(att, convert(att, data, propertyNames.indexOf(att.name())), id))
                     .collect(Collectors.toList()));
    }

    private Object convert(AttributeDescription attributeDescription, String[] data, int dataIndex) {
        return dataIndex == -1 ?
               attributeDescription.getInitialValue() :
               attributeDescription.convertToValue(data[dataIndex].replaceAll("<br>", "\n"));
    }

    private List<String> getPropertyNamesFromFile() {
        List<String> propertyNames;
        try (Stream<String> streamOfLines = getStreamOfLines()) {
            propertyNames = Arrays.asList(streamOfLines.limit(1)
                                                       .map(s -> s.split(DELIMITER))
                                                       .findAny()
                                                       .orElseThrow(IllegalStateException::new));
        }
        return propertyNames;
    }

    private File getTempFile() {
        return new File(System.getProperty("java.io.tmpdir"), dataFileName);
    }

    private Stream<String> getStreamOfLines() {
        copyDataToTempDirIfNecessary();
        try {
            return Files.lines(getTempFile().toPath(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new IllegalStateException("tmp file not found");
        }
    }

    private void copyDataToTempDirIfNecessary() {
        File tmpFile = getTempFile();
        if (tmpFile.exists()) {
            return;
        }

        tmpFile.getParentFile().mkdirs();

        try {
            InputStream resource = getClass().getResourceAsStream(dataFileName);
            if(resource != null){
                Files.copy(resource, tmpFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            }
            else {
                tmpFile.createNewFile();
                update(new ArrayList<>(), new ArrayList<>(), new ArrayList<>());
            }
        } catch (IOException e) {
            throw new IllegalStateException("copy file to tmpdir failed");
        }
    }
}

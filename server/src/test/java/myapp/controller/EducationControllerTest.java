package myapp.controller;

import java.util.ArrayList;
import java.util.List;

import myapp.presentationmodel.education.EducationAtt;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.opendolphin.core.server.DTO;
import org.opendolphin.core.server.DefaultServerDolphin;
import org.opendolphin.core.server.EventBus;
import org.opendolphin.core.server.ServerAttribute;
import org.opendolphin.core.server.ServerConnector;
import org.opendolphin.core.server.ServerModelStore;
import org.opendolphin.core.server.ServerPresentationModel;

import myapp.presentationmodel.PMDescription;
import myapp.service.EducationService;
import myapp.service.TranslationService;
import myapp.util.DTOMixin;
import myapp.util.Language;

import static org.junit.jupiter.api.Assertions.*;

class EducationControllerTest {
    private TranslationService translationService = new TranslationService() {
        @Override
        public String translate(String pmType, String attributeName, Language language) {
            return attributeName;
        }

        @Override
        public String translateCommand(String command, Language language) {
            return command;
        }
    };

    private EducationController controller;

    private EducationServiceStub service;
    private EducationServiceStub msgService;
    private EducationServiceStub faqService;

    @BeforeEach
    void setup() {
        service = new EducationServiceStub();
        controller = new EducationController(service, translationService, new EventBus());


        ServerModelStore     serverModelStore = new TestModelStore();
        DefaultServerDolphin serverDolphin = new DefaultServerDolphin(serverModelStore, new ServerConnector());

        ApplicationStateController applicationStateController = new ApplicationStateController(translationService);
        applicationStateController.setServerDolphin(serverDolphin);
        applicationStateController.initializeBasePMs();
        applicationStateController.initializeController();

        controller.setServerDolphin(serverDolphin);
    }

    @Test
    void testInitializeBasePMs(){
        //given

        //when
        controller.initializeBasePMs();
        ServerPresentationModel p = controller.getDetailProxyPM();

        //then
        assertNotNull(p);
        assertFalse(p.isDirty());
    }

    @Test
    void testDirtyState(){
        //given
        controller.initializeBasePMs();
        ServerPresentationModel p = controller.getDetailProxyPM();
        ServerAttribute nameAttribute = p.getAt(EducationAtt.NAME.name());

        //when
        nameAttribute.setValue("some new value");

        //then
        assertTrue(p.isDirty());
        assertTrue(nameAttribute.isDirty());
    }


    @Test
    void testSave(){
        //given
        controller.initializeBasePMs();
        controller.initializeController();
        controller.loadAll();

        ServerPresentationModel p = controller.getDetailProxyPM();
        p.getAt(EducationAtt.NAME.name()).setValue("abc");

        //when
        controller.saveOnUserRequest();

        //then
        assertEquals(1, service.saveCounter);
        assertFalse(p.isDirty());
    }

    private class EducationServiceStub implements EducationService, DTOMixin {
        int saveCounter;

        @Override
        public DTO loadDetails(long id) {
            return createDTO(PMDescription.EDUCATION, id);
        }

        @Override
        public List<DTO> loadAllMasters() {
            ArrayList<DTO> dtos = new ArrayList<>();
            dtos.add(createDTO(PMDescription.EDUCATION_MASTER, 1));
            return dtos;
        }

        @Override
        public void update(List<DTO> modified, List<Long> created, List<Long> deleted) {
            saveCounter++;
        }
    }

    private class TestModelStore extends ServerModelStore{
        TestModelStore(){
            setCurrentResponse(new ArrayList<>());
        }
    }


}
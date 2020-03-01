package myapp.controller;

import java.util.Arrays;

import org.opendolphin.core.Attribute;
import org.opendolphin.core.Tag;
import org.opendolphin.core.server.ServerAttribute;
import org.opendolphin.core.server.ServerPresentationModel;

import myapp.presentationmodel.PMDescription;
import myapp.presentationmodel.applicationstate.ApplicationStateAtt;
import myapp.service.TranslationService;
import myapp.util.Controller;
import myapp.util.Language;

import static myapp.presentationmodel.PMDescription.EMPTY_SELECTION_ID;


class ApplicationStateController extends Controller {

    private ServerPresentationModel applicationStatePM;

    ApplicationStateController(TranslationService translationService) {
        super(translationService);
    }

    @Override
    protected void initializeBasePMs() {
        applicationStatePM = createSinglePM(PMDescription.APPLICATION_STATE);

        Arrays.stream(PMDescription.values())
              .filter(pmDescription -> !pmDescription.equals(PMDescription.APPLICATION_STATE))
              .map(PMDescription::selectedIdPropertyName)
              .distinct()
              .map(name -> new ServerAttribute(name, EMPTY_SELECTION_ID, name, Tag.VALUE))
              .forEach(applicationStatePM::addAttribute);
    }

    @Override
    protected void specifyAllCommandSubscriptions() {
        //no specific commands needed
    }

    @Override
    protected void setDefaultValues() {
        applicationStatePM.getAt(ApplicationStateAtt.LANGUAGE.name()).setValue(Language.GERMAN.name());
        applicationStatePM.getAt(ApplicationStateAtt.CLEAN_DATA.name()).setValue(true);
    }

    @Override
    protected void setupValueChangedListener() {
        applicationStatePM.getAt(ApplicationStateAtt.LANGUAGE.name()).addPropertyChangeListener(Attribute.VALUE,
                                                                                                evt -> translate(applicationStatePM, Language.valueOf((String) evt.getNewValue())));
    }}
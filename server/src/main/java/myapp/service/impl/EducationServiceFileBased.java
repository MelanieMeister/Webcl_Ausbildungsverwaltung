package myapp.service.impl;

import myapp.presentationmodel.PMDescription;
import myapp.service.EducationService;


public class EducationServiceFileBased extends FileBasedEntityService implements EducationService {
    public EducationServiceFileBased() {
        super(PMDescription.EDUCATION_MASTER, PMDescription.EDUCATION);
    }
}

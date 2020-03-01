package myapp.presentationmodel.education;

import myapp.presentationmodel.PMDescription;
import myapp.util.AttributeDescription;
import myapp.util.Definition;
import myapp.util.validators.IntegerRangeValidator;
import myapp.util.validators.RegexValidator;
import myapp.util.validators.StringLengthValidator;

public enum EducationAtt implements AttributeDescription {
    ENTITY_ID(Definition.ID()),

    NAME(Definition.STRING()
                   .mandatory(true)
                   .valueValidators(StringLengthValidator.between(2, 42, "NAME_OUT_OF_RANGE"))),

    AGE(Definition.INT()
                  .syntaxValidator(RegexValidator.forPositiveInt("NOT_AN_AGE"))
                  .valueValidators(IntegerRangeValidator.between(0, 120, "AGE_OUT_OF_RANGE"))
                  .formatPattern("%d")),

    IS_ADULT(Definition.BOOLEAN()
                       .initialValue(false)),
    PERSNR(Definition.STRING()
            .mandatory(true)),
    FIRSTNAME(Definition.STRING()
            .mandatory(true)),
    ORGANISATION(Definition.STRING()
            .mandatory(true)),
    ACTIVITYLEVEL(Definition.INT()),
    EDUCATIONNAME(Definition.STRING()
            .mandatory(true)),
    ORGANISER(Definition.STRING()
            .mandatory(true)),
    SUBMITTEDDATE(Definition.STRING()
            .mandatory(true)),
    COSTEDUCATION(Definition.DOUBLE()),
    COSTPARTICIPATION(Definition.DOUBLE()),
    CHARGEEDUCATION(Definition.DOUBLE()),
    CHARGEPARTICIPATION(Definition.DOUBLE()),
    COSTTOTALEDUCATION(Definition.DOUBLE()),
    COSTTOTALPARTICIPATION(Definition.DOUBLE()),
    DURATIONEDUCATION(Definition.DOUBLE()),
    DURATIONPARTICIPATION(Definition.DOUBLE()),
    PROCENTIMEPARTICIPATION(Definition.DOUBLE()),
    PROCENTTOTALCOSTPARTICIPATION(Definition.DOUBLE()),
    PROCENTCHARGEPARTICIPATION(Definition.DOUBLE()),
    PROCENTCOSTPARTICIPATION(Definition.DOUBLE()),
    NEEDREVERS(Definition.BOOLEAN()),
    STARTEDUCATION(Definition.STRING()
            .mandatory(true)),
    FINISTHEDUCATION(Definition.STRING()
            .mandatory(true)),
    SENDREVERSTOEMPLOYEE(Definition.BOOLEAN()
                       .initialValue(false)),
    TRANSFERMEETING(Definition.BOOLEAN()
                       .initialValue(false)),
    SENDREVERSTOPA(Definition.BOOLEAN()
            .initialValue(false)),
    SENDINFOTOTV(Definition.BOOLEAN()
            .initialValue(false)),
    INFOFINISHEDUCATION(Definition.BOOLEAN()
            .initialValue(false)),
    INFOFINISHEDUCATIONWITHOUTREVERS(Definition.BOOLEAN()
            .initialValue(false)),
  //  SENDREVERSTOEMPLOYEE(Definition.STRING()
  //          .mandatory(true)),
  //  SENDREVERSTOPA(Definition.STRING()
  //          .mandatory(true)),
  //  SENDINFOTOTV(Definition.STRING()
  //          .mandatory(true)),
    PAYMENTDATE(Definition.STRING()),
    PAYMENTAMOUNT(Definition.STRING()),
    ACTUALPAYAMOUNT(Definition.DOUBLE()),
   // TRANSFERMEETING(Definition.STRING()
   //         .mandatory(true)),
    NOTES(Definition.STRING()
            .mandatory(true)),
    STATUS(Definition.STRING()
            .mandatory(true)),
    STATESUPPORT(Definition.BOOLEAN());


    private final Definition definition;

    EducationAtt(Definition definition) {
        this.definition = definition;
    }

    @Override
    public Definition getDefinition() {
        return definition;
    }

    @Override
    public PMDescription getPMDescription() {
        return PMDescription.EDUCATION;
    }

}

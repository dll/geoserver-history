/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package net.opengis.wcs.validation;

import net.opengis.wcs.ContactType;

/**
 * A sample validator interface for {@link net.opengis.wcs.ResponsiblePartyType}.
 * This doesn't really do anything, and it's not a real EMF artifact.
 * It was generated by the org.eclipse.emf.examples.generator.validator plug-in to illustrate how EMF's code generator can be extended.
 * This can be disabled with -vmargs -Dorg.eclipse.emf.examples.generator.validator=false.
 */
public interface ResponsiblePartyTypeValidator {
    boolean validate();

    boolean validateIndividualName(String value);
    boolean validateOrganisationName(String value);
    boolean validateOrganisationName1(String value);
    boolean validatePositionName(String value);
    boolean validateContactInfo(ContactType value);
}

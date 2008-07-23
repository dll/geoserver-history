/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package net.opengis.wcs.validation;

import net.opengis.wcs.SpatialDomainType;
import net.opengis.wcs.TimeSequenceType;

/**
 * A sample validator interface for {@link net.opengis.wcs.DomainSetType}.
 * This doesn't really do anything, and it's not a real EMF artifact.
 * It was generated by the org.eclipse.emf.examples.generator.validator plug-in to illustrate how EMF's code generator can be extended.
 * This can be disabled with -vmargs -Dorg.eclipse.emf.examples.generator.validator=false.
 */
public interface DomainSetTypeValidator {
    boolean validate();

    boolean validateSpatialDomain(SpatialDomainType value);
    boolean validateTemporalDomain(TimeSequenceType value);
    boolean validateTemporalDomain1(TimeSequenceType value);
}

/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package net.opengis.gml.validation;

import java.util.List;

/**
 * A sample validator interface for {@link net.opengis.gml.CodeListType}.
 * This doesn't really do anything, and it's not a real EMF artifact.
 * It was generated by the org.eclipse.emf.examples.generator.validator plug-in to illustrate how EMF's code generator can be extended.
 * This can be disabled with -vmargs -Dorg.eclipse.emf.examples.generator.validator=false.
 */
public interface CodeListTypeValidator {
    boolean validate();

    boolean validateValue(List value);
    boolean validateCodeSpace(String value);
}

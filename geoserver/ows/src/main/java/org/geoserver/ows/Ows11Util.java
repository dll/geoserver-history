/* Copyright (c) 2001 - 2007 TOPP - www.openplans.org.  All rights reserved.
 * This code is licensed under the GPL 2.0 license, availible at the root
 * application directory.
 */
package org.geoserver.ows;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.List;
import java.util.Locale;

import org.geoserver.ows.util.OwsUtils;
import org.geoserver.platform.ServiceException;
import org.opengis.util.InternationalString;

import net.opengis.ows11.CodeType;
import net.opengis.ows11.DomainMetadataType;
import net.opengis.ows11.ExceptionReportType;
import net.opengis.ows11.ExceptionType;
import net.opengis.ows11.KeywordsType;
import net.opengis.ows11.LanguageStringType;
import net.opengis.ows11.Ows11Factory;

public class Ows11Util {

    static Ows11Factory f = Ows11Factory.eINSTANCE;

    public static LanguageStringType languageString( InternationalString value ) {
        return languageString( value.toString( Locale.getDefault() ) );
    }
    
    public static LanguageStringType languageString( String value ) {
        LanguageStringType ls = f.createLanguageStringType();
        ls.setValue( value );
        return ls;
    }
    
    public static KeywordsType keywords( List<String> keywords) {
        KeywordsType kw = f.createKeywordsType();
        for ( String keyword : keywords ) {
            kw.getKeyword().add( languageString( keyword ) );
        }
        return kw;
    }
    
    public static CodeType code( String value ) {
        CodeType code = f.createCodeType();
        code.setValue( value );
        
        return code;
    }
    
    public static CodeType code( CodeType value ) {
        return code( value.getValue() );
    }
    
    public static DomainMetadataType type( String name ) {
        DomainMetadataType type = f.createDomainMetadataType();
        type.setValue( name );
        
        return type;
    }

    public static ExceptionReportType exceptionReport(
            ServiceException exception, boolean verboseExceptions) {
        
        ExceptionType e = f.createExceptionType();

        if (exception.getCode() != null) {
            e.setExceptionCode(exception.getCode());
        } else {
            //set a default
            e.setExceptionCode("NoApplicableCode");
        }

        e.setLocator(exception.getLocator());

        //add the message
        StringBuffer sb = new StringBuffer();
        OwsUtils.dumpExceptionMessages(exception, sb, true);
        e.getExceptionText().add(sb.toString());
        e.getExceptionText().addAll(exception.getExceptionText());

        if(verboseExceptions) {
            //add the entire stack trace
            //exception.
            e.getExceptionText().add("Details:");
            ByteArrayOutputStream trace = new ByteArrayOutputStream();
            exception.printStackTrace(new PrintStream(trace));
            e.getExceptionText().add(new String(trace.toByteArray()));
        }

        ExceptionReportType report = f.createExceptionReportType();
        report.setVersion("1.1.0");
        report.getException().add(e);
        
        return report;
    }
}

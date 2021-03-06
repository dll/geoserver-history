/* Copyright (c) 2001 - 2007 TOPP - www.openplans.org. All rights reserved.
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.web.data.resource;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.apache.wicket.WicketRuntimeException;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.markup.html.form.FormComponentPanel;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.geoserver.catalog.CoverageInfo;
import org.geoserver.catalog.DimensionInfo;
import org.geoserver.catalog.DimensionPresentation;
import org.geoserver.catalog.FeatureTypeInfo;
import org.geoserver.catalog.ResourceInfo;
import org.geoserver.catalog.impl.DimensionInfoImpl;
import org.geoserver.web.wicket.ParamResourceModel;
import org.geotools.coverage.grid.io.AbstractGridCoverage2DReader;
import org.opengis.coverage.grid.GridCoverageReader;
import org.opengis.feature.type.PropertyDescriptor;

/**
 * Edits a {@link DimensionInfo} object for the specified resource
 * 
 * @author Andrea Aime - GeoSolutions
 */
@SuppressWarnings("serial")
public class DimensionEditor extends FormComponentPanel<DimensionInfo> {

    List<DimensionPresentation> PRESENTATION_MODES = Arrays.asList(DimensionPresentation.values());

    private CheckBox enabled;

    private DropDownChoice<String> attribute;

    private DropDownChoice<DimensionPresentation> presentation;

    private PeriodEditor resTime;

    private TextField<BigDecimal> resElevation;
    
    boolean time;

    public DimensionEditor(String id, IModel<DimensionInfo> model, ResourceInfo resource, Class type) {
        super(id, model);

        // double container dance to get stuff to show up and hide on demand (grrr)
        final WebMarkupContainer configsContainer = new WebMarkupContainer("configContainer");
        configsContainer.setOutputMarkupId(true);
        add(configsContainer);
        final WebMarkupContainer configs = new WebMarkupContainer("configs");
        configs.setOutputMarkupId(true);
        configs.setVisible(getModelObject().isEnabled());
        configsContainer.add(configs);

        // enabled flag, and show the rest only if enabled is true
        final PropertyModel<Boolean> enabledModel = new PropertyModel<Boolean>(model, "enabled");
        enabled = new CheckBox("enabled", enabledModel);
        add(enabled);
        enabled.add(new AjaxFormComponentUpdatingBehavior("onclick") {

            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                Boolean visile = enabled.getModelObject();

                configs.setVisible(visile);
                target.addComponent(configsContainer);
            }

        });

        // error message label
        Label noAttributeMessage = new Label("noAttributeMsg", "");
        add(noAttributeMessage);
        
        // the attribute label and dropdown container
        WebMarkupContainer attContainer = new WebMarkupContainer("attributeContainer");
        configs.add(attContainer);

        // check the attributes and show a dropdown
        List<String> attributes = getAttributesOfType(resource, type);
        attribute = new DropDownChoice<String>("attribute", new PropertyModel<String>(model,
		        "attribute"), attributes);
		attribute.setOutputMarkupId(true);
		attribute.setRequired(true);
		attContainer.add(attribute);

		// do we show it?
        if(resource instanceof FeatureTypeInfo) { 
	        if (attributes.isEmpty()) {
	            disableDimension(type, configs, noAttributeMessage);
	        } else {
	            noAttributeMessage.setVisible(false);
	        }
        } else if(resource instanceof CoverageInfo) {
        	attContainer.setVisible(false);
        	attribute.setRequired(false);
        	try {
        		GridCoverageReader reader = ((CoverageInfo) resource).getGridCoverageReader(null, null);
        		if(Number.class.isAssignableFrom(type)) {
        			String elev = reader.getMetadataValue(AbstractGridCoverage2DReader.HAS_ELEVATION_DOMAIN);
        			if(!Boolean.parseBoolean(elev)) {
        				disableDimension(type, configs, noAttributeMessage);
        			}
        		} else if(Date.class.isAssignableFrom(type)) {
        			String time = reader.getMetadataValue(AbstractGridCoverage2DReader.HAS_TIME_DOMAIN);
        			if(!Boolean.parseBoolean(time)) {
        				disableDimension(type, configs, noAttributeMessage);
        			}
        		}
        	} catch(IOException e) {
        		throw new WicketRuntimeException(e);
        	}
        }

        // presentation/resolution block
        final WebMarkupContainer resContainer = new WebMarkupContainer("resolutionContainer");
        resContainer.setOutputMarkupId(true);
        configs.add(resContainer);
        final WebMarkupContainer resolutions = new WebMarkupContainer("resolutions");
        resolutions
                .setVisible(model.getObject().getPresentation() == DimensionPresentation.DISCRETE_INTERVAL);
        resolutions.setOutputMarkupId(true);
        resContainer.add(resolutions);

        presentation = new DropDownChoice<DimensionPresentation>("presentation",
                new PropertyModel<DimensionPresentation>(model, "presentation"),
                PRESENTATION_MODES, new PresentationModeRenderer());
        configs.add(presentation);
        presentation.setRequired(true);
        presentation.add(new AjaxFormComponentUpdatingBehavior("onchange") {

            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                boolean visible = presentation.getModelObject() == DimensionPresentation.DISCRETE_INTERVAL;
                resolutions.setVisible(visible);
                target.addComponent(resContainer);
            }

        });

        IModel<BigDecimal> rmodel = new PropertyModel<BigDecimal>(model, "resolution");
        resTime = new PeriodEditor("resTime", rmodel);
        resolutions.add(resTime);
        resElevation = new TextField<BigDecimal>("resElevation", rmodel);
        resolutions.add(resElevation);
        time = Date.class.isAssignableFrom(type);
        if(time) {
            resElevation.setVisible(false);
            resTime.setRequired(true);
        } else {
            resTime.setVisible(false);
            resElevation.setRequired(true);
        }
    }

	private void disableDimension(Class type, final WebMarkupContainer configs,
			Label noAttributeMessage) {
		// no attributes of the required type, no party
		enabled.setEnabled(false);
		enabled.setModelObject(false);
		configs.setVisible(false);
		ParamResourceModel typeName = new ParamResourceModel("AttributeType."
		        + type.getSimpleName(), null);
		ParamResourceModel error = new ParamResourceModel("missingAttribute", this, typeName
		        .getString());
		noAttributeMessage.setDefaultModelObject(error.getString());
	}

    @Override
    public boolean processChildren() {
        return true;
    }

    protected void convertInput() {
        if (!enabled.getModelObject()) {
            setConvertedInput(new DimensionInfoImpl());
        } else {
            DimensionInfoImpl info = new DimensionInfoImpl();
            info.setEnabled(true);
            attribute.processInput();
            info.setAttribute(attribute.getModelObject());
            info.setPresentation(presentation.getModelObject());
            if (info.getPresentation() == DimensionPresentation.DISCRETE_INTERVAL) {
                if(time) {
                    resTime.processInput();
                    info.setResolution(resTime.getModelObject());
                } else {
                    resElevation.processInput();
                    info.setResolution(resElevation.getModelObject());
                }
            }
            setConvertedInput(info);
        }
    };

    /**
     * Returns all attributes conforming to the specified type
     * 
     * @param resource
     * @param type
     * @return
     */
    List<String> getAttributesOfType(ResourceInfo resource, Class<?> type) {
        List<String> result = new ArrayList<String>();

		if (resource instanceof FeatureTypeInfo) {
			try {
				FeatureTypeInfo ft = (FeatureTypeInfo) resource;
				for (PropertyDescriptor pd : ft.getFeatureType()
						.getDescriptors()) {
					if (type.isAssignableFrom(pd.getType().getBinding())) {
						result.add(pd.getName().getLocalPart());
					}
				}
			} catch (IOException e) {
				throw new WicketRuntimeException(e);
			}
		}

        return result;
    }

    /**
     * Renders a presentation mode into a human readable form
     * 
     * @author Alessio
     */
    public class PresentationModeRenderer implements IChoiceRenderer<DimensionPresentation> {

        public PresentationModeRenderer() {
            super();
        }

        public Object getDisplayValue(DimensionPresentation object) {
            return new ParamResourceModel(object.name(), DimensionEditor.this).getString();
        }

        public String getIdValue(DimensionPresentation object, int index) {
            return String.valueOf(object.ordinal());
        }
    }

}

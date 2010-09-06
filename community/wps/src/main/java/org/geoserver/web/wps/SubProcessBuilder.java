package org.geoserver.web.wps;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxSubmitLink;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.form.Form;

public class SubProcessBuilder extends WebPage {

	public SubProcessBuilder(ExecuteRequest request, final ModalWindow window) {
		Form form = new Form("form");
		add(form);

		final RequestBuilderPanel builder = new RequestBuilderPanel("builder", request);
		form.add(builder);

		form.add(new AjaxSubmitLink("apply") {

			@Override
			protected void onSubmit(AjaxRequestTarget target, Form form) {
				window.close(target);
			}
			
			@Override
			protected void onError(AjaxRequestTarget target, Form form) {
				super.onError(target, form);
				target.addComponent(builder.getFeedbackPanel());
			}
		});

	}
}
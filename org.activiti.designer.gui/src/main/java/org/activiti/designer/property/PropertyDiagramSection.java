/*******************************************************************************
 * <copyright>
 *
 * Copyright (c) 2005, 2010 SAP AG.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    SAP AG - initial API, implementation and documentation
 *
 * </copyright>
 *
 *******************************************************************************/
package org.activiti.designer.property;

import java.util.List;

import org.activiti.bpmn.model.BpmnModel;
import org.activiti.bpmn.model.Pool;
import org.activiti.bpmn.model.Process;
import org.activiti.designer.util.editor.Bpmn2MemoryModel;
import org.activiti.designer.util.editor.ModelHandler;
import org.activiti.designer.util.property.ActivitiPropertySection;
import org.apache.commons.lang.StringUtils;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.graphiti.features.IFeature;
import org.eclipse.graphiti.features.context.IContext;
import org.eclipse.graphiti.features.context.impl.CustomContext;
import org.eclipse.graphiti.features.impl.AbstractFeature;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.views.properties.tabbed.ITabbedPropertyConstants;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetWidgetFactory;

public class PropertyDiagramSection extends ActivitiPropertySection implements ITabbedPropertyConstants {

	private Text idText;
	private Text nameText;
	private Text namespaceText;
	private Text documentationText;
	private Text candidateStarterUsersText;
	private Text candidateStarterGroupsText;
	

	@Override
	public void createControls(Composite parent, TabbedPropertySheetPage tabbedPropertySheetPage) {
		super.createControls(parent, tabbedPropertySheetPage);

		TabbedPropertySheetWidgetFactory factory = getWidgetFactory();
		Composite composite = factory.createFlatFormComposite(parent);
		FormData data;

		idText = createText(composite, factory, null);
		createLabel(composite, "Id:", idText, factory); //$NON-NLS-1$
		
		nameText = createText(composite, factory, idText);
		createLabel(composite, "Name:", nameText, factory); //$NON-NLS-1$
		
		namespaceText = createText(composite, factory, nameText);
		createLabel(composite, "Namespace:", namespaceText, factory); //$NON-NLS-1$
		
    candidateStarterUsersText = createText(composite, factory, namespaceText);
    createLabel(composite, "Candidate start users (comma separated):",  candidateStarterUsersText, factory);
    
    candidateStarterGroupsText = createText(composite, factory, candidateStarterUsersText);
    createLabel(composite, "Candidate start groups (comma separated):",  candidateStarterGroupsText, factory);
    
		documentationText = factory.createText(composite, "", SWT.MULTI | SWT.BORDER | SWT.WRAP | SWT.V_SCROLL); //$NON-NLS-1$
		data = new FormData(SWT.DEFAULT, 100);
		data.left = new FormAttachment(0, 250);
		data.right = new FormAttachment(100, 0);
		data.top = new FormAttachment(candidateStarterGroupsText, VSPACE);
		documentationText.setLayoutData(data);
		documentationText.addFocusListener(listener);

		createLabel(composite, "Documentation:", documentationText, factory); //$NON-NLS-1$
		
	}

	@Override
	public void refresh() {
	  idText.removeFocusListener(listener);
		nameText.removeFocusListener(listener);
		namespaceText.removeFocusListener(listener);
		documentationText.removeFocusListener(listener);
		candidateStarterUsersText.removeFocusListener(listener);
		candidateStarterGroupsText.removeFocusListener(listener);
		
		Bpmn2MemoryModel model = ModelHandler.getModel(EcoreUtil.getURI(getDiagram()));
		Process process = null;
		if(getSelectedPictogramElement() instanceof Diagram) {
		  if (model.getBpmnModel().getPools().size() > 0) {
		    process = model.getBpmnModel().getProcess(model.getBpmnModel().getPools().get(0).getId());
		    setEnabled(false);
		  } else {
		    process = model.getBpmnModel().getMainProcess();
		    setEnabled(true);
		  }
		
		} else {
		  Pool pool = ((Pool) getBusinessObject(getSelectedPictogramElement()));
		  process = model.getBpmnModel().getProcess(pool.getId());
		  setEnabled(true);
		}
		
		if(StringUtils.isNotEmpty(model.getBpmnModel().getTargetNamespace())) {
      namespaceText.setText(model.getBpmnModel().getTargetNamespace());
    } else {
      namespaceText.setText("http://www.activiti.org/test");
    }
		
		if (process != null) {
  		idText.setText(process.getId());
  		if(StringUtils.isNotEmpty(process.getName())) {
  		  nameText.setText(process.getName());
  		} else {
  		  nameText.setText("");
  		}
  		if(StringUtils.isNotEmpty(process.getDocumentation())) {
  			documentationText.setText(process.getDocumentation());
  		} else {
  			documentationText.setText("");
  		}
  		
  		candidateStarterUsersText.setText("");
      if (process.getCandidateStarterUsers().size() > 0) {
        StringBuffer expressionBuffer = new StringBuffer();
        for (String user : process.getCandidateStarterUsers()) {
          if (expressionBuffer.length() > 0) {
            expressionBuffer.append(",");
          }
          expressionBuffer.append(user.trim());
        }
        candidateStarterUsersText.setText(expressionBuffer.toString());
      } 
  		
  		candidateStarterGroupsText.setText("");
  		if (process.getCandidateStarterGroups().size() > 0) {
  			StringBuffer expressionBuffer = new StringBuffer();
  			for (String group : process.getCandidateStarterGroups()) {
  				if (expressionBuffer.length() > 0) {
  					expressionBuffer.append(",");
  				}
  				expressionBuffer.append(group.trim());
  			}
  			candidateStarterGroupsText.setText(expressionBuffer.toString());
  		}
		}
		
		idText.addFocusListener(listener);
		nameText.addFocusListener(listener);
		namespaceText.addFocusListener(listener);
		documentationText.addFocusListener(listener);
		candidateStarterUsersText.addFocusListener(listener);
		candidateStarterGroupsText.addFocusListener(listener);
	}
	
	private void setEnabled(boolean enabled) {
	  idText.setEnabled(enabled);
    nameText.setEnabled(enabled);
    namespaceText.setEnabled(enabled);
    documentationText.setEnabled(enabled);
    candidateStarterUsersText.setEnabled(enabled);
    candidateStarterGroupsText.setEnabled(enabled);
	}

	private FocusListener listener = new FocusListener() {

		public void focusGained(final FocusEvent e) {
		}

		public void focusLost(final FocusEvent e) {
			Bpmn2MemoryModel model = ModelHandler.getModel(EcoreUtil.getURI(getDiagram()));
			if (model == null) {
				return;
			}
			
	    Process process = null;
	    if(getSelectedPictogramElement() instanceof Diagram) {
	      process = model.getBpmnModel().getMainProcess();
	    
	    } else {
	      Pool pool = ((Pool) getBusinessObject(getSelectedPictogramElement()));
	      process = model.getBpmnModel().getProcess(pool.getId());
	    }
	    
	    updateProcess(process, model.getBpmnModel(), e.getSource());
		}
	};
	
	protected String getCandidatesString(List<String> candidateList) {
    StringBuffer expressionBuffer = new StringBuffer();
    if (candidateList.size() > 0) {
      for (String candidate : candidateList) {
        if (expressionBuffer.length() > 0) {
          expressionBuffer.append(",");
        }
        expressionBuffer.append(candidate.trim());
      }
    }
    return expressionBuffer.toString();
  }
	
	protected void updateProcess(final Process process, final BpmnModel model, final Object source) {
    String oldValue = null;
    final String newValue = ((Text) source).getText();
    if (source == idText) {
      oldValue = process.getId();
    } else if (source == nameText) {
      oldValue = process.getName();
    } else if (source == namespaceText) {
      oldValue = model.getTargetNamespace();
    } else if (source == documentationText) {
      oldValue = process.getDocumentation();
    } else if (source == candidateStarterUsersText) {
      oldValue = getCandidatesString(process.getCandidateStarterUsers());
    } else if (source == candidateStarterGroupsText) {
      oldValue = getCandidatesString(process.getCandidateStarterGroups());
    }
    
    if ((StringUtils.isEmpty(oldValue) && StringUtils.isNotEmpty(newValue)) || (StringUtils.isNotEmpty(oldValue) && newValue.equals(oldValue) == false)) {
      IFeature feature = new AbstractFeature(getDiagramTypeProvider().getFeatureProvider()) {
        
        @Override
        public void execute(IContext context) {
          if (source == idText) {
            process.setId(newValue);
            if(getSelectedPictogramElement() instanceof Diagram == false) {
              Pool pool = ((Pool) getBusinessObject(getSelectedPictogramElement()));
              pool.setProcessRef(process.getId());
            }
          } else if (source == nameText) {
            process.setName(newValue);
          } else if (source == namespaceText) {
            model.setTargetNamespace(newValue);
          } else if (source == documentationText) {
            process.setDocumentation(newValue);
          } else if (source == candidateStarterUsersText) {
            updateCandidates(process, source);
          } else if (source == candidateStarterGroupsText) {
            updateCandidates(process, source);
          }
        }
        
        @Override
        public boolean canExecute(IContext context) {
          return true;
        }
      };
      CustomContext context = new CustomContext();
      execute(feature, context);
    }
  }
	
	protected void updateCandidates(Process process, Object source) {
    String candidates = ((Text) source).getText();
    if (StringUtils.isNotEmpty(candidates)) {
      String[] expressionList = null;
      if (candidates.contains(",")) {
        expressionList = candidates.split(",");
      } else {
        expressionList = new String[] { candidates };
      }
      
      if (source == candidateStarterUsersText) {
        process.getCandidateStarterUsers().clear();
      } else {
        process.getCandidateStarterGroups().clear();
      }

      for (String user : expressionList) {
        if (source == candidateStarterUsersText) {
          process.getCandidateStarterUsers().add(user.trim());
        } else {
          process.getCandidateStarterGroups().add(user.trim());
        }
      }
    }
  }
	
	private Text createText(Composite parent, TabbedPropertySheetWidgetFactory factory, Control top) {
    Text text = factory.createText(parent, ""); //$NON-NLS-1$
    FormData data = new FormData();
    data.left = new FormAttachment(0, 250);
    data.right = new FormAttachment(100, -HSPACE);
    if(top == null) {
      data.top = new FormAttachment(0, VSPACE);
    } else {
      data.top = new FormAttachment(top, VSPACE);
    }
    text.setLayoutData(data);
    text.addFocusListener(listener);
    return text;
  }
  
  private CLabel createLabel(Composite parent, String text, Control control, TabbedPropertySheetWidgetFactory factory) {
    CLabel label = factory.createCLabel(parent, text); //$NON-NLS-1$
    FormData data = new FormData();
    data.left = new FormAttachment(0, 0);
    data.right = new FormAttachment(control, -HSPACE);
    data.top = new FormAttachment(control, 0, SWT.CENTER);
    label.setLayoutData(data);
    return label;
  }

}

/*<ATGCOPYRIGHT>
 * Copyright (C) 1998-2011 Art Technology Group, Inc.
 * All Rights Reserved.  No use, copying or distribution of this
 * work may be made except in accordance with a valid license
 * agreement from Art Technology Group.  This notice must be
 * included on all copies, modifications and derivatives of this
 * work.
 *
 * Art Technology Group (ATG) MAKES NO REPRESENTATIONS OR WARRANTIES
 * ABOUT THE SUITABILITY OF THE SOFTWARE, EITHER EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO THE IMPLIED WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE, OR NON-INFRINGEMENT. ATG SHALL NOT BE
 * LIABLE FOR ANY DAMAGES SUFFERED BY LICENSEE AS A RESULT OF USING,
 * MODIFYING OR DISTRIBUTING THIS SOFTWARE OR ITS DERIVATIVES.
 *
 * "Dynamo" is a trademark of Art Technology Group, Inc.
 </ATGCOPYRIGHT>*/

package atg.droplet;

import java.beans.*;
import java.awt.Image;

/**
 * <P>A base class for all the droplet BeanInfo classes.
 *
 * @author Natalya Cohen
 * @version $Id: //product/DAS/version/10.0.3/Java/atg/droplet/DropletBeanInfo.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 **/

public class DropletBeanInfo extends SimpleBeanInfo {
  //-------------------------------------
  // CONSTANTS
  //-------------------------------------
  public static String CLASS_VERSION =
  "$Id: //product/DAS/version/10.0.3/Java/atg/droplet/DropletBeanInfo.java#2 $$Change: 651448 $";

  //-------------------------------------
  // FIELDS
  //-------------------------------------

  public final static String PARAM_DESCRIPTORS_NAME = "paramDescriptors";
  public final static String FUNCTIONAL_COMPONENT_CATEGORY_NAME = "functionalComponentCategory";
  public final static String FEATURE_COMPONENT_CATEGORY_NAME = "featureComponentCategory";
  public final static String PAGE_WIZARD_NAME = "pageWizard";

  public final static String FUNCTIONAL_COMPONENT_CATEGORY = "Servlet Beans";
  public final static String FEATURE_COMPONENT_CATEGORY = null;

  private final static String sSmallIcon = "/atg/ui/common/images/dropletcomp.gif";
  private final static String sLargeIcon = "/atg/ui/common/images/dropletcomp.gif";

  //-------------------------------------
  // METHODS
  //-------------------------------------

  //-------------------------------------
  /**
   * Creates the BeanInfo's BeanDescriptor.
   *
   * @param pClass the droplet class for which the BeanDescriptor is
   * being created
   * @param pPageWizardClassname the class name of the page wizard
   * which can be used to create/edit the droplet
   * @param pShortDescription the short description of the droplet
   * @param pParamDescriptors an array of ParamDescriptors describing
   * the droplet parameters
   **/
  public static BeanDescriptor createBeanDescriptor(Class pClass,
                String pPageWizardClassName,
                String pShortDescription,
                ParamDescriptor[] pParamDescriptors)
  {
    return createBeanDescriptor(pClass,
                                pPageWizardClassName,
                                pShortDescription,
                                pParamDescriptors,
                                FUNCTIONAL_COMPONENT_CATEGORY,
                                FEATURE_COMPONENT_CATEGORY);
  }

  //-------------------------------------
  /**
   * Creates the BeanInfo's BeanDescriptor.
   *
   * @param pClass the droplet class for which the BeanDescriptor is
   * being created
   * @param pPageWizardClassname the class name of the page wizard
   * which can be used to create/edit the droplet
   * @param pShortDescription the short description of the droplet
   * @param pParamDescriptors an array of ParamDescriptors describing
   * the droplet parameters
   * @param pFunctionalComponentCategory the component functional category this bean should be organized in
   **/
  public static BeanDescriptor createBeanDescriptor(
    Class pClass,
    String pPageWizardClassName,
    String pShortDescription,
    ParamDescriptor[] pParamDescriptors,
    String pFunctionalComponentCategory)
  {
    return createBeanDescriptor(pClass,
                                pPageWizardClassName,
                                pShortDescription,
                                pParamDescriptors,
                                pFunctionalComponentCategory,
                                FEATURE_COMPONENT_CATEGORY);
  }

  //-------------------------------------
  /**
   * Creates the BeanInfo's BeanDescriptor.
   *
   * @param pClass the droplet class for which the BeanDescriptor is
   * being created
   * @param pPageWizardClassname the class name of the page wizard
   * which can be used to create/edit the droplet
   * @param pShortDescription the short description of the droplet
   * @param pParamDescriptors an array of ParamDescriptors describing
   * the droplet parameters
   * @param pFunctionalComponentCategory the component functional category this bean should be organized in
   * @param pFeatureComponentCategory the component feature category this bean should be organized in
   **/
  public static BeanDescriptor createBeanDescriptor(
    Class pClass,
    String pPageWizardClassName,
    String pShortDescription,
    ParamDescriptor[] pParamDescriptors,
    String pFunctionalComponentCategory,
    String pFeatureComponentCategory)
  {
    BeanDescriptor beanDescriptor = new BeanDescriptor(pClass);
    beanDescriptor.setShortDescription(pShortDescription);

    if (pParamDescriptors != null)
      beanDescriptor.setValue(PARAM_DESCRIPTORS_NAME, pParamDescriptors);

    if (pFunctionalComponentCategory != null)
      beanDescriptor.setValue(FUNCTIONAL_COMPONENT_CATEGORY_NAME, pFunctionalComponentCategory);

    if (pFeatureComponentCategory != null)
      beanDescriptor.setValue(FEATURE_COMPONENT_CATEGORY_NAME, pFeatureComponentCategory);

    if (pPageWizardClassName != null)
      beanDescriptor.setValue(PAGE_WIZARD_NAME, pPageWizardClassName);
    return beanDescriptor;
  }

  //-------------------------------------
  /**
   * Merges two ParamDescriptor arrays into a new array.
   **/
  public static ParamDescriptor[] merge(ParamDescriptor[] pArray1,
                                        ParamDescriptor[] pArray2)
  {
    ParamDescriptor[] a = new ParamDescriptor[pArray1.length + pArray2.length];
    int i = 0;
    for ( ; i < pArray1.length; i++)
      a[i] = pArray1[i];
    for (int j = 0; j < pArray2.length; i++, j++)
      a[i] = pArray2[j];
    return a;
  }

  //-------------------------------------
  // SimpleBeanInfo overrides
  //-------------------------------------

  //-------------------------------------
  /**
   * Gets an image that may be used to visually represent this bean
   * (in the toolbar, on a form, etc).
   * @param iconKind the type of icon desired.
   * @see BeanInfo
   **/
  public Image getIcon(int iconKind) {
    if (iconKind == BeanInfo.ICON_MONO_16x16 ||
        iconKind == BeanInfo.ICON_COLOR_16x16) {
      Image img = loadImage(sSmallIcon);
      return img;
    }

    if (iconKind == BeanInfo.ICON_MONO_32x32 ||
        iconKind == BeanInfo.ICON_COLOR_32x32) {
      Image img = loadImage(sLargeIcon);
      return img;
    }

    return null;
  }
}

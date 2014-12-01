/*<ATGCOPYRIGHT>
 * Copyright (C) 2006-2010 Art Technology Group, Inc.
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
package atg.projects.store.ui;

import atg.ui.commerce.pricing.PricingExpressionGrammar;
import atg.ui.expreditor.SequenceRule;
import atg.ui.process.expression.DefaultProcessExpressionGrammarExtension;
import atg.ui.process.expression.ProcessExpressionGrammar;

import java.io.Serializable;


/**
 * A lightweight extension to the Scenario Grammar.
 *
 * @author ATG
 * @version $Revision: #3 $
 */
public class StoreGrammarExtension extends DefaultProcessExpressionGrammarExtension implements Serializable {

  /** Class version string. */
  public static String CLASS_VERSION = "$Id: //edu/ILT-Courses/main/COMM/StudentFiles/COM/setup/copy-files/apps/MyStore/EStore/src/atg/projects/store/ui/StoreGrammarExtension.java#3 $$Change: 635816 $";

  /**
   * Default constructor.
    */
  public StoreGrammarExtension() {
    super("atg.projects.store.ui.store-expression-grammar");
  }
  
  /**
   * Extend the grammar, given a host ScenarioContext, after all other
   * standard initialization (including default constructs) has taken
   * place.
   *
   * @param pGrammar an XclContext holding the grammar Constructs.
   * @param pScenarioContext the ScenarioContext representing the host scenario UI
   */
  public void completeExtendedGrammar(ProcessExpressionGrammar pGrammar)
  {
    SequenceRule itemFilter = (SequenceRule) pGrammar.getConstruct("store-commerce-item-filter");
    itemFilter.addConstruct(PricingExpressionGrammar.getItemConditionConstruct());
  }
}

/*<ATGCOPYRIGHT>
 * Copyright (C) 1999-2011 Art Technology Group, Inc.
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

package atg.commerce.fulfillment.processor;

import atg.service.pipeline.*;
import atg.core.util.ResourceUtils;
import atg.commerce.fulfillment.*;
import atg.commerce.order.Order;
import atg.commerce.order.ShippingGroup;
import atg.commerce.order.ShippingGroupCommerceItemRelationship;
import atg.commerce.order.RelationshipTypes;
import atg.commerce.order.InvalidParameterException;
import atg.commerce.states.*;
import atg.commerce.CommerceException;
import atg.commerce.order.DuplicateShippingGroupException;
import atg.commerce.order.ObjectCreationException;
 
import java.util.*;

/**
 * This processor splits the shipping group into two shipping groups, one with items 
 * PENDING_DELIVERY and one with items that aren't PENDING_DELIVERY
 *
 * @author Michael Traskunov
 * @version $Id: //product/DCS/version/10.0.3/Java/atg/commerce/fulfillment/processor/ProcSplitShippingGroupForAvailableItems.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 */
public class ProcSplitShippingGroupForAvailableItems implements PipelineProcessor {
  /** Class version string */
  public static final String CLASS_VERSION = "$Id: //product/DCS/version/10.0.3/Java/atg/commerce/fulfillment/processor/ProcSplitShippingGroupForAvailableItems.java#2 $$Change: 651448 $";

  static final String MY_RESOURCE_NAME = "atg.commerce.fulfillment.FulfillmentResources";

  /** Resource Bundle **/
  private static java.util.ResourceBundle sResourceBundle = java.util.ResourceBundle.getBundle(MY_RESOURCE_NAME, atg.service.dynamo.LangLicense.getLicensedDefault());
  
  private static final int SUCCESS = 1;
  
  //-----------------------------------------------
  public ProcSplitShippingGroupForAvailableItems() {
  }

  //-----------------------------------------------
  /**
   * Returns the valid return codes
   * 1 - The processor completed
   * @return an integer array of the valid return codes.
   */
  public int[] getRetCodes()
  {
    int[] ret = {SUCCESS};
    return ret;
  } 

  //-----------------------------------------------
  /**
   * Split the shipping group into two shipping groups, one with items PENDING_DELIVERY
   * and one with items that aren't PENDING_DELIVERY
   *
   * @beaninfo
   *          desription: Split the given shipping group into two groups.  One whose items
   *                      are all allocated, and ones whose items are not
   *
   * This method requires that an Order, ShippingGroup and OrderFulfiller object be supplied
   * in pParam in a HashMap. Use the PipelineConstants class' static members to key
   * the objects in the HashMap.
   *
   * @param pParam a HashMap which must contain an Order, ShippingGroup and OrderFulfiller object
   * @param pResult a PipelineResult object which stores any information which must
   *                be returned from this method invokation
   * @return an integer specifying the processor's return code
   * @exception Exception throws any exception back to the caller
   * @see atg.service.pipeline.PipelineProcessor#runProcess(Object, PipelineResult)
   */
  public int runProcess(Object pParam, PipelineResult pResult) throws Exception
  {
    HashMap map = (HashMap) pParam;
    Order pOrder = (Order) map.get(PipelineConstants.ORDER);
    HardgoodFulfiller of = (HardgoodFulfiller) map.get(PipelineConstants.ORDERFULFILLER);
    ShippingGroup pShippingGroup = (ShippingGroup) map.get(PipelineConstants.SHIPPINGGROUP);
    List performedModifications = (List) map.get(PipelineConstants.MODIFICATIONLIST);

    if (pOrder == null)
        throw new InvalidParameterException(ResourceUtils.getMsgResource("InvalidOrderParameter",
                                                                         MY_RESOURCE_NAME, sResourceBundle));
    if (of == null)
        throw new InvalidParameterException(ResourceUtils.getMsgResource("InvalidOrderFulfillerParameter",
                                                                         MY_RESOURCE_NAME, sResourceBundle));
 
    ShippingGroupStates sgs = of.getShippingGroupStates();
    ShipItemRelationshipStates sirs = of.getShipItemRelationshipStates();
    List newShippingGroups = null;
    try {
        newShippingGroups = splitShippingGroupWithAvailableItems(of, pOrder, pShippingGroup,
                                                                 performedModifications);
    }
    catch (Exception i) { // 4 type of exception
        if(of.isLoggingError())
            of.logError(i);
        throw new CommerceException(i);
    }
           
    if(newShippingGroups.size() > 0) {
        for(int groupNum = 0; groupNum < newShippingGroups.size(); groupNum++) {
            pShippingGroup = (ShippingGroup) newShippingGroups.get(groupNum);
            
            // We should be able to get buy with only checking one
            // relationship since splitShippingGroupWithAvailableItems
            // only splits if some items are PENDING_DELIVERY and
            // always splits all PENDING_DELIVERY items into a
            // separate group with no items that are not
            // PENDING_DELIVERY
            ShippingGroupCommerceItemRelationship sgcir = (ShippingGroupCommerceItemRelationship) 
                pShippingGroup.getCommerceItemRelationships().get(0);
            if(sgcir.getState() == sirs.getStateValue(ShipItemRelationshipStates.PENDING_DELIVERY)) {
                of.getOrderFulfillmentTools().setShippingGroupState(pShippingGroup,
                                                                    sgs.getStateValue(ShippingGroupStates.PENDING_SHIPMENT),
                                                                    null, performedModifications);

                break; // since we know the other group has no shippable items
              } // else this group is not shippable
        } //for
    } else {
        if(of.isLoggingDebug())
            of.logDebug("Split shipping groups returned an empty list... no new groups created.");
    }

    return SUCCESS;
  }

 
  /**
   * Split the shipping group into two shipping groups, one with items PENDING_DELIVERY
   * and one with items that aren't PENDING_DELIVERY
   *
   * @beaninfo
   *          desription: Split the given shipping group into two groups.  One whose items
   *                      are all allocated, and ones whose items are not.
   * @param pOrder The order containing the shippingGroup
   * @param pShippingGroup The shipping group to split
   * @param pModificationList A place to store all new modifications
   * @return A list of the newly created shipping groups.
   * @exception atg.commerce.order.InvalidParameterException
   * @exception atg.commerce.ObjectCreationException
   * @exception atg.commerce.order.DuplicateShippingGroupException
   * @exception atg.commerce.CommerceException
   **/
  protected List splitShippingGroupWithAvailableItems(HardgoodFulfiller of, Order pOrder, 
                                                      ShippingGroup pShippingGroup,
                                                      List pModificationList)
    throws InvalidParameterException, ObjectCreationException, DuplicateShippingGroupException, CommerceException
  {
    ShipItemRelationshipStates sirs = of.getShipItemRelationshipStates();
    List itemIds = new ArrayList();
    List quantities = new ArrayList();
    List relationships = null;
    Iterator relationshipIter = null;
    ShippingGroupCommerceItemRelationship sgcir = null;
    int shippableItemCount = 0;
    int totalItemCount = 0; //BUG 82099 - counter initialized
    List newShippingGroups = new ArrayList();
    List returnedGroups = null;
    
    if(of.isLoggingDebug())
        of.logDebug("Inside splitShippingGroupsWithAvailableItems");

    relationships = pShippingGroup.getCommerceItemRelationships();
    relationshipIter = relationships.iterator();

    while(relationshipIter.hasNext()) {
      totalItemCount++; //BUG 82099 - counter added
      sgcir = (ShippingGroupCommerceItemRelationship) relationshipIter.next();
      if(sgcir.getState() != sirs.getStateValue(ShipItemRelationshipStates.PENDING_DELIVERY)) {
        itemIds.add(sgcir.getCommerceItem().getId());
        if(sgcir.getRelationshipType() == RelationshipTypes.SHIPPINGQUANTITY) {
          quantities.add(Long.valueOf(sgcir.getQuantity()));
        } else {
          quantities.add(Long.valueOf(0));
        } //else
      } else {
        shippableItemCount++;
      }// else
    } // while

    //BUG 82099 - If clause changed

    if((shippableItemCount > 0) && (shippableItemCount < totalItemCount)) {

      long[] quantityArray = new long[quantities.size()];
      String[] itemIdArray = null;

      for(int i=0;i<quantities.size();i++) {
        quantityArray[i] = ((Long) quantities.get(i)).longValue();
      }
      itemIdArray = (String []) itemIds.toArray(new String[itemIds.size()]);
      returnedGroups = of.getOrderManager().splitShippingGroup(pOrder, 
                                                               pShippingGroup.getId(), 
                                                               itemIdArray,
                                                               quantityArray);
      // create modifications
      Iterator newGroups = returnedGroups.iterator();
      while(newGroups.hasNext()) {
        ShippingGroup newSg = (ShippingGroup)newGroups.next();
        if(newSg == pShippingGroup) {
          //this is not the new group
        } else {
          // this is the new group
          GenericAdd ga = 
              of.getOrderFulfillmentTools().createGenericAddValueToIdModification(Modification.TARGET_SHIPPING_GROUP,
                                                                                  newSg,
                                                                                  Modification.TARGET_ORDER,
                                                                                  pOrder.getId());
          pModificationList.add(ga);
          // add an update for each relationship in the group
          List rels = newSg.getCommerceItemRelationships();
          Iterator sgcirs = rels.iterator();
          ShippingGroupCommerceItemRelationship changedSgcir = null;
          while(sgcirs.hasNext()) {
            changedSgcir = (ShippingGroupCommerceItemRelationship)sgcirs.next();
            GenericUpdate gu = 
                of.getOrderFulfillmentTools().createGenericUpdateModification(of.getShipItemRelShippingGroupPropertyName(),
                                                                              pShippingGroup,
                                                                              newSg,
                                                                              changedSgcir.getId(),
                                                                              Modification.TARGET_RELATIONSHIP,
                                                                              true);
            pModificationList.add(gu);
          }
        }
      }

      newShippingGroups.addAll(returnedGroups);

    } else {
      // do nothing
    }

    return newShippingGroups;

  } // method

}

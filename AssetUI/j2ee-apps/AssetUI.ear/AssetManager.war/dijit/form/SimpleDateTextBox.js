/*<ATGCOPYRIGHT>
 * Copyright (C) 2011 Art Technology Group, Inc.
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

//----------------------------------------------------------------------------//

/**
 * This subclass of the Dojo 1.0 DateTextBox is intended to address an IE
 * rendering issue with the original DateTextBox, in which the visible text
 * input is moved to a separate line below its original location in the form.
 * This seems to be caused by the insertion of a table element into the DOM,
 * where, originally, there was only a text input.
 *
 * The table element is defined in the templateString of the ValidationTextBox
 * class, from which DateTextBox inherits.  The ValidationTextBox templateString
 * replaces that of its TextBox superclass.
 *
 * TextBox:
 * <input class="dojoTextBox" dojoAttachPoint='textbox,focusNode' name="${name}"
 *   dojoAttachEvent='onmouseenter:_onMouse,onmouseleave:_onMouse,onfocus:_onMouse,onblur:_onMouse,onkeyup,onkeypress:_onKeyPress'
 *   autocomplete="off" type="${type}"/>
 *
 * ValidationTextBox:
 * <table style="display: -moz-inline-stack;" class="dijit dijitReset dijitInlineTable" cellspacing="0" cellpadding="0"
 *   id="widget_${id}" name="${name}"
 *   dojoAttachEvent="onmouseenter:_onMouse,onmouseleave:_onMouse" waiRole="presentation"
 *   ><tr class="dijitReset"
 *     ><td class="dijitReset dijitInputField" width="100%"
 *       ><input dojoAttachPoint='textbox,focusNode' dojoAttachEvent='onfocus,onblur:_onMouse,onkeyup,onkeypress:_onKeyPress' autocomplete="off"
 *         type='${type}' name='${name}'
 *     /></td
 *     ><td class="dijitReset dijitValidationIconField" width="0%"
 *       ><div dojoAttachPoint='iconNode' class='dijitValidationIcon'></div><div class='dijitValidationIconText'>&Chi;</div
 *     ></td
 *   ></tr
 * ></table>
 *
 * This widget replaces the ValidationTextBox templateString with the original
 * TextBox templateString.
 *
 * @version $Id: //product/AssetUI/version/10.0.3/src/web-apps/AssetManager/dijit/form/SimpleDateTextBox.js#1 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 */

dojo.provide("atg.widget.form.SimpleDateTextBox");

dojo.require("dijit.form.DateTextBox");

dojo.declare(
  "atg.widget.form.SimpleDateTextBox",
  dijit.form.DateTextBox, {
    templateString:"<input class=\"dojoTextBox\" dojoAttachPoint='textbox,focusNode' name=\"${name}\"\r\n\tdojoAttachEvent='onmouseenter:_onMouse,onmouseleave:_onMouse,onfocus:_onMouse,onblur:_onMouse,onkeyup,onkeypress:_onKeyPress'\r\n\tautocomplete=\"off\" type=\"${type}\"\r\n\t/>\r\n"
  }
);

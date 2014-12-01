dojo.provide("atg.widget.validation.SubtotalTextbox");
dojo.require("dojo.event.*");
dojo.require("dojo.io.*");
dojo.require("dojo.widget.*");
dojo.require("dojo.widget.CurrencyTextbox");
dojo.require("dojo.validate.*");
dojo.require("dojo.validate.common");

dojo.widget.defineWidget(
  "atg.widget.validation.SubtotalTextbox",
  dojo.widget.CurrencyTextbox,
  {
		// summary:
		//	  A subclass that extends CurrencyTextbox to
		//		allow the sum of values in multiple text boxes to be constrained to 
		//    the value in another ValidationTextbox.
		//    To attach the sum of values in a set of subtotal textboxes to the value 
		//    in another element, use the same total widget for all the linked text boxes.
		//

    // Widget ID of the validation textbox widget containing the total value
    totalWidget: "",

    buildRendering: function() {
      atg.widget.validation.SubtotalTextbox.superclass.buildRendering.apply(this, arguments);
      this.setRendered(true);
    },

    destroyRendering: function(finalize) {
      atg.widget.validation.SubtotalTextbox.superclass.destroyRendering.apply(this, arguments);
      this.setRendered(false);
      this.unregisterSubtotalTextbox(atg.widget.validation.totalWidgetMap, this.totalWidget, this);
    },

    postCreate: function() {
      atg.widget.validation.SubtotalTextbox.superclass.postCreate.apply(this, arguments);
      // Register to contributing to the total via array
      this.registerSubtotalTextbox(atg.widget.validation.totalWidgetMap, this.totalWidget, this);
    },

		isInRange: function(){
			// summary: Over-ride for subtotal currency validation
			return this.validateRange();
		},

    // Registers the current textbox widget with the total widget map
    // to sum its value with the values in other registered textboxes
    registerSubtotalTextbox: function(totalMap, totalId, textbox) {
      if (totalId) {
        var textboxArray = totalMap[totalId];
        if (!textboxArray) {
          textboxArray = [];
        }
        this.appendTextbox(textboxArray, textbox);
        totalMap[totalId] = textboxArray;
        var dbg = new dojo.string.Builder;
        dbg.append("SubtotalTextbox.js: Connecting value in subtotal textbox ",
          textbox.widgetId, " to total value in ", totalId);
        dojo.debug(dbg.valueOf());
        var totalTextbox = dojo.widget.byId(totalId);
        // total exists, notify subtotals when total value changes
        // only do this once, bc subtotals will notify each other
        if (totalTextbox && textboxArray.length === 1) {
          dojo.event.connectOnce(totalTextbox, "onblur", textbox, "isInRange");
          dojo.event.connectOnce(totalTextbox, "onfocus", textbox, "isInRange");
          dojo.event.connectOnce(totalTextbox, "onkeyup", textbox, "isInRange");
        }
      }
    },

    appendTextbox: function(textboxArray, textbox) {
      var index = -1;
      for (var i = 0; i < textboxArray.length; i++) {
        // compare by widget id's because objects could be different after parsing
        if (textboxArray[i].widgetId == textbox.widgetId) {
          index = i;
          break;
        }
      }
      if (index === -1) {
        textboxArray.push(textbox);
      }
      else {
        textboxArray[index] = textbox; // replace
      }
    },

    // Unregisters the current textbox widget with the total widget map
    // to free up widget allocation
    unregisterSubtotalTextbox: function(totalMap, totalId, textbox) {
      if (totalId) {
        var textboxArray = totalMap[totalId];
        if (!textboxArray)
          return;

        var index = textboxArray.indexOf(textbox);
        if (index > -1) {
          textboxArray.splice(index, 1);
        }
        totalMap[totalId] = textboxArray;
        var dbg = new dojo.string.Builder;
        dbg.append("SubtotalTextbox.js: Unconnecting subtotal textbox ", textbox.widgetId);
        dojo.debug(dbg.valueOf());
      }
    },

    getTotalValue: function(totalId) {
      var value = 0;
      if (totalId) {
        var totalTextbox = dojo.widget.byId(totalId);
        if (totalTextbox) {
          value = this.convertStringToNumber(totalTextbox.textbox.value);
        }
      }
      return value;
    },

    computeSum: function(totalMap, totalId) {
      var sum = 0;
      if (totalId) {
        var textboxArray = totalMap[totalId];
        if (textboxArray && textboxArray.length) {
          var dbg = new dojo.string.Builder;
          dbg.append("SubtotalTextbox.js: Computing total sum for values in ",
            textboxArray.length, " subtotal textboxes: ");
          for (var i = 0; i < textboxArray.length; i++) {
            var value = this.convertStringToNumber(textboxArray[i].textbox.value);
            if (value) {
              sum += value;
              dbg.append(textboxArray[i].widgetId, " = ", value, "; ");
            }
          }
          dojo.debug(dbg.valueOf());
        }
      }
      return sum;
    },

		validateRange: function(){
			// summary: Over-ride for subtotal currency validation
			var result = false;
      // Look up total value in total widget
      var totalValue = this.getTotalValue(this.totalWidget);
      var summedValue = 0;
      if (totalValue) {
        // Value should be the sum of all connected widget values
  			summedValue = this.computeSum(atg.widget.validation.totalWidgetMap, this.totalWidget);
  			if (summedValue == totalValue) {
  			  result = true;
  			}
  			else {
          var dbg = new dojo.string.Builder;
          dbg.append("SubtotalTextbox.js: Subtotals do not add up: summed value is ",
            summedValue, ", total value is ", totalValue);
          dojo.debug(dbg.valueOf());
  			}
      }
      this.updateUI(atg.widget.validation.totalWidgetMap, this.totalWidget, result);
			return result;
		},

    updateUI: function(totalMap, totalId, isInRange) {
      if (!this.isRendered())
        return;

      if (totalId) {
        var textboxArray = totalMap[totalId];
        if (textboxArray) {
          for (var i = 0; i < textboxArray.length; i++) {
            var indicatorElement = document.getElementById(textboxArray[i].inlineIndicator);
            if (indicatorElement) {
              if (isInRange) {
// TODO: fix this - no empty indicator style
//                indicatorElement.className = textboxArray[i].emptyIndicatorStyle;
                indicatorElement.title = "";
              }
              else {
                indicatorElement.className = textboxArray[i].outOfRangeIndicatorStyle;
                indicatorElement.title = textboxArray[i].rangeMessage;
              }
            }
          }
        }
        var totalTextbox = dojo.widget.byId(totalId);
        if (totalTextbox) {
          var indicatorElement = document.getElementById(totalTextbox.inlineIndicator);
          if (indicatorElement) {
            var dbg = new dojo.string.Builder;
            dbg.append("SubtotalTextbox.js: Subtotal ", this.widgetId);
            if (isInRange) {
// TODO: fix this - no empty indicator style
//              indicatorElement.className = totalTextbox.emptyIndicatorStyle;
              indicatorElement.title = "";
              dbg.append(" clearing inline indicator for total ");
            }
            else {
              indicatorElement.className = totalTextbox.outOfRangeIndicatorStyle;
              indicatorElement.title = totalTextbox.rangeMessage;
              dbg.append(" setting inline indicator for total ");
            }
            dbg.append(totalId);
            dojo.debug(dbg.valueOf());
          }
        }
      }
    },

    isRendered: function() {
      var rendered = false;
      if (atg.widget.validation.isRenderedMap[this.widgetId]) {
        rendered = true;
      }
      else {
        atg.widget.validation.isRenderedMap[this.widgetId] = false;
      }
      return rendered;
    },

    setRendered: function(rendered) {
      atg.widget.validation.isRenderedMap[this.widgetId] = rendered;
    },

    // ripped off from part of isInRange in dojo.validate.common
    convertStringToNumber: function(/*String*/value) {
    	// splice out anything not part of a number
    	var pattern = "[^" + "." + "\\deE+-]";
    	value = value.replace(RegExp(pattern, "g"), "");
    
    	// trim ends of things like e, E, or the decimal character
    	value = value.replace(/^([+-]?)(\D*)/, "$1");
    	value = value.replace(/(\D*)$/, "");
    
    	// replace decimal with ".". The minus sign '-' could be the decimal!
    	pattern = "(\\d)[" + "." + "](\\d)";
    	value = value.replace(RegExp(pattern, "g"), "$1.$2");
    
    	return Number(value);
    }
  }
);

// Map of SubtotalTextbox widgets keyed by totalWidget. Values in all widgets with the
// same totalWidget when summed must equal at least the value in the total widget.
// Put this outside the widgets to avoid order dependencies in widget creation.
atg.widget.validation.totalWidgetMap = {};
atg.widget.validation.isRenderedMap = {};

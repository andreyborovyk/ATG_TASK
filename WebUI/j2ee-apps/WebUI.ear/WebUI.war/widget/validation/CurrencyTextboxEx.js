dojo.provide("atg.widget.validation.CurrencyTextboxEx");
dojo.require("dojo.event.*");
dojo.require("dojo.io.*");
dojo.require("dojo.widget.*");
dojo.require("dojo.widget.CurrencyTextbox");
dojo.require("dojo.validate.*");
dojo.require("dojo.validate.common");
dojo.require("dojo.i18n.currency");
dojo.require("dojo.i18n.number");

dojo.widget.defineWidget(
  "atg.widget.validation.CurrencyTextboxEx",
  dojo.widget.CurrencyTextbox,
  {
    locale : "",
    currencyCode : "",
    invalidWidget : false,


    isValid: function(){
      // summary: Over-ride for subtotal currency validation
      // currently do not do any validation.
      // validation is done in the corresponding functional area.

      // if the widget is marked as invalid, then return false. Otherwise resume the validation.

      if (this.invalidWidget) {
        return false;
      }

      if (typeof this.textbox.value === 'undefined') {
        return false;
      }

      var tempAmount = null;
      if (isNaN(this.parseAmount(this.textbox.value))) {
        dojo.debug("The currency is not valid");
        //the currency may be invalid due to the formating.
        // remove special characters and parse again.
        //Replace ','
        tempAmount = this.textbox.value;
        tempAmount = this.replaceSeparator(tempAmount);
        tempAmount = this.replaceCurrencySymbol(tempAmount,this.currencyCode);
        tempAmount = this.formatAmount(tempAmount, this.currencyCode, true);

        result = this.parseAmount(tempAmount);
        if (isNaN(result)) {
          return false;
        } else {
          return true;
        }
      } else {
        return true;
      }
    },

    parseAmount : function (pAmount, pFlags/*optional*/) {
      var result;
      if (typeof pFlags == 'undefined') {
        pFlags = {
          fractional : true
        };
      }

      pAmount = this.replaceCurrencySymbol(pAmount,this.currencyCode);
      return result = dojo.i18n.number.parse(pAmount, null, pFlags);
    },

    replaceCurrencySymbol : function (pAmount, pCurrencyCode) {
      if (!pAmount)  {
        return;
      }

      if (!pCurrencyCode)  {
        pCurrencyCode ="USD";
      }

      var formatData = dojo.i18n.currency._mapToLocalizedFormatData(dojo.i18n.currency.FORMAT_TABLE, pCurrencyCode, null);
      if (typeof pAmount != 'string') {
        pAmount = pAmount.toString();
      }

      return pAmount.replace(new RegExp("\\" + formatData.symbol), "");
   },

   replaceSeparator : function (pAmount, pCurrencyCode) {
      if (!pAmount)  {
        return;
      }

      if (!pCurrencyCode)  {
        pCurrencyCode ="USD";
      }

      var formatData = dojo.i18n.number._mapToLocalizedFormatData(dojo.i18n.currency.FORMAT_TABLE, pCurrencyCode, null);
      if (typeof pAmount != 'string') {
        pAmount = pAmount.toString();
      }

      return pAmount.replace(new RegExp("\\" + ','), "");
   },

  /**
   * This method formats the given amount.
   */
   formatAmount : function (pAmount, pCurrencyCode, pRemoveCurrencySymbol, pFlags/*optional*/) {
    if (typeof pFlags == 'undefined') {
      pFlags = {
        places : 2,
        round : true
      };
    }

    var formattedAmount = dojo.i18n.currency.format(pAmount, pCurrencyCode ,pFlags);

    if (pRemoveCurrencySymbol) {
      formattedAmount = this.replaceCurrencySymbol(formattedAmount, pCurrencyCode);
    }

    return formattedAmount;
  },

  /**
   *
   * This method is used to mark widget as valid or invalid.
   * All validations could not be done in the widget. If there is any special validation needs to be
   * done, they can be done outside of this widget and mark the widget as invalid.
   *
   */
  markAsInvalid : function (pFlag) {
    this.invalidWidget = pFlag;
  }
  }
);

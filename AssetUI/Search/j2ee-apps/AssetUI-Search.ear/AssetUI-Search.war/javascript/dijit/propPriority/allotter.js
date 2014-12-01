/*
  Property Prioritization Widget v0.2

  Created by Sykes on 2008-01-10.
  Copyright (c) 2006 Media~Hive Inc.. All rights reserved.

***************************************************************************/


dojo.provide("atg.widget.propPriority.allotter");

dojo.declare(
    "atg.widget.propPriority.allotter",
    [dijit._Widget, dijit._Templated, dijit._Container],
    {
      widgetsInClass: true,
      ppData: '',
      templatePath: "/AssetUI-Search/javascript/dijit/propPriority/templates/ppAllotter.jsp",
      i18n:{
        "ignoreText":"Ignore",
        "errorZero":"Error: Properties Total is 0%",
        "errorNoWeights":"No Search Result Weights have been set",
        "errorNoPropertyWeights":"Property Weight needs to be set",
        "errorTotalNo100":"Total must equal 100%"
      },

      postCreate: function() {
        var _this = this;
        dojo.subscribe("atg/propPriority/mainValidation", this, "handleValidation");

        // Handle the data that's been passed
        if (this.ppData) {
          this.handleLoad(this.ppData);
        }

        // Set up the allotter fields array
        this.allotterInputs = [
            this.propertiesWeightView,
            this.overallTextRelevanceView,
            this.textRelevanceByFieldView
        ];

        // Add on blur event handler to top main input values
        for (input in this.allotterInputs) {
          dojo.connect(this.allotterInputs[input], "blur", this, "updateMainValues");
        }
      },


      handleLoad: function(data) {
        // do the daat setup
        this.data = data;
        this.populateMainData();
        this.setUpSliders();
      },


      populateMainData:function() {
        // Set up the property slider data
        if (this.data.properties.length == 0) {
          this.propertiesWeightView.value = "0";
          this.propertiesWeightView.disabled = true;
          //dojo.byId('atg_smerch_propertyWeightSetters').firstChild.style.display = "block";
          dojo.byId('atg_smerch_propertyWeightSetters').firstChild.style.visibility = "visible";
        } else {
          dojo.byId('atg_smerch_propertyWeightSetters').firstChild.style.display = "none";
          this.propertiesWeightView.value = this.propertiesWeightView.oldValue = this.data.allotter.propertiesWeight;
        }
        this.overallTextRelevanceView.value = this.overallTextRelevanceView.oldValue =  this.data.allotter.overallTextRelevance;
        this.textRelevanceByFieldView.value = this.textRelevanceByFieldView.oldValue = this.data.allotter.textRelevanceByField;
        this.mainWeightTotal.innerHTML = parseInt(this.propertiesWeightView.value)
            + parseInt(this.overallTextRelevanceView.value)
            + parseInt(this.textRelevanceByFieldView.value)
            + "%";
        this.updateMainValues();
      },


      setUpSliders: function() {
        this.sliderRows = [];
        // set up the slider parent widgets
        for (x in this.data.properties) {
          var newRow = new atg.widget.propPriority.weightSetter(
            {
                data:this.data.properties[x],
								ignoreText : this.i18n.ignoreText,
                widgetParent: this
            });
          this.sliderRows.push(newRow);
          dojo.byId('atg_smerch_propertyWeightSetters').appendChild(newRow.domNode);
        }
        // cycle through and do the math
        this.updateRelativeWeights();
      },


      updateRelativeWeights: function() {
        this.sliderValueTotal = 0;
        this.sliderPercentTotal = 0;

        var sliderValue = 0;
        for (x in this.sliderRows) {
          sliderValue = parseInt(this.sliderRows[x].sliderValue);
          this.sliderValueTotal += (isNaN(sliderValue) ? 0 : sliderValue);
        }

        for (x in this.sliderRows) {
          sliderValue = parseInt(this.sliderRows[x].sliderValue);
          sliderValue = (isNaN(sliderValue) ? 0 : sliderValue);

          var pValue = sliderValue / this.sliderValueTotal * this.propertiesWeightView.value;
          this.sliderRows[x].updateValue(pValue);
          this.sliderPercentTotal += pValue;
        }

        if(this.textRelevanceByFieldView.value == 0){
          dojo.byId('searchTermPropertyMatchLink').style.visibility = "hidden";
        }else{
          dojo.byId('searchTermPropertyMatchLink').style.visibility = "visible";
        }

        var pwvValue = parseInt(this.propertiesWeightView.value).toFixed(0);
        if (this.propertiesWeightView.value == 0) {
          for (x in this.sliderRows) {
            this.sliderRows[x].slider.setDisabled(true);
            dojo.query('EM', this.sliderRows[x].domNode)[0].style.display = "none";
            this.sliderRows[x].slider.domNode.title = this.i18n.errorZero;
          }

          // dojo.addClass(dojo.query(":first-child", "propertyTotal")[0], "invalid");
					// dojo.query(":first-child", "propertyTotal")[0].innerHTML = (isNaN(pwvValue) ? 0 : pwvValue) +"%";
          dojo.query(":first-child", "propertyTotal")[0].innerHTML = this.doToOneDecimalPercent(this.propertiesWeightView.value);
          dojo.query(":last-child", 'propertyTotal')[0].style.display = "none";
        } else {
          for (x in this.sliderRows) {
            this.sliderRows[x].slider.setDisabled(false);
            this.sliderRows[x].updateValue(this.sliderRows[x].sliderValue/this.sliderValueTotal * this.propertiesWeightView.value);
          }

          dojo.removeClass(dojo.query(":first-child", "propertyTotal")[0], "invalid");
          dojo.query(":last-child", 'propertyTotal')[0].title = "";
          var sptValue = this.sliderPercentTotal.toFixed(0);
//          dojo.query(":first-child", "propertyTotal")[0].innerHTML = (isNaN(sptValue) ? 0 : sptValue) + "%";
          dojo.query(":first-child", "propertyTotal")[0].innerHTML = this.doToOneDecimalPercent(this.propertiesWeightView.value);
          dojo.query(":last-child", 'propertyTotal')[0].style.display = "block";
          dojo.query(":last-child", 'propertyTotal')[0].style.width = (isNaN(pwvValue) ? 0 : pwvValue) + "px";

          if (this.sliderValueTotal == 0) {
            dojo.addClass(dojo.query(":first-child", "propertyTotal")[0], "invalid");
            dojo.query(":first-child", "propertyTotal")[0].title = this.i18n.errorNoPropertyWeights;

            dojo.query(":last-child", 'propertyTotal')[0].style.display = "none";
          }
        }

        this.checkStatus();
      },


      doNumberRounding: function(num) {
        var rounded = Math.round(num * 10) / 10;
        return rounded;
      },

      doToOneDecimalPercent: function(num) {
        var parsed = (Math.round(num * 10) / 10).toFixed(1) + "%";
        return parsed;
      },


      totalTotal: 0,
      
      errorExist: false,


      doOnError: function() {},


      handleError: function() {
        this.errorExist = true;
        this.doOnError();
      },


      doOnSuccess: function() {},


      handleSuccess: function() {
        if (this.errorExist) {
          this.errorExist = false;
          this.doOnSuccess();
        }
      },


      updateMainValues: function() {

        dojo.byId(this.data.allotter.propertiesWeightId).value = this.propertiesWeightView.value;
        dojo.byId(this.data.allotter.overallTextRelevanceId).value = this.overallTextRelevanceView.value;
        dojo.byId(this.data.allotter.textRelevanceByFieldId).value = this.textRelevanceByFieldView.value;

        /* Validate the input from the user */
        for (input in this.allotterInputs) {
          this.allotterInputs[input].value = this.doNumberRounding(this.allotterInputs[input].value);
          if((isNaN(parseInt(this.allotterInputs[input].value)))
              || (this.allotterInputs[input].value > 100)
              || (this.allotterInputs[input].value < 0)) {
            atg.sparkle(this.allotterInputs[input]);
            this.allotterInputs[input].value = this.allotterInputs[input].oldValue;
          } else {
            this.allotterInputs[input].oldValue = this.allotterInputs[input].value;
          }
        }

        this.totalTotal = parseInt(this.propertiesWeightView.value)
            + parseInt(this.overallTextRelevanceView.value)
            + parseInt(this.textRelevanceByFieldView.value);

				/* Updates the Totals and the subTotals */
				dojo.query(":first-child", 'totalTotal')[0].innerHTML = this.mainWeightTotal.innerHTML = this.totalTotal+"%";

				dojo.query(":first-child", "textRelevanceTotal")[0].innerHTML = this.doToOneDecimalPercent(this.overallTextRelevanceView.value);

				if(this.overallTextRelevanceView.value == 0){
					dojo.query(":last-child", "textRelevanceTotal")[0].style.display = "none";
				}else{
					dojo.query(":last-child", "textRelevanceTotal")[0].style.display = "block";
					dojo.query(":last-child", "textRelevanceTotal")[0].style.width = this.overallTextRelevanceView.value+"px";
				}

				dojo.query(":first-child", "textPropertyMatchTotal")[0].innerHTML = this.doToOneDecimalPercent(this.textRelevanceByFieldView.value);

				if(this.textRelevanceByFieldView.value == 0){
					dojo.query(":last-child", "textPropertyMatchTotal")[0].style.display = "none";
				}else{
					dojo.query(":last-child", "textPropertyMatchTotal")[0].style.display = "block";
					dojo.query(":last-child", "textPropertyMatchTotal")[0].style.width = this.textRelevanceByFieldView.value+"px";
				}

        if (this.totalTotal != 100) {
          dojo.addClass(dojo.byId('totalTotal'), "invalid");
          dojo.byId('totalTotal').title = this.i18n.errorTotalNo100;
          dojo.addClass(this.mainWeights, "validationError");
          this.mainWeights.title = this.i18n.errorTotalNo100;
        } else {
          dojo.removeClass(dojo.byId('totalTotal'), "invalid");
          dojo.byId('totalTotal').title = "";
          dojo.removeClass(this.mainWeights, "validationError");
          this.mainWeights.title = "";
        }

        this.updateRelativeWeights();
      },

      checkStatus: function() {
        var errors = 0;

        if (this.totalTotal != 100)
          errors++;

        if ((this.sliderRows != null) && (this.sliderRows.length != 0)) {
          var propertyTotal = isNaN(this.propertiesWeightView.value) ? 0 : this.propertiesWeightView.value;
          if ((0 < propertyTotal) && (0 == this.sliderValueTotal))
            errors++;
        }

        if (errors > 0)
          this.handleError();
        else
          this.handleSuccess();
      },

      handleValidation: function(obj) {
        // extension point
      },


      sanitySaver: ''
    }
);

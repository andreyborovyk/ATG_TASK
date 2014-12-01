/*
  Property Prioritization

  Weight Setter Widget v 0.2

  Created by Sykes on 2008-01-10.
  Copyright (c) 2006 Media~Hive Inc.. All rights reserved.

***************************************************************************/


dojo.provide("atg.widget.propPriority.weightSetter");

dojo.declare(
    "atg.widget.propPriority.weightSetter",
    [dijit._Widget, dijit._Templated, dijit._Container],
    {
      widgetsInTemplate: true,
      templatePath: "/AssetUI-Search/javascript/dijit/propPriority/templates/ppWeightSet.jsp",


      postCreate: function() {
        this.sliderValue = this.data.weight;
        this.uniqueName = this.data.id;
        this.domId = this.data.domId;
        this.propertyName.innerHTML = this.data.name;
        this.propertyValueRankLink.innerHTML = this.data.valueRanking;
        this.propertyValueRankLink.href = this.data.valueLink;

        this.slider = new dijit.form.HorizontalSlider(
          {
              value: this.data.weight,
              maximum: 10,
              minimum: 0,
              pageIncrement: 10,
              showButtons: false,
              intermediateChanges: false,
              discreteValues: 11,
              widgetParent: this,
              onChange: function() {
                // Set the new slider value
                this.widgetParent.sliderValue = arguments[0];

                // Update the associated form field with thie slider
                dojo.byId(this.widgetParent.domId).value =  arguments[0];

                // update the sliders
                this.widgetParent.widgetParent.updateRelativeWeights();
                eval('var f = ' + dojo.byId(this.widgetParent.domId).onclick + '; f();');

                this.sliderHandle.title = arguments[0];
              }
          }
        );

        this.sliderView.appendChild(this.slider.domNode);
        this.slider.domNode.style.width="100%";
        this.slider.domNode.style.height="20px";
        this.slider.sliderHandle.title = this.data.weight;

        var horizontalRule = new dijit.form.HorizontalRule(
          {
            style: "height: 5px; margin-top: -10px !important; margin-top: -5px; z-index: 1",
            count: 11
          }
        );

        this.slider.bottomDecoration.appendChild(horizontalRule.domNode);
      },


      updateValue: function(data) {
        if (data == 0 || isNaN(data)) {
          this.propertyWeightNumeric.innerHTML = "<em>["+this.widgetParent.i18n.ignoreText+"]</em>";
          this.propertyWeightGraphic.style.display = "none";
          this.propertyValueRankLink.style.display = "none"
          this.propertyWeightGraphic.style.width = 0;
        } else {
          this.propertyWeightNumeric.innerHTML = data.toFixed(1) + "%";
          this.propertyWeightGraphic.style.width = data + "px";
          this.propertyWeightGraphic.style.display = "block";
          this.propertyValueRankLink.style.display = "inline"
          this.propertyWeightGraphic.title = data.toFixed(1) + "%";
        }
      },


      onSliderChange: function(data) {},

      sanitySaver: ''
    }
);

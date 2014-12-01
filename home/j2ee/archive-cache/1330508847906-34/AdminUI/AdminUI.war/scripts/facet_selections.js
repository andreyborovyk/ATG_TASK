var facetSelections = {
  init: function(displayMethod, selectionOption, sorting) {
    this.onDisplayMethodChange(displayMethod);
    this.onSelectionChange(selectionOption);
    this.onSortingChange(sorting);
    this._updatePropertyTypeDependencies();
    initTable(document.getElementById("specifiedValuesTable"));
    initTable(document.getElementById("excludedValuesTable"));
  },

    // Property Type functions
  _updatePropertyTypeDependencies: function() {
    var propertyType = document.getElementById("facetPropertyType").value;
    var caseSensitiveDisplay = (propertyType == "string") || (propertyType == "enum") ? "" : "none";
    document.getElementById("caseSensitiveDiv").style.display = caseSensitiveDisplay;
    var disableOptions1and2 = propertyType == "boolean" ? "true" : null;
    document.getElementById("selectionOption1").disabled = disableOptions1and2;
    document.getElementById("selectionOption2").disabled = disableOptions1and2;
  },
  onPropertyTypeChange: function(dontSortRangePoints) {
    this._updatePropertyTypeDependencies();
    this.sortRangePoints();
  },

  // Facet Value Display Method functions
  onDisplayMethodChange: function(value) {
    var rangesMethod = value == "ranges";
    var numericSortOption = document.getElementById('facetSorting0');
    if (rangesMethod) {
      var explSortOption = document.getElementById('facetSorting3');
      if (explSortOption.checked == true) {
        numericSortOption.checked = true;
      }
      this.onSortingChange("none");
    } else {
      var rangeSortOption = document.getElementById('facetSorting2');
      if (rangeSortOption.checked == true) {
        numericSortOption.checked = true;
      }
    }
    document.getElementById("rangeOrder1Row").style.display = rangesMethod ? "" : "none";
    document.getElementById("rangeOrder2Row").style.display = rangesMethod ? "" : "none";
    document.getElementById("rangeOrder3Row").style.display = rangesMethod ? "" : "none";
    document.getElementById("rangeSortingDiv").style.display = rangesMethod ? "" : "none";
    document.getElementById("explicitSortingDiv").style.display = !rangesMethod ? "" : "none";
  },

  // Selection Option functions
  onSelectionChange: function(value) {
    document.getElementById("freeDiv").style.display = value == "free" ? "" : "none";
    document.getElementById("fixedDiv").style.display = value == "fixed" ? "" : "none";
    document.getElementById("explicitDiv").style.display = value == "explicit" ? "" : "none";
    document.getElementById("addRangePointDiv").style.display = value == "explicit" ? "" : "none";
  },

  // Range Points functions
  initRangePoints: function(jsonData) {
    atg.searchadmin.adminui.formsubmitter.hijackNode(document.getElementById("addRangePointSubmit"), this.rangePointsOnLoad);
    atg.searchadmin.adminui.formsubmitter.hijackNode(document.getElementById("deleteRangePointSubmit"), this.rangePointsOnLoad);
    atg.searchadmin.adminui.formsubmitter.hijackNode(document.getElementById("sortRangePointsSubmit"), this.rangePointsOnLoad);
    this.rangePointsOnLoad(jsonData);
  },
  deleteRangePoint: function(index) {
    document.getElementById("deleteRangePointIndexHidden").value = index;
    document.getElementById("deleteRangePointSubmit").click();
    return false;
  },
  sortRangePoints: function() {
    document.getElementById("sortRangePointsSubmit").click();
    return false;
  },
  disableAddButton: function(inputElement) {
    var buttonAddRangePoint = document.getElementById("addRangePointSubmit");
    buttonAddRangePoint.disabled = (inputElement.value == "");
  },
  rangePointsOnLoad: function(data) {
    document.getElementById("rangePointsTableDiv").innerHTML = data.tableContent;
    if (data.alerting && data.alerting.messages.length > 0) {
      alerting.showMessages(data.alerting.messages, data.alerting.title);
    } else {
      var rpi = document.getElementById("rangePointInput");
      rpi.value = "";
      facetSelections.disableAddButton(rpi); // it's not possible to use "this.disable..." here.
    }
    return true;
  },
  
  // Facet Value Sorting functions
  onSortingChange: function(value) {
    var explicitOrderDisplay = value == "explicitOrder" ? "" : "none";
    document.getElementById("specifiedValuesRow").style.display = explicitOrderDisplay;
    document.getElementById("excludedValuesRow").style.display = explicitOrderDisplay;
  }
};

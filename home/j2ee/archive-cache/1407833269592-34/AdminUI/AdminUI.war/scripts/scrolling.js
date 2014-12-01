var tableScrolling = {
  widthTemplateControlId: "inputPanel",
  leftOuterDivId: "scrollableTableContainer",
  leftInnerDivId: "scrollableTableAnywidth",
  initialContentTableId: "scrollableTable",
  leftColumnId: "titleColumnCell",
  rightOuterDivId: "scrollingArea",
  rightInnerDivId: "scrollingAreaWindow",
  rightContentDivId: "scrollingAreaContent",
  handle: null,
  
  leftColumnWidth: 0,
  resize: function() {
    var rightOuterDiv = document.getElementById(tableScrolling.rightOuterDivId);
    if (rightOuterDiv) {
      rightOuterDiv.style.width =
        document.getElementById(tableScrolling.widthTemplateControlId).clientWidth - tableScrolling.leftColumnWidth + "px";
    } else {
      dojo.disconnect(tableScrolling.handle);
    }
  },
  init: function() {
    var stc = document.getElementById(this.leftOuterDivId);
    if (stc) {
      var sta = document.getElementById(this.leftInnerDivId);
      var st = document.getElementById(this.initialContentTableId);
      sta.style.width = st.offsetWidth + "px";
      var h = st.clientHeight + stc.offsetHeight - stc.clientHeight + 1;
      stc.style.height = h + "px";
      stc.style.overflowX = "hidden";
      this.leftColumnWidth = st.rows[0].cells[1].offsetLeft;
      stc.style.width = this.leftColumnWidth + "px";
      var tcc = document.getElementById(this.leftColumnId);
      tcc.style.width = this.leftColumnWidth + "px";
      var rightOuterDiv = document.getElementById(this.rightOuterDivId);
      rightOuterDiv.style.height = h + "px";
      var saw = document.getElementById(this.rightInnerDivId);
      saw.style.width = st.offsetWidth - this.leftColumnWidth + "px";
      var sac = document.getElementById(this.rightContentDivId);
      sac.style.left = -this.leftColumnWidth + "px";
      sac.style.width = sta.style.width;
      sac.innerHTML = sta.innerHTML;
      this.resize();
      this.handle = dojo.connect(window, "onresize", this.resize);
    }
  }
};

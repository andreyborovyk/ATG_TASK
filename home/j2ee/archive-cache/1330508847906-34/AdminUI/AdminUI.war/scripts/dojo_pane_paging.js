//scripts for dojoPagingTag. dojoPagingTag uses attached content pane to show items.
var pageSize;
var firstPage = 0;
var pageSizeParamName;
var pageNumberParamName;
var maxDocsPerSetParamName;
var dojoPane;
var itemsTotalCount;
var itemsDisplayedCount;
var currentPage = 0;
var pagesCount;
var paneInitialHref;
var firstLoad = true;
//opens page with the given game number. Adds 3 params to content pane url and refreshes content pane.
function openPage(pageNum) {
  currentPage = pageNum;
  var paneHref = paneInitialHref;
  paneHref = addParamToUrl(paneHref, pageNumberParamName != '' ? pageNumberParamName : "pageNum", pageNum);
  paneHref = addParamToUrl(paneHref, pageSizeParamName != '' ? pageSizeParamName : "pageSize", pageSize);
  paneHref = addParamToUrl(paneHref, maxDocsPerSetParamName != '' ? maxDocsPerSetParamName : "maxDocsPerSet", pageSize * pageNum + pageSize);
  dojoPane.setHref(paneHref);
}
//called ones after content pane initialized. It's called when content pane object exist and it's possible to
//make necessary init and show first page with items.
function dojoLoadedCallback(pane) {
  //init
  dojoPane = pane;
  paneInitialHref = dojoPane.href;
}

function afterContentPaneLoad(message) {
  itemsTotalCount = message.totalCount;
  itemsDisplayedCount = message.itemsDisplayed;
  fillPagingValues();
}
      
//fills paging component UI with necessary values(pages count, items count and etc.)
//if all items fit into one page paging UI will be hided.
function fillPagingValues() {
  pagesCount = Math.ceil(itemsTotalCount / pageSize);
  if (pagesCount < 2) {
    document.getElementById("pagingTable").style.display = "none";
  } else {
    document.getElementById("pagingTable").style.display = "";
    //"1" should be displayed for the first page. But real first page number is "0", so +1
    document.getElementById("firstDisplayed").innerHTML = currentPage * pageSize + 1;
    document.getElementById("lastDisplayed").innerHTML = currentPage * pageSize + itemsDisplayedCount;
    document.getElementById("itemsCount").innerHTML = itemsTotalCount;
    document.getElementById("currentPage").innerHTML = currentPage + 1;
    document.getElementById("pagesCount").innerHTML = pagesCount;
    //disable/enable back link
    var back = document.getElementById("pageBackLink");
    if (currentPage == firstPage) {
      addClassToElement(back, "inactive");
      back.disabled = true;
    } else {
      back.className = back.className.replace("inactive", "");
      back.disabled = false;
    }
    //disable/enable forward link
    var forward = document.getElementById("pageForwardLink");
    if (currentPage == Math.floor(itemsTotalCount / pageSize)) {
      addClassToElement(forward, "inactive");
      forward.disabled = true;
    } else {
      forward.className = forward.className.replace("inactive", "");
      forward.disabled = false;
    }
  }
}
//checks if given page number is suitable and if it is opens page with the given number.
function gotoPage(element) {
  var num = document.getElementById('currentPageIndex').value;
  if (isDecimalDigits(num)) {
    //user wants to see "1" as the number of first page. But real first page number is "0"
    num--;
    if (num >= firstPage && num < pagesCount) {
      openPage(num);
    } else {
      document.getElementById('currentPageIndex').value = "";
    }
  } else {
    document.getElementById('currentPageIndex').value = "";
  }
}

function isDecimalDigits(argvalue) {
  argvalue = argvalue.toString();
  var validChars = "0123456789";

  for (var n = 0; n < argvalue.length; n++) {
    if (validChars.indexOf(argvalue.substring(n, n+1)) == -1) return false;
  }
  return true;
}
  
//open previous page
function pageBack() {
  openPage(--currentPage);
}
//open next page
function pageNext() {
  openPage(++currentPage);
}

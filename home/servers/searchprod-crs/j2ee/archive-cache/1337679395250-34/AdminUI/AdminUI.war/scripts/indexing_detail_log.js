// This functions will be used for open/close detail indexing log.
function getFullErrorMessage(pSyncStatusEntryId){
  var em = document.getElementById("em_" + pSyncStatusEntryId);
  var full_em = document.getElementById("full_em_" + pSyncStatusEntryId);
  em.innerHTML = full_em.value;
  return true;
}

function getPartErrorMessage(pSyncStatusEntryId){
  var em = document.getElementById("em_" + pSyncStatusEntryId);
  var full_em = document.getElementById("full_em_" + pSyncStatusEntryId);
  full_em.value = em.innerHTML;
  em.innerHTML = em.innerHTML.substring(0, 150);
  return true;
}

function toggleLog(pSyncStatusEntryId) {
  var toggle = document.getElementById("logToggle_" + pSyncStatusEntryId);
  if (toggle.className == "indexingDetailLogOpen") {
    toggle.className = "indexingDetailLogClose";
    var row = document.getElementById("tr_" + pSyncStatusEntryId);
    row.className = "alt closedetail";
    getPartErrorMessage(pSyncStatusEntryId);
  } else {
    toggle.className = "indexingDetailLogOpen";
    var row = document.getElementById("tr_" + pSyncStatusEntryId);
    row.className = "alt";
    var cp = dijit.byId("cp_" + pSyncStatusEntryId);
    if (cp) {
      cp.refresh();
      document.getElementById("cp_" + pSyncStatusEntryId).style.display = "";
    }
    getFullErrorMessage(pSyncStatusEntryId);
  }
  return true;
}

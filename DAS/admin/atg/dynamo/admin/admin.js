// some very simple javascript functions used by the nucleus admin

    function toggle_visibility(id) {
       var e = document.getElementById(id);
       if(e.style.display == 'block')
          e.style.display = 'none';
       else
          e.style.display = 'block';
    }

    function toggle_visibility_inline(id) {
       var e = document.getElementById(id);
       if(e.style.display == 'inline')
          e.style.display = 'none';
       else
          e.style.display = 'inline';
    }


    function set_focus(id) {
       var e = document.getElementById(id);
       e.focus();
    }

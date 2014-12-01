# ws-online-create-ds-postlude.py

  success = 1
finally:
    if (success):
        save()
        activate(block="true")
    else:
        cancelEdit('y')
        pass
    pass
exit()

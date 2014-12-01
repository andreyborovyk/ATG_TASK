



IF OBJECT_ID('dbo.TRUNC') IS NOT NULL
    DROP FUNCTION dbo.TRUNC
go



CREATE  FUNCTION TRUNC(@mydate datetime, @type varchar(4))
RETURNS datetime
AS
BEGIN
  declare @ret datetime

  IF(@type='YEAR')
  BEGIN
    -- return the first day of the year
    select @ret = convert(datetime, CONVERT(char(10), DATEADD(day, 1-DATEPART(dayofyear, @mydate), @mydate), 101))
  END
  ELSE IF(@type='Q')
  BEGIN
    -- return the first day for the quarter
    select @ret = convert(datetime, convert(varchar(2),(DATEPART(quarter, @mydate)-1)*3+1) + '/01/' + convert(varchar(4),datepart(year, @mydate)), 101)
  END
  ELSE IF(@type='MON')
  BEGIN
    -- return the first day of the month
    select @ret = convert(datetime, CONVERT(char(10), DATEADD(day, 1-DATEPART(day, @mydate), @mydate), 101))
  END
  ELSE IF(@type='DAY')
  BEGIN
    -- return the fist day (SUNDAY) of the week
    select @ret = convert(datetime, CONVERT(char(10), DATEADD(day, 1-DATEPART(weekday, @mydate), @mydate), 101))
  END
  ELSE IF(@type='DATE')
  BEGIN
    -- return date part and truncate time part
    select @ret = convert(datetime, CONVERT(char(10), @mydate, 101))
  END
  ELSE
  BEGIN
    SELECT @ret = @mydate
  END
  RETURN @ret
END
go



IF OBJECT_ID('dbo.MONTHS_BETWEEN') IS NOT NULL
    DROP FUNCTION dbo.MONTHS_BETWEEN
go




CREATE  FUNCTION MONTHS_BETWEEN(@mydate1 datetime, @mydate2 datetime)
RETURNS INTEGER
AS
BEGIN
  RETURN DATEDIFF(MONTH, @mydate2, @mydate1)
END




go

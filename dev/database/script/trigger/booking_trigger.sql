-- noinspection SqlNoDataSourceInspectionForFile
-- noinspection SqlDialectInspectionForFile
-- noinspection SqlResolveForFile
-- @author: bht

USE [BOOKING]
GO

/****** Object:  Trigger [TG_DELETE_BOOKING]    Script Date: 8/26/2020 6:19:13 AM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TRIGGER [TG_DELETE_BOOKING]
    ON [BOOKING]
    FOR DELETE
    AS
BEGIN
    SET NOCOUNT ON

	UPDATE BS
	SET NUMBER_OF_BOOKING = NUMBER_OF_BOOKING - 1
	FROM BOOKING_STATISTIC BS
	INNER JOIN DELETED D ON BS.PARKING_LOT_ID = D.PARKING_LOT_ID
END
GO
ALTER TABLE [dbo].[BOOKING] ENABLE TRIGGER [TG_DELETE_BOOKING]
GO
/****** Object:  Trigger [TG_INSERT_BOOKING]    Script Date: 8/26/2020 6:19:13 AM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TRIGGER [TG_INSERT_BOOKING]
    ON [BOOKING]
    AFTER INSERT
    AS
BEGIN
    SET NOCOUNT ON

	INSERT INTO BOOKING_HISTORY (BOOKING_ID, STATUS_ID, LAST_UPDATED)
	SELECT I.ID, S.ID, getdate()
	FROM INSERTED I, BOOKING_STATUS S
	WHERE S.STATUS = 'CREATED'

	UPDATE BS
	SET NUMBER_OF_BOOKING = NUMBER_OF_BOOKING + 1
	FROM BOOKING_STATISTIC BS
	INNER JOIN INSERTED I ON BS.PARKING_LOT_ID = I.PARKING_LOT_ID
END
GO
ALTER TABLE [dbo].[BOOKING] ENABLE TRIGGER [TG_INSERT_BOOKING]
GO
/****** Object:  Trigger [TG_INSERT_UPDATE_BOOKING_HISTORY]    Script Date: 8/26/2020 6:19:13 AM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO

CREATE TRIGGER [TG_INSERT_UPDATE_BOOKING_HISTORY]
    ON [BOOKING_HISTORY]
    AFTER INSERT, UPDATE
    AS
BEGIN
    SET NOCOUNT ON

	UPDATE B
	SET LATEST_STATUS_ID = I.STATUS_ID,
		IS_FINISHED = CASE WHEN S.STATUS IN ('FINISHED', 'CANCELLED', 'REJECTED') THEN 1 ELSE 0 END
	FROM BOOKING B 
	INNER JOIN INSERTED I ON B.ID = I.BOOKING_ID
	INNER JOIN BOOKING_STATUS S ON S.ID = I.STATUS_ID
END
GO
ALTER TABLE [dbo].[BOOKING_HISTORY] ENABLE TRIGGER [TG_INSERT_UPDATE_BOOKING_HISTORY]
GO
/****** Object:  Trigger [TG_CREATE_BOOKING_RATING]    Script Date: 8/26/2020 6:19:13 AM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TRIGGER [TG_CREATE_BOOKING_RATING]
    ON [BOOKING_RATING]
    AFTER INSERT
    AS
BEGIN
    SET NOCOUNT ON

    UPDATE B
    SET IS_RATED = 1
    FROM BOOKING B INNER JOIN INSERTED I ON I.ID = B.ID

	UPDATE BS
    SET RATING_AVERAGE = ((RATING_AVERAGE * NUMBER_OF_RATING) + I.RATING) / (NUMBER_OF_RATING + 1),
        NUMBER_OF_RATING = NUMBER_OF_RATING + 1
    FROM BOOKING_STATISTIC BS 
		INNER JOIN BOOKING B ON B.PARKING_LOT_ID = BS.PARKING_LOT_ID
		INNER JOIN INSERTED I ON I.ID = B.ID
END
GO
ALTER TABLE [dbo].[BOOKING_RATING] ENABLE TRIGGER [TG_CREATE_BOOKING_RATING]
GO
/****** Object:  Trigger [TG_DELETE_BOOKING_RATING]    Script Date: 8/26/2020 6:19:13 AM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TRIGGER [TG_DELETE_BOOKING_RATING]
    ON [BOOKING_RATING]
    FOR DELETE
    AS
BEGIN
    SET NOCOUNT ON

	UPDATE B
    SET IS_RATED = 0
    FROM BOOKING B INNER JOIN DELETED D ON D.ID = B.ID

	UPDATE BS
    SET RATING_AVERAGE   = ((RATING_AVERAGE * NUMBER_OF_RATING) - D.RATING) / (NUMBER_OF_RATING - 1),
        NUMBER_OF_RATING = NUMBER_OF_RATING - 1
	FROM BOOKING_STATISTIC BS 
		INNER JOIN BOOKING B ON B.PARKING_LOT_ID = BS.PARKING_LOT_ID
		INNER JOIN DELETED D ON D.ID = B.ID
END
GO
ALTER TABLE [dbo].[BOOKING_RATING] ENABLE TRIGGER [TG_DELETE_BOOKING_RATING]
GO
/****** Object:  Trigger [TG_UPDATE_BOOKING_RATING]    Script Date: 8/26/2020 6:19:13 AM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TRIGGER [TG_UPDATE_BOOKING_RATING]
    ON [BOOKING_RATING]
    FOR UPDATE
    AS
BEGIN
    SET NOCOUNT ON

    UPDATE BS
    SET RATING_AVERAGE = ((RATING_AVERAGE * NUMBER_OF_RATING) - D.RATING + I.RATING) / NUMBER_OF_RATING
    FROM BOOKING_STATISTIC BS
			 INNER JOIN (SELECT ID, PARKING_LOT_ID FROM BOOKING) AS B ON BS.PARKING_LOT_ID = B.PARKING_LOT_ID 
             INNER JOIN (SELECT ID, RATING FROM DELETED) AS D ON D.ID = B.ID
             INNER JOIN (SELECT ID, RATING FROM INSERTED) AS I ON I.ID = B.ID
END
GO
ALTER TABLE [dbo].[BOOKING_RATING] ENABLE TRIGGER [TG_UPDATE_BOOKING_RATING]
GO